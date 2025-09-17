package net.mat0u5.lifeseries.utils.versions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UpdateChecker {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static boolean majorUpdateAvailable = false;
    public static boolean updateAvailable = false;
    public static String majorVersionName;
    public static String versionName;
    public static String versionDescription;
    public static String changelogLink = null;
    public static int version;
    public static final boolean TEST_UPDATE_FAKE = false;
    public static final boolean TEST_UPDATE_LAST = false;

    public static void checkForMajorUpdates() {
        if (TEST_UPDATE_FAKE) {
            updateAvailable = true;
            versionName = "versionName";
            majorVersionName = "majorVersionName";
            versionDescription = "versionDescription";
            version = 2_000_000_000;
            return;
        }
        executor.submit(() -> {
            HttpURLConnection connection = null;
            try {
                // Connect to the GitHub API
                connection = (HttpURLConnection) new URI(Main.LATEST_UPDATE_URL).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    // Parse the JSON response
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    String name = json.get("tag_name").getAsString();

                    int currentVersionNumber = VersionControl.getModVersionInt(Main.MOD_VERSION);
                    int updateVersionNumber = VersionControl.getModVersionInt(name);

                    // Compare the current version with the latest version
                    if (currentVersionNumber < updateVersionNumber || TEST_UPDATE_LAST) {
                        Main.LOGGER.info("New major version found: "+name);
                        updateAvailable = true;
                        majorUpdateAvailable = true;
                        versionName = name;
                        majorVersionName = name;
                        version = updateVersionNumber;
                        versionDescription = formatDescription(json.get("body").getAsString());
                    }


                } else {
                    Main.LOGGER.error("Failed to fetch update info: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Main.LOGGER.error("Error while checking for updates: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                checkForMinorUpdates();
            }
        });
    }

    public static void checkForMinorUpdates() {
        executor.submit(() -> {
            HttpURLConnection connection = null;
            try {
                // Connect to the GitHub API
                connection = (HttpURLConnection) new URL(Main.ALL_UPDATES_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    // Parse the JSON response
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonArray jsonList = JsonParser.parseReader(reader).getAsJsonArray();

                    for (JsonElement jsonElement : jsonList.asList()) {
                        JsonObject json = jsonElement.getAsJsonObject();

                        String name = json.get("tag_name").getAsString();
                        boolean draft = json.get("draft").getAsBoolean();
                        boolean prerelease = json.get("prerelease").getAsBoolean();

                        if (draft || prerelease) continue;

                        try {
                            int currentVersionNumber = VersionControl.getModVersionInt(Main.MOD_VERSION);
                            int updateVersionNumber = VersionControl.getModVersionInt(name);

                            if (version < updateVersionNumber && currentVersionNumber < updateVersionNumber) {
                                Main.LOGGER.info("New minor version found: "+name);
                                updateAvailable = true;
                                versionName = name;
                                version = updateVersionNumber;
                                if (!majorUpdateAvailable) majorVersionName = versionName;
                                if (!majorUpdateAvailable) versionDescription = formatDescription(json.get("body").getAsString());
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                            Main.LOGGER.error(TextUtils.formatString("Error while parsing version number for update: {} - {}", name, e.getMessage()));
                        }
                    }

                } else {
                    Main.LOGGER.error("Failed to fetch update info: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Main.LOGGER.error("Error while checking for updates: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public static String getChangelogLink() {
        if (changelogLink != null) return changelogLink;
        if (majorVersionName != null) {
            return "https://github.com/Mat0u5/LifeSeries/blob/main/docs/changelogs/"+majorVersionName+".md";
        }
        return "https://github.com/Mat0u5/LifeSeries/blob/main/docs/changelogs/"+versionName+".md";
    }

    public static String formatDescription(String rawDesctiption) {
        if (rawDesctiption.contains("~~")) {
            String[] split = rawDesctiption.split("~~");
            String newDesctiption = split[0];
            if (!newDesctiption.isEmpty()) {
                rawDesctiption = newDesctiption;
            }
            if (split.length >= 2) {
                for (int i = 1; i < split.length; i++) {
                    try {
                        String line = split[i].trim();
                        if (line.startsWith("changelogLink=")) {
                            changelogLink = line.replace("changelogLink=","");
                        }
                        if (line.startsWith("versionName=")) {
                            versionName = line.replace("versionName=","");
                        }
                        if (line.startsWith("majorVersionName=")) {
                            majorVersionName = line.replace("majorVersionName=","");
                        }
                        if (line.startsWith("versionDescription=")) {
                            rawDesctiption = line.replace("versionDescription=","");
                        }
                        if (line.startsWith("version=")) {
                            version = Integer.parseInt(line.replace("version=",""));
                        }
                    }catch(Exception ignored) {}
                }
            }
        }
        return rawDesctiption;
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        if (!updateAvailable || versionName == null) {
            return;
        }
        if (!VersionControl.isDevVersion()) {
            Text discordText = Text.literal("§7Click ").append(
                    Text.literal("here")
                        .styled(style -> style
                            .withColor(Formatting.BLUE)
                            .withClickEvent(TextUtils.openURLClickEvent("https://discord.gg/QWJxfb4zQZ"))
                            .withUnderline(true)
                        )).append(Text.of("§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)\n"));
            Text updateText =
                    TextUtils.formatLoosely("A new version of the Life Series Mod is available ({}) §nserver-side§f. \n",versionName)
                        .styled(style -> style
                            .withHoverEvent(
                                TextUtils.showTextHoverEvent(TextUtils.formatLoosely(
                                    "§7§nUpdate Description:§r\n\n{}", versionDescription
                                ))
                            )
                        )
                        .append(
                            Text.literal("Click to download on Modrinth")
                                .styled(style -> style
                                    .withColor(Formatting.BLUE)
                                        .withClickEvent(TextUtils.openURLClickEvent("https://modrinth.com/mod/life-series"))
                                    .withUnderline(true)
                                )
                        );
            if (PermissionManager.isAdmin(player)) {
                player.sendMessage(updateText);
                player.sendMessage(discordText);
            }
        }
        else {
            Text updateText =
                Text.literal("§c[Life Series] You are playing on a developer version, there are probably some bugs, and it's possible that some features don't work.\n")
                    .append(
                        Text.literal("Download full releases on Modrinth")
                            .styled(style -> style
                                .withColor(Formatting.BLUE)
                                .withClickEvent(TextUtils.openURLClickEvent("https://modrinth.com/mod/life-series"))
                                .withUnderline(true)
                            )
                    );

            player.sendMessage(updateText);
        }
    }
    public static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
