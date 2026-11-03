package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class FinalLife extends Season {
	public FinalLifeBoogeymanManager finalLifeBoogeymanManager = new FinalLifeBoogeymanManager();

	@Override
	public Seasons getSeason() {
		return Seasons.FINAL_LIFE;
	}

	@Override
	public SeasonConfig createConfig() {
		return new FinalLifeConfig();
	}

	@Override
	public LivesManager createLivesManager() {
		return new FinalLifeLivesManager();
	}

	@Override
	public void reload() {
		super.reload();
		finalLifeBoogeymanManager.onReload();
	}

	@Override
	public String getTeamForPlayer(ServerPlayer player) {
		if (finalLifeBoogeymanManager.isBoogeyman(player)) {
			return FinalLifeBoogeymanManager.TEAM_NAME;
		}
		return super.getTeamForPlayer(player);
	}

	@Override
	public void createTeams() {
		super.createTeams();
		finalLifeBoogeymanManager.createTeamsIfNecessary();
	}
	@Override
	public void createScoreboards() {
		super.createScoreboards();
		finalLifeBoogeymanManager.createScoreboardsIfNecessary();
	}

	@Override
	public void onPlayerJoin(ServerPlayer player) {
		finalLifeBoogeymanManager.onPlayerJoin(player);
		super.onPlayerJoin(player);
	}

	@Override
	public void onPlayerDeath(ServerPlayer player, DamageSource source) {
		finalLifeBoogeymanManager.onPlayerDeath(player, source);
		super.onPlayerDeath(player, source);
	}
}
