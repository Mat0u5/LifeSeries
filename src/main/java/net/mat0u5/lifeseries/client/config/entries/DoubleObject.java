package net.mat0u5.lifeseries.client.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

public class DoubleObject extends ConfigObject {
    public double doubleValue;
    public double defaultValue;
    public double startingValue;
    public DoubleObject(ConfigPayload payload, double doubleValue, double defaultValue) {
        super(payload);
        this.doubleValue = doubleValue;
        this.defaultValue = defaultValue;
        this.startingValue = doubleValue;
    }
}
