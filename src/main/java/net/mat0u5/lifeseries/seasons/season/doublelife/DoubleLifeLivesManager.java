package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class DoubleLifeLivesManager extends LivesManager {

    @Override
    public void resetPlayerLife(ServerPlayer player) {
        super.resetPlayerLife(player);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(player);
        }
    }


    @Override
    public void receiveLifeFromOtherPlayer(Component playerName, ServerPlayer target, boolean isRevive) {
        super.receiveLifeFromOtherPlayer(playerName, target, isRevive);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(target);
        }
    }

    @Override
    public Map<ServerPlayer, Integer> getFinalRandomLives(List<ServerPlayer> players) {
        if (!(currentSeason instanceof DoubleLife doubleLife) || !doubleLife.SOULBOUND_LIVES) return super.getFinalRandomLives(players);

        Map<UUID, Integer> livesUUID = new HashMap<>();
        Map<ServerPlayer, Integer> lives = new HashMap<>();
        for (ServerPlayer player : players) {
            int randomLives = getRandomLife();

            ServerPlayer soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null) {
                if (soulmate.ls$hasAssignedLives()) {
                    randomLives = soulmate.ls$getLives();
                }
                if (livesUUID.containsKey(soulmate.getUUID())) {
                    randomLives = livesUUID.get(soulmate.getUUID());
                }
            }

            livesUUID.put(player.getUUID(), randomLives);
            lives.put(player, randomLives);
        }
        return lives;
    }

    @Override
    public void showDeathTitle(ServerPlayer player) {
        if (currentSeason instanceof DoubleLife doubleLife && doubleLife.isSoulmateOnline(player)) {
            ServerPlayer soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null && doubleLife.SOULBOUND_LIVES) {
                if (doubleLife.isMainSoulmate(player)) {
                    if (SHOW_DEATH_TITLE) {
                        PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.DOUBLELIFE_FINAL_DEATH_TITLE.get(player, soulmate), ModifiableText.DOUBLELIFE_FINAL_DEATH_TITLE_SUBTITLE.get(), 20, 80, 20);
                    }
                    Component deathMessage = ModifiableText.DOUBLELIFE_FINAL_DEATH.get(player, soulmate);
                    if (!deathMessage.getString().isEmpty()) {
                        if (SHOW_LIFE_DIFF) {
                            TaskScheduler.schedulePriorityTask(1, () -> {
                                PlayerUtils.broadcastMessage(deathMessage);
                            });
                        }
                        else {
                            PlayerUtils.broadcastMessage(deathMessage);
                        }
                    }
                }
                return;
            }
        }


        super.showDeathTitle(player);
    }
}
