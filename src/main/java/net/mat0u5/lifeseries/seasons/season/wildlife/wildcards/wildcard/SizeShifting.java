package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class SizeShifting extends Wildcard {

    public static double MIN_SIZE_HARD = 0.06;
    public static double MAX_SIZE_HARD = 16;

    public static double MIN_SIZE = 0.25;
    public static double MAX_SIZE = 3;

    public static double MIN_SIZE_NERFED = 0.6;
    public static double MAX_SIZE_NERFED = 1.5;

    public static double SIZE_CHANGE_MULTIPLIER = 1;
    public static double SIZE_CHANGE_STEP = 0.0015;

    //public static boolean SAVE_FROM_FALLING = true;
    
    @Override
    public Wildcards getType() {
        return Wildcards.SIZE_SHIFTING;
    }

    @Override
    public void tick() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (TriviaBot.cursedGigantificationPlayers.contains(player.getUuid())) continue;
            if (player.isSpectator()) continue;
            if (player.isSneaking()) {
                addPlayerSize(player, -SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER);
            }
        }
    }

    public static void onHoldingJump(ServerPlayerEntity player) {
        if (TriviaBot.cursedGigantificationPlayers.contains(player.getUuid())) return;
        addPlayerSize(player, SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER);
    }

    public static double getPlayerSize(ServerPlayerEntity player) {
        return AttributeUtils.getPlayerSize(player);
    }

    public static void addPlayerSize(ServerPlayerEntity player, double amount) {
        setPlayerSize(player, getPlayerSize(player)+amount);
    }

    public static void setPlayerSize(ServerPlayerEntity player, double size) {
        if (size < MIN_SIZE_HARD) size = MIN_SIZE_HARD;
        if (size > MAX_SIZE_HARD) size = MAX_SIZE_HARD;
        if (size < MIN_SIZE) size = MIN_SIZE;
        if (size > MAX_SIZE) size = MAX_SIZE;

        if (WildcardManager.isActiveWildcard(Wildcards.CALLBACK)) {
            if (size < MIN_SIZE_NERFED) size = MIN_SIZE_NERFED;
            if (size > MAX_SIZE_NERFED) size = MAX_SIZE_NERFED;
        }


        if (MorphManager.getOrCreateComponent(player).isMorphed()) return;
        /*
        if (saveFromFalling) {
            Has to be done client-side :/
        }
        */

        AttributeUtils.setScale(player, size);
    }
    public static void setPlayerSizeUnchecked(ServerPlayerEntity player, double size) {
        AttributeUtils.setScale(player, size);
    }

    public static void resetSizesTick(boolean isActive) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (!isActive || (player.isSpectator() && !currentSeason.isAlive(player))) {
                double size = getPlayerSize(player);
                if (TriviaBot.cursedGigantificationPlayers.contains(player.getUuid())) continue;
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
}
