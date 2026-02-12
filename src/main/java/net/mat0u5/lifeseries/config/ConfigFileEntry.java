package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.List;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class ConfigFileEntry<T> {
    public final String key;
    public T defaultValue;
    public ConfigTypes type;
    public String displayName;
    public String description;
    public final String groupInfo;
    public final List<String> args;
    public final boolean dynamic;

    public ConfigFileEntry(String key, T defaultValue, String groupInfo, String displayName, String description) {
        this(key, defaultValue, getTypeFromValue(defaultValue), groupInfo, displayName, description);
    }
    public ConfigFileEntry(String key, T defaultValue, String groupInfo, String displayName, String description, boolean dynamic) {
        this(key, defaultValue, getTypeFromValue(defaultValue), groupInfo, displayName, description, dynamic);
    }

    public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo, String displayName, String description) {
        this(key, defaultValue, type, groupInfo, displayName, description, null);
    }
    public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo, String displayName, String description, boolean dynamic) {
        this(key, defaultValue, type, groupInfo, displayName, description, null, dynamic);
    }
    public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo, String displayName, String description, List<String> args) {
        this(key, defaultValue, type, groupInfo, displayName, description, args, false);
    }

    public ConfigFileEntry(String key, T defaultValue, ConfigTypes type, String groupInfo, String displayName, String description, List<String> args, boolean dynamic) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.groupInfo = groupInfo;
        this.args = args;
        this.dynamic = dynamic;
    }

    public static ConfigTypes getTypeFromValue(Object defaultValue) {
        if (defaultValue instanceof Integer) {
            return ConfigTypes.INTEGER;
        }
        else if (defaultValue instanceof Boolean) {
            return ConfigTypes.BOOLEAN;
        }
        else if (defaultValue instanceof Double) {
            return ConfigTypes.DOUBLE;
        }
        else if (defaultValue instanceof String) {
            return ConfigTypes.STRING;
        }
        return ConfigTypes.NULL;
    }
    public T get() {
        return get(seasonConfig);
    }

    @SuppressWarnings("unchecked")
    public T get(ConfigManager config) {
        try {
        if (defaultValue instanceof Integer i) {
            return (T) Integer.valueOf(config.getOrCreateInt(key, i));
        }
        else if (defaultValue instanceof Boolean b) {
            return (T) Boolean.valueOf(config.getOrCreateBoolean(key, b));
        }
        else if (defaultValue instanceof Double d) {
            return (T) Double.valueOf(config.getOrCreateDouble(key, d));
        }
        else if (defaultValue instanceof String s) {
            return (T) config.getOrCreateProperty(key, s);
        }
        }catch(Exception e) {}

        Main.LOGGER.error(TextUtils.formatString("Config value {} was null, returning default value - {}", key, defaultValue));
        return defaultValue;
    }
}