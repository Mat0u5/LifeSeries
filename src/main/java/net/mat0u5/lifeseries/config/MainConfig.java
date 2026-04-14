package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;

import java.util.List;

public class MainConfig extends ConfigManager {
    public MainConfig() {
        super("./config/lifeseries/main", LifeSeries.MOD_ID+".properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of();
    }
    @Override
    public void instantiateProperties() {
        getOrCreateProperty("currentSeries", LifeSeries.DEFAULT_SEASON.getId());
    }
}
