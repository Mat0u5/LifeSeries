package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.mat0u5.lifeseries.Main.currentSession;

public class Callback extends Wildcard {
    private static final Random rnd = new Random();
    private static int activatedAt = -1;

    public static double TURN_OFF = 0.75; // When all wildcards stop
    private static final Time INITIAL_ACTIVATION_INTERVAL = Time.minutes(5);
    private static final Time INITIAL_DEACTIVATION_INTERVAL = Time.seconds(30);
    public static boolean NERFED_WILDCARDS = true;

    private int nextActivationTick = -1;
    private int nextDeactivationTick = -1;
    public static boolean allWildcardsPhaseReached = false;
    private boolean preAllWildcardsPhaseReached = false;

    private static List<Wildcards> blacklistedWildcards = List.of(Wildcards.HUNGER);

    public static void setBlacklist(String blacklist) {
        blacklistedWildcards = new ArrayList<>();
        String[] wildcards = blacklist.replace("[","").replace("]","").split(",");
        for (String wildcardName : wildcards) {
            Wildcards wildcard = Wildcards.getFromString(wildcardName.trim());
            if (wildcard == null || wildcard == Wildcards.NULL) continue;
            blacklistedWildcards.add(wildcard);
        }
    }

    @Override
    public Wildcards getType() {
        return Wildcards.CALLBACK;
    }

    @Override
    public void tick() {
        if (!currentSession.validTime()) return;
        int passedTimeTicks = currentSession.getPassedTime().getTicks();
        int sessionLengthTicks = currentSession.getSessionLength().getTicks();

        double sessionProgress = (passedTimeTicks -activatedAt) / (sessionLengthTicks -activatedAt);

        if (nextActivationTick == -1) {
            nextActivationTick = passedTimeTicks + 20 * 60 * 5; // First activation after 5 minutes
        }

        if (sessionProgress >= TURN_OFF && active) {
            deactivate();
            allWildcardsPhaseReached = true;
            return;
        }

        if (allWildcardsPhaseReached) return;

        double approachingEndPhase = TURN_OFF - (6000.0/sessionLengthTicks); // 5 minutes before the end
        if (sessionProgress >= approachingEndPhase) {
            activateAllWildcards();
            allWildcardsPhaseReached = true;
            return;
        }

        if (preAllWildcardsPhaseReached) return;
        double furtherApproachingEndPhase = TURN_OFF - (6600.0/sessionLengthTicks); // 5.5 minutes before the end - no more actions
        if (sessionProgress >= furtherApproachingEndPhase) {
            if (WildcardManager.isActiveWildcard(Wildcards.TIME_DILATION)) {
                // Disable Time Dilation if it's active...
                WildcardManager.fadedWildcard();
                Wildcard wildcardInstance = WildcardManager.activeWildcards.get(Wildcards.TIME_DILATION);
                wildcardInstance.deactivate();
                WildcardManager.activeWildcards.remove(Wildcards.TIME_DILATION);
                NetworkHandlerServer.sendUpdatePackets();
            }
            preAllWildcardsPhaseReached = true;
            return;
        }

        int targetActiveCount = getTargetActiveWildcardCount(sessionProgress, sessionLengthTicks);
        int currentActiveCount = Wildcards.getActiveWildcards().size()-1;

        if ((currentActiveCount < targetActiveCount && passedTimeTicks >= nextActivationTick) ||
                (passedTimeTicks >= nextActivationTick && nextActivationTick > 0)) {

            activateRandomWildcard();

            double progressFactor = 1.0 - sessionProgress;
            int activationIntervalTicks = (int)(INITIAL_ACTIVATION_INTERVAL.getTicks() * Math.max(0.5, progressFactor));
            nextActivationTick = passedTimeTicks + activationIntervalTicks;

            double deactivationProgressFactor = 1 + (sessionProgress / TURN_OFF) * 4;
            int deactivationIntervalTicks = (int)(INITIAL_DEACTIVATION_INTERVAL.getTicks() * OtherUtils.clamp(deactivationProgressFactor, 1, 5));
            nextDeactivationTick = passedTimeTicks + deactivationIntervalTicks;
        }

        if (currentActiveCount > targetActiveCount && nextDeactivationTick > 0 && passedTimeTicks >= nextDeactivationTick) {

            deactivateRandomWildcard();
            nextDeactivationTick = -1;
        }
    }

    private int getTargetActiveWildcardCount(double sessionProgress, int sessionLengthTicks) {
        double approachingEndPhase = TURN_OFF - (6000.0/sessionLengthTicks);
        double newProgress = sessionProgress/approachingEndPhase;
        if (newProgress < 0.25) return 1;
        if (newProgress < 0.5) return 2;
        if (newProgress < 0.75) return 3;
        return 4;
    }

    @Override
    public void activate() {
        activatedAt = currentSession.getPassedTime().getTicks();
        nextActivationTick = -1;
        nextDeactivationTick = -1;
        allWildcardsPhaseReached = false;
        preAllWildcardsPhaseReached = false;
        //? if <= 1.20.3 {
        /*softActivateWildcard(getRandomInactiveWildcard());
        *///?} else {
        if (!blacklistedWildcards.contains(Wildcards.SIZE_SHIFTING)) {
            softActivateWildcard(Wildcards.SIZE_SHIFTING);
        }
        else {
            softActivateWildcard(getRandomInactiveWildcard());
        }
        //?}
        super.activate();
    }

    @Override
    public void deactivate() {
        deactivateAllWildcards();
        TaskScheduler.scheduleTask(50, () -> {
            if (currentSession.statusStarted()) {
                showEndingTitles();
            }
        });
        super.deactivate();
    }

    public static void showEndingTitles() {
        SessionTranscript.endingIsYours();
        List<ServerPlayer> players = PlayerUtils.getAllPlayers();
        PlayerUtils.sendTitleToPlayers(players, ModifiableText.WILDLIFE_MAKEITWILD_PT1.get(), 0, 90, 0);
        TaskScheduler.scheduleTask(80, () -> {

            PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, ModifiableText.WILDLIFE_MAKEITWILD_PT2.get(), 0, 40, 0);
        });
        TaskScheduler.scheduleTask(110, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, ModifiableText.WILDLIFE_MAKEITWILD_PT3.get(), 0, 40, 0);
        });
        TaskScheduler.scheduleTask(140, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.ZOMBIE_VILLAGER_CURE, 0.2f, 1);
            PlayerUtils.sendTitleToPlayers(players, ModifiableText.WILDLIFE_MAKEITWILD_PT4.get(), 0, 90, 20);

        });
    }

    public void activateAllWildcards() {
        List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
        for (Wildcards wildcard : inactiveWildcards) {
            if (wildcard == Wildcards.CALLBACK) continue;
            if (blacklistedWildcards.contains(wildcard)) continue;
            Wildcard wildcardInstance = wildcard.getInstance();
            if (wildcardInstance == null) continue;
            WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
        }

        WildcardManager.showDots();
        TaskScheduler.scheduleTask(90, () -> {
            for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
                if (wildcard.active) continue;
                wildcard.activate();
            }
            WildcardManager.showRainbowCryptTitle("All wildcards are active!");
        });
        TaskScheduler.scheduleTask(92, NetworkHandlerServer::sendUpdatePackets);

    }

    public void deactivateAllWildcards() {
        for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
            if (wildcard.getType() == Wildcards.CALLBACK) continue;
            wildcard.deactivate();
            PlayerUtils.broadcastMessage(ModifiableText.WILDLIFE_WILDCARD_FADED.get());
        }
        WildcardManager.activeWildcards.clear();
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.BEACON_DEACTIVATE);
        NetworkHandlerServer.sendUpdatePackets();
    }
    private Wildcards lastActivatedWildcard;
    public void activateRandomWildcard() {
        Wildcards wildcard = getRandomInactiveWildcard();
        if (wildcard == null) return;
        Wildcard wildcardInstance = wildcard.getInstance();
        if (wildcardInstance == null) return;
        WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
        WildcardManager.activateWildcards();
        lastActivatedWildcard = wildcard;
    }

    public void deactivateRandomWildcard() {
        Wildcards wildcard = getRandomActiveWildcard();
        if (wildcard == null) return;
        Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
        if (wildcardInstance == null) return;
        wildcardInstance.deactivate();
        WildcardManager.activeWildcards.remove(wildcard);
        WildcardManager.fadedWildcard();
        NetworkHandlerServer.sendUpdatePackets();
    }

    public Wildcards getRandomInactiveWildcard() {
        List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
        inactiveWildcards.remove(Wildcards.CALLBACK);
        inactiveWildcards.removeIf(blacklistedWildcards::contains);
        if (inactiveWildcards.isEmpty()) return null;
        return inactiveWildcards.get(rnd.nextInt(inactiveWildcards.size()));
    }

    public Wildcards getRandomActiveWildcard() {
        List<Wildcards> activeWildcards = Wildcards.getActiveWildcards();
        activeWildcards.remove(Wildcards.CALLBACK);
        activeWildcards.removeIf(blacklistedWildcards::contains);
        if (activeWildcards.isEmpty()) return null;
        if (lastActivatedWildcard != null) {
            activeWildcards.remove(lastActivatedWildcard);
            if (activeWildcards.isEmpty()) return lastActivatedWildcard;
        }
        return activeWildcards.get(rnd.nextInt(activeWildcards.size()));
    }

    public void softActivateWildcard(Wildcards wildcard) {
        if (WildcardManager.isActiveWildcard(wildcard)) return;
        Wildcard wildcardInstance = wildcard.getInstance();
        if (wildcardInstance == null) return;
        WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
        wildcardInstance.activate();
        TaskScheduler.scheduleTask(2, NetworkHandlerServer::sendUpdatePackets);
    }
}
