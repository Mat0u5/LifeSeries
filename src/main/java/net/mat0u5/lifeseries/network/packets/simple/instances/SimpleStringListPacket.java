package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.StringListPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

import java.util.List;

public class SimpleStringListPacket extends SimplePacket<StringListPayload, List<String>> {

    public SimpleStringListPacket(String name) {
        super(name);
    }

    public StringListPayload generatePayload(List<String> value) {
        if (value == null) return null;
        return new StringListPayload(this.name, value);
    }
}
