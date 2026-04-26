package net.mat0u5.lifeseries.client.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

public class BooleanObject extends ConfigObject {
    public boolean booleanValue;
    public boolean defaultValue;
    public boolean startingValue;
    public BooleanObject(ConfigPayload payload, boolean booleanValue, boolean defaultValue) {
        super(payload);
        this.booleanValue = booleanValue;
        this.defaultValue = defaultValue;
        this.startingValue = booleanValue;
    }
}
