package net.mat0u5.lifeseries.seasons.season.finallife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.util.LivesManager;

public class FinalLife extends Season {
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
}
