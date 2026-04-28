package net.mat0u5.lifeseries.platform.forge;

//? if forge {

import net.mat0u5.lifeseries.platform.Platform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		//? if <= 1.21.11 {
		/*return ModList.get().isLoaded(modId);
		*///?} else {
		return ModList.isLoaded(modId);
		//?}
	}

	@Override
	public ModLoader loader() {
		return ModLoader.FORGE;
	}

	@Override
	public String mcVersion() {
		return "";
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}
}
//?}
