package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.core.BlockPos;

import java.util.List;

public class SecretLifeLocationConfig extends ConfigManager {
    public SecretLifeLocationConfig() {
        super("./config/lifeseries/main", "DO_NOT_MODIFY_secretlife_locations.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of();
    }
    @Override
    public void instantiateProperties() {}

    public void loadLocations() {
        TaskManager.successButtonPos = getLocation("successButtonPos");
        TaskManager.rerollButtonPos = getLocation("rerollButtonPos");
        TaskManager.failButtonPos = getLocation("failButtonPos");
        TaskManager.itemSpawnerPos = getLocation("itemSpawnerPos");
    }

    public void saveLocations() {
        setLocation("successButtonPos", TaskManager.successButtonPos);
        setLocation("rerollButtonPos", TaskManager.rerollButtonPos);
        setLocation("failButtonPos", TaskManager.failButtonPos);
        setLocation("itemSpawnerPos", TaskManager.itemSpawnerPos);
    }

    public void deleteLocations() {
        TaskManager.successButtonPos = null;
        TaskManager.rerollButtonPos = null;
        TaskManager.failButtonPos = null;
        TaskManager.itemSpawnerPos = null;
        resetProperties("-- DO NOT MODIFY --");
    }

    public void setLocation(String name, BlockPos pos) {
        String posString = "null";
        if (pos != null) {
            posString = TextUtils.formatString("{}_{}_{}", pos.getX(), pos.getY(), pos.getZ());
        }
        setPropertyCommented(name, posString, "-- DO NOT MODIFY --");
    }

    public BlockPos getLocation(String name) {
        try {
            String location = getProperty(name);
            if (location == null) return null;
            if (!location.contains("_")) return null;
            String[] split = location.split("_");
            if (split.length != 3) return null;
            return new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }catch (Exception e) {
            return null;
        }
    }
}
