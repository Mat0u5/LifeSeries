package net.mat0u5.lifeseries.entity.triviabot;

import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotGlideGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotLookAtPlayerGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotTeleportGoal;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotPathfinding;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotServerData;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotSounds;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

public class TriviaBot extends AmbientCreature {
    //? if <= 1.21.9 {
    public static final ResourceLocation ID = IdentifierHelper.mod("triviabot");
    //?} else {
    /*public static final Identifier ID = IdentifierHelper.mod("triviabot");
    *///?}

    public static final int STATIONARY_TP_COOLDOWN = 400; // No movement for 20 seconds teleports the bot
    public static final float MOVEMENT_SPEED = 0.45f;
    public static final int MAX_DISTANCE = 100;
    public static boolean CAN_START_RIDING = true;
    public static int EASY_TIME = 180;
    public static int NORMAL_TIME = 240;
    public static int HARD_TIME = 300;

    public TriviaBotClientData clientData = new TriviaBotClientData(this);
    public TriviaBotServerData serverData = new TriviaBotServerData(this);
    public TriviaBotSounds sounds = new TriviaBotSounds(this);
    public TriviaBotPathfinding pathfinding = new TriviaBotPathfinding(this);
    public TriviaHandler triviaHandler = new TriviaHandler(this);


    private static final EntityDataAccessor<Boolean> submittedAnswer = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ranOutOfTime = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> answeredRight = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> interactedWith = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> gliding = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> analyzing = SynchedEntityData.defineId(TriviaBot.class, EntityDataSerializers.INT);


    public TriviaBot(EntityType<? extends AmbientCreature> entityType, Level level) {
        super(entityType, level);
        setInvulnerable(true);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10000)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, MOVEMENT_SPEED)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.FOLLOW_RANGE, 100)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(Attributes.SAFE_FALL_DISTANCE, 100)
                .add(Attributes.ATTACK_DAMAGE, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new TriviaBotTeleportGoal(this));
        goalSelector.addGoal(1, new TriviaBotGlideGoal(this));
        goalSelector.addGoal(2, new TriviaBotLookAtPlayerGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        serverData.tick();
        clientData.tick();
    }

    /*
        Override vanilla things
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return triviaHandler.interactMob(player, hand);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }
    
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

    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double speed) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return CAN_START_RIDING;
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
        builder.define(ranOutOfTime, false);
        builder.define(submittedAnswer, false);
        builder.define(answeredRight, false);
        builder.define(interactedWith, false);
        builder.define(gliding, false);
        builder.define(analyzing, -1);
    }
    public void setRanOutOfTime(boolean value) {
        this.entityData.set(ranOutOfTime, value);
    }
    public void setSubmittedAnswer(boolean value) {
        this.entityData.set(submittedAnswer, value);
    }
    public void setAnsweredRight(boolean value) {
        this.entityData.set(answeredRight, value);
    }
    public void setInteractedWith(boolean value) {
        this.entityData.set(interactedWith, value);
    }
    public void setGliding(boolean value) {
        this.entityData.set(gliding, value);
    }
    public void setAnalyzingTime(int value) {
        this.entityData.set(analyzing, value);
    }
    public boolean ranOutOfTime() {
        return this.entityData.get(ranOutOfTime);
    }
    public boolean submittedAnswer() {
        return this.entityData.get(submittedAnswer);
    }
    public boolean answeredRight() {
        return this.entityData.get(answeredRight);
    }
    public boolean interactedWith() {
        return this.entityData.get(interactedWith);
    }
    public boolean isBotGliding() {
        return this.entityData.get(gliding);
    }
    public int getAnalyzingTime() {
        return this.entityData.get(analyzing);
    }
}
