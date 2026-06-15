package net.mat0u5.lifeseries.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLEnvironment;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		try {
			//? if <= 1.21.6 {
			/^if (FMLLoader.getLoadingModList() != null) {
				return FMLLoader.getLoadingModList().getModFileById(modId) != null;
			}
			^///?} else {
			if (FMLLoader.getCurrent().getLoadingModList() != null) {
				return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
			}
			//?}
		} catch (Throwable ignored) {}

		try {
			if (ModList.get() != null) {
				return ModList.get().isLoaded(modId);
			}
		} catch (Throwable ignored) {}
		return false;
	}

	@Override
	public ModLoader loader() {
		return ModLoader.NEOFORGE;
	}

	@Override
	public String mcVersion() {
		return "";
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader/^? if > 1.21.7 {^/.getCurrent()/^?}^/.isProduction();
	}

	@Override
	public boolean isClient() {
		//? if <= 1.21.6 {
        /^return FMLEnvironment.dist.isClient();
		^///?} else {
        return FMLEnvironment.getDist().isClient();
		//?}
	}
}
*///?}
