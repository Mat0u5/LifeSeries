package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeConfig;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;

public class SizeShifting extends Wildcard {

    public static double MIN_SIZE_HARD = 0.06;
    public static double MAX_SIZE_HARD = 16;

    public static final ConfigRef<Double> MIN_SIZE = ConfigRef.of(() -> WildLifeConfig.get().WILDCARD_SIZESHIFTING_MIN_SIZE);
    public static final ConfigRef<Double> MAX_SIZE = ConfigRef.of(() -> WildLifeConfig.get().WILDCARD_SIZESHIFTING_MAX_SIZE);

    public static double MIN_SIZE_NERFED = 0.6;
    public static double MAX_SIZE_NERFED = 1.5;

    public static final ConfigRef<Double> SIZE_CHANGE_MULTIPLIER = ConfigRef.of(() -> WildLifeConfig.get().WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER);
    public static double SIZE_CHANGE_STEP = 0.0015;

    public static final ConfigRef<Boolean> FIX_SIZECHANGING_BUGS = ConfigRef.of(() -> WildLifeConfig.get().WILDCARD_SIZESHIFTING_FIX_BUGS);
    
    @Override
    public Wildcards getType() {
        return Wildcards.SIZE_SHIFTING;
    }

    @Override
    public void tick() {
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (WildLifeTriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) continue;
            if (player.isSpectator()) continue;
            if (player.isShiftKeyDown()) {
                //? if > 1.20.3 {
                addPlayerSize(player, -SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER.get());
                //?}
            }
        }
    }

    public static void onHoldingJump(ServerPlayer player) {
        if (WildLifeTriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) return;
        if (player.isSpectator()) return;
        if (((IPlayer) player).ls$isWatcher()) return;
        //? if > 1.20.3 {
        addPlayerSize(player, SIZE_CHANGE_STEP * SIZE_CHANGE_MULTIPLIER.get());
        //?}
    }

    //? if > 1.20.3 {
    public static double getPlayerSize(ServerPlayer player) {
        return AttributeUtils.SCALE.of(player).get();
    }

    public static void addPlayerSize(ServerPlayer player, double amount) {
        setPlayerSize(player, getPlayerSize(player)+amount);
    }

    public static void setPlayerSize(ServerPlayer player, double size) {
        if (size < MIN_SIZE_HARD) size = MIN_SIZE_HARD;
        if (size > MAX_SIZE_HARD) size = MAX_SIZE_HARD;
        if (size < MIN_SIZE.get()) size = MIN_SIZE.get();
        if (size > MAX_SIZE.get()) size = MAX_SIZE.get();

        if (Wildcard.isFinale()) {
            if (size < MIN_SIZE_NERFED) size = MIN_SIZE_NERFED;
            if (size > MAX_SIZE_NERFED) size = MAX_SIZE_NERFED;
        }


        if (MorphManager.getOrCreateComponent(player).isMorphed()) return;

        AttributeUtils.SCALE.of(player).set(size);
    }
    public static void setPlayerSizeUnchecked(ServerPlayer player, double size) {
        AttributeUtils.SCALE.of(player).set(size);
    }

    public static void resetSizesTick(boolean isActive) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            boolean isWatcher = ((IPlayer) player).ls$isWatcher();
            boolean isDeadSpectator = player.isSpectator() && ((IPlayer) player).ls$isDead();
            if (!isActive || isDeadSpectator || isWatcher) {
                double size = getPlayerSize(player);
                if (WildLifeTriviaHandler.cursedGigantificationPlayers.contains(player.getUUID()) && !isWatcher && !isDeadSpectator) continue;
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
