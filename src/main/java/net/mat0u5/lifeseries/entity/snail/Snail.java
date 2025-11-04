package net.mat0u5.lifeseries.entity.snail;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.goal.*;
import net.mat0u5.lifeseries.entity.snail.server.SnailPathfinding;
import net.mat0u5.lifeseries.entity.snail.server.SnailServerData;
import net.mat0u5.lifeseries.entity.snail.server.SnailSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static net.mat0u5.lifeseries.Main.currentSession;

public class Snail extends Monster {
    public static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/entity/snail/default.png");
    public static final ResourceLocation TRIVIA_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/entity/snail/trivia.png");
    public static final ResourceLocation ZOMBIE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/entity/snail/zombie.png");
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "snail");
    public static double GLOBAL_SPEED_MULTIPLIER = 1;
    public static boolean SHOULD_DROWN_PLAYER = true;

    private static final EntityDataAccessor<Boolean> attacking = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> flying = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> gliding = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> landing = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> mining = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> fromTrivia = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> skinName = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> playerDead = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BOOLEAN);

    public static final float MOVEMENT_SPEED = 0.35f;
    public static final float FLYING_SPEED = 0.3f;
    public static final int STATIONARY_TP_COOLDOWN = 400; // No movement for 20 seconds teleports the snail
    public static final int TP_MIN_RANGE = 75;
    public static final int MAX_DISTANCE = 150; // Distance over this teleports the snail to the player
    public static final int JUMP_COOLDOWN_SHORT = 10;
    public static final int JUMP_COOLDOWN_LONG = 30;
    public static final int JUMP_RANGE_SQUARED = 14;

    public SnailServerData serverData = new SnailServerData(this);
    public SnailSounds sounds = new SnailSounds(this);
    public SnailPathfinding pathfinding = new SnailPathfinding(this);
    public SnailClientData clientData = new SnailClientData(this);

    public Snail(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setInvulnerable(true);
        setPersistenceRequired();
    }

    @Override
    protected Component getTypeName() {
        return serverData.getDefaultName();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10000)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, FLYING_SPEED)
                .add(Attributes.STEP_HEIGHT, 1.2)
                .add(Attributes.FOLLOW_RANGE, 150)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(Attributes.SAFE_FALL_DISTANCE, 100)
                .add(Attributes.ATTACK_DAMAGE, 20);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SnailTeleportGoal(this));

        goalSelector.addGoal(1, new SnailLandGoal(this));
        goalSelector.addGoal(2, new SnailMineTowardsPlayerGoal(this));
        goalSelector.addGoal(3, new SnailFlyGoal(this));
        goalSelector.addGoal(4, new SnailGlideGoal(this));
        goalSelector.addGoal(5, new SnailJumpAttackPlayerGoal(this));
        goalSelector.addGoal(6, new SnailStartFlyingGoal(this));

        goalSelector.addGoal(7, new SnailBlockInteractGoal(this));
        goalSelector.addGoal(8, new SnailPushEntitiesGoal(this));
        goalSelector.addGoal(9, new SnailPushProjectilesGoal(this));
    }

    @Override
    public void tick() {
        serverData.tick();
        clientData.tick();
        super.tick();
    }

    public boolean isPaused() {
        return currentSession.statusPaused();
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    /*
        Override vanilla things
     */

    @Override
    public Vec3 getFluidFallingAdjustedMovement(double gravity, boolean falling, Vec3 motion) {
        return motion;
    }

    @Override
    //? if <= 1.21.4 {
    protected boolean isAffectedByFluids() {
        return false;
    }
    //?} else {
    /*public boolean isAffectedByFluids() {
        return false;
    }
    *///?}

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.setSharedFlag(4, false);
    }

    public boolean isInLavaLocal = false;
    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double speed) {
        if (FluidTags.LAVA != tag) {
            return false;
        }

        if (this.touchingUnloadedChunk()) {
            return false;
        }
        AABB box = this.getBoundingBox().deflate(0.001);
        int i = Mth.floor(box.minX);
        int j = Mth.ceil(box.maxX);
        int k = Mth.floor(box.minY);
        int l = Mth.ceil(box.maxY);
        int m = Mth.floor(box.minZ);
        int n = Mth.ceil(box.maxZ);
        double d = 0.0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for(int p = i; p < j; ++p) {
            for(int q = k; q < l; ++q) {
                for(int r = m; r < n; ++r) {
                    mutable.set(p, q, r);
                    FluidState fluidState = level().getFluidState(mutable);
                    if (fluidState.is(tag)) {
                        double e = q + fluidState.getHeight(this.level(), mutable);
                        if (e >= box.minY) {
                            d = Math.max(e - box.minY, d);
                        }
                    }
                }
            }
        }

        isInLavaLocal = d > 0.0;
        return false;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        pathfinding.killPathFinders();
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }


    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    public float soundVolume() {
        return getSoundVolume();
    }

    public void setNavigation(PathNavigation newNavigation) {
        this.navigation = newNavigation;
    }
    public void setMoveControl(MoveControl newMoveControl) {
        this.moveControl = newMoveControl;
    }
    /*
    Data Tracker Stuff
     */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(attacking, false);
        builder.define(flying, false);
        builder.define(gliding, false);
        builder.define(landing, false);
        builder.define(mining, false);
        builder.define(fromTrivia, false);
        builder.define(skinName, "");
        builder.define(playerDead, false);
    }
    public void setSnailAttacking(boolean value) {
        this.entityData.set(attacking, value);
    }
    public void setSnailFlying(boolean value) {
        this.entityData.set(flying, value);
    }
    public void setSnailGliding(boolean value) {
        this.entityData.set(gliding, value);
    }
    public void setSnailLanding(boolean value) {
        this.entityData.set(landing, value);
    }
    public void setSnailMining(boolean value) {
        this.entityData.set(mining, value);
    }
    public void setFromTrivia(boolean value) {
        this.entityData.set(fromTrivia, value);
    }
    public void setSkinName(String value) {
        this.entityData.set(skinName, value);
    }
    public void setBoundPlayerDead(boolean value) {
        this.entityData.set(playerDead, value);
    }

    public boolean isSnailAttacking() {
        return this.entityData.get(attacking);
    }
    public boolean isSnailFlying() {
        return this.entityData.get(flying);
    }
    public boolean isSnailGliding() {
        return this.entityData.get(gliding);
    }
    public boolean isSnailLanding() {
        return this.entityData.get(landing);
    }
    public boolean isSnailMining() {
        return this.entityData.get(mining);
    }
    public boolean isFromTrivia() {
        return this.entityData.get(fromTrivia);
    }
    public String getSkinName() {
        return this.entityData.get(skinName);
    }
    public boolean isBoundPlayerDead() {
        return this.entityData.get(playerDead);
    }
}
