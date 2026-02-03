package net.mat0u5.lifeseries.seasons.season.pastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.List;

public class PastLifeConfig extends ConfigManager {
    public PastLifeConfig() {
        super("./config/"+ Main.MOD_ID,"pastlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(BOOGEYMAN_MESSAGE);
        return defaultEntries;
    }

    @Override
    public void instantiateProperties() {
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", ThirdLifeConfig.CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue = 6;
        BOOGEYMAN.defaultValue = true;
        BOOGEYMAN_ADVANCED_DEATHS.defaultValue = true;
        SECRET_SOCIETY.defaultValue = true;
        super.instantiateProperties();
    }
}
