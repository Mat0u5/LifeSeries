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
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.utils.other.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

import static net.mat0u5.matlib.MatLib.server;

public class InBetweenTriviaManager {
	public static final ConfigRef<Boolean> TRIVIA_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().IN_BETWEEN_TRIVIA);
	public static final ConfigRef<Integer> DEATH_MIN = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_DEATH_NUM_MIN);
	public static final ConfigRef<Integer> DEATH_MAX = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_DEATH_NUM_MAX);
	public static final ConfigRef<Integer> QUESTION_TIME = ConfigRef.of(() -> FinalLifeConfig.get().TRIVIA_QUESTION_TIME);
	//Timestamps
	public static final int INTRO_DELAY = 130; // After entering the In-Between
	public static final double SPAWN_AHEAD = 20; // Blocks on the path, that distance from the player
	public static final int LIGHTS_ON = 220;
	public static final int FIREWORK_BURST = 320;
	public static final int BOT_SPAWN = 300;
	public static final int SPOTLIGHT_BOT_TRACK = BOT_SPAWN + 40;
	public static final int FIREWORK_LARGE_BALL = 360;
	public static final int FIREWORK_FACE = 415;
	public static final int BOT_DROP_HEIGHT = 31;
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
		if (inBetween.triviaTicks >= FIREWORK_FACE || !hasTrivia(inBetween.deathNumber)) return;
		if (inBetween.triviaTicks < 0) {
			if (inBetween.age < INTRO_DELAY) return;
			ModifiableSound.FINALLIFE_TRIVIA_INTRO.play(player, 0.7f, 1);
		}
		inBetween.triviaTicks++;
		if (inBetween.triviaTicks == LIGHTS_ON) inBetween.triviaPos = pickSpawnPos(player, inBetween);
		Vec3 spawnPos = inBetween.triviaPos;
		if (spawnPos == null) return;
		float yaw = getSceneYaw(spawnPos, player, inBetween);
		switch (inBetween.triviaTicks) {
			case LIGHTS_ON -> sendPacketInfo(player, "lights", String.valueOf(spawnPos.x), String.valueOf(spawnPos.y), String.valueOf(spawnPos.z), String.valueOf(yaw));
			case FIREWORK_BURST -> sendPacketInfo(player, "firework", "burst");
			case BOT_SPAWN -> {
				spawnBotFor(player, BlockPos.containing(spawnPos.add(0, BOT_DROP_HEIGHT, 0)), yaw);
				sendPacketInfo(player, "firework", "small_ball");
			}
			case SPOTLIGHT_BOT_TRACK -> {
				TriviaBot bot = bots.get(player.getUUID());
				if (bot != null && bot.isAlive()) sendPacketInfo(player, "track", String.valueOf(bot.getId()));
			}
			case FIREWORK_LARGE_BALL -> sendPacketInfo(player, "firework", "large_ball");
			case FIREWORK_FACE -> sendPacketInfo(player, "firework", "face");
		}
	}

	public static void sendPacketInfo(ServerPlayer player, String... values) {
		SimplePackets.IN_BETWEEN_TRIVIA.sendToClient(List.of(values), player);
	}

	private static Vec3 pickSpawnPos(ServerPlayer player, InBetweenInstance inBetween) {
		Vec3 pos = InBetweenTriviaPath.getPointAhead(inBetween.inBetweenBuilder.pathPoints, player.position(), SPAWN_AHEAD);
		if (pos != null) return pos;
		double radians = Math.toRadians(player.getYRot());
		return player.position().add(-Math.sin(radians) * SPAWN_AHEAD, 0, Math.cos(radians) * SPAWN_AHEAD);
	}

	private static float getSceneYaw(Vec3 triviaPos, ServerPlayer player, InBetweenInstance inBetween) {
		Vec3 behind = InBetweenTriviaPath.getPointAhead(inBetween.inBetweenBuilder.pathPoints, triviaPos, -2);
		if (behind == null || behind.distanceToSqr(triviaPos) < 0.01) behind = player.position();
		double dx = behind.x - triviaPos.x;
		double dz = behind.z - triviaPos.z;
		if (dx * dx + dz * dz < 0.0001) return inBetween.getSpawnYaw() + 180;
		return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
	}

	public static void onInstanceDiscarded(UUID uuid) {
		bots.remove(uuid);
	}

	public static void onInstanceDestroyed(InBetweenInstance inBetween) {
		if (inBetween.triviaTicks < 0) return;
		ServerPlayer player = PlayerUtils.getPlayer(inBetween.playerUUID);
		if (player == null) return;
		if (inBetween.triviaTicks < BOT_SPAWN) SimplePackets.STOP_TRIVIA_SOUNDS.sendToClient(player);
		sendPacketInfo(player, "stop");
	}

	public static void handleAnswer(ServerPlayer player, int answer) {
		if (bots.containsKey(player.getUUID())) {
			TriviaBot bot = bots.get(player.getUUID());
			if (bot.isAlive()) {
				bot.triviaHandler.handleAnswer(answer);
			}
		}
	}

	@Nullable
	public static TriviaBot spawnBotFor(ServerPlayer player, BlockPos pos, float yaw) {
		SimplePackets.RESET_TRIVIA.sendToClient(player);
		ServerLevel level = ((IPlayer) player).ls$getServerLevel();
		//? if <= 1.20.3 {
		//TriviaBot bot = MobRegistry.TRIVIA_BOT.spawn(level, null, created -> prepareBot(created, yaw), pos, EntitySpawnReason.COMMAND, false, false);
		//?} else {
		TriviaBot bot = MobRegistry.TRIVIA_BOT.spawn(level, created -> prepareBot(created, yaw), pos, EntitySpawnReason.COMMAND, false, false);
		//?}
		if (bot != null) {
			SessionTranscript.newTriviaBot(player);
			bot.serverData.setBoundPlayer(player);
			bots.put(player.getUUID(), bot);
			DatapackIntegration.EVENT_TRIVIA_BOT_SPAWN.trigger(List.of(
					new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
					new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
			));
		}
		return bot;
	}

	private static void prepareBot(TriviaBot bot, float yaw) {
		setYaw(bot, yaw);
		bot.setGliding(true);
	}

	private static void setYaw(TriviaBot bot, float yaw) {
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
