package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.LifeSkinsTexturePayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LifeSkinsManager {

    private static final String SKINS_BASE_DIR = "config/lifeseries/lifeskins";

    private static final Map<String, Map<String, Tuple<Boolean, File>>> skinsCache = new HashMap<>();

    public static Map<String, Map<String, Tuple<Boolean, File>>> getCache() {
        return skinsCache;
    }

    public static void refreshLifeSkin(ServerPlayer player) {
        sendTeamNumUpdatesFrom(player);
    }

    public static void onPlayerJoin(ServerPlayer player) {
        refreshLifeSkin(player);
        sendImagePackets(List.of(player));
    }

    public static void reloadAll() {
        reloadCache();
        PlayerUtils.getAllPlayers().forEach(LifeSkinsManager::refreshLifeSkin);
    }

    public static void reloadCache() {
        reloadSkinsCache();
        sendImagePackets();
    }

    private static void reloadSkinsCache() {
        skinsCache.clear();
        File rootFolder = new File(SKINS_BASE_DIR);
        createLifeSkinsFolder(rootFolder);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            LifeSeries.LOGGER.error("[LifeSkins] failed to create main folder.");
            return;
        }
        File[] playerDirs = rootFolder.listFiles();
        if (playerDirs == null) return;
        for (File playerDir : playerDirs) {
            if (playerDir == null || !playerDir.exists() || !playerDir.isDirectory()) continue;
            String name = playerDir.getName();
            Map<String, Tuple<Boolean, File>> skins = new HashMap<>();
            File[] skinFiles = playerDir.listFiles();
            if (skinFiles == null) continue;
            for (File skinFile : skinFiles) {
                if (skinFile == null || !skinFile.exists() || !skinFile.isFile()) continue;
                try {
                    String skinName = skinFile.getName();
                    if (!skinName.toLowerCase(Locale.ROOT).endsWith(".png")) continue;
                    int dotIndex = skinName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        skinName = skinName.substring(0, dotIndex);
                    }
                    boolean slim = skinName.endsWith("_slim");
                    skinName = skinName.replace("_slim","");
                    String teamName = skinName;
                    try {
                        Integer intName = skinName.equalsIgnoreCase("null") ? null : Integer.parseInt(skinName);
                        teamName = "lives_"+intName;
                    } catch (Exception ignored) {}
                    skins.put(teamName, new Tuple<>(slim, skinFile));
                }catch(Exception ignored) {}
            }
            if (skins.isEmpty()) continue;
            skinsCache.put(name, skins);
        }
    }

    public static void createLifeSkinsFolder(File rootFolder) {
        Path exampleSkins = rootFolder.toPath().resolve("Steve");
        try {
            Files.createDirectories(rootFolder.toPath());
            Files.createDirectories(exampleSkins);
        }catch(Exception e) {
            e.printStackTrace();
        }
        ResourceHandler handler = new ResourceHandler();
        handler.copyBundledSingleFile("/files/lifeskins/Steve/1.png", exampleSkins.resolve("1.png"));
        handler.copyBundledSingleFile("/files/lifeskins/Steve/2.png", exampleSkins.resolve("2.png"));
        handler.copyBundledSingleFile("/files/lifeskins/Steve/3.png", exampleSkins.resolve("3.png"));
    }

    public static void sendImagePackets() {
        sendImagePackets(PlayerUtils.getAllPlayers());
    }

    public static void sendImagePackets(List<ServerPlayer> targets) {
        SimplePackets.LIFESKINS_RELOAD_START.target(targets).sendToClient();
        for (Map.Entry<String, Map<String, Tuple<Boolean, File>>> entry : skinsCache.entrySet()) {
            String lifeSkinPlayerName = entry.getKey();
            if (lifeSkinPlayerName == null) continue;
            if (entry.getValue() == null) continue;
            for (Map.Entry<String, Tuple<Boolean, File>> entry2 : entry.getValue().entrySet()) {
                if (entry2.getValue() == null) continue;
                String teamName = entry2.getKey();
                Boolean slim = entry2.getValue().x;
                File file = entry2.getValue().y;
                if (file == null) continue;
                if (slim == null) slim = false;

                String skinId = lifeSkinPlayerName+"_"+ teamName;
                String identifier = "dynamic/lifeskins/" + skinId.toLowerCase(Locale.ROOT);
                if (!isValidIdentifier(identifier)) {
                    LifeSeries.LOGGER.error(TextUtils.formatString("LifeSkins error: Non [a-z0-9/._-] character in path of location: {}:{}", "lifeseries", identifier));
                    continue;
                }

                try {
                    byte[] textureData = Files.readAllBytes(file.toPath());
                    // Validate a valid PNG struct
                    if (textureData == null || textureData.length < 8 ||
                            (textureData[0] & 0xFF) != 0x89 ||
                            (textureData[1] & 0xFF) != 0x50 ||
                            (textureData[2] & 0xFF) != 0x4E ||
                            (textureData[3] & 0xFF) != 0x47) {
                        LifeSeries.LOGGER.error(
                                "LifeSkins error: Received invalid PNG for skin '{}' ({}). Length={}, first bytes={}",
                                lifeSkinPlayerName, teamName, textureData == null ? "null" : textureData.length,
                                textureData != null && textureData.length >= 4
                                        ? String.format("%02X %02X %02X %02X", textureData[0] & 0xFF, textureData[1] & 0xFF, textureData[2] & 0xFF, textureData[3] & 0xFF)
                                        : "N/A"
                        );
                        continue;
                    }

                    LifeSkinsTexturePayload packet = new LifeSkinsTexturePayload(lifeSkinPlayerName, teamName, slim, textureData);
                    for (ServerPlayer player : targets) {
                        if (VersionControl.isDevVersion()) {
                            LifeSeries.LOGGER.info(TextUtils.formatString("Sending life skins '{}' at '{}' to {}", lifeSkinPlayerName, teamName, player));
                        }
                        NetworkHandlerServer.sendPacket(player, packet);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isValidIdentifier(String string) {
        for(int i = 0; i < string.length(); ++i) {
            if (!validIdentifierChar(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean validIdentifierChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    public static void sendTeamNumUpdates() {
        PlayerUtils.getAllPlayers().forEach(LifeSkinsManager::sendTeamNumUpdatesFrom);
    }

    public static void sendTeamNumUpdatesFrom(ServerPlayer player) {
        SimplePackets.LIFESKINS_PLAYER.sendToClient(getLifeSkinsPacketInfo(player));
    }

    public static void sendTeamNumUpdatesTo(ServerPlayer target) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            SimplePackets.LIFESKINS_PLAYER.target(target).sendToClient(getLifeSkinsPacketInfo(player));
        }
    }

    public static List<String> getLifeSkinsPacketInfo(ServerPlayer player) {
        String teamName = "";
        Team team = player.getTeam();
        if (team != null) {
            teamName = team.getName();
        }

        UUID uuid = player.getUUID();
        String playerName = null;
        if (SubInManager.isSubbingIn(uuid)) {
            boolean changeSkin = SubInManager.CHANGE_SKIN;
            boolean changeName = SubInManager.CHANGE_NAME;

            if (changeSkin && !changeName) {
                playerName = OtherUtils.profileName(SubInManager.getSubstitutedPlayer(uuid));
            }
            if (!changeSkin && changeName) {
                playerName = OtherUtils.profileName(SubInManager.getSubstituterOriginal(uuid));
            }
        }
        if (playerName == null) {
            playerName = player.getScoreboardName();
        }

        if (teamName.startsWith("lives_")) {
            try {
                String lastPart = teamName.replaceAll("lives_", "");
                Integer teamNum = lastPart.equalsIgnoreCase("null") ? null : Integer.parseInt(lastPart);
                var lifeSkins = skinsCache.get(playerName);
                if (teamNum != null && lifeSkins != null) {
                    for (int closestTeam = teamNum; closestTeam >= 0; closestTeam--) {
                        if (lifeSkins.containsKey("lives_"+closestTeam)) {
                            teamName = "lives_"+closestTeam;
                            break;
                        }
                    }
                }
            }catch(Exception e) {}
        }

        if (ProfileManager.manualSkins.containsKey(uuid)) {
            if (!ProfileManager.manualSkins.get(uuid).equalsIgnoreCase(playerName)) {
                teamName = "";
            }
        }

        String skinId = playerName+"_"+teamName;
        return List.of(uuid.toString(), skinId);
    }
}