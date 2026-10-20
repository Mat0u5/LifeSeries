package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.utils.enums.TriviaGuiType;

import java.util.List;

public class WildLifeConfig extends SeasonConfig {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            //? if >= 1.21
            "mace",
            "end_crystal",
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

    public final ConfigHeader GROUP_GENERAL = ConfigHeader.register(this, "group_general", "General")
            .group("season.general").groupHeader(true);
    public final ConfigHeader GROUP_SIZESHIFTING = ConfigHeader.register(this, "group_sizeshifting", "Size Shifting")
            .group("season.sizeshifting").groupHeader(true);
    public final ConfigHeader GROUP_HUNGER = ConfigHeader.register(this, "group_hunger", "Hunger")
            .group("season.hunger").groupHeader(true);
    public final ConfigHeader GROUP_SNAILS = ConfigHeader.register(this, "group_snails", "Snails")
            .group("season.snails").groupHeader(true);
    public final ConfigHeader GROUP_TIMEDILATION = ConfigHeader.register(this, "group_timedilation", "Time Dilation")
            .group("season.timedilation").groupHeader(true);
    public final ConfigHeader GROUP_TRIVIA = ConfigHeader.register(this, "group_trivia", "Trivia")
            .group("season.trivia").groupHeader(true);
    public final ConfigHeader GROUP_MOBSWAP = ConfigHeader.register(this, "group_mobswap", "Mob Swap")
            .group("season.mobswap").groupHeader(true);
    public final ConfigHeader GROUP_SUPERPOWERS = ConfigHeader.register(this, "group_superpowers", "Superpowers")
            .group("season.superpowers").groupHeader(true);
    public final ConfigHeader GROUP_CALLBACK = ConfigHeader.register(this, "group_callback", "Callback")
            .group("season.callback").groupHeader(true);

    public final ConfigurableValue<Double> ACTIVATE_WILDCARD_MINUTE = ConfigurableValue.register(this, "activate_wildcard_minute", 2.5, ConfigTypes.MINUTES)
            .name("Activate Wildcard Time")
            .description("The number of minutes (in the session) after which the wildcard is activated.")
            .group("season.general");
    public final ConfigurableValue<Boolean> INSTANTLY_ACTIVATE_WILDCARDS = ConfigurableValue.register(this, "instantly_activate_wildcards", false)
            .name("Disable Wildcards Activate Cooldown")
            .description("Skips the wildcard activate dots cooldown.")
            .group("season.general")
            .setNew(true);

    public final ConfigurableValue<Double> WILDCARD_SIZESHIFTING_MIN_SIZE = ConfigurableValue.register(this, "wildcard_sizeshifting_min_size", 0.25)
            .name("Min Size")
            .description("Smallest size you can achieve during Size Shifting.")
            .group("season.sizeshifting");
    public final ConfigurableValue<Double> WILDCARD_SIZESHIFTING_MAX_SIZE = ConfigurableValue.register(this, "wildcard_sizeshifting_max_size", 3.0)
            .name("Max Size")
            .description("Biggest size you can achieve during Size Shifting.")
            .group("season.sizeshifting");
    public final ConfigurableValue<Double> WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER = ConfigurableValue.register(this, "wildcard_sizeshifting_size_change_multiplier", 1.0)
            .name("Change Multiplier")
            .description("The speed with which you change your size during Size Shifting.")
            .group("season.sizeshifting");
    public final ConfigurableValue<Boolean> WILDCARD_SIZESHIFTING_FIX_BUGS = ConfigurableValue.register(this, "wildcard_sizeshifting_fix_bugs", true)
            .name("Fix Bugs")
            .description("Fixes the bug where you fall from blocks when shifting and when you get stuck on blocks when jumping.")
            .group("season.sizeshifting");

    public final ConfigurableValue<Integer> WILDCARD_HUNGER_EFFECT_LEVEL = ConfigurableValue.register(this, "wildcard_hunger_effect_level", 3)
            .name("Hunger Effect Level")
            .description("Controls the hunger effect level.")
            .group("season.hunger");
    public final ConfigurableValue<Integer> WILDCARD_HUNGER_RANDOMIZE_INTERVAL = ConfigurableValue.register(this, "wildcard_hunger_randomize_interval_", 1800, ConfigTypes.SECONDS)
            .name("Randomize Interval")
            .description("The duration between food changes, in seconds.")
            .group("season.hunger");
    public final ConfigurableValue<Double> WILDCARD_HUNGER_NUTRITION_CHANCE = ConfigurableValue.register(this, "wildcard_hunger_nutrition_chance", 0.4, ConfigTypes.PERCENTAGE)
            .name("Nutrition Chance")
            .description("Chance for food to have nutrition (give hunger bars).")
            .group("season.hunger");
    public final ConfigurableValue<Double> WILDCARD_HUNGER_SATURATION_CHANCE = ConfigurableValue.register(this, "wildcard_hunger_saturation_chance", 0.5, ConfigTypes.PERCENTAGE)
            .name("Saturation Chance")
            .description("Chance for food to have saturation (needs to have nutrition to have saturation too btw).")
            .group("season.hunger");
    public final ConfigurableValue<Double> WILDCARD_HUNGER_EFFECT_CHANCE = ConfigurableValue.register(this, "wildcard_hunger_effect_chance", 0.65, ConfigTypes.PERCENTAGE)
            .name("Effect Chance")
            .description("Chance for food to give a random effect.")
            .group("season.hunger");
    public final ConfigurableValue<Integer> WILDCARD_HUNGER_AVG_EFFECT_DURATION = ConfigurableValue.register(this, "wildcard_hunger_avg_effect_duration", 10, ConfigTypes.SECONDS)
            .name("Average Random Effect Duration")
            .description("Average random effect duration, in seconds.")
            .group("season.hunger");
    public final ConfigurableValue<Double> WILDCARD_HUNGER_SOUND_CHANCE = ConfigurableValue.register(this, "wildcard_hunger_sound_chance", 0.01, ConfigTypes.PERCENTAGE)
            .name("Play Sound Chance")
            .description("Chance for food to play a random sound to everyone on the server.")
            .group("season.hunger");
    public final ConfigurableValue<List<String>> WILDCARD_HUNGER_NON_EDIBLE_ITEMS = ConfigurableValue.register(this, "wildcard_hunger_non_edible_items", List.of(), ConfigTypes.ITEM_LIST)
            .name("Non Edible Items")
            .description("A list of items that you can't eat.")
            .group("season.hunger");

    public final ConfigurableValue<Double> WILDCARD_SNAILS_SPEED_MULTIPLIER = ConfigurableValue.register(this, "wildcard_snails_speed_multiplier", 1.0)
            .name("Speed Multiplier")
            .description("Snail movement speed multiplier.")
            .group("season.snails");
    public final ConfigurableValue<Boolean> WILDCARD_SNAILS_DROWN_PLAYERS = ConfigurableValue.register(this, "wildcard_snails_drown_players", true)
            .name("Drown Players")
            .description("Controls whether snails can drown players when the snails are underwater.")
            .group("season.snails");
    public final ConfigurableValue<Boolean> WILDCARD_SNAILS_EFFECTS = ConfigurableValue.register(this, "wildcard_snails_effects", false)
            .name("Can Have Potion Effects")
            .description("Controls whether snails can have potion effects, like invisibility.")
            .group("season.snails");
    public final ConfigurableValue<Boolean> WILDCARD_SNAILS_RED_LIVES = ConfigurableValue.register(this, "wildcard_snails_red_lives", true)
            .name("Can Red Players Have Snails")
            .description("Controls whether red players have snails or not.")
            .group("season.snails");
    public final ConfigurableValue<Integer> WILDCARD_SNAILS_PER_PLAYER = ConfigurableValue.register(this, "wildcard_snails_per_player", 1)
            .name("Number of Snails Per Player")
            .description("Controls how many snails each player has.\n§cNOTE: Higher numbers can be very laggy, use high number with caution.")
            .group("season.snails");
    public final ConfigurableValue<Boolean> WILDCARD_SNAILS_RANDOM_PRESET_SKIN = ConfigurableValue.register(this, "wildcard_snails_random_preset_skin", true)
            .name("Assign Random Preset Snail Skin")
            .description("Assigns a random snail skin preset to any players without their own snail skin.")
            .group("season.snails");
    public final ConfigurableValue<Boolean> WILDCARD_SNAILS_KILL_ANY_PLAYER = ConfigurableValue.register(this, "wildcard_snails_kill_any_player", false)
            .name("Kill Any Player On Touch")
            .description("Makes the snails kill any player they touch, not just their bound player.")
            .group("season.snails")
            .setNew(true);

    public final ConfigurableValue<Double> WILDCARD_TIMEDILATION_MIN_SPEED = ConfigurableValue.register(this, "wildcard_timedilation_min_speed", 0.05)
            .name("Min World Speed Multiplier")
            .description("Controls the minimum speed the WORLD can move.")
            .group("season.timedilation");
    public final ConfigurableValue<Double> WILDCARD_TIMEDILATION_MAX_SPEED = ConfigurableValue.register(this, "wildcard_timedilation_max_speed", 5.0)
            .name("Max World Speed Multiplier")
            .description("Controls the maximum speed the WORLD can move.")
            .group("season.timedilation");
    public final ConfigurableValue<Double> WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED = ConfigurableValue.register(this, "wildcard_timedilation_player_max_speed", 2.0)
            .name("Max Player Speed Multiplier")
            .description("Controls the maximum speed the PLAYERS themselves can move (not the world).")
            .group("season.timedilation");
    public final ConfigurableValue<Boolean> WILDCARD_TIMEDILATION_START_RAIN = ConfigurableValue.register(this, "wildcard_timedilation_start_rain", true)
            .name("Start Rain")
            .description("Controls whether it starts raining when this wildcard starts (helps give an indication of game speed).")
            .group("season.timedilation");

    public final ConfigurableValue<TriviaGuiType> WILDCARD_TRIVIA_GUI_TYPE = ConfigurableValue.register(this, "gui_type", TriviaGuiType.WILD_LIFE)
            .name("Gui Type")
            .description("Controls the trivia gui style.")
            .group("season.trivia");
    public final ConfigurableValue<Boolean> WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS = ConfigurableValue.register(this, "wildcard_trivia_bots_can_enter_boats", true)
            .name("Trivia Bots Can Enter Boats")
            .description("Controls whether trivia bots can enter boats.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> WILDCARD_TRIVIA_BOTS_PER_PLAYER = ConfigurableValue.register(this, "wildcard_trivia_bots_per_player", 5)
            .name("Trivia Bots per Player")
            .description("The amount of trivia bots that will spawn for each player over the session.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> WILDCARD_TRIVIA_SECONDS_EASY = ConfigurableValue.register(this, "wildcard_trivia_seconds_easy", 180, ConfigTypes.SECONDS)
            .name("Easy Timer")
            .description("Easy question timer length, in seconds.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> WILDCARD_TRIVIA_SECONDS_NORMAL = ConfigurableValue.register(this, "wildcard_trivia_seconds_normal", 240, ConfigTypes.SECONDS)
            .name("Normal Timer")
            .description("Normal question timer length, in seconds.")
            .group("season.trivia");
    public final ConfigurableValue<Integer> WILDCARD_TRIVIA_SECONDS_HARD = ConfigurableValue.register(this, "wildcard_trivia_seconds_hard", 300, ConfigTypes.SECONDS)
            .name("Hard Timer")
            .description("Hard question timer length, in seconds.")
            .group("season.trivia");
    public final ConfigurableValue<List<String>> WILDCARD_TRIVIA_EFFECTS = ConfigurableValue.register(this, "wildcard_trivia_effects", List.of("speed", "haste", "strength", "jump_boost", "resistance", "regeneration", "fire_resistance", "water_breathing", "night_vision", "health_boost", "absorption"), ConfigTypes.EFFECT_LIST)
            .name("Trivia Reward Effects")
            .description("List of potion effects you can get from trivia rewards.")
            .group("season.trivia")
            .setNew(true);
    public final ConfigHeader GROUP_TRIVIA_QUESTIONS = ConfigHeader.register(this, "group_trivia_questions", "Trivia Questions")
            .group("season.trivia.questions").groupHeader(true);
    public final ConfigHeader GROUP_TRIVIA_QUESTIONS_EASY = ConfigHeader.register(this, "group_trivia_questions_easy", "Easy Questions")
            .group("season.trivia.questions.easy").groupHeader(true);
    public final ConfigHeader GROUP_TRIVIA_QUESTIONS_NORMAL = ConfigHeader.register(this, "group_trivia_questions_normal", "Normal Questions")
            .group("season.trivia.questions.normal").groupHeader(true);
    public final ConfigHeader GROUP_TRIVIA_QUESTIONS_HARD = ConfigHeader.register(this, "group_trivia_questions_hard", "Hard Questions")
            .group("season.trivia.questions.hard").groupHeader(true);

    public final ConfigurableValue<Integer> WILDCARD_MOBSWAP_START_SPAWN_DELAY = ConfigurableValue.register(this, "wildcard_mobswap_start_spawn_delay_", 360, ConfigTypes.SECONDS)
            .name("Session Start Spawn Delay")
            .description("The delay between mob spawns at the START of the session, in seconds.")
            .group("season.mobswap");
    public final ConfigurableValue<Integer> WILDCARD_MOBSWAP_END_SPAWN_DELAY = ConfigurableValue.register(this, "wildcard_mobswap_end_spawn_delay_", 120, ConfigTypes.SECONDS)
            .name("Session End Spawn Delay")
            .description("The delay between mob spawns at the END of the session, in seconds.")
            .group("season.mobswap");
    public final ConfigurableValue<Integer> WILDCARD_MOBSWAP_SPAWN_MOBS = ConfigurableValue.register(this, "wildcard_mobswap_spawn_mobs", 250)
            .name("Number of Mobs")
            .description("The number of mobs that spawn each cycle.")
            .group("season.mobswap");
    public final ConfigurableValue<Double> WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER = ConfigurableValue.register(this, "wildcard_mobswap_boss_chance_multiplier", 1.0)
            .name("Boss Chance Multiplier")
            .description("Multiplier for boss chance (wither / warden).")
            .group("season.mobswap");
    public final ConfigurableValue<Double> WILDCARD_MOBSWAP_BOSS_SPAWN_CUTOFF = ConfigurableValue.register(this, "wildcard_mobswap_boss_spawn_cutoff", 0.7, ConfigTypes.PERCENTAGE)
            .name("Boss Spawn Session Percentage")
            .description("Controls after which point in the session bosses can spawn.")
            .group("season.mobswap");

    public final ConfigurableValue<List<String>> WILDCARD_SUPERPOWERS_POWER_BLACKLIST = ConfigurableValue.register(this, "wildcard_superpowers_power_blacklist", List.of(), ConfigTypes.stringListOf(() -> Superpowers.getAllStr()))
            .name("Blacklisted Powers")
            .description("List of superpowers that cannot be rolled randomly.")
            .group("season.superpowers");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = ConfigurableValue.register(this, "wildcard_superpowers_disable_intro_theme", false)
            .name("Disable Intro Theme")
            .description("Disables the theme music that plays when this wildcard is activated.")
            .group("season.superpowers");

    // Animal Disguise
    public final ConfigHeader SUPERPOWER_ANIMAL_DISGUISE = ConfigHeader.register(this, "superpower_animal_disguise", "Animal Disguise")
            .group("season.superpowers.animal_disguise").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_ANIMAL_DISGUISE = ConfigurableValue.register(this, "superpower_cooldown_animal_disguise", 1, ConfigTypes.SECONDS)
            .name("Animal Disguise Power Cooldown")
            .group("season.superpowers.animal_disguise");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR = ConfigurableValue.register(this, "wildcard_superpowers_animaldisguise_armor", false)
            .name("Show Armor")
            .description("Controls whether armor is seen on players disguised as mobs.")
            .group("season.superpowers.animal_disguise");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS = ConfigurableValue.register(this, "wildcard_superpowers_animaldisguise_hands", true)
            .name("Show Hand Items")
            .description("Controls whether hand items are seen on players disguised as mobs.")
            .group("season.superpowers.animal_disguise");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ANIMAL_DISGUISE_DAMAGE_CANCELS = ConfigurableValue.register(this, "wildcard_superpowers_animal_disguise_damage_cancels", true)
            .name("Damage Cancels Disguise")
            .description("Controls if your disguise ends when you take damage.")
            .group("season.superpowers.animal_disguise");

    // Astral Projection
    public final ConfigHeader SUPERPOWER_ASTRAL_PROJECTION = ConfigHeader.register(this, "superpower_astral_projection", "Astral Projection")
            .group("season.superpowers.astral_projection").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_ASTRAL_PROJECTION = ConfigurableValue.register(this, "superpower_cooldown_astral_projection", 5, ConfigTypes.SECONDS)
            .name("Astral Projection Power Cooldown")
            .group("season.superpowers.astral_projection");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ASTRAL_PROJECTION_DAMAGE_CANCELS = ConfigurableValue.register(this, "wildcard_superpowers_astral_projection_damage_cancels", true)
            .name("Damage Cancels Projection")
            .description("Controls if your projection ends when you take damage.")
            .group("season.superpowers.astral_projection");

    // Creaking
    public final ConfigHeader SUPERPOWER_CREAKING = ConfigHeader.register(this, "superpower_creaking", "Creaking")
            .group("season.superpowers.creaking").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_CREAKING = ConfigurableValue.register(this, "superpower_cooldown_creaking", 10, ConfigTypes.SECONDS)
            .name("Creaking Power Cooldown")
            .group("season.superpowers.creaking");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_CREAKING_AMOUNT = ConfigurableValue.register(this, "wildcard_superpowers_creaking_amount", 3)
            .name("Creaking Amount")
            .description("Controls the number of creakings that spawn.")
            .group("season.superpowers.creaking");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_CREAKING_PARTICLES = ConfigurableValue.register(this, "wildcard_superpowers_creaking_particles", true)
            .name("Show Trail Particles")
            .description("Controls whether the trail particles towards the creaking show up.")
            .group("season.superpowers.creaking");

    // Flight
    public final ConfigHeader SUPERPOWER_FLIGHT = ConfigHeader.register(this, "superpower_flight", "Flight")
            .group("season.superpowers.flight").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_FLIGHT = ConfigurableValue.register(this, "superpower_cooldown_flight", 45, ConfigTypes.SECONDS)
            .name("Flight Power Cooldown")
            .group("season.superpowers.flight");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_FLIGHT_JUMP_AMPLIFIER = ConfigurableValue.register(this, "wildcard_superpowers_flight_jump_amplifier", 54)
            .name("Jump Boost Amplifier")
            .description("Controls the jump boost amplifier.")
            .group("season.superpowers.flight");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_FLIGHT_ELYTRA_LAUNCH_NEEDED = ConfigurableValue.register(this, "wildcard_superpowers_flight_elytra_launch_needed", true)
            .name("Needs Launch To Use Elytra")
            .group("season.superpowers.flight");

    // Invisibility
    public final ConfigHeader SUPERPOWER_INVISIBILITY = ConfigHeader.register(this, "superpower_invisibility", "Invisibility")
            .group("season.superpowers.invisibility").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_INVISIBILITY = ConfigurableValue.register(this, "superpower_cooldown_invisibility", 1, ConfigTypes.SECONDS)
            .name("Invisibility Power Cooldown")
            .group("season.superpowers.invisibility");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_DAMAGE_CANCELS = ConfigurableValue.register(this, "wildcard_superpowers_invisibility_damage_cancels", true)
            .name("Taking Damage Cancels Invis")
            .group("season.superpowers.invisibility");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_ATTACK_CANCELS = ConfigurableValue.register(this, "wildcard_superpowers_invisibility_attack_cancels", true)
            .name("Attacking Cancels Invis")
            .group("season.superpowers.invisibility");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_SHOW_PARTICLES = ConfigurableValue.register(this, "wildcard_superpowers_invisibility_show_particles", true)
            .name("Show Transluscent Particles")
            .group("season.superpowers.invisibility");

    // Mimicry
    public final ConfigHeader SUPERPOWER_MIMICRY = ConfigHeader.register(this, "superpower_mimicry", "Mimicry")
            .group("season.superpowers.mimicry").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_MIMICRY = ConfigurableValue.register(this, "superpower_cooldown_mimicry", 300, ConfigTypes.SECONDS)
            .name("Mimicry Power Cooldown")
            .group("season.superpowers.mimicry");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_MIMICRY_DISABLE_OTHER_POWER = ConfigurableValue.register(this, "wildcard_superpowers_mimicry_disable_other_power", false)
            .name("Disable Stolen Power")
            .description("Disables the power of the person you mimic for the duration of the effect.")
            .group("season.superpowers.mimicry")
            .setNew(true);

    // Necromancy
    public final ConfigHeader SUPERPOWER_NECROMANCY = ConfigHeader.register(this, "superpower_necromancy", "Necromancy")
            .group("season.superpowers.necromancy").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_NECROMANCY = ConfigurableValue.register(this, "superpower_cooldown_necromancy", 300, ConfigTypes.SECONDS)
            .name("Necromancy Power Cooldown")
            .group("season.superpowers.necromancy");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS = ConfigurableValue.register(this, "wildcard_superpowers_zombies_first_spawn_clear_items", true)
            .name("Zombies First Spawn Clear Items")
            .description("Controls whether zombies get cleared when they first get respawned.")
            .group("season.superpowers.necromancy");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY = ConfigurableValue.register(this, "wildcard_superpowers_zombies_keep_inventory", true)
            .name("Zombies Keep Inventory")
            .description("Controls whether zombies keep their items when they die.")
            .group("season.superpowers.necromancy");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN = ConfigurableValue.register(this, "wildcard_superpowers_zombies_revive_by_killing_dark_green", false)
            .name("Zombies Can Revive")
            .description("Controls whether zombies can be revived (gain a life) by killing a dark green player.")
            .group("season.superpowers.necromancy");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH = ConfigurableValue.register(this, "wildcard_superpowers_zombies_health", 8)
            .name("Zombie Health Amount")
            .description("Controls how much health zombies will have.")
            .group("season.superpowers.necromancy");

    // Player Disguise
    public final ConfigHeader SUPERPOWER_PLAYER_DISGUISE = ConfigHeader.register(this, "superpower_player_disguise", "Player Disguise")
            .group("season.superpowers.player_disguise").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_PLAYER_DISGUISE = ConfigurableValue.register(this, "superpower_cooldown_player_disguise", 10, ConfigTypes.SECONDS)
            .name("Player Disguise Power Cooldown")
            .group("season.superpowers.player_disguise");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_PLAYER_DISGUISE_DAMAGE_CANCELS = ConfigurableValue.register(this, "wildcard_superpowers_player_disguise_damage_cancels", true)
            .name("Damage Cancels Disguise")
            .description("Controls if your disguise ends when you take damage.")
            .group("season.superpowers.player_disguise");

    // Shadow Play
    public final ConfigHeader SUPERPOWER_SHADOW_PLAY = ConfigHeader.register(this, "superpower_shadow_play", "Shadow Play")
            .group("season.superpowers.shadow_play").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_SHADOW_PLAY = ConfigurableValue.register(this, "superpower_cooldown_shadow_play", 30, ConfigTypes.SECONDS)
            .name("Shadow Play Power Cooldown")
            .group("season.superpowers.shadow_play");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_RANGE = ConfigurableValue.register(this, "wildcard_superpowers_shadow_play_blind_range", 10)
            .name("Blind Range")
            .description("Controls the range in which players are blinded.")
            .group("season.superpowers.shadow_play");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_TIME = ConfigurableValue.register(this, "wildcard_superpowers_shadow_play_blind_time", 5, ConfigTypes.SECONDS)
            .name("Blind Time")
            .description("Controls the time for which players are blinded.")
            .group("season.superpowers.shadow_play");

    // Super Punch
    public final ConfigHeader SUPERPOWER_SUPER_PUNCH = ConfigHeader.register(this, "superpower_super_punch", "Super Punch")
            .group("season.superpowers.super_punch").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_SUPER_PUNCH = ConfigurableValue.register(this, "superpower_cooldown_super_punch", 1, ConfigTypes.SECONDS)
            .name("Super Punch Power Cooldown")
            .group("season.superpowers.super_punch");
    public final ConfigurableValue<Double> WILDCARD_SUPERPOWERS_SUPER_PUNCH_THORNS_DAMAGE = ConfigurableValue.register(this, "wildcard_superpowers_super_punch_thorns_damage", 1.0)
            .name("Thorns Damage")
            .description("Controls the damage others take when they hit you.")
            .group("season.superpowers.super_punch");
    public final ConfigurableValue<Double> WILDCARD_SUPERPOWERS_SUPER_PUNCH_KNOCKBACK_STRENGTH = ConfigurableValue.register(this, "wildcard_superpowers_super_punch_knockback_strength", 3.0)
            .name("Knockback Strength")
            .description("Controls the knockback strength when you punch someone.")
            .group("season.superpowers.super_punch");

    // Superspeed
    public final ConfigHeader SUPERPOWER_SUPERSPEED = ConfigHeader.register(this, "superpower_superspeed", "Superspeed")
            .group("season.superpowers.superspeed").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_SUPERSPEED = ConfigurableValue.register(this, "superpower_cooldown_superspeed", 3, ConfigTypes.SECONDS)
            .name("Superspeed Power Cooldown")
            .group("season.superpowers.superspeed");
    public final ConfigurableValue<Boolean> WILDCARD_SUPERPOWERS_SUPERSPEED_STEP = ConfigurableValue.register(this, "wildcard_superpowers_superspeed_step", false)
            .name("Step Up Blocks")
            .description("Controls whether players with the superspeed power active can step up blocks without jumping (like when riding a horse).")
            .group("season.superpowers.superspeed");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_SUPERSPEED_FROST_WALKER_LEVEL = ConfigurableValue.register(this, "wildcard_superpowers_superspeed_frost_walker_level", 3)
            .name("Frost Walker Level")
            .description("Controls the frost walker enchant level.")
            .group("season.superpowers.superspeed");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_SUPERSPEED_HUNGER_LEVEL = ConfigurableValue.register(this, "wildcard_superpowers_superspeed_hunger_level", 5)
            .name("Hunger Level")
            .description("Controls the hunger level amplifier.")
            .group("season.superpowers.superspeed");
    public final ConfigurableValue<Double> WILDCARD_SUPERPOWERS_SUPERSPEED_TARGET_SPEED = ConfigurableValue.register(this, "wildcard_superpowers_superspeed_target_speed", 0.35)
            .name("Maximum Speed")
            .description("Controls the maximum speed.")
            .group("season.superpowers.superspeed");

    // Teleportation
    public final ConfigHeader SUPERPOWER_TELEPORTATION = ConfigHeader.register(this, "superpower_teleportation", "Teleportation")
            .group("season.superpowers.teleportation").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_TELEPORTATION = ConfigurableValue.register(this, "superpower_cooldown_teleportation", 5, ConfigTypes.SECONDS)
            .name("Teleportation Power Cooldown")
            .group("season.superpowers.teleportation");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_TELEPORTATION_SWAP_DISTANCE = ConfigurableValue.register(this, "wildcard_superpowers_teleportation_swap_distance", 100)
            .name("Max Swap Distance")
            .description("Controls the max player swap distance.\nVery high numbers may cause performance issues.")
            .group("season.superpowers.teleportation");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_TELEPORTATION_TP_DISTANCE = ConfigurableValue.register(this, "wildcard_superpowers_teleportation_tp_distance", 100)
            .name("Max Teleport Distance")
            .description("Controls the max teleport distance.\nVery high numbers may cause performance issues.")
            .group("season.superpowers.teleportation");

    // Time Control
    public final ConfigHeader SUPERPOWER_TIME_CONTROL = ConfigHeader.register(this, "superpower_time_control", "Time Control")
            .group("season.superpowers.time_control").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_TIME_CONTROL = ConfigurableValue.register(this, "superpower_cooldown_time_control", 300, ConfigTypes.SECONDS)
            .name("Time Control Power Cooldown")
            .group("season.superpowers.time_control");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_TIME_DILATION_TICK_RATE = ConfigurableValue.register(this, "wildcard_superpowers_time_dilation_tick_rate", 4)
            .name("Target Tick Rate")
            .description("Controls the target tick rate.")
            .group("season.superpowers.time_control");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_TIME_DILATION_DURATION = ConfigurableValue.register(this, "wildcard_superpowers_time_dilation_duration", 70)
            .name("Slow Duration")
            .description("Controls the slowdown duration.")
            .group("season.superpowers.time_control");

    // Triple Jump
    public final ConfigHeader SUPERPOWER_TRIPLE_JUMP = ConfigHeader.register(this, "superpower_triple_jump", "Triple Jump")
            .group("season.superpowers.triple_jump").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_TRIPLE_JUMP = ConfigurableValue.register(this, "superpower_cooldown_triple_jump", 1, ConfigTypes.SECONDS)
            .name("Triple Jump Power Cooldown")
            .group("season.superpowers.triple_jump");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_TRIPLE_JUMP_JUMPS = ConfigurableValue.register(this, "wildcard_superpowers_triple_jump_jumps", 3)
            .name("Max Jump Amount")
            .description("Controls the maximum jump amount.")
            .group("season.superpowers.triple_jump");

    // Wind Charge
    public final ConfigHeader SUPERPOWER_WIND_CHARGE = ConfigHeader.register(this, "superpower_wind_charge", "Wind Charge")
            .group("season.superpowers.wind_charge").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_WIND_CHARGE = ConfigurableValue.register(this, "superpower_cooldown_wind_charge", 1, ConfigTypes.SECONDS)
            .name("Wind Charge Power Cooldown")
            .group("season.superpowers.wind_charge");
    public final ConfigurableValue<Integer> WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE = ConfigurableValue.register(this, "wildcard_superpowers_windcharge_max_mace_damage", 2)
            .name("Max Mace Damage")
            .description("The max amount of damage you can deal with a mace while using the Wind Charge superpower.")
            .group("season.superpowers.wind_charge");
    public final ConfigurableValue<Double> WILDCARD_SUPERPOWERS_WIND_CHARGE_EXPLOSION_POWER = ConfigurableValue.register(this, "wildcard_superpowers_wind_charge_explosion_power", 3.0)
            .name("Wind Charge Explosion Power")
            .description("Controls the explosion power of wind charges.")
            .group("season.superpowers.wind_charge");

    public final ConfigurableValue<List<String>> WILDCARD_CALLBACK_WILDCARDS_BLACKLIST = ConfigurableValue.register(this, "wildcard_callback_wildcards_blacklist", List.of("hunger"), ConfigTypes.stringListOf(() -> Wildcards.getWildcardsStr()))
            .name("Blacklisted Wildcards")
            .description("List of wildcards that cannot be activated in Callback.")
            .group("season.callback");
    public final ConfigurableValue<Double> WILDCARD_CALLBACK_TURN_OFF = ConfigurableValue.register(this, "wildcard_callback_turn_off", 0.75, ConfigTypes.PERCENTAGE)
            .name("Turn Off In Session")
            .description("Controls when in the session the callback wildcard turns off (percentage).")
            .group("season.callback");
    public final ConfigurableValue<Boolean> WILDCARD_CALLBACK_NERFED_WILDCARDS = ConfigurableValue.register(this, "wildcard_callback_nerfed_wildcards", true)
            .name("Nerfed Wildcards")
            .description("Controls whether wildcards are nerfed in callback (recommended).")
            .group("season.callback");
    public final ConfigurableValue<Integer> WILDCARD_CALLBACK_INITIAL_ACTIVATION_INTERVAL = ConfigurableValue.register(this, "wildcard_callback_initial_activation_interval", 300, ConfigTypes.SECONDS)
            .name("Initial Activation Interval")
            .description("Controls in what intervals will the wildcards get activated (it speeds up throughout the session). Changing this mid-session might have unexpected consequences.")
            .group("season.callback");

    public final ConfigHeader SUPERPOWER_LISTENING = ConfigHeader.register(this, "superpower_listening", "Listening")
            .group("season.superpowers.listening").groupHeader(true);
    public final ConfigurableValue<Integer> SUPERPOWER_COOLDOWN_LISTENING = ConfigurableValue.register(this, "superpower_cooldown_listening", 1, ConfigTypes.SECONDS)
            .name("Listening Power Cooldown")
            .group("season.superpowers.listening");

    public WildLifeConfig() {
        super(Seasons.WILD_LIFE);
        removeEntry(NO_SEASON_SPECIFIC);
        //? if < 1.21.2 {
        /*removeEntry(SUPERPOWER_CREAKING);
        removeEntry(SUPERPOWER_COOLDOWN_CREAKING);
        removeEntry(WILDCARD_SUPERPOWERS_CREAKING_AMOUNT);
        removeEntry(WILDCARD_SUPERPOWERS_CREAKING_PARTICLES);
        removeEntry(SUPERPOWER_FLIGHT);
        removeEntry(SUPERPOWER_COOLDOWN_FLIGHT);
        removeEntry(WILDCARD_SUPERPOWERS_FLIGHT_JUMP_AMPLIFIER);
        removeEntry(WILDCARD_SUPERPOWERS_FLIGHT_ELYTRA_LAUNCH_NEEDED);
        *///?}
        //? if <= 1.20.3 {
        /*removeEntry(SUPERPOWER_SUPERSPEED);
        removeEntry(SUPERPOWER_COOLDOWN_SUPERSPEED);
        removeEntry(WILDCARD_SUPERPOWERS_SUPERSPEED_STEP);
        removeEntry(WILDCARD_SUPERPOWERS_SUPERSPEED_FROST_WALKER_LEVEL);
        removeEntry(WILDCARD_SUPERPOWERS_SUPERSPEED_HUNGER_LEVEL);
        removeEntry(WILDCARD_SUPERPOWERS_SUPERSPEED_TARGET_SPEED);
        *///?}
        //? if < 1.20.3 {
        /*removeEntry(SUPERPOWER_TIME_CONTROL);
        removeEntry(SUPERPOWER_COOLDOWN_TIME_CONTROL);
        removeEntry(WILDCARD_SUPERPOWERS_TIME_DILATION_TICK_RATE);
        removeEntry(WILDCARD_SUPERPOWERS_TIME_DILATION_DURATION);
        *///?}
        //? if < 1.21 {
        /*removeEntry(SUPERPOWER_WIND_CHARGE);
        removeEntry(SUPERPOWER_COOLDOWN_WIND_CHARGE);
        removeEntry(WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE);
        removeEntry(WILDCARD_SUPERPOWERS_WIND_CHARGE_EXPLOSION_POWER);
        *///?}
        if (!CompatibilityManager.voicechatLoaded()) {
            removeEntry(SUPERPOWER_LISTENING);
            removeEntry(SUPERPOWER_COOLDOWN_LISTENING);
        }

        CUSTOM_ENCHANTER_ALGORITHM.defaultValue(true);
        BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue(6);
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue(true);
        SPAWNER_RECIPE.defaultValue(true);
        TAB_LIST_SHOW_LIVES.defaultValue(true);

        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");
    }

    public static WildLifeConfig get() {
        return getActive(WildLifeConfig.class, WildLifeConfig::new);
    }
}
