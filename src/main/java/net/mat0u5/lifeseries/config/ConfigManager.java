package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
//? if > 1.20.2
import net.minecraft.world.scores.PlayerScoreEntry;

public abstract class ConfigManager extends DefaultConfigValues {

    protected Properties properties = new Properties();
    protected String folderPath;
    protected String filePath;

    protected ConfigManager(String folderPath, String filePath) {
        this.folderPath = folderPath;
        this.filePath = folderPath + "/" + filePath;
        createFileIfNotExists();
        loadProperties();
        renamedProperties();
        instantiateProperties();
    }

    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return new ArrayList<>(List.of(
                GROUP_GLOBAL // Group
                ,GROUP_SEASON // Group
                ,GROUP_LIVES
                ,GROUP_TEAMS
                ,GROUP_EVENTS

                , GROUP_GLOBAL_LIVES // Group
                ,DEFAULT_LIVES
                ,ONLY_TAKE_LIVES_IN_SESSION
                ,TAB_LIST_SHOW_LIVES // Group
                , LIVES_SYSTEM_DISABLED

                ,MAX_PLAYER_HEALTH // Group
                ,KEEP_INVENTORY

                ,WORLDBORDER_GROUP
                ,WORLDBORDER_SIZE
                //? if >= 1.21.9 {
                ,WORLDBORDER_NETHER_SIZE
                ,WORLDBORDER_END_SIZE
                //?}
                , WORLDBORDER_OUTSIDE_TELEPORT

                //? if >= 1.21.6 {
                ,LOCATOR_BAR
                 //?}
                ,ALLOW_SELF_DEFENSE
                ,SEE_FRIENDLY_INVISIBLE_PLAYERS
                ,SHOW_LOGIN_COMMAND_INFO
                ,HIDE_UNJUSTIFIED_KILL_MESSAGES
                ,SHOW_ADVANCEMENTS
                //? if >= 1.20.3 {
                ,TICK_FREEZE_NOT_IN_SESSION
                //?}
                ,BROADCAST_LIFE_GAIN
                ,ADDITIONAL_WITHER_SKULL_RATE
                ,SESSION_START_COUNTDOWN


                ,GROUP_BLACKLIST // Group
                ,BOOGEYMAN // Group
                ,SECRET_SOCIETY //Group
                ,GIVELIFE_COMMAND_ENABLED // Group
                ,GROUP_FINAL_DEATH // Group
                ,CUSTOM_ENCHANTER_ALGORITHM
                ,MUTE_DEAD_PLAYERS
                ,TAB_LIST_SHOW_DEAD_PLAYERS
                ,GROUP_SPAWN_EGG // Group
                ,GROUP_WATCHERS // Group
                ,GROUP_SUBIN // Group


                //Group stuff
                ,SHOW_HEALTH_BELOW_NAME
                ,BLACKLIST_ITEMS
                ,BLACKLIST_RECIPES
                ,BLACKLIST_BLOCKS
                ,GROUP_CLAMPED_ENCHANTS
                    ,BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1
                    ,BLACKLIST_CLAMPED_ENCHANTS_LEVEL_2
                    ,BLACKLIST_CLAMPED_ENCHANTS_LEVEL_3
                    ,BLACKLIST_CLAMPED_ENCHANTS_LEVEL_4
                ,BLACKLIST_BANNED_ENCHANTS
                ,BLACKLIST_BANNED_POTION_EFFECTS
                ,BLACKLIST_CLAMPED_POTION_EFFECTS
                ,CREATIVE_IGNORE_BLACKLIST

                ,BOOGEYMAN_MIN_AMOUNT
                ,BOOGEYMAN_MAX_AMOUNT
                ,BOOGEYMAN_ADVANCED_DEATHS
                ,BOOGEYMAN_CHANCE_MULTIPLIER
                ,BOOGEYMAN_IGNORE
                ,BOOGEYMAN_FORCE
                ,BOOGEYMAN_MESSAGE
                ,BOOGEYMAN_CHOOSE_MINUTE
                ,BOOGEYMAN_ANNOUNCE_OUTCOME
                    ,BOOGEYMAN_INFINITE // Group
                    ,BOOGEYMAN_INFINITE_LAST_PICK
                    ,BOOGEYMAN_INFINITE_AUTO_FAIL
                ,BOOGEYMAN_TEAM_NOTICE
                ,BOOGEYMAN_KILLS_NEEDED
                ,BOOGEYMAN_STEAL_LIFE

                ,SECRET_SOCIETY_MEMBER_AMOUNT
                ,SECRET_SOCIETY_START_TIME
                ,SECRET_SOCIETY_WORDS
                ,SECRET_SOCIETY_FORCE
                ,SECRET_SOCIETY_IGNORE
                ,SECRET_SOCIETY_PUNISHMENT_LIVES
                ,SECRET_SOCIETY_KILLS_REQUIRED
                ,SECRET_SOCIETY_SOUND_ONLY_MEMBERS

                ,PLAYERS_DROP_ITEMS_ON_FINAL_DEATH
                ,FINAL_DEATH_TITLE_SHOW
                ,FINAL_DEATH_TITLE_SUBTITLE
                ,FINAL_DEATH_MESSAGE
                ,FINAL_DEATH_LIGHTNING
                ,FINAL_DEATH_SOUND

                ,GIVELIFE_LIVES_MAX
                ,GIVELIFE_BROADCAST
                ,GIVELIFE_CAN_REVIVE

                ,TAB_LIST_SHOW_EXACT_LIVES

                ,SPAWN_EGG_DROP_CHANCE
                ,SPAWN_EGG_DROP_ONLY_NATURAL
                ,SPAWN_EGG_ALLOW_ON_SPAWNER
                ,SPAWNER_RECIPE

                ,WATCHERS_IN_TAB
                ,WATCHERS_MUTED
                ,SUBIN_CHANGE_SKIN
                ,SUBIN_CHANGE_USERNAME
                ,LIVES_RANDOMIZE
                ,LIVES_RANDOMIZE_MIN
                ,LIVES_RANDOMIZE_MAX
        ));
    }

    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                NO_SEASON_SPECIFIC
        ));
    }

    protected List<ConfigFileEntry<?>> getAllConfigEntries() {
        List<ConfigFileEntry<?>> allEntries = new ArrayList<>();
        allEntries.addAll(getDefaultConfigEntries());
        allEntries.addAll(getSeasonSpecificConfigEntries());
        return allEntries;
    }

    protected void instantiateProperties() {
        for (ConfigFileEntry<?> entry : getAllConfigEntries()) {
            if (entry.defaultValue instanceof Integer integerValue) {
                getOrCreateInt(entry.key, integerValue);
            } else if (entry.defaultValue instanceof Boolean booleanValue) {
                getOrCreateBoolean(entry.key, booleanValue);
            } else if (entry.defaultValue instanceof Double doubleValue) {
                getOrCreateDouble(entry.key, doubleValue);
            } else if (entry.defaultValue instanceof String stringValue) {
                getOrCreateProperty(entry.key, stringValue);
            }
        }
    }

    public void sendConfigTo(ServerPlayer player) {
        int index = 0;
        for (ConfigFileEntry<?> entry : getAllConfigEntries()) {
            sendConfigEntry(player, entry, index);
            index++;
        }
        //? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME)) {
            ConfigFileEntry<Integer> lifeEntry = new ConfigFileEntry<>(
                    "dynamic_lives_"+entry.getOwner(), entry.getScore(), ConfigTypes.LIVES_ENTRY, "lives",
                    entry.getOwner(), "", true
            );
            sendConfigEntry(player, lifeEntry, index);
            index++;
        }
        *///?} else {
        for (PlayerScoreEntry entry : ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME)) {
            ConfigFileEntry<Integer> lifeEntry = new ConfigFileEntry<>(
                    "dynamic_lives_"+entry.owner(), entry.value(), ConfigTypes.LIVES_ENTRY, "lives",
                    entry.owner(), "", true
            );
            sendConfigEntry(player, lifeEntry, index);
            index++;
        }
        //?}
        for (ServerPlayer nonAssignedPlayer : livesManager.getNonAssignedPlayers()) {
            ConfigFileEntry<Integer> lifeEntry = new ConfigFileEntry<>(
                    "dynamic_lives_"+nonAssignedPlayer.getScoreboardName(), null, ConfigTypes.LIVES_ENTRY, "lives",
                    nonAssignedPlayer.getScoreboardName(), "", true
            );
            sendConfigEntry(player, lifeEntry, index);
            index++;
        }
        for (Map.Entry<Integer, PlayerTeam> entry : livesManager.getLivesTeams().entrySet()) {
            PlayerTeam team = entry.getValue();
            String teamName = team.getName();
            int teamNum = entry.getKey();
            Integer validKill = livesManager.getTeamCanKill(teamName);
            Integer gainLife = livesManager.getTeamGainLives(teamName);
            String validKillStr = validKill != null ? String.valueOf(validKill) : "";
            String gainLifeStr = gainLife != null ? String.valueOf(gainLife) : "";
            ConfigFileEntry<Object> teamEntry = new ConfigFileEntry<>(
                    "dynamic_teams_"+ UUID.randomUUID(), null, ConfigTypes.TEAM_ENTRY, "teams",
                    "", "", List.of(String.valueOf(teamNum), team.getDisplayName().getString(), team.getColor().getName(), validKillStr, gainLifeStr), true
            );
            sendConfigEntry(player, teamEntry, index);
            index++;
        }
        for (DatapackIntegration.Events event : DatapackIntegration.getAllEvents()) {
            ConfigFileEntry<String> teamEntry = new ConfigFileEntry<>(
                    event.getEventName(), event.getCommand(), ConfigTypes.EVENT_ENTRY, "events",
                    event.getDisplayName(), event.getDescription(), List.of(event.getCanceled()), true
            );
            sendConfigEntry(player, teamEntry, index);
            index++;
        }
    }

    public void sendConfigEntry(ServerPlayer player, ConfigFileEntry<?> entry, int index) {
        NetworkHandlerServer.sendConfig(player, getConfigPayload(entry, index));
    }

    public ConfigPayload getConfigPayload(ConfigFileEntry<?> entry, int index) {
        String value = "";
        if (!entry.type.parentText()) {
            if (!entry.dynamic) {
                value = getPropertyAsString(entry.key, entry.defaultValue);
            }
        }
        String defaultValue = "";
        if (entry.defaultValue != null) {
            defaultValue = entry.defaultValue.toString();
            if (entry.dynamic) {
                value = entry.defaultValue.toString();
            }
        }
        List<String> args = new ArrayList<>(List.of(value, defaultValue, entry.groupInfo));
        if (entry.args != null) {
            args.addAll(entry.args);
        }
        return new ConfigPayload(entry.type.toString(), entry.key, index, entry.displayName, entry.description, args);
    }

    private String getPropertyAsString(String key, Object defaultValue) {
        if (defaultValue instanceof Integer intValue) {
            return String.valueOf(getOrCreateInt(key, intValue));
        } else if (defaultValue instanceof Boolean booleanValue) {
            return String.valueOf(getOrCreateBoolean(key, booleanValue));
        } else if (defaultValue instanceof Double doubleValue) {
            return String.valueOf(getOrCreateDouble(key, doubleValue));
        } else if (defaultValue instanceof String stringValue) {
            return getOrCreateProperty(key, stringValue);
        }
        if (defaultValue == null) return "";
        return defaultValue.toString();
    }


    protected void renamedProperties() {
        renamedProperty("show_death_title_on_last_death", "final_death_title_show");
        renamedProperty("players_drop_items_on_last_death", "players_drop_items_on_final_death");
        renamedProperty("blacklist_banned_potions", "blacklist_banned_potion_effects");
        renamedProperty("auto_keep_inventory", "keep_inventory");
        renamedProperty("beoadcast_secret_keeper", "broadcast_secret_keeper");
        renamedProperty("blacklist_clamped_enchants", "blacklist_clamped_enchants_level_1");
    }

    private void renamedProperty(String from, String to) {
        if (properties.containsKey(from)) {
            if (!properties.containsKey(to)) {
                String value = getProperty(from);
                if (value != null) {
                    setProperty(to, value);
                }
            }
            removeProperty(from);
        }
    }

    public static void onUpdatedBoolean(String id, boolean value) {
        if (id.equals(seasonConfig.TICK_FREEZE_NOT_IN_SESSION.key)) {
            currentSession.freezeIfNecessary();
            if (!value) {
                OtherUtils.setFreezeGame(false);
            }
        }
    }

    public static void onUpdatedInteger(String id, int value) {
        if (server == null) return;

        if (id.equals(seasonConfig.WORLDBORDER_SIZE.key)) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            if (overworld != null) overworld.getWorldBorder().setSize(value);
        }
        //? if >= 1.21.9 {
        if (id.equals(seasonConfig.WORLDBORDER_NETHER_SIZE.key)) {
            ServerLevel nether = server.getLevel(Level.NETHER);
            if (nether != null) nether.getWorldBorder().setSize(value);
        }
        if (id.equals(seasonConfig.WORLDBORDER_END_SIZE.key)) {
            ServerLevel end = server.getLevel(Level.END);
            if (end != null) end.getWorldBorder().setSize(value);
        }
        //?}
    }

    public static void moveOldMainFileIfExists() {
        File newFolder = new File("./config/lifeseries/main/");
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", newFolder);
                return;
            }
        }

        File oldFile = new File("./config/"+ Main.MOD_ID+".properties");
        if (!oldFile.exists()) return;
        File newFile = new File("./config/lifeseries/main/"+ Main.MOD_ID+".properties");
        if (newFile.exists()) {
            if (oldFile.delete()) {
                Main.LOGGER.info("Deleted old config file.");
            }
        }
        else {
            try {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Main.LOGGER.info("Moved old config file.");
            } catch (IOException e) {
                Main.LOGGER.info("Failed to move old config file.");
            }
        }
    }

    private void createFileIfNotExists() {
        if (folderPath == null || filePath == null) return;
        File configDir = new File(folderPath);
        if (!configDir.exists()) {
            if (!configDir.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", configDir);
                return;
            }
        }

        File configFile = new File(filePath);
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    Main.LOGGER.error("Failed to create file {}", configFile);
                    return;
                }
                try (OutputStream output = new FileOutputStream(configFile)) {
                    instantiateProperties();
                    properties.store(output, null);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void loadProperties() {
        if (folderPath == null || filePath == null) return;

        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setOrRemoveProperty(String key, String value) {
        if (value == null || value.isEmpty()) {
            removeProperty(key);
        }
        else {
            setProperty(key, value);
        }
    }

    public void setProperty(String key, String value) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeProperty(String key) {
        if (folderPath == null || filePath == null) return;
        if (!properties.containsKey(key)) return;
        properties.remove(key);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setPropertyCommented(String key, String value, String comment) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void resetProperties(String comment) {
        properties.clear();
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
        Various getters
     */

    public String getProperty(String key) {
        if (folderPath == null || filePath == null) return null;
        if (properties == null) return null;

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return null;
    }

    public String getOrCreateProperty(String key, String defaultValue) {
        if (folderPath == null || filePath == null) return "";
        if (properties == null) return "";

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        setProperty(key, defaultValue);
        return defaultValue;
    }

    public boolean getOrCreateBoolean(String key, boolean defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        return defaultValue;
    }

    public double getOrCreateDouble(String key, double defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public int getOrCreateInt(String key, int defaultValue) {
        String value = getOrCreateProperty(key, String.valueOf(defaultValue));
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {}
        return defaultValue;
    }
}
