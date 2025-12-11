package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;

public class NiceLife extends Season {

    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";

    @Override
    public Seasons getSeason() {
        return Seasons.NICE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new NiceLifeConfig();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }
}
