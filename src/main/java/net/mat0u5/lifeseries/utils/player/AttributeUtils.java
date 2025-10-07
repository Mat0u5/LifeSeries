package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class AttributeUtils {
    public static final double DEFAULT_PLAYER_JUMP_HEIGHT = 0.41999998688697815;
    public static final double DEFAULT_PLAYER_SAFE_FALL_HEIGHT = 3.0;
    public static final double DEFAULT_PLAYER_MOVEMENT_SPEED = 0.10000000149011612;
    public static final double DEFAULT_PLAYER_STEP_HEIGHT = 0.6;


    public static void resetAttributesOnPlayerJoin(ServerPlayerEntity player) {
        resetMaxPlayerHealthIfNecessary(player);
        if (!TriviaBot.cursedMoonJumpPlayers.contains(player.getUuid())) {
            resetPlayerJumpHeight(player);
        }
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
            resetSafeFallHeight(player);
        }
        resetMovementSpeed(player);
        resetStepHeight(player);
    }

    public static void resetMaxPlayerHealthIfNecessary(ServerPlayerEntity player) {
        if (currentSeason.getSeason() == Seasons.SECRET_LIFE) return;
        double currentMaxHealth = getMaxPlayerHealth(player);
        if (currentMaxHealth == 13 && TriviaBot.cursedHeartPlayers.contains(player.getUuid())) return;
        if (currentMaxHealth == 8 && Necromancy.isRessurectedPlayer(player)) return;
        resetMaxPlayerHealth(player);
    }

    public static void resetMaxPlayerHealth(ServerPlayerEntity player) {
        double health = seasonConfig.MAX_PLAYER_HEALTH.get(seasonConfig);
        setMaxPlayerHealth(player, health);
    }

    public static void resetPlayerJumpHeight(ServerPlayerEntity player) {
        setPlayerJumpHeight(player, DEFAULT_PLAYER_JUMP_HEIGHT);
    }

    public static void resetSafeFallHeight(ServerPlayerEntity player) {
        setSafeFallHeight(player, DEFAULT_PLAYER_SAFE_FALL_HEIGHT);
    }

    public static void resetMovementSpeed(ServerPlayerEntity player) {
        setMovementSpeed(player, DEFAULT_PLAYER_MOVEMENT_SPEED);
    }

    public static void resetStepHeight(ServerPlayerEntity player) {
        setStepHeight(player, DEFAULT_PLAYER_STEP_HEIGHT);
    }

    /*
        Setters
     */

    public static void setMaxPlayerHealth(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(value);*/
    }

    public static void setPlayerJumpHeight(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);*/
    }

    public static void setSafeFallHeight(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SAFE_FALL_DISTANCE)).setBaseValue(value);*/
    }

    public static void setScale(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_SCALE)).setBaseValue(value);
        //?} else {
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.SCALE)).setBaseValue(value);
         *///?}
    }

    public static void setJumpStrength(ServerPlayerEntity player, double value) {
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH)).setBaseValue(value);*/
    }

    public static void setMovementSpeed(ServerPlayerEntity player, double value) {
        if (player == null) return;
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(value);*/
    }

    public static void setStepHeight(ServerPlayerEntity player, double value) {
        if (player == null) return;
        //? if <=1.21 {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT)).setBaseValue(value);
        //?} else
        /*Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.STEP_HEIGHT)).setBaseValue(value);*/
    }

    /*
        Getters
     */
    public static double getMaxPlayerHealth(ServerPlayerEntity player) {
        //? if <=1.21 {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH);
        //?} else
        /*return player.getAttributeBaseValue(EntityAttributes.MAX_HEALTH);*/
    }

    public static double getMovementSpeed(ServerPlayerEntity player) {
        //? if <=1.21 {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        //?} else
        /*return player.getAttributeBaseValue(EntityAttributes.MOVEMENT_SPEED);*/
    }

    public static double getPlayerSize(ServerPlayerEntity player) {
        //? if <=1.21 {
        return player.getAttributeBaseValue(EntityAttributes.GENERIC_SCALE);
        //?} else
        /*return player.getAttributeBaseValue(EntityAttributes.SCALE);*/
    }
}
