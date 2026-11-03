package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.utils.player.ScoreboardUtils;
import net.mat0u5.matlib.utils.player.TeamUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.TeamColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public class FinalLifeBoogeymanManager {
	public static final String SCOREBOARD_NAME = "finallife_boogyeman";
	public static final String TEAM_NAME = "finallife_boogyeman";
	public final ConfigRef<Boolean> BOOGEYMAN_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN);
	public final ConfigRef<List<String>> BOOGEYMAN_LIST = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_LIST);
	public final ConfigRef<Boolean> BOOGEYMAN_HIDE_IN_TAB = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_HIDE_IN_TAB);
	private List<UUID> boogeymen = new ArrayList<>();
	public boolean isEnabled() {
		return BOOGEYMAN_ENABLED.get();
	}

	public void onReload() {
		List<UUID> newBoogeymen = new ArrayList<>();
		for (String uuidStr : BOOGEYMAN_LIST.get()) {
			try {
				newBoogeymen.add(UUID.fromString(uuidStr));
			}catch(Exception ignored) {}
		}
		boogeymen = newBoogeymen;
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
		boolean wasBoogey = ScoreboardUtils.getScore(player, SCOREBOARD_NAME) != null;
		boolean isBoogey = isBoogeyman(player);
		if (isBoogey) {
			PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_JOIN.get(ModifiableText.FINALLIFE_BOOGEYMAN_USERNAME.get()));
		}
		if (isBoogey && !wasBoogey) {
			processAddBoogeyman(player.getUUID());
		}
		if (!isBoogey && wasBoogey) {
			processRemoveBoogeyman(player.getUUID());
		}
	}

	public void onPlayerDeath(ServerPlayer player, DamageSource source) {
		if (isBoogeyman(player)) {
			PlayerUtils.broadcastMessage(ModifiableText.FINALLIFE_BOOGEYMAN_MESSAGE_LEAVE.get(ModifiableText.FINALLIFE_BOOGEYMAN_USERNAME.get()));
			player.setGameMode(GameType.SPECTATOR);
		}
	}

	public void saveBoogeymenList() {
		List<String> newValue = boogeymen.stream().map(UUID::toString).collect(Collectors.toList());
		BOOGEYMAN_LIST.value().set(newValue);
	}

	public boolean anyBoogeymen() {
		return !boogeymen.isEmpty();
	}

	public boolean isBoogeyman(ServerPlayer player) {
		return boogeymen.contains(player.getUUID());
	}

	public List<UUID> getBoogeymen() {
		return boogeymen;
	}

	public void addBoogeyman(ServerPlayer player) {
		if (isBoogeyman(player)) return;
		boogeymen.add(player.getUUID());
		saveBoogeymenList();
		processAddBoogeyman(player.getUUID());
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_ADDED.get());
	}

	public void removeBoogeyman(ServerPlayer player) {
		if (!isBoogeyman(player)) return;
		boogeymen.remove(player.getUUID());
		saveBoogeymenList();
		processRemoveBoogeyman(player.getUUID());
		((IPlayer)player).ls$message(ModifiableText.FINALLIFE_BOOGEYMAN_NOTICE_REMOVED.get());
	}

	private void processAddBoogeyman(UUID uuid) {
		ServerPlayer player = PlayerUtils.getPlayer(uuid);
		if (player == null) return;
		ScoreboardUtils.setScore(player, SCOREBOARD_NAME, 1);
		currentSeason.reloadPlayerTeam(player);
		ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.original(), ProfileManager.ProfileChange.set(ModifiableText.FINALLIFE_BOOGEYMAN_USERNAME.getString()));
	}

	private void processRemoveBoogeyman(UUID uuid) {
		ServerPlayer player = PlayerUtils.getPlayer(uuid);
		if (player == null) return;
		ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
		currentSeason.reloadPlayerTeam(player);
		ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.original(), ProfileManager.ProfileChange.original());
	}

	public void clearBoogeymen() {
		List<UUID> oldBooogeymen = new ArrayList<>(boogeymen);
		boogeymen.clear();
		saveBoogeymenList();
		oldBooogeymen.forEach(this::processRemoveBoogeyman);
	}
}
