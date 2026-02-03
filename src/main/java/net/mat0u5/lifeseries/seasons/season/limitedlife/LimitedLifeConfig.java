package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;

import java.util.ArrayList;
import java.util.List;

public class LimitedLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            //? if >= 1.21
            "mace",
            "leather_helmet",
            "chainmail_helmet",
            "golden_helmet",
            "iron_helmet",
            "diamond_helmet",
            "netherite_helmet",
            "turtle_helmet",
            //? if >= 1.21.9
            "copper_helmet",
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

    public static final ConfigFileEntry<Integer> TIME_DEFAULT = new ConfigFileEntry<>(
            "time_default", 86400, ConfigTypes.SECONDS, "season.time",
            "Time Default", "The time with which players start, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_YELLOW = new ConfigFileEntry<>(
            "time_yellow", 57600, ConfigTypes.SECONDS, "season.time",
            "Time Yellow", "The Green-Yellow time border, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_RED = new ConfigFileEntry<>(
            "time_red", 28800, ConfigTypes.SECONDS, "season.time",
            "Time Red", "The Yellow-Red time border, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_DEATH = new ConfigFileEntry<>(
            "time_death", -3600, ConfigTypes.SECONDS, "season.time",
            "Time Death", "Time time you lose for dying, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_DEATH_BOOGEYMAN = new ConfigFileEntry<>(
            "time_death_boogeyman", -7200, ConfigTypes.SECONDS, "season.time",
            "Time Death Boogeyman", "The time you lose for the Boogeyman killing you, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_KILL = new ConfigFileEntry<>(
            "time_kill", 1800, ConfigTypes.SECONDS, "season.time",
            "Time Kill", "The time you gain for killing someone, in seconds."
    );
    public static final ConfigFileEntry<Integer> TIME_KILL_BOOGEYMAN = new ConfigFileEntry<>(
            "time_kill_boogeyman", 3600, ConfigTypes.SECONDS, "season.time",
            "Time Kill Boogeyman", "The time you gain for killing someone while you are the boogeyman, in seconds."
    );
    public static final ConfigFileEntry<Boolean> TICK_OFFLINE_PLAYERS = new ConfigFileEntry<>(
            "tick_offline_players", false, "season",
            "Tick Offline Players", "Controls whether even players that are offline lose time when the session is on."
    );
    public static final ConfigFileEntry<Boolean> BROADCAST_COLOR_CHANGES = new ConfigFileEntry<>(
            "broadcast_color_changes", false, "season",
            "Broadcast Color Changes", "Sends a message in chat to all players when someone changes color."
    );
    public static final ConfigFileEntry<Boolean> SHOW_TIME_BELOW_NAME = new ConfigFileEntry<>(
            "show_time_below_name", false, "season",
            "Show Time Below Name", "Show the time a player has left below their username."
    );

    public static final ConfigFileEntry<Object> GROUP_TIME = new ConfigFileEntry<>(
            "group_time", null, ConfigTypes.TEXT, "{season.time}",
            "Time Rewards / Punishments", ""
    );

    public LimitedLifeConfig() {
        super("./config/"+ Main.MOD_ID,"limitedlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(DEFAULT_LIVES);
        defaultEntries.remove(TAB_LIST_SHOW_EXACT_LIVES);
        defaultEntries.remove(SECRET_SOCIETY_PUNISHMENT_LIVES);
        return defaultEntries;
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                TICK_OFFLINE_PLAYERS
                ,BROADCAST_COLOR_CHANGES
                ,SHOW_TIME_BELOW_NAME

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
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        FINAL_DEATH_TITLE_SUBTITLE.defaultValue = "ran out of time!";
        FINAL_DEATH_MESSAGE.defaultValue = "${player} ran out of time.";
        BOOGEYMAN.defaultValue = true;
        BOOGEYMAN_MAX_AMOUNT.defaultValue = 1;
        BOOGEYMAN_MESSAGE.defaultValue = "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, your time will be dropped to the next color. All loyalties and friendships are removed while you are the Boogeyman.";
        GIVELIFE_LIVES_MAX.displayName = "Max Givelife Time";
        GIVELIFE_LIVES_MAX.description = "The maximum amount of time a player can have from other players giving them time using /givelife.";
        GIVELIFE_LIVES_MAX.defaultValue = 3600000;
        GIVELIFE_BROADCAST.description = "Broadcasts the message when a player gives time to another player using /givelife.";
        LIVES_RANDOMIZE_MIN.defaultValue = Time.hours(12).getSeconds();
        LIVES_RANDOMIZE_MAX.defaultValue = Time.hours(36).getSeconds();

        GROUP_GLOBAL_LIVES.displayName = "Time Stuff";
        GROUP_LIVES.displayName = "Time Manager";
        ONLY_TAKE_LIVES_IN_SESSION.displayName = "Only Lose Time In Session";
        ONLY_TAKE_LIVES_IN_SESSION.description = "Makes players only lose time when they die while a session is active.";

        LIVES_SYSTEM_DISABLED.displayName = "Fully Disable Time System";
        LIVES_SYSTEM_DISABLED.description = "Fully disables the time system, if you want to implement a custom one for example :)";
        TAB_LIST_SHOW_LIVES.displayName = "Tab List Show Time";
        TAB_LIST_SHOW_LIVES.description = "Controls whether you can see the players' time in the tab list.";
        LIVES_RANDOMIZE.displayName = "Randomize Time";
        LIVES_RANDOMIZE.description = "Makes every player get a random amount of time.";
        LIVES_RANDOMIZE_MIN.displayName = "Minimum Time";
        LIVES_RANDOMIZE_MIN.description = "The minimum number of time any player can have after randomization, in seconds.";
        LIVES_RANDOMIZE_MAX.displayName = "Maximum Time";
        LIVES_RANDOMIZE_MAX.description = "The minimum number of time any player can have after randomization, in seconds.";
        super.instantiateProperties();
    }
}
