package net.mat0u5.lifeseries.platform.forge;

//? if forge {

/*import net.mat0u5.lifeseries.platform.Platform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.LoadingModList;

public class ForgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		try {
			//? if <= 1.21.11 {
			/^if (LoadingModList.get() != null) {
				return LoadingModList.get().getModFileById(modId) != null;
			}
			^///?} else {
			return LoadingModList.getModFileById(modId) != null;
			//?}
		} catch (Throwable ignored) {}

		try {
			//? if <= 1.21.11 {
			/^if (ModList.get() != null) {
				return ModList.get().isLoaded(modId);
			}
			^///?} else {
			return ModList.isLoaded(modId);
			//?}
		} catch (Throwable ignored) {}
		return false;
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

	@Override
	public boolean isClient() {
		return FMLEnvironment.dist.isClient();
	}
}
*///?}
