package net.mat0u5.lifeseries.platform.fabric;

//? if fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.mat0u5.lifeseries.LifeSeries;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		LifeSeries.onInitialize();
	}
}
//?}
