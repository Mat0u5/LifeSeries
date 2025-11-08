package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.ScoreHolder;

import java.util.Locale;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

public class LimitedLifeLivesManager extends LivesManager {
    public static int DEFAULT_TIME = 86400;
    public static int YELLOW_TIME = 57600;
    public static int RED_TIME = 28800;
    public static boolean BROADCAST_COLOR_CHANGES = false;

    @Override
    public ChatFormatting getColorForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return ChatFormatting.GRAY;
        if (lives == 1) return ChatFormatting.RED;
        if (lives == 2) return ChatFormatting.YELLOW;
        if (lives == 3) return ChatFormatting.GREEN;
        if (lives >= 4) return ChatFormatting.DARK_GREEN;
        return ChatFormatting.DARK_GRAY;
    }

    @Override
    public Component getFormattedLives(Integer lives) {
        if (lives == null) return Component.empty();
        ChatFormatting color = getColorForLives(lives);
        return Component.literal(OtherUtils.formatTime(lives*20)).withStyle(color);
    }

    @Override
    public String getTeamForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return "lives_null";
        if (lives == 1) return "lives_1";
        if (lives == 2) return "lives_2";
        if (lives == 3) return "lives_3";
        if (lives >= 4) return "lives_4";
        return "lives_0";
    }

    @Override
    public void setPlayerLives(ServerPlayer player, int lives) {
        if (isWatcher(player)) return;
        Integer livesBefore = getPlayerLives(player);
        ChatFormatting colorBefore = null;
        if (player.getTeam() != null) {
            colorBefore = player.getTeam().getColor();
        }
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly(player.getScoreboardName()), LivesManager.SCOREBOARD_NAME, lives);
        if (lives <= 0) {
            playerLostAllLives(player, livesBefore);
        }
        ChatFormatting colorNow = getColorForLives(lives);
        if (colorBefore != colorNow) {
            if (player.isSpectator() && lives > 0) {
                PlayerUtils.safelyPutIntoSurvival(player);
            }
            if (lives > 0 && colorBefore != null && livesBefore != null && BROADCAST_COLOR_CHANGES) {
                Component livesText = TextUtils.format("{} name", colorNow.getName().replaceAll("_", " ").toLowerCase(Locale.ROOT)).withStyle(colorNow);
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 is now a {}§7.", player, livesText));
            }
        }
        currentSeason.reloadPlayerTeam(player);
    }

    @Override
    public Boolean isOnSpecificLives(ServerPlayer player, int check) {
        if (isDead(player)) return null;
        Integer lives = getEquivalentLives(getPlayerLives(player));
        if (lives == null) return null;
        return lives == check;
    }

    public Integer getEquivalentLives(Integer limitedLifeLives) {
        if (limitedLifeLives == null) return null;
        if (limitedLifeLives <= 0) return 0;
        if (limitedLifeLives <= RED_TIME) return 1;
        if (limitedLifeLives <= YELLOW_TIME) return 2;
        if (limitedLifeLives <= DEFAULT_TIME) return 3;
        return 4;
    }
}
