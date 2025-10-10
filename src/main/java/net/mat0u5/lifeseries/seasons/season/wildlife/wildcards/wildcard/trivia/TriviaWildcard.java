package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaBotPathfinding;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.SizeShifting;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class TriviaWildcard extends Wildcard {
    private static final Map<UUID, Queue<Integer>> playerSpawnQueue = new HashMap<>();
    private static final Map<UUID, Integer> spawnedBotsFor = new HashMap<>();
    public static final Map<UUID, Snail> snails = new HashMap<>();
    private static boolean globalScheduleInitialized = false;
    public static Map<UUID, TriviaBot> bots = new HashMap<>();
    public static int activatedAt = -1;
    public static int TRIVIA_BOTS_PER_PLAYER = 5;
    public static int MIN_BOT_DELAY = 8400;
    public static TriviaQuestionManager easyTrivia;
    public static TriviaQuestionManager normalTrivia;
    public static TriviaQuestionManager hardTrivia;
    private static final Random rnd = new Random();
    public static long ticks = 0;

    @Override
    public Wildcards getType() {
        return Wildcards.TRIVIA;
    }

    @Override
    public void tickSessionOn() {
        int passedTime = (int) ((float) currentSession.passedTime - activatedAt);
        if (passedTime % 20 == 0) trySpawnBots();
        if (passedTime % 200 == 0) updateDeadBots();
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks % 200 == 0) {
            for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
                UUID playerUUID = player.getUuid();
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
        activatedAt = (int) currentSession.passedTime;
        bots.clear();
        TriviaHandler.cursedGigantificationPlayers.clear();
        TriviaHandler.cursedHeartPlayers.clear();
        TriviaHandler.cursedMoonJumpPlayers.clear();
        if (!currentSession.statusStarted()) {
            PlayerUtils.broadcastMessageToAdmins(Text.of("§7You must start a session for trivia bots to spawn!"));
        }
        PlayerUtils.broadcastMessageToAdmins(Text.of("§7You can modify the trivia questions in the config files (./config/lifeseries/wildlife/*-trivia)"));
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
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            TriviaWildcard.resetPlayerOnBotSpawn(player);
        }
        TriviaHandler.cursedGigantificationPlayers.clear();
        TriviaHandler.cursedHeartPlayers.clear();
        TriviaHandler.cursedMoonJumpPlayers.clear();
        super.deactivate();
    }

    public void trySpawnBots() {
        int currentTick = (int) currentSession.passedTime;
        int sessionStart = activatedAt;
        int sessionEnd = currentSession.sessionLength - 6000; // Don't spawn bots 5 minutes before the end
        int availableTime = sessionEnd - sessionStart;

        List<ServerPlayerEntity> players = livesManager.getAlivePlayers();
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

        for (ServerPlayerEntity player : players) {
            UUID uuid = player.getUuid();
            if (!playerSpawnQueue.containsKey(uuid)) {
                playerSpawnQueue.put(uuid, new LinkedList<>());
                globalScheduleInitialized = false;
            }
        }

        if (!globalScheduleInitialized) {
            playerSpawnQueue.values().forEach(Collection::clear);
            for (int i = 0; i < maxSpawns; i++) {
                int spawnTime = sessionStart + 100 + i * interval;
                ServerPlayerEntity assignedPlayer = players.get(i % numPlayers);
                UUID uuid = assignedPlayer.getUuid();
                if (spawnTime > currentTick) {
                    playerSpawnQueue.get(uuid).offer(spawnTime);
                }
            }
            globalScheduleInitialized = true;
        }

        for (ServerPlayerEntity player : players) {
            UUID uuid = player.getUuid();
            Queue<Integer> queue = playerSpawnQueue.get(uuid);
            if (queue != null && !queue.isEmpty()) {
                if (currentTick >= queue.peek()) {
                    queue.poll();
                    if (spawnedBotsFor.containsKey(player.getUuid())) {
                        spawnedBotsFor.put(player.getUuid(), 1+spawnedBotsFor.get(player.getUuid()));
                    }
                    else {
                        spawnedBotsFor.put(player.getUuid(), 1);
                    }
                    if (spawnedBotsFor.get(player.getUuid()) <= getBotsPerPlayer()) {
                        spawnBotFor(player);
                    }
                }
            }
        }
    }

    public void updateDeadBots() {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            UUID playerUUID = player.getUuid();
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

    public static void handleAnswer(ServerPlayerEntity player, int answer) {
        if (bots.containsKey(player.getUuid())) {
            TriviaBot bot = bots.get(player.getUuid());
            if (bot.isAlive()) {
                bot.triviaHandler.handleAnswer(answer);
            }
        }
    }

    public static void spawnBotFor(ServerPlayerEntity player) {
        spawnBotFor(player, TriviaBotPathfinding.getBlockPosNearTarget(player, player.getBlockPos().add(0,50,0), 10));
    }
    public static void spawnBotFor(ServerPlayerEntity player, BlockPos pos) {
        resetPlayerOnBotSpawn(player);
        //TODO
        TriviaBot bot = null;
        //TriviaBot bot = MobRegistry.TRIVIA_BOT.spawn(PlayerUtils.getServerWorld(player), pos, SpawnReason.COMMAND);
        if (bot != null) {
            SessionTranscript.newTriviaBot(player);
            bot.serverData.setBoundPlayer(player);
            bots.put(player.getUuid(), bot);
            player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 0.5f, 1);
            NetworkHandlerServer.sendNumberPacket(player, PacketNames.FAKE_THUNDER, 7);
        }
    }

    public static void resetPlayerOnBotSpawn(ServerPlayerEntity player) {
        if (bots.containsKey(player.getUuid())) {
            TriviaBot bot = bots.get(player.getUuid());
            if (bot.isAlive()) {
                bot.serverData.despawn();
            }
        }
        killTriviaSnailFor(player);

        if (TriviaHandler.cursedGigantificationPlayers.contains(player.getUuid())) {
            TriviaHandler.cursedGigantificationPlayers.remove(player.getUuid());
            SizeShifting.setPlayerSize(player, 1);
        }
        if (TriviaHandler.cursedHeartPlayers.contains(player.getUuid())) {
            TriviaHandler.cursedHeartPlayers.remove(player.getUuid());
            AttributeUtils.resetMaxPlayerHealthIfNecessary(player);
        }
        if (TriviaHandler.cursedMoonJumpPlayers.contains(player.getUuid())) {
            TriviaHandler.cursedMoonJumpPlayers.remove(player.getUuid());
            AttributeUtils.resetPlayerJumpHeight(player);
        }

        TriviaHandler.cursedSliding.remove(player.getUuid());
        TriviaHandler.cursedRoboticVoicePlayers.remove(player.getUuid());
        NetworkHandlerServer.sendLongPacket(player, PacketNames.CURSE_SLIDING, 0);

        NetworkHandlerServer.sendStringPacket(player, PacketNames.RESET_TRIVIA, "true");
    }

    public static void killAllBots() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof TriviaBot) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            NetworkHandlerServer.sendStringPacket(player, PacketNames.RESET_TRIVIA, "true");
        }
    }

    public static void killAllTriviaSnails() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof Snail snail) {
                    if (snail.isFromTrivia()) {
                        toKill.add(entity);
                    }
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void killTriviaSnailFor(ServerPlayerEntity player) {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof Snail snail) {
                    if (snail.isFromTrivia()) {
                        UUID boundPlayer = snail.serverData.boundPlayerUUID;
                        if (boundPlayer == null || boundPlayer.equals(player.getUuid())) {
                            toKill.add(entity);
                        }
                    }
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static TriviaQuestion getTriviaQuestion(int difficulty) {
        try {
            if (difficulty == 1) {
                return getEasyQuestion();
            }
            if (difficulty == 2) {
                return getNormalQuestion();
            }
            return getHardQuestion();
        } catch(Exception e) {
            LOGGER.error(e.toString());
            return TriviaQuestion.getDefault();
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
        return WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
    }
}
