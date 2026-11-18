package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.StringListConfig;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.utils.other.Tuple;
import net.mat0u5.matlib.utils.player.PlayerReference;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.util.*;

import static net.mat0u5.matlib.MatLib.server;

public class InBetweenTriviaManager {
	public static final ConfigRef<Boolean> TRIVIA_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().IN_BETWEEN_TRIVIA);
	public static final ConfigRef<Integer> DEATH_MIN = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_DEATH_NUM_MIN);
	public static final ConfigRef<Integer> DEATH_MAX = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_DEATH_NUM_MAX);
	public static final ConfigRef<Integer> QUESTION_TIME = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_QUESTION_TIME);
	public static Map<UUID, TriviaBot> bots = new HashMap<>();
	private static final Random rnd = new Random();
	public static Map<UUID, TriviaQuestion> preAssignedTrivia = new HashMap<>();
	public static TriviaQuestionManager triviaQuestions;
	private static StringListConfig usedQuestions;

	public static boolean isEnabled()  {
		return InBetweenManager.ENABLED.get() && TRIVIA_ENABLED.get();
	}

	public static boolean hasTrivia(int deathNumber)  {
		return isEnabled() && deathNumber >= DEATH_MIN.get() && deathNumber <= DEATH_MAX.get();
	}

	public static void initialize() {
		triviaQuestions = new TriviaQuestionManager("./config/lifeseries/finallife","trivia.json");
		getUsedQuestions();
	}

	public static void tick(ServerPlayer player, InBetweenInstance inBetween) {
		if (player == null || inBetween == null) return;
		int deathNum = inBetween.deathNumber;
		if (inBetween.spawnedTrivia || !hasTrivia(deathNum)) return;
		Vec3 spawnPos = inBetween.inBetweenBuilder.triviaSpawnPos;
		if (spawnPos == null) return;
		if (player.position().distanceTo(spawnPos) <= 20) {//TODO maybe tweak range
			inBetween.spawnedTrivia = true;
			startBotSpawn(player, inBetween, BlockPos.containing(spawnPos.add(0, 25, 0)));
		}
	}

	public static void onInstanceDiscarded(UUID uuid) {
		bots.remove(uuid); // The bot despawns in its next despawn check
	}

	public static void handleAnswer(ServerPlayer player, int answer) {
		if (bots.containsKey(player.getUUID())) {
			TriviaBot bot = bots.get(player.getUUID());
			if (bot.isAlive()) {
				bot.triviaHandler.handleAnswer(answer);
			}
		}
	}

	public static void startBotSpawn(ServerPlayer playerOld, InBetweenInstance inBetween, BlockPos pos) {
		ModifiableSound.FINALLIFE_TRIVIA_INTRO.play(playerOld, 0.7f, 1);
		PlayerReference ref = PlayerReference.of(playerOld);
		TaskScheduler.schedulePriorityTask(309, () -> {
			ServerPlayer player = ref.get();
			if (player == null) return;
			if (InBetweenManager.getInstance(player) != inBetween) {
				if (!InBetweenManager.isInInBetween(player)) SimplePackets.STOP_TRIVIA_SOUNDS.sendToClient(player);
				return;
			}
			spawnBotFor(player, pos, inBetween.inBetweenBuilder.spawnPos);
		});
	}

	public static void spawnBotFor(ServerPlayer player, BlockPos pos, Vec3 faceTowards) {
		SimplePackets.RESET_TRIVIA.sendToClient(player);
		ServerLevel level = ((IPlayer) player).ls$getServerLevel();
		TriviaBot bot = LevelUtils.spawnEntity(MobRegistry.TRIVIA_BOT, level, pos);
		if (bot != null) {
			faceTowards(bot, faceTowards);
			SessionTranscript.newTriviaBot(player);
			bot.serverData.setBoundPlayer(player);
			bots.put(player.getUUID(), bot);
			DatapackIntegration.EVENT_TRIVIA_BOT_SPAWN.trigger(List.of(
					new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
					new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
			));
		}
	}

	private static void faceTowards(TriviaBot bot, Vec3 target) {
		double dx = target.x - bot.getX();
		double dz = target.z - bot.getZ();
		if (dx * dx + dz * dz < 0.0001) return;
		float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
		bot.setYRot(yaw);
		bot.yRotO = yaw;
		bot.setYBodyRot(yaw);
		bot.yBodyRotO = yaw;
		bot.setYHeadRot(yaw);
		bot.yHeadRotO = yaw;
	}

	private static StringListConfig getUsedQuestions() {
		if (usedQuestions == null) {
			usedQuestions = new StringListConfig("lifeseries/main", "DO_NOT_MODIFY_finallife_used_trivia.properties");
		}
		return usedQuestions;
	}

	public static void resetUsedQuestions() {
		getUsedQuestions().save(List.of());
	}

	public static void killAllBots() {
		SimplePackets.STOP_TRIVIA_SOUNDS.sendToAllClients();
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
		SimplePackets.RESET_TRIVIA.sendToAllClients();
	}

	public static Tuple<Integer, TriviaQuestion> getTriviaQuestion(ServerPlayer player) {
		if (preAssignedTrivia.containsKey(player.getUUID())) {
			var assigned = preAssignedTrivia.get(player.getUUID());
			preAssignedTrivia.remove(player.getUUID());
			return Tuple.of(1, assigned);
		}

		try {
			return Tuple.of(1, getQuestion(player.getScoreboardName()));
		} catch(Exception e) {
			LifeSeries.LOGGER.error(e.toString());
			return Tuple.of(1, TriviaQuestion.getDefault());
		}
	}


	public static TriviaQuestion getQuestion(String playerName) throws IOException {
		if (triviaQuestions == null) {
			triviaQuestions = new TriviaQuestionManager("./config/lifeseries/finallife","trivia.json");
		}
		return triviaQuestions.getRandomQuestion(getUsedQuestions(), rnd, playerName);
	}

}
