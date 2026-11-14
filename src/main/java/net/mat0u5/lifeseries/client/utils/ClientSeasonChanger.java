package net.mat0u5.lifeseries.client.utils;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.config.ConfigManager;
import net.mat0u5.matlib.config.entry.ConfigurableValue;

import static net.mat0u5.lifeseries.LifeSeries.seasonConfig;

public class ClientSeasonChanger {
	private static SeasonConfig localSeasonConfig = null;
	private static Seasons matchedSeason = null;

	public static void matchServerSeasonConfig(Seasons season) {
		if (LifeSeries.isLogicalSide()) return;
		if (season == null || season == Seasons.UNASSIGNED) return;
		if (season == matchedSeason) return;
		matchedSeason = season;
		SeasonConfig serverConfig = season.getSeasonInstance().createConfig();
		if (seasonConfig != null && seasonConfig.getClass() == serverConfig.getClass()) return;
		if (localSeasonConfig == null) localSeasonConfig = seasonConfig;
		setCurrentSeasonConfig(serverConfig);
	}

	public static void restoreLocalSeasonConfig() {
		matchedSeason = null;
		if (localSeasonConfig == null) return;
		SeasonConfig local = localSeasonConfig;
		localSeasonConfig = null;
		setCurrentSeasonConfig(local);
	}

	private static void setCurrentSeasonConfig(SeasonConfig newConfig) {
		if (seasonConfig != null) seasonConfig.getConfigurableValues().forEach(ConfigurableValue::clearSynced);
		newConfig.getConfigurableValues().forEach(ConfigurableValue::clearSynced);
		seasonConfig = newConfig;
		ConfigManager.unregisterConfig(SeasonConfig.CONFIG_ID);
		ConfigManager.registerConfig(newConfig);
	}
}
