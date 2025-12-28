package net.mat0u5.lifeseries.seasons.season.nicelife;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static net.mat0u5.lifeseries.Main.server;

public class NiceLifeTriviaManager {
    public static Map<UUID, TriviaBot> bots = new HashMap<>();
    public static TriviaQuestionManager triviaQuestions;
    private static List<String> usedQuestions = new ArrayList<>();
    public static boolean triviaInProgress = false;
    public static boolean firstTriviaInSession = true;
    public static TriviaQuestion currentQuestion = TriviaQuestion.getDefault();
    public static Random rnd = new Random();
    public static int QUESTION_TIME = 67;
    public static List<TriviaSpawn> triviaSpawns = new ArrayList<>();
    public static List<UUID> triviaPlayersUUID = new ArrayList<>();
    public static boolean preparingForSpawn = false;

    public static void initialize() {

    }

    public static void startTrivia(List<ServerPlayer> triviaPlayers) {
        triviaPlayersUUID.clear();
        for (ServerPlayer player : triviaPlayers) {
            triviaPlayersUUID.add(player.getUUID());
        }

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
            TaskScheduler.scheduleTask(612-160, () -> {
                spawnTriviaBots(160);
            });
        }
        else {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_santabot_introduction_short"));
            PlayerUtils.playSoundToPlayers(triviaPlayers, sound, 1f, 1);
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
    }

    public static void spawnTriviaBots(int soundDelay) {
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
            for (int breakY = spawnBotPos.getY(); breakY <= maxY; breakY++) {
                //TODO stop at an air pos.
                //TODO sequential block breaking
                BlockPos breakBlockPos = spawnBotPos.atY(breakY);
                breakBlocksAround(level, breakBlockPos, triviaSpawnInfo.bedPos.getY());
            }
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
                if (pos.getY() > bedYPos && level.getBlockState(breakBlockPos).getBlock() instanceof BedBlock) {
                    continue;
                }
                level.destroyBlock(breakBlockPos, true);
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
