package net.mat0u5.lifeseries.features;

import java.util.HashMap;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class SnailSkinsClient {
    private static final Map<String, Identifier> snailTextures = new HashMap<>();

    public static void handleSnailTexture(String skinName, byte[] textureData) {
        try {
            Main.LOGGER.info(TextUtils.formatString("Received snail texture '{}'", skinName));
            MinecraftClient client = MinecraftClient.getInstance();

            Identifier textureId = Identifier.of(Main.MOD_ID, "dynamic/snailskin/" + skinName);

            NativeImage image = NativeImage.read(new ByteArrayInputStream(textureData));

            //? if <= 1.21.4 {
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            //?} else {
            /*NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> skinName, image);
            *///?}
            removeSnailTexture(skinName);
            client.getTextureManager().registerTexture(textureId, texture);
            Main.LOGGER.info(TextUtils.formatString("Added snail texture '{}'", textureId));

            snailTextures.put(skinName, textureId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Identifier getSnailTexture(String skinName) {
        if (skinName == null) return null;
        return snailTextures.get(skinName);
    }

    public static void removeSnailTexture(String skinName) {
        Identifier textureId = snailTextures.remove(skinName);
        if (textureId != null) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(textureId);
            Main.LOGGER.info(TextUtils.formatString("Removed old snail texture '{}'", textureId));
        }
    }
}
