package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.server.level.ServerPlayer;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LastLife extends Season {

    @Override
    public Seasons getSeason() {
        return Seasons.LAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LastLifeConfig();
    }
}
