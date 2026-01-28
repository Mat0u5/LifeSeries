package net.mat0u5.lifeseries.config;

import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.List;

public class WorldConfig extends ConfigManager {
    public WorldConfig(LevelStorageSource.LevelStorageAccess worldAccess) {
        super(worldAccess.getLevelPath(LevelResource.ROOT).resolve("lifeseries").toString(), "lifeseries.dat");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of();
    }
    @Override
    public void instantiateProperties() {
        acknowledged();
    }
    public boolean acknowledged() {
        return getOrCreateBoolean("acknowledged", false);
    }
}