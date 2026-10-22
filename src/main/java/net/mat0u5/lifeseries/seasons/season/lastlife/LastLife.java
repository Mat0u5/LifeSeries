package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class LastLife extends Season {

    @Override
    public Seasons getSeason() {
        return Seasons.LAST_LIFE;
    }

    @Override
    public SeasonConfig createConfig() {
        return new LastLifeConfig();
    }
}
