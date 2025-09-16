package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.ImagePayload;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class SnailSkinsServer {
    public static void sendImageToClient(ServerPlayerEntity player, String name, int index, int maxIndex, Path imagePath) {
        try {
            BufferedImage image = ImageIO.read(Files.newInputStream(imagePath));

            if (image.getWidth() != 32 || image.getHeight() != 32) {
                Main.LOGGER.error("Image must be 32x32 pixels");
                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            ImagePayload payload = new ImagePayload(name, index, maxIndex, imageBytes);

            NetworkHandlerServer.sendImagePacket(player, payload);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> indexedSkins = new HashMap<>();
    public static int currentIndex = 0;

    public static void sendStoredImages(List<ServerPlayerEntity> players) {
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) return;
        File folder = new File("./config/lifeseries/wildlife/vhsnailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        File[] files = folder.listFiles();
        if (files == null) return;
        int maxIndex = 0;
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.toLowerCase().replaceAll(".png","");
            if (!indexedSkins.containsKey(replacedName)) {
                indexedSkins.put(replacedName, currentIndex);
                currentIndex++;
            }
            int imageIndex = indexedSkins.get(replacedName);
            maxIndex = Math.max(maxIndex,imageIndex);
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.toLowerCase().replaceAll(".png","");
            int imageIndex = indexedSkins.get(replacedName);
            for (ServerPlayerEntity player : players) {
                sendImageToClient(player, PacketNames.SNAIL_SKIN.getName(), imageIndex, maxIndex, file.toPath());
            }
        }
    }

    public static List<String> getAllSkins() {
        List<String> result = new ArrayList<>();
        File folder = new File("./config/lifeseries/wildlife/vhsnailskins/");
        File[] files = folder.listFiles();
        if (files == null) return result;
        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName().toLowerCase();
            if (name.equalsIgnoreCase("example.png")) continue;
            if (!name.endsWith(".png")) continue;
            String replacedName = name.replaceAll(".png","");
            result.add(replacedName);
        }
        return result;
    }

    public static void sendStoredImages() {
        sendStoredImages(PlayerUtils.getAllPlayers());
    }

    public static void createConfig() {
        File folder = new File("./config/lifeseries/wildlife/vhsnailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        ResourceHandler handler = new ResourceHandler();

        Path modelResult = new File("./config/lifeseries/wildlife/vhsnailskins/snail.bbmodel").toPath();
        handler.copyBundledSingleFile("/model/" + Main.MOD_ID + "/snail.bbmodel", modelResult);

        Path textureResult = new File("./config/lifeseries/wildlife/vhsnailskins/example.png").toPath();
        handler.copyBundledSingleFile("/model/" + Main.MOD_ID + "/texture/example.png", textureResult);
    }
}
