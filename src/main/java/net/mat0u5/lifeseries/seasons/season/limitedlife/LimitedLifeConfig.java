package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.utils.other.Time;

import java.util.List;

public class LimitedLifeConfig extends SeasonConfig {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            //? if >= 1.21
            "mace",
            "#minecraft:head_armor",
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
            //"sweeping",
            //?} else {
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

    public final ConfigurableValue<Boolean> TICK_OFFLINE_PLAYERS = ConfigurableValue.register(this, "tick_offline_players", false)
            .name("Tick Offline Players")
            .description("Controls whether even players that are offline lose time when the session is on.")
            .group("season");
    public final ConfigurableValue<Boolean> BROADCAST_COLOR_CHANGES = ConfigurableValue.register(this, "broadcast_color_changes", false)
            .name("Broadcast Color Changes")
            .description("Sends a message in chat to all players when someone changes color.")
            .group("season");
    public final ConfigurableValue<Boolean> SHOW_TIME_BELOW_NAME = ConfigurableValue.register(this, "show_time_below_name", false)
            .name("Show Time Below Name")
            .description("Show the time a player has left below their username.")
            .group("season");

    public final ConfigHeader GROUP_TIME = ConfigHeader.register(this, "group_time", "Time Rewards / Punishments")
            .group("season.time").groupHeader(true);

    public final ConfigurableValue<Integer> TIME_DEFAULT = ConfigurableValue.register(this, "time_default", 86400, ConfigTypes.SECONDS)
            .name("Time Default")
            .description("The time with which players start, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_YELLOW = ConfigurableValue.register(this, "time_yellow", 57600, ConfigTypes.SECONDS)
            .name("Time Yellow")
            .description("The Green-Yellow time border, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_RED = ConfigurableValue.register(this, "time_red", 28800, ConfigTypes.SECONDS)
            .name("Time Red")
            .description("The Yellow-Red time border, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_DEATH = ConfigurableValue.register(this, "time_death", -3600, ConfigTypes.SECONDS)
            .name("Time Death")
            .description("Time time you lose for dying, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_DEATH_BOOGEYMAN = ConfigurableValue.register(this, "time_death_boogeyman", -7200, ConfigTypes.SECONDS)
            .name("Time Death Boogeyman")
            .description("The time you lose for the Boogeyman killing you, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_KILL = ConfigurableValue.register(this, "time_kill", 1800, ConfigTypes.SECONDS)
            .name("Time Kill")
            .description("The time you gain for killing someone, in seconds.")
            .group("season.time");
    public final ConfigurableValue<Integer> TIME_KILL_BOOGEYMAN = ConfigurableValue.register(this, "time_kill_boogeyman", 3600, ConfigTypes.SECONDS)
            .name("Time Kill Boogeyman")
            .description("The time you gain for killing someone while you are the boogeyman, in seconds.")
            .group("season.time");

    public final ConfigurableValue<Integer> TIME_RANDOMIZE_INTERVAL = ConfigurableValue.register(this, "time_randomize_interval", Time.hours(1).getSeconds(), ConfigTypes.SECONDS)
            .name("Time Randomize Intervals")
            .description("The intervals on which the time randomize can land.")
            .group("global.lives.random");
    public final ConfigurableValue<Integer> TIME_RANDOMIZE_AVERAGE = ConfigurableValue.register(this, "time_randomize_average", Time.hours(24).getSeconds(), ConfigTypes.SECONDS)
            .name("Average Time")
            .description("The average time a player gets after randomization.")
            .group("global.lives.random.customavg");
    public final ConfigurableValue<Boolean> PAUSE_SESSION_TIME_UNTIL_ROLL = ConfigurableValue.register(this, "pause_session_time_until_roll", false)
            .name("Pause Session Time Until Roll")
            .description("Makes the session time stay at max untill the lives roll happens.")
            .group("global.lives.random");

    public LimitedLifeConfig() {
        super(Seasons.LIMITED_LIFE);
        removeEntry(NO_SEASON_SPECIFIC);
        removeEntry(DEFAULT_LIVES);
        removeEntry(TAB_LIST_SHOW_EXACT_LIVES);
        removeEntry(SECRET_SOCIETY_PUNISHMENT_LIVES);
        removeEntry(LIVES_RANDOMIZE_AVERAGE);
        removeEntry(LIVES_RANDOMIZE_PSEUDORANDOM);

        CUSTOM_ENCHANTER_ALGORITHM.defaultValue(true);
        BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
        BOOGEYMAN.defaultValue(true);
        BOOGEYMAN_MAX_AMOUNT.defaultValue(1);
        GIVELIFE_LIVES_MAX.name("Max Givelife Time");
        GIVELIFE_LIVES_MAX.description("The maximum amount of time a player can have from other players giving them time using /givelife.");
        GIVELIFE_LIVES_MAX.defaultValue(3600000);
        GIVELIFE_BROADCAST.description("Broadcasts the message when a player gives time to another player using /givelife.");
        LIVES_RANDOMIZE_MIN.defaultValue(Time.hours(12).getSeconds());
        LIVES_RANDOMIZE_MAX.defaultValue(Time.hours(36).getSeconds());

        GROUP_GLOBAL_LIVES.name("Time Stuff");
        GROUP_LIVES.name("Time Manager");
        ONLY_CHANGE_LIVES_IN_SESSION.name("Only Change Time In Session");
        ONLY_CHANGE_LIVES_IN_SESSION.description("Makes players only lose/gain time when they die while a session is active.");

        LIVES_SYSTEM_DISABLED.name("Fully Disable Time System");
        LIVES_SYSTEM_DISABLED.description("Fully disables the time system, if you want to implement a custom one for example :)");
        TAB_LIST_SHOW_LIVES.name("Tab List Show Time");
        TAB_LIST_SHOW_LIVES.description("Controls whether you can see the players' time in the tab list.");
        LIVES_RANDOMIZE.name("Randomize Time");
        LIVES_RANDOMIZE.description("Makes every player get a random amount of time.");
        LIVES_RANDOMIZE_MIN.name("Minimum Time");
        LIVES_RANDOMIZE_MIN.description("The minimum number of time any player can have after randomization, in seconds.");
        LIVES_RANDOMIZE_MAX.name("Maximum Time");
        LIVES_RANDOMIZE_MAX.description("The minimum number of time any player can have after randomization, in seconds.");
        LIVES_RANDOMIZE_MIN.type(ConfigTypes.SECONDS);
        LIVES_RANDOMIZE_MAX.type(ConfigTypes.SECONDS);
        LIVES_LIFE_DIFF_MESSAGE.name("Show Time Diff In Death Message");
        LIVES_LIFE_DIFF_MESSAGE.description("Shows an indicator of how much time was lost in the death messages.");
        LIVES_LOSE_KILLS_ONLY.name("Only Lose Time From PvP Kills");
        LIVES_LOSE_KILLS_ONLY.description("Makes players not lose time from natural deaths.");
        LIVES_RANDOMIZE_CUSTOMAVG.name("Custom Average Time");
    }

    public static LimitedLifeConfig get() {
        return getActive(LimitedLifeConfig.class, LimitedLifeConfig::new);
    }
}
