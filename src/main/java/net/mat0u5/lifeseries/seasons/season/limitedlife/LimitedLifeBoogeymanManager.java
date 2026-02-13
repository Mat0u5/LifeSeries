package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.boogeyman.Boogeyman;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class LimitedLifeBoogeymanManager extends BoogeymanManager {

    @Override
    public void sessionEnd() {
        if (!BOOGEYMAN_ENABLED) return;
        if (server == null) return;
        for (Boogeyman boogeyman : new ArrayList<>(boogeymen)) {
            if (boogeyman.died) continue;

            if (!boogeyman.cured && !boogeyman.failed) {
                ServerPlayer player = PlayerUtils.getPlayer(boogeyman.uuid);
                if (player == null) {
                    Integer currentLives = ScoreboardUtils.getScore(boogeyman.name, LivesManager.SCOREBOARD_NAME);
                    if (currentLives == null) continue;
                    if (currentLives <= LimitedLifeLivesManager.RED_TIME) continue;
                    Integer setLives = LimitedLife.getNextLivesColorLives(currentLives);
                    if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
                        PlayerUtils.broadcastMessage(ModifiableText.BOOGEYMAN_FAIL.get(boogeyman.name, livesManager.getFormattedLives(setLives)));
                    }
                    ScoreboardUtils.setScore(boogeyman.name, LivesManager.SCOREBOARD_NAME, setLives);
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

        player.removeTag("boogeyman_cured");
        player.addTag("boogeyman_failed");
        boogeyman.cured = false;
        if (boogeyman.failed) return false;
        boogeyman.failed = true;

        boolean canChangeLives = player.ls$isAlive() && !player.ls$isOnLastLife(true);

        Integer currentLives = player.ls$getLives();
        if (currentLives == null) return false;
        Integer setToLives = LimitedLife.getNextLivesColorLives(currentLives);
        if (setToLives == null) return false;

        DatapackIntegration.EVENT_BOOGEYMAN_FAIL_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        if (!DatapackIntegration.EVENT_BOOGEYMAN_FAIL_REWARD.isCanceled()) {
            if (BOOGEYMAN_ADVANCED_DEATHS) {

                PlayerUtils.sendTitle(player, ModifiableText.BOOGEYMAN_FAIL_ADVANCEDDEATH_NOTIFY_TITLE.get(), 20, 30, 20);
                if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                    PlayerUtils.broadcastMessage(ModifiableText.BOOGEYMAN_FAIL_ADVANCEDDEATH.get(player));
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

                PlayerUtils.sendTitle(player, ModifiableText.BOOGEYMAN_FAIL_NOTIFY_TITLE.get(), 20, 30, 20);
                PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_fail")));
                if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                    PlayerUtils.broadcastMessage(ModifiableText.BOOGEYMAN_FAIL.get(player, setTo));
                }
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
                if (BOOGEYMAN_IGNORE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
                if (BOOGEYMAN_FORCE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
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
