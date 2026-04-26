package net.mat0u5.lifeseries.client.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

public class StringObject extends ConfigObject {
    public String stringValue;
    public String defaultValue;
    public String startingValue;
    public StringObject(ConfigPayload payload, String stringValue, String defaultValue) {
        super(payload);
        this.stringValue = stringValue;
        this.defaultValue = defaultValue;
        this.startingValue = stringValue;
    }
}
