package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Collection;

//? if <= 1.20.2
//import net.minecraft.world.scores.Score;
//? if > 1.20.2
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Team;

import static net.mat0u5.lifeseries.Main.*;

public class LimitedLife extends Season {

    private boolean SHOW_DEATH_TITLE = true;

    public static Time NEW_DEATH_NORMAL = Time.hours(-1);
    private static Time NEW_DEATH_BOOGEYMAN = Time.hours(-2);
    private static Time NEW_KILL_NORMAL = Time.minutes(30);
    private static Time NEW_KILL_BOOGEYMAN = Time.hours(1);
    public static boolean TICK_OFFLINE_PLAYERS = false;
    public static boolean SHOW_TIME_BELOW_NAME = false;

    @Override
    public Seasons getSeason() {
        return Seasons.LIMITED_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LimitedLifeConfig();
    }

    @Override
    public BoogeymanManager createBoogeymanManager() {
        return new LimitedLifeBoogeymanManager();
    }

    @Override
    public SecretSociety createSecretSociety() {
        return new LimitedLifeSecretSociety();
    }

    @Override
    public LivesManager createLivesManager() {
        return new LimitedLifeLivesManager();
    }

    public void displayTimers(MinecraftServer server) {
        String message = "";
        if (currentSession.statusNotStarted()) {
            message = "Session has not started";
        }
        else if (currentSession.statusStarted()) {
            message = currentSession.getRemainingTimeStr();
        }
        else if (currentSession.statusPaused()) {
            message = "Session has been paused";
        }
        else if (currentSession.statusFinished()) {
            message = "Session has ended";
        }

        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {

            if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                long timestamp = SessionTimerStates.OFF.getValue();
                if (currentSession.statusNotStarted()) timestamp = SessionTimerStates.NOT_STARTED.getValue();
                else if (currentSession.statusPaused()) timestamp = SessionTimerStates.PAUSED.getValue();
                else if (currentSession.statusFinished()) timestamp = SessionTimerStates.ENDED.getValue();
                else if (currentSession.validTime()) {
                    Time remainingTime = currentSession.getRemainingTime();
                    timestamp = Time.now().add(remainingTime).getMillis();
                }
                if (timestamp != SessionTimerStates.OFF.getValue()) {
                    NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, timestamp);
                }

                if (player.ls$hasAssignedLives() && player.ls$getLives() != null) {
                    long playerLives;
                    if (player.ls$isAlive()) {
                        Integer playerLivesInt = player.ls$getLives();
                        playerLives = playerLivesInt == null ? -1 : playerLivesInt;
                    }
                    else {
                        playerLives = -1;
                    }
                    String livesColor = livesManager.getColorForLives(player).toString();
                    NetworkHandlerServer.sendLongPacket(player, PacketNames.fromName(PacketNames.LIMITED_LIFE_TIMER.getName()+livesColor), playerLives);
                }
            }
            else {
                MutableComponent fullMessage = Component.empty();
                if (currentSession.displayTimer.contains(player.getUUID())) {
                    fullMessage.append(Component.literal(message).withStyle(ChatFormatting.GRAY));
                }
                if (player.ls$hasAssignedLives()) {
                    if (!fullMessage.getString().isEmpty()) fullMessage.append(Component.nullToEmpty("  |  "));
                    fullMessage.append(livesManager.getFormattedLives(player));
                }
                player.displayClientMessage(fullMessage, true);
            }
        }
    }

    private int secondCounter = 0;
    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        if (!currentSession.statusStarted()) return;

        secondCounter--;
        if (secondCounter <= 0) {
            secondCounter = 20;
            livesManager.getAlivePlayers().forEach(ServerPlayer::ls$removeLife);

            if (TICK_OFFLINE_PLAYERS) {
                //? if <= 1.20.2 {
                /*Collection<Score> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
                for (Score entry : entries) {
                    if (entry.getScore() <= 0) continue;
                    if (PlayerUtils.getPlayer(entry.getOwner()) != null) continue;
                    ScoreboardUtils.setScore(entry.getOwner(), LivesManager.SCOREBOARD_NAME, entry.getScore() - 1);
                }
                *///?} else {
                Collection<PlayerScoreEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
                for (PlayerScoreEntry entry : entries) {
                    if (entry.value() <= 0) continue;
                    if (PlayerUtils.getPlayer(entry.owner()) != null) continue;
                    ScoreboardUtils.setScore(entry.owner(), LivesManager.SCOREBOARD_NAME, entry.value() - 1);
                }
                //?}
            }
        }
    }

    @Override
    public void onPlayerDeath(ServerPlayer player, DamageSource source) {
        SessionTranscript.onPlayerDeath(player, source);
        if (source != null) {
            if (source.getEntity() instanceof ServerPlayer serverAttacker) {
                if (player != source.getEntity()) {
                    onPlayerKilledByPlayer(player, serverAttacker);
                    return;
                }
            }
        }
        if (player.getKillCredit() != null) {
            if (player.getKillCredit() instanceof ServerPlayer serverAdversary) {
                if (player != player.getKillCredit()) {
                    onPlayerKilledByPlayer(player, serverAdversary);
                    return;
                }
            }
        }
        onPlayerDiedNaturally(player, source);
        DatapackIntegration.EVENT_PLAYER_DEATH.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        if (!DatapackIntegration.EVENT_PLAYER_DEATH.isCanceled() && livesManager.canChangeLivesNaturally(player)) {
            player.ls$addLives(NEW_DEATH_NORMAL.getSeconds());
        }
    }

    @Override
    public void onClaimKill(ServerPlayer killer, ServerPlayer victim) {
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onClaimKill(killer, victim);
        boolean cancelGain = DatapackIntegration.EVENT_CLAIM_KILL.isCanceled();
        boolean cancelPunishment = DatapackIntegration.EVENT_PLAYER_DEATH.isCanceled();
        if (cancelGain && cancelPunishment) return;

        if (wasBoogeyCure && livesManager.canChangeLivesNaturally()) {
            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
            boolean wasAlive = victim.ls$isAlive();
            if (wasAlive && !cancelPunishment) {
                victim.ls$addLives(NEW_DEATH_BOOGEYMAN.diff(NEW_DEATH_NORMAL).getSeconds());
            }
            if (!cancelGain) killer.ls$addLives(NEW_KILL_BOOGEYMAN.getSeconds());
        }
    }

    @Override
    public void tryClaimKillLifeGain(ServerPlayer killer, ServerPlayer victim) {
        Team team = killer.getTeam();
        if (team != null) {
            Integer canGainLife = livesManager.getTeamGainLives(team.getName());
            Integer victimLives = victim.ls$getLives();
            int amount = NEW_KILL_NORMAL.getSeconds();
            if (canGainLife != null && victimLives != null && victimLives > 0) {
                if (victimLives + amount >= canGainLife) { // +amount because the victim already lost time
                    broadcastLifeGain(killer, victim);
                    killer.ls$addLives(amount);
                }
            }
        }
    }

    @Override
    public void tryKillLifeGain(ServerPlayer killer, ServerPlayer victim) {
        Team team = killer.getTeam();
        if (team != null) {
            Integer canGainLife = livesManager.getTeamGainLives(team.getName());
            if (canGainLife != null && victim.ls$isOnAtLeastLives(canGainLife, false)) {
                broadcastLifeGain(killer, victim);
                killer.ls$addLives(NEW_KILL_NORMAL.getSeconds());
            }
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onPlayerKilledByPlayer(victim, killer);
        if (DatapackIntegration.EVENT_PLAYER_PVP_KILLED.isCanceled()) return;

        if (!wasBoogeyCure && livesManager.canChangeLivesNaturally()) {
            victim.ls$addLives(NEW_DEATH_NORMAL.getSeconds());
        }
        else if (livesManager.canChangeLivesNaturally()) {
            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
            victim.ls$addLives(NEW_DEATH_BOOGEYMAN.getSeconds());
            livesManager.addToLivesNoUpdate(killer, NEW_KILL_BOOGEYMAN.getSeconds());
            currentSeason.reloadPlayerTeam(killer);
        }
    }

    @Override
    public void reload() {
        SHOW_TIME_BELOW_NAME = LimitedLifeConfig.SHOW_TIME_BELOW_NAME.get(seasonConfig);
        super.reload();
        LimitedLifeLivesManager.DEFAULT_TIME = LimitedLifeConfig.TIME_DEFAULT.get(seasonConfig);
        LimitedLifeLivesManager.YELLOW_TIME = LimitedLifeConfig.TIME_YELLOW.get(seasonConfig);
        LimitedLifeLivesManager.RED_TIME = LimitedLifeConfig.TIME_RED.get(seasonConfig);
        NEW_DEATH_NORMAL = Time.seconds(LimitedLifeConfig.TIME_DEATH.get(seasonConfig));
        NEW_DEATH_BOOGEYMAN = Time.seconds(LimitedLifeConfig.TIME_DEATH_BOOGEYMAN.get(seasonConfig));
        NEW_KILL_NORMAL = Time.seconds(LimitedLifeConfig.TIME_KILL.get(seasonConfig));
        NEW_KILL_BOOGEYMAN = Time.seconds(LimitedLifeConfig.TIME_KILL_BOOGEYMAN.get(seasonConfig));
        TICK_OFFLINE_PLAYERS = LimitedLifeConfig.TICK_OFFLINE_PLAYERS.get(seasonConfig);
        LimitedLifeLivesManager.BROADCAST_COLOR_CHANGES = LimitedLifeConfig.BROADCAST_COLOR_CHANGES.get(seasonConfig);
    }

    @Override
    public Integer getDefaultLives() {
        if (livesManager.ROLL_LIVES) {
            return null;
        }
        return LimitedLifeLivesManager.DEFAULT_TIME;
    }

    public static Integer getNextLivesColorLives(Integer currentLives) {
        if (currentLives == null) return null;

        if (currentLives > LimitedLifeLivesManager.DEFAULT_TIME) return LimitedLifeLivesManager.DEFAULT_TIME;
        else if (currentLives > LimitedLifeLivesManager.YELLOW_TIME) return LimitedLifeLivesManager.YELLOW_TIME;
        else if (currentLives > LimitedLifeLivesManager.RED_TIME) return LimitedLifeLivesManager.RED_TIME;
        return 0;
    }
}
