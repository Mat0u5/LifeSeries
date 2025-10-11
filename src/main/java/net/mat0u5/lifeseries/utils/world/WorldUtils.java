package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WorldUtils {

    public static int findTopSafeY(World world, Vec3d pos) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos.getX(), world.getHeight(), pos.getZ());
        // Check upwards or downwards for the first safe position
        while (mutablePos.getY() >= world.getBottomY()) {
            if (isSafeSpot(world, mutablePos)) {
                return mutablePos.getY(); // Found a safe spot
            }
            mutablePos.move(0, -1, 0);
        }
        // Fallback to original position if no safe spot found
        return (int) pos.getY();
    }

    public static boolean isSafeSpot(World world, BlockPos.Mutable pos) {
        // Check if the block below is solid
        boolean isSolidBlockBelow = world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down(), new ZombieEntity(world));

        // Check if the current position and one above are non-collision blocks (air, water, etc.)
        boolean isNonCollisionAbove = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()
                && world.getBlockState(pos.up()).getCollisionShape(world, pos.up()).isEmpty();

        return isSolidBlockBelow && isNonCollisionAbove;
    }

    public static void summonHarmlessLightning(ServerPlayerEntity player) {
        summonHarmlessLightning(PlayerUtils.getServerWorld(player), WorldUtils.getEntityPos(player));
    }

    public static void summonHarmlessLightning(ServerWorld world, Vec3d pos) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setPos(pos.x, pos.y, pos.z);
        lightning.setCosmetic(true);
        world.spawnEntity(lightning);
    }

    public static BlockPos getCloseBlockPos(World world, BlockPos targetPos, double distanceFromTarget, int height, boolean bottomSupport) {
        for (int attempts = 0; attempts < 20; attempts++) {
            Vec3d offset = new Vec3d(
                    world.random.nextDouble() * 2 - 1,
                    0,
                    world.random.nextDouble() * 2 - 1
            ).normalize().multiply(distanceFromTarget);

            BlockPos pos = targetPos.add((int) offset.getX(), 0, (int) offset.getZ());

            BlockPos validPos = findNearestAirBlock(pos, world, height, bottomSupport);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private static BlockPos findNearestAirBlock(BlockPos pos, World world, int height, boolean bottomSupport) {
        for (int yOffset = 5; yOffset >= -5; yOffset--) {
            BlockPos newPos = pos.up(yOffset);
            if (bottomSupport) {
                BlockPos bottomPos = newPos.down();
                if (!world.getBlockState(bottomPos).isSideSolidFullSquare(world, bottomPos, Direction.UP)) {
                    continue;
                }
            }
            boolean allAir = true;
            for (int i = 0; i < height; i++) {
                BlockPos airTest = newPos.up(i);
                if (!world.getBlockState(airTest).isAir()) {
                    allAir = false;
                }
            }
            if (allAir) {
                return newPos;
            }

        }
        return null;
    }

    public static World getEntityWorld(Entity entity) {
        //? if = 1.21.6 {
        /*return entity.getWorld();
         *///?} else {
        return entity.getEntityWorld();
        //?}
    }
    public static Vec3d getEntityPos(Entity entity) {
        //? if <= 1.21.6 {
        return entity.getPos();
         //?} else {
        /*return entity.getEntityPos();
        *///?}
    }
}
