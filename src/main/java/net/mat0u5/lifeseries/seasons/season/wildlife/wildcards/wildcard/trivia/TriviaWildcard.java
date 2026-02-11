package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotPathfinding;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.io.IOException;
import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class TriviaWildcard extends Wildcard {
    private static final Map<UUID, Queue<Integer>> playerSpawnQueue = new HashMap<>();
    private static final Map<UUID, Integer> spawnedBotsFor = new HashMap<>();
    public static final Map<UUID, Snail> snails = new HashMap<>();
    private static boolean globalScheduleInitialized = false;
    public static Map<UUID, TriviaBot> bots = new HashMap<>();
    public static Time activatedAt = Time.nullTime();
    public static int TRIVIA_BOTS_PER_PLAYER = 5;
    public static int MIN_BOT_DELAY = 8400;
    public static TriviaQuestionManager easyTrivia;
    public static TriviaQuestionManager normalTrivia;
    public static TriviaQuestionManager hardTrivia;
    private static final Random rnd = new Random();
    public static Time timer = Time.zero();
    public static Map<UUID, Tuple<Integer, TriviaQuestion>> preAssignedTrivia = new HashMap<>();

    @Override
    public Wildcards getType() {
        return Wildcards.TRIVIA;
    }

    @Override
    public void tickSessionOn() {
        Time passedTime = currentSession.getPassedTime().diff(activatedAt);
        if (passedTime.isMultipleOf(Time.seconds(1))) trySpawnBots();
        if (passedTime.isMultipleOf(Time.seconds(10))) updateDeadBots();
    }

    @Override
    public void tick() {
        timer.tick();
        if (timer.isMultipleOf(Time.seconds(10))) {
            for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
                UUID playerUUID = player.getUUID();
                if (snails.containsKey(playerUUID)) {
                    Snail snail = snails.get(playerUUID);
                    if (snail == null || !snail.isAlive()) {
                        snails.remove(playerUUID);
                    }
                }
            }
        }
    }

    @Override
    public void activate() {
        usedEasyQuestions.clear();
        usedNormalQuestions.clear();
        usedHardQuestions.clear();
        resetQueue();
        spawnedBotsFor.clear();
        activatedAt = currentSession.getPassedTime();
        bots.clear();
        WildLifeTriviaHandler.cursedGigantificationPlayers.clear();
        WildLifeTriviaHandler.cursedHeartPlayers.clear();
        WildLifeTriviaHandler.cursedMoonJumpPlayers.clear();
        if (!currentSession.statusStarted()) {
            PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7You must start a session for trivia bots to spawn!"));
        }
        PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7You can modify the trivia questions in the config files (./config/lifeseries/wildlife/*-trivia)"));
        super.activate();
    }

    @Override
    public void deactivate() {
        usedEasyQuestions.clear();
        usedNormalQuestions.clear();
        usedHardQuestions.clear();
        resetQueue();
        spawnedBotsFor.clear();
        bots.clear();
        killAllBots();
        killAllTriviaSnails();
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            TriviaWildcard.resetPlayerOnBotSpawn(player);
        }
        WildLifeTriviaHandler.cursedGigantificationPlayers.clear();
        WildLifeTriviaHandler.cursedHeartPlayers.clear();
        WildLifeTriviaHandler.cursedMoonJumpPlayers.clear();
        super.deactivate();
    }

    public void trySpawnBots() {
        int currentTick = currentSession.getPassedTime().getTicks();
        int sessionStart = activatedAt.getTicks();
        int sessionEnd = currentSession.getSessionLength().getTicks() - 6000; // Don't spawn bots 5 minutes before the end
        int availableTime = sessionEnd - sessionStart;

        List<ServerPlayer> players = livesManager.getAlivePlayers();
        if (players.isEmpty()) return;
        if (isBuffed()) Collections.shuffle(players);

        int numPlayers = players.size();
        int desiredTotalSpawns = numPlayers * getBotsPerPlayer();

        if (desiredTotalSpawns == 0) return;

        int interval = availableTime / desiredTotalSpawns;
        if (numPlayers * interval < MIN_BOT_DELAY) {
            interval = MIN_BOT_DELAY / numPlayers;
        }

        int maxSpawns = Math.min(desiredTotalSpawns, availableTime / interval);

        for (ServerPlayer player : players) {
            UUID uuid = player.getUUID();
            if (!playerSpawnQueue.containsKey(uuid)) {
                playerSpawnQueue.put(uuid, new LinkedList<>());
                globalScheduleInitialized = false;
            }
        }

        if (!globalScheduleInitialized) {
            playerSpawnQueue.values().forEach(Collection::clear);
            for (int i = 0; i < maxSpawns; i++) {
                int spawnTime = sessionStart + 100 + i * interval;
                ServerPlayer assignedPlayer = players.get(i % numPlayers);
                UUID uuid = assignedPlayer.getUUID();
                if (spawnTime > currentTick) {
                    playerSpawnQueue.get(uuid).offer(spawnTime);
                }
            }
            globalScheduleInitialized = true;
        }

        for (ServerPlayer player : players) {
            UUID uuid = player.getUUID();
            Queue<Integer> queue = playerSpawnQueue.get(uuid);
            if (queue != null && !queue.isEmpty()) {
                if (currentTick >= queue.peek()) {
                    queue.poll();
                    if (spawnedBotsFor.containsKey(player.getUUID())) {
                        spawnedBotsFor.put(player.getUUID(), 1+spawnedBotsFor.get(player.getUUID()));
                    }
                    else {
                        spawnedBotsFor.put(player.getUUID(), 1);
                    }
                    if (spawnedBotsFor.get(player.getUUID()) <= getBotsPerPlayer()) {
                        spawnBotFor(player);
                    }
                }
            }
        }
    }

    public void updateDeadBots() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            UUID playerUUID = player.getUUID();
            if (bots.containsKey(playerUUID)) {
                TriviaBot bot = bots.get(playerUUID);
                if (bot == null || !bot.isAlive()) {
                    bots.remove(playerUUID);
                }
            }
        }
    }

    public static void reload() {
        resetQueue();
    }

    public static void resetQueue() {
        easyTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        normalTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        hardTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");
        globalScheduleInitialized = false;
        playerSpawnQueue.clear();
    }

    public static void handleAnswer(ServerPlayer player, int answer) {
        if (bots.containsKey(player.getUUID())) {
            TriviaBot bot = bots.get(player.getUUID());
            if (bot.isAlive()) {
                bot.triviaHandler.handleAnswer(answer);
            }
        }
    }

    public static void spawnBotFor(ServerPlayer player) {
        spawnBotFor(player, TriviaBotPathfinding.getBlockPosNearPlayer(player, player.blockPosition().offset(0,50,0), 10));
    }
    public static void spawnBotFor(ServerPlayer player, BlockPos pos) {
        resetPlayerOnBotSpawn(player);
        TriviaBot bot = LevelUtils.spawnEntity(MobRegistry.TRIVIA_BOT, player.ls$getServerLevel(), pos);
        if (bot != null) {
            SessionTranscript.newTriviaBot(player);
            bot.serverData.setBoundPlayer(player);
            bots.put(player.getUUID(), bot);
            player.ls$playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 0.5f, 1);
            SimplePackets.FAKE_THUNDER.target(player).sendToClient(7);
            DatapackIntegration.EVENT_TRIVIA_BOT_SPAWN.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
            ));
        }
    }

    public static void resetPlayerOnBotSpawn(ServerPlayer player) {
        if (bots.containsKey(player.getUUID())) {
            TriviaBot bot = bots.get(player.getUUID());
            if (bot.isAlive()) {
                bot.serverData.despawn();
            }
        }
        killTriviaSnailFor(player);

        resetPlayerPunishments(player);

        SimplePackets.RESET_TRIVIA.target(player).sendToClient();
    }

    public static void resetPlayerPunishments(ServerPlayer player) {
        if (WildLifeTriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) {
            WildLifeTriviaHandler.cursedGigantificationPlayers.remove(player.getUUID());
            //? if > 1.20.3 {
            SizeShifting.setPlayerSize(player, 1);
            //?}
        }
        if (WildLifeTriviaHandler.cursedHeartPlayers.contains(player.getUUID())) {
            WildLifeTriviaHandler.cursedHeartPlayers.remove(player.getUUID());
            AttributeUtils.resetMaxPlayerHealthIfNecessary(player);
        }
        if (WildLifeTriviaHandler.cursedMoonJumpPlayers.contains(player.getUUID())) {
            WildLifeTriviaHandler.cursedMoonJumpPlayers.remove(player.getUUID());
            AttributeUtils.resetPlayerJumpHeight(player);
        }

        WildLifeTriviaHandler.cursedSliding.remove(player.getUUID());
        WildLifeTriviaHandler.cursedRoboticVoicePlayers.remove(player.getUUID());
        SimplePackets.CURSE_SLIDING.target(player).sendToClient(0);
    }

    public static void killAllBots() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof TriviaBot) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
        SimplePackets.RESET_TRIVIA.sendToClient();
    }

    public static void killAllTriviaSnails() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Snail snail) {
                    if (snail.isFromTrivia()) {
                        toKill.add(entity);
                    }
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void killTriviaSnailFor(ServerPlayer player) {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Snail snail) {
                    if (snail.isFromTrivia()) {
                        UUID boundPlayer = snail.serverData.getBoundPlayerUUID();
                        if (boundPlayer == null || boundPlayer.equals(player.getUUID())) {
                            toKill.add(entity);
                        }
                    }
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static Tuple<Integer, TriviaQuestion> getTriviaQuestion(ServerPlayer player) {
        if (preAssignedTrivia.containsKey(player.getUUID())) {
            var assigned = preAssignedTrivia.get(player.getUUID());
            preAssignedTrivia.remove(player.getUUID());
            return assigned;
        }

        int difficulty = 1 + player.getRandom().nextInt(3);
        try {
            if (difficulty == 1) {
                return new Tuple<>(difficulty, getEasyQuestion());
            }
            if (difficulty == 2) {
                return new Tuple<>(difficulty, getNormalQuestion());
            }
            return new Tuple<>(difficulty, getHardQuestion());
        } catch(Exception e) {
            LOGGER.error(e.toString());
            return new Tuple<>(difficulty, TriviaQuestion.getDefault());
        }
    }

    private static List<String> usedEasyQuestions = new ArrayList<>();
    public static TriviaQuestion getEasyQuestion() throws IOException {
        if (easyTrivia == null) {
            easyTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        }
        List<TriviaQuestion> unusedQuestions = new ArrayList<>();
        for (TriviaQuestion trivia : easyTrivia.getTriviaQuestions()) {
            if (usedEasyQuestions.contains(trivia.getQuestion())) continue;
            unusedQuestions.add(trivia);
        }
        if (unusedQuestions.isEmpty()) {
            usedEasyQuestions.clear();
            unusedQuestions = easyTrivia.getTriviaQuestions();
        }
        if (unusedQuestions.isEmpty()) return TriviaQuestion.getDefault();
        TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
        usedEasyQuestions.add(result.getQuestion());
        return result;
    }

    private static List<String> usedNormalQuestions = new ArrayList<>();
    public static TriviaQuestion getNormalQuestion() throws IOException {
        if (normalTrivia == null) {
            normalTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        }
        List<TriviaQuestion> unusedQuestions = new ArrayList<>();
        for (TriviaQuestion trivia : normalTrivia.getTriviaQuestions()) {
            if (usedNormalQuestions.contains(trivia.getQuestion())) continue;
            unusedQuestions.add(trivia);
        }
        if (unusedQuestions.isEmpty()) {
            usedNormalQuestions.clear();
            unusedQuestions = normalTrivia.getTriviaQuestions();
        }
        if (unusedQuestions.isEmpty()) return TriviaQuestion.getDefault();
        TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
        usedNormalQuestions.add(result.getQuestion());
        return result;
    }

    private static List<String> usedHardQuestions = new ArrayList<>();
    public static TriviaQuestion getHardQuestion() throws IOException {
        if (hardTrivia == null) {
            hardTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");
        }
        List<TriviaQuestion> unusedQuestions = new ArrayList<>();
        for (TriviaQuestion trivia : hardTrivia.getTriviaQuestions()) {
            if (usedHardQuestions.contains(trivia.getQuestion())) continue;
            unusedQuestions.add(trivia);
        }
        if (unusedQuestions.isEmpty()) {
            usedHardQuestions.clear();
            unusedQuestions = hardTrivia.getTriviaQuestions();
        }
        if (unusedQuestions.isEmpty()) return TriviaQuestion.getDefault();
        TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
        usedHardQuestions.add(result.getQuestion());
        return result;
    }

    public static int getBotsPerPlayer() {
        if (isBuffed()) return TRIVIA_BOTS_PER_PLAYER*2;
        return TRIVIA_BOTS_PER_PLAYER;
    }

    public static boolean isBuffed() {
        return Wildcard.isFinale();
    }
}
