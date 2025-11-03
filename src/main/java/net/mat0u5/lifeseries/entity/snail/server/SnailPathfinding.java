package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.goal.MiningNavigation;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RelativeMovement;
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
        if (snail.getSnailWorld().isClientSide()) return;
        if (pathFinder != null && pathFinder.touchingUnloadedChunk()) {
            pathFinder.discard();
            pathFinder = null;
        }
        else if (pathFinder == null || pathFinder.isRemoved()) {
            pathFinder = MobRegistry.PATH_FINDER.spawn((ServerLevel) snail.getSnailWorld(), snail.blockPosition(), MobSpawnType.COMMAND);
        }
        else {
            pathFinder.resetDespawnTimer();
        }

        if (groundPathFinder != null && groundPathFinder.touchingUnloadedChunk()) {
            groundPathFinder.discard();
            groundPathFinder = null;
        }
        else if (groundPathFinder == null || groundPathFinder.isRemoved()) {
            groundPathFinder = MobRegistry.PATH_FINDER.spawn((ServerLevel) snail.getSnailWorld(), snail.blockPosition(), MobSpawnType.COMMAND);
        }
        else {
            groundPathFinder.resetDespawnTimer();
        }

        ServerLevel world = (ServerLevel) snail.getSnailWorld();
        //? if <= 1.21 {
        if (pathFinder != null) pathFinder.teleportTo(world, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(RelativeMovement.class), snail.getYRot(), snail.getXRot());
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleportTo(world, snail.getX(), pos.getY() + 1.0, snail.getZ(), EnumSet.noneOf(RelativeMovement.class), snail.getYRot(), snail.getXRot());
        //?} else {
        /*if (pathFinder != null) pathFinder.teleport(world, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch(), false);
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleport(world, snail.getX(), pos.getY()+1, snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch(), false);
        *///?}
    }

    public void killPathFinders() {
        if (!snail.getSnailWorld().isClientSide()) {
            //? if <= 1.21 {
            if (groundPathFinder != null) groundPathFinder.kill();
            if (pathFinder != null) pathFinder.kill();
            //?} else {
            /*if (groundPathFinder != null) groundPathFinder.kill((ServerWorld) groundPathFinder.ls$getEntityWorld());
            if (pathFinder != null) pathFinder.kill((ServerWorld) pathFinder.ls$getEntityWorld());
            *///?}
            if (groundPathFinder != null) groundPathFinder.discard();
            if (pathFinder != null) pathFinder.discard();
        }
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3 startPos = snail.ls$getEntityPos();
        Vec3 endPos = new Vec3(startPos.x(), snail.getSnailWorld().getMinBuildHeight(), startPos.z());

        BlockHitResult result = snail.getSnailWorld().clip(
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
        if (snail.getSnailWorld() instanceof ServerLevel world) {
            Entity boundEntity = snail.serverData.getBoundEntity();
            ServerPlayer boundPlayer = snail.serverData.getBoundPlayer();
            if (boundEntity == null || boundPlayer == null) return;
            if (boundEntity.ls$getEntityWorld() instanceof ServerLevel entityWorld) {
                if (!snail.serverData.shouldPathfind()) return;
                BlockPos tpTo = getBlockPosNearTarget(boundEntity, minDistanceFromPlayer);
                world.playSound(null, snail.getX(), snail.getY(), snail.getZ(), SoundEvents.PLAYER_TELEPORT, snail.getSoundSource(), snail.soundVolume(), snail.getVoicePitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.PLAYER_TELEPORT, snail.getSoundSource(), snail.soundVolume(), snail.getVoicePitch());
                AnimationUtils.spawnTeleportParticles(world, snail.ls$getEntityPos());
                AnimationUtils.spawnTeleportParticles(entityWorld, tpTo.getCenter());
                snail.serverData.despawn();
                Snails.spawnSnailFor(boundPlayer, tpTo);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        BlockPos targetPos = target.blockPosition();
        return WorldUtils.getCloseBlockPos(target.ls$getEntityWorld(), targetPos, distanceFromTarget, 1, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3 targetPos = snail.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.containing(targetPos.x, targetPos.y, targetPos.z);
        return WorldUtils.getCloseBlockPos(target.ls$getEntityWorld(), targetBlockPos, distanceFromTarget, 1, false);
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
        BlockState block = groundPathFinder.ls$getEntityWorld().getBlockState(groundPathFinder.blockPosition());
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
        snail.setNavigation(new FlyingPathNavigation(snail, snail.getSnailWorld()));
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingMalus(PathType.BLOCKED, -1);
        snail.setPathfindingMalus(PathType.TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, -1);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, -1);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new GroundPathNavigation(snail, snail.getSnailWorld()));
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        snail.setPathfindingMalus(PathType.BLOCKED, 0);
        snail.setPathfindingMalus(PathType.TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingMalus(PathType.WALKABLE_DOOR, 0);
        snail.setPathfindingMalus(PathType.DOOR_OPEN, 0);
        snail.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MiningNavigation(snail, snail.getSnailWorld()));
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
                /*Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.FLYING_SPEED)).setBaseValue(flyingSpeed);
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
