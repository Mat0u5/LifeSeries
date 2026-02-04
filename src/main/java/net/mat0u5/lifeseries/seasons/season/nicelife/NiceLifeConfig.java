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
    public static final ConfigFileEntry<Boolean> ADVANCE_TIME_WHEN_NOT_IN_SESSION = new ConfigFileEntry<>(
            "advance_time_not_in_session", false, "season[new]",
            "Advance Time When Not In Session", "Controls whether the daylight cycle is paused when not in session."
    );

    public static final ConfigFileEntry<Boolean> SNOWY_NETHER = new ConfigFileEntry<>(
            "snowy_nether", true, "season[new]",
            "Snowy Nether", "Controls the nether is frozen."
    );

    public static final ConfigFileEntry<Integer> TRIVIA_QUESTION_TIME = new ConfigFileEntry<>(
            "trivia_question_time", 68, ConfigTypes.SECONDS, "season.trivia[new]",
            "Trivia Question Time", "Controls how much time people have to answer the trivia, in seconds."
    );
    public static final ConfigFileEntry<Boolean> BOT_CAN_BREAK_BEDS = new ConfigFileEntry<>(
            "bot_can_break_beds", true, "season.trivia[new]",
            "Bots Can Break Beds", "Controls whether descending santa bots can break the beds of other players in the way."
    );
    public static final ConfigFileEntry<Boolean> BOT_BREAKING_BLOCKS_DROP_RESOURCES = new ConfigFileEntry<>(
            "bot_breaking_blocks_drop_resources", true, "season.trivia[new]",
            "Bots Breaking Blocks Drop Resources", "Controls whether block broken by descending santa bots drop items."
    );
    public static final ConfigFileEntry<Integer> VOTING_TIME = new ConfigFileEntry<>(
            "voting_time", 60, ConfigTypes.SECONDS, "season.voting[new]",
            "Voting Time", "Controls how much time people have for voting, in seconds."
    );
    public static final ConfigFileEntry<Boolean> ALLOW_REDS_ON_NAUGHTY_LIST = new ConfigFileEntry<>(
            "allow_reds_on_naughty_list", false, "season.voting[new]",
            "Naughty List Allow Reds", "Controls red players can be voted for on the naughty list."
    );
    public static final ConfigFileEntry<Integer> NAUGHTY_LIST_PLAYERS = new ConfigFileEntry<>(
            "naughty_list_players", 3, "season.voting[new]",
            "Naughty List Player Amount", "Controls the maximum number of players that can be on the naughty list."
    );
    public static final ConfigFileEntry<Double> NICE_LIST_CHANCE = new ConfigFileEntry<>(
            "nice_list_chance", 0.5, ConfigTypes.PERCENTAGE, "season.voting[new]",
            "Nice List Chance", "Percentage of how often the nice list will be chosen for the vote instead of the naughty list."
    );
    public static final ConfigFileEntry<Integer> NICE_LIST_PLAYERS = new ConfigFileEntry<>(
            "nice_list_players", 3, "season.voting[new]",
            "Nice List Player Amount", "Controls the maximum number of players that can be on the nice list."
    );


    public static final ConfigFileEntry<Object> GROUP_TRIVIA = new ConfigFileEntry<>(
            "group_trivia", null, ConfigTypes.TEXT, "{season.trivia}[new]",
            "Trivia", ""
    );
    public static final ConfigFileEntry<Object> GROUP_VOTING = new ConfigFileEntry<>(
            "group_voting", null, ConfigTypes.TEXT, "{season.voting}[new]",
            "Voting", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS = new ConfigFileEntry<>(
            "group_trivia_questions", null, ConfigTypes.TEXT, "{season.trivia.questions}",
            "Trivia Questions", ""
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
                ,ADVANCE_TIME_WHEN_NOT_IN_SESSION
                ,SNOWY_NETHER

                ,GROUP_TRIVIA
                ,GROUP_VOTING

                ,TRIVIA_QUESTION_TIME
                ,BOT_CAN_BREAK_BEDS
                ,BOT_BREAKING_BLOCKS_DROP_RESOURCES
                ,VOTING_TIME
                ,ALLOW_REDS_ON_NAUGHTY_LIST
                ,NAUGHTY_LIST_PLAYERS
                ,NICE_LIST_PLAYERS
                ,NICE_LIST_CHANCE
                ,GROUP_TRIVIA_QUESTIONS
        ));
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        super.instantiateProperties();
    }
}