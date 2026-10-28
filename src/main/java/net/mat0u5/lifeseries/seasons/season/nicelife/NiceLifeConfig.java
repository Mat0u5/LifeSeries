package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.TriviaGuiType;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;

import java.util.ArrayList;
import java.util.List;

public class NiceLifeConfig extends SeasonConfig {
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

    public final ConfigurableValue<Boolean> LIGHT_MELTS_SNOW = ConfigurableValue.register(this, "light_melts_snow", false)
            .name("Light Melts Snow")
            .description("Controls whether light sources will melt snow.")
            .group("season");
    public final ConfigurableValue<Boolean> SNOW_WHEN_NOT_IN_SESSION = ConfigurableValue.register(this, "snow_when_not_in_session", false)
            .name("Snow When Not In Session")
            .description("Controls it snows when the session is not started.")
            .group("season");
    public final ConfigurableValue<Integer> SNOW_LAYER_INCREMENT_DELAY = ConfigurableValue.register(this, "snow_layer_increment_delay", 600, ConfigTypes.SECONDS)
            .name("Snow Layer Increment Delay")
            .description("Controls the interval between snow layer increments, in seconds.")
            .group("season");
    public final ConfigurableValue<Boolean> DISABLE_SNOW = ConfigurableValue.register(this, "disable_snow", false)
            .name("Disable Snowing")
            .description("Controls whether it snows at all.")
            .setNew(true)
            .group("season");
    public final ConfigurableValue<Boolean> SNOWY_NETHER = ConfigurableValue.register(this, "snowy_nether", true)
            .name("Snowy Nether")
            .description("Controls whether the nether is frozen.")
            .group("season");
    public final ConfigurableValue<Boolean> RED_WINTER = ConfigurableValue.register(this, "red_winter", true)
            .name("Allow Red Winter")
            .description("Controls whether the Red Winter triggers.")
            .group("season");

    public final ConfigHeader GROUP_TRIVIA = ConfigHeader.register(this, "group_trivia", "Trivia")
            .group("season.trivia").groupHeader(true);
    public final ConfigHeader GROUP_VOTING = ConfigHeader.register(this, "group_voting", "Voting")
            .group("season.voting").groupHeader(true);

    public final ConfigurableValue<TriviaGuiType> TRIVIA_GUI_TYPE = ConfigurableValue.register(this, "gui_type", TriviaGuiType.NICE_LIFE)
            .name("Gui Type")
            .description("Controls the trivia gui style.")
            .group("season.trivia");
    public final ConfigurableValue<NightLength> TRIVIA_NIGHT_LENGTH = ConfigurableValue.register(this, "night_length", NightLength.FIRST_LONG)
            .name("Night Length")
            .description("Controls when there are long nights in trivia.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> TRIVIA_QUESTION_TIME = ConfigurableValue.register(this, "trivia_question_time", 68, ConfigTypes.SECONDS)
            .name("Trivia Question Time")
            .description("Controls how much time people have to answer the trivia, in seconds.")
            .group("season.trivia");
    public final ConfigurableValue<Boolean> BOT_CAN_BREAK_BEDS = ConfigurableValue.register(this, "bot_can_break_beds", true)
            .name("Bots Can Break Beds")
            .description("Controls whether descending santa bots can break the beds of other players in the way.")
            .group("season.trivia");
    public final ConfigurableValue<Boolean> BOT_BREAKING_BLOCKS_DROP_RESOURCES = ConfigurableValue.register(this, "bot_breaking_blocks_drop_resources", true)
            .name("Bots Breaking Blocks Drop Resources")
            .description("Controls whether block broken by descending santa bots drop items.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> VOTING_TIME = ConfigurableValue.register(this, "voting_time", 60, ConfigTypes.SECONDS)
            .name("Voting Time")
            .description("Controls how much time people have for voting, in seconds.")
            .group("season.voting");
    public final ConfigurableValue<Boolean> ALLOW_REDS_ON_NAUGHTY_LIST = ConfigurableValue.register(this, "allow_reds_on_naughty_list", false)
            .name("Naughty List Allow Reds")
            .description("Controls red players can be voted for on the naughty list.")
            .group("season.voting");
    public final ConfigurableValue<Integer> NAUGHTY_LIST_PLAYERS = ConfigurableValue.register(this, "naughty_list_players", 3)
            .name("Naughty List Player Amount")
            .description("Controls the maximum number of players that can be on the naughty list.")
            .group("season.voting");
    public final ConfigurableValue<Integer> NICE_LIST_PLAYERS = ConfigurableValue.register(this, "nice_list_players", 3)
            .name("Nice List Player Amount")
            .description("Controls the maximum number of players that can be on the nice list.")
            .group("season.voting");
    public final ConfigurableValue<Double> NICE_LIST_CHANCE = ConfigurableValue.register(this, "nice_list_chance", 0.5, ConfigTypes.PERCENTAGE)
            .name("Nice List Chance")
            .description("Percentage of how often the nice list will be chosen for the vote instead of the naughty list.")
            .group("season.voting");
    public final ConfigHeader GROUP_TRIVIA_QUESTIONS = ConfigHeader.register(this, "group_trivia_questions", "Trivia Questions")
            .group("season.trivia.questions").groupHeader(true);
    public final ConfigurableValue<Boolean> TRIVIA_SHOW_NONSLEEPING = ConfigurableValue.register(this, "trivia_show_nonsleeping", false)
            .name("Display Non-Sleeping Players")
            .description("Displays players that are not sleeping yet when you are in bed.")
            .group("season.trivia");
    public final ConfigurableValue<Boolean> PUBLIC_VOTING = ConfigurableValue.register(this, "public_voting", false)
            .name("Public Voting")
            .description("Displays how many players have voted for each player in the voting screen.")
            .group("season.voting");

    public NiceLifeConfig() {
        super(Seasons.NICE_LIFE);
        removeEntry(NO_SEASON_SPECIFIC);
        BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
        ONLY_ADVANCE_TIME_IN_SESSION.defaultValue(true);
    }

    public static NiceLifeConfig get() {
        return getActive(NiceLifeConfig.class, NiceLifeConfig::new);
    }
}
