package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Locale;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.seasonConfig;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

public class LimitedLifeLivesManager extends LivesManager {
    public static int DEFAULT_TIME = 86400;
    public static int YELLOW_TIME = 57600;
    public static int RED_TIME = 28800;
    public static boolean BROADCAST_COLOR_CHANGES = false;
    public static int TIME_RANDOMIZE_INTERVAL = Time.hours(1).getSeconds();

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
        return Component.literal(Time.seconds(lives).formatLong()).withStyle(color);
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
        ScoreboardUtils.setScore(player.getScoreboardName(), LivesManager.SCOREBOARD_NAME, lives);
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

    @Override
    public void receiveLifeFromOtherPlayer(Component playerName, ServerPlayer target, boolean isRevive) {
        target.ls$playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.MASTER, 10, 1);
        Component amount = Component.literal(LimitedLife.NEW_DEATH_NORMAL.copy().multiply(-1).formatLong());

        if (seasonConfig.GIVELIFE_BROADCAST.get()) {
            PlayerUtils.broadcastMessageExcept(TextUtils.format("{} received {} from {}", target, amount, playerName), target);
        }
        target.sendSystemMessage(TextUtils.format("You received {} from {}", amount, playerName));
        PlayerUtils.sendTitleWithSubtitle(target, TextUtils.format("You received {}", amount), TextUtils.format("from {}", playerName), 10, 60, 10);
        AnimationUtils.createSpiral(target, 175);
        currentSeason.reloadPlayerTeam(target);
        SessionTranscript.givelife(playerName, target);
        if (isRevive && isAlive(target)) {
            PlayerUtils.safelyPutIntoSurvival(target);
        }
    }

    @Override
    public void addToPlayerLives(ServerPlayer player, int amount) {
        if (Math.abs(amount) >= 2 && !LIVES_SYSTEM_DISABLED) {
            sendTimeTitle(player, Time.seconds(amount), amount < 0 ? ChatFormatting.RED : ChatFormatting.GREEN);
        }
        super.addToPlayerLives(player, amount);
    }

    public void sendTimeTitle(ServerPlayer player, Time time, ChatFormatting style) {
        sendTimeTitle(player, Component.literal(time.formatReadable()).withStyle(style));
    }

    public void sendTimeTitle(ServerPlayer player, Component text) {
        PlayerUtils.sendTitle(player, text, 20, 80, 20);
    }

    @Override
    public void reload() {
        super.reload();
        TIME_RANDOMIZE_INTERVAL = LimitedLifeConfig.TIME_RANDOMIZE_INTERVAL.get();

    }

    @Override
    public int getRandomLife() {
        int minLives = ROLL_MIN_LIVES;
        int maxLives = ROLL_MAX_LIVES;
        int interval = TIME_RANDOMIZE_INTERVAL;

        int numIntervals = (maxLives - minLives) / interval;

        int randomInterval = rnd.nextInt(numIntervals + 1);

        return minLives + (interval * randomInterval);
    }
}
