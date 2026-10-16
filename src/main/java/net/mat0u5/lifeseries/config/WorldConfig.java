package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.config.SimpleConfig;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

public class WorldConfig extends SimpleConfig {
    public WorldConfig(LevelStorageSource.LevelStorageAccess worldAccess) {
        super(LifeSeries.MOD_ID+":world", worldAccess.getLevelPath(LevelResource.ROOT).resolve("lifeseries"), "lifeseries.dat");
        acknowledged();
    }

    public boolean acknowledged() {
        return getOrCreateBoolean("acknowledged", false);
    }
}
