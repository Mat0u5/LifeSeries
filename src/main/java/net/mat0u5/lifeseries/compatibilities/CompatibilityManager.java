package net.mat0u5.lifeseries.compatibilities;

import net.mat0u5.matlib.MatLib;

public class CompatibilityManager {

    public static boolean fabricApiLoaded() {
        return isModLoaded("fabric-api");
    }

    public static boolean voicechatLoaded() {
        return isModLoaded("voicechat");
    }

    public static boolean flashbackLoaded() {
        return isModLoaded("flashback");
    }

    public static boolean replayModLoaded() {
        return isModLoaded("replaymod");
    }

    public static boolean appleSkinLoaded() {
        return isModLoaded("appleskin");
    }

    public static boolean isModLoaded(String modId) {
        return MatLib.platform().isModLoaded(modId);
    }
}
