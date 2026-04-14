package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.lifeseries.command.manager.CommandManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;

public class ModRegistries {

    public static void registerModStuff() {
        registerCommands();
        registerEvents();
        TextUtils.setEmotes();
        MobRegistry.registerMobs();
        ParticleRegistry.registerParticles();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CommandManager::registerAllCommands);
    }

    private static void registerEvents() {
        TaskScheduler.registerTickHandler();
    }
}
