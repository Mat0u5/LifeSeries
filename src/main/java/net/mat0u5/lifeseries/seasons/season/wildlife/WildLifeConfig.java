package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class WildLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "mace",
            "end_crystal",
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

    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_MIN_SIZE = new ConfigFileEntry<>(
            "wildcard_sizeshifting_min_size", 0.25, "season.sizeshifting",
            "Min Size", "Smallest size you can achieve during Size Shifting."
    );
    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_MAX_SIZE = new ConfigFileEntry<>(
            "wildcard_sizeshifting_max_size", 3.0, "season.sizeshifting",
            "Max Size", "Biggest size you can achieve during Size Shifting."
    );
    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_sizeshifting_size_change_multiplier", 1.0, "season.sizeshifting",
            "Change Multiplier", "The speed with which you change your size during Size Shifting."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_RANDOMIZE_INTERVAL = new ConfigFileEntry<>(
            "wildcard_hunger_randomize_interval_", 1800, ConfigTypes.SECONDS, "season.hunger",
            "Randomize Interval", "The duration between food changes, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_EFFECT_LEVEL = new ConfigFileEntry<>(
            "wildcard_hunger_effect_level", 3, "season.hunger",
            "Hunger Effect Level", "Controls the hunger effect level."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_NUTRITION_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_nutrition_chance", 0.4, ConfigTypes.PERCENTAGE, "season.hunger[new]",
            "Nutrition Chance", "Chance for food to have nutrition (give hunger bars)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_SATURATION_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_saturation_chance", 0.5, ConfigTypes.PERCENTAGE, "season.hunger[new]",
            "Saturation Chance", "Chance for food to have saturation (needs to have nutrition to have saturation too btw)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_EFFECT_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_effect_chance", 0.65, ConfigTypes.PERCENTAGE, "season.hunger[new]",
            "Effect Chance", "Chance for food to give a random effect."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_AVG_EFFECT_DURATION = new ConfigFileEntry<>(
            "wildcard_hunger_avg_effect_duration", 10, ConfigTypes.SECONDS, "season.hunger[new]",
            "Average Random Effect Duration", "Average random effect duration, in seconds."
    );

    public static final ConfigFileEntry<Double> WILDCARD_SNAILS_SPEED_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_snails_speed_multiplier", 1.0, "season.snails",
            "Speed Multiplier", "Snail movement speed multiplier."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SNAILS_DROWN_PLAYERS = new ConfigFileEntry<>(
            "wildcard_snails_drown_players", true, "season.snails",
            "Drown Players", "Controls whether snails can drown players when the snails are underwater."
    );


    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_MIN_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_min_speed", 0.05, "season.timedilation",
            "Min World Speed Multiplier", "Controls the minimum speed the WORLD can move."
    );
    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_MAX_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_max_speed", 5.0, "season.timedilation",
            "Max World Speed Multiplier", "Controls the maximum speed the WORLD can move."
    );
    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_player_max_speed", 2.0, "season.timedilation",
            "Max Player Speed Multiplier", "Controls the maximum speed the PLAYERS themselves can move (not the world)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS = new ConfigFileEntry<>(
            "wildcard_trivia_bots_can_enter_boats", true, "season.trivia",
            "Trivia Bots Can Enter Boats", "Controls whether trivia bots can enter boats."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_TRIVIA_BOTS_PER_PLAYER = new ConfigFileEntry<>(
            "wildcard_trivia_bots_per_player", 5, "season.trivia",
            "Trivia Bots per Player", "The amount of trivia bots that will spawn for each player over the session."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_TRIVIA_SECONDS_EASY = new ConfigFileEntry<>(
            "wildcard_trivia_seconds_easy", 180, ConfigTypes.SECONDS, "season.trivia",
            "Easy Timer", "Easy question timer length, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_TRIVIA_SECONDS_NORMAL = new ConfigFileEntry<>(
            "wildcard_trivia_seconds_normal", 240, ConfigTypes.SECONDS, "season.trivia",
            "Normal Timer", "Normal question timer length, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_TRIVIA_SECONDS_HARD = new ConfigFileEntry<>(
            "wildcard_trivia_seconds_hard", 300, ConfigTypes.SECONDS, "season.trivia",
            "Hard Timer", "Hard question timer length, in seconds."
    );


    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_START_SPAWN_DELAY = new ConfigFileEntry<>(
            "wildcard_mobswap_start_spawn_delay_", 360, ConfigTypes.SECONDS, "season.mobswap",
            "Session Start Spawn Delay", "The delay between mob spawns at the START of the session, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_END_SPAWN_DELAY = new ConfigFileEntry<>(
            "wildcard_mobswap_end_spawn_delay_", 120, ConfigTypes.SECONDS, "season.mobswap",
            "Session End Spawn Delay", "The delay between mob spawns at the END of the session, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_SPAWN_MOBS = new ConfigFileEntry<>(
            "wildcard_mobswap_spawn_mobs", 250, "season.mobswap",
            "Number of Mobs", "The number of mobs that spawn each cycle."
    );
    public static final ConfigFileEntry<Double> WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_mobswap_boss_chance_multiplier", 1.0, "season.mobswap",
            "Boss Chance Multiplier", "Multiplier for boss chance (wither / warden)."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE = new ConfigFileEntry<>(
            "wildcard_superpowers_windcharge_max_mace_damage", 2, "season.superpowers",
            "Wind Charge: Max Mace Damage", "The max amount of damage you can deal with a mace while using the Wind Charge superpower."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_lose_items", true, "season.superpowers",
            "Necromancy: Zombies Lose Items", "Controls whether zombies keep their items when they first get respawned."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_revive_by_killing_dark_green", false, "season.superpowers",
            "Necromancy: Zombies Can Revive", "Controls whether zombies can be revived (gain a life) by killing a dark green player."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_SUPERSPEED_STEP = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_step", false, "season.superpowers",
            "Superspeed: Step Up Blocks", "Controls whether players with the superspeed power active can step up blocks without jumping (like when riding a horse)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = new ConfigFileEntry<>(
            "wildcard_superpowers_disable_intro_theme", false, "season.superpowers",
            "Disable Intro Theme", "Disables the theme music that plays when this curse is activated."
    );
    public static final ConfigFileEntry<String> WILDCARD_SUPERPOWERS_POWER_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_superpowers_power_blacklist", "[]", ConfigTypes.STRING_LIST, "season.superpowers[new]",
            "Blacklisted Powers", "List of superpowers that cannot be rolled randomly.", Superpowers.getAllStr()
    );


    public static final ConfigFileEntry<String> WILDCARD_CALLBACK_WILDCARDS_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_callback_wildcards_blacklist", "[hunger]", ConfigTypes.STRING_LIST, "season.callback[new]",
            "Blacklisted Curses", "List of curses that cannot be activated in Callback.", Wildcards.getWildcardsStr()
    );
    public static final ConfigFileEntry<Double> WILDCARD_CALLBACK_TURN_OFF = new ConfigFileEntry<>(
            "wildcard_callback_turn_off", 0.75, ConfigTypes.PERCENTAGE, "season.callback[new]",
            "Turn Off In Session", "Controls when in the session the callback curse turns off (percentage)."
    );

    //Groups
    public static final ConfigFileEntry<Object> GROUP_GENERAL = new ConfigFileEntry<>(
            "group_general", null, ConfigTypes.TEXT, "{season.general}",
            "General", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SIZESHIFTING = new ConfigFileEntry<>(
            "group_sizeshifting", null, ConfigTypes.TEXT, "{season.sizeshifting}",
            "Size Shifting", ""
    );
    public static final ConfigFileEntry<Object> GROUP_HUNGER = new ConfigFileEntry<>(
            "group_hunger", null, ConfigTypes.TEXT, "{season.hunger}",
            "Hunger", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SNAILS = new ConfigFileEntry<>(
            "group_snails", null, ConfigTypes.TEXT, "{season.snails}",
            "Snails", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TIMEDILATION = new ConfigFileEntry<>(
            "group_timedilation", null, ConfigTypes.TEXT, "{season.timedilation}",
            "Time Dilation", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA = new ConfigFileEntry<>(
            "group_trivia", null, ConfigTypes.TEXT, "{season.trivia}",
            "Trivia", ""
    );
    public static final ConfigFileEntry<Object> GROUP_MOBSWAP = new ConfigFileEntry<>(
            "group_mobswap", null, ConfigTypes.TEXT, "{season.mobswap}",
            "Mob Swap", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SUPERPOWERS = new ConfigFileEntry<>(
            "group_superpowers", null, ConfigTypes.TEXT, "{season.superpowers}",
            "Superpowers", ""
    );
    public static final ConfigFileEntry<Object> GROUP_CALLBACK = new ConfigFileEntry<>(
            "group_callback", null, ConfigTypes.TEXT, "{season.callback}",
            "Callback", ""
    );

    public static final ConfigFileEntry<Double> ACTIVATE_WILDCARD_MINUTE = new ConfigFileEntry<>(
            "activate_wildcard_minute", 2.5, ConfigTypes.MINUTES, "season.general",
            "Activate Curse Time", "The number of minutes (in the session) after which a curse is activated."
    );
    public static final ConfigFileEntry<Boolean> KILLING_DARK_GREENS_GAINS_LIVES = new ConfigFileEntry<>(
            "killing_dark_greens_gains_lives", true, "{season.general.darkgreen}",
            "Killing Dark Greens Gains Lives", "Controls whether killing dark green players (4+ lives) gives the killer a life."
    );
    public static final ConfigFileEntry<Boolean> BROADCAST_LIFE_GAIN = new ConfigFileEntry<>(
            "broadcast_life_gain", false, "season.general.darkgreen",
            "Broadcast Life Gain", "Shows a message in chat when a player gains a life by killing a dark green player."
    );

    public WildLifeConfig() {
        super("./config/"+ Main.MOD_ID,"wildlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                GROUP_GENERAL //Group
                    ,KILLING_DARK_GREENS_GAINS_LIVES//Group
                ,GROUP_SIZESHIFTING //Group
                ,GROUP_HUNGER //Group
                ,GROUP_SNAILS //Group
                ,GROUP_TIMEDILATION //Group
                ,GROUP_TRIVIA //Group
                ,GROUP_MOBSWAP //Group
                ,GROUP_SUPERPOWERS //Group
                ,GROUP_CALLBACK //Group

                //Group stuff
                ,BROADCAST_LIFE_GAIN
                ,ACTIVATE_WILDCARD_MINUTE

                ,WILDCARD_SIZESHIFTING_MIN_SIZE
                ,WILDCARD_SIZESHIFTING_MAX_SIZE
                ,WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER

                ,WILDCARD_HUNGER_EFFECT_LEVEL
                ,WILDCARD_HUNGER_RANDOMIZE_INTERVAL
                ,WILDCARD_HUNGER_NUTRITION_CHANCE
                ,WILDCARD_HUNGER_SATURATION_CHANCE
                ,WILDCARD_HUNGER_EFFECT_CHANCE
                ,WILDCARD_HUNGER_AVG_EFFECT_DURATION

                ,WILDCARD_SNAILS_SPEED_MULTIPLIER
                ,WILDCARD_SNAILS_DROWN_PLAYERS

                ,WILDCARD_TIMEDILATION_MIN_SPEED
                ,WILDCARD_TIMEDILATION_MAX_SPEED
                ,WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED

                ,WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS
                ,WILDCARD_TRIVIA_BOTS_PER_PLAYER
                ,WILDCARD_TRIVIA_SECONDS_EASY
                ,WILDCARD_TRIVIA_SECONDS_NORMAL
                ,WILDCARD_TRIVIA_SECONDS_HARD

                ,WILDCARD_MOBSWAP_START_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_END_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_SPAWN_MOBS
                ,WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER

                ,WILDCARD_SUPERPOWERS_POWER_BLACKLIST
                ,WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME
                ,WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE
                ,WILDCARD_SUPERPOWERS_ZOMBIES_LOSE_ITEMS
                ,WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_STEP

                ,WILDCARD_CALLBACK_WILDCARDS_BLACKLIST
                ,WILDCARD_CALLBACK_TURN_OFF
        ));
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue = 6;
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
        SPAWNER_RECIPE.defaultValue = true;
        TAB_LIST_SHOW_LIVES.defaultValue = true;

        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");

        super.instantiateProperties();
    }
}
