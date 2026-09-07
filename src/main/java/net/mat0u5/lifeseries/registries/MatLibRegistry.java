package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.events.Events;

public class MatLibRegistry {
	public static void register() {
		Events.registerAllEvents();
	}
}
