package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.SnailTexturePacket;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.utils.BufferedImageUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.server.level.ServerPlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SnailSkins {

    public static void sendTexturesTo(ServerPlayer player) {
        sendTexturesTo(List.of(player));
    }

    public static void sendTexturesTo(List<ServerPlayer> players) {
        for (File file : getAllSkinFiles()) {
            try {
                String name = file.getName().toLowerCase(Locale.ROOT).replaceAll(".png","");
                byte[] textureData = Files.readAllBytes(file.toPath());

                SnailTexturePacket packet = new SnailTexturePacket(name, textureData);
                for (ServerPlayer player : players) {
                    if (VersionControl.isDevVersion()) {
                        LifeSeries.LOGGER.info(TextUtils.formatString("Sending snail texture '{}' to {}", name, player));
                    }
                    NetworkHandlerServer.sendPacket(player, packet);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendTextures() {
        sendTexturesTo(PlayerUtils.getAllPlayers());
    }

    public static List<File> getAllSkinFiles() {
        List<File> result = new ArrayList<>();
        try {
            File folder = new File("./config/lifeseries/wildlife/snailskins/");
            File[] files = folder.listFiles();
            if (files == null) return result;
            for (File file : files) {
                if (!file.isFile()) continue;
                String name = file.getName().toLowerCase(Locale.ROOT);
                if (name.equalsIgnoreCase("example.png")) continue;
                if (!name.endsWith(".png")) continue;
                upgradeSnailSkinIfNeeded(file);
                result.add(file);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getAllSkins() {
        List<String> result = new ArrayList<>();
        for (File file : getAllSkinFiles()) {
            String name = file.getName().toLowerCase(Locale.ROOT).replaceAll(".png","");
            result.add(name);
        }
        return result;
    }

    public static void createConfig() {
        File folder = new File("./config/lifeseries/wildlife/snailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                LifeSeries.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        ResourceHandler handler = new ResourceHandler();

        Path modelResult = new File("./config/lifeseries/wildlife/snailskins/snail.bbmodel").toPath();
        handler.copyBundledSingleFile("/files/snails/snail-skins.bbmodel", modelResult);

        Path textureResult = new File("./config/lifeseries/wildlife/snailskins/example.png").toPath();
        handler.copyBundledSingleFile("/files/snails/example.png", textureResult);
    }

    public static void upgradeSnailSkinIfNeeded(File skinFile) {
        try {
            BufferedImage src = ImageIO.read(skinFile);
            if (src == null) return;

            // Only convert old 32x32 skins
            if (!(src.getWidth() == 32 && src.getHeight() == 32)) {
                return;
            }

            LifeSeries.LOGGER.info("Legacy 32x32 snail skin detected. Converting: " + skinFile.getName());

            // Create new 128x128 transparent image
            BufferedImage dest = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dest.createGraphics();

            // Parachute
            BufferedImageUtils.scaleRegion(src, g, 24, 18, 4, 4, 16, 0, 4);
            BufferedImageUtils.scaleRegion(src, g, 24, 18, 4, 4, 32, 0, 4);

            BufferedImageUtils.scaleRegionXY(src, g, 28, 18, 4, 1, 16, 16, 4, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 28, 19, 4, 1, 0, 16, 4, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 28, 20, 4, 1, 48, 16, 4, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 28, 21, 4, 1, 32, 16, 4, 1);

            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 60, 37);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 58, 38);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 58, 42);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 58, 47);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 46, 58);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 44, 58);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 40, 59);
            BufferedImageUtils.copyRegion(src, g, 26, 1, 1, 4, 42, 59);

            BufferedImageUtils.scaleRegionXY(src, g, 29, 0, 1, 1, 34, 33, 34, 4);
            BufferedImageUtils.scaleRegionXY(src, g, 29, 0, 1, 1, 0, 50, 34, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 29, 0, 1, 1, 0, 33, 34, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 29, 0, 1, 1, 16, 17, 2, 34);

            // Propeller
            BufferedImageUtils.copyRegion(src, g, 24, 18, 4, 4, 36, 53);
            BufferedImageUtils.copyRegion(src, g, 24, 18, 4, 4, 40, 53);
            BufferedImageUtils.copyRegion(src, g, 28, 18, 4, 1, 36, 57);
            BufferedImageUtils.copyRegion(src, g, 28, 19, 4, 1, 32, 57);
            BufferedImageUtils.copyRegion(src, g, 28, 20, 4, 1, 44, 57);
            BufferedImageUtils.copyRegion(src, g, 28, 21, 4, 1, 40, 57);

            BufferedImageUtils.scaleRegionXY(src, g, 25, 5, 1, 1, 37, 58, 2, 1);
            BufferedImageUtils.scaleRegionXY(src, g, 25, 5, 1, 1, 36, 59, 4, 2);

            BufferedImageUtils.copyRegion(src, g, 26, 6, 6, 6, 40, 47);

            // Main Body
            BufferedImageUtils.copyRegion(src, g, 0, 0, 8, 8, 42, 25);
            BufferedImageUtils.copyRegion(src, g, 8, 0, 8, 8, 58, 25);
            BufferedImageUtils.copyRegion(src, g, 16, 0, 8, 8, 50, 17);
            BufferedImageUtils.copyRegionFlipped(src, g, 0, 8, 8, 8, 34, 25);
            BufferedImageUtils.copyRegion(src, g, 8, 8, 8, 8, 50, 25);
            BufferedImageUtils.copyRegion(src, g, 0, 16, 8, 8, 42, 17);

            BufferedImageUtils.copyRegion(src, g, 24, 24, -4, -2, 2, 58);
            BufferedImageUtils.copyRegion(src, g, 16, 23, -4, 2, 6, 58);
            BufferedImageUtils.copyRegion(src, g, 22, 14, 4, 2, 2, 60);
            BufferedImageUtils.copyRegion(src, g, 24, 3, 2, 2, 6, 60);
            BufferedImageUtils.copyRegion(src, g, 22, 16, 4, 2, 8, 60);
            BufferedImageUtils.copyRegion(src, g, 24, 1, 2, 2, 0, 60);

            BufferedImageUtils.copyRegion(src, g, 12, 20, -4, -4, 20, 51);
            BufferedImageUtils.copyRegion(src, g, 20, 8, -4, 4, 24, 51);
            BufferedImageUtils.copyRegion(src, g, 26, 12, 4, 2, 16, 55);
            BufferedImageUtils.scaleRegionXY(src, g, 0, 24, 4, 1, 20, 55, 1, 2);
            BufferedImageUtils.copyRegion(src, g, 26, 12, 4, 2, 24, 55);
            BufferedImageUtils.scaleRegionXY(src, g, 24, 0, 4, 1, 28, 55, 1, 2);

            BufferedImageUtils.copyRegion(src, g, 12, 24, -4, -8, 42, 37);
            BufferedImageUtils.copyRegion(src, g, 20, 8, -4, 8, 46, 37);
            BufferedImageUtils.copyRegion(src, g, 22, 12, 8, 2, 34, 45);
            BufferedImageUtils.scaleRegionXY(src, g, 0, 24, 4, 1, 42, 45, 1, 2);
            BufferedImageUtils.copyRegion(src, g, 22, 12, 8, 2, 46, 45);
            BufferedImageUtils.scaleRegionXY(src, g, 24, 0, 4, 1, 54, 45, 1, 2);

            BufferedImageUtils.copyRegion(src, g, 26, 10, -4, -2, 18, 57);
            BufferedImageUtils.copyRegion(src, g, 26, 10, -4, 2, 22, 57);
            BufferedImageUtils.copyRegion(src, g, 16, 23, 2, 2, 16, 59);
            BufferedImageUtils.copyRegion(src, g, 12, 21, 4, 2, 18, 59);
            BufferedImageUtils.copyRegion(src, g, 18, 23, 2, 2, 22, 59);
            BufferedImageUtils.copyRegion(src, g, 16, 21, 4, 2, 24, 59);

            BufferedImageUtils.copyRegion(src, g, 24, 20, -4, -2, 50, 53);
            BufferedImageUtils.copyRegion(src, g, 24, 20, -4, 2, 54, 53);
            BufferedImageUtils.copyRegion(src, g, 20, 8, 2, 5, 48, 55);
            BufferedImageUtils.copyRegion(src, g, 12, 16, 4, 5, 50, 55);
            BufferedImageUtils.copyRegion(src, g, 20, 13, 2, 5, 54, 55);
            BufferedImageUtils.copyRegion(src, g, 16, 16, 4, 5, 56, 55);


            BufferedImageUtils.copyRegion(src, g, 8, 25, -1, -1, 13, 58);
            BufferedImageUtils.copyRegion(src, g, 9, 24, -1, 1, 14, 58);
            BufferedImageUtils.copyRegion(src, g, 5, 24, 1, 3, 12, 59);
            BufferedImageUtils.copyRegion(src, g, 4, 24, 1, 3, 13, 59);
            BufferedImageUtils.copyRegion(src, g, 6, 24, 1, 3, 14, 59);
            BufferedImageUtils.copyRegion(src, g, 24, 5, 1, 3, 15, 59);

            BufferedImageUtils.copyRegion(src, g, 8, 25, -1, -1, 29, 57);
            BufferedImageUtils.copyRegion(src, g, 9, 24, -1, 1, 30, 57);
            BufferedImageUtils.copyRegion(src, g, 5, 24, 1, 3, 28, 58);
            BufferedImageUtils.copyRegion(src, g, 4, 24, 1, 3, 29, 58);
            BufferedImageUtils.copyRegion(src, g, 6, 24, 1, 3, 30, 58);
            BufferedImageUtils.copyRegion(src, g, 24, 5, 1, 3, 31, 58);

            g.dispose();

            // Move the old file to a legacy folder
            Path parentDir = skinFile.toPath().getParent();
            Path legacyDir = parentDir.resolve("_legacy_skins");
            Files.createDirectories(legacyDir);

            Path legacyFile = legacyDir.resolve(skinFile.getName());
            Files.move(skinFile.toPath(), legacyFile, StandardCopyOption.REPLACE_EXISTING);

            // Save the new image
            ImageIO.write(dest, "PNG", skinFile);

        } catch (Exception e) {
            LifeSeries.LOGGER.error("Failed to convert legacy snail skin: " + skinFile.getName(), e);
        }
    }
}
