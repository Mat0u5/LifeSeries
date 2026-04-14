package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;

public class SimpleLifeConfig extends ThirdLifeConfig {
    public SimpleLifeConfig() {
        super("./config/"+ LifeSeries.MOD_ID+"/aprilfools","simplelife.properties");
    }
}
