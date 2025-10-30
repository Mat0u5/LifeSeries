package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLife;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;

import java.util.*;

import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.currentSeason;
//? if >= 1.21.2
/*import net.minecraft.network.packet.s2c.play.PositionFlag;*/

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
            PlayerUtils.broadcastMessage(Text.literal("Session ends in 5 minutes!").formatted(Formatting.GOLD));
        }
    };
    SessionAction endWarning2 = new SessionAction(OtherUtils.minutesToTicks(-30)) {
        @Override
        public void trigger() {
            PlayerUtils.broadcastMessage(Text.literal("Session ends in 30 minutes!").formatted(Formatting.GOLD));
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
        Text line1 = TextUtils.formatLoosely("§6Session started! §7[{}]", OtherUtils.formatTime(sessionLength));
        Text line2 = Text.literal("§f/session timer showDisplay§7 - toggles a session timer on your screen.");
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
            PlayerUtils.broadcastMessage(Text.literal("The session has ended!").formatted(Formatting.GOLD));
        }
        changeStatus(SessionStatus.FINISHED);
        passedTime = 0;
        currentSeason.sessionEnd();
    }

    public void sessionPause() {
        if (statusPaused()) {
            PlayerUtils.broadcastMessage(Text.literal("Session unpaused!").formatted(Formatting.GOLD));
            changeStatus(SessionStatus.STARTED);
        }
        else {
            PlayerUtils.broadcastMessage(Text.literal("Session paused!").formatted(Formatting.GOLD));
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

    public boolean isInDisplayTimer(ServerPlayerEntity player) {
        return displayTimer.contains(player.getUuid());
    }

    public void addToDisplayTimer(ServerPlayerEntity player) {
        displayTimer.add(player.getUuid());
    }

    public void removeFromDisplayTimer(ServerPlayerEntity player) {
        if (!displayTimer.contains(player.getUuid())) return;
        displayTimer.remove(player.getUuid());
    }

    public void tick(MinecraftServer server) {
        currentTimer++;
        if (currentTimer % DISPLAY_TIMER_INTERVAL == 0) {
            displayTimers(server);
            for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                NetworkHandlerServer.sendStringPacket(player, PacketNames.SESSION_STATUS, status.getName());
            }
            for (RegistryEntry<StatusEffect> effect : blacklist.getBannedEffects()) {
                for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                    if (player.hasStatusEffect(effect)) {
                        StatusEffectInstance actualEffect = player.getStatusEffect(effect);
                        if (actualEffect != null) {
                            if (!actualEffect.isAmbient() && !actualEffect.shouldShowIcon() && !actualEffect.shouldShowParticles()) continue;
                        }
                        player.removeStatusEffect(effect);
                    }
                }
            }
        }
        if (currentTimer % TAB_LIST_INTERVAL == 0) {
            Events.updatePlayerListsNextTick = true;
        }

        if (playerNaturalDeathLog != null && !playerNaturalDeathLog.isEmpty()) {
            int currentTime = server.getTicks();
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
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (player.isSpectator()) continue;
            checkPlayerPosition(player);
        }

        if (!validTime()) return;
        if (!statusStarted()) return;
        tickSessionOn(server);
        currentSeason.tickSessionOn(server);
    }

    public void tickSessionOn(MinecraftServer server) {
        float tickRate = server.getTickManager().getTickRate();
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

    private Map<UUID, Vec3d> lastNonBorderPositions = new HashMap<>();
    public void checkPlayerPosition(ServerPlayerEntity player) {
        WorldBorder border = PlayerUtils.getServerWorld(player).getWorldBorder();
        double playerSize = player.getBoundingBox().getLengthX()/2;
        double minX = Math.floor(border.getBoundWest()) + playerSize;
        double maxX = Math.ceil(border.getBoundEast()) - playerSize;
        double minZ = Math.floor(border.getBoundNorth()) + playerSize;
        double maxZ = Math.ceil(border.getBoundSouth()) - playerSize;

        double playerX = player.getX();
        double playerZ = player.getZ();

        UUID uuid = player.getUuid();

        if (playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ) {
            if (lastNonBorderPositions.containsKey(uuid)) {
                Vec3d pos = lastNonBorderPositions.get(uuid);
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
            lastNonBorderPositions.put(uuid, player.ls$getEntityPos());
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

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            UUID uuid = player.getUuid();
            if (displayTimer.contains(player.getUuid())) {
                if (skipTimer.containsKey(uuid)) {
                    int value = skipTimer.get(uuid);
                    value--;
                    if (value > 0) skipTimer.put(uuid, value);
                    else skipTimer.remove(uuid);
                    continue;
                }

                if (!NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                    player.sendMessage(Text.literal(message).formatted(Formatting.GRAY), true);
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
        List<Text> messages = new ArrayList<>();
        for (SessionAction action : actions) {
            String actionMessage = action.sessionMessage;
            if (actionMessage == null) continue;
            if (actionMessage.isEmpty()) continue;
            if (messages.isEmpty()) {
                messages.add(Text.of("§7Queued session actions:"));
            }
            messages.add(Text.of("§7- "+actionMessage));
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
