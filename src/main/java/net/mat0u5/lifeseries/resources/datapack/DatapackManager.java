package net.mat0u5.lifeseries.resources.datapack;

import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.server.MinecraftServer;

import static net.mat0u5.lifeseries.Main.server;

public class DatapackManager {
    public static void onServerStarted(MinecraftServer server) {
        DynamicDatapackManager.onServerStarted(server);
        TaskScheduler.scheduleTask(50, OtherUtils::reloadServerNoUpdate);
    }

    public static void onReloadStart() {
        DynamicDatapackManager.copyLootTables(server);
    }

    public static void onReloadEnd() {
        DynamicDatapackManager.enableDatapack();
    }

    public static String getMinecraftVersion() {
        //? if = 1.20
        /*return "1.20-1.20.1";*/
        //? if = 1.20.2
        //return "1.20.2";
        //? if = 1.20.3
        //return "1.20.3-1.20.4";
        //? if = 1.20.5
        //return "1.20.5-1.20.6";
        //? if = 1.21
        //return "1.21-1.21.1";
        //? if = 1.21.2
        /*return "1.21.2-1.21.3";*/
        //? if = 1.21.4
        /*return "1.21.4";*/
        //? if = 1.21.5
        /*return "1.21.5";*/
        //? if = 1.21.6
        /*return "1.21.6";*/
        //? if = 1.21.9
        //return "1.21.9";
        //? if = 1.21.11
        return "1.21.11";
        //? if = 26.1
        //return "26.1";
    }

    public static String getResourceTriviaPackVersion() {
        //? if < 1.20.5 {
        /*return "_pre1.20.5";
        *///?} else if < 1.21 {
        /*return "_pre1.21";
        *///?} else if <= 1.21.4 {
        /*return "";
        *///?} else {
        return "_1.21.5";
         //?}
    }

    public static String getResourceNiceLifeTriviaPackVersion() {
        //? if < 1.20.5 {
        /*return "_pre1.20.5";
        *///?} else if < 1.21 {
        /*return "_pre1.21";
        *///?} else if <= 1.21.4 {
        /*return "";
        *///?} else if <= 1.21.5 {
        /*return "_1.21.5";
        *///?} else {
        return "_1.21.6";
        //?}
    }

    public static String getResourceTaskPackVersion() {
        //? if < 1.20.5 {
        /*return "_pre1.20.5";
        *///?} else if < 1.21 {
        /*return "_pre1.21";
        *///?} else if <= 1.21.4 {
        /*return "";
        *///?} else {
        return "_1.21.5";
        //?}
    }
}
