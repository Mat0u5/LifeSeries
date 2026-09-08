package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.command.manager.CustomCommand;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.matlib.events.common.*;

//? if <= 1.20.2 {
/*import net.mat0u5.lifeseries.seasons.season.Season;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import java.util.Optional;
*///?}

public class MatLibRegistry {
	public static void register() {
		registerEvents();
		registerCommands();
		//? if <= 1.20.2 {
		/*registerCustomResourcepack();
		*///?}
	}

	public static void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
		ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);
		ServerResourceEvents.RELOAD_START.register(Events::onReloadStart);
		ServerResourceEvents.RELOAD_STOPPING.register(Events::onReloadEnd);
		ServerPlayerEvents.CONNECT.register((connection, player) -> Events.onPlayerJoin(player));
		ServerPlayerEvents.DISCONNECT.register((details, player) -> Events.onPlayerDisconnect(player));
		ServerTickEvents.END_TICK.register(Events::onServerTickEnd);
	}

	public static void registerCommands() {
		ServerCommandEvents.CUSTOM_REGISTER.register(CustomCommand::getAllCommands);
	}

	//? if <= 1.20.2 {
	/*public static void registerCustomResourcepack() {
		ServerResourceEvents.GET_SERVER_PACK.register(originalPack -> {
			String url = Season.RESOURCEPACK_COMBINED_URL;
			String hash = Season.RESOURCEPACK_COMBINED_SHA;
			boolean isRequired = false;
			Component prompt = Component.nullToEmpty("Life Series Resourcepack.");
			return Optional.of(new MinecraftServer().ServerResourcePackInfo(url, hash, isRequired, prompt));
		});
	}
	*///?}
}
