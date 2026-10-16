package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.config.SeasonConfig;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;
import net.mat0u5.lifeseries.seasons.season.Seasons;

import java.util.List;

public class SecretLifeConfig extends SeasonConfig {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            //? if >= 1.21
            "mace",
            "end_crystal",
            "#minecraft:head_armor",
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

    public final ConfigurableValue<Boolean> PLAYERS_DROP_TASK_ON_DEATH = ConfigurableValue.register(this, "players_drop_task_on_death", false)
            .name("Drop Task On Death")
            .description("Decides whether players drop their secret task book on death or if they keep it.")
            .group("season");
    public final ConfigurableValue<Double> ASSIGN_TASKS_MINUTE = ConfigurableValue.register(this, "assign_tasks_minute", 1.0, ConfigTypes.MINUTES)
            .name("Assign Tasks Time")
            .description("The number of minutes (in the session) after which the tasks get assigned.")
            .group("season");
    public final ConfigurableValue<Integer> MAX_PLAYER_KILL_HEALTH = ConfigurableValue.register(this, "max_player_kill_health", 1000, ConfigTypes.HEARTS)
            .name("Max Health After Killing")
            .description("The maximum health a player can have after killing a player (since killing gives hearts).")
            .group("season");
    public final ConfigurableValue<Boolean> BROADCAST_SECRET_KEEPER = ConfigurableValue.register(this, "broadcast_secret_keeper", false)
            .name("Secret Keeper Broadcast")
            .description("Makes the secret keeper broadcast a message in chat when someone succeeds, fails or rerolls a task.")
            .group("season");
    public final ConfigurableValue<Boolean> BROADCAST_TASKS_WHEN_SUBMITTED = ConfigurableValue.register(this, "broadcast_tasks_when_submitted", false)
            .name("Broadcast Tasks When Submitted")
            .description("Gives an option for all players to view a task when it gets failed, rerolled or succeeded.")
            .group("season");
    public final ConfigurableValue<Boolean> CONSTANT_TASKS = ConfigurableValue.register(this, "constant_tasks", false)
            .name("Constant Tasks")
            .description("Automatically gives players a new task when they have completed one.")
            .group("season");
    public final ConfigurableValue<Boolean> TASKS_NEED_CONFIRMATION = ConfigurableValue.register(this, "tasks_need_confirmation", false)
            .name("Tasks Need Confirmation")
            .description("Requires an admin to confirm when someone is succeeding a task.")
            .group("season");
    public final ConfigurableValue<Boolean> UNBREAKABLE_BUTTONS = ConfigurableValue.register(this, "unbreakable_buttons", true)
            .name("Unbreakable Buttons")
            .description("Makes the task buttons and supporting blocks unbreakable.")
            .group("season")
            .setNew(true);

    public final ConfigHeader GROUP_HEALTH = ConfigHeader.register(this, "group_health", "Health Rewards / Punishments")
            .group("season.health").groupHeader(true);
    public final ConfigHeader GROUP_TASKS = ConfigHeader.register(this, "group_tasks", "Tasks")
            .group("season.tasks").groupHeader(true);
    public final ConfigHeader GROUP_TASKS_EASY = ConfigHeader.register(this, "group_tasks_easy", "Easy Tasks")
            .group("season.tasks.easy").groupHeader(true);
    public final ConfigHeader GROUP_TASKS_HARD = ConfigHeader.register(this, "group_tasks_hard", "Hard Tasks")
            .group("season.tasks.hard").groupHeader(true);
    public final ConfigHeader GROUP_TASKS_RED = ConfigHeader.register(this, "group_tasks_red", "Red Tasks")
            .group("season.tasks.red").groupHeader(true);

    public final ConfigurableValue<Boolean> ONLY_LOSE_HEARTS_IN_SESSION = ConfigurableValue.register(this, "only_lose_hearts_in_session", false)
            .name("Only Lose Hearts In Session")
            .description("Makes players only lose hearts while a session is active.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_EASY_PASS = ConfigurableValue.register(this, "task_health_easy_pass", 20, ConfigTypes.HEARTS)
            .name("Task Health Easy Pass")
            .description("The health you gain for passing an easy task.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_EASY_FAIL = ConfigurableValue.register(this, "task_health_easy_fail", 0, ConfigTypes.HEARTS)
            .name("Task Health Easy Fail")
            .description("The health you lose for failing an easy task.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_HARD_PASS = ConfigurableValue.register(this, "task_health_hard_pass", 40, ConfigTypes.HEARTS)
            .name("Task Health Hard Pass")
            .description("The health you gain for passing a hard task.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_HARD_FAIL = ConfigurableValue.register(this, "task_health_hard_fail", -20, ConfigTypes.HEARTS)
            .name("Task Health Hard Fail")
            .description("The health you lose for failing a hard task.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_RED_PASS = ConfigurableValue.register(this, "task_health_red_pass", 10, ConfigTypes.HEARTS)
            .name("Task Health Red Pass")
            .description("The health you gain for passing a red task.")
            .group("season.health");
    public final ConfigurableValue<Integer> TASK_HEALTH_RED_FAIL = ConfigurableValue.register(this, "task_health_red_fail", -5, ConfigTypes.HEARTS)
            .name("Task Health Red Fail")
            .description("The health you lose for failing a red task.")
            .group("season.health");

    public SecretLifeConfig() {
        super(Seasons.SECRET_LIFE);
        removeEntry(NO_SEASON_SPECIFIC);
        BLACKLIST_ITEMS.defaultValue(BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue(BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue(CLAMPED_ENCHANTMENTS);
        MAX_PLAYER_HEALTH.defaultValue(60);
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue(true);
        SPAWNER_RECIPE.defaultValue(true);
    }

    public static SecretLifeConfig get() {
        return getActive(SecretLifeConfig.class, SecretLifeConfig::new);
    }
}
