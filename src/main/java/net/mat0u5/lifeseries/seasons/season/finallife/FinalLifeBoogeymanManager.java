package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.LifeSeriesNetworkHandlerServer;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.utils.player.PlayerReference;
import net.mat0u5.matlib.utils.player.ScoreboardUtils;
import net.mat0u5.matlib.utils.player.TeamUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

import java.util.*;
import java.util.stream.Collectors;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

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

	private List<UUID> boogeymen = new ArrayList<>();
	private final Set<UUID> disguised = new HashSet<>();

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
		if (TeamUtils.createTeam(TEAM_NAME, "Boogeyman", TeamColor.DARK_RED)) {
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

	public void onPlayerJoin(ServerPlayer player) {
		PlayerReference ref = PlayerReference.of(player);
		TaskScheduler.scheduleTask(1, () -> checkBoogeymanStatus(ref.get(), false));
	}

	public void onPlayerDisconnect(ServerPlayer player) {
		disguised.remove(player.getUUID());
	}

	public void onPlayerDeath(ServerPlayer player, DamageSource source) {
		if (!isBoogeyman(player)) return;
		if (!isDeadBoogeyman(player)) {
			announceLeaveMessage();
		}
		setStoredStatus(player.getUUID(), SCOREBOARD_STATUS_DEAD);
		player.setGameMode(GameType.SPECTATOR);
	}

	public void switchOutOfSeason() {
		for (UUID uuid : new ArrayList<>(disguised)) {
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			if (player != null) removeDisguise(player);
		}
		disguised.clear();
	}

	public static boolean overwriteConnectionMessage(ServerPlayer player, boolean joined) {
		if (player == null) return false;
		if (!(currentSeason instanceof FinalLife finalLife)) return false;
		FinalLifeBoogeymanManager bm = finalLife.finalLifeBoogeymanManager;
		if (!bm.isBoogeyman(player)) return false;
		if (!bm.isDeadBoogeyman(player)) {
			if (joined) {
				bm.announceJoinMessage();
			}
			else {
				bm.announceLeaveMessage();
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

	public boolean isDeadBoogeyman(ServerPlayer player) {
		return isBoogeyman(player) && isStoredDead(player.getUUID());
	}

	public boolean isDisguisedBoogeyman(ServerPlayer player) {
		return isBoogeyman(player) && disguised.contains(player.getUUID()) && ProfileManager.hasChangedName(player);
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

		if (isDeadBoogeyman(player) && !player.isSpectator()) {
			player.setGameMode(GameType.SPECTATOR);
		}
	}

	private void updateDisguise(ServerPlayer player, boolean announce, boolean wasDead) {
		boolean shouldBeDisguised = isBoogeyman(player);
		boolean isDisguised = disguised.contains(player.getUUID());
		if (shouldBeDisguised && !isDisguised) {
			applyDisguise(player);
			if (announce && !isDeadBoogeyman(player)) announceJoinMessage();
		}
		else if (!shouldBeDisguised && isDisguised) {
			removeDisguise(player);
			if (announce && !wasDead) announceLeaveMessage();
		}
	}

	private void onBecameBoogeyman(ServerPlayer player) {
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_ADDED.get());
		//TODO
	}

	private void onStoppedBeingBoogeyman(ServerPlayer player, boolean wasDead) {
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_REMOVED.get());
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
		currentSeason.reloadPlayerTeam(player);
		Events.updatePlayerListsNextTick = true;
	}

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

	private void announceJoinMessage() {
		PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_JOIN.get(Component.literal(getBoogeymanUsername())));
	}

	private void announceLeaveMessage() {
		PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_LEAVE.get(Component.literal(getBoogeymanUsername())));
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
}
