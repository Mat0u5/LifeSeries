package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

public class NullableIntegerObject extends ConfigObject {
    public Integer integerValue;
    public Integer defaultValue;
    public Integer startingValue;
    public NullableIntegerObject(ConfigPayload payload, Integer integerValue, Integer defaultValue) {
        super(payload);
        this.integerValue = integerValue;
        this.defaultValue = defaultValue;
        this.startingValue = integerValue;
    }
}