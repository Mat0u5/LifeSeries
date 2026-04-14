package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class SeasonConfig extends ConfigManager {

    protected SeasonConfig(Seasons season) {
        super("./config/"+ LifeSeries.MOD_ID,season.getId()+".properties");
    }

    protected SeasonConfig(String folderPath, String filePath) {
        super(folderPath, filePath);
    }
}
