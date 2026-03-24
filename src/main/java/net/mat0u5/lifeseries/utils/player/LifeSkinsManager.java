package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LifeSkinsManager {

    private static final String SKINS_BASE_DIR = "config/lifeseries/lifeskins";

    private static final Map<UUID, File> lastAppliedFile = new HashMap<>();
    private static final Map<String, Map<Integer, Tuple<Boolean, File>>> skinsCache = new HashMap<>();

    public static Map<String, Map<Integer, Tuple<Boolean, File>>> getCache() {
        return skinsCache;
    }

    public static void refreshLifeSkin(ServerPlayer player) {
        refreshLifeSkin(player, false);
    }

    private static void refreshLifeSkin(ServerPlayer player, boolean forceReload) {
        Integer currentLives = player.ls$getLives();
        UUID uuid = player.getUUID();

        Tuple<Boolean, File> skinInfo = resolveFile(player, currentLives);
        boolean slim = skinInfo.x;
        File skinFile = skinInfo.y;

        if (!forceReload) {
            if (lastAppliedFile.containsKey(uuid) && Objects.equals(lastAppliedFile.get(uuid), skinFile)) {
                return;
            }
            if (!lastAppliedFile.containsKey(uuid) && skinFile == null) {
                return;
            }
        }

        if (skinFile != null) {
            if (ProfileManager.manualSkins.containsKey(player.getUUID())) {
                ProfileManager.ProfileChange skinChange = ProfileManager.ProfileChange.set(ProfileManager.manualSkins.get(player.getUUID()));
                ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.none());
            }
            else {
                Main.LOGGER.info("[LifeSkins] Applying skin for " + player.getScoreboardName() + " at " + currentLives + " lives: " + skinFile.getPath());
                if (slim) {
                    ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.fileSlim(skinFile.getAbsolutePath()), ProfileManager.ProfileChange.none());
                }
                else {
                    ProfileManager.modifyProfile(player, ProfileManager.ProfileChange.file(skinFile.getAbsolutePath()), ProfileManager.ProfileChange.none());
                }
            }
            lastAppliedFile.put(uuid, skinFile);
        } else {
            // No Life Skins -> Back to default skin
            Main.LOGGER.info("[LifeSkins] No skin file for " + player.getScoreboardName() + " at " + currentLives + " lives, restoring original.");
            ProfileManager.ProfileChange skinChange = ProfileManager.ProfileChange.original();
            if (SubInManager.isSubbingIn(player.getUUID()) && SubInManager.CHANGE_SKIN) {
                skinChange = ProfileManager.ProfileChange.set(OtherUtils.profileName(SubInManager.getSubstitutedPlayer(player.getUUID())));
            }
            if (ProfileManager.manualSkins.containsKey(player.getUUID())) {
                skinChange = ProfileManager.ProfileChange.set(ProfileManager.manualSkins.get(player.getUUID()));
            }
            ProfileManager.modifyProfile(player, skinChange, ProfileManager.ProfileChange.none());
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

    private static Tuple<Boolean, File> resolveFile(ServerPlayer player, Integer lifeCount) {
        Map<Integer, Tuple<Boolean, File>> skins = skinsCache.get(player.getScoreboardName());
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
            Tuple<Boolean, File> skinInfo = skins.get(lifeCount);
            if (skinInfo != null && skinInfo.y != null) return skinInfo;

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
                        skinInfo = skins.get(currentLifeCount);
                        if (skinInfo != null && skinInfo.y != null) return skinInfo;
                    }
                }
            }
        }

        return new Tuple<>(false, null);
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
            Map<Integer, Tuple<Boolean, File>> skins = new HashMap<>();
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
                    boolean slim = skinName.endsWith("_slim");
                    skinName = skinName.replace("_slim","");
                    Integer intName = skinName.equalsIgnoreCase("null") ? null : Integer.parseInt(skinName);
                    skins.put(intName, new Tuple<>(slim, skinFile));
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