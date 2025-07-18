package net.mat0u5.lifeseries.config.entries;

import net.mat0u5.lifeseries.network.packets.ConfigPayload;

import java.util.List;

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

    public void updateValue(String newValue) {
        stringValue = newValue;
        if (!modified && !stringValue.equalsIgnoreCase(startingValue)) {
            modified = true;
        }
    }

    @Override
    public List<String> getArgs() {
        return List.of(stringValue);
    }
}
