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
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.ScoreHolder;

import java.util.Collection;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LimitedLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /boogeyman";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";

    private boolean SHOW_DEATH_TITLE = true;
    public static int DEATH_NORMAL = -3600;
    private int DEATH_BOOGEYMAN = -7200;
    private int KILL_NORMAL = 1800;
    private int KILL_BOOGEYMAN = 3600;
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

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
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
                else if (currentSession.sessionLength != null) {
                    long remainingMillis = (currentSession.sessionLength - (int) currentSession.passedTime) * 50;
                    timestamp = System.currentTimeMillis() + remainingMillis;
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
                Collection<PlayerScoreEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
                for (PlayerScoreEntry entry : entries) {
                    if (entry.value() <= 0) continue;
                    if (PlayerUtils.getPlayer(entry.owner()) != null) continue;
                    ScoreboardUtils.setScore(ScoreHolder.forNameOnly(entry.owner()), LivesManager.SCOREBOARD_NAME, entry.value() - 1);
                }
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
        if (livesManager.canChangeLivesNaturally(player)) {
            player.ls$addLives(DEATH_NORMAL);
            if (player.ls$isAlive()) {
                sendTimeTitle(player, DEATH_NORMAL, ChatFormatting.RED);
            }
        }
    }

    public void sendTimeTitle(ServerPlayer player, int timeSeconds, ChatFormatting style) {
        sendTimeTitle(player, Component.literal(OtherUtils.formatSecondsToReadable(timeSeconds)).withStyle(style));
    }
    public void sendTimeTitle(ServerPlayer player, Component text) {
        PlayerUtils.sendTitle(player, text, 20, 80, 20);
    }

    @Override
    public void onClaimKill(ServerPlayer killer, ServerPlayer victim) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onClaimKill(killer, victim);

        if (!wasBoogeyCure) {
            if (wasAllowedToAttack && livesManager.canChangeLivesNaturally()) {
                killer.ls$addLives(KILL_NORMAL);
                sendTimeTitle(killer, KILL_NORMAL, ChatFormatting.GREEN);
            }
        }
        else if (livesManager.canChangeLivesNaturally()) {
            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey

            boolean wasAlive = victim.ls$isAlive();
            if (wasAlive) {
                victim.ls$addLives(DEATH_BOOGEYMAN-DEATH_NORMAL);
            }
            killer.ls$addLives(KILL_BOOGEYMAN);
            boolean isAlive = victim.ls$isAlive();

            if (isAlive) {
                sendTimeTitle(killer, KILL_BOOGEYMAN, ChatFormatting.GREEN);
                sendTimeTitle(victim, (DEATH_BOOGEYMAN-DEATH_NORMAL), ChatFormatting.RED);
            }
            else {
                //Is dead right now
                if (wasAlive && SHOW_DEATH_TITLE) {
                    String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);
                    PlayerUtils.sendTitleWithSubtitle(killer,
                            Component.literal(msgKiller).withStyle(ChatFormatting.GREEN),
                            livesManager.getDeathMessage(victim),
                            20, 80, 20);
                }
                else {
                    sendTimeTitle(killer, KILL_BOOGEYMAN, ChatFormatting.GREEN);
                }
            }
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onPlayerKilledByPlayer(victim, killer);

        if (!wasBoogeyCure && livesManager.canChangeLivesNaturally()) {
            Component victimDeathMessage = livesManager.getDeathMessage(victim);

            boolean wasAlive = victim.ls$isAlive();
            victim.ls$addLives(DEATH_NORMAL);
            boolean isAlive = victim.ls$isAlive();

            if (wasAllowedToAttack) {
                killer.ls$addLives(KILL_NORMAL);
                if ((wasAlive && !isAlive) && SHOW_DEATH_TITLE) {
                    PlayerUtils.sendTitleWithSubtitle(killer,
                            Component.literal(OtherUtils.formatSecondsToReadable(KILL_NORMAL)).withStyle(ChatFormatting.GREEN),
                            victimDeathMessage,
                            20, 80, 20);
                }
                else {
                    sendTimeTitle(killer, KILL_NORMAL, ChatFormatting.GREEN);
                }
            }
            if (isAlive) {
                sendTimeTitle(victim, DEATH_NORMAL, ChatFormatting.RED);
            }
        }
        else if (livesManager.canChangeLivesNaturally()) {

            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
            String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);

            victim.ls$addLives(DEATH_BOOGEYMAN);
            killer.ls$addLives(KILL_BOOGEYMAN);

            if (victim.ls$isAlive() || !SHOW_DEATH_TITLE) {
                sendTimeTitle(victim, DEATH_BOOGEYMAN, ChatFormatting.RED);
                PlayerUtils.sendTitleWithSubtitle(killer,Component.nullToEmpty("§aYou are cured!"), Component.literal(msgKiller).withStyle(ChatFormatting.GREEN), 20, 80, 20);
            }
            else {
                PlayerUtils.sendTitleWithSubtitle(killer,Component.nullToEmpty("§aYou are cured, "+msgKiller),
                        livesManager.getDeathMessage(victim)
                        , 20, 80, 20);
            }
        }
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LimitedLifeConfig config)) return;
        LimitedLifeLivesManager.DEFAULT_TIME = LimitedLifeConfig.TIME_DEFAULT.get(config);
        LimitedLifeLivesManager.YELLOW_TIME = LimitedLifeConfig.TIME_YELLOW.get(config);
        LimitedLifeLivesManager.RED_TIME = LimitedLifeConfig.TIME_RED.get(config);
        DEATH_NORMAL = LimitedLifeConfig.TIME_DEATH.get(config);
        DEATH_BOOGEYMAN = LimitedLifeConfig.TIME_DEATH_BOOGEYMAN.get(config);
        KILL_NORMAL = LimitedLifeConfig.TIME_KILL.get(config);
        KILL_BOOGEYMAN = LimitedLifeConfig.TIME_KILL_BOOGEYMAN.get(config);
        TICK_OFFLINE_PLAYERS = LimitedLifeConfig.TICK_OFFLINE_PLAYERS.get(config);
        LimitedLifeLivesManager.BROADCAST_COLOR_CHANGES = LimitedLifeConfig.BROADCAST_COLOR_CHANGES.get(config);
        SHOW_TIME_BELOW_NAME = LimitedLifeConfig.SHOW_TIME_BELOW_NAME.get(config);
    }

    @Override
    public Integer getDefaultLives() {
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
