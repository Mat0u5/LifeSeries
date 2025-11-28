package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

public class SizeShifting extends Wildcard {

    public static double MIN_SIZE_HARD = 0.06;
    public static double MAX_SIZE_HARD = 16;

    public static double MIN_SIZE = 0.25;
    public static double MAX_SIZE = 3;

    public static double MIN_SIZE_NERFED = 0.6;
    public static double MAX_SIZE_NERFED = 1.5;

    public static double SIZE_CHANGE_MULTIPLIER = 1;
    public static double SIZE_CHANGE_STEP = 0.0015;

    public static boolean FIX_SIZECHANGING_BUGS = false;
    
    @Override
    public Wildcards getType() {
        return Wildcards.SIZE_SHIFTING;
    }

    @Override
    public void tick() {
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (TriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) continue;
            if (player.isSpectator()) continue;
            if (player.isShiftKeyDown()) {
                //? if > 1.20.3 {
                addPlayerSize(player, -SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER);
                //?}
            }
        }
    }

    public static void onHoldingJump(ServerPlayer player) {
        if (TriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) return;
        if (player.isSpectator()) return;
        if (player.ls$isWatcher()) return;
        //? if > 1.20.3 {
        addPlayerSize(player, SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER);
        //?}
    }

    //? if > 1.20.3 {
    public static double getPlayerSize(ServerPlayer player) {
        return AttributeUtils.getPlayerSize(player);
    }

    public static void addPlayerSize(ServerPlayer player, double amount) {
        setPlayerSize(player, getPlayerSize(player)+amount);
    }

    public static void setPlayerSize(ServerPlayer player, double size) {
        if (size < MIN_SIZE_HARD) size = MIN_SIZE_HARD;
        if (size > MAX_SIZE_HARD) size = MAX_SIZE_HARD;
        if (size < MIN_SIZE) size = MIN_SIZE;
        if (size > MAX_SIZE) size = MAX_SIZE;

        if (Wildcard.isFinale()) {
            if (size < MIN_SIZE_NERFED) size = MIN_SIZE_NERFED;
            if (size > MAX_SIZE_NERFED) size = MAX_SIZE_NERFED;
        }


        if (MorphManager.getOrCreateComponent(player).isMorphed()) return;

        AttributeUtils.setScale(player, size);
    }
    public static void setPlayerSizeUnchecked(ServerPlayer player, double size) {
        AttributeUtils.setScale(player, size);
    }

    public static void resetSizesTick(boolean isActive) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            boolean isWatcher = player.ls$isWatcher();
            boolean isDeadSpectator = player.isSpectator() && player.ls$isDead();
            if (!isActive || isDeadSpectator || isWatcher) {
                double size = getPlayerSize(player);
                if (TriviaHandler.cursedGigantificationPlayers.contains(player.getUUID()) && !isWatcher && !isDeadSpectator) continue;
                if (size == 1) continue;
                if (size < 0.98) {
                    addPlayerSize(player, 0.01);
                }
                else if (size > 1.02) {
                    addPlayerSize(player, -0.01);
                }
                else {
                    setPlayerSize(player, 1);
                }
            }
        }
    }
    //?}
}
