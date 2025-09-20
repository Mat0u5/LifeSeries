package net.mat0u5.lifeseries.utils.versions;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.TextUtils;

public class VersionControl {

    // 🔹 Mod version from gradle.properties (manual)
    public static final String MOD_VERSION = "1.0.0";

    // 🔹 Original mod version from gradle.properties (manual)
    public static final String BASE_MOD_VERSION = "1.4.2.0";

    // 🔹 Minecraft version (dynamic, same as in Gradle/stonecutter)
    public static final String MC_VERSION = "1.21.2";

    // 🔹 Combined full version string for display/logging
    public static String getFullVersion() {
        return MOD_VERSION + "+" + BASE_MOD_VERSION + "+" + MC_VERSION;
    }

    // 🔹 Detects if version is a dev/pre-release or debug mode
    public static boolean isDevVersion() {
        return MOD_VERSION.contains("dev") || MOD_VERSION.contains("pre") || Main.DEBUG;
    }

    // 🔹 Convert default mod version string into comparable integer
    public static int getModVersionInt() {
        return parseVersionToInt(MOD_VERSION);
    }

    // 🔹 Convert a custom version string into comparable integer
    public static int getModVersionInt(String versionString) {
        return parseVersionToInt(versionString);
    }

    private static int parseVersionToInt(String string) {
        String originalVersion = string;

        if (string.contains("-pre")) {
            string = string.split("-pre")[0];
        }

        string = string.replaceAll("[^\\d.]", "");
        string = string.replaceAll("^\\.+|\\.+$", "");
        while (string.contains("..")) string = string.replace("..", ".");

        String[] parts = string.split("\\.");

        int major = 0;
        int minor = 0;
        int patch = 0;

        try {
            major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        } catch (Exception e) {
            Main.LOGGER.error(TextUtils.formatString(
                "Failed to parse mod version to int: {} (formatted to {})",
                originalVersion, string
            ));
        }

        return (major * 10000) + (minor * 100) + patch;
    }

    // 🔹 Minimum version the client needs for this server
    public static String clientCompatibilityMin() {
        if (Main.ISOLATED_ENVIRONMENT) return MOD_VERSION;
        return "1.0.0";
    }

    // 🔹 Minimum version the server needs for this client
    public static String serverCompatibilityMin() {
        if (Main.ISOLATED_ENVIRONMENT) return MOD_VERSION;
        return "1.0.0";
    }
}