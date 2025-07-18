package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.ArrayList;
import java.util.List;

public class LimitedLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "mace",
            "leather_helmet",
            "chainmail_helmet",
            "golden_helmet",
            "iron_helmet",
            "diamond_helmet",
            "netherite_helmet",
            "turtle_helmet",
            "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = List.of(
            "lectern",
            "bookshelf"
    );
    public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
            "sharpness",
            "smite",
            "bane_of_arthropods",
            "fire_aspect",
            "knockback",
            "sweeping_edge",

            "power",
            "punch",

            "protection",
            "projectile_protection",
            "blast_protection",
            "fire_protection",
            "feather_falling",
            "thorns",

            "breach",
            "density",
            "wind_burst",

            "multishot",
            "piercing",
            "quick_charge"
    );

    public static final ConfigFileEntry<Integer> TIME_DEFAULT = new ConfigFileEntry<>(
            "time_default", 86400, "season.time", "Time Default", "The time with which players start, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_YELLOW = new ConfigFileEntry<>(
            "time_yellow", 57600, "season.time", "Time Yellow", "The Green-Yellow time border, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_RED = new ConfigFileEntry<>(
            "time_red", 28800, "season.time", "Time Red", "The Yellow-Red time border, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_DEATH = new ConfigFileEntry<>(
            "time_death", -3600, "season.time", "Time Death", "Time time you lose for dying, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_DEATH_BOOGEYMAN = new ConfigFileEntry<>(
            "time_death_boogeyman", -7200, "season.time", "Time Death Boogeyman", "The time you lose for the Boogeyman killing you, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_KILL = new ConfigFileEntry<>(
            "time_kill", 1800, "season.time", "Time Kill", "The time you gain for killing someone, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_KILL_BOOGEYMAN = new ConfigFileEntry<>(
            "time_kill_boogeyman", 3600, "season.time", "Time Kill Boogeyman", "The time you gain for killing someone while you are the boogeyman, in seconds."
    );
    public static final ConfigFileEntry<Boolean> TICK_OFFLINE_PLAYERS = new ConfigFileEntry<>(
            "tick_offline_players", false, "season", "Tick Offline Players", "Controls whether even players that are offline lose time when the session is on."
    );

    public static final ConfigFileEntry<Object> GROUP_TIME = new ConfigFileEntry<>(
            "group_time", null, ConfigTypes.TEXT, "{season.time}", "Time Rewards / Punishments", ""
    );

    public LimitedLifeConfig() {
        super("./config/"+ Main.MOD_ID,"limitedlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(DEFAULT_LIVES);
        defaultEntries.remove(GIVELIFE_COMMAND_ENABLED);
        defaultEntries.remove(GIVELIFE_LIVES_MAX);
        return defaultEntries;
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                TICK_OFFLINE_PLAYERS

                ,GROUP_TIME //Group

                //Group stuff
                ,TIME_DEFAULT
                ,TIME_YELLOW
                ,TIME_RED
                ,TIME_DEATH
                ,TIME_DEATH_BOOGEYMAN
                ,TIME_KILL
                ,TIME_KILL_BOOGEYMAN
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = "["+String.join(", ", BLACKLISTED_ITEMS)+"]";
        BLACKLIST_BLOCKS.defaultValue = "["+String.join(", ", BLACKLISTED_BLOCKS)+"]";
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = "["+String.join(", ", CLAMPED_ENCHANTMENTS)+"]";
        FINAL_DEATH_TITLE_SUBTITLE.defaultValue = "ran out of time!";
        FINAL_DEATH_MESSAGE.defaultValue = "${player} ran out of time.";
        BOOGEYMAN.defaultValue = true;
        BOOGEYMAN_MAX_AMOUNT.defaultValue = 1;
        BOOGEYMAN_MESSAGE.defaultValue = "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, your time will be dropped to the next color. All loyalties and friendships are removed while you are the Boogeyman.";
        super.instantiateProperties();
    }
}
