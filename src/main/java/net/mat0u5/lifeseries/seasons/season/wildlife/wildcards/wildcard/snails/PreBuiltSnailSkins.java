package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerScoreEntry;

import java.io.File;
import java.util.*;

public class PreBuiltSnailSkins {
	public static final List<String> prebuiltSkins = List.of("BdoubleO100", "Bigbst4tz2", "Etho", "GeminiTay", "GoodTimeWithScar", "Grian", "impulseSV", "InTheLittleWood", "LDShadowLady", "Mumbo", "PearlescentMoon", "Renthedog", "Skizzleman", "Smajor1995", "Smallishbeans", "SolidarityGaming", "TangoTek", "ZombieCleo");
	public static final String SCOREBOARD_NAME = "PresetSnailSkins";
	private static final Random rnd = new Random();
	public static boolean SNAILS_RANDOM_PRESET_SKIN = true;

	public static void initialize() {
		ScoreboardUtils.createObjective(SCOREBOARD_NAME);
	}

	public static boolean isValidPrebuiltSkin(String name) {
		if (name == null) return false;
		return prebuiltSkins.contains(name);
	}

	public static void setPrebuiltSkin(ServerPlayer player, String name) {
		if (name == null) return;
		int index = prebuiltSkins.indexOf(name);
		if (index == -1) return;
		ScoreboardUtils.setScore(player, SCOREBOARD_NAME, index);
	}

	public static void setPrebuiltSkin(ServerPlayer player, int index) {
		if (index < 0 || index >= prebuiltSkins.size()) return;
		ScoreboardUtils.setScore(player, SCOREBOARD_NAME, index);
	}

	public static void randomPrebuiltSkin(ServerPlayer player) {
		ScoreboardUtils.setScore(player, SCOREBOARD_NAME, rnd.nextInt(prebuiltSkins.size()));
	}

	public static void resetPrebuiltSkin(ServerPlayer player) {
		ScoreboardUtils.setScore(player, SCOREBOARD_NAME, -1);
	}

	public static boolean hasPrebuiltSkinOrReset(ServerPlayer player) {
		return ScoreboardUtils.getScore(player, SCOREBOARD_NAME) != null;
	}

	public static void onPlayerJoin(ServerPlayer player) {
		if (SNAILS_RANDOM_PRESET_SKIN && !hasPrebuiltSkinOrReset(player)) {
			assignNextAvailableSnailPreset(player);
			TaskScheduler.scheduleTask(1, PreBuiltSnailSkins::sendPrebuiltSkins);
		}
	}

	public static void assignNextAvailableSnailPreset(ServerPlayer player) {
		Map<Integer, Integer> skins = new HashMap<>();
		for (int i = 0; i < prebuiltSkins.size(); i++) {
			skins.put(i, 0);
		}

		//? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
			int skinIndex = entry.getScore();
        *///?} else {
		for (PlayerScoreEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
			int skinIndex = entry.value();
		//?}
			if (skins.containsKey(skinIndex)) {
				skins.put(skinIndex, skins.get(skinIndex)+1);
			}
		}
		int minValue = Collections.min(skins.values());
		List<Integer> minKeys = skins.entrySet().stream()
				.filter(entry -> entry.getValue() == minValue)
				.map(Map.Entry::getKey)
				.toList();

		Integer randomSkin = minKeys.get(rnd.nextInt(minKeys.size()));
		if (randomSkin == null) randomSkin = 0;

		setPrebuiltSkin(player, randomSkin);
	}

	public static void sendPrebuiltSkins() {
		sendPrebuiltSkinsTo(PlayerUtils.getAllPlayers());
	}

	public static void sendPrebuiltSkinsTo(List<ServerPlayer> players) {
		List<String> skins = new ArrayList<>();

		//? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(LivesManageSCOREBOARD_NAME)) {
            String name = entry.getOwner();
            int skinIndex = entry.getScore();
        *///?} else {
		for (PlayerScoreEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
			String name = entry.owner();
			int skinIndex = entry.value();
		//?}
			if (skinIndex >= 0 && skinIndex < prebuiltSkins.size()) {
				skins.add(name+":"+prebuiltSkins.get(skinIndex));
			}
		}
		SimplePackets.PREBUILT_SNAILSKINS.sendToClient(skins, players);
	}

	public static void copyConfigFiles(ResourceHandler handler) {
		// Builtin skins
		File folder = new File("./config/lifeseries/wildlife/snailskins/builtin");
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				LifeSeries.LOGGER.error("Failed to create folder {}", folder);
				return;
			}
		}
		for (String skinName : prebuiltSkins) {
			handler.copyBundledSingleFile("/resourcepacks/lifeseries/assets/lifeseries/textures/entity/snail/builtin/"+skinName.toLowerCase(Locale.ROOT)+".png", new File("./config/lifeseries/wildlife/snailskins/builtin/"+skinName+".png").toPath(), true);
		}
		handler.copyBundledSingleFile("/resourcepacks/lifeseries/assets/lifeseries/textures/entity/snail/builtin/mat0u5.png", new File("./config/lifeseries/wildlife/snailskins/builtin/Mat0u5.png").toPath(), true);
	}

}
