package net.mat0u5.lifeseries.utils.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

//? if <= 1.21
//? if >= 1.21.2
/*import net.minecraft.world.entity.EntitySpawnReason;*/

//? if <= 1.21.9 {
import net.minecraft.world.entity.monster.Zombie;
//?} else {
/*import net.minecraft.world.entity.monster.zombie.Zombie;
*///?}

public class LevelUtils {

    public static int findTopSafeY(Level level, Vec3 pos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.x(), level.getHeight(), pos.z());
        // Check upwards or downwards for the first safe position
        //? if <= 1.21 {
        int minBuildHeight = level.getMinBuildHeight();
        //?} else {
        /*int minBuildHeight = level.getMinY();
        *///?}
        while (mutablePos.getY() >= minBuildHeight) {
            if (isSafeSpot(level, mutablePos)) {
                return mutablePos.getY(); // Found a safe spot
            }
            mutablePos.move(0, -1, 0);
        }
        // Fallback to original position if no safe spot found
        return (int) pos.y();
    }

    public static boolean isSafeSpot(Level level, BlockPos.MutableBlockPos pos) {
        // Check if the block below is solid
        boolean isSolidBlockBelow = level.getBlockState(pos.below()).entityCanStandOn(level, pos.below(), new Zombie(level));

        // Check if the current position and one above are non-collision blocks (air, water, etc.)
        boolean isNonCollisionAbove = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();

        return isSolidBlockBelow && isNonCollisionAbove;
    }

    public static void summonHarmlessLightning(ServerPlayer player) {
        summonHarmlessLightning(player.ls$getServerLevel(), player.position());
    }

    public static void summonHarmlessLightning(ServerLevel level, Vec3 pos) {
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setPosRaw(pos.x, pos.y, pos.z);
        lightning.setVisualOnly(true);
        level.addFreshEntity(lightning);
    }

    public static BlockPos getCloseBlockPos(Level level, BlockPos targetPos, double distanceFromTarget, int height, boolean bottomSupport) {
        for (int attempts = 0; attempts < 20; attempts++) {
            Vec3 offset = new Vec3(
                    level.random.nextDouble() * 2 - 1,
                    0,
                    level.random.nextDouble() * 2 - 1
            ).normalize().scale(distanceFromTarget);

            BlockPos pos = targetPos.offset((int) offset.x(), 0, (int) offset.z());

            BlockPos validPos = findNearestAirBlock(pos, level, height, bottomSupport);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private static BlockPos findNearestAirBlock(BlockPos pos, Level level, int height, boolean bottomSupport) {
        for (int yOffset = 5; yOffset >= -5; yOffset--) {
            BlockPos newPos = pos.above(yOffset);
            if (bottomSupport) {
                BlockPos bottomPos = newPos.below();
                if (!level.getBlockState(bottomPos).isFaceSturdy(level, bottomPos, Direction.UP)) {
                    continue;
                }
            }
            boolean allAir = true;
            for (int i = 0; i < height; i++) {
                BlockPos airTest = newPos.above(i);
                if (!level.getBlockState(airTest).isAir()) {
                    allAir = false;
                }
            }
            if (allAir) {
                return newPos;
            }

        }
        return null;
    }

    public static <T extends Entity> T spawnEntity(EntityType<T> entityType, ServerLevel level, BlockPos pos) {
        //? if <= 1.21 {
        return entityType.spawn(level, pos, MobSpawnType.COMMAND);
        //?} else {
        /*return entityType.spawn(level, pos, EntitySpawnReason.COMMAND);
        *///?}
    }

    public static void teleport(Entity entity, ServerLevel level, double destX, double destY, double destZ) {
        teleport(entity, level, destX, destY, destZ, entity.getYRot(), entity.getXRot());
    }

    public static void teleport(Entity entity, ServerLevel level, BlockPos pos) {
        //? if <= 1.20.5 {
        /*teleport(entity, level, pos.getCenter());
         *///?} else {
        teleport(entity, level, pos.getBottomCenter());
        //?}
    }

    public static void teleport(Entity entity, ServerLevel level, Vec3 pos) {
        teleport(entity, level, pos.x(), pos.y(), pos.z(), entity.getYRot(), entity.getXRot());
    }

    public static void teleport(Entity entity, ServerLevel level, Vec3 pos, float yaw, float pitch) {
        teleport(entity, level, pos.x(), pos.y(), pos.z(), yaw, pitch);
    }

    public static void teleport(Entity entity, ServerLevel level, double destX, double destY, double destZ, float yaw, float pitch) {
        //? if <= 1.21 {
        entity.teleportTo(level, destX, destY, destZ, EnumSet.noneOf(RelativeMovement.class), yaw, pitch);
        //?} else {
        /*entity.teleportTo(level, destX, destY, destZ, EnumSet.noneOf(Relative.class), yaw, pitch, false);
         *///?}
    }
}