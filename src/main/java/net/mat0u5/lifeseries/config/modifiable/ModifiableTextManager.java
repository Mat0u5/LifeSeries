package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.matlib.utils.enums.Formatted;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.mat0u5.lifeseries.LifeSeries.seasonConfig;

public class ModifiableTextManager {
    public record TextEntry(String key, String defaultValue, String description) {
        public String configKey() {
            return "text." + key;
        }
    }

    private static Map<String, TextEntry> registeredEntries = new TreeMap<>();
    private static boolean initialized = false;

    public static void initialize() {
        initialized = true;
        registeredEntries.clear();
        LifeSeries.LOGGER.info("Loading modifiable texts...");
        ModifiableText.registerAllTexts();
        LifeSeries.LOGGER.info("Loaded "+registeredEntries.size()+" modifiable texts");
    }

    public static String toMinecraftColorFormatting(String str) {
        return str.replace("#","§").replace("§§","#").replace("\\n", "\n");
    }

    public static String fromMinecraftColorFormatting(String str) {
        return str.replace("#","##").replace("§","#").replace("\n", "\\n");
    }

    public static Map<String, TextEntry> getRegisteredEntries() {
        return registeredEntries;
    }

    public static void register(String key, String value) {
        register(key, value, null);
    }

    public static void register(String key, String value, List<String> args) {
        if (registeredEntries.containsKey(key)) {
            LifeSeries.LOGGER.error("Tried to register duplicate key for modifiable text: "+key);
            return;
        }
        String description = "";
        if (args != null) {
            description = "Arguments: §f" + String.join(", ", args);
        }
        TextEntry entry = new TextEntry(key, value, description);
        if (seasonConfig != null) {
            seasonConfig.getOrCreateProperty(entry.configKey(), value); // To initialize it
        }
        registeredEntries.put(key, entry);
    }

    public static Component get(Formatted formatted, String key, Object... args) {
        String value = getRawValue(key);
        return getFromRawValue(formatted, value, false, args);
    }

    public static Component getFromRawValue(Formatted formatted, String value, boolean silent, Object... args) {
        if (formatted == Formatted.LOOSELY_STYLED) {
            return getFromRawValue(value, silent, true, args);
        }
        Component styled = getFromRawValue(value, silent, false, args);
        if (formatted == Formatted.PLAIN) {
            Component.literal(styled.getString());
        }
        return styled;
    }

    public static Component getFromRawValue(String value, boolean silent, boolean looselyStyled, Object... args) {
        // %1$s
        MutableComponent result = Component.empty();
        StringBuilder resultLooselyStyled = new StringBuilder();

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
                resultLooselyStyled.append(textBefore);
            }

            String formatType = matcher.group(2);
            String formatString = value.substring(start, end);

            if ("%".equals(formatType) && "%%".equals(formatString)) {
                result.append(Component.literal("%"));
                resultLooselyStyled.append("%");
            } else {
                if (!"s".equals(formatType)) {
                    if (!silent) {
                        LifeSeries.LOGGER.error("Unsupported format: '{}' in key '{}'", formatString, value);
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
                    resultLooselyStyled.append(argText.getString());
                } else if (!silent) {
                    LifeSeries.LOGGER.warn("Argument index {} out of bounds for key '{}'. Got {} args.", argIndex + 1, value, args.length);
                }
            }

            lastIndex = end;
        }

        if (lastIndex < value.length()) {
            String remainingText = value.substring(lastIndex);
            result.append(Component.literal(remainingText));
            resultLooselyStyled.append(remainingText);
        }

        if (looselyStyled) {
            return Component.literal(resultLooselyStyled.toString());
        }

        return result;
    }

    private static String getRawValue(String key) {
        if (!initialized) {
            initialize();
        }
        TextEntry entry = registeredEntries.get(key);
        if (entry == null) {
            LifeSeries.LOGGER.warn("Could not find modifiable text " + key);
            return key;
        }
        if (seasonConfig == null) {
            return toMinecraftColorFormatting(entry.defaultValue());
        }
        return toMinecraftColorFormatting(seasonConfig.getOrCreateProperty(entry.configKey(), entry.defaultValue()));
    }
}
