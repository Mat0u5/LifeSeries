package net.mat0u5.lifeseries.resources.datapack;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.resources.ResourceHandler;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DynamicDatapackManager {
    private static final String CONFIG_TABLE_TRIVIA = "./config/lifeseries/wildlife/trivia_reward_loottable_"+DatapackManager.getMinecraftVersion()+".json";
    private static final String CONFIG_TABLE_TASK = "./config/lifeseries/secretlife/task_reward_loottable_"+DatapackManager.getMinecraftVersion()+".json";

    private static final String LOCAL_TABLE_TRIVIA = "/files/dynamicpack/loottables/trivia_reward_loottable"+DatapackManager.getResourceTriviaPackVersion()+".json";
    private static final String LOCAL_TABLE_TASK = "/files/dynamicpack/loottables/task_reward_loottable"+DatapackManager.getResourceTaskPackVersion()+".json";
    private static final String LOCAL_MCMETA = "/files/dynamicpack/pack.mcmeta";

    private static final String DATAPACK_MAIN = "Life Series Dynamic Datapack";
    private static final String DATAPACK_LOOTTABLE = DATAPACK_MAIN+"/data/lifeseriesdynamic/loot_table";
    private static final String DATAPACK_MCMETA = DATAPACK_MAIN+"/pack.mcmeta";
    private static final String DATAPACK_TABLE_TRIVIA = DATAPACK_LOOTTABLE+"/trivia_reward_loottable.json";
    private static final String DATAPACK_TABLE_TASK = DATAPACK_LOOTTABLE+"/task_reward_loottable.json";

    public static void onServerStarted(MinecraftServer server) {
        createDatapack(server);
        copyLootTables(server);
    }

    private static void createDatapack(MinecraftServer server) {
        Path datapackFolder = server.getWorldPath(LevelResource.DATAPACK_DIR);
        ResourceHandler handler = new ResourceHandler();

        datapackFolder.resolve(DATAPACK_MAIN).toFile().mkdirs();
        datapackFolder.resolve(DATAPACK_LOOTTABLE).toFile().mkdirs();
        handler.copyBundledSingleFile(LOCAL_MCMETA, datapackFolder.resolve(DATAPACK_MCMETA));
    }

    public static void copyLootTables(MinecraftServer server) {
        if (server == null) return;
        Path datapackFolder = server.getWorldPath(LevelResource.DATAPACK_DIR);
        ResourceHandler handler = new ResourceHandler();

        File configTrivia = new File(CONFIG_TABLE_TRIVIA);
        if (!configTrivia.exists()) {
            handler.copyBundledSingleFile(LOCAL_TABLE_TRIVIA, configTrivia.toPath());
        }
        File configTask = new File(CONFIG_TABLE_TASK);
        if (!configTask.exists()) {
            handler.copyBundledSingleFile(LOCAL_TABLE_TASK, configTask.toPath());
        }

        try {
            Files.copy(configTrivia.toPath(), datapackFolder.resolve(DATAPACK_TABLE_TRIVIA), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(configTask.toPath(), datapackFolder.resolve(DATAPACK_TABLE_TASK), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            Main.LOGGER.error("Error copying loot tables: {}", e.getMessage());
        }
    }

    public static void disableDatapack() {
        OtherUtils.executeCommand("datapack disable \"file/Life Series Dynamic Datapack\"");
    }

    public static void enableDatapack() {
        OtherUtils.executeCommand("datapack enable \"file/Life Series Dynamic Datapack\"");
    }
}
