package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.config.GuiConfig;
import net.mat0u5.matlib.config.entry.ConfigHeader;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.type.ConfigTypes;

import java.util.List;

public abstract class DefaultConfigValues extends GuiConfig {
    public final ConfigHeader GROUP_GLOBAL = ConfigHeader.register(this, "group_global", "General Settings")
            .group("global").groupHeader(true)
            .groupShowSidebar(false);
    public final ConfigHeader GROUP_SEASON = ConfigHeader.register(this, "group_season", "Season Specific Settings")
            .group("season").groupHeader(true)
            .groupShowSidebar(false);
    public final ConfigHeader GROUP_LIVES = ConfigHeader.register(this, "group_lives", "Lives Manager")
            .group("lives").groupHeader(true);
    public final ConfigHeader GROUP_TEAMS = ConfigHeader.register(this, "group_teams", "Teams Manager")
            .group("teams").groupHeader(true);
    public final ConfigHeader GROUP_EVENTS = ConfigHeader.register(this, "group_events", "Events")
            .group("events").groupHeader(true);
    public final ConfigHeader GROUP_TEXTS = ConfigHeader.register(this, "group_texts", "Modifiable Texts")
            .group("text").groupHeader(true);

    public final ConfigHeader GROUP_GLOBAL_LIVES = ConfigHeader.register(this, "group_global_lives", "Lives Stuff")
            .group("global.lives").groupHeader(true);
    public final ConfigHeader GROUP_SESSION = ConfigHeader.register(this, "group_session", "Session")
            .group("global.session").groupHeader(true)
            .setNew(true);
    public final ConfigurableValue<Integer> DEFAULT_LIVES = ConfigurableValue.register(this, "default_lives", 3)
            .name("Default Lives")
            .description("The number of lives every player will have by default.")
            .group("global.lives");
    public final ConfigurableValue<Boolean> TAB_LIST_SHOW_LIVES = ConfigurableValue.register(this, "tab_list_show_lives", false)
            .name("Tab List Show Lives")
            .description("Controls whether you can see the players' lives in the tab list.")
            .group("global.lives.showlives").groupHeader(true);

    public final ConfigurableValue<Integer> MAX_PLAYER_HEALTH = ConfigurableValue.register(this, "max_player_health", 20, ConfigTypes.HEARTS)
            .name("Default Health")
            .description("The amount of health (half-hearts) every player will have by default.")
            .group("global.health").groupHeader(true)
            .onChange((integer, integer2) -> PlayerUtils.getAllPlayers().forEach(PlayerUtils::resetMaxPlayerHealthIfNecessary));
    public final ConfigurableValue<Boolean> KEEP_INVENTORY = ConfigurableValue.register(this, "keep_inventory", true)
            .name("Keep Inventory")
            .description("Decides whether players drop their items when they die.")
            .group("global");

    public final ConfigHeader WORLDBORDER_GROUP = ConfigHeader.register(this, "worldborder_sizes", "Worldborder Settings")
            .group("global.worldborder").groupHeader(true);
    //? if < 1.21.9 {
    /*public final ConfigurableValue<Integer> WORLDBORDER_SIZE = ConfigurableValue.register(this, "worldborder_size", 500)
            .name("Worldborder Size")
            .description("Sets the worldborder size.")
            .group("global");
    *///?} else {
    public final ConfigurableValue<Integer> WORLDBORDER_SIZE = ConfigurableValue.register(this, "worldborder_size", 500)
            .name("Worldborder Size")
            .description("Sets the worldborder size in the overworld.")
            .group("global.worldborder");
    public final ConfigurableValue<Integer> WORLDBORDER_NETHER_SIZE = ConfigurableValue.register(this, "worldborder_nether_size", 500)
            .name("Worldborder Nether Size")
            .description("Sets the worldborder size in the nether.")
            .group("global.worldborder");
    public final ConfigurableValue<Integer> WORLDBORDER_END_SIZE = ConfigurableValue.register(this, "worldborder_end_size", 500)
            .name("Worldborder End Size")
            .description("Sets the worldborder size in the end.")
            .group("global.worldborder");
    //?}
    public final ConfigurableValue<Boolean> WORLDBORDER_OUTSIDE_TELEPORT = ConfigurableValue.register(this, "worldborder_outside_teleport", true)
            .name("Worldborder Outside Teleport")
            .description("Controls whether players that get outside the worldborder get teleported back inside.")
            .group("global.worldborder");

    public final ConfigurableValue<Boolean> LOCATOR_BAR = ConfigurableValue.register(this, "locator_bar", false)
            .name("Locator Bar")
            .description("Enables the player Locator Bar.")
            .group("global");
    public final ConfigurableValue<Boolean> ALLOW_SELF_DEFENSE = ConfigurableValue.register(this, "allow_self_defense", true)
            .name("Allow Self Defense Kills")
            .description("Controls whether self-defense kills should count as unjustified.")
            .group("global");
    public final ConfigurableValue<Boolean> SEE_FRIENDLY_INVISIBLE_PLAYERS = ConfigurableValue.register(this, "see_friendly_invisible_players", false)
            .name("See Friendly Invisible Players")
            .description("Controls whether players can see other invisible players on the same life color.")
            .group("global");
    public final ConfigurableValue<Boolean> SHOW_LOGIN_COMMAND_INFO = ConfigurableValue.register(this, "show_login_command_info", true)
            .name("Show Command Info On Login")
            .description("Controls whether players get a message in chat showing the available commands when they login.")
            .group("global");
    public final ConfigurableValue<Boolean> HIDE_UNJUSTIFIED_KILL_MESSAGES = ConfigurableValue.register(this, "hide_unjustified_kills", false)
            .name("Hide Unjustified Kill Messages")
            .description("Controls whether unjustified kill messages show up in admin chat.")
            .group("global");
    public final ConfigurableValue<Boolean> SHOW_ADVANCEMENTS = ConfigurableValue.register(this, "show_advancements", true)
            .name("Show Advancements In Chat")
            .description("Controls advancements show up in the chat.")
            .group("global");
    public final ConfigurableValue<Boolean> BROADCAST_LIFE_GAIN = ConfigurableValue.register(this, "broadcast_life_gain", false)
            .name("Broadcast Kill Life Gain")
            .description("Shows a message in chat when a player gains a life by killing a player.")
            .group("global");
    public final ConfigurableValue<Double> ADDITIONAL_WITHER_SKULL_RATE = ConfigurableValue.register(this, "additional_wither_skull_rate", 0.05, ConfigTypes.PERCENTAGE)
            .name("Additional Wither Skull Rate")
            .description("Controls the additional chance for wither skeletons to drop skulls.")
            .group("global");

    public final ConfigHeader GROUP_BLACKLIST = ConfigHeader.register(this, "group_blacklist", "Blacklists")
            .group("global.blacklist").groupHeader(true);
    public final ConfigurableValue<Boolean> BOOGEYMAN = ConfigurableValue.register(this, "boogeyman", false, LifeSeriesConfigTypes.BOOGEYMAN)
            .name("Boogeyman Enabled")
            .description("Enables the boogeyman.")
            .group("global.boogeyman").groupHeader(true);
    public final ConfigurableValue<Boolean> SECRET_SOCIETY = ConfigurableValue.register(this, "secret_society", false)
            .name("Secret Society Enabled")
            .description("Enables the Secret Society in the session.")
            .group("global.society").groupHeader(true);
    public final ConfigurableValue<Boolean> GIVELIFE_COMMAND_ENABLED = ConfigurableValue.register(this, "givelife_command_enabled", false)
            .name("Givelife Command Enabled")
            .description("Controls whether the '/givelife' command is available.")
            .group("global.givelife").groupHeader(true);
    public final ConfigHeader GROUP_FINAL_DEATH = ConfigHeader.register(this, "group_final_death", "Final Death")
            .group("global.finaldeath").groupHeader(true);
    public final ConfigurableValue<Boolean> CUSTOM_ENCHANTER_ALGORITHM = ConfigurableValue.register(this, "custom_enchanter_algorithm", false)
            .name("Custom Enchanter Algorithm")
            .description("Modifies the enchanting table algorithm to allow players to get all enchants even without bookshelves.")
            .group("global");
    public final ConfigurableValue<Boolean> TAB_LIST_SHOW_DEAD_PLAYERS = ConfigurableValue.register(this, "tab_list_show_dead_players", true)
            .name("Tab List Show Dead Players")
            .description("Controls whether dead players show up in the tab list.")
            .group("global");
    public final ConfigHeader GROUP_SPAWN_EGG = ConfigHeader.register(this, "group_spawn_egg", "Spawn Egg")
            .group("global.spawnegg").groupHeader(true);
    public final ConfigHeader GROUP_WATCHERS = ConfigHeader.register(this, "group_watchers", "Watchers §7('/watcher' command)")
            .group("global.watchers").groupHeader(true);
    public final ConfigHeader GROUP_SUBIN = ConfigHeader.register(this, "group_subin", "Sub-in Settings")
            .group("global.subin").groupHeader(true);
    public final ConfigHeader GROUP_MUTED = ConfigHeader.register(this, "group_muted", "Muted Players")
            .group("global.muted").groupHeader(true);

    public final ConfigurableValue<Boolean> ONLY_CHANGE_LIVES_IN_SESSION = ConfigurableValue.register(this, "only_change_lives_in_session", false)
            .name("Only Change Lives In Session")
            .description("Makes players only lose/gain lives when they die while a session is active.")
            .group("global.session");
    public final ConfigurableValue<Boolean> ONLY_LOSE_DURABILITY_IN_SESSION = ConfigurableValue.register(this, "only_lose_durability_in_session", false)
            .name("Only Lose Durability In Session")
            .description("Makes players only lose durability on their items and gear while a session is active.")
            .group("global.session")
            .setNew(true);
    public final ConfigurableValue<Boolean> SESSION_START_COUNTDOWN = ConfigurableValue.register(this, "session_start_countdown", false)
            .name("Session Start Countdown")
            .description("Shows a countdown when the session is starting.")
            .group("global.session");
    public final ConfigurableValue<Boolean> TICK_FREEZE_NOT_IN_SESSION = ConfigurableValue.register(this, "tick_freeze_not_in_session", false)
            .name("Tick Freeze When Not In Session")
            .description("Automatically freezes the game when the session is paused or ended or not started.")
            .group("global.session");

    public final ConfigurableValue<Boolean> MUTE_DEAD_PLAYERS = ConfigurableValue.register(this, "mute_dead_players", false)
            .name("Mute Dead Players")
            .description("Controls whether dead players should be allowed to type in chat.")
            .group("global.muted");
    public final ConfigurableValue<Boolean> WATCHERS_MUTED = ConfigurableValue.register(this, "watchers_muted", false)
            .name("Mute Watchers")
            .description("Controls whether the Watchers should be allowed to type in chat.")
            .group("global.muted");
    public final ConfigurableValue<Boolean> MUTED_CHAT = ConfigurableValue.register(this, "muted_chat", true)
            .name("Allow Muted Only Chat")
            .description("Controls whether the muted players can still talk to each other, or to noone at all.")
            .group("global.muted.chat").groupHeader(true);
    public final ConfigurableValue<Boolean> MUTED_CHAT_ADMIN = ConfigurableValue.register(this, "muted_chat_admin", true)
            .name("Muted Chat Admin Broadcast")
            .description("Controls whether the muted player chat gets broadcast to admins too.")
            .group("global.muted.chat");
    public final ConfigurableValue<Boolean> SHOW_HEALTH_BELOW_NAME = ConfigurableValue.register(this, "show_health_below_name", false)
            .name("Show Health Below Name")
            .description("Show the HP a player is on below their username.")
            .group("global.health");
    public final ConfigurableValue<List<String>> BLACKLIST_ITEMS = ConfigurableValue.register(this, "blacklist_items", List.of(), ConfigTypes.ITEM_LIST)
            .name("Blacklisted Items")
            .description("List of banned items.")
            .group("global.blacklist")
            .setRequiresReload(true);
    public final ConfigurableValue<List<String>> BLACKLIST_RECIPES = ConfigurableValue.register(this, "blacklist_recipes", List.of(), ConfigTypes.ITEM_LIST)
            .name("Blacklisted Recipes")
            .description("List of banned recipes - items you can't craft.")
            .group("global.blacklist")
            .setRequiresReload(true);
    public final ConfigurableValue<List<String>> BLACKLIST_BLOCKS = ConfigurableValue.register(this, "blacklist_blocks", List.of(), ConfigTypes.BLOCK_LIST)
            .name("Blacklisted Blocks")
            .description("List of banned blocks.")
            .group("global.blacklist");
    public final ConfigHeader GROUP_CLAMPED_ENCHANTS = ConfigHeader.register(this, "group_clamped_enchants", "Clamped Enchantments")
            .group("global.blacklist.enchants").groupHeader(true);
    public final ConfigurableValue<List<String>> BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1 = ConfigurableValue.register(this, "blacklist_clamped_enchants_level_1", List.of(), ConfigTypes.ENCHANT_LIST)
            .name("Clamped To Level 1")
            .description("List of enchantments clamped to level 1 (any higher levels will be set to lvl1).")
            .group("global.blacklist.enchants");
    public final ConfigurableValue<List<String>> BLACKLIST_CLAMPED_ENCHANTS_LEVEL_2 = ConfigurableValue.register(this, "blacklist_clamped_enchants_level_2", List.of(), ConfigTypes.ENCHANT_LIST)
            .name("Clamped To Level 2")
            .description("List of enchantments clamped to level 2 (any higher levels will be set to lvl2).")
            .group("global.blacklist.enchants");
    public final ConfigurableValue<List<String>> BLACKLIST_CLAMPED_ENCHANTS_LEVEL_3 = ConfigurableValue.register(this, "blacklist_clamped_enchants_level_3", List.of(), ConfigTypes.ENCHANT_LIST)
            .name("Clamped To Level 3")
            .description("List of enchantments clamped to level 3 (any higher levels will be set to lvl3).")
            .group("global.blacklist.enchants");
    public final ConfigurableValue<List<String>> BLACKLIST_CLAMPED_ENCHANTS_LEVEL_4 = ConfigurableValue.register(this, "blacklist_clamped_enchants_level_4", List.of(), ConfigTypes.ENCHANT_LIST)
            .name("Clamped To Level 4")
            .description("List of enchantments clamped to level 4 (any higher levels will be set to lvl4).")
            .group("global.blacklist.enchants");
    public final ConfigurableValue<List<String>> BLACKLIST_BANNED_ENCHANTS = ConfigurableValue.register(this, "blacklist_banned_enchants", List.of(), ConfigTypes.ENCHANT_LIST)
            .name("Blacklisted Enchants")
            .description("List of banned enchants.")
            .group("global.blacklist");
    public final ConfigurableValue<List<String>> BLACKLIST_BANNED_POTION_EFFECTS = ConfigurableValue.register(this, "blacklist_banned_potion_effects", List.of("strength", "instant_health", "instant_damage"), ConfigTypes.EFFECT_LIST)
            .name("Banned Potion Effects")
            .description("List of banned potion effects.")
            .group("global.blacklist");
    public final ConfigurableValue<List<String>> BLACKLIST_CLAMPED_POTION_EFFECTS = ConfigurableValue.register(this, "blacklist_clamped_potion_effects", List.of(), ConfigTypes.EFFECT_LIST)
            .name("Clamped Potion Effects")
            .description("List of potion effects clamped to level 1 (any higher levels will be set to level 1).")
            .group("global.blacklist");
    public final ConfigurableValue<Boolean> CREATIVE_IGNORE_BLACKLIST = ConfigurableValue.register(this, "creative_ignore_blacklist", true)
            .name("Creative Ignore Blacklist")
            .description("Controls whether players in creative mode are able to bypass the blacklists.")
            .group("global.blacklist");

    public final ConfigurableValue<Integer> BOOGEYMAN_MIN_AMOUNT = ConfigurableValue.register(this, "boogeyman_min_amount", 1)
            .name("Minimum Boogeyman Amount")
            .description("The minimum amount of Boogeymen a session can have.")
            .group("global.boogeyman");
    public final ConfigurableValue<Integer> BOOGEYMAN_MAX_AMOUNT = ConfigurableValue.register(this, "boogeyman_max_amount", 99)
            .name("Maximum Boogeyman Amount")
            .description("The maximum amount of Boogeymen a session can have.")
            .group("global.boogeyman");
    public final ConfigurableValue<Boolean> BOOGEYMAN_ADVANCED_DEATHS = ConfigurableValue.register(this, "boogeyman_advanced_deaths", false)
            .name("Advanced Deaths")
            .description("Enables the advanced deaths (seen in Past Life), where you actually die by different causes instead of your lives just being set to 1.")
            .group("global.boogeyman");
    public final ConfigurableValue<Double> BOOGEYMAN_CHANCE_MULTIPLIER = ConfigurableValue.register(this, "boogeyman_chance_multiplier", 0.5, ConfigTypes.PERCENTAGE)
            .name("Boogeyman Chance Multiplier")
            .description("Controls how likely it is to get one extra boogeyman.")
            .group("global.boogeyman");
    public final ConfigurableValue<String> BOOGEYMAN_IGNORE = ConfigurableValue.register(this, "boogeyman_ignore", "[]")
            .name("Boogeyman Ignore List")
            .description("A list of players that cannot become the boogeyman.")
            .group("global.boogeyman");
    public final ConfigurableValue<String> BOOGEYMAN_FORCE = ConfigurableValue.register(this, "boogeyman_force", "[]")
            .name("Boogeyman Force List")
            .description("A list of players that are forced to become the boogeyman.")
            .group("global.boogeyman");
    public final ConfigurableValue<Double> BOOGEYMAN_CHOOSE_MINUTE = ConfigurableValue.register(this, "boogeyman_choose_minute", 10.0, ConfigTypes.MINUTES)
            .name("Boogeyman Choose Time")
            .description("The number of minutes (in the session) after which the boogeyman gets picked.")
            .group("global.boogeyman");
    public final ConfigurableValue<Boolean> BOOGEYMAN_ANNOUNCE_OUTCOME = ConfigurableValue.register(this, "boogeyman_announce_outcome", true)
            .name("Boogeyman Announce Outcome")
            .description("Shows a message in chat when the boogeyman succeeds or fails.")
            .group("global.boogeyman");
    public final ConfigurableValue<Boolean> BOOGEYMAN_INFINITE = ConfigurableValue.register(this, "boogeyman_infinite", false)
            .name("Boogeyman Infinite Rolling")
            .description("When any boogeyman is cured, a new one will replace them immediatelly.")
            .group("global.boogeyman.infinite").groupHeader(true);
    public final ConfigurableValue<Integer> BOOGEYMAN_INFINITE_LAST_PICK = ConfigurableValue.register(this, "boogeyman_infinite_last_pick", 1800, ConfigTypes.SECONDS)
            .name("Last Roll Before End Of Session")
            .description("Controls how long before the end of session the infinite boogey picking will stop, in seconds.")
            .group("global.boogeyman.infinite");
    public final ConfigurableValue<Integer> BOOGEYMAN_INFINITE_AUTO_FAIL = ConfigurableValue.register(this, "boogeyman_infinite_auto_fail", 360000, ConfigTypes.SECONDS)
            .name("Automatic Fail")
            .description("Controls how long a Boogeyman has to kill someone before they automatically fail, in seconds.")
            .group("global.boogeyman.infinite");
    public final ConfigurableValue<Boolean> BOOGEYMAN_TEAM_NOTICE = ConfigurableValue.register(this, "boogeyman_team_notice", false)
            .name("Boogeyman Team Notice")
            .description("Shows every Boogeyman a list of the other players that are also Boogeymen in chat.")
            .group("global.boogeyman");
    public final ConfigurableValue<Integer> BOOGEYMAN_KILLS_NEEDED = ConfigurableValue.register(this, "boogeyman_kills_needed", 1)
            .name("Boogeyman Kills Needed")
            .description("Controls how many kills you need as the Boogeyman to be cured.")
            .group("global.boogeyman");
    public final ConfigurableValue<Boolean> BOOGEYMAN_STEAL_LIFE = ConfigurableValue.register(this, "boogeyman_steal_life", false)
            .name("Boogeyman Steal Life")
            .description("When a boogeyman gets cured, they gain a life for completing their task.")
            .group("global.boogeyman");
    public final ConfigurableValue<Boolean> BOOGEYMAN_LOCATOR_BAR = ConfigurableValue.register(this, "boogeyman_locator_bar", false)
            .name("Boogeyman Locator Bar")
            .description("Controls whether the Boogeyman can see all other players on the locator bar.")
            .group("global.boogeyman");

    public final ConfigurableValue<Integer> SECRET_SOCIETY_MEMBER_AMOUNT = ConfigurableValue.register(this, "secret_society_member_amount", 3)
            .name("Member Amount")
            .description("The number of players that are a part of the Secret Society")
            .group("global.society");
    public final ConfigurableValue<Double> SECRET_SOCIETY_START_TIME = ConfigurableValue.register(this, "secret_society_start_time", 5.0, ConfigTypes.MINUTES)
            .name("Society Start Time")
            .description("Controls when in the session the Society will start, in minutes.")
            .group("global.society");
    public final ConfigurableValue<String> SECRET_SOCIETY_WORDS = ConfigurableValue.register(this, "secret_society_words", "[Hammer, Magnet, Throne, Gravity, Puzzle, Spiral, Pivot, Flare]")
            .name("Random Words")
            .description("List of words that can be picked as the secret word.")
            .group("global.society");
    public final ConfigurableValue<String> SECRET_SOCIETY_FORCE = ConfigurableValue.register(this, "secret_society_force", "[]")
            .name("Member Force List")
            .description("A list of players that are forced to become a Member in the society.")
            .group("global.society");
    public final ConfigurableValue<String> SECRET_SOCIETY_IGNORE = ConfigurableValue.register(this, "secret_society_ignore", "[]")
            .name("Member Ignore List")
            .description("A list of players that cannot become a Member in the society.")
            .group("global.society");
    public final ConfigurableValue<Integer> SECRET_SOCIETY_PUNISHMENT_LIVES = ConfigurableValue.register(this, "secret_society_punishment_lives", -2)
            .name("Punishment Lives")
            .description("The amount of lives all Members of the society lose if they fail.")
            .group("global.society");
    public final ConfigurableValue<Integer> SECRET_SOCIETY_KILLS_REQUIRED = ConfigurableValue.register(this, "secret_society_kills_required", 2)
            .name("Kills Required To Succeed")
            .description("The number of kills the Members need to succeed in the Society.")
            .group("global.society");
    public final ConfigurableValue<Boolean> SECRET_SOCIETY_SOUND_ONLY_MEMBERS = ConfigurableValue.register(this, "secret_society_sound_only_members", false)
            .name("Whisper Sound Only For Members")
            .description("Makes the whispering sound only play for Society Members, thus making the Society fully secret.")
            .group("global.society");
    public final ConfigurableValue<Boolean> SECRET_SOCIETY_ADVANCED_DEATHS = ConfigurableValue.register(this, "secret_society_advanced_deaths", false)
            .name("Secret Society Advanced Deaths")
            .description("Enables the advanced deaths (seen in Past Life), where you actually die by different causes instead of your lives just being changed.")
            .group("global.society");

    public final ConfigurableValue<Boolean> PLAYERS_DROP_ITEMS_ON_FINAL_DEATH = ConfigurableValue.register(this, "players_drop_items_on_final_death", false)
            .name("Players Drop Items on Final Death")
            .description("Controls whether players drop their items on the final death (even if keepInventory is on).")
            .group("global.finaldeath");
    public final ConfigurableValue<Boolean> FINAL_DEATH_TITLE_SHOW = ConfigurableValue.register(this, "final_death_title_show", true)
            .name("Show Death Title on Final Death")
            .description("Controls whether the death title (the one covering like half the screen) should show up when a player fully dies.")
            .group("global.finaldeath.title").groupHeader(true);
    public final ConfigurableValue<Boolean> DEATH_TITLE_MATCH_DEATH_MESSAGE = ConfigurableValue.register(this, "final_death_title_match_death_message", false)
            .name("Death Title Match Death Message")
            .description("Makes the death title match the death message.")
            .group("global.finaldeath.title");
    public final ConfigurableValue<Boolean> FINAL_DEATH_LIGHTNING = ConfigurableValue.register(this, "final_death_lightning", true)
            .name("Final Death Lightning")
            .description("Spawns a harmless (no damage) lightning strike when a player fully dies.")
            .group("global.finaldeath");
    public final ConfigurableValue<String> FINAL_DEATH_SOUND = ConfigurableValue.register(this, "final_death_sound", "minecraft:entity.lightning_bolt.thunder")
            .name("Final Death Sound")
            .description("The sound that gets played to all players when anyone fully dies.")
            .group("global.finaldeath");

    public final ConfigurableValue<Integer> GIVELIFE_LIVES_MAX = ConfigurableValue.register(this, "givelife_lives_max", 99)
            .name("Max Givelife Lives")
            .description("The maximum amount of lives a player can have from other players giving them lives using /givelife.")
            .group("global.givelife");
    public final ConfigurableValue<Boolean> GIVELIFE_BROADCAST = ConfigurableValue.register(this, "givelife_broadcast", false)
            .name("Broadcast Givelife")
            .description("Broadcasts the message when a player gives a life to another player using /givelife.")
            .group("global.givelife");
    public final ConfigurableValue<Boolean> GIVELIFE_CAN_REVIVE = ConfigurableValue.register(this, "givelife_can_revive", false)
            .name("Givelife Can Revive Dead Players")
            .description("Controls whether players can revive dead players using /givelife.")
            .group("global.givelife");

    public final ConfigurableValue<Boolean> TAB_LIST_SHOW_EXACT_LIVES = ConfigurableValue.register(this, "tab_list_show_exact_lives", false)
            .name("Show EXACT Lives")
            .description("Shows the actual number of lives when above 4 instead of just '4+'.")
            .group("global.lives.showlives");

    public final ConfigurableValue<Double> SPAWN_EGG_DROP_CHANCE = ConfigurableValue.register(this, "spawn_egg_drop_chance", 0.05, ConfigTypes.PERCENTAGE)
            .name("Spawn Egg Drop Chance")
            .description("Modifies the chance of mobs dropping their spawn egg. (0.05 = 5%)")
            .group("global.spawnegg");
    public final ConfigurableValue<Boolean> SPAWN_EGG_DROP_ONLY_NATURAL = ConfigurableValue.register(this, "spawn_egg_drop_only_natural", true)
            .name("Spawn Egg Only Natural Drops")
            .description("Controls whether spawn eggs should only drop from mobs that spawn naturally (no breeding, spawners, etc).")
            .group("global.spawnegg");
    public final ConfigurableValue<Boolean> SPAWN_EGG_ALLOW_ON_SPAWNER = ConfigurableValue.register(this, "spawn_egg_allow_on_spawner", false)
            .name("Spawn Egg Allow on Spawners")
            .description("Controls whether players should be able to use the spawn eggs on spawners.")
            .group("global.spawnegg");
    public final ConfigurableValue<Boolean> SPAWNER_RECIPE = ConfigurableValue.register(this, "spawner_recipe", false)
            .name("Spawner Recipe")
            .description("Controls whether the spawner crafting recipe is enabled.")
            .group("global.spawnegg")
            .setRequiresReload(true);

    public final ConfigurableValue<Boolean> WATCHERS_IN_TAB = ConfigurableValue.register(this, "watchers_in_tab", true)
            .name("Show Watchers In Tab")
            .description("Controls whether Watchers should show up in the tab list.")
            .group("global.watchers");
    public final ConfigurableValue<Boolean> SUBIN_CHANGE_SKIN = ConfigurableValue.register(this, "subin_change_skin", true)
            .name("Sub-in Change Skin")
            .description("Controls whether the player subbing in will get the skin of the player they are subbing in for.")
            .group("global.subin");
    public final ConfigurableValue<Boolean> SUBIN_CHANGE_USERNAME = ConfigurableValue.register(this, "subin_change_username", true)
            .name("Sub-in Change Username")
            .description("Controls whether the player subbing in will get the username of the player they are subbing in for.")
            .group("global.subin");
    public final ConfigurableValue<Boolean> LIVES_LIFE_DIFF_MESSAGE = ConfigurableValue.register(this, "lives_life_diff_message", false)
            .name("Show Life Diff In Death Message")
            .description("Shows an indicator of how many lives were lost in the death messages.")
            .group("global.lives");
    public final ConfigurableValue<Boolean> LIVES_RANDOMIZE = ConfigurableValue.register(this, "lives_randomize", false)
            .name("Randomize Lives")
            .description("Makes every player get a random amount of lives.")
            .group("global.lives.random").groupHeader(true);
    public final ConfigurableValue<Boolean> LIVES_RANDOMIZE_PSEUDORANDOM = ConfigurableValue.register(this, "lives_randomize_pseudorandom", true)
            .name("Pseudorandom Lives")
            .description("If there are enough players, it forces at least one occurrence of every possible life amount.")
            .group("global.lives.random");
    public final ConfigurableValue<Integer> LIVES_RANDOMIZE_MIN = ConfigurableValue.register(this, "lives_randomize_min", 2)
            .name("Minimum Lives")
            .description("The minimum number of lives any player can have after randomization.")
            .group("global.lives.random");
    public final ConfigurableValue<Integer> LIVES_RANDOMIZE_MAX = ConfigurableValue.register(this, "lives_randomize_max", 6)
            .name("Maximum Lives")
            .description("The maximum number of lives any player can have after randomization.")
            .group("global.lives.random");
    public final ConfigurableValue<Boolean> LIVES_RANDOMIZE_CUSTOMAVG = ConfigurableValue.register(this, "lives_randomize_custom_average", false)
            .name("Custom Average Lives")
            .group("global.lives.random.customavg").groupHeader(true)
            .setNew(true);
    public final ConfigurableValue<Double> LIVES_RANDOMIZE_AVERAGE = ConfigurableValue.register(this, "lives_randomize_average", 4.0)
            .name("Average Lives")
            .description("The average number of lives a player gets after randomization.")
            .group("global.lives.random.customavg")
            .setNew(true);
    public final ConfigurableValue<Double> LIVES_RANDOMIZE_MINUTE = ConfigurableValue.register(this, "lives_randomize_time", 1.0, ConfigTypes.MINUTES)
            .name("Lives Randomize Time")
            .description("How many minutes after the session starts the lives get picked.")
            .group("global.lives.random");
    public final ConfigurableValue<Boolean> LIVES_LOSE_KILLS_ONLY = ConfigurableValue.register(this, "lives_lose_kills_only", false)
            .name("Only Lose Lives From PvP Kills")
            .description("Makes players not lose lives from natural deaths.")
            .group("global.lives");

    public final ConfigHeader GROUP_DISABLE = ConfigHeader.register(this, "group_disable", "Disable Functionality")
            .group("global.disable").groupHeader(true);
    public final ConfigurableValue<Boolean> LIVES_SYSTEM_DISABLED = ConfigurableValue.register(this, "lives_system_disabled", false)
            .name("Fully Disable Lives System")
            .description("Fully disables the lives system, if you want to implement a custom one for example :)")
            .group("global.disable");
    public final ConfigurableValue<Boolean> TEAMS_SYSTEM_DISABLED = ConfigurableValue.register(this, "teams_system_disabled", false)
            .name("Fully Disable Teams System")
            .description("Fully disables the teams system, if you want to implement a custom one for example :)")
            .group("global.disable");

    public final ConfigHeader NO_SEASON_SPECIFIC = ConfigHeader.register(this, "no_season_specific", "There are no season specific entries")
            .group("season");

    protected DefaultConfigValues(String configId, String guiTitle, String folderPath, String filePath) {
        super(configId, guiTitle, folderPath, filePath);
        //? if < 1.21.6 {
        //removeEntry(LOCATOR_BAR);
        //?}
        //? if < 1.20.3 {
        //removeEntry(TICK_FREEZE_NOT_IN_SESSION);
        //?}
    }
}
