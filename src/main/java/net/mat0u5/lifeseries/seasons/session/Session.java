package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
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
    public static final int DISPLAY_TIMER_INTERVAL = 5;
    public static final int TAB_LIST_INTERVAL = 20;
    public long currentTimer = 0;

    public Integer sessionLength = null;
    public double passedTime = 0;
    private SessionStatus status = SessionStatus.NOT_STARTED;

    SessionAction endWarning1 = new SessionAction(OtherUtils.minutesToTicks(-5)) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("Session ends in 5 minutes!").withStyle(ChatFormatting.GOLD));
        }
    };
    SessionAction endWarning2 = new SessionAction(OtherUtils.minutesToTicks(-30)) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Component.literal("Session ends in 30 minutes!").withStyle(ChatFormatting.GOLD));
        }
    };
    SessionAction actionInfoAction = new SessionAction(OtherUtils.secondsToTicks(7)) {
        @Override
        public void trigger() {
            showActionInfo();
        }
    };

    public boolean sessionStart() {
        if (!canStartSession()) return false;
        clearSessionActions();
        if (!currentSeason.sessionStart()) return false;
        changeStatus(SessionStatus.STARTED);
        passedTime = 0;
        Component line1 = TextUtils.formatLoosely("§6Session started! §7[{}]", OtherUtils.formatTime(sessionLength));
        Component line2 = Component.literal("§f/session timer showDisplay§7 - toggles a session timer on your screen.");
        PlayerUtils.broadcastMessage(line1);
        PlayerUtils.broadcastMessage(line2);

        addSessionActionIfTime(endWarning1);
        addSessionActionIfTime(endWarning2);
        addSessionAction(actionInfoAction);

        SessionTranscript.sessionStart();
        SessionTranscript.logPlayers();
        return true;
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
        passedTime = 0;
        currentSeason.sessionEnd();
    }

    public void sessionPause() {
        if (statusPaused()) {
            PlayerUtils.broadcastMessage(Component.literal("Session unpaused!").withStyle(ChatFormatting.GOLD));
            changeStatus(SessionStatus.STARTED);
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

    public void setSessionLength(int lengthTicks) {
        sessionLength = lengthTicks;
        Main.getMainConfig().setProperty("session_length", String.valueOf(sessionLength));
    }

    public void addSessionLength(int lengthTicks) {
        if (sessionLength == null) sessionLength = 0;
        setSessionLength(sessionLength + lengthTicks);
    }

    public void removeSessionLength(int lengthTicks) {
        if (sessionLength == null) sessionLength = 0;
        setSessionLength(sessionLength - lengthTicks);
    }

    public String getSessionLength() {
        if (sessionLength == null) return "";
        return OtherUtils.formatTime(sessionLength);
    }

    public String getPassedTimeStr() {
        return OtherUtils.formatTime(getPassedTime());
    }

    public String getRemainingTimeStr() {
        if (sessionLength == null) return "";
        return OtherUtils.formatTime(getRemainingTime());
    }

    public int getPassedTime() {
        return (int) passedTime;
    }

    public int getRemainingTime() {
        return sessionLength - getPassedTime();
    }

    public boolean validTime() {
        return sessionLength != null;
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

    public void tick(MinecraftServer server) {
        currentTimer++;
        if (currentTimer % DISPLAY_TIMER_INTERVAL == 0) {
            displayTimers(server);
            for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
                NetworkHandlerServer.sendStringPacket(player, PacketNames.SESSION_STATUS, status.getName());
            }
            for (Holder<MobEffect> effect : blacklist.getBannedEffects()) {
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
        }
        if (currentTimer % TAB_LIST_INTERVAL == 0) {
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

        if (!validTime()) return;
        if (!statusStarted()) return;
        tickSessionOn(server);
        currentSeason.tickSessionOn(server);
    }

    public void tickSessionOn(MinecraftServer server) {
        float tickRate = server.tickRateManager().tickrate();
        if (tickRate == 20) {
            passedTime++;
        }
        else {
            passedTime += (20/tickRate);
        }

        if (passedTime >= sessionLength) {
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
            double clampedX = Math.clamp(playerX, minX, maxX);
            double clampedZ = Math.clamp(playerZ, minZ, maxZ);

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
                else if (sessionLength != null) {
                    long remainingMillis = (sessionLength - (int) passedTime) * 50;
                    timestamp = System.currentTimeMillis() + remainingMillis;
                }
                if (timestamp != SessionTimerStates.OFF.getValue()) {
                    NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, timestamp);
                }
            }
        }
    }

    public void showActionInfo() {
        if (getSessionActions().isEmpty()) return;
        List<SessionAction> actions = new ArrayList<>(getSessionActions());
        actions.sort(Comparator.comparingInt(SessionAction::getTriggerTime));
        List<Component> messages = new ArrayList<>();
        for (SessionAction action : actions) {
            String actionMessage = action.sessionMessage;
            if (actionMessage == null) continue;
            if (actionMessage.isEmpty()) continue;
            if (messages.isEmpty()) {
                messages.add(Component.nullToEmpty("§7Queued session actions:"));
            }
            messages.add(Component.nullToEmpty("§7- "+actionMessage));
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
        status = newStatus;
        currentSeason.sessionChangeStatus(status);
    }
}
