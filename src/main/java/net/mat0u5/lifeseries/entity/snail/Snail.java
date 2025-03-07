package net.mat0u5.lifeseries.entity.snail;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.Animator;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.AnimationHandler;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.goal.*;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Snails;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.AnimationUtils;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.server;

public class Snail extends HostileEntity implements AnimatedEntity {
    public static final RegistryKey<DamageType> SNAIL_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Main.MOD_ID, "snail"));
    public static final Identifier ID = Identifier.of(Main.MOD_ID, "snail");
    public static final Model MODEL = BbModelLoader.load(ID);
    public static double GLOBAL_SPEED_MULTIPLIER = 1;
    public static boolean SHOULD_DROWN_PLAYER = true;

    private final EntityHolder<Snail> holder;
    public UUID boundPlayerUUID;
    public boolean attacking;
    public boolean flying;
    public boolean gliding;
    public boolean landing;
    public boolean mining;
    public boolean setNavigation = false;
    public boolean fromTrivia = false;
    public int dontAttackFor = 0;
    @Nullable
    public PathFinder groundPathFinder;
    @Nullable
    public PathFinder pathFinder;
    public int nullPlayerChecks = 0;
    public Text snailName;
    private long chunkTicketExpiryTicks = 0L;
    private int lastAir = 0;

    public static final float MOVEMENT_SPEED = 0.35f;
    public static final float FLYING_SPEED = 0.3f;
    public static final int STATIONARY_TP_COOLDOWN = 400; // No movement for 20 seconds teleports the snail
    public static final int TP_MIN_RANGE = 15;
    public static final int MAX_DISTANCE = 150; // Distance over this teleports the snail to the player
    public static final int JUMP_COOLDOWN_SHORT = 10;
    public static final int JUMP_COOLDOWN_LONG = 30;
    public static final int JUMP_RANGE_SQUARED = 14;

    public Snail(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(holder, this);
        setInvulnerable(true);
        setPersistent();
    }

    public int getJumpRangeSquared() {
        if (isNerfed()) return 9;
        return JUMP_RANGE_SQUARED;
    }

    public void setBoundPlayer(ServerPlayerEntity player) {
        if (player == null) return;
        boundPlayerUUID = player.getUuid();
        writeCustomDataToNbt(new NbtCompound());
        updateSnailName();
    }

    public void updateSnailName() {
        if (getBoundPlayer() == null) return;
        snailName = Text.of(Snails.getSnailName(getBoundPlayer()));
    }

    @Override
    protected Text getDefaultName() {
        if (fromTrivia) return Text.of("VHSnail");
        if (snailName == null) return this.getType().getName();
        if (snailName.getString().isEmpty()) return this.getType().getName();
        return snailName;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (boundPlayerUUID == null) return;
        nbt.putUuid("boundPlayer", boundPlayerUUID);
        if (pathFinder != null) nbt.putUuid("pathFinder", pathFinder.getUuid());
        if (groundPathFinder != null) nbt.putUuid("groundPathFinder", groundPathFinder.getUuid());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID newUUID = nbt.getUuid("boundPlayer");
        if (newUUID != null) {
            boundPlayerUUID = newUUID;
        }
        if (getWorld() instanceof ServerWorld world) {
            UUID pfUUID = nbt.getUuid("pathFinder");
            if (pfUUID != null) {
                if (world.getEntity(pfUUID) instanceof PathFinder pf) {
                    pathFinder = pf;
                }
            }
            UUID gpfUUID = nbt.getUuid("groundPathFinder");
            if (gpfUUID != null) {
                if (world.getEntity(gpfUUID) instanceof PathFinder gpf) {
                    groundPathFinder = gpf;
                }
            }
        }
    }

    @Override
    public EntityHolder<Snail> getHolder() {
        return holder;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        //? if <= 1.21 {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, FLYING_SPEED)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
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
        super.tick();

        if (dontAttackFor > 0) {
            dontAttackFor--;
        }

        if (nullPlayerChecks > 1000 && !fromTrivia) {
            despawn();
        }

        if (age % 20 == 0) {
            updateSnailName();
        }

        if (age % 2 == 0) {
            updateAnimations();
        }

        if (age % 100 == 0) {
            if (!fromTrivia) {
                if (!Snails.snails.containsValue(this) || !WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
                    despawn();
                }
            }
            else {
                if (!WildcardManager.isActiveWildcard(Wildcards.TRIVIA) || age >= 36000) {
                    despawn();
                }
            }
        }
        ServerPlayerEntity boundPlayer = getBoundPlayer();
        if (boundPlayer != null) {
            if (this.getBoundingBox().expand(0.05).intersects(boundPlayer.getBoundingBox())) {
                killBoundPlayer();
            }
            if (age % 100 == 0 || !setNavigation) {
                setNavigation = true;
                updateMoveControl();
                updateNavigation();
            }
            else if (age % 21 == 0) {
                updateMovementSpeed();
            }
            else if (age % 5 == 0) {
                updateNavigationTarget();
            }
        }

        if (SHOULD_DROWN_PLAYER && !fromTrivia && getBoundPlayer() != null) {
            int currentAir = getAir();
            if (lastAir != currentAir) {
                lastAir = currentAir;
                NetworkHandlerServer.sendNumberPacket(getBoundPlayer(), "snail_air", lastAir);
            }
            if (currentAir == 0) damageFromDrowning();
        }

        handleHighVelocity();
        updatePathFinders();
        chunkLoading();
        playSounds();
        clearStatusEffects();
    }

    public boolean isNerfed() {
        if (fromTrivia) return true;
        return WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
    }

    public void setFromTrivia() {
        fromTrivia = true;
        dontAttackFor = 100;
        playAttackSound();
    }

    public void chunkLoading() {
        if (getWorld() instanceof ServerWorld world) {
            if (--this.chunkTicketExpiryTicks <= 0L) {
                world.resetIdleTimeout();
                this.chunkTicketExpiryTicks = addTicket(world) - 20L;
            }
        }
    }

    public long addTicket(ServerWorld world) {
        world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(getBlockPos()), 1, getBlockPos());
        return ChunkTicketType.PORTAL.getExpiryTicks();
    }

    public void despawn() {
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        killPathFinders();
        //? if <= 1.21 {
        this.kill();
        //?} else {
        /*this.kill((ServerWorld) getWorld());
        *///?}
        this.discard();
    }

    public void killPathFinders() {
        //? if <= 1.21 {
        if (groundPathFinder != null) groundPathFinder.kill();
        if (pathFinder != null) pathFinder.kill();
        //?} else {
        /*if (groundPathFinder != null) groundPathFinder.kill((ServerWorld) groundPathFinder.getWorld());
        if (pathFinder != null) pathFinder.kill((ServerWorld) pathFinder.getWorld());
        *///?}
        if (groundPathFinder != null) groundPathFinder.discard();
        if (pathFinder != null) pathFinder.discard();
    }

    public void handleHighVelocity() {
        Vec3d velocity = getVelocity();
        if (velocity.y > 0.15) {
            setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void killBoundPlayer() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;

        //? if <=1.21 {
        DamageSource damageSource = new DamageSource( player.getServerWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE).entryOf(SNAIL_DAMAGE));
        player.setAttacker(this);
        player.damage(damageSource, 1000);
        //?} else {
        /*DamageSource damageSource = new DamageSource( player.getServerWorld().getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SNAIL_DAMAGE));
        player.setAttacker(this);
        player.damage(player.getServerWorld(), damageSource, 1000);
        *///?}
    }

    public void damageFromDrowning() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;
        //? if <=1.21 {
        DamageSource damageSource = new DamageSource( player.getServerWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.DROWN));
        player.setAttacker(this);
        player.damage(damageSource, 2);
        //?} else {
        /*DamageSource damageSource = new DamageSource( player.getServerWorld().getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN));
        player.setAttacker(this);
        player.damage(player.getServerWorld(), damageSource, 2);
        *///?}
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    private int flyAnimation = 0;
    public void updateAnimations() {
        AnimationHandler.updateHurtVariant(this, holder);
        Animator animator = holder.getAnimator();
        if (flyAnimation < 0) {
            flyAnimation++;
            pauseAllAnimations("stopFly");
        }
        else if (flyAnimation > 0) {
            flyAnimation--;
            pauseAllAnimations("startFly");
        }
        else if (this.flying) {
            pauseAllAnimations("fly");
            animator.playAnimation("fly", 3);
        }
        else if (this.gliding || this.landing) {
            pauseAllAnimations("glide");
            animator.playAnimation("glide", 2);
        }
        else if (this.limbAnimator.isLimbMoving() && this.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            animator.playAnimation("walk", 1);
        }
        else {
            pauseAllAnimations("idle");
            animator.playAnimation("idle", 0, true);
        }
    }

    public void pauseAllAnimations(String except) {
        Animator animator = holder.getAnimator();
        if (!except.equalsIgnoreCase("glide")) animator.pauseAnimation("glide");
        if (!except.equalsIgnoreCase("fly")) animator.pauseAnimation("fly");
        if (!except.equalsIgnoreCase("walk")) animator.pauseAnimation("walk");
        if (!except.equalsIgnoreCase("idle")) animator.pauseAnimation("idle");
    }

    public void playStartFlyAnimation() {
        flyAnimation = 7;
        Animator animator = holder.getAnimator();
        animator.playAnimation("startFly", 4);
    }

    public void playStopFlyAnimation() {
        flyAnimation = -7;
        Animator animator = holder.getAnimator();
        animator.playAnimation("stopFly", 5);
    }

    public void updatePathFinders() {
        if (pathFinder != null && pathFinder.isRegionUnloaded()) {
            pathFinder.discard();
            pathFinder = null;
        }
        else if (pathFinder == null || pathFinder.isRemoved()) {
            pathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) this.getWorld(), this.getBlockPos(), SpawnReason.COMMAND);
            if (pathFinder != null) {
                NbtCompound pathFinderNbt = new NbtCompound();
                pathFinderNbt.putUuid("pathFinder", pathFinder.getUuid());
                writeCustomDataToNbt(pathFinderNbt);
            }
        }
        else {
            pathFinder.resetDespawnTimer();
        }

        if (groundPathFinder != null && groundPathFinder.isRegionUnloaded()) {
            groundPathFinder.discard();
            groundPathFinder = null;
        }
        else if (groundPathFinder == null || groundPathFinder.isRemoved()) {
            groundPathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) this.getWorld(), this.getBlockPos(), SpawnReason.COMMAND);
            if (groundPathFinder != null) {
                NbtCompound pathFinderNbt = new NbtCompound();
                pathFinderNbt.putUuid("groundPathFinder", groundPathFinder.getUuid());
                writeCustomDataToNbt(pathFinderNbt);
            }
        }
        else {
            groundPathFinder.resetDespawnTimer();
        }

        ServerWorld world = (ServerWorld) this.getWorld();
        //? if <= 1.21 {
        if (pathFinder != null) this.pathFinder.teleport(world, this.getX(), this.getY(), this.getZ(), EnumSet.noneOf(PositionFlag.class), getYaw(), getPitch());
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) this.groundPathFinder.teleport(world, this.getX(), pos.getY() + 1.0, this.getZ(), EnumSet.noneOf(PositionFlag.class), getYaw(), getPitch());
        //?} else {
        /*if (pathFinder != null) this.pathFinder.teleport(world, this.getX(), this.getY(), this.getZ(), EnumSet.noneOf(PositionFlag.class), getYaw(), getPitch(), false);
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) this.groundPathFinder.teleport(world, this.getX(), pos.getY()+1, this.getZ(), EnumSet.noneOf(PositionFlag.class), getYaw(), getPitch(), false);
        *///?}
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = getPos();
        Vec3d endPos = startPos.add(0, getWorld().getBottomY(), 0);

        BlockHitResult result = getWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        this
                )
        );
        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return getY() - belowBlock.getY() - 1;
    }

    public void teleportNearPlayer(double minDistanceFromPlayer) {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return;
        if (getWorld() instanceof ServerWorld world) {
            this.playSound(SoundEvents.ENTITY_PLAYER_TELEPORT);
            AnimationUtils.spawnTeleportParticles(world, getPos());

            BlockPos tpTo = getBlockPosNearTarget(world, minDistanceFromPlayer);
            Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
            //? if <= 1.21 {
            teleport(player.getServerWorld(), tpTo.getX(), tpTo.getY(), tpTo.getZ(), flags, getYaw(), getPitch());
            //?} else {
            /*teleport(player.getServerWorld(), tpTo.getX(), tpTo.getY(), tpTo.getZ(), flags, getYaw(), getPitch(), false);
             *///?}

            this.playSound(SoundEvents.ENTITY_PLAYER_TELEPORT);
            AnimationUtils.spawnTeleportParticles(world, getPos());
            this.chunkTicketExpiryTicks = 0L;
        }
    }

    public BlockPos getBlockPosNearTarget(ServerWorld world, double minDistanceFromTarget) {
        if (getBoundPlayer() == null) return getBlockPos();

        BlockPos targetPos = getBoundPlayer().getBlockPos();

        for (int attempts = 0; attempts < 10; attempts++) {
            Vec3d offset = new Vec3d(
                    random.nextDouble() * 2 - 1,
                    0,
                    random.nextDouble() * 2 - 1
            ).normalize().multiply(minDistanceFromTarget);

            BlockPos pos = targetPos.add((int) offset.getX(), 0, (int) offset.getZ());

            BlockPos validPos = findNearestAirBlock(pos, world);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private BlockPos findNearestAirBlock(BlockPos pos, World world) {
        for (int yOffset = -5; yOffset <= 5; yOffset++) {
            BlockPos newPos = pos.up(yOffset);
            if (world.getBlockState(newPos).isAir()) {
                return newPos;
            }
        }
        return null;
    }

    public boolean canPathToPlayer(boolean flying) {
        if (pathFinder == null) return false;
        return pathFinder.canPathfind(getBoundPlayer(), flying);
    }

    public boolean canPathToPlayerFromGround(boolean flying) {
        if (groundPathFinder == null) return false;
        return groundPathFinder.canPathfind(getBoundPlayer(), flying);
    }

    public void updateNavigation() {
        if (mining) {
            setNavigationMining();
        }
        else if (flying) {
            setNavigationFlying();
        }
        else {
            setNavigationWalking();
        }
    }

    public void updateMoveControl() {
        if (flying || mining) {
            setMoveControlFlight();
        }
        else {
            setMoveControlWalking();
        }
    }

    public void setNavigationFlying() {
        setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        navigation = new BirdNavigation(this, getWorld());
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        navigation = new MobNavigation(this, getWorld());
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        setPathfindingPenalty(PathNodeType.BLOCKED, 0);
        navigation = new MiningNavigation(this, getWorld());
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        if (getBoundPlayer() == null) return;
        if (this.distanceTo(getBoundPlayer()) > MAX_DISTANCE) return;
        if (navigation instanceof BirdNavigation) {
            navigation.setSpeed(1);
            Path path = navigation.findPathTo(getBoundPlayer(), 0);
            if (path != null) navigation.startMovingAlong(path, 1);
        }
        else {
            navigation.setSpeed(MOVEMENT_SPEED);
            Path path = navigation.findPathTo(getBoundPlayer(), 0);
            if (path != null) navigation.startMovingAlong(path, MOVEMENT_SPEED);
        }
    }

    private double lastSpeedMultiplier = 1;
    public void updateMovementSpeed() {
        Path path = navigation.getCurrentPath();
        if (path != null) {
            double length = path.getLength();
            double speedMultiplier = 1;
            if (length > 10) {
                speedMultiplier += length / 100.0;
            }
            if (speedMultiplier != lastSpeedMultiplier) {
                lastSpeedMultiplier = speedMultiplier;
                double movementSpeed = MOVEMENT_SPEED * speedMultiplier * GLOBAL_SPEED_MULTIPLIER;
                double flyingSpeed = FLYING_SPEED * speedMultiplier * GLOBAL_SPEED_MULTIPLIER;
                if (isNerfed()) {
                    movementSpeed *= 0.7;
                    flyingSpeed *= 0.7;
                }
                //? if <= 1.21 {
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED)).setBaseValue(flyingSpeed);
                //?} else {
                /*Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.FLYING_SPEED)).setBaseValue(flyingSpeed);
                *///?}
            }
        }
    }

    public void setMoveControlFlight() {
        setNoGravity(true);
        moveControl = new FlightMoveControl(this, 20, true);
    }

    public void setMoveControlWalking() {
        setNoGravity(false);
        moveControl = new MoveControl(this);
    }

    @Nullable
    public ServerPlayerEntity getBoundPlayer() {
        if (server == null) return null;
        ServerPlayerEntity player = PlayerUtils.getPlayer(boundPlayerUUID);
        if (player == null || (player.isSpectator() && player.isDead())) {
            nullPlayerChecks++;
            return null;
        }
        nullPlayerChecks = 0;
        if (player.isSpectator()) return null;
        if (player.isDead()) return null;
        return player;
    }

    /*
        Override vanilla things
     */

    @Override
    public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
        return motion;
    }

    @Override
    protected boolean shouldSwimInFluids() {
        return false;
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.setFlag(4, false);
    }

    boolean isInLavaLocal = false;
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
                    FluidState fluidState = this.getWorld().getFluidState(mutable);
                    if (fluidState.isIn(tag)) {
                        double e = q + fluidState.getHeight(this.getWorld(), mutable);
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
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        killPathFinders();
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

    /*
        Sounds
     */

    private int propellerSoundCooldown = 0;
    private int walkSoundCooldown = 0;
    private boolean lastFlying = false;
    private boolean lastGlidingOrLanding = false;
    public void playSounds() {
        if (soundCooldown > 0) {
            soundCooldown--;
        }

        if (isInLavaLocal && random.nextInt(100) == 0) {
            playLavaSound();
        }

        if (isOnFire() && random.nextInt(100) == 0) {
            playBurnSound();
        }

        if (getAir() == 0 && random.nextInt(100) == 0) {
            playDrownSound();
        }

        if (gliding || landing) {
            if (!lastGlidingOrLanding) {
                playFallSound();
            }
        }

         if (flying) {
            if (!lastFlying) {
                playFlySound();
            }
            if (propellerSoundCooldown > 0) {
                propellerSoundCooldown--;
            }
            if (propellerSoundCooldown == 0) {
                propellerSoundCooldown=40;
                playPropellerSound();
            }
        }
        if (!flying && !gliding && !landing && forwardSpeed > 0.001) {
            if (walkSoundCooldown > 0) {
                walkSoundCooldown--;
            }
            if (walkSoundCooldown == 0) {
                walkSoundCooldown = 22;
                playWalkSound();
            }
        }
        lastFlying = flying;
        lastGlidingOrLanding = gliding || landing;
    }

    public void playAttackSound() {
        playRandomSound("attack", 0.25f, 1, 9);
    }

    public void playBurnSound() {
        playRandomSound("burn", 0.25f, 1, 9);
    }

    public void playDrownSound() {
        playRandomSound("drown", 0.25f, 1, 9);
    }

    public void playFallSound() {
        playRandomSound("fall", 0.25f, 1, 5);
    }

    public void playFlySound() {
        playRandomSound("fly", 0.25f, 1, 8);
    }

    public void playPropellerSound() {
        int cooldownBefore = soundCooldown;
        soundCooldown = 0;
        playRandomSound("propeller", 0.2f, 0, 0);
        soundCooldown = cooldownBefore;
    }

    public void playWalkSound() {
        int cooldownBefore = soundCooldown;
        soundCooldown = 0;
        playRandomSound("walk", 0.2f, 0, 0);
        soundCooldown = cooldownBefore;
    }

    public void playLavaSound() {
        playRandomSound("lava", 0.25f, 1, 2);
    }

    public void playThrowSound() {
        playRandomSound("throw", 0.25f, 1, 7);
    }

    private int soundCooldown = 0;
    public void playRandomSound(String name, float volume, int from, int to) {
        if (soundCooldown > 0) return;
        soundCooldown = 20;
        SoundEvent sound = OtherUtils.getRandomSound("wildlife_snail_"+name, from, to);
        this.playSound(sound, volume, 1);
    }
}
