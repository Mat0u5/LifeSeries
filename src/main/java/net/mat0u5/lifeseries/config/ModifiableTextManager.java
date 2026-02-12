package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifiableTextManager {
    private static Map<String, ConfigFileEntry<String>> registeredEntries = new TreeMap<>();
    private static boolean initialized = false;

    public static void initialize() {
        initialized = true;
        registeredEntries.clear();
        ModifiableText.registerAllTexts();
    }

    public static String toMinecraftColorFormatting(String str) {
        return str.replace("#","§").replace("§§","#");
    }

    public static String fromMinecraftColorFormatting(String str) {
        return str.replace("#","##").replace("§","#");
    }

    public static Map<String, ConfigFileEntry<String>> getRegisteredEntries() {
        return registeredEntries;
    }

    public static void register(String key, String value) {
        register(key, value, null);
    }

    public static void register(String key, String value, List<String> args) {
        if (registeredEntries.containsKey(key)) {
            Main.LOGGER.error("Tried to register duplicate key for modifiable text: "+key);
            return;
        }
        String description = "";
        if (args != null) {
            description = "Arguments: §f" + String.join(", ", args);
        }
        ConfigFileEntry<String> configEntry = new ConfigFileEntry<>("text."+key, value, ConfigTypes.MODIFIABLE_TEXT, "text", key, description);
        configEntry.get(); // To initialize it
        registeredEntries.put(key, configEntry);
    }

    public static Component get(String key, Object... args) {
        String value = getRawValue(key);
        return getFromRawValue(value, false, args);
    }
    public static Component getFromRawValue(String value, boolean silent, Object... args) {
        // %1$s
        MutableComponent result = Component.empty();

        Pattern pattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
        Matcher matcher = pattern.matcher(value);

        int lastIndex = 0;
        int sequentialIndex = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (start > lastIndex) {
                String textBefore = value.substring(lastIndex, start);
                result.append(Component.literal(textBefore));
            }

            String formatType = matcher.group(2);
            String formatString = value.substring(start, end);

            if ("%".equals(formatType) && "%%".equals(formatString)) {
                result.append(Component.literal("%"));
            } else {
                if (!"s".equals(formatType)) {
                    if (!silent) {
                        Main.LOGGER.error("Unsupported format: '{}' in key '{}'", formatString, value);
                    }
                    lastIndex = end;
                    continue;
                }

                String positionGroup = matcher.group(1);
                int argIndex;

                if (positionGroup != null) {
                    argIndex = Integer.parseInt(positionGroup) - 1;
                } else {
                    argIndex = sequentialIndex++;
                }

                if (argIndex >= 0 && argIndex < args.length) {
                    Component argText = TextUtils.getTextForArgument(args[argIndex]);
                    result.append(argText);
                } else if (!silent) {
                    Main.LOGGER.warn("Argument index {} out of bounds for key '{}'. Got {} args.", argIndex + 1, value, args.length);
                }
            }

            lastIndex = end;
        }

        if (lastIndex < value.length()) {
            String remainingText = value.substring(lastIndex);
            result.append(Component.literal(remainingText));
        }

        return result;
    }

    private static String getRawValue(String key) {
        if (!initialized) {
            initialize();
        }
        ConfigFileEntry<String> configEntry = registeredEntries.get(key);
        if (configEntry == null) {
            Main.LOGGER.warn("Could not find modifiable text " + key);
            return key;
        }
        return toMinecraftColorFormatting(configEntry.get());
    }
}
