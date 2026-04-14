package net.mat0u5.lifeseries.seasons.season.aprilfools.reallife;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;

public class RealLifeConfig extends ThirdLifeConfig {
    public RealLifeConfig() {
        super("./config/"+ LifeSeries.MOD_ID+"/aprilfools","reallife.properties");
    }
}