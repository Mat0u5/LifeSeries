package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.StringPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleStringPacket extends SimplePacket<StringPayload, String> {

    public SimpleStringPacket(String name) {
        super(name);
    }

    public StringPayload generatePayload(String value) {
        if (value == null) return null;
        return new StringPayload(this.name, value);
    }
}
