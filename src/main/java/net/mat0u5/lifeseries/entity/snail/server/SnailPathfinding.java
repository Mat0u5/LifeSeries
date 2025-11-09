package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.goal.MiningNavigation;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("resource")
public class SnailPathfinding {
    private final Snail snail;

    private GroundPathNavigation groundNavigation;
    private FlyingPathNavigation flyingNavigation;
    private MiningNavigation miningNavigation;

    private NavigationMode currentMode = NavigationMode.WALKING;
    private NavigationMode lastMode = NavigationMode.WALKING;

    private int pathfindingCacheTicks = 0;
    private static final int PATHFINDING_CACHE_DURATION = 20;
    private boolean cachedCanPathOnGround = true;
    private boolean cachedCanPathFlying = true;

    public boolean navigationInit = false;
    private double lastSpeedMultiplier = 0.99;

    public SnailPathfinding(Snail snail) {
        this.snail = snail;
        initializeNavigations();
    }

    private enum NavigationMode {
        WALKING,
        FLYING,
        MINING
    }

    private void initializeNavigations() {
        groundNavigation = new GroundPathNavigation(snail, snail.level());
        flyingNavigation = new FlyingPathNavigation(snail, snail.level());
        miningNavigation = new MiningNavigation(snail, snail.level());

        groundNavigation.setCanFloat(false);
        groundNavigation.setCanOpenDoors(false);
        groundNavigation.setCanWalkOverFences(false);

        flyingNavigation.setCanFloat(false);
        miningNavigation.setCanFloat(false);
        //flyingNavigation.setMaxVisitedNodesMultiplier(10.0f);
        //miningNavigation.setMaxVisitedNodesMultiplier(10.0f);
    }

    public void tick() {
        if (snail.isPaused()) {
            snail.getNavigation().stop();
            return;
        }

        if (snail.tickCount % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateMoveControl();
            updateNavigation();
        }
        else if (snail.tickCount % 21 == 0) {
            updateMovementSpeed();
        }
        else if (snail.tickCount % 5 == 0) {
            updateNavigationTarget();
        }

        if (pathfindingCacheTicks <= 0) {
            updatePathfindingCache();
            pathfindingCacheTicks = PATHFINDING_CACHE_DURATION;
        }
        else {
            pathfindingCacheTicks--;
        }

        if (snail.tickCount % 10 == 0) {
            checkPathStaleness();
        }
    }

    private void checkPathStaleness() {
        if (!snail.serverData.shouldPathfind()) return;

        Path currentPath = snail.getNavigation().getPath();
        if (currentPath != null && (currentPath.isDone() || currentPath.getNodeCount() <= 1)) {
            updateNavigationTarget();
        }
    }

    private void updatePathfindingCache() {
        if (!snail.serverData.shouldPathfind() || snail.level().isClientSide()) {
            return;
        }

        LivingEntity target = snail.serverData.getBoundEntity();
        if (target == null) {
            cachedCanPathOnGround = false;
            cachedCanPathFlying = false;
            return;
        }

        Vec3 targetPos = target.position();

        BlockPos groundPos = getGroundBlock();

        if (groundPos != null) {
            Vec3 originalPos = snail.position();
            double groundY = groundPos.getY() + 1.0;

            boolean wasOnGround = snail.onGround();
            if (!wasOnGround) {
                snail.setOnGround(true);
                snail.setPos(snail.getX(), groundY, snail.getZ());
            }
            Path groundPath = groundNavigation.createPath(target, 0);
            if (!wasOnGround) {
                snail.setPos(originalPos.x, originalPos.y, originalPos.z);
                snail.setOnGround(wasOnGround);
            }

            cachedCanPathOnGround =  groundPath != null && groundPath.getEndNode() != null && groundPath.getEndNode().asBlockPos().getCenter().distanceTo(targetPos) < 2;
        }
        else {
            cachedCanPathOnGround = false;
        }

        if (!cachedCanPathOnGround) {
            Path flyingPath = flyingNavigation.createPath(target, 0);
            cachedCanPathFlying =  flyingPath != null && flyingPath.getEndNode() != null && flyingPath.getEndNode().asBlockPos().getCenter().distanceTo(targetPos) < 2;
        }
        else {
            cachedCanPathFlying = true;
        }
    }

    public boolean isValidGroundPosition(BlockPos groundPos) {
        if (groundPos == null) return false;
        BlockState block = snail.level().getBlockState(groundPos);
        if (block.is(Blocks.LAVA)) return false;
        if (block.is(Blocks.WATER)) return false;
        if (block.is(Blocks.POWDER_SNOW)) return false;
        return true;
    }

    public boolean canPathToPlayer(boolean requireFlying) {
        if (!snail.serverData.shouldPathfind()) return false;

        if (requireFlying) {
            return cachedCanPathFlying;
        }
        return cachedCanPathOnGround;
    }

    public boolean canPathToPlayerFromGround(boolean flying) {
        BlockPos groundPos = getGroundBlock();
        if (!isValidGroundPosition(groundPos)) {
            return false;
        }
        return canPathToPlayer(flying);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3 startPos = snail.position();
        //? if <= 1.21 {
        int minY = snail.level().getMinBuildHeight();
        //?} else {
        /*int minY = snail.level().getMinY();
        *///?}
        Vec3 endPos = new Vec3(startPos.x(), minY, startPos.z());

        BlockHitResult result = snail.level().clip(
                new ClipContext(
                        startPos,
                        endPos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        snail
                )
        );
        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return snail.getY() - belowBlock.getY() - 1;
    }

    public void fakeTeleportNearPlayer(double minDistanceFromPlayer) {
        if (snail.level() instanceof ServerLevel level) {
            Entity boundEntity = snail.serverData.getBoundEntity();
            ServerPlayer boundPlayer = snail.serverData.getBoundPlayer();
            if (boundEntity == null || boundPlayer == null) return;
            if (boundEntity.level() instanceof ServerLevel entityWorld) {
                if (!snail.serverData.shouldPathfind()) return;
                BlockPos tpTo = getBlockPosNearTarget(boundEntity, minDistanceFromPlayer);
                if (tpTo == null) return;
                level.playSound(null, snail.getX(), snail.getY(), snail.getZ(), SoundEvents.PLAYER_TELEPORT, snail.getSoundSource(), snail.soundVolume(), snail.getVoicePitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.PLAYER_TELEPORT, snail.getSoundSource(), snail.soundVolume(), snail.getVoicePitch());
                AnimationUtils.spawnTeleportParticles(level, snail.position());
                AnimationUtils.spawnTeleportParticles(entityWorld, tpTo.getCenter());
                snail.serverData.despawn();
                Snails.spawnSnailFor(boundPlayer, tpTo);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        BlockPos targetPos = target.blockPosition();
        return LevelUtils.getCloseBlockPos(target.level(), targetPos, distanceFromTarget, 1, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3 targetPos = snail.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.containing(targetPos.x, targetPos.y, targetPos.z);
        return LevelUtils.getCloseBlockPos(target.level(), targetBlockPos, distanceFromTarget, 1, false);
    }

    public void updateNavigation() {
        NavigationMode desiredMode;

        if (snail.isSnailMining()) {
            desiredMode = NavigationMode.MINING;
        }
        else if (snail.isSnailFlying()) {
            desiredMode = NavigationMode.FLYING;
        }
        else {
            desiredMode = NavigationMode.WALKING;
        }

        if (desiredMode != currentMode) {
            switchNavigationMode(desiredMode);
            currentMode = desiredMode;
        }
    }

    private void switchNavigationMode(NavigationMode mode) {
        snail.getNavigation().stop();

        if (mode == NavigationMode.MINING) {
            setNavigationMining();
        }
        else if (mode == NavigationMode.FLYING) {
            setNavigationFlying();
        }
        else if (mode == NavigationMode.WALKING) {
            setNavigationWalking();
        }

        lastMode = mode;
    }

    public void updateMoveControl() {
        if (snail.isSnailFlying() || snail.isSnailMining()) {
            setMoveControlFlight();
        }
        else {
            setMoveControlWalking();
        }
    }

    public void setNavigationFlying() {
        snail.setPathfindingMalus(PathType.BLOCKED, -1);
        snail.setPathfindingMalus(PathType.TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, -1);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, -1);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setPathfindingMalus(PathType.FENCE, -1);
        snail.setPathfindingMalus(PathType.DAMAGE_OTHER, 0);
        snail.setPathfindingMalus(PathType.DANGER_OTHER, 0);
        snail.setPathfindingMalus(PathType.WALKABLE, 0);
        snail.setPathfindingMalus(PathType.OPEN, 0);
        snail.setNavigation(flyingNavigation);
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingMalus(PathType.BLOCKED, -1);
        snail.setPathfindingMalus(PathType.TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, -1);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, -1);
        snail.setPathfindingMalus(PathType.WATER, 8);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);

        snail.setPathfindingMalus(PathType.FENCE, 0);
        snail.setPathfindingMalus(PathType.DAMAGE_OTHER, 0);
        snail.setPathfindingMalus(PathType.DANGER_OTHER, 0);
        snail.setPathfindingMalus(PathType.WALKABLE, 0);
        snail.setPathfindingMalus(PathType.OPEN, 0);

        snail.setNavigation(groundNavigation);
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        snail.setPathfindingMalus(PathType.BLOCKED, 4.0f);
        snail.setPathfindingMalus(PathType.TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, 0);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, 0);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setPathfindingMalus(PathType.FENCE, 0);
        snail.setPathfindingMalus(PathType.DAMAGE_OTHER, 0);
        snail.setPathfindingMalus(PathType.DANGER_OTHER, 0);
        snail.setPathfindingMalus(PathType.WALKABLE, 0);
        snail.setPathfindingMalus(PathType.OPEN, 0);
        snail.setNavigation(miningNavigation);
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        if (snail.serverData.shouldPathfind() && snail.serverData.getPlayerPos() != null) {
            snail.setTarget(snail.serverData.getBoundEntity());
        }
        else {
            snail.setTarget(null);
        }

        PathNavigation nav = snail.getNavigation();
        if (nav instanceof FlyingPathNavigation) {
            nav.setSpeedModifier(1);
        }
        else {
            nav.setSpeedModifier(Snail.MOVEMENT_SPEED);
        }
    }

    public void updateMovementSpeed() {
        Path path = snail.getNavigation().getPath();
        if (path != null) {
            double length = path.getNodeCount();
            double speedMultiplier = 1;
            if (length > 10) {
                speedMultiplier += length / 100.0;
            }
            if (speedMultiplier != lastSpeedMultiplier) {
                lastSpeedMultiplier = speedMultiplier;
                double movementSpeed = Snail.MOVEMENT_SPEED * speedMultiplier * Snail.GLOBAL_SPEED_MULTIPLIER;
                double flyingSpeed = Snail.FLYING_SPEED * speedMultiplier * Snail.GLOBAL_SPEED_MULTIPLIER;
                if (snail.serverData.isNerfed()) {
                    movementSpeed *= 0.6;
                    flyingSpeed *= 0.6;
                }
                if (movementSpeed < 0.01) movementSpeed = 0.01;
                if (flyingSpeed < 0.01) flyingSpeed = 0.01;

                //? if <= 1.21 {
                Objects.requireNonNull(snail.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttribute(Attributes.FLYING_SPEED)).setBaseValue(flyingSpeed);
                //?} else {
                /*Objects.requireNonNull(snail.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttribute(Attributes.FLYING_SPEED)).setBaseValue(flyingSpeed);
                *///?}
            }
        }
    }

    public void setMoveControlFlight() {
        snail.setNoGravity(true);
        snail.setMoveControl(new FlyingMoveControl(snail, 20, true));
    }

    public void setMoveControlWalking() {
        snail.setNoGravity(false);
        snail.setMoveControl(new MoveControl(snail));
    }

    public void cleanup() {
        if (groundNavigation != null) groundNavigation.stop();
        if (flyingNavigation != null) flyingNavigation.stop();
        if (miningNavigation != null) miningNavigation.stop();
    }
}