package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class NiceLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            //? if >= 1.21
            "mace",
            "end_crystal",
            "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = new ArrayList<>();
    public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
            "sharpness",
            "smite",
            "bane_of_arthropods",
            "fire_aspect",
            "knockback",
            //? if <= 1.20.3 {
            /*"sweeping",
             *///?} else {
            "sweeping_edge",
            //?}

            "power",
            "punch",

            "protection",
            "projectile_protection",
            "blast_protection",
            "fire_protection",
            "feather_falling",
            "thorns",

            //? if >= 1.21 {
            "breach",
            "density",
            "wind_burst",
            //?}

            "multishot",
            "piercing",
            "quick_charge"
    );

    public static final ConfigFileEntry<Boolean> LIGHT_MELTS_SNOW = new ConfigFileEntry<>(
            "light_melts_snow", false, "season[new]",
            "Light Melts Snow", "Controls whether light sources will melt snow."
    );

    public static final ConfigFileEntry<Boolean> SNOW_WHEN_NOT_IN_SESSION = new ConfigFileEntry<>(
            "snow_when_not_in_session", false, "season[new]",
            "Snow When Not In Session", "Controls it snows when the session is not started."
    );

    public static final ConfigFileEntry<Integer> SNOW_LAYER_INCREMENT_DELAY = new ConfigFileEntry<>(
            "snow_layer_increment_delay", 600, ConfigTypes.SECONDS, "season[new]",
            "Snow Layer Increment Delay", "Controls the interval between snow layer increments, in seconds."
    );

    public NiceLifeConfig() {
        super("./config/"+ Main.MOD_ID,"nicelife.properties");
    }

    public NiceLifeConfig(String folderPath, String filePath) {
        super(folderPath, filePath);
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                LIGHT_MELTS_SNOW
                ,SNOW_WHEN_NOT_IN_SESSION
                ,SNOW_LAYER_INCREMENT_DELAY
        ));
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        super.instantiateProperties();
    }
}