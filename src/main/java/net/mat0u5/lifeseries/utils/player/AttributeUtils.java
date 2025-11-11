package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class AttributeUtils {
    public static final double DEFAULT_PLAYER_JUMP_HEIGHT = 0.41999998688697815;
    public static final double DEFAULT_PLAYER_SAFE_FALL_HEIGHT = 3.0;
    public static final double DEFAULT_PLAYER_MOVEMENT_SPEED = 0.10000000149011612;
    public static final double DEFAULT_PLAYER_STEP_HEIGHT = 0.6;


    public static void resetAttributesOnPlayerJoin(ServerPlayer player) {
        if (player == null) return;
        resetMaxPlayerHealthIfNecessary(player);
        if (!TriviaHandler.cursedMoonJumpPlayers.contains(player.getUUID())) {
            resetPlayerJumpHeight(player);
        }
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
            resetSafeFallHeight(player);
        }
        resetMovementSpeed(player);
        resetStepHeight(player);
    }

    public static void resetMaxPlayerHealthIfNecessary(ServerPlayer player) {
        if (Main.modDisabled()) {
            resetMaxPlayerHealth(player);
            return;
        }
        if (currentSeason.getSeason() == Seasons.SECRET_LIFE) return;
        double currentMaxHealth = getMaxPlayerHealth(player);
        if (currentMaxHealth == 13 && TriviaHandler.cursedHeartPlayers.contains(player.getUUID())) return;
        if (currentMaxHealth == SuperpowersWildcard.ZOMBIES_HEALTH && Necromancy.isRessurectedPlayer(player)) return;
        resetMaxPlayerHealth(player);
    }

    public static void resetMaxPlayerHealth(ServerPlayer player) {
        double health = seasonConfig.MAX_PLAYER_HEALTH.get(seasonConfig);
        setMaxPlayerHealth(player, health);
    }

    public static void resetPlayerJumpHeight(ServerPlayer player) {
        setPlayerJumpHeight(player, DEFAULT_PLAYER_JUMP_HEIGHT);
    }

    public static void resetSafeFallHeight(ServerPlayer player) {
        setSafeFallHeight(player, DEFAULT_PLAYER_SAFE_FALL_HEIGHT);
    }

    public static void resetMovementSpeed(ServerPlayer player) {
        setMovementSpeed(player, DEFAULT_PLAYER_MOVEMENT_SPEED);
    }

    public static void resetStepHeight(ServerPlayer player) {
        setStepHeight(player, DEFAULT_PLAYER_STEP_HEIGHT);
    }

    /*
        Setters
     */

    public static void setMaxPlayerHealth(ServerPlayer player, double value) {
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(value);
    }

    public static void setPlayerJumpHeight(ServerPlayer player, double value) {
        Objects.requireNonNull(player.getAttribute(Attributes.JUMP_STRENGTH)).setBaseValue(value);
    }

    public static void setSafeFallHeight(ServerPlayer player, double value) {
        Objects.requireNonNull(player.getAttribute(Attributes.SAFE_FALL_DISTANCE)).setBaseValue(value);
    }

    public static void setScale(ServerPlayer player, double value) {
        Objects.requireNonNull(player.getAttribute(Attributes.SCALE)).setBaseValue(value);
    }

    public static void setJumpStrength(ServerPlayer player, double value) {
        Objects.requireNonNull(player.getAttribute(Attributes.JUMP_STRENGTH)).setBaseValue(value);
    }

    public static void setMovementSpeed(ServerPlayer player, double value) {
        if (player == null) return;
        Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(value);
    }

    public static void setStepHeight(ServerPlayer player, double value) {
        if (player == null) return;
        Objects.requireNonNull(player.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(value);
    }

    /*
        Getters
     */
    public static double getMaxPlayerHealth(ServerPlayer player) {
        return player.getAttributeBaseValue(Attributes.MAX_HEALTH);
    }

    public static double getMovementSpeed(ServerPlayer player) {
        return player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
    }

    public static double getPlayerSize(ServerPlayer player) {
        return player.getAttributeBaseValue(Attributes.SCALE);
    }
}
