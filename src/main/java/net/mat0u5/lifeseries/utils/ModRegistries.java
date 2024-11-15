package net.mat0u5.lifeseries.utils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.lifeseries.command.Commands;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.series.lastlife.LastLifeCommands;

public class ModRegistries {
    public static void registerModStuff() {
        registerCommands();
        registerEvents();
        TextUtils.setEmotes();
    }
    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(Commands::register);
        CommandRegistrationCallback.EVENT.register(LastLifeCommands::register);
    }
    private static void registerEvents() {
        Events.register();
    }
}
