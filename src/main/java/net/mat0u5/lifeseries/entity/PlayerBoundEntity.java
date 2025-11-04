package net.mat0u5.lifeseries.entity;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface PlayerBoundEntity {
    void onSetPlayer(ServerPlayer player);
    UUID getBoundPlayerUUID();
    void setBoundPlayerUUID(UUID uuid);
    boolean shouldPathfind();

    default boolean hasBoundPlayer() {
        if (getBoundPlayerUUID() == null) return false;
        if (getBoundPlayer() == null) return false;
        return true;
    }

    default void setBoundPlayer(ServerPlayer player) {
        if (player == null) return;
        setBoundPlayerUUID(player.getUUID());
        onSetPlayer(player);
    }

    default ServerPlayer getBoundPlayer() {
        if (Main.isLogicalSide()) {
            return PlayerUtils.getPlayer(getBoundPlayerUUID());
        }
        return null;
    }

    default LivingEntity getBoundEntity() {
        if (Main.isLogicalSide()) {
            ServerPlayer player = PlayerUtils.getPlayer(getBoundPlayerUUID());
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

    default Vec3 getPlayerPos() {
        if (!Main.isLogicalSide()) return null;
        Entity entity = getBoundEntity();
        if (entity != null) {
            return entity.position();
        }
        return null;
    }
}
