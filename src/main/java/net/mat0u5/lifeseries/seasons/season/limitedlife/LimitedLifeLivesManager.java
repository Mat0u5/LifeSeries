package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.LifeSkinsManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;

import static net.mat0u5.lifeseries.LifeSeries.*;
import static net.mat0u5.lifeseries.seasons.util.WatcherManager.isWatcher;

//? if >= 26.2 {
import net.minecraft.world.scores.TeamColor;
 //?}

public class LimitedLifeLivesManager extends LivesManager {
    public static int DEFAULT_TIME = 86400;
    public static int YELLOW_TIME = 57600;
    public static int RED_TIME = 28800;
    public static boolean BROADCAST_COLOR_CHANGES = false;
    public static int TIME_RANDOMIZE_INTERVAL = Time.hours(1).getSeconds();
    public static int CUSTOM_AVERAGE_TIME = Time.hours(24).getSeconds();
    public static boolean PAUSE_SESSION_TIME_UNTIL_ROLL = false;

    //~ if >= 26.2 'ChatFormatting' -> 'TeamColor' {
    @Override
    public Component getFormattedLives(ServerPlayer player) {
        Integer lives = getPlayerLives(player);
        if (lives == null) return Component.empty();
        TeamColor color = getColorForLives(player);
        //~ if >= 26.2 '.withStyle(color)' -> '.withColor(color.textColor())' {
        return Component.literal(Time.seconds(lives).formatLong()).withColor(color.textColor());
        //~}
    }


    @Override
    public Component getFormattedLives(Integer lives) {
        if (lives == null) return Component.empty();
        TeamColor color = getColorForLives(lives);
        //~ if >= 26.2 '.withStyle(color)' -> '.withColor(color.textColor())' {
        return Component.literal(Time.seconds(lives).formatLong()).withColor(color.textColor());
        //~}
    }

    @Override
    public String getTeamForLives(Integer lives) {
        return super.getTeamForLives(getEquivalentLives(lives));
    }

    @Override
    public void setPlayerLives(ServerPlayer player, int lives, boolean ignoreFinalDeath) {
        if (isWatcher(player)) return;
        Integer livesBefore = getPlayerLives(player);
        boolean livesChanged = !Objects.equals(lives, livesBefore);
        TeamColor colorBefore = null;
        if (player.getTeam() != null) {
            //? if <= 26.1 {
            /*colorBefore = player.getTeam().getColor();
            *///?} else {
            colorBefore = player.getTeam().getColor().orElse(null);
            //?}
        }
        SessionTranscript.addRecordIfMissing(player);
        ScoreboardUtils.setScore(player.getScoreboardName(), LivesManager.SCOREBOARD_NAME, lives);
        if (lives <= 0 && !ignoreFinalDeath) {
            playerLostAllLives(player, livesBefore);
        }
        TeamColor colorNow = getColorForLives(lives);
        if (colorBefore != colorNow) {
            if (player.isSpectator() && lives > 0) {
                PlayerUtils.safelyPutIntoSurvival(player);
            }
            if (lives > 0 && colorBefore != null && livesBefore != null && BROADCAST_COLOR_CHANGES) {
                //? if <= 26.1 {
                /*Component colorText = Component.literal(colorNow.getName().replaceAll("_", " ").toLowerCase(Locale.ROOT)).withStyle(colorNow);
                *///?} else {
                Component colorText = Component.literal(colorNow.getSerializedName().replaceAll("_", " ").toLowerCase(Locale.ROOT)).withColor(colorNow.textColor());
                //?}
                PlayerUtils.broadcastMessage(ModifiableText.LIMITEDLIFE_CHANGE_COLOR.get(player, colorText));
            }
        }
        currentSeason.reloadPlayerTeam(player);
        if (livesChanged) {
            LifeSkinsManager.refreshLifeSkin(player);
        }
    }
    //~}

    @Override
    public Boolean isOnSpecificLives(ServerPlayer player, int check) {
        if (isDead(player)) return null;
        Integer lives = getEquivalentLives(getPlayerLives(player));
        if (lives == null) return null;
        return lives == check;
    }

    public static Integer getEquivalentLives(Integer limitedLifeLives) {
        if (limitedLifeLives == null) return null;
        if (limitedLifeLives <= 0) return 0;
        if (limitedLifeLives <= RED_TIME) return 1;
        if (limitedLifeLives <= YELLOW_TIME) return 2;
        if (limitedLifeLives <= DEFAULT_TIME) return 3;
        return limitedLifeLives;
    }

    @Override
    public void receiveLifeFromOtherPlayer(Component playerName, ServerPlayer target, boolean isRevive) {
        ModifiableSound.LIVES_RECEIVE.play(target, 10, 1);
        Component amount = Component.literal(LimitedLife.NEW_DEATH_NORMAL.copy().multiply(-1).formatLong());

        if (seasonConfig.GIVELIFE_BROADCAST.get()) {
            PlayerUtils.broadcastMessageExcept(ModifiableText.GIVELIFE_RECEIVE_OTHER.get(target, amount, playerName), target);
        }
        ((IPlayer) target).ls$message(ModifiableText.GIVELIFE_RECEIVE_SELF.get(amount, playerName));
        PlayerUtils.sendTitleWithSubtitle(target, ModifiableText.GIVELIFE_RECEIVE_SELF_TITLE.get(amount), ModifiableText.GIVELIFE_RECEIVE_SELF_TITLE_SUBTITLE.get(playerName), 10, 60, 10);


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
            // Ignore final deaths
            Integer currentLives = ((IPlayer)player).ls$getLives();
            boolean finalDeath = false;
            if (currentLives != null && currentLives > 0 && (currentLives+amount) <=0) {
                finalDeath = true;
            }
            if (!finalDeath) {
                sendTimeTitle(player, Time.seconds(amount), amount < 0 ? ChatFormatting.RED : ChatFormatting.GREEN);
            }
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
        TIME_RANDOMIZE_INTERVAL = Math.max(1, LimitedLifeConfig.TIME_RANDOMIZE_INTERVAL.get());
        CUSTOM_AVERAGE_TIME = LimitedLifeConfig.TIME_RANDOMIZE_AVERAGE.get();
        PAUSE_SESSION_TIME_UNTIL_ROLL = LimitedLifeConfig.PAUSE_SESSION_TIME_UNTIL_ROLL.get();
    }

    @Override
    public Map<ServerPlayer, Integer> getFinalRandomLives(List<ServerPlayer> players) {
        if (!CUSTOM_AVERAGE_ENABLED) {
            return super.getFinalRandomLives(players);
        }

        Map<ServerPlayer, Integer> lives = new HashMap<>();
        int totalSize = players.size();
        int interval = Math.max(1, TIME_RANDOMIZE_INTERVAL);

        double targetTotal = totalSize * CUSTOM_AVERAGE_TIME;
        int minTotal = totalSize * ROLL_MIN_LIVES;
        int maxTotal = totalSize * ROLL_MAX_LIVES;
        targetTotal = Math.max(minTotal, Math.min(targetTotal, maxTotal));

        for (ServerPlayer player : players) {
            lives.put(player, ROLL_MIN_LIVES);
        }

        int remaining = (int) (targetTotal - minTotal);
        List<ServerPlayer> playerList = new ArrayList<>(players);

        while (remaining >= interval && !playerList.isEmpty()) {
            ServerPlayer player = playerList.get(rnd.nextInt(playerList.size()));
            int current = lives.get(player);
            if (current + interval <= ROLL_MAX_LIVES) {
                lives.put(player, current + interval);
                remaining -= interval;
            }
            else {
                playerList.remove(player);
            }
        }
        return lives;
    }

    @Override
    public int getRandomLife() {
        int minLives = ROLL_MIN_LIVES;
        int maxLives = ROLL_MAX_LIVES;
        int interval = Math.max(1, TIME_RANDOMIZE_INTERVAL);

        int numIntervals = (maxLives - minLives) / interval;

        int randomInterval = rnd.nextInt(numIntervals + 1);

        return minLives + (interval * randomInterval);
    }

    @Override
    public void rollLivesFinished() {
        super.rollLivesFinished();
        if (PAUSE_SESSION_TIME_UNTIL_ROLL) currentSession.resetPassedTime();
    }
}
