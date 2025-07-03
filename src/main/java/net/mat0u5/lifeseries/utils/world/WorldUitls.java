package net.mat0u5.lifeseries.utils.world;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WorldUitls {

    public static double findSafeY(World world, Vec3d pos) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
        for (boolean movingUp : List.of(true,false)) {
            // Check upwards or downwards for the first safe position
            while (mutablePos.getY() >= world.getBottomY() && mutablePos.getY() < world.getHeight()) {
                if (isSafeSpot(world, mutablePos)) {
                    return mutablePos.getY(); // Found a safe spot
                }
                mutablePos.move(0, movingUp ? 1 : -1, 0);
            }
            mutablePos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
        }
        // Fallback to original position if no safe spot found
        return pos.getY();
    }

    public static boolean isSafeSpot(World world, BlockPos.Mutable pos) {
        // Check if the block below is solid
        boolean isSolidBlockBelow = world.getBlockState(pos.down()).hasSolidTopSurface(world, pos.down(), new ZombieEntity(world));

        // Check if the current position and one above are non-collision blocks (air, water, etc.)
        boolean isNonCollisionAbove = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()
                && world.getBlockState(pos.up()).getCollisionShape(world, pos.up()).isEmpty();

        return isSolidBlockBelow && isNonCollisionAbove;
    }

    public static void summonHarmlessLightning(ServerWorld world, Vec3d pos) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setPos(pos.x, pos.y, pos.z);
        lightning.setCosmetic(true);
        world.spawnEntity(lightning);
    }
}
