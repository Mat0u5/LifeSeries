package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class SeasonConfig extends ConfigManager {

    protected SeasonConfig(Seasons season) {
        super("./config/"+ Main.MOD_ID,season.getId()+".properties");
    }

    protected SeasonConfig(String folderPath, String filePath) {
        super(folderPath, filePath);
    }
}
