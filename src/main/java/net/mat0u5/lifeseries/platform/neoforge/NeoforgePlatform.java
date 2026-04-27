package net.mat0u5.lifeseries.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.platform.Platform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		if (ModList.get() == null) return false;
		return ModList.get().isLoaded(modId);
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
}
*///?}
