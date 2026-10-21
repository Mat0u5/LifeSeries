package net.mat0u5.lifeseries.seasons.season.secretlife;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.config.SimpleConfig;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.minecraft.core.BlockPos;

public class SecretLifeLocationConfig extends SimpleConfig {
    public SecretLifeLocationConfig() {
        super(LifeSeries.MOD_ID+":secretlife_locations", "lifeseries/main", "DO_NOT_MODIFY_secretlife_locations.properties");
    }

    public void loadLocations() {
        SecretKeeper.successButtonPos = getLocation("successButtonPos");
        SecretKeeper.rerollButtonPos = getLocation("rerollButtonPos");
        SecretKeeper.failButtonPos = getLocation("failButtonPos");
        SecretKeeper.itemSpawnerPos = getLocation("itemSpawnerPos");
    }

    public void saveLocations() {
        setLocation("successButtonPos", SecretKeeper.successButtonPos);
        setLocation("rerollButtonPos", SecretKeeper.rerollButtonPos);
        setLocation("failButtonPos", SecretKeeper.failButtonPos);
        setLocation("itemSpawnerPos", SecretKeeper.itemSpawnerPos);
    }

    public void deleteLocations() {
        SecretKeeper.successButtonPos = null;
        SecretKeeper.rerollButtonPos = null;
        SecretKeeper.failButtonPos = null;
        SecretKeeper.itemSpawnerPos = null;
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
