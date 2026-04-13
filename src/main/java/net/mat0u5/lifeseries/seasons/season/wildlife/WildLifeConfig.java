package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;

import java.util.ArrayList;
import java.util.List;

public class WildLifeConfig extends SeasonConfig {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            //? if >= 1.21
            "mace",
            "end_crystal",
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
    public static final ConfigFileEntry<Boolean> WILDCARD_SIZESHIFTING_FIX_BUGS = new ConfigFileEntry<>(
            "wildcard_sizeshifting_fix_bugs", true, "season.sizeshifting",
            "Fix Bugs", "Fixes the bug where you fall from blocks when shifting and when you get stuck on blocks when jumping."
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
            "wildcard_hunger_nutrition_chance", 0.4, ConfigTypes.PERCENTAGE, "season.hunger",
            "Nutrition Chance", "Chance for food to have nutrition (give hunger bars)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_SATURATION_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_saturation_chance", 0.5, ConfigTypes.PERCENTAGE, "season.hunger",
            "Saturation Chance", "Chance for food to have saturation (needs to have nutrition to have saturation too btw)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_EFFECT_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_effect_chance", 0.65, ConfigTypes.PERCENTAGE, "season.hunger",
            "Effect Chance", "Chance for food to give a random effect."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_AVG_EFFECT_DURATION = new ConfigFileEntry<>(
            "wildcard_hunger_avg_effect_duration", 10, ConfigTypes.SECONDS, "season.hunger",
            "Average Random Effect Duration", "Average random effect duration, in seconds."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_SOUND_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_sound_chance", 0.01, ConfigTypes.PERCENTAGE, "season.hunger",
            "Play Sound Chance", "Chance for food to play a random sound to everyone on the server."
    );
    public static final ConfigFileEntry<String> WILDCARD_HUNGER_NON_EDIBLE_ITEMS = new ConfigFileEntry<>(
            "wildcard_hunger_non_edible_items", "[]", ConfigTypes.ITEM_LIST, "season.hunger",
            "Non Edible Items", "A list of items that you can't eat."
    );

    public static final ConfigFileEntry<Double> WILDCARD_SNAILS_SPEED_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_snails_speed_multiplier", 1.0, "season.snails",
            "Speed Multiplier", "Snail movement speed multiplier."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SNAILS_DROWN_PLAYERS = new ConfigFileEntry<>(
            "wildcard_snails_drown_players", true, "season.snails",
            "Drown Players", "Controls whether snails can drown players when the snails are underwater."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SNAILS_EFFECTS = new ConfigFileEntry<>(
            "wildcard_snails_effects", false, "season.snails",
            "Can Have Potion Effects", "Controls whether snails can have potion effects, like invisibility."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SNAILS_RED_LIVES = new ConfigFileEntry<>(
            "wildcard_snails_red_lives", true, "season.snails",
            "Can Red Players Have Snails", "Controls whether red players have snails or not."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SNAILS_PER_PLAYER = new ConfigFileEntry<>(
            "wildcard_snails_per_player", 1, "season.snails[new]",
            "Number of Snails Per Player", "Controls how many snails each player has.\n§cNOTE: Higher numbers can be very laggy, use high number with caution."
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
    public static final ConfigFileEntry<Boolean> WILDCARD_TIMEDILATION_START_RAIN = new ConfigFileEntry<>(
            "wildcard_timedilation_start_rain", true, "season.timedilation[new]",
            "Start Rain", "Controls whether it starts raining when this wildcard starts (helps give an indication of game speed)."
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

    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = new ConfigFileEntry<>(
            "wildcard_superpowers_disable_intro_theme", false, "season.superpowers",
            "Disable Intro Theme", "Disables the theme music that plays when this wildcard is activated."
    );
    public static final ConfigFileEntry<String> WILDCARD_SUPERPOWERS_POWER_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_superpowers_power_blacklist", "[]", ConfigTypes.STRING_LIST, "season.superpowers",
            "Blacklisted Powers", "List of superpowers that cannot be rolled randomly.", Superpowers.getAllStr()
    );

    public static final ConfigFileEntry<Object> SUPERPOWER_TIME_CONTROL = new ConfigFileEntry<>("superpower_time_control", null, ConfigTypes.TEXT, "{season.superpowers.time_control[new]}", "Time Control", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_CREAKING = new ConfigFileEntry<>("superpower_creaking", null, ConfigTypes.TEXT, "{season.superpowers.creaking[new]}", "Creaking", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_WIND_CHARGE = new ConfigFileEntry<>("superpower_wind_charge", null, ConfigTypes.TEXT, "{season.superpowers.wind_charge[new]}", "Wind Charge", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_ASTRAL_PROJECTION = new ConfigFileEntry<>("superpower_astral_projection", null, ConfigTypes.TEXT, "{season.superpowers.astral_projection[new]}", "Astral Projection", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_SUPER_PUNCH = new ConfigFileEntry<>("superpower_super_punch", null, ConfigTypes.TEXT, "{season.superpowers.super_punch[new]}", "Super Punch", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_MIMICRY = new ConfigFileEntry<>("superpower_mimicry", null, ConfigTypes.TEXT, "{season.superpowers.mimicry[new]}", "Mimicry", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_TELEPORTATION = new ConfigFileEntry<>("superpower_teleportation", null, ConfigTypes.TEXT, "{season.superpowers.teleportation[new]}", "Teleportation", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_LISTENING = new ConfigFileEntry<>("superpower_listening", null, ConfigTypes.TEXT, "{season.superpowers.listening[new]}", "Listening", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_SHADOW_PLAY = new ConfigFileEntry<>("superpower_shadow_play", null, ConfigTypes.TEXT, "{season.superpowers.shadow_play[new]}", "Shadow Play", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_FLIGHT = new ConfigFileEntry<>("superpower_flight", null, ConfigTypes.TEXT, "{season.superpowers.flight[new]}", "Flight", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_PLAYER_DISGUISE = new ConfigFileEntry<>("superpower_player_disguise", null, ConfigTypes.TEXT, "{season.superpowers.player_disguise[new]}", "Player Disguise", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_ANIMAL_DISGUISE = new ConfigFileEntry<>("superpower_animal_disguise", null, ConfigTypes.TEXT, "{season.superpowers.animal_disguise[new]}", "Animal Disguise", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_TRIPLE_JUMP = new ConfigFileEntry<>("superpower_triple_jump", null, ConfigTypes.TEXT, "{season.superpowers.triple_jump[new]}", "Triple Jump", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_INVISIBILITY = new ConfigFileEntry<>("superpower_invisibility", null, ConfigTypes.TEXT, "{season.superpowers.invisibility[new]}", "Invisibility", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_SUPERSPEED = new ConfigFileEntry<>("superpower_superspeed", null, ConfigTypes.TEXT, "{season.superpowers.superspeed[new]}", "Superspeed", "");
    public static final ConfigFileEntry<Object> SUPERPOWER_NECROMANCY = new ConfigFileEntry<>("superpower_necromancy", null, ConfigTypes.TEXT, "{season.superpowers.necromancy[new]}", "Necromancy", "");

    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_TIME_CONTROL = new ConfigFileEntry<>("superpower_cooldown_time_control", 300, ConfigTypes.SECONDS, "season.superpowers.time_control[new]", "Time Control Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_CREAKING = new ConfigFileEntry<>("superpower_cooldown_creaking", 10, ConfigTypes.SECONDS, "season.superpowers.creaking[new]", "Creaking Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_WIND_CHARGE = new ConfigFileEntry<>("superpower_cooldown_wind_charge", 1, ConfigTypes.SECONDS, "season.superpowers.wind_charge[new]", "Wind Charge Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_ASTRAL_PROJECTION = new ConfigFileEntry<>("superpower_cooldown_astral_projection", 5, ConfigTypes.SECONDS, "season.superpowers.astral_projection[new]", "Astral Projection Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_SUPER_PUNCH = new ConfigFileEntry<>("superpower_cooldown_super_punch", 1, ConfigTypes.SECONDS, "season.superpowers.super_punch[new]", "Super Punch Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_MIMICRY = new ConfigFileEntry<>("superpower_cooldown_mimicry", 300, ConfigTypes.SECONDS, "season.superpowers.mimicry[new]", "Mimicry Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_TELEPORTATION = new ConfigFileEntry<>("superpower_cooldown_teleportation", 5, ConfigTypes.SECONDS, "season.superpowers.teleportation[new]", "Teleportation Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_LISTENING = new ConfigFileEntry<>("superpower_cooldown_listening", 1, ConfigTypes.SECONDS, "season.superpowers.listening[new]", "Listening Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_SHADOW_PLAY = new ConfigFileEntry<>("superpower_cooldown_shadow_play", 30, ConfigTypes.SECONDS, "season.superpowers.shadow_play[new]", "Shadow Play Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_FLIGHT = new ConfigFileEntry<>("superpower_cooldown_flight", 45, ConfigTypes.SECONDS, "season.superpowers.flight[new]", "Flight Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_PLAYER_DISGUISE = new ConfigFileEntry<>("superpower_cooldown_player_disguise", 10, ConfigTypes.SECONDS, "season.superpowers.player_disguise[new]", "Player Disguise Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_ANIMAL_DISGUISE = new ConfigFileEntry<>("superpower_cooldown_animal_disguise", 1, ConfigTypes.SECONDS, "season.superpowers.animal_disguise[new]", "Animal Disguise Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_TRIPLE_JUMP = new ConfigFileEntry<>("superpower_cooldown_triple_jump", 1, ConfigTypes.SECONDS, "season.superpowers.triple_jump[new]", "Triple Jump Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_INVISIBILITY = new ConfigFileEntry<>("superpower_cooldown_invisibility", 1, ConfigTypes.SECONDS, "season.superpowers.invisibility[new]", "Invisibility Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_SUPERSPEED = new ConfigFileEntry<>("superpower_cooldown_superspeed", 3, ConfigTypes.SECONDS, "season.superpowers.superspeed[new]", "Superspeed Power Cooldown", "");
    public static final ConfigFileEntry<Integer> SUPERPOWER_COOLDOWN_NECROMANCY = new ConfigFileEntry<>("superpower_cooldown_necromancy", 300, ConfigTypes.SECONDS, "season.superpowers.necromancy[new]", "Necromancy Power Cooldown", "");

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE = new ConfigFileEntry<>(
            "wildcard_superpowers_windcharge_max_mace_damage", 2, "season.superpowers.wind_charge",
            "Max Mace Damage", "The max amount of damage you can deal with a mace while using the Wind Charge superpower."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_first_spawn_clear_items", true, "season.superpowers.necromancy",
            "Zombies First Spawn Clear Items", "Controls whether zombies get cleared when they first get respawned."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_keep_inventory", true, "season.superpowers.necromancy",
            "Zombies Keep Inventory", "Controls whether zombies keep their items when they die."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_revive_by_killing_dark_green", false, "season.superpowers.necromancy",
            "Zombies Can Revive", "Controls whether zombies can be revived (gain a life) by killing a dark green player."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_health", 8, "season.superpowers.necromancy",
            "Zombie Health Amount", "Controls how much health zombies will have."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_SUPERSPEED_STEP = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_step", false, "season.superpowers.superspeed",
            "Step Up Blocks", "Controls whether players with the superspeed power active can step up blocks without jumping (like when riding a horse)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR = new ConfigFileEntry<>(
            "wildcard_superpowers_animaldisguise_armor", false, "season.superpowers.animal_disguise",
            "Show Armor", "Controls whether armor is seen on players disguised as mobs."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS = new ConfigFileEntry<>(
            "wildcard_superpowers_animaldisguise_hands", true, "season.superpowers.animal_disguise",
            "Show Hand Items", "Controls whether hand items are seen on players disguised as mobs."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_TIME_DILATION_TICK_RATE = new ConfigFileEntry<>(
            "wildcard_superpowers_time_dilation_tick_rate", 4, "season.superpowers.time_control[new]",
            "Target Tick Rate", "Controls the target tick rate."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_TIME_DILATION_DURATION = new ConfigFileEntry<>(
            "wildcard_superpowers_time_dilation_duration", 70, "season.superpowers.time_control[new]",
            "Slow Duration", "Controls the slowdown duration."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_CREAKING_AMOUNT = new ConfigFileEntry<>(
            "wildcard_superpowers_creaking_amount", 3, "season.superpowers.creaking[new]",
            "Creaking Amount", "Controls the number of creakings that spawn."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_CREAKING_PARTICLES = new ConfigFileEntry<>(
            "wildcard_superpowers_creaking_particles", true, "season.superpowers.creaking[new]",
            "Show Trail Particles", "Controls whether the trail particles towards the creaking show up."
    );

    public static final ConfigFileEntry<Double> WILDCARD_SUPERPOWERS_WIND_CHARGE_EXPLOSION_POWER = new ConfigFileEntry<>(
            "wildcard_superpowers_wind_charge_explosion_power", 3.0, "season.superpowers.wind_charge[new]",
            "Wind Charge Explosion Power", "Controls the explosion power of wind charges."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ASTRAL_PROJECTION_DAMAGE_CANCELS = new ConfigFileEntry<>(
            "wildcard_superpowers_astral_projection_damage_cancels", true, "season.superpowers.astral_projection[new]",
            "Damage Cancels Projection", "Controls if your projection ends when you take damage."
    );

    public static final ConfigFileEntry<Double> WILDCARD_SUPERPOWERS_SUPER_PUNCH_THORNS_DAMAGE = new ConfigFileEntry<>(
            "wildcard_superpowers_super_punch_thorns_damage", 1.0, "season.superpowers.super_punch[new]",
            "Thorns Damage", "Controls the damage others take when they hit you."
    );
    public static final ConfigFileEntry<Double> WILDCARD_SUPERPOWERS_SUPER_PUNCH_KNOCKBACK_STRENGTH = new ConfigFileEntry<>(
            "wildcard_superpowers_super_punch_knockback_strength", 3.0, "season.superpowers.super_punch[new]",
            "Knockback Strength", "Controls the knockback strength when you punch someone."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_PLAYER_DISGUISE_DAMAGE_CANCELS = new ConfigFileEntry<>(
            "wildcard_superpowers_player_disguise_damage_cancels", true, "season.superpowers.player_disguise[new]",
            "Damage Cancels Disguise", "Controls if your disguise ends when you take damage."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ANIMAL_DISGUISE_DAMAGE_CANCELS = new ConfigFileEntry<>(
            "wildcard_superpowers_animal_disguise_damage_cancels", true, "season.superpowers.animal_disguise[new]",
            "Damage Cancels Disguise", "Controls if your disguise ends when you take damage."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_TELEPORTATION_SWAP_DISTANCE = new ConfigFileEntry<>(
            "wildcard_superpowers_teleportation_swap_distance", 100, "season.superpowers.teleportation[new]",
            "Max Swap Distance", "Controls the max player swap distance.\nVery high numbers may cause performance issues."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_TELEPORTATION_TP_DISTANCE = new ConfigFileEntry<>(
            "wildcard_superpowers_teleportation_tp_distance", 100, "season.superpowers.teleportation[new]",
            "Max Teleport Distance", "Controls the max teleport distance.\nVery high numbers may cause performance issues."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_RANGE = new ConfigFileEntry<>(
            "wildcard_superpowers_shadow_play_blind_range", 10, "season.superpowers.shadow_play[new]",
            "Blint Range", "Controls the range in which players are blinded."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_TIME = new ConfigFileEntry<>(
            "wildcard_superpowers_shadow_play_blind_time", 5, ConfigTypes.SECONDS, "season.superpowers.shadow_play[new]",
            "Blind Time", "Controls the time for which players are blinded."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_FLIGHT_JUMP_AMPLIFIER = new ConfigFileEntry<>(
            "wildcard_superpowers_flight_jump_amplifier", 54, "season.superpowers.flight[new]",
            "Jump Boost Amplifier", "Controls the jump boost amplifier."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_FLIGHT_ELYTRA_LAUNCH_NEEDED = new ConfigFileEntry<>(
            "wildcard_superpowers_flight_elytra_launch_needed", true, "season.superpowers.flight[new]",
            "Needs Launch To Use Elytra", ""
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_DAMAGE_CANCELS = new ConfigFileEntry<>(
            "wildcard_superpowers_invisibility_damage_cancels", true, "season.superpowers.invisibility[new]",
            "Taking Damage Cancels Invis", ""
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_ATTACK_CANCELS = new ConfigFileEntry<>(
            "wildcard_superpowers_invisibility_attack_cancels", true, "season.superpowers.invisibility[new]",
            "Attacking Cancels Invis", ""
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_INVISIBILITY_SHOW_PARTICLES = new ConfigFileEntry<>(
            "wildcard_superpowers_invisibility_show_particles", true, "season.superpowers.invisibility[new]",
            "Show Transluscent Particles", ""
    );

    public static final ConfigFileEntry<Double> WILDCARD_SUPERPOWERS_SUPERSPEED_TARGET_SPEED = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_target_speed", 0.35, "season.superpowers.superspeed[new]",
            "Maximum Speed", "Controls the maximum speed."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_SUPERSPEED_FROST_WALKER_LEVEL = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_frost_walker_level", 3, "season.superpowers.superspeed[new]",
            "Frost Walker Level", "Controls the frost walker enchant level."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_SUPERSPEED_HUNGER_LEVEL = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_hunger_level", 5, "season.superpowers.superspeed[new]",
            "Hunger Level", "Controls the hunger level amplifier."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_TRIPLE_JUMP_JUMPS = new ConfigFileEntry<>(
            "wildcard_superpowers_triple_jump_jumps", 3, "season.superpowers.triple_jump[new]",
            "Max Jump Amount", "Controls the maximum jump amount."
    );


    public static final ConfigFileEntry<String> WILDCARD_CALLBACK_WILDCARDS_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_callback_wildcards_blacklist", "[hunger]", ConfigTypes.STRING_LIST, "season.callback",
            "Blacklisted Wildcards", "List of wildcards that cannot be activated in Callback.", Wildcards.getWildcardsStr()
    );
    public static final ConfigFileEntry<Double> WILDCARD_CALLBACK_TURN_OFF = new ConfigFileEntry<>(
            "wildcard_callback_turn_off", 0.75, ConfigTypes.PERCENTAGE, "season.callback",
            "Turn Off In Session", "Controls when in the session the callback wildcard turns off (percentage)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_CALLBACK_NERFED_WILDCARDS = new ConfigFileEntry<>(
            "wildcard_callback_nerfed_wildcards", true, "season.callback",
            "Nerfed Wildcards", "Controls whether wildcards are nerfed in callback (recommended)."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_CALLBACK_INITIAL_ACTIVATION_INTERVAL = new ConfigFileEntry<>(
            "wildcard_callback_initial_activation_interval", 300, ConfigTypes.SECONDS, "season.callback[new]",
            "Initial Activation Interval", "Controls in what intervals will the wildcards get activated (it speeds up throughout the session). Changing this mid-session might have unexpected consequences."
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
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS = new ConfigFileEntry<>(
            "group_trivia_questions", null, ConfigTypes.TEXT, "{season.trivia.questions}",
            "Trivia Questions", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS_EASY = new ConfigFileEntry<>(
            "group_trivia_questions_easy", null, ConfigTypes.TEXT, "{season.trivia.questions.easy}",
            "Easy Questions", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS_NORMAL = new ConfigFileEntry<>(
            "group_trivia_questions_normal", null, ConfigTypes.TEXT, "{season.trivia.questions.normal}",
            "Normal Questions", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS_HARD = new ConfigFileEntry<>(
            "group_trivia_questions_hard", null, ConfigTypes.TEXT, "{season.trivia.questions.hard}",
            "Hard Questions", ""
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
            "Activate Wildcard Time", "The number of minutes (in the session) after which the wildcard is activated."
    );

    public WildLifeConfig() {
        super(Seasons.WILD_LIFE);
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        List<ConfigFileEntry<?>> result = new ArrayList<>(List.of(
                GROUP_GENERAL //Group
                ,GROUP_SIZESHIFTING //Group
                ,GROUP_HUNGER //Group
                ,GROUP_SNAILS //Group
                ,GROUP_TIMEDILATION //Group
                ,GROUP_TRIVIA //Group
                ,GROUP_MOBSWAP //Group
                ,GROUP_SUPERPOWERS //Group
                ,GROUP_CALLBACK //Group

                //Group stuff
                ,ACTIVATE_WILDCARD_MINUTE

                ,WILDCARD_SIZESHIFTING_MIN_SIZE
                ,WILDCARD_SIZESHIFTING_MAX_SIZE
                ,WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER
                ,WILDCARD_SIZESHIFTING_FIX_BUGS

                ,WILDCARD_HUNGER_EFFECT_LEVEL
                ,WILDCARD_HUNGER_RANDOMIZE_INTERVAL
                ,WILDCARD_HUNGER_NUTRITION_CHANCE
                ,WILDCARD_HUNGER_SATURATION_CHANCE
                ,WILDCARD_HUNGER_EFFECT_CHANCE
                ,WILDCARD_HUNGER_AVG_EFFECT_DURATION
                ,WILDCARD_HUNGER_SOUND_CHANCE
                ,WILDCARD_HUNGER_NON_EDIBLE_ITEMS

                ,WILDCARD_SNAILS_SPEED_MULTIPLIER
                ,WILDCARD_SNAILS_DROWN_PLAYERS
                ,WILDCARD_SNAILS_EFFECTS
                ,WILDCARD_SNAILS_RED_LIVES
                ,WILDCARD_SNAILS_PER_PLAYER

                ,WILDCARD_TIMEDILATION_MIN_SPEED
                ,WILDCARD_TIMEDILATION_MAX_SPEED
                ,WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED
                ,WILDCARD_TIMEDILATION_START_RAIN

                ,WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS
                ,WILDCARD_TRIVIA_BOTS_PER_PLAYER
                ,WILDCARD_TRIVIA_SECONDS_EASY
                ,WILDCARD_TRIVIA_SECONDS_NORMAL
                ,WILDCARD_TRIVIA_SECONDS_HARD
                ,GROUP_TRIVIA_QUESTIONS
                    ,GROUP_TRIVIA_QUESTIONS_EASY
                    ,GROUP_TRIVIA_QUESTIONS_NORMAL
                    ,GROUP_TRIVIA_QUESTIONS_HARD

                ,WILDCARD_MOBSWAP_START_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_END_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_SPAWN_MOBS
                ,WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER

                ,WILDCARD_SUPERPOWERS_POWER_BLACKLIST
                ,WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME

                //Astral Projection
                ,SUPERPOWER_ASTRAL_PROJECTION
                ,SUPERPOWER_COOLDOWN_ASTRAL_PROJECTION
                ,WILDCARD_SUPERPOWERS_ASTRAL_PROJECTION_DAMAGE_CANCELS

                //Super Punch
                ,SUPERPOWER_SUPER_PUNCH
                ,SUPERPOWER_COOLDOWN_SUPER_PUNCH
                ,WILDCARD_SUPERPOWERS_SUPER_PUNCH_THORNS_DAMAGE
                ,WILDCARD_SUPERPOWERS_SUPER_PUNCH_KNOCKBACK_STRENGTH

                //Necromancy
                ,SUPERPOWER_NECROMANCY
                ,SUPERPOWER_COOLDOWN_NECROMANCY
                ,WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS
                ,WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY
                ,WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN
                ,WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH

                // Player Disguise
                ,SUPERPOWER_PLAYER_DISGUISE
                ,SUPERPOWER_COOLDOWN_PLAYER_DISGUISE
                ,WILDCARD_SUPERPOWERS_PLAYER_DISGUISE_DAMAGE_CANCELS

                //Animal Disguise
                ,SUPERPOWER_ANIMAL_DISGUISE
                ,SUPERPOWER_COOLDOWN_ANIMAL_DISGUISE
                ,WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR
                ,WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS
                ,WILDCARD_SUPERPOWERS_ANIMAL_DISGUISE_DAMAGE_CANCELS

                //Teleportation
                ,SUPERPOWER_TELEPORTATION
                ,SUPERPOWER_COOLDOWN_TELEPORTATION
                ,WILDCARD_SUPERPOWERS_TELEPORTATION_SWAP_DISTANCE
                ,WILDCARD_SUPERPOWERS_TELEPORTATION_TP_DISTANCE

                //Shadow Play
                ,SUPERPOWER_SHADOW_PLAY
                ,SUPERPOWER_COOLDOWN_SHADOW_PLAY
                ,WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_RANGE
                ,WILDCARD_SUPERPOWERS_SHADOW_PLAY_BLIND_TIME

                ,SUPERPOWER_MIMICRY
                ,SUPERPOWER_COOLDOWN_MIMICRY

                // Triple Jump
                ,SUPERPOWER_TRIPLE_JUMP
                ,SUPERPOWER_COOLDOWN_TRIPLE_JUMP
                ,WILDCARD_SUPERPOWERS_TRIPLE_JUMP_JUMPS

                // Invisibility
                ,SUPERPOWER_INVISIBILITY
                ,SUPERPOWER_COOLDOWN_INVISIBILITY
                ,WILDCARD_SUPERPOWERS_INVISIBILITY_DAMAGE_CANCELS
                ,WILDCARD_SUPERPOWERS_INVISIBILITY_ATTACK_CANCELS
                ,WILDCARD_SUPERPOWERS_INVISIBILITY_SHOW_PARTICLES

                ,SUPERPOWER_SUPERSPEED
                ,SUPERPOWER_COOLDOWN_SUPERSPEED

                //? if >= 1.20.3 {
                //Time Control
                ,SUPERPOWER_TIME_CONTROL
                ,SUPERPOWER_COOLDOWN_TIME_CONTROL
                ,WILDCARD_SUPERPOWERS_TIME_DILATION_TICK_RATE
                ,WILDCARD_SUPERPOWERS_TIME_DILATION_DURATION
                //?}
                //? if >= 1.21 {
                //Wind Charge
                ,SUPERPOWER_WIND_CHARGE
                ,SUPERPOWER_COOLDOWN_WIND_CHARGE
                ,WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE
                ,WILDCARD_SUPERPOWERS_WIND_CHARGE_EXPLOSION_POWER
                //?}
                //? if >= 1.21.2 {
                //Creaking
                ,SUPERPOWER_CREAKING
                ,SUPERPOWER_COOLDOWN_CREAKING
                ,WILDCARD_SUPERPOWERS_CREAKING_AMOUNT
                ,WILDCARD_SUPERPOWERS_CREAKING_PARTICLES

                //Flight
                ,SUPERPOWER_FLIGHT
                ,SUPERPOWER_COOLDOWN_FLIGHT
                ,WILDCARD_SUPERPOWERS_FLIGHT_JUMP_AMPLIFIER
                ,WILDCARD_SUPERPOWERS_FLIGHT_ELYTRA_LAUNCH_NEEDED
                //?}

                //? if > 1.20.3 {
                //Superspeed
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_STEP
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_FROST_WALKER_LEVEL
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_HUNGER_LEVEL
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_TARGET_SPEED
                //?}

                ,WILDCARD_CALLBACK_WILDCARDS_BLACKLIST
                ,WILDCARD_CALLBACK_TURN_OFF
                ,WILDCARD_CALLBACK_NERFED_WILDCARDS
                ,WILDCARD_CALLBACK_INITIAL_ACTIVATION_INTERVAL
        ));

        if (CompatibilityManager.voicechatLoaded()) {
            result.add(SUPERPOWER_LISTENING);
            result.add(SUPERPOWER_COOLDOWN_LISTENING);
        }

        return result;
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
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
