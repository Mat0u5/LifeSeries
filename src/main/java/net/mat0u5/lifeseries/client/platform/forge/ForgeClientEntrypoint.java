package net.mat0u5.lifeseries.client.platform.forge;

//? if forge {

import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.LifeSeries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//? if <= 1.21.5 {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
 *///?} else {
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
//?}

@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEntrypoint {

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		LifeSeriesClient.onInitializeClient();
	}
}
//?}
