package net.mat0u5.lifeseries.utils.versions;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import static net.mat0u5.lifeseries.Main.MOD_VERSION;

public class VersionControl {
    public static boolean isDevVersion() {
        return MOD_VERSION.contains("dev") || MOD_VERSION.contains("pre") || Main.DEBUG;
    }

    public static String strippedVersionName() {
        return strippedVersionName(MOD_VERSION);
    }

    public static String strippedVersionName(String string) {
        if (string.contains("-pre")) {
            string = string.split("-pre")[0];
        }
        string = string.replaceAll("[^\\d.]", ""); //Remove all non-digit and non-dot characters.
        string = string.replaceAll("^\\.+|\\.+$", ""); //Remove all leading or trailing dots.
        while (string.contains("..")) string = string.replace("..",".");

        return string;
    }

    public static int getModVersionInt(String string) {
        try {

            String originalVersion = string;
            string = strippedVersionName(string);

            String[] parts = string.split("\\.");

            int major = 0;
            int minor = 0;
            int patch = 0;
            int build = 0;
            try {
                major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
                minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                build = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
            }catch(Exception e) {
                Main.LOGGER.error(TextUtils.formatString("Failed to parse mod version to int: {} (formatted to {})", originalVersion, string));
            }

            if (originalVersion.contains("-pre")) {
                build = -100;
                try {
                    build += Integer.parseInt(originalVersion.split("-pre")[1]);
                }catch(Exception ignored) {}
            }

            /*
                Supports up to:
                 213 major versions
                 99 minor versions
                 99 patch versions
                 999 build versions

                 So 213.99.99.999 is a valid version for example.

                 Pre-releases act as if 900 build versions are already added, so 100 pre-releases are supported
             */

            return (major * 10_000_000) + (minor * 100_000) + (patch * 1_000) + build;
        }catch(Exception ignored) {}
        return 0;
    }

    /*
        *     COMPATIBILITY TABLE
        *   1.3.0
        *   1.3.1       -   1.3.1.2
        *   1.3.1.3     -   1.3.1.4
        *   1.3.2
        *   1.3.2.1     -   1.3.2.2
        *   1.3.2.3
        *   1.3.2.4
        *   1.3.2.5
        *   1.3.2.6
        *   1.3.3       -   1.3.3.2
        *   1.3.4       -   1.3.4.4
        *   1.3.4.5     -   1.3.4.9
        *   1.3.4.10    -   1.3.4.19
        *   1.3.5       -   1.3.5.2
        *   1.3.5.3     -   1.3.5.7
        *   1.3.5.8     -   1.3.5.16
        *   1.3.5.17    -   1.3.5.23
        *   1.3.5.24    -   1.3.5.29
        *   1.3.6       -   1.3.6.7     (clientCompatibility stayed)
        *   1.3.6.8     -   1.3.6.26
        *   1.3.6.27    -   1.3.6.37
        *   1.3.7       -   1.3.7.11
        *   1.3.7.12
        *   1.3.7.13    -   1.3.7.26
        *   1.3.7.27    -   1.4.0-pre4
        *   1.4.0       -   1.4.0.3
        *   1.4.0.4     -   1.4.0.5
        *   1.4.0.6     -   1.4.0.13
        *   1.4.0.14    -   1.4.1-pre1
        *   1.4.1       -   1.4.1.1
        *   1.4.1.9     -   1.4.1.16
        *   1.4.1.17
        *   1.4.2       -   1.4.2.12
        *   1.4.3
        *   1.4.3.1     -   1.4.3.8
        *   1.4.3.9     -   1.4.3.22
        *   1.4.3.23    -   1.4.4-pre1
        *   1.4.4       -   1.4.5-pre1
        *   1.4.5       -   1.4.5.4
        *   1.4.5.5     -   1.4.5.42
        *   1.4.5.43    -   1.5.0-pre3
        *   1.5.0
        *   1.5.0.1     -   1.5.0.15
        *   1.5.0.16
        *   1.5.0.17    -   1.5.0.21
        *   1.5.0.22    -   1.5.0.23
        *   1.5.0.24
        *   1.5.0.25    -   *
     */

    public static String clientCompatibilityMin() {
        // This is the version that the SERVER needs to have for the current client.
        if (Main.ISOLATED_ENVIRONMENT) return MOD_VERSION;
        return "dev-1.5.0.25";
    }

    public static String serverCompatibilityMin() {
        // This is the version that the CLIENT needs to have for the current server.
        if (Main.ISOLATED_ENVIRONMENT) return MOD_VERSION;
        return "dev-1.5.0.25";
    }
}
