package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.goal.MiningNavigation;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

//? if <= 1.21
import net.minecraft.world.entity.RelativeMovement;
//? if >= 1.21.2
/*import net.minecraft.world.entity.Relative;*/

@SuppressWarnings("resource")
public class SnailPathfinding {
    private final Snail snail;
    public SnailPathfinding(Snail snail) {
        this.snail = snail;
    }
    @Nullable
    public PathFinder groundPathFinder;
    @Nullable
    public PathFinder pathFinder;
    public boolean navigationInit = false;

    public void tick() {
        updatePathFinders();
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
    }

    public void updatePathFinders() {
        if (snail.level().isClientSide()) return;
        if (pathFinder != null && pathFinder.touchingUnloadedChunk()) {
            pathFinder.discard();
            pathFinder = null;
        }
        else if (pathFinder == null || pathFinder.isRemoved()) {
            pathFinder = LevelUtils.spawnEntity(MobRegistry.PATH_FINDER, (ServerLevel) snail.level(), snail.blockPosition());
        }
        else {
            pathFinder.resetDespawnTimer();
        }

        if (groundPathFinder != null && groundPathFinder.touchingUnloadedChunk()) {
            groundPathFinder.discard();
            groundPathFinder = null;
        }
        else if (groundPathFinder == null || groundPathFinder.isRemoved()) {
            groundPathFinder = LevelUtils.spawnEntity(MobRegistry.PATH_FINDER, (ServerLevel) snail.level(), snail.blockPosition());
        }
        else {
            groundPathFinder.resetDespawnTimer();
        }

        ServerLevel level = (ServerLevel) snail.level();
        //? if <= 1.21 {
        if (pathFinder != null) pathFinder.teleportTo(level, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(RelativeMovement.class), snail.getYRot(), snail.getXRot());
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleportTo(level, snail.getX(), pos.getY() + 1.0, snail.getZ(), EnumSet.noneOf(RelativeMovement.class), snail.getYRot(), snail.getXRot());
        //?} else {
        /*if (pathFinder != null) pathFinder.teleportTo(level, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(Relative.class), snail.getYRot(), snail.getXRot(), false);
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleportTo(level, snail.getX(), pos.getY()+1, snail.getZ(), EnumSet.noneOf(Relative.class), snail.getYRot(), snail.getXRot(), false);
        *///?}
    }

    public void killPathFinders() {
        if (!snail.level().isClientSide()) {
            //? if <= 1.21 {
            if (groundPathFinder != null) groundPathFinder.kill();
            if (pathFinder != null) pathFinder.kill();
            //?} else {
            /*if (groundPathFinder != null) groundPathFinder.kill((ServerLevel) groundPathFinder.level());
            if (pathFinder != null) pathFinder.kill((ServerLevel) pathFinder.level());
            *///?}
            if (groundPathFinder != null) groundPathFinder.discard();
            if (pathFinder != null) pathFinder.discard();
        }
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

    public boolean canPathToPlayer(boolean flying) {
        if (!snail.serverData.shouldPathfind()) return false;
        if (pathFinder == null) return false;
        return pathFinder.canPathfind(snail.serverData.getBoundEntity(), flying);
    }

    public boolean canPathToPlayerFromGround(boolean flying) {
        if (!snail.serverData.shouldPathfind()) return false;
        if (groundPathFinder == null) return false;
        return groundPathFinder.canPathfind(snail.serverData.getBoundEntity(), flying);
    }

    public boolean isValidBlockOnGround() {
        if (groundPathFinder == null) return false;
        BlockState block = groundPathFinder.level().getBlockState(groundPathFinder.blockPosition());
        if (block.is(Blocks.LAVA)) return false;
        if (block.is(Blocks.WATER)) return false;
        if (block.is(Blocks.POWDER_SNOW)) return false;
        return true;
    }

    public void updateNavigation() {
        if (snail.isSnailMining()) {
            setNavigationMining();
        }
        else if (snail.isSnailFlying()) {
            setNavigationFlying();
        }
        else {
            setNavigationWalking();
        }
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
        snail.setNavigation(new FlyingPathNavigation(snail, snail.level()));
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingMalus(PathType.BLOCKED, -1);
        snail.setPathfindingMalus(PathType.TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, -1);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, -1);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new GroundPathNavigation(snail, snail.level()));
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        snail.setPathfindingMalus(PathType.BLOCKED, 0);
        snail.setPathfindingMalus(PathType.TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, 0);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, 0);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MiningNavigation(snail, snail.level()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        Vec3 targetPos = snail.serverData.getPlayerPos();
        if (!snail.serverData.shouldPathfind() || targetPos == null ||
                snail.distanceToSqr(targetPos) > (Snail.MAX_DISTANCE*Snail.MAX_DISTANCE)) {
            snail.getNavigation().stop();
            return;
        }

        if (snail.getNavigation() instanceof FlyingPathNavigation) {
            snail.getNavigation().setSpeedModifier(1);
            Path path = snail.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 0);
            if (path != null) snail.getNavigation().moveTo(path, 1);
        }
        else {
            snail.getNavigation().setSpeedModifier(Snail.MOVEMENT_SPEED);
            Path path = snail.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 0);
            if (path != null) snail.getNavigation().moveTo(path, Snail.MOVEMENT_SPEED);
        }
    }

    private double lastSpeedMultiplier = 0.99;
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
}