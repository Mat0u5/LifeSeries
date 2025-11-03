package net.mat0u5.lifeseries.seasons.season.thirdlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class ThirdLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";
    @Override
    public Seasons getSeason() {
        return Seasons.THIRD_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new ThirdLifeConfig();
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
