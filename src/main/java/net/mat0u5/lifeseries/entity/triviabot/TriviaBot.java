package net.mat0u5.lifeseries.entity.triviabot;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotGlideGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotLookAtPlayerGoal;
import net.mat0u5.lifeseries.entity.triviabot.goal.TriviaBotTeleportGoal;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotPathfinding;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotServerData;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotSounds;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TriviaBot extends AmbientEntity {
    public static final Identifier ID = Identifier.of(Main.MOD_ID, "triviabot");

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


    private static final TrackedData<Boolean> submittedAnswer = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ranOutOfTime = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> answeredRight = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> interactedWith = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> gliding = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> analyzing = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.INTEGER);


    public TriviaBot(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
        setInvulnerable(true);
        setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        //? if <= 1.21 {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0);
        //?} else {
        /*return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000)
                .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.FOLLOW_RANGE, 100)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.ATTACK_DAMAGE, 0);
        *///?}
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new TriviaBotTeleportGoal(this));
        goalSelector.add(1, new TriviaBotGlideGoal(this));
        goalSelector.add(2, new TriviaBotLookAtPlayerGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        serverData.tick();
        clientData.tick();
    }

    
    public World getBotWorld() {
        return ls$getEntityWorld();
    }

    /*
        Override vanilla things
     */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return triviaHandler.interactMob(player, hand);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }
    
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

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return CAN_START_RIDING;
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
        builder.add(ranOutOfTime, false);
        builder.add(submittedAnswer, false);
        builder.add(answeredRight, false);
        builder.add(interactedWith, false);
        builder.add(gliding, false);
        builder.add(analyzing, -1);
    }
    public void setRanOutOfTime(boolean value) {
        this.dataTracker.set(ranOutOfTime, value);
    }
    public void setSubmittedAnswer(boolean value) {
        this.dataTracker.set(submittedAnswer, value);
    }
    public void setAnsweredRight(boolean value) {
        this.dataTracker.set(answeredRight, value);
    }
    public void setInteractedWith(boolean value) {
        this.dataTracker.set(interactedWith, value);
    }
    public void setGliding(boolean value) {
        this.dataTracker.set(gliding, value);
    }
    public void setAnalyzingTime(int value) {
        this.dataTracker.set(analyzing, value);
    }
    public boolean ranOutOfTime() {
        return this.dataTracker.get(ranOutOfTime);
    }
    public boolean submittedAnswer() {
        return this.dataTracker.get(submittedAnswer);
    }
    public boolean answeredRight() {
        return this.dataTracker.get(answeredRight);
    }
    public boolean interactedWith() {
        return this.dataTracker.get(interactedWith);
    }
    public boolean isBotGliding() {
        return this.dataTracker.get(gliding);
    }
    public int getAnalyzingTime() {
        return this.dataTracker.get(analyzing);
    }
}
