package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class SecretLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
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
            /*"copper_helmet",*/
            "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = List.of(
            "lectern"
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


    public static final ConfigFileEntry<Boolean> PLAYERS_DROP_TASK_ON_DEATH = new ConfigFileEntry<>(
            "players_drop_task_on_death", false, "season",
            "Drop Task On Death", "Decides whether players drop their secret task book on death or if they keep it."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_PASS = new ConfigFileEntry<>(
            "task_health_easy_pass", 20, ConfigTypes.HEARTS, "season.health",
            "Task Health Easy Pass", "The health you gain for passing an easy task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_EASY_FAIL = new ConfigFileEntry<>(
            "task_health_easy_fail", 0, ConfigTypes.HEARTS, "season.health",
            "Task Health Easy Fail", "The health you lose for failing an easy task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_PASS = new ConfigFileEntry<>(
            "task_health_hard_pass", 40, ConfigTypes.HEARTS, "season.health",
            "Task Health Hard Pass", "The health you gain for passing a hard task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_HARD_FAIL = new ConfigFileEntry<>(
            "task_health_hard_fail", -20, ConfigTypes.HEARTS, "season.health",
            "Task Health Hard Fail", "The health you lose for failing a hard task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_PASS = new ConfigFileEntry<>(
            "task_health_red_pass", 10, ConfigTypes.HEARTS, "season.health",
            "Task Health Red Pass", "The health you gain for passing a red task."
    );
    public static final ConfigFileEntry<Integer> TASK_HEALTH_RED_FAIL = new ConfigFileEntry<>(
            "task_health_red_fail", -5, ConfigTypes.HEARTS, "season.health",
            "Task Health Red Fail", "The health you lose for failing a red task."
    );
    public static final ConfigFileEntry<Double> ASSIGN_TASKS_MINUTE = new ConfigFileEntry<>(
            "assign_tasks_minute", 1.0, ConfigTypes.MINUTES, "season",
            "Assign Tasks Time", "The number of minutes (in the session) after which the tasks get assigned."
    );
    public static final ConfigFileEntry<Integer> MAX_PLAYER_KILL_HEALTH = new ConfigFileEntry<>(
            "max_player_kill_health", 1000, ConfigTypes.HEARTS, "season",
            "Max Health After Killing", "The maximum health a player can have after killing a player (since killing gives hearts)."
    );
    public static final ConfigFileEntry<Boolean> BROADCAST_SECRET_KEEPER = new ConfigFileEntry<>(
            "broadcast_secret_keeper", false, "season",
            "Secret Keeper Broadcast", "Makes the secret keeper broadcast a message in chat when someone succeeds, fails or rerolls a task."
    );
    public static final ConfigFileEntry<Boolean> BROADCAST_TASKS_WHEN_SUBMITTED = new ConfigFileEntry<>(
            "broadcast_tasks_when_submitted", false, "season",
            "Broadcast Tasks When Submitted", "Gives an option for all players to view a task when it gets failed, rerolled or succeeded."
    );
    public static final ConfigFileEntry<Boolean> CONSTANT_TASKS = new ConfigFileEntry<>(
            "constant_tasks", false, "season",
            "Constant Tasks", "Automatically gives players a new task when they have completed one."
    );
    public static final ConfigFileEntry<Boolean> TASKS_NEED_CONFIRMATION = new ConfigFileEntry<>(
            "tasks_need_confirmation", false, "season",
            "Tasks Need Confirmation", "Requires an admin to confirm when someone is succeeding a task."
    );

    public static final ConfigFileEntry<Boolean> ONLY_LOSE_HEARTS_IN_SESSION = new ConfigFileEntry<>(
            "only_lose_hearts_in_session", false, "season.health",
            "Only Lose Hearts In Session", "Makes players only lose hearts while a session is active."
    );

    public static final ConfigFileEntry<Object> GROUP_HEALTH = new ConfigFileEntry<>(
            "group_health", null, ConfigTypes.TEXT, "{season.health}",
            "Health Rewards / Punishments", ""
    );


    public SecretLifeConfig() {
        super("./config/"+ Main.MOD_ID,"secretlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                PLAYERS_DROP_TASK_ON_DEATH
                ,ASSIGN_TASKS_MINUTE
                ,MAX_PLAYER_KILL_HEALTH
                ,BROADCAST_SECRET_KEEPER
                ,BROADCAST_TASKS_WHEN_SUBMITTED
                ,CONSTANT_TASKS
                ,TASKS_NEED_CONFIRMATION

                ,GROUP_HEALTH //Group

                //Group stuff
                ,ONLY_LOSE_HEARTS_IN_SESSION
                ,TASK_HEALTH_EASY_PASS
                ,TASK_HEALTH_EASY_FAIL
                ,TASK_HEALTH_HARD_PASS
                ,TASK_HEALTH_HARD_FAIL
                ,TASK_HEALTH_RED_PASS
                ,TASK_HEALTH_RED_FAIL
        ));
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        MAX_PLAYER_HEALTH.defaultValue = 60;
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
        SPAWNER_RECIPE.defaultValue = true;
        super.instantiateProperties();
    }
}
