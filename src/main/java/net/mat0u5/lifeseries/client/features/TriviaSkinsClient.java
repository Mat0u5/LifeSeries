package net.mat0u5.lifeseries.client.features;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TriviaSkinsClient {
    private static final Map<String, Identifier> triviaTextures = new HashMap<>();

    public static void handleTriviaTexture(String skinName, byte[] textureData) {
        try {
            LifeSeries.LOGGER.info(TextUtils.formatString("Received trivia texture '{}'", skinName));
            Minecraft client = Minecraft.getInstance();

            var textureId = IdentifierHelper.mod("dynamic/triviaskin/" + skinName);

            NativeImage image = NativeImage.read(new ByteArrayInputStream(textureData));

            if (!(image.getWidth() == 128 && image.getHeight() == 128)) {
                LifeSeries.LOGGER.info("Trivia texture has wrong dimensions, ignoring.");
                return;
            }

            //? if <= 1.21.4 {
            /*DynamicTexture texture = new DynamicTexture(image);
            *///?} else {
            DynamicTexture texture = new DynamicTexture(() -> skinName, image);
            //?}
            removeTriviaTexture(skinName);
            client.getTextureManager().register(textureId, texture);
            LifeSeries.LOGGER.info(TextUtils.formatString("Added trivia texture '{}'", textureId));

            triviaTextures.put(skinName, textureId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Identifier getTriviaTexture(String skinName) {
        if (skinName == null) return null;
        return triviaTextures.get(skinName);
    }

    public static void removeTriviaTexture(String skinName) {
        var textureId = triviaTextures.remove(skinName);
        if (textureId != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
            LifeSeries.LOGGER.info(TextUtils.formatString("Removed old trivia texture '{}'", textureId));
        }
    }
}
