package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.command.manager.CustomCommand;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.matlib.events.common.ServerCommandEvents;
import net.mat0u5.matlib.events.common.ServerLifecycleEvents;
import net.mat0u5.matlib.events.common.ServerResourceEvents;

public class MatLibRegistry {
	public static void register() {
		registerEvents();
		registerCommands();
	}

	public static void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
		ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);
		ServerResourceEvents.RELOAD_START.register(Events::onReloadStart);
		ServerResourceEvents.RELOAD_STOPPING.register(Events::onReloadEnd);
	}

	public static void registerCommands() {
		ServerCommandEvents.CUSTOM_REGISTER.register(CustomCommand::getAllCommands);
	}
}
