package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.scores.ScoreHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class LimitedLifeBoogeymanManager extends BoogeymanManager {
    @Override
    public boolean isBoogeymanThatCanBeCured(ServerPlayer player, ServerPlayer victim) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return false;
        if (boogeyman.cured) return false;
        if (boogeyman.failed) return false;
        if (victim.ls$isOnLastLife(true) && !player.ls$isOnLastLife(true)) return false;
        return true;
    }

    @Override
    public void sessionEnd() {
        if (!BOOGEYMAN_ENABLED) return;
        if (server == null) return;
        for (Boogeyman boogeyman : new ArrayList<>(boogeymen)) {
            if (boogeyman.died) continue;

            if (!boogeyman.cured && !boogeyman.failed) {
                ServerPlayer player = PlayerUtils.getPlayer(boogeyman.uuid);
                if (player == null) {
                    Integer currentLives = ScoreboardUtils.getScore(ScoreHolder.forNameOnly(boogeyman.name), LivesManager.SCOREBOARD_NAME);
                    if (currentLives == null) continue;
                    if (currentLives <= LimitedLifeLivesManager.RED_TIME) continue;

                    if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
                        PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. Their time has been dropped to the next color.", boogeyman.name));
                    }
                    ScoreboardUtils.setScore(ScoreHolder.forNameOnly(boogeyman.name), LivesManager.SCOREBOARD_NAME, LimitedLife.getNextLivesColorLives(currentLives));
                    continue;
                }
                playerFailBoogeyman(player, true);
            }
        }
    }
    @Override
    public boolean playerFailBoogeyman(ServerPlayer player, boolean sendMessage) {
        if (!BOOGEYMAN_ENABLED) return false;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return false;
        if (player.ls$isDead()) return false;

        boogeyman.cured = false;
        if (boogeyman.failed) return false;
        boogeyman.failed = true;

        boolean canChangeLives = player.ls$isAlive() && !player.ls$isOnLastLife(true);

        Integer currentLives = player.ls$getLives();
        if (currentLives == null) return false;
        Integer setToLives = LimitedLife.getNextLivesColorLives(currentLives);
        if (setToLives == null) return false;

        if (BOOGEYMAN_ADVANCED_DEATHS) {
            PlayerUtils.sendTitle(player,Component.nullToEmpty("§cThe curse consumes you.."), 20, 30, 20);
            if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been consumed by the curse.", player));
            }
            if (canChangeLives) {
                AdvancedDeathsManager.setPlayerLives(player, setToLives);
            }
        }
        else {
            if (canChangeLives) {
                player.ls$setLives(setToLives);
            }
            Component setTo = livesManager.getFormattedLives(player);

            PlayerUtils.sendTitle(player,Component.nullToEmpty("§cYou have failed."), 20, 30, 20);
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("minecraft","lastlife_boogeyman_fail")));
            if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. Their time has been dropped to {}", player, setTo));
            }
        }
        return true;
    }

    @Override
    public List<ServerPlayer> getRandomBoogeyPlayers(List<ServerPlayer> allowedPlayers, BoogeymanRollType rollType) {
        List<ServerPlayer> boogeyPlayers = super.getRandomBoogeyPlayers(allowedPlayers, rollType);
        int chooseBoogeymen = getBoogeymanAmount(rollType) - boogeyPlayers.size();
        if (chooseBoogeymen > 0) {
            List<ServerPlayer> redPlayers = livesManager.getRedPlayers();
            Collections.shuffle(redPlayers);
            for (ServerPlayer player : redPlayers) {
                // Third loop for red boogeymen if necessary
                if (chooseBoogeymen <= 0) break;
                if (isBoogeyman(player)) continue;
                if (!allowedPlayers.contains(player)) continue;
                if (rolledPlayers.contains(player.getUUID())) continue;
                if (BOOGEYMAN_IGNORE.contains(player.getScoreboardName().toLowerCase())) continue;
                if (BOOGEYMAN_FORCE.contains(player.getScoreboardName().toLowerCase())) continue;
                if (boogeyPlayers.contains(player)) continue;

                boogeyPlayers.add(player);
                chooseBoogeymen--;
            }
        }

        return boogeyPlayers;
    }

    @Override
    public List<ServerPlayer> getAllowedBoogeyPlayers() {
        List<ServerPlayer> result = new ArrayList<>();
        for (ServerPlayer player : livesManager.getAlivePlayers()) {
            if (isBoogeyman(player)) continue;
            result.add(player);
        }
        return result;
    }
}
