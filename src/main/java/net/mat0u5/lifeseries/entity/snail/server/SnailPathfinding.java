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

    public void updatePathFinders() {
        if (snail.getWorldEntity().isClient()) return;
        if (pathFinder != null && pathFinder.isRegionUnloaded()) {
            pathFinder.discard();
            pathFinder = null;
        }
        else if (pathFinder == null || pathFinder.isRemoved()) {
            pathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) snail.getWorldEntity(), snail.getBlockPos(), SpawnReason.COMMAND);
        }
        else {
            pathFinder.resetDespawnTimer();
        }

        if (groundPathFinder != null && groundPathFinder.isRegionUnloaded()) {
            groundPathFinder.discard();
            groundPathFinder = null;
        }
        else if (groundPathFinder == null || groundPathFinder.isRemoved()) {
            groundPathFinder = MobRegistry.PATH_FINDER.spawn((ServerWorld) snail.getWorldEntity(), snail.getBlockPos(), SpawnReason.COMMAND);
        }
        else {
            groundPathFinder.resetDespawnTimer();
        }

        ServerWorld world = (ServerWorld) snail.getWorldEntity();
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
        if (!snail.getWorldEntity().isClient()) {
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
        Vec3d endPos = new Vec3d(startPos.getX(), snail.getWorldEntity().getBottomY(), startPos.getZ());

        BlockHitResult result = snail.getWorldEntity().raycast(
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
        if (snail.getWorldEntity() instanceof ServerWorld world) {
            ServerPlayerEntity player = snail.serverData.getServerBoundPlayer();
            if (player == null) return;
            ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
            BlockPos tpTo = getBlockPosNearTarget(player, minDistanceFromPlayer);
            world.playSound(null, snail.getX(), snail.getY(), snail.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getSoundPitch());
            playerWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getSoundPitch());
            AnimationUtils.spawnTeleportParticles(world, WorldUtils.getEntityPos(snail));
            AnimationUtils.spawnTeleportParticles(playerWorld, tpTo.toCenterPos());
            snail.serverData.despawn();
            Snails.spawnSnailFor(player, tpTo);
        }
    }

    public static BlockPos getBlockPosNearTarget(PlayerEntity target, double distanceFromTarget) {
        if (target == null) return null;
        BlockPos targetPos = target.getBlockPos();
        return WorldUtils.getCloseBlockPos(PlayerUtils.getWorld(target), targetPos, distanceFromTarget, 1, false);
    }


    public boolean canPathToPlayer(boolean flying) {
        if (pathFinder == null) return false;
        return pathFinder.canPathfind(snail.serverData.getBoundPlayer(), flying);
    }

    public boolean canPathToPlayerFromGround(boolean flying) {
        if (groundPathFinder == null) return false;
        return groundPathFinder.canPathfind(snail.serverData.getBoundPlayer(), flying);
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
        if (snail.isMining()) {
            setNavigationMining();
        }
        else if (snail.isFlying()) {
            setNavigationFlying();
        }
        else {
            setNavigationWalking();
        }
    }

    public void updateMoveControl() {
        if (snail.isFlying() || snail.isMining()) {
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
        snail.setNavigation(new BirdNavigation(snail, snail.getWorldEntity()));
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MobNavigation(snail, snail.getWorldEntity()));
        updateNavigationTarget();
    }

    public void setNavigationMining() {
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, 0);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, 0);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setNavigation(new MiningNavigation(snail, snail.getWorldEntity()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        PlayerEntity boundPlayer = snail.serverData.getBoundPlayer();
        if (boundPlayer == null) return;
        if (snail.distanceTo(boundPlayer) > Snail.MAX_DISTANCE) return;
        if (snail.getNavigation() instanceof BirdNavigation) {
            snail.getNavigation().setSpeed(1);
            Path path = snail.getNavigation().findPathTo(boundPlayer, 0);
            if (path != null) snail.getNavigation().startMovingAlong(path, 1);
        }
        else {
            snail.getNavigation().setSpeed(Snail.MOVEMENT_SPEED);
            Path path = snail.getNavigation().findPathTo(boundPlayer, 0);
            if (path != null) snail.getNavigation().startMovingAlong(path, Snail.MOVEMENT_SPEED);
        }
    }

    private double lastSpeedMultiplier = 1;
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
