package net.mat0u5.lifeseries.seasons.season.thirdlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class ThirdLife extends Season {
    @Override
    public Seasons getSeason() {
        return Seasons.THIRD_LIFE;
    }

    @Override
    public SeasonConfig createConfig() {
        return new ThirdLifeConfig();
    }
}
