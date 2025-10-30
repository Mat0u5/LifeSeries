package net.mat0u5.lifeseries.entity.snail;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.goal.*;
import net.mat0u5.lifeseries.entity.snail.server.SnailPathfinding;
import net.mat0u5.lifeseries.entity.snail.server.SnailServerData;
import net.mat0u5.lifeseries.entity.snail.server.SnailSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import static net.mat0u5.lifeseries.Main.*;

public class Snail extends HostileEntity {
    public static final Identifier DEFAULT_TEXTURE = Identifier.of(Main.MOD_ID, "textures/entity/snail/default.png");
    public static final Identifier TRIVIA_TEXTURE = Identifier.of(Main.MOD_ID, "textures/entity/snail/trivia.png");
    public static final Identifier ZOMBIE_TEXTURE = Identifier.of(Main.MOD_ID, "textures/entity/snail/zombie.png");
    public static final Identifier ID = Identifier.of(Main.MOD_ID, "snail");
    public static double GLOBAL_SPEED_MULTIPLIER = 1;
    public static boolean SHOULD_DROWN_PLAYER = true;

    private static final TrackedData<Boolean> attacking = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> flying = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> gliding = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> landing = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> mining = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> fromTrivia = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> skinName = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> playerDead = DataTracker.registerData(Snail.class, TrackedDataHandlerRegistry.BOOLEAN);

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

    public Snail(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        setInvulnerable(true);
        setPersistent();
    }

    @Override
    protected Text getDefaultName() {
        return serverData.getDefaultName();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        //? if <= 1.21 {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, FLYING_SPEED)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 150)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20);
        //?} else {
        /*return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000)
                .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED, FLYING_SPEED)
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.FOLLOW_RANGE, 150)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.ATTACK_DAMAGE, 20);
        *///?}
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SnailTeleportGoal(this));

        goalSelector.add(1, new SnailLandGoal(this));
        goalSelector.add(2, new SnailMineTowardsPlayerGoal(this));
        goalSelector.add(3, new SnailFlyGoal(this));
        goalSelector.add(4, new SnailGlideGoal(this));
        goalSelector.add(5, new SnailJumpAttackPlayerGoal(this));
        goalSelector.add(6, new SnailStartFlyingGoal(this));

        goalSelector.add(7, new SnailBlockInteractGoal(this));
        goalSelector.add(8, new SnailPushEntitiesGoal(this));
        goalSelector.add(9, new SnailPushProjectilesGoal(this));
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
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    /*
        Override vanilla things
     */

    @Override
    public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
        return motion;
    }

    @Override
    //? if <= 1.21.4 {
    protected boolean shouldSwimInFluids() {
        return false;
    }
    //?} else {
    /*public boolean shouldSwimInFluids() {
        return false;
    }
    *///?}

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.setFlag(4, false);
    }

    public boolean isInLavaLocal = false;
    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        if (FluidTags.LAVA != tag) {
            return false;
        }

        if (this.isRegionUnloaded()) {
            return false;
        }
        Box box = this.getBoundingBox().contract(0.001);
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        double d = 0.0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int p = i; p < j; ++p) {
            for(int q = k; q < l; ++q) {
                for(int r = m; r < n; ++r) {
                    mutable.set(p, q, r);
                    FluidState fluidState = getSnailWorld().getFluidState(mutable);
                    if (fluidState.isIn(tag)) {
                        double e = q + fluidState.getHeight(this.getSnailWorld(), mutable);
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

    public World getSnailWorld() {
        return ls$getEntityWorld();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        pathfinding.killPathFinders();
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }


    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    public float soundVolume() {
        return getSoundVolume();
    }

    public void setNavigation(EntityNavigation newNavigation) {
        this.navigation = newNavigation;
    }
    public void setMoveControl(MoveControl newMoveControl) {
        this.moveControl = newMoveControl;
    }
    /*
    Data Tracker Stuff
     */

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(attacking, false);
        builder.add(flying, false);
        builder.add(gliding, false);
        builder.add(landing, false);
        builder.add(mining, false);
        builder.add(fromTrivia, false);
        builder.add(skinName, "");
        builder.add(playerDead, false);
    }
    public void setSnailAttacking(boolean value) {
        this.dataTracker.set(attacking, value);
    }
    public void setSnailFlying(boolean value) {
        this.dataTracker.set(flying, value);
    }
    public void setSnailGliding(boolean value) {
        this.dataTracker.set(gliding, value);
    }
    public void setSnailLanding(boolean value) {
        this.dataTracker.set(landing, value);
    }
    public void setSnailMining(boolean value) {
        this.dataTracker.set(mining, value);
    }
    public void setFromTrivia(boolean value) {
        this.dataTracker.set(fromTrivia, value);
    }
    public void setSkinName(String value) {
        this.dataTracker.set(skinName, value);
    }
    public void setBoundPlayerDead(boolean value) {
        this.dataTracker.set(playerDead, value);
    }

    public boolean isSnailAttacking() {
        return this.dataTracker.get(attacking);
    }
    public boolean isSnailFlying() {
        return this.dataTracker.get(flying);
    }
    public boolean isSnailGliding() {
        return this.dataTracker.get(gliding);
    }
    public boolean isSnailLanding() {
        return this.dataTracker.get(landing);
    }
    public boolean isSnailMining() {
        return this.dataTracker.get(mining);
    }
    public boolean isFromTrivia() {
        return this.dataTracker.get(fromTrivia);
    }
    public String getSkinName() {
        return this.dataTracker.get(skinName);
    }
    public boolean isBoundPlayerDead() {
        return this.dataTracker.get(playerDead);
    }
}
