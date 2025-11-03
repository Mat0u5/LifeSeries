package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import ConfigFileEntry;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig extends ConfigManager {
    public ClientConfig() {
        super("./config/lifeseries/client", Main.MOD_ID+"_client.properties");
    }

    public static final ConfigFileEntry<Boolean> MINIMAL_ARMOR = new ConfigFileEntry<>(
            "minimal_armor", true, "",
            "Minimal Armor Resourcepack", "Enables the minimal armor resourcepack."
    );
    public static final ConfigFileEntry<Boolean> SESSION_TIMER = new ConfigFileEntry<>(
            "session_timer", true, "",
            "Session Timer", "Enables the session timer in the bottom right of the screen."
    );
    public static final ConfigFileEntry<Boolean> COLORBLIND_SUPPORT = new ConfigFileEntry<>(
            "colorblind_support", false, "",
            "Colorblind Support", "Enables a feature that shows the team color name next to a players' usernames."
    );
    public static final ConfigFileEntry<String> RUN_COMMAND = new ConfigFileEntry<>(
            "run_command", "/lifeseries config", "",
            "Keybind Command", "Pressing the 'Keybind Command' keybind will run this command. Only available in dev versions."
    );

    public static final ConfigFileEntry<Boolean> COLORED_HEARTS = new ConfigFileEntry<>(
            "colored_hearts", false, "{coloredhearts}",
            "Colored Hearts Based on Lives", "Makes your hearts the same color as how many lives you have."
    );
    public static final ConfigFileEntry<Boolean> COLORED_HEARTS_HARDCORE_LAST_LIFE = new ConfigFileEntry<>(
            "colored_hearts_hardcore_last_life", true, "coloredhearts",
            "Show Last Life as Hardcore", "When you are on your last life, the hearts will appear as though you are in hardcore."
    );
    public static final ConfigFileEntry<Boolean> COLORED_HEARTS_HARDCORE_ALL_LIVES = new ConfigFileEntry<>(
            "colored_hearts_hardcore_all_lives", false, "coloredhearts",
            "Show All Lives as Hardcore", "Hearts will always appear as though you are in hardcore."
    );

    public static final ConfigFileEntry<Double> TEXT_HUD_SCALE = new ConfigFileEntry<>(
            "text_hud_scale", 1.0, "",
            "Side Text Hud Scale", "The scale at which the side texts, like the session timer render at."
    );

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> result = new ArrayList<>(List.of(
                MINIMAL_ARMOR
                ,SESSION_TIMER
                ,COLORBLIND_SUPPORT
                ,COLORED_HEARTS // Group

                ,COLORED_HEARTS_HARDCORE_LAST_LIFE
                ,COLORED_HEARTS_HARDCORE_ALL_LIVES

                ,TEXT_HUD_SCALE
        ));

        if (VersionControl.isDevVersion()) {
            result.add(RUN_COMMAND);
        }
        return result;
    }
}
