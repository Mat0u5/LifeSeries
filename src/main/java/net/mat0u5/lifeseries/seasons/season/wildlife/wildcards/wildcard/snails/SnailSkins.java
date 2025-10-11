package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.packets.SnailTexturePacket;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SnailSkins {

    public static void sendTexturesTo(ServerPlayerEntity player) {
        sendTexturesTo(List.of(player));
    }

    public static void sendTexturesTo(List<ServerPlayerEntity> players) {
        for (File file : getAllSkinFiles()) {
            try {
                String name = file.getName().toLowerCase().replaceAll(".png","");
                byte[] textureData = Files.readAllBytes(file.toPath());

                SnailTexturePacket packet = new SnailTexturePacket(name, textureData);
                for (ServerPlayerEntity player : players) {
                    if (VersionControl.isDevVersion()) {
                        Main.LOGGER.info(TextUtils.formatString("Sending snail texture '{}' to {}", name, player));
                    }
                    ServerPlayNetworking.send(player, packet);
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
                String name = file.getName().toLowerCase();
                if (name.equalsIgnoreCase("example.png")) continue;
                if (!name.endsWith(".png")) continue;
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
            String name = file.getName().toLowerCase().replaceAll(".png","");
            result.add(name);
        }
        return result;
    }

    public static void createConfig() {
        File folder = new File("./config/lifeseries/wildlife/snailskins/");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Main.LOGGER.error("Failed to create folder {}", folder);
                return;
            }
        }
        ResourceHandler handler = new ResourceHandler();

        Path modelResult = new File("./config/lifeseries/wildlife/snailskins/snail.bbmodel").toPath();
        handler.copyBundledSingleFile("/files/snails/snail-old.bbmodel", modelResult);

        Path textureResult = new File("./config/lifeseries/wildlife/snailskins/example.png").toPath();
        handler.copyBundledSingleFile("/files/snails/example.png", textureResult);
    }
}
