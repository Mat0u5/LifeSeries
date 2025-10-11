package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.goal.MiningNavigation;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

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
        if (snail.age % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateMoveControl();
            updateNavigation();
        }
        else if (snail.age % 21 == 0) {
            updateMovementSpeed();
        }
        else if (snail.age % 5 == 0) {
            updateNavigationTarget();
        }
    }

    public void updatePathFinders() {
        if (snail.getSnailWorld().isClient()) return;
        if (pathFinder != null && pathFinder.isRegionUnloaded()) {
            pathFinder.discard();
            pathFinder = null;
        }
        else if (pathFinder == null || pathFinder.isRemoved()) {
            pathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) snail.getSnailWorld(), snail.getBlockPos(), SpawnReason.COMMAND);
        }
        else {
            pathFinder.resetDespawnTimer();
        }

        if (groundPathFinder != null && groundPathFinder.isRegionUnloaded()) {
            groundPathFinder.discard();
            groundPathFinder = null;
        }
        else if (groundPathFinder == null || groundPathFinder.isRemoved()) {
            groundPathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) snail.getSnailWorld(), snail.getBlockPos(), SpawnReason.COMMAND);
        }
        else {
            groundPathFinder.resetDespawnTimer();
        }

        ServerWorld world = (ServerWorld) snail.getSnailWorld();
        //? if <= 1.21 {
        if (pathFinder != null) pathFinder.teleport(world, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch());
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleport(world, snail.getX(), pos.getY() + 1.0, snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch());
        //?} else {
        /*if (pathFinder != null) pathFinder.teleport(world, snail.getX(), snail.getY(), snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch(), false);
        BlockPos pos = getGroundBlock();
        if (pos == null) return;
        if (groundPathFinder != null) groundPathFinder.teleport(world, snail.getX(), pos.getY()+1, snail.getZ(), EnumSet.noneOf(PositionFlag.class), snail.getYaw(), snail.getPitch(), false);
        *///?}
    }

    public void killPathFinders() {
        if (!snail.getSnailWorld().isClient()) {
            //? if <= 1.21 {
            if (groundPathFinder != null) groundPathFinder.kill();
            if (pathFinder != null) pathFinder.kill();
            //?} else {
            /*if (groundPathFinder != null) groundPathFinder.kill((ServerWorld) WorldUtils.getEntityWorld(groundPathFinder));
            if (pathFinder != null) pathFinder.kill((ServerWorld) WorldUtils.getEntityWorld(pathFinder));
            *///?}
            if (groundPathFinder != null) groundPathFinder.discard();
            if (pathFinder != null) pathFinder.discard();
        }
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = WorldUtils.getEntityPos(snail);
        Vec3d endPos = new Vec3d(startPos.getX(), snail.getSnailWorld().getBottomY(), startPos.getZ());

        BlockHitResult result = snail.getSnailWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
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
        if (snail.getSnailWorld() instanceof ServerWorld world) {
            Entity boundEntity = snail.serverData.getBoundEntity();
            ServerPlayerEntity boundPlayer = snail.serverData.getBoundPlayer();
            if (boundEntity == null || boundPlayer == null) return;
            if (WorldUtils.getEntityWorld(boundEntity) instanceof ServerWorld entityWorld) {
                if (!snail.serverData.shouldPathfind()) return;
                BlockPos tpTo = getBlockPosNearTarget(boundEntity, minDistanceFromPlayer);
                world.playSound(null, snail.getX(), snail.getY(), snail.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getSoundPitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getSoundPitch());
                AnimationUtils.spawnTeleportParticles(world, WorldUtils.getEntityPos(snail));
                AnimationUtils.spawnTeleportParticles(entityWorld, tpTo.toCenterPos());
                snail.serverData.despawn();
                Snails.spawnSnailFor(boundPlayer, tpTo);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        BlockPos targetPos = target.getBlockPos();
        return WorldUtils.getCloseBlockPos(WorldUtils.getEntityWorld(target), targetPos, distanceFromTarget, 1, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3d targetPos = snail.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.ofFloored(targetPos.x, targetPos.y, targetPos.z);
        return WorldUtils.getCloseBlockPos(WorldUtils.getEntityWorld(target), targetBlockPos, distanceFromTarget, 1, false);
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
        BlockState block = WorldUtils.getEntityWorld(groundPathFinder).getBlockState(groundPathFinder.getBlockPos());
        if (block.isOf(Blocks.LAVA)) return false;
        if (block.isOf(Blocks.WATER)) return false;
        if (block.isOf(Blocks.POWDER_SNOW)) return false;
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
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new BirdNavigation(snail, snail.getSnailWorld()));
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MobNavigation(snail, snail.getSnailWorld()));
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, 0);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, 0);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MiningNavigation(snail, snail.getSnailWorld()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        Vec3d targetPos = snail.serverData.getPlayerPos();
        if (!snail.serverData.shouldPathfind() || targetPos == null ||
                snail.squaredDistanceTo(targetPos) > (Snail.MAX_DISTANCE*Snail.MAX_DISTANCE)) {
            snail.getNavigation().stop();
            return;
        }

        if (snail.getNavigation() instanceof BirdNavigation) {
            snail.getNavigation().setSpeed(1);
            Path path = snail.getNavigation().findPathTo(targetPos.x, targetPos.y, targetPos.z, 0);
            if (path != null) snail.getNavigation().startMovingAlong(path, 1);
        }
        else {
            snail.getNavigation().setSpeed(Snail.MOVEMENT_SPEED);
            Path path = snail.getNavigation().findPathTo(targetPos.x, targetPos.y, targetPos.z, 0);
            if (path != null) snail.getNavigation().startMovingAlong(path, Snail.MOVEMENT_SPEED);
        }
    }

    private double lastSpeedMultiplier = 0.99;
    public void updateMovementSpeed() {
        Path path = snail.getNavigation().getCurrentPath();
        if (path != null) {
            double length = path.getLength();
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
                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED)).setBaseValue(flyingSpeed);
                //?} else {
                /*Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.FLYING_SPEED)).setBaseValue(flyingSpeed);
                *///?}
            }
        }
    }

    public void setMoveControlFlight() {
        snail.setNoGravity(true);
        snail.setMoveControl(new FlightMoveControl(snail, 20, true));
    }

    public void setMoveControlWalking() {
        snail.setNoGravity(false);
        snail.setMoveControl(new MoveControl(snail));
    }
}
