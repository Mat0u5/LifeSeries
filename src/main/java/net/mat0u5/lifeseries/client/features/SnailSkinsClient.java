package net.mat0u5.lifeseries.client.features;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.PreBuiltSnailSkins;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.Identifier;

public class SnailSkinsClient {
    private static final Map<String, Identifier> prebuiltAssignments = new ConcurrentHashMap<>();
    private static final Map<String, Identifier> snailTextures = new HashMap<>();

    public static void handleSnailTexture(String skinName, byte[] textureData) {
        try {
            LifeSeries.LOGGER.info(TextUtils.formatString("Received snail texture '{}'", skinName));
            Minecraft client = Minecraft.getInstance();

            var textureId = IdentifierHelper.mod("dynamic/snailskin/" + skinName);

            NativeImage image = NativeImage.read(new ByteArrayInputStream(textureData));

            if (!(image.getWidth() == 128 && image.getHeight() == 128)) {
                LifeSeries.LOGGER.info("Snail texture has wrong dimensions, ignoring.");
                return;
            }

            //? if <= 1.21.4 {
            /*DynamicTexture texture = new DynamicTexture(image);
            *///?} else {
            DynamicTexture texture = new DynamicTexture(() -> skinName, image);
            //?}
            removeSnailTexture(skinName);
            client.getTextureManager().register(textureId, texture);
            LifeSeries.LOGGER.info(TextUtils.formatString("Added snail texture '{}'", textureId));

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
        var textureId = snailTextures.remove(skinName);
        if (textureId != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
            LifeSeries.LOGGER.info(TextUtils.formatString("Removed old snail texture '{}'", textureId));
        }
    }

    public static void handlePrebuiltAssignPacket(List<String> list) {
        prebuiltAssignments.clear();
        if (list == null) return;
        for (String str : list) {
            if (!str.contains(":")) continue;
            String[] split = str.split(":");
            if (split.length != 2) continue;
            String textureName = split[1];
            if (PreBuiltSnailSkins.prebuiltSkins.contains(textureName)) {
                Identifier texture = IdentifierHelper.mod("textures/entity/snail/builtin/"+textureName.toLowerCase(Locale.ROOT)+".png");
                prebuiltAssignments.put(split[0].toLowerCase(Locale.ROOT), texture);
            }
        }
    }

    public static Identifier getPrebuiltAssignTexture(String skinName) {
        if (skinName == null) return null;
        return prebuiltAssignments.get(skinName);
    }
}
