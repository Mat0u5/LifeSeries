package net.mat0u5.lifeseries.entity;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerBoundEntity {
    UUID boundPlayerUUID = null;
    default boolean hasBoundPlayer() {
        if (boundPlayerUUID == null) return false;
        if (getBoundPlayer() == null) return false;
        return true;
    }

    @Nullable
    default PlayerEntity getBoundPlayer() {
        if (Main.isLogicalSide()) {
            return PlayerUtils.getPlayer(boundPlayerUUID);
        }
        return null;
    }

    @Nullable
    default Vec3d getPlayerPos() {
        if (Main.isLogicalSide()) {
            PlayerEntity player = getBoundPlayer();
            if (player != null) {
                return WorldUtils.getEntityPos(player);
            }
        }
        return null;
    }
}
