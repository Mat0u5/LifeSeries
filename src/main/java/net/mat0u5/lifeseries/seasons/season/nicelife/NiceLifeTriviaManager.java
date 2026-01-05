package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class NiceLifeTriviaManager {
    public static Map<UUID, TriviaBot> bots = new HashMap<>();
    public static TriviaQuestionManager triviaQuestions;
    private static List<String> usedQuestions = new ArrayList<>();
    public static boolean triviaInProgress = false;
    public static boolean firstTriviaInSession = true;
    public static TriviaQuestion currentQuestion = TriviaQuestion.getDefault();
    public static Random rnd = new Random();
    public static int QUESTION_TIME = 68;
    public static boolean CAN_BREAK_BEDS = true;
    public static boolean BREAKING_DROPS_RESOURCES = true;
    public static List<TriviaSpawn> triviaSpawns = new ArrayList<>();
    public static List<UUID> triviaPlayersUUID = new ArrayList<>();
    public static boolean preparingForSpawn = false;
    public static List<UUID> correctAnswers = new ArrayList<>();
    public static List<UUID> incorrectAnswers = new ArrayList<>();

    public static void initialize() {

    }

    public static void startTrivia(List<ServerPlayer> triviaPlayers) {
        triviaPlayersUUID.clear();
        for (ServerPlayer player : triviaPlayers) {
            triviaPlayersUUID.add(player.getUUID());
        }
        correctAnswers.clear();
        incorrectAnswers.clear();
        if (CompatibilityManager.voicechatLoaded()) {
            VoicechatMain.niceLifeTriviaStart(triviaPlayers);
        }

        NetworkHandlerServer.sendUpdatePackets();
        triviaInProgress = true;
        killAllBots();
        usedQuestions.clear();
        NiceLifeVotingManager.reset();
        triviaQuestions = new TriviaQuestionManager("./config/lifeseries/nicelife","trivia.json");
        triviaSpawns.clear();
        currentQuestion = getQuestion();
        NiceLifeVotingManager.chooseVote();
        for (ServerPlayer player : triviaPlayers) {
            NetworkHandlerServer.sendStringPacket(player, PacketNames.HIDE_SLEEP_DARKNESS, "true");
            NetworkHandlerServer.sendStringPacket(player, PacketNames.EMPTY_SCREEN, "true");
            BlockPos bedPos = player.getSleepingPos().orElse(null);
            if (bedPos == null) {
                continue;
            }

            ServerLevel level = player.ls$getServerLevel();
            BlockState bedState = level.getBlockState(bedPos);

            if (bedState.getBlock() instanceof BedBlock) {
                Direction bedDirection = BedBlock.getConnectedDirection(bedState);

                BlockPos headPos = bedPos.relative(bedDirection);
                BlockPos frontBedPos = headPos.relative(bedDirection);
                BlockPos spawnBotPos = frontBedPos;
                for (int i = 0; i <= 6; i++) {
                    BlockPos newPos = frontBedPos.relative(bedDirection, i);
                    if (!level.getBlockState(newPos.below()).isFaceSturdy(level, newPos, Direction.UP)) {
                        break;
                    }
                    spawnBotPos = newPos;
                }

                triviaSpawns.add(new TriviaSpawn(player.getUUID(), spawnBotPos, frontBedPos, bedDirection));
            }
        }

        preparingForSpawn = true;
        if (firstTriviaInSession) {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_introduction_long"));
            PlayerUtils.playSoundToPlayers(triviaPlayers, sound, 1f, 1);
            breakBotSpawnBlocks(616-160);
            TaskScheduler.scheduleTask(616-160, () -> {
                spawnTriviaBots(160);
            });
        }
        else {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_introduction_short"));
            PlayerUtils.playSoundToPlayers(triviaPlayers, sound, 1f, 1);
            breakBotSpawnBlocks(71);
            TaskScheduler.scheduleTask(71, () -> {
                spawnTriviaBots(0);
            });
            //TaskScheduler.scheduleTask(71+90, () -> {
            //    SoundEvent sound2 = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_christmas_soundbyte"));
            //    PlayerUtils.playSoundToPlayers(triviaPlayers, sound2, 0.8f, 1);
            //});
        }
        firstTriviaInSession = false;
    }

    public static void endTrivia() {
        killAllBots();
        triviaInProgress = false;
        if (correctAnswers.isEmpty() && incorrectAnswers.isEmpty()) return;
        if (correctAnswers.isEmpty()) {
            TaskScheduler.scheduleTask(100, NiceLifeTriviaManager::allWrong);
        }
        else {
            NiceLifeVotingManager.endTriviaVoting();
        }
        if (CompatibilityManager.voicechatLoaded()) {
            VoicechatMain.niceLifeTick();
        }
    }

    public static void allWrong() {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_incorrect_all_wrong"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
        PlayerUtils.broadcastMessage(Component.literal("§f<§2§mTrivia§m§2 Santa Bot§f>§4 WRONG! WRONG! WRONG! ALL WRONG!"));
        NetworkHandlerServer.sendStringPackets(PacketNames.TRIVIA_ALL_WRONG, "");
        TaskScheduler.scheduleTask(120, () -> {
            PlayerUtils.broadcastMessage(Component.literal("§f<§2§mTrivia§m§2 Santa Bot§f>§4 SNOW MUST GO ON!"));
            for (ServerPlayer player : livesManager.getAlivePlayers()) {
                for (int i = 0; i < 3; i++) {
                    BlockPos pos = LevelUtils.getCloseBlockPos(player.ls$getServerLevel(), player.blockPosition(), 8, 2, true);
                    AngrySnowman snowman = LevelUtils.spawnEntity(MobRegistry.ANGRY_SNOWMAN, player.ls$getServerLevel(), pos);
                    if (snowman != null) {
                        snowman.setPumpkin(false);
                    }
                }
            }
        });
    }

    public static void breakBotSpawnBlocks(int overTicks) {
        for (TriviaSpawn triviaSpawnInfo : triviaSpawns) {
            ServerPlayer player = PlayerUtils.getPlayer(triviaSpawnInfo.uuid());
            if (player == null) continue;
            BlockPos spawnBotPos = triviaSpawnInfo.spawnPos().offset(0, 20, 0);
            ServerLevel level = player.ls$getServerLevel();
            //? if <= 1.21 {
            int maxY = level.getMaxBuildHeight();
            //?} else {
            /*int maxY = level.getMaxY();
             *///?}
            List<Integer> breakYPositions = new ArrayList<>();
            for (int breakY = spawnBotPos.getY(); breakY < maxY; breakY++) {
                BlockPos breakBlockPos = spawnBotPos.atY(breakY);
                BlockPos aboveBreakBlockPos = breakBlockPos.above();
                breakYPositions.add(breakY);
                if (level.getBlockState(breakBlockPos).isAir() && level.getBlockState(aboveBreakBlockPos).isAir()) {
                    breakYPositions.add(breakY+1);
                    break;
                }
            }
            Collections.reverse(breakYPositions);
            double delay = 0;
            double step = (double) overTicks / breakYPositions.size();
            for (int yPos : breakYPositions) {
                BlockPos breakBlockPos = spawnBotPos.atY(yPos);
                TaskScheduler.scheduleTask((int) delay, () -> {
                    breakBlocksAround(level, breakBlockPos, triviaSpawnInfo.bedPos.getY());
                });
                delay += step;
            }
        }
    }

    public static void spawnTriviaBots(int soundDelay) {
        for (TriviaSpawn triviaSpawnInfo : triviaSpawns) {
            ServerPlayer player = PlayerUtils.getPlayer(triviaSpawnInfo.uuid());
            if (player == null) continue;
            BlockPos spawnBotPos = triviaSpawnInfo.spawnPos().offset(0, 20, 0);
            TriviaBot bot = LevelUtils.spawnEntity(MobRegistry.TRIVIA_BOT, player.ls$getServerLevel(), spawnBotPos);
            if (bot != null) {
                bot.sounds.delay = soundDelay;
                SessionTranscript.newTriviaBot(player);
                bot.serverData.setBoundPlayer(player);
                bots.put(player.getUUID(), bot);
                DatapackIntegration.EVENT_TRIVIA_BOT_SPAWN.trigger(List.of(
                        new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                        new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
                ));
                if (bot.triviaHandler instanceof NiceLifeTriviaHandler triviaHandler) {
                    triviaHandler.spawnInfo = triviaSpawnInfo;
                }
            }
        }
        preparingForSpawn = false;
    }

    public static void breakBlocksAround(ServerLevel level, BlockPos pos, int bedYPos) {
        for (int dirX = -1; dirX <= 1; dirX++) {
            for (int dirZ = -1; dirZ <= 1; dirZ++) {
                BlockPos breakBlockPos = pos.offset(dirX, 0, dirZ);
                if ((breakBlockPos.getY() <= bedYPos || !CAN_BREAK_BEDS) && level.getBlockState(breakBlockPos).getBlock() instanceof BedBlock) {
                    continue;
                }
                if (level.getBlockState(breakBlockPos).getBlock().defaultDestroyTime() == -1) {
                    //Unbreakable blocks
                    continue;
                }
                level.destroyBlock(breakBlockPos, BREAKING_DROPS_RESOURCES);
            }
        }
    }

    public static void sessionStart() {
        firstTriviaInSession = true;
        killAllBots();
        triviaInProgress = false;
    }
    public static void sessionEnd() {
        killAllBots();
        triviaInProgress = false;
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
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            NetworkHandlerServer.sendStringPacket(player, PacketNames.RESET_TRIVIA, "true");
        }
    }
    public static void killAllSnowmen() {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof AngrySnowman) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void handleAnswer(ServerPlayer player, int answer) {
        if (bots.containsKey(player.getUUID())) {
            TriviaBot bot = bots.get(player.getUUID());
            if (bot.isAlive()) {
                bot.triviaHandler.handleAnswer(answer);
            }
        }
    }

    public static Tuple<Integer, TriviaQuestion> getTriviaQuestion(ServerPlayer player) {
        //TODO allow pre-assigning trivia with commands
        return new Tuple<>(1, currentQuestion);
    }

    public static TriviaQuestion getQuestion() {
        try {
            if (triviaQuestions == null) {
                triviaQuestions = new TriviaQuestionManager("./config/lifeseries/nicelife","trivia.json");
            }
            List<TriviaQuestion> unusedQuestions = new ArrayList<>();
            for (TriviaQuestion trivia : triviaQuestions.getTriviaQuestions()) {
                if (usedQuestions.contains(trivia.getQuestion())) continue;
                unusedQuestions.add(trivia);
            }
            if (unusedQuestions.isEmpty()) {
                usedQuestions.clear();
                unusedQuestions = triviaQuestions.getTriviaQuestions();
            }
            if (unusedQuestions.isEmpty()) return TriviaQuestion.getDefault();
            TriviaQuestion result = unusedQuestions.get(rnd.nextInt(unusedQuestions.size()));
            usedQuestions.add(result.getQuestion());
            return result;
        }catch(Exception ignored) {}
        return TriviaQuestion.getDefault();
    }

    public record TriviaSpawn(UUID uuid, BlockPos spawnPos, BlockPos bedPos, Direction bedDirection) {}
}
