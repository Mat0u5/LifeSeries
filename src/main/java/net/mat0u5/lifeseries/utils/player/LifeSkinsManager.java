package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LifeSkinsManager {

    private static final String SKINS_BASE_DIR = "config/lifeseries/lifeskins";

    private static final Map<UUID, File> lastAppliedFile = new HashMap<>();
    private static final Map<String, Map<Integer, File>> skinsCache = new HashMap<>();

    public static Map<String, Map<Integer, File>> getCache() {
        return skinsCache;
    }

    public static void refreshLifeSkin(ServerPlayer player) {
        refreshLifeSkin(player, false);
    }

    private static void refreshLifeSkin(ServerPlayer player, boolean forceReload) {
        Integer currentLives = player.ls$getLives();
        UUID uuid = player.getUUID();

        File skinFile = resolveFile(player, currentLives);
        if (!forceReload) {
            if (lastAppliedFile.containsKey(uuid) && Objects.equals(lastAppliedFile.get(uuid), skinFile)) {
                return;
            }
            if (!lastAppliedFile.containsKey(uuid) && skinFile == null) {
                return;
            }
        }

        if (skinFile != null) {
            Main.LOGGER.info("[LifeSkins] Applying skin for " + player.getScoreboardName() + " at " + currentLives + " lives: " + skinFile.getPath());

            ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.FILE.withInfo(skinFile.getAbsolutePath()), ProfileManager.ProfileChange.NONE);
            lastAppliedFile.put(uuid, skinFile);
        } else {
            // No Life Skins -> Back to default skin
            Main.LOGGER.info("[LifeSkins] No skin file for " + player.getScoreboardName() + " at " + currentLives + " lives, restoring original.");
            ProfileManager.ProfileChange skinChange = ProfileManager.ProfileChange.ORIGINAL;
            if (SubInManager.isSubbingIn(player.getUUID()) && SubInManager.CHANGE_SKIN) {
                skinChange = ProfileManager.ProfileChange.SET.withInfo(OtherUtils.profileName(SubInManager.getSubstitutedPlayer(player.getUUID())));
            }
            ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.NONE);
            lastAppliedFile.put(uuid, null);
        }
    }

    public static void reloadSkin(ServerPlayer player) {
        refreshLifeSkin(player, true);
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        lastAppliedFile.remove(player.getUUID());
    }

    public static void onPlayerJoin(ServerPlayer player) {
        reloadSkin(player);
    }

    public static void reloadAll() {
        reloadSkinsCache();
        PlayerUtils.getAllPlayers().forEach(LifeSkinsManager::reloadSkin);
    }

    private static File resolveFile(ServerPlayer player, Integer lifeCount) {
        Map<Integer, File> skins = skinsCache.get(player.getScoreboardName());
        if (skins == null) {
            // If no name skins were found, use the UUID files
            UUID uuid = player.getUUID();
            if (SubInManager.isSubbingIn(player.getUUID())) {
                uuid = SubInManager.getSubstitutedPlayerUUID(player.getUUID());
            }
            if (uuid != null) {
                skins = skinsCache.get(uuid.toString());
            }
        }

        if (skins != null && !skins.isEmpty()) {
            File file = skins.get(lifeCount);
            if (file != null) return file;

            if (lifeCount != null) {
                List<Integer> sortedLives = new ArrayList<>();
                for (Integer availableCount : skins.keySet()) {
                    if (availableCount == null) continue;
                    sortedLives.add(availableCount);
                }
                Collections.sort(sortedLives);
                for (int i = sortedLives.size()-1; i >= 0; i--) {
                    int currentLifeCount = sortedLives.get(i);
                    if (currentLifeCount <= lifeCount) {
                        file = skins.get(currentLifeCount);
                        if (file != null) return file;
                    }
                }
            }
        }

        return null;
    }

    public static void reloadSkinsCache() {
        skinsCache.clear();
        File rootFolder = new File(SKINS_BASE_DIR);
        if (!rootFolder.exists()) {
            createLifeSkinsFolder(rootFolder);
        }
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            Main.LOGGER.error("[LifeSkins] failed to create main folder.");
            return;
        }
        File[] playerDirs = rootFolder.listFiles();
        if (playerDirs == null) return;
        for (File playerDir : playerDirs) {
            if (playerDir == null || !playerDir.exists() || !playerDir.isDirectory()) continue;
            String name = playerDir.getName();
            Map<Integer, File> skins = new HashMap<>();
            File[] skinFiles = playerDir.listFiles();
            if (skinFiles == null) continue;
            for (File skinFile : skinFiles) {
                if (skinFile == null || !skinFile.exists() || !skinFile.isFile()) continue;
                try {
                    String skinName = skinFile.getName();
                    int dotIndex = skinName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        skinName = skinName.substring(0, dotIndex);
                    }
                    Integer intName = skinName.equalsIgnoreCase("null") ? null : Integer.parseInt(skinName);
                    skins.put(intName, skinFile);
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
}