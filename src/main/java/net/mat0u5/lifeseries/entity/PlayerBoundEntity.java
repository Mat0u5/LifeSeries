package net.mat0u5.lifeseries.entity;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface PlayerBoundEntity {
    void onSetPlayer(ServerPlayerEntity player);
    UUID getBoundPlayerUUID();
    void setBoundPlayerUUID(UUID uuid);
    boolean shouldPathfind();

    default boolean hasBoundPlayer() {
        if (getBoundPlayerUUID() == null) return false;
        if (getBoundPlayer() == null) return false;
        return true;
    }

    default void setBoundPlayer(ServerPlayerEntity player) {
        if (player == null) return;
        setBoundPlayerUUID(player.getUuid());
        onSetPlayer(player);
    }

    default ServerPlayerEntity getBoundPlayer() {
        if (Main.isLogicalSide()) {
            return PlayerUtils.getPlayer(getBoundPlayerUUID());
        }
        return null;
    }

    default LivingEntity getBoundEntity() {
        if (Main.isLogicalSide()) {
            ServerPlayerEntity player = PlayerUtils.getPlayer(getBoundPlayerUUID());
            if (player != null) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                    if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection astralProjection) {
                        if (astralProjection.clone != null) {
                            return astralProjection.clone;
                        }
                    }
                }
            }
            return player;
        }
        return null;
    }

    default Vec3d getPlayerPos() {
        if (!Main.isLogicalSide()) return null;
        Entity entity = getBoundEntity();
        if (entity != null) {
            return WorldUtils.getEntityPos(entity);
        }
        return null;
    }
}
