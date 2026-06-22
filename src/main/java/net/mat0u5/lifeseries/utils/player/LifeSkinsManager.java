package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.LifeSkinsTexturePayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Triple;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

import static net.mat0u5.lifeseries.LifeSeries.livesManager;

public class LifeSkinsManager {

    private static final String SKINS_BASE_DIR = "config/lifeseries/lifeskins";

    private static final Map<String, Map<String, Triple<Boolean, File, Integer>>> skinsCache = new HashMap<>();

    public static Map<String, Map<String, Triple<Boolean, File, Integer>>> getCache() {
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
            if (name.equalsIgnoreCase("_legacy_skins")) continue;
            Map<String, Triple<Boolean, File, Integer>> skins = new HashMap<>();
            File[] skinFiles = playerDir.listFiles();
            if (skinFiles == null) continue;
            for (File skinFile : skinFiles) {
                if (skinFile == null || !skinFile.exists() || !skinFile.isFile()) continue;
                try {
                    String skinName = skinFile.getName();
                    if (!skinName.toLowerCase(Locale.ROOT).endsWith(".png")) continue;
                    upgradeSkinIfNeeded(name, skinFile);
                    int dotIndex = skinName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        skinName = skinName.substring(0, dotIndex);
                    }
                    boolean slim = skinName.endsWith("_slim");
                    skinName = skinName.replace("_slim","");
                    String teamName = skinName;
                    Integer intName = null;
                    try {
                        intName = skinName.equalsIgnoreCase("null") ? null : Integer.parseInt(skinName);
                        teamName = "lives_"+intName;
                    } catch (Exception ignored) {}
                    skins.put(teamName, new Triple<>(slim, skinFile, intName));
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
        for (Map.Entry<String, Map<String, Triple<Boolean, File, Integer>>> entry : skinsCache.entrySet()) {
            String lifeSkinPlayerName = entry.getKey();
            if (lifeSkinPlayerName == null) continue;
            if (entry.getValue() == null) continue;
            for (Map.Entry<String, Triple<Boolean, File, Integer>> entry2 : entry.getValue().entrySet()) {
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
                Integer teamNum = ((IPlayer) player).ls$getLives();
                if (livesManager instanceof LimitedLifeLivesManager lllm) teamNum = lllm.getEquivalentLives(teamNum);
                var lifeSkins = skinsCache.get(playerName);
                if (teamNum != null && teamNum > 0 && lifeSkins != null) {
                    Integer closestTeamNum = null;
                    for (Map.Entry<String, Triple<Boolean, File, Integer>> entry : lifeSkins.entrySet()) {
                        if (entry.getValue() == null) continue;
                        Integer entryTeamNum = entry.getValue().z;
                        if (entryTeamNum == null) continue;
                        if (teamNum < entryTeamNum) continue;
                        if (closestTeamNum == null || entryTeamNum > closestTeamNum) {
                            closestTeamNum = entryTeamNum;
                        }
                    }
                    if (closestTeamNum != null) {
                        teamName = "lives_"+closestTeamNum;
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

    public static void upgradeSkinIfNeeded(String playerName, File skinFile) {
        try {
            BufferedImage src = ImageIO.read(skinFile);
            if (src == null) return;

            // Only convert old 64x32 skins
            if (!(src.getWidth() == 64 && src.getHeight() == 32)) {
                return;
            }

            LifeSeries.LOGGER.info("Legacy 64x32 life skin detected. Converting: " + skinFile.getName());

            // Create new 64x64 transparent image
            BufferedImage dest = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dest.createGraphics();

            // Copy the entire top half
            g.drawImage(src, 0, 0, null);

            // Mirror Right Leg (0, 16) into Left Leg (16, 48)
            mirrorLimb(src, g, 0, 16, 16, 48);

            // Mirror Right Arm (40, 16) into Left Arm (32, 48)
            mirrorLimb(src, g, 40, 16, 32, 48);

            g.dispose();

            // Move the old file to a legacy folder
            Path parentDir = skinFile.toPath().getParent().getParent();
            Path legacyDir = parentDir.resolve("_legacy_skins");
            Files.createDirectories(legacyDir);
            Path legacyFile = legacyDir.resolve(playerName+"_"+skinFile.getName());
            Files.move(skinFile.toPath(), legacyFile, StandardCopyOption.REPLACE_EXISTING);

            // Save the new image
            ImageIO.write(dest, "PNG", skinFile);

        } catch (Exception e) {
            LifeSeries.LOGGER.error("Failed to convert legacy skin: " + skinFile.getName(), e);
        }
    }

    private static void mirrorLimb(BufferedImage src, Graphics2D g, int srcX, int srcY, int destX, int destY) {
        // Top Face (4x4)
        drawFlipped(src, g, srcX + 4, srcY, destX + 4, destY, 4, 4);
        // Bottom Face (4x4)
        drawFlipped(src, g, srcX + 8, srcY, destX + 8, destY, 4, 4);
        // Outside Face -> Left-side Inside Face (4x12)
        drawFlipped(src, g, srcX, srcY + 4, destX + 8, destY + 4, 4, 12);
        // Front Face (4x12)
        drawFlipped(src, g, srcX + 4, srcY + 4, destX + 4, destY + 4, 4, 12);
        // Inside Face -> Left-side Outside Face (4x12)
        drawFlipped(src, g, srcX + 8, srcY + 4, destX, destY + 4, 4, 12);
        // Back Face (4x12)
        drawFlipped(src, g, srcX + 12, srcY + 4, destX + 12, destY + 4, 4, 12);
    }

    private static void drawFlipped(BufferedImage src, Graphics2D g, int srcX, int srcY, int destX, int destY, int width, int height) {
        g.drawImage(src,
                destX + width, destY, destX, destY + height,
                srcX, srcY, srcX + width, srcY + height,
                null);
    }
}