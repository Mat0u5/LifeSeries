package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

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
}
