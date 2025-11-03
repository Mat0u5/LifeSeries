package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WorldUtils {

    public static int findTopSafeY(Level world, Vec3 pos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.x(), world.getHeight(), pos.z());
        // Check upwards or downwards for the first safe position
        while (mutablePos.getY() >= world.getMinBuildHeight()) {
            if (isSafeSpot(world, mutablePos)) {
                return mutablePos.getY(); // Found a safe spot
            }
            mutablePos.move(0, -1, 0);
        }
        // Fallback to original position if no safe spot found
        return (int) pos.y();
    }

    public static boolean isSafeSpot(Level world, BlockPos.MutableBlockPos pos) {
        // Check if the block below is solid
        boolean isSolidBlockBelow = world.getBlockState(pos.below()).entityCanStandOn(world, pos.below(), new Zombie(world));

        // Check if the current position and one above are non-collision blocks (air, water, etc.)
        boolean isNonCollisionAbove = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()
                && world.getBlockState(pos.above()).getCollisionShape(world, pos.above()).isEmpty();

        return isSolidBlockBelow && isNonCollisionAbove;
    }

    public static void summonHarmlessLightning(ServerPlayer player) {
        summonHarmlessLightning(PlayerUtils.getServerWorld(player), player.ls$getEntityPos());
    }

    public static void summonHarmlessLightning(ServerLevel world, Vec3 pos) {
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
        lightning.setPosRaw(pos.x, pos.y, pos.z);
        lightning.setVisualOnly(true);
        world.addFreshEntity(lightning);
    }

    public static BlockPos getCloseBlockPos(Level world, BlockPos targetPos, double distanceFromTarget, int height, boolean bottomSupport) {
        for (int attempts = 0; attempts < 20; attempts++) {
            Vec3 offset = new Vec3(
                    world.random.nextDouble() * 2 - 1,
                    0,
                    world.random.nextDouble() * 2 - 1
            ).normalize().scale(distanceFromTarget);

            BlockPos pos = targetPos.offset((int) offset.x(), 0, (int) offset.z());

            BlockPos validPos = findNearestAirBlock(pos, world, height, bottomSupport);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private static BlockPos findNearestAirBlock(BlockPos pos, Level world, int height, boolean bottomSupport) {
        for (int yOffset = 5; yOffset >= -5; yOffset--) {
            BlockPos newPos = pos.above(yOffset);
            if (bottomSupport) {
                BlockPos bottomPos = newPos.below();
                if (!world.getBlockState(bottomPos).isFaceSturdy(world, bottomPos, Direction.UP)) {
                    continue;
                }
            }
            boolean allAir = true;
            for (int i = 0; i < height; i++) {
                BlockPos airTest = newPos.above(i);
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
}
