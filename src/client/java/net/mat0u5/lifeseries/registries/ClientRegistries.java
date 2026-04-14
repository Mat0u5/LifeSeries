package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.mat0u5.lifeseries.command.ClientCommands;
import net.mat0u5.lifeseries.events.ClientEvents;

public class ClientRegistries {
	public static void registerModStuff() {
		ClientEvents.registerClientEvents();//TODO
		ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);//TODO
	}
}