package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.ConfigPayload;
import net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife.SimpleLifeConfig;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLifeConfig;
import net.mat0u5.lifeseries.seasons.season.lastlife.LastLifeConfig;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLifeConfig;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeConfig;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

                ,DEFAULT_LIVES
                ,MAX_PLAYER_HEALTH
                ,KEEP_INVENTORY

                ,CUSTOM_ENCHANTER_ALGORITHM
                ,MUTE_DEAD_PLAYERS
                ,AUTO_SET_WORLDBORDER
                //? if >= 1.21.6 {
                /*,LOCATOR_BAR
                 *///?}


                ,GROUP_BLACKLIST // Group
                ,BOOGEYMAN // Group
                ,GIVELIFE_COMMAND_ENABLED // Group
                ,GROUP_FINAL_DEATH // Group
                ,GROUP_TABLIST // Group
                ,GROUP_SPAWN_EGG // Group


                //Group stuff
                ,BLACKLIST_ITEMS
                ,BLACKLIST_BLOCKS
                ,BLACKLIST_CLAMPED_ENCHANTS
                ,BLACKLIST_BANNED_ENCHANTS
                ,BLACKLIST_BANNED_POTION_EFFECTS
                ,CREATIVE_IGNORE_BLACKLIST

                ,BOOGEYMAN_MIN_AMOUNT
                ,BOOGEYMAN_MAX_AMOUNT
                ,BOOGEYMAN_CHANCE_MULTIPLIER
                ,BOOGEYMAN_IGNORE
                ,BOOGEYMAN_FORCE
                ,BOOGEYMAN_MESSAGE
                ,BOOGEYMAN_CHOOSE_MINUTE

                ,PLAYERS_DROP_ITEMS_ON_FINAL_DEATH
                ,FINAL_DEATH_TITLE_SHOW
                ,FINAL_DEATH_TITLE_SUBTITLE
                ,FINAL_DEATH_MESSAGE

                ,GIVELIFE_LIVES_MAX

                ,TAB_LIST_SHOW_DEAD_PLAYERS
                ,TAB_LIST_SHOW_LIVES

                ,SPAWN_EGG_DROP_CHANCE
                ,SPAWN_EGG_DROP_ONLY_NATURAL
                ,SPAWN_EGG_ALLOW_ON_SPAWNER
                ,SPAWNER_RECIPE
        ));
    }

    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of());
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

    public void sendConfigTo(ServerPlayerEntity player) {
        int index = 0;
        for (ConfigFileEntry<?> entry : getAllConfigEntries()) {
            sendConfigEntry(player, entry, index);
            index++;
        }
    }

    public void sendConfigEntry(ServerPlayerEntity player, ConfigFileEntry<?> entry, int index) {
        NetworkHandlerServer.sendConfig(player, getConfigPayload(entry, index));
    }

    public ConfigPayload getConfigPayload(ConfigFileEntry<?> entry, int index) {
        String value = "";
        if (!entry.type.parentText()) {
            value = getPropertyAsString(entry.key, entry.defaultValue);
        }
        String defaultValue = "";
        if (entry.defaultValue != null) {
            defaultValue = entry.defaultValue.toString();
        }
        return new ConfigPayload(entry.type.toString(), entry.key, index, entry.displayName, entry.description, List.of(value, defaultValue, entry.groupInfo));
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
        return defaultValue.toString();
    }


    protected void renamedProperties() {
        renamedProperty("show_death_title_on_last_death", "final_death_title_show");
        renamedProperty("players_drop_items_on_last_death", "players_drop_items_on_final_death");
        renamedProperty("blacklist_banned_potions", "blacklist_banned_potion_effects");
        renamedProperty("auto_keep_inventory", "keep_inventory");
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

    public static void createConfigs() {
        new ThirdLifeConfig();
        new LastLifeConfig();
        new DoubleLifeConfig();
        new LimitedLifeConfig();
        new SecretLifeConfig();
        new WildLifeConfig();
        new SimpleLifeConfig();
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
                Main.LOGGER.error(ex.getMessage());
            }
        }
    }

    public void loadProperties() {
        if (folderPath == null || filePath == null) return;

        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void setProperty(String key, String value) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void removeProperty(String key) {
        if (folderPath == null || filePath == null) return;
        if (!properties.containsKey(key)) return;
        properties.remove(key);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void setPropertyCommented(String key, String value, String comment) {
        if (folderPath == null || filePath == null) return;
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
        }
    }

    public void resetProperties(String comment) {
        properties.clear();
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comment);
        } catch (IOException ex) {
            Main.LOGGER.error(ex.getMessage());
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
