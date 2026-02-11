package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.mixin.MobEffectInstanceAccessor;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.currentSeason;

public class Session {
    public Map<UUID, Integer> playerNaturalDeathLog = new HashMap<>();
    private List<SessionAction> activeActions = new ArrayList<>();
    public List<UUID> displayTimer = new ArrayList<>();
    public static final int NATURAL_DEATH_LOG_MAX = 2400;
    public static final Time DISPLAY_TIMER_INTERVAL = Time.ticks(5);
    public static final Time TAB_LIST_INTERVAL = Time.ticks(20);
    public static boolean TICK_FREEZE_NOT_IN_SESSION = false;
    public static boolean WORLDBORDER_OUTSIDE_TELEPORT = true;
    public static boolean SESSION_START_COUNTDOWN = false;

    private Time timer = Time.zero();
    private Time sessionLength = Time.nullTime();
    private Time passedTime = Time.zero();
    private Time fullPassedTime = Time.zero();
    public List<Time[]> sessionPauses = new ArrayList<>();

    private SessionStatus status = SessionStatus.NOT_STARTED;
    private int sessionStartInProgress = 0;

    SessionAction endWarning1 = new SessionAction(Time.minutes(-5)) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("Session ends in 5 minutes!").withStyle(ChatFormatting.GOLD));
        }
    };
    SessionAction endWarning2 = new SessionAction(Time.minutes(-30)) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("Session ends in 30 minutes!").withStyle(ChatFormatting.GOLD));
        }
    };
    SessionAction actionInfoAction = new SessionAction(Time.seconds(7)) {
        @Override
        public void trigger() {
            showActionInfo();
        }
    };

    public boolean sessionStart() {
        if (!canStartSession()) return false;
        clearSessionActions();
        if (!currentSeason.sessionStart()) return false;
        if (sessionStartInProgress > 0) return false;
        if (!SESSION_START_COUNTDOWN) {
            startSession();
        }
        else {
            sessionStartInProgress = 150;
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§a3"), 15, 35, 15);
            TaskScheduler.schedulePriorityTask(50, () -> {
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§e2"), 15, 35, 15);
            });
            TaskScheduler.schedulePriorityTask(100, () -> {
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§c1"), 15, 35, 15);
            });
            TaskScheduler.schedulePriorityTask(150, () -> {
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§aThe timer has begun!"), 15, 35, 15);
                startSession();
            });
        }
        return true;
    }

    private void startSession() {
        changeStatus(SessionStatus.STARTED);
        passedTime = Time.zero();
        fullPassedTime = Time.zero();
        DatapackIntegration.setSessionTimePassed(getPassedTime());
        Component line1 = TextUtils.formatLoosely("§6Session started! §7[{}]", sessionLength.formatLong());
        Component line2 = Component.literal("§f/session timer showDisplay§7 - toggles a session timer on your screen.");
        PlayerUtils.broadcastMessage(line1);
        PlayerUtils.broadcastMessage(line2);

        addSessionActionIfTime(endWarning1);
        addSessionActionIfTime(endWarning2);
        addSessionAction(actionInfoAction);

        SessionTranscript.sessionStart();
        SessionTranscript.logPlayers();
    }

    public void clearSessionActions() {
        activeActions.clear();
    }

    public List<SessionAction> getSessionActions() {
        return activeActions;
    }

    public void addSessionAction(SessionAction action) {
        activeActions.add(action);
    }

    public void addSessionActionIfTime(SessionAction action) {
        if (action.shouldTrigger()) return;
        addSessionAction(action);
    }

    public void sessionEnd() {
        SessionTranscript.sessionEnd();
        if (status != SessionStatus.FINISHED && status != SessionStatus.NOT_STARTED) {
            SessionTranscript.onSessionEnd();
            PlayerUtils.broadcastMessage(Component.literal("The session has ended!").withStyle(ChatFormatting.GOLD));
        }
        changeStatus(SessionStatus.FINISHED);
        passedTime = Time.zero();
        fullPassedTime = Time.zero();
        DatapackIntegration.setSessionTimePassed(getPassedTime());
        currentSeason.sessionEnd();
        discardQueuedPauses();
    }

    public void sessionPause() {
        if (statusPaused()) {
            PlayerUtils.broadcastMessage(Component.literal("Session unpaused!").withStyle(ChatFormatting.GOLD));
            changeStatus(SessionStatus.STARTED);
            if (isInQueuedPause()) {
                discardCurrentQueuedPause();
            }
        }
        else {
            PlayerUtils.broadcastMessage(Component.literal("Session paused!").withStyle(ChatFormatting.GOLD));
            changeStatus(SessionStatus.PAUSED);
        }
    }

    public boolean canStartSession() {
        if (!validTime()) return false;
        if (statusStarted()) return false;
        return !statusPaused();
    }

    public void passTime(Time time) {
        passedTime.add(time);
        fullPassedTime.add(time);
    }

    public void setSessionLength(Time time) {
        sessionLength = time;
        Main.getMainConfig().setProperty("session_length", String.valueOf(sessionLength.getTicks()));
        DatapackIntegration.setSessionLength(time);
    }

    public void addSessionLength(Time time) {
        sessionLength.add(time);
        Main.getMainConfig().setProperty("session_length", String.valueOf(sessionLength.getTicks()));
        DatapackIntegration.setSessionLength(time);
    }

    public void removeSessionLength(Time time) {
        addSessionLength(time.multiply(-1));
    }

    public String getRemainingTimeStr() {
        if (!sessionLength.isPresent()) return "";
        return getRemainingTime().formatLong();
    }

    public Time getPassedTime() {
        return passedTime.copy();
    }

    public Time getSessionLength() {
        return sessionLength.copy();
    }

    public Time getRemainingTime() {
        return sessionLength.diff(passedTime);
    }

    public double progress() {
        if (!validTime()) return 0;
        return (double) passedTime.getMillis() / (double) sessionLength.getMillis();
    }

    public double progress(Time offset) {
        if (!validTime()) return 0;
        return ((double) passedTime.getMillis() - offset.getMillis()) / ((double) sessionLength.getMillis() - offset.getMillis());
    }

    public boolean validTime() {
        return sessionLength.isPresent();
    }

    public boolean isInDisplayTimer(ServerPlayer player) {
        return displayTimer.contains(player.getUUID());
    }

    public void addToDisplayTimer(ServerPlayer player) {
        displayTimer.add(player.getUUID());
    }

    public void removeFromDisplayTimer(ServerPlayer player) {
        if (!displayTimer.contains(player.getUUID())) return;
        displayTimer.remove(player.getUUID());
    }

    public void queuePause(Time pauseAt, Time pauseLength) {
        Time[] pauseEntry = new Time[2];
        pauseEntry[0] = pauseAt;
        pauseEntry[1] = pauseLength;
        sessionPauses.add(pauseEntry);
    }

    public boolean isInQueuedPause() {
        for (int i = 0; i < sessionPauses.size(); i++) {
            Time[] pauseEntry = sessionPauses.get(i);
            Time startPause = pauseEntry[0];
            Time pauseLength = pauseEntry[1];
            Time endPause = startPause.copy().add(pauseLength);
            if (fullPassedTime.isLarger(startPause) && fullPassedTime.isSmaller(endPause)) {
                return true;
            }
        }
        return false;
    }

    public void discardCurrentQueuedPause() {
        sessionPauses.removeIf(pauseEntry -> {
            Time startPause = pauseEntry[0];
            Time pauseLength = pauseEntry[1];
            Time endPause = startPause.copy().add(pauseLength);
            return fullPassedTime.isLarger(startPause) && fullPassedTime.isSmaller(endPause);
        });
    }

    public void discardQueuedPauses() {
        sessionPauses.removeIf(pauseEntry -> {
            Time startPause = pauseEntry[0];
            Time pauseLength = pauseEntry[1];
            Time endPause = startPause.copy().add(pauseLength);
            return fullPassedTime.isLarger(endPause);
        });
    }

    public void discardAllQueuedPauses() {
        if (isInQueuedPause()) {
            sessionPause();
        }
        sessionPauses.clear();
    }

    public void tick(MinecraftServer server) {
        if (sessionStartInProgress > 0) sessionStartInProgress--;

        if (statusPaused()) {
            //? if < 1.20.3 {
            /*float tickRate = 20;
             *///?} else {
            float tickRate = server.tickRateManager().tickrate();
            //?}
            if (tickRate == 20) {
                fullPassedTime.tick();
            }
            else {
                fullPassedTime.add((long)((20.0/tickRate)*Time.CONVERT_TICKS));
            }
        }
        discardQueuedPauses();
        boolean inQueuedPause = isInQueuedPause();
        if ((statusStarted() && inQueuedPause) || (statusPaused() && !inQueuedPause)) {
            sessionPause();
        }


        timer.tick();
        if (timer.isMultipleOf(DISPLAY_TIMER_INTERVAL)) {
            displayTimers(server);
            for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
                SimplePackets.SESSION_STATUS.target(player).sendToClient(status.getName());
            }
            //? if <= 1.20.3 {
            /*for (MobEffect effect : blacklist.getBannedEffects()) {
             *///?} else {
            for (Holder<MobEffect> effect : blacklist.getBannedEffects()) {
                //?}
                for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
                    if (player.hasEffect(effect)) {
                        MobEffectInstance actualEffect = player.getEffect(effect);
                        if (actualEffect != null) {
                            if (!actualEffect.isAmbient() && !actualEffect.showIcon() && !actualEffect.isVisible()) continue;
                        }
                        player.removeEffect(effect);
                    }
                }
            }
            //? if <= 1.20.3 {
            /*for (MobEffect effect : blacklist.getClampedEffects()) {
             *///?} else {
            for (Holder<MobEffect> effect : blacklist.getClampedEffects()) {
                //?}
                for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
                    if (player.hasEffect(effect)) {
                        MobEffectInstance actualEffect = player.getEffect(effect);
                        if (actualEffect != null && actualEffect.getAmplifier() > 0 && actualEffect instanceof MobEffectInstanceAccessor accessor) {
                            player.removeEffect(effect);
                            accessor.ls$setAmplifier(1);
                            player.addEffect(actualEffect);
                        }
                    }
                }
            }
        }
        if (timer.isMultipleOf(TAB_LIST_INTERVAL)) {
            Events.updatePlayerListsNextTick = true;
        }

        if (playerNaturalDeathLog != null && !playerNaturalDeathLog.isEmpty()) {
            int currentTime = server.getTickCount();
            List<UUID> removeQueue = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : playerNaturalDeathLog.entrySet()) {
                int tickDiff = currentTime - entry.getValue();
                if (tickDiff >= NATURAL_DEATH_LOG_MAX) {
                    removeQueue.add(entry.getKey());
                }
            }
            if (!removeQueue.isEmpty()) {
                removeQueue.forEach(playerNaturalDeathLog::remove);
            }
        }
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (player.isSpectator()) continue;
            checkPlayerPosition(player);
        }

        //? if >= 1.20.3 {
        if (server.tickRateManager().isFrozen()) return;
        //?}
        if (!validTime()) return;
        if (!statusStarted()) return;
        tickSessionOn(server);
        currentSeason.tickSessionOn(server);
    }

    public void tickSessionOn(MinecraftServer server) {
        //? if < 1.20.3 {
        /*float tickRate = 20;
        *///?} else {
        float tickRate = server.tickRateManager().tickrate();
        //?}
        if (tickRate == 20) {
            passedTime.tick();
        }
        else {
            passedTime.add((long)((20.0/tickRate)*Time.CONVERT_TICKS));
        }
        fullPassedTime = passedTime.copy();
        DatapackIntegration.setSessionTimePassed(getPassedTime());

        if (passedTime.isLarger(sessionLength)) {
            sessionEnd();
        }

        //Actions
        if (activeActions == null) return;
        if (activeActions.isEmpty()) return;
        List<SessionAction> remaining = new ArrayList<>();
        for (SessionAction action : activeActions) {
            boolean triggered = action.tick();
            if (!triggered) {
                remaining.add(action);
            }
        }
        activeActions = remaining;
    }

    private Map<UUID, Vec3> lastNonBorderPositions = new HashMap<>();
    public void checkPlayerPosition(ServerPlayer player) {
        if (!WORLDBORDER_OUTSIDE_TELEPORT) return;
        WorldBorder border = player.ls$getServerLevel().getWorldBorder();
        double playerSize = player.getBoundingBox().getXsize()/2;
        double minX = Math.floor(border.getMinX()) + playerSize;
        double maxX = Math.ceil(border.getMaxX()) - playerSize;
        double minZ = Math.floor(border.getMinZ()) + playerSize;
        double maxZ = Math.ceil(border.getMaxZ()) - playerSize;

        double playerX = player.getX();
        double playerZ = player.getZ();

        UUID uuid = player.getUUID();

        if (playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ) {
            if (lastNonBorderPositions.containsKey(uuid)) {
                Vec3 pos = lastNonBorderPositions.get(uuid);
                if (!(pos.x < minX || pos.x > maxX || pos.z < minZ || pos.z > maxZ)) {
                    PlayerUtils.teleport(player, pos);
                    return;
                }
            }

            // Clamp player position inside the border
            double clampedX = OtherUtils.clamp(playerX, minX, maxX);
            double clampedZ = OtherUtils.clamp(playerZ, minZ, maxZ);

            // Teleport player inside the world border
            PlayerUtils.teleport(player, clampedX, player.getY(), clampedZ);
        }
        else {
            lastNonBorderPositions.put(uuid, player.position());
        }
    }

    public static final Map<UUID, Integer> skipTimer = new HashMap<>();
    public void displayTimers(MinecraftServer server) {
        if (currentSeason instanceof LimitedLife limitedLife) {
            limitedLife.displayTimers(server);
            return;
        }

        String message = "";
        if (statusNotStarted()) {
            message = "Session has not started";
        }
        else if (statusStarted()) {
            message = getRemainingTimeStr();
        }
        else if (statusPaused()) {
            message = "Session has been paused";
        }
        else if (statusFinished()) {
            message = "Session has ended";
        }

        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            UUID uuid = player.getUUID();
            if (displayTimer.contains(player.getUUID())) {
                if (skipTimer.containsKey(uuid)) {
                    int value = skipTimer.get(uuid);
                    value--;
                    if (value > 0) skipTimer.put(uuid, value);
                    else skipTimer.remove(uuid);
                    continue;
                }

                if (!NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                    player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.GRAY), true);
                }
            }
            if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                long timestamp = SessionTimerStates.OFF.getValue();
                if (statusNotStarted()) timestamp = SessionTimerStates.NOT_STARTED.getValue();
                else if (statusPaused()) timestamp = SessionTimerStates.PAUSED.getValue();
                else if (statusFinished()) timestamp = SessionTimerStates.ENDED.getValue();
                else if (sessionLength.isPresent()) {
                    Time remainingTime = getRemainingTime();
                    timestamp = Time.now().add(remainingTime).getMillis();
                }
                if (timestamp != SessionTimerStates.OFF.getValue()) {
                    SimplePackets.SESSION_TIMER.target(player).sendToClient(timestamp);
                }
            }
        }
    }

    public void showActionInfo() {
        if (getSessionActions().isEmpty()) return;
        List<SessionAction> actions = new ArrayList<>(getSessionActions());
        actions.sort(Comparator.comparingInt(action -> action.getTriggerTime().getTicks()));
        List<Component> messages = new ArrayList<>();
        for (SessionAction action : actions) {
            String actionMessage = action.sessionMessage;
            if (actionMessage == null) continue;
            if (actionMessage.isEmpty()) continue;
            if (messages.isEmpty()) {
                messages.add(Component.nullToEmpty("§7Queued session actions:"));
            }
            if (action.showTime) {
                messages.add(TextUtils.formatLoosely("§7- {} §f[{}]", actionMessage, action.getTriggerTime().formatLong()));
            }
            else {
                messages.add(TextUtils.formatLoosely("§7- {}", actionMessage));
            }
        }

        messages.forEach(PlayerUtils::broadcastMessageToAdmins);
    }

    public boolean statusStarted() {
        return status == SessionStatus.STARTED;
    }

    public boolean statusPaused() {
        return status == SessionStatus.PAUSED;
    }

    public boolean statusFinished() {
        return status == SessionStatus.FINISHED;
    }

    public boolean statusNotStarted() {
        return status == SessionStatus.NOT_STARTED;
    }

    public void changeStatus(SessionStatus newStatus) {
        SessionStatus prevStatus = status;
        status = newStatus;
        currentSeason.sessionChangeStatus(status);
        freezeIfNecessary();
        DatapackIntegration.changeSessionStatus(prevStatus, newStatus);
    }

    public void freezeIfNecessary() {
        if (!TICK_FREEZE_NOT_IN_SESSION) return;
        boolean frozen = status != SessionStatus.STARTED;
        OtherUtils.setFreezeGame(frozen);
    }
}
