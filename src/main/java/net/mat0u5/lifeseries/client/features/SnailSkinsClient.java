package net.mat0u5.lifeseries.client.features;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.PreBuiltSnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.client.utils.SyncedTexturesClient;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnailSkinsClient {
    private static final Map<String, Identifier> prebuiltAssignments = new ConcurrentHashMap<>();

    public static Identifier getSnailTexture(String skinName) {
        return SyncedTexturesClient.getTexture(SnailSkins.TEXTURES.getId(), skinName);
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
                Identifier texture = IdentifierHelper.lifeseries("textures/entity/snail/builtin/"+textureName.toLowerCase(Locale.ROOT)+".png");
                prebuiltAssignments.put(split[0].toLowerCase(Locale.ROOT), texture);
            }
        }
    }

    public static Identifier getPrebuiltAssignTexture(String skinName) {
        if (skinName == null) return null;
        return prebuiltAssignments.get(skinName);
    }
}
