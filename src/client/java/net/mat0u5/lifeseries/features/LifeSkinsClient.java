package net.mat0u5.lifeseries.features;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//? if >= 1.21.9 {
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.core.ClientAsset;
//?}
//? if > 1.20 {
import net.minecraft.world.entity.player.PlayerSkin;
//?}

public class LifeSkinsClient {

    //? if <= 1.20 {
    /*private static final Map<String, Identifier> lifeSkinsTextures = new ConcurrentHashMap<>();
    *///?} else {
    private static final Map<String, Tuple<Identifier, PlayerSkin>> lifeSkinsTextures = new ConcurrentHashMap<>();
    //?}
    private static final Map<UUID, String> currentPlayerSkinIds = new ConcurrentHashMap<>();

    public static void handleTexture(String skinName, String teamName, boolean slim, byte[] textureData) {
        String skinId = skinName+"_"+ teamName;
        try {
            Minecraft client = Minecraft.getInstance();

            var textureId = IdentifierHelper.mod("dynamic/lifeskins/" + skinId.toLowerCase(Locale.ROOT));

            NativeImage image = NativeImage.read(new ByteArrayInputStream(textureData));

            //? if <= 1.21.4 {
            /*DynamicTexture texture = new DynamicTexture(image);
             *///?} else {
            DynamicTexture texture = new DynamicTexture(() -> skinId, image);
            //?}
            removeTexture(skinId);
            client.getTextureManager().register(textureId, texture);
            Main.LOGGER.info(TextUtils.formatString("Updated life skins texture '{}'", textureId));

            //? if <= 1.20 {
            /*lifeSkinsTextures.put(skinId, textureId);
            *///?} else if <= 1.21.6 {
            /*PlayerSkin.Model modelType = slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE;
            PlayerSkin skin = new PlayerSkin(textureId, "", textureId, textureId, modelType, false);
            lifeSkinsTextures.put(skinId, new Tuple<>(textureId, skin));
            *///?} else {
            ClientAsset.DownloadedTexture resourceTexture = new ClientAsset.DownloadedTexture(textureId, "");
            PlayerModelType modelType = slim ? PlayerModelType.SLIM : PlayerModelType.WIDE;
            PlayerSkin skin = new PlayerSkin(resourceTexture, resourceTexture, resourceTexture, modelType, false);
            lifeSkinsTextures.put(skinId, new Tuple<>(textureId, skin));
            //?}
        } catch (IOException e) {
            Main.LOGGER.error("Error while processing life skins texture '"+skinId+"'");
            e.printStackTrace();
        }
    }

    public static void setCurrentSkinId(UUID uuid, String livesNum) {
        currentPlayerSkinIds.put(uuid, livesNum);
    }

    @Nullable
    //? if <= 1.20 {
    /*public static Identifier getTexture(UUID playerUUID) {
    *///?} else {
    public static PlayerSkin getTexture(UUID playerUUID) {
    //?}
        if (playerUUID == null) return null;
        String playerTeamName = currentPlayerSkinIds.get(playerUUID);
        if (playerTeamName == null) return null;
        //? if <= 1.20 {
        /*return lifeSkinsTextures.get(playerTeamName);
        *///?} else {
        Tuple<Identifier, PlayerSkin> lifeSkin = lifeSkinsTextures.get(playerTeamName);
        if (lifeSkin == null) return null;
        return lifeSkin.y;
        //?}
    }

    public static void clearTextures() {
        for (String str : new ArrayList<>(lifeSkinsTextures.keySet())) {
            removeTexture(str);
        }
    }

    public static void removeTexture(String skinId) {
        var textureId = lifeSkinsTextures.remove(skinId);
        if (textureId != null) {
            //? if <= 1.20 {
            /*Minecraft.getInstance().getTextureManager().release(textureId);
            *///?} else {
            Minecraft.getInstance().getTextureManager().release(textureId.x);
            //?}
        }
    }
}
