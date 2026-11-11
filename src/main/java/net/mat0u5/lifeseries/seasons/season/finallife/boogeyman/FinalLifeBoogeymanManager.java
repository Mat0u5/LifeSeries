package net.mat0u5.lifeseries.seasons.season.finallife.boogeyman;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.LifeSeriesNetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLife;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.util.WatcherManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.LifeSkinsManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.events.EventResult;
import net.mat0u5.matlib.utils.other.Triple;
import net.mat0u5.matlib.utils.player.PlayerReference;
import net.mat0u5.matlib.utils.player.ScoreboardUtils;
import net.mat0u5.matlib.utils.player.TeamUtils;
import net.mat0u5.matlib.utils.resource.ResourceHandler;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import static net.mat0u5.lifeseries.LifeSeries.livesManager;

//? if <= 26.1 {
//import net.minecraft.ChatFormatting;
//?} else {
import net.minecraft.world.scores.TeamColor;
//?}

public class FinalLifeBoogeymanManager {
	public static final String SCOREBOARD_NAME = "finallife_boogeyman";
	public static final String TEAM_NAME = "finallife_boogeyman";
	public static final String DEFAULT_USERNAME = "The_Boogeyman";
	private static final int SCOREBOARD_STATUS_ALIVE = 1;
	private static final int SCOREBOARD_STATUS_DEAD = 2;
	public final ConfigRef<Boolean> BOOGEYMAN_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN);
	public final ConfigRef<List<String>> BOOGEYMAN_LIST = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_LIST);
	public final ConfigRef<Boolean> BOOGEYMAN_HIDE_IN_TAB = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_HIDE_IN_TAB);
	public static final ConfigRef<Boolean> BOOGEYMAN_HIDE_ARMOR = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_HIDE_ARMOR); // Synced value

	private List<UUID> boogeymen = new ArrayList<>();
	private final Set<UUID> disguised = new HashSet<>();
	private final Map<UUID, GameType> hiddenGameModes = new HashMap<>();

	public boolean isEnabled() {
		return BOOGEYMAN_ENABLED.get();
	}

	public void onReload() {
		List<UUID> newBoogeymen = new ArrayList<>();
		for (String uuidStr : BOOGEYMAN_LIST.get()) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				if (!newBoogeymen.contains(uuid)) newBoogeymen.add(uuid);
			}catch(Exception ignored) {}
		}
		boogeymen = newBoogeymen;
		PlayerUtils.getAllPlayers().forEach(player -> checkBoogeymanStatus(player, true));
	}

	public void createTeamsIfNecessary() {
		//~ if >= 26.2 'ChatFormatting' -> 'TeamColor' {
		if (TeamUtils.createTeam(TEAM_NAME, "Boogeyman", TeamColor.DARK_GREEN)) {
			PlayerTeam team = TeamUtils.getTeam(TEAM_NAME);
			if (team != null) {
				team.setNameTagVisibility(Team.Visibility.NEVER);
			}
		}
		//~}
	}

	public void createScoreboardsIfNecessary() {
		ScoreboardUtils.createObjective(SCOREBOARD_NAME);
	}

	public void resetScoreboard() {
		ScoreboardUtils.removeObjective(SCOREBOARD_NAME);
		createScoreboardsIfNecessary();
	}

	public void onPlayerJoin(ServerPlayer player) {
		PlayerReference ref = PlayerReference.of(player);
		if (isBoogeyman(player) && !player.isSpectator()) {
			// To hide them before their profile is finished loading
			hiddenGameModes.put(player.getUUID(), player.gameMode.getGameModeForPlayer());
			player.setGameMode(GameType.SPECTATOR);
			PlayerUtils.updatePlayerLists(); //TODO it still shows the name for a tick or so.
		}
		TaskScheduler.scheduleTask(1, () -> checkBoogeymanStatus(ref.get(), false));
	}

	public void onPlayerDisconnect(ServerPlayer player) {
		disguised.remove(player.getUUID());
		hiddenGameModes.remove(player.getUUID());
	}

	public EventResult onPlayerPreDeath(ServerPlayer player, DamageSource source) {
		if (!isBoogeyman(player)) return EventResult.PASS;
		if (!isDeadBoogeyman(player)) {
			announceLeave(player);
		}
		setStoredStatus(player.getUUID(), SCOREBOARD_STATUS_DEAD);
		// The death is cancelled, so the player would otherwise stay at 0 health
		player.setHealth(player.getMaxHealth());
		player.clearFire();
		player.removeAllEffects();
		hiddenGameModes.remove(player.getUUID());
		player.setGameMode(GameType.SPECTATOR);
		return EventResult.DENY;
	}

	public void onSessionStart() {
		//? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
            String uuid = entry.getOwner();
            int score = entry.getScore();
        *///?} else {
		for (PlayerScoreEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
			String uuid = entry.owner();
			int score = entry.value();
			//?}
			if (score == SCOREBOARD_STATUS_DEAD) {
				ScoreboardUtils.setScore(uuid, SCOREBOARD_NAME, SCOREBOARD_STATUS_ALIVE);
				try {
					UUID uuidReal = UUID.fromString(uuid);
					ServerPlayer player = PlayerUtils.getPlayer(uuidReal);
					if (player != null && isBoogeyman(player)) {
						checkBoogeymanGameMode(player);
						announceJoin();
					}
				} catch (Exception ignored) {}
			}
		}
	}

	public void onSessionEnd() {
		//? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
            String uuid = entry.getOwner();
            int score = entry.getScore();
        *///?} else {
		for (PlayerScoreEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
			String uuid = entry.owner();
			int score = entry.value();
			//?}
			if (score == SCOREBOARD_STATUS_ALIVE) {
				ScoreboardUtils.setScore(uuid, SCOREBOARD_NAME, SCOREBOARD_STATUS_DEAD);
				try {
					UUID uuidReal = UUID.fromString(uuid);
					ServerPlayer player = PlayerUtils.getPlayer(uuidReal);
					if (player != null && isBoogeyman(player)) {
						checkBoogeymanGameMode(player);
						announceLeave(player);
					}
				} catch (Exception ignored) {}
			}
		}
	}

	public void switchOutOfSeason() {
		for (UUID uuid : new ArrayList<>(disguised)) {
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			if (player != null) removeDisguise(player);
		}
		disguised.clear();
		hiddenGameModes.clear();
		resetScoreboard();
	}

	public static boolean overwriteConnectionMessage(ServerPlayer player, boolean joined) {
		if (player == null) return false;
		if (!(currentSeason instanceof FinalLife finalLife)) return false;
		FinalLifeBoogeymanManager bm = finalLife.finalLifeBoogeymanManager;
		if (!bm.isBoogeyman(player)) return false;
		if (!bm.isDeadBoogeyman(player)) {
			if (joined) {
				bm.announceJoin();
			}
			else {
				bm.announceLeave(player);
			}
		}
		return true;
	}

	public void saveBoogeymenList() {
		List<String> newValue = boogeymen.stream().map(UUID::toString).collect(Collectors.toList());
		LifeSeriesNetworkHandlerServer.updatedConfigThisTickFromCommand = true;
		BOOGEYMAN_LIST.value().set(newValue);
	}

	public boolean anyBoogeymen() {
		return !boogeymen.isEmpty();
	}

	public boolean isBoogeyman(UUID uuid) {
		return uuid != null && isEnabled() && boogeymen.contains(uuid);
	}

	public boolean isBoogeyman(ServerPlayer player) {
		return player != null && isBoogeyman(player.getUUID());
	}

	public boolean canHaveBoogeymanWeapons(ServerPlayer player) {
		return player != null && (isBoogeyman(player) || hasStoredStatus(player.getUUID()));
	}

	public boolean isDeadBoogeyman(ServerPlayer player) {
		return isBoogeyman(player) && isStoredDead(player.getUUID());
	}

	public List<UUID> getBoogeymen() {
		return boogeymen;
	}

	public boolean addBoogeyman(UUID uuid) {
		if (uuid == null || boogeymen.contains(uuid)) return false;
		boogeymen.add(uuid);
		saveBoogeymenList();
		checkBoogeymanStatus(PlayerUtils.getPlayer(uuid), true);
		return true;
	}

	public boolean removeBoogeyman(UUID uuid) {
		if (uuid == null || !boogeymen.contains(uuid)) return false;
		boogeymen.remove(uuid);
		saveBoogeymenList();
		checkBoogeymanStatus(PlayerUtils.getPlayer(uuid), true);
		return true;
	}

	public void clearBoogeymen() {
		List<UUID> oldBoogeymen = new ArrayList<>(boogeymen);
		boogeymen.clear();
		saveBoogeymenList();
		for (UUID uuid : oldBoogeymen) {
			checkBoogeymanStatus(PlayerUtils.getPlayer(uuid), true);
		}
	}

	public void reloadDisguises() {
		if (!isEnabled()) return;
		LifeSkinsManager.reloadCache();
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			if (isBoogeyman(player) && disguised.contains(player.getUUID())) {
				applyDisguise(player);
			}
		}
	}

	public void checkBoogeymanStatus(ServerPlayer player, boolean announce) {
		if (player == null || player.hasDisconnected()) return;
		boolean wasDead = isStoredDead(player.getUUID());
		updateStoredStatus(player);
		updateDisguise(player, announce, wasDead);
	}

	private void updateStoredStatus(ServerPlayer player) {
		if (!isEnabled()) return;
		UUID uuid = player.getUUID();
		boolean wasBoogeyman = hasStoredStatus(uuid);
		boolean wasDead = isStoredDead(uuid);
		boolean isBoogeyman = isBoogeyman(uuid);

		if (isBoogeyman && !wasBoogeyman) {
			setStoredStatus(uuid, SCOREBOARD_STATUS_ALIVE);
			onBecameBoogeyman(player);
		}
		else if (!isBoogeyman && wasBoogeyman) {
			clearStoredStatus(uuid);
			onStoppedBeingBoogeyman(player, wasDead);
		}
	}

	private void checkBoogeymanGameMode(ServerPlayer player) {
		boolean dead = isDeadBoogeyman(player);
		boolean spectator = player.isSpectator();
		GameType hiddenGameMode = hiddenGameModes.remove(player.getUUID());
		if (dead && !spectator) {
			player.setGameMode(GameType.SPECTATOR);
		}
		else if (!dead && spectator) {
			if (hiddenGameMode != null && hiddenGameMode != GameType.SPECTATOR) {
				player.setGameMode(hiddenGameMode);
			}
			else {
				PlayerUtils.safelyPutIntoSurvival(player);
			}
		}
	}

	private void restoreNormalGameMode(ServerPlayer player) {
		GameType hiddenGameMode = hiddenGameModes.remove(player.getUUID());
		if (!player.isSpectator()) return;
		if (WatcherManager.isWatcher(player)) return;
		if (livesManager.hasAssignedLives(player) && !livesManager.isAlive(player)) return;
		if (hiddenGameMode != null && hiddenGameMode != GameType.SPECTATOR) {
			player.setGameMode(hiddenGameMode);
		}
		else {
			PlayerUtils.safelyPutIntoSurvival(player);
		}
	}

	private void updateDisguise(ServerPlayer player, boolean announce, boolean wasDead) {
		boolean shouldBeDisguised = isBoogeyman(player);
		boolean isDisguised = disguised.contains(player.getUUID());
		if (shouldBeDisguised && !isDisguised) {
			applyDisguise(player);
			if (announce && !isDeadBoogeyman(player)) announceJoin();
		}
		else if (!shouldBeDisguised && isDisguised) {
			removeDisguise(player);
			if (announce && !wasDead) announceLeave(player);
		}
		else if (shouldBeDisguised) {
			checkBoogeymanGameMode(player);
		}
	}

	private void onBecameBoogeyman(ServerPlayer player) {
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_ADDED.get());
		//TODO
	}

	private void onStoppedBeingBoogeyman(ServerPlayer player, boolean wasDead) {
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_REMOVED.get());
		restoreNormalGameMode(player);
		ThrownBoogeymanWeapon.returnWeapons(player);
		BoogeymanWeaponManager.removeBoogeymanWeaponNBTs(player);
		//TODO
	}

	private Integer getStoredStatus(UUID uuid) {
		return ScoreboardUtils.getScore(uuid.toString(), SCOREBOARD_NAME);
	}

	private boolean hasStoredStatus(UUID uuid) {
		Integer status = getStoredStatus(uuid);
		return status != null && (status == SCOREBOARD_STATUS_ALIVE || status == SCOREBOARD_STATUS_DEAD);
	}

	private boolean isStoredDead(UUID uuid) {
		Integer status = getStoredStatus(uuid);
		return status != null && status == SCOREBOARD_STATUS_DEAD;
	}

	private void setStoredStatus(UUID uuid, int status) {
		ScoreboardUtils.setScore(uuid.toString(), SCOREBOARD_NAME, status);
	}

	private void clearStoredStatus(UUID uuid) {
		ScoreboardUtils.resetScore(uuid.toString(), SCOREBOARD_NAME);
	}

	private void applyDisguise(ServerPlayer player) {
		disguised.add(player.getUUID());
		var matchUser = ProfileManager.ProfileChange.set(getBoogeymanUsername());
		ProfileManager.modifyProfile(player, matchUser, matchUser)
				.thenRun(() -> afterProfileChange(player));
	}

	private void removeDisguise(ServerPlayer player) {
		disguised.remove(player.getUUID());
		ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.original(), ProfileManager.ProfileChange.original())
				.thenRun(() -> afterProfileChange(player));
	}

	private void afterProfileChange(ServerPlayer player) {
		if (player.hasDisconnected()) return;
		if (isBoogeyman(player)) {
			checkBoogeymanGameMode(player);
		}
		else {
			restoreNormalGameMode(player);
		}
		currentSeason.reloadPlayerTeam(player);
		Events.updatePlayerListsNextTick = true;
	}

	@NotNull
	public String getBoogeymanUsername() {
		String username = ModifiableText.FINALLIFE_BOOGEYMAN_USERNAME.getString().replaceAll("[^A-Za-z0-9_]", "");
		if (username.length() > PlayerUtils.MAX_USERNAME_LENGTH) {
			username = username.substring(0, PlayerUtils.MAX_USERNAME_LENGTH);
		}
		if (username.isEmpty()) {
			username = DEFAULT_USERNAME;
		}
		return username;
	}

	private void announceJoin() {
		PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_JOIN.get(Component.literal(getBoogeymanUsername())));
		ModifiableSound.FINALLIFE_BOOGEYMAN_JOIN.broadcast();
	}

	private void announceLeave(ServerPlayer player) {
		PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_LEAVE.get(Component.literal(getBoogeymanUsername())));
		LevelUtils.summonHarmlessLightning(player);
	}

	public static String getRealName(UUID uuid) {
		String originalName = ProfileManager.getOriginalName(uuid);
		if (originalName != null && !originalName.isEmpty()) return originalName;
		return PlayerUtils.getNameFromUUIDOrRaw(uuid);
	}

	public UUID resolvePlayer(String nameOrUUID) {
		if (nameOrUUID == null || nameOrUUID.isEmpty()) return null;
		try {
			return UUID.fromString(nameOrUUID);
		}catch(Exception ignored) {}

		for (UUID uuid : boogeymen) {
			if (getRealName(uuid).equalsIgnoreCase(nameOrUUID)) return uuid;
		}
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			if (getRealName(player.getUUID()).equalsIgnoreCase(nameOrUUID)) return player.getUUID();
		}

		UUID uuid = PlayerUtils.getUUIDFromName(nameOrUUID);
		if (uuid == null) return null;
		if (PlayerUtils.getPlayer(uuid) != null) return null;
		return uuid;
	}

	public List<String> getAddSuggestions() {
		List<String> result = new ArrayList<>();
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			if (boogeymen.contains(player.getUUID())) continue;
			result.add(getRealName(player.getUUID()));
		}
		return result;
	}

	public List<String> getRemoveSuggestions() {
		List<String> result = new ArrayList<>();
		for (UUID uuid : boogeymen) {
			result.add(getRealName(uuid));
		}
		return result;
	}

	/*
		Boogeyman Skin
	 */
	private static final String FINAL_LIFE_CONFIG = "config/lifeseries/finallife";
	private static final String FINAL_LIFE_BOOGEY_FILE = "boogeyman.png";
	private static final String FINAL_LIFE_BOOGEY_FILE_SLIM = "boogeyman_slim.png";
	public void copyBoogeymanSkin(ResourceHandler handler) {
		File finalLifeConfig = new File(FINAL_LIFE_CONFIG);
		try {
			Files.createDirectories(finalLifeConfig.toPath());
		}catch(Exception e) {
			e.printStackTrace();
		}
		File boogeyFileSlim = finalLifeConfig.toPath().resolve(FINAL_LIFE_BOOGEY_FILE_SLIM).toFile();
		File boogeyFile = finalLifeConfig.toPath().resolve(FINAL_LIFE_BOOGEY_FILE).toFile();
		if (!boogeyFile.exists() && !boogeyFileSlim.exists()) {
			handler.copyBundledSingleFile("/files/finallife/boogeyman.png", boogeyFile.toPath());
		}
	}

	@Nullable
	public File getBoogeymanSkin() {
		File fileSlim = new File(FINAL_LIFE_CONFIG).toPath().resolve(FINAL_LIFE_BOOGEY_FILE_SLIM).toFile();
		if (fileSlim != null && fileSlim.exists() && fileSlim.isFile()) {
			return fileSlim;
		}
		File file = new File(FINAL_LIFE_CONFIG).toPath().resolve(FINAL_LIFE_BOOGEY_FILE).toFile();
		if (file != null && file.exists() && file.isFile()) {
			return file;
		}
		return null;
	}

	public void onGatherSkinData() {
		if (!isEnabled()) return;
		String boogeymanUsername = getBoogeymanUsername();
		File skinFile = getBoogeymanSkin();
		if (skinFile == null) return;
		String skinName = skinFile.getName();
		if (!skinName.toLowerCase(Locale.ROOT).endsWith(".png")) return;
		LifeSkinsManager.upgradeSkinIfNeeded(boogeymanUsername, skinFile, false);
		int dotIndex = skinName.lastIndexOf('.');
		if (dotIndex > 0) {
			skinName = skinName.substring(0, dotIndex);
		}
		Map<String, Triple<Boolean, File, Integer>> skins = new HashMap<>();
		boolean isSlim = skinName.endsWith("_slim");
		skins.put(TEAM_NAME, Triple.of(isSlim, skinFile, null));
		LifeSkinsManager.getCache().put(boogeymanUsername, skins);
	}
}
