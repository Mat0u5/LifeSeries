package net.mat0u5.lifeseries.seasons.session;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.Task;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class SessionTranscript {
    public static final List<String> messages = new ArrayList<>();

    public static void societyEndSuccess(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has marked the Secret Society as successful.", player));
    }
    public static void societyEndFail(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has marked the Secret Society as failed.", player));
    }
    public static void societyMemberInitiated(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has been initiated into the Secret Society.", player));
    }

    public static void societyMembersChosen(List<ServerPlayer> players) {
        addMessageWithTime(TextUtils.formatString("Secret Society members chosen: {}", players));
    }
    public static void societyStarted() {
        addMessageWithTime("The Secret Society has started.");
    }
    public static void societyEnded() {
        addMessageWithTime("The Secret Society has ended.");
    }

    public static void logHealth(ServerPlayer player, double health) {
        addMessageWithTime(TextUtils.formatString("{} is now on {} health.", player, health));
    }

    public static void giftHeart(ServerPlayer player, ServerPlayer receiver) {
        addMessageWithTime(TextUtils.formatString("{} gifted a heart to {}.", player, receiver));
    }

    public static void newSuperpower(ServerPlayer player, Superpowers superpower) {
        addMessageWithTime(TextUtils.formatString("{} has been assigned the {} superpower.", player, superpower.getString()));
    }

    public static void newTriviaBot(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("Spawned trivia bot for {}", player));
    }

    public static void endingIsYours() {
        addMessageWithTime("The ending is yours... Make it WILD.");
    }

    public static void newHungerRule() {
        addMessageWithTime("[Wildcard] Food has been randomized.");
    }

    public static void mobSwap() {
        addMessageWithTime("[Wildcard] Mobs have been swapped.");
    }

    public static void deactivateWildcard(Wildcards type) {
        addMessageWithTime(TextUtils.formatString("Deactivated Wildcard: {}", type));
    }

    public static void activateWildcard(Wildcards type) {
        addMessageWithTime(TextUtils.formatString("Activated Wildcard: {}", type));
    }

    public static void logPlayers() {
        addMessageWithTime(TextUtils.formatString("Players online: {}", PlayerUtils.getAllPlayers()));
    }

    public static void rerollTask(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has rerolled their task.", player));
    }

    public static void successTask(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has passed their task.", player));
    }

    public static void failTask(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} has failed their task.", player));
    }

    public static void assignTask(ServerPlayer player, Task task, List<String> linesStr) {
        addMessageWithTime(TextUtils.formatString("{} has been given a {} task: {}", player, task.type.name(), String.join(" ", linesStr)));
    }

    public static void claimKill(ServerPlayer killer, ServerPlayer victim) {
        addMessageWithTime(TextUtils.formatString("{}'s kill claim of {} has been accepted.", killer, victim));
    }

    public static void soulmate(ServerPlayer player, ServerPlayer soulmate) {
        addMessageWithTime(TextUtils.formatString("{}'s soulmate has been chosen to be {}", player, soulmate));
    }

    public static void assignRandomLives(ServerPlayer player, int amount) {
        addMessageWithTime(TextUtils.formatString("{} has been randomly assigned {} lives", player, amount));
    }

    public static void givelife(Component playerName, ServerPlayer target) {
        addMessageWithTime("<@","> ",TextUtils.formatString("{} gave a life to {}", playerName, target));
    }

    public static void playerLeave(ServerPlayer player) {
        addMessageWithTime("<@","> ",TextUtils.formatString("{} left the game.", player));
        addRecordIfMissing(player);
    }

    public static void playerJoin(ServerPlayer player) {
        addMessageWithTime("<@","> ",TextUtils.formatString("{} joined the game.", player));
        addRecordIfMissing(player);
    }

    public static void triggerSessionAction(String message) {
        if (message == null || message.isEmpty()) return;
        addMessageWithTime("TRIGGERED_SESSION_ACTION: " + message);
    }

    public static void onPlayerDeath(ServerPlayer player, DamageSource source) {
        addMessageWithTime("<@","> ",source.getLocalizedDeathMessage(player).getString());
        playerRecords.computeIfPresent(player.getScoreboardName(), (key, value) -> {
            value.set(1, value.get(1)+1);
            return value;
        });
    }

    public static void onPlayerKilledByPlayer(ServerPlayer victim, ServerPlayer killer) {
        addRecordIfMissing(killer);
        playerRecords.computeIfPresent(killer.getScoreboardName(), (key, value) -> {
            value.set(0, value.get(0)+1);
            return value;
        });
    }

    public static void addRecordIfMissing(ServerPlayer player) {
        if (player.ls$isDead() || player.ls$isWatcher()) return;
        if (!playerRecords.containsKey(player.getScoreboardName())) {
            playerRecords.put(player.getScoreboardName(), new ArrayList<>(List.of(0,0)));
        }
    }

    public static void onPlayerLostAllLives(ServerPlayer player) {
        addMessageWithTime(TextUtils.formatString("{} lost all lives.", player));
    }

    public static void boogeymenChosen(List<ServerPlayer> players) {
        addMessageWithTime(TextUtils.formatString("Boogeymen chosen: {}", players));
    }

    public static void sessionStart() {
        playerRecords.clear();
        PlayerUtils.getAllFunctioningPlayers().forEach(SessionTranscript::addRecordIfMissing);
        messages.add("\n");
        addMessageWithTime("-----  Session started!  -----");
    }

    public static Map<String, List<Integer>> playerRecords = new HashMap<>();
    public static void sessionEnd() {
        addMessageWithTime("-----  The session has ended!  -----\n");
        for (Map.Entry<String, List<Integer>> playerRecord : playerRecords.entrySet()) {
            if (playerRecord.getValue().size() < 2) continue;
            int kills = playerRecord.getValue().get(0);
            int deaths = playerRecord.getValue().get(1);
            messages.add(TextUtils.formatString("\t{}: {} {} and {} {}",
                    playerRecord.getKey(),
                    kills, TextUtils.pluralize("kill", kills),
                    deaths, TextUtils.pluralize("death", deaths)));
        }
        if (!playerRecords.isEmpty()) {
            messages.add("\n");
        }
    }
    public static void addMessageWithTime(String message) {
        addMessageWithTime("[@","] ", message);
    }

    private static void addMessageWithTime(String start, String end, String message) {
        String time = currentSession.getPassedTimeStr();
        String finalMessage = start+time+end+message;

        if (currentSession.statusNotStarted() || currentSession.statusFinished()) {
            finalMessage = message;
        }

        if (messages.isEmpty()) {
            addDefaultMessages();
        }
        messages.add(finalMessage);
    }

    public static void resetStats() {
        messages.clear();
        addDefaultMessages();
    }

    public static void addDefaultMessages() {
        messages.add(TextUtils.formatString("-----  Life Series Mod by Mat0u5  |  Mod version: {}  -----", Main.MOD_VERSION));
        messages.add(TextUtils.formatString("-----  {}  |  Time and date: {}  -----", currentSeason.getSeason().name(), OtherUtils.getTimeAndDate()));
        messages.add("-----  Session Transcript  -----\n");
    }

    public static String getStats() {
        return String.join("\n", messages);
    }

    public static void onSessionEnd() {
        if (currentSeason instanceof SecretLife secretLife) {
            secretLife.heartsTranscript();
        }
        sendTranscriptToAdmins();
        writeTranscriptToFile();
    }

    public static void sendTranscriptToAdmins() {
        Component sessionTranscript = getTranscriptMessage();
        PlayerUtils.broadcastMessageToAdmins(sessionTranscript);
    }

    public static void writeTranscriptToFile() {
        String content = SessionTranscript.getStats();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String filename = now.format(formatter) + ".txt";
        try {
            Path filePath = Paths.get("transcripts", filename);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes());
            Main.LOGGER.info("Session transcript file created: " + filePath);
        }catch(Exception ignored) {}
    }

    public static Component getTranscriptMessage() {
        return TextUtils.format("§7Click {}§7 to copy the session transcript.", TextUtils.copyClipboardText(SessionTranscript.getStats()));
    }

    public record TranscriptPlayerRecord(UUID uuid, String name, int kills, int deaths) {}
}
