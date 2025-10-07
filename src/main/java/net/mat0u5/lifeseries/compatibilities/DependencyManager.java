package net.mat0u5.lifeseries.compatibilities;

import net.fabricmc.loader.api.FabricLoader;

public class DependencyManager {

    public static boolean voicechatLoaded() {
        return isModLoaded("voicechat");
    }

    public static boolean flashbackLoaded() {
        return isModLoaded("flashback");
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
