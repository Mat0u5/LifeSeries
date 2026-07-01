package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.IntPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleIntegerPacket extends SimplePacket<SimpleIntegerPacket, IntPayload, Integer> {

    public SimpleIntegerPacket(String name) {
        super(name);
    }

    public IntPayload generatePayload(Integer value) {
        if (value == null) return null;
        return new IntPayload(this.name, value);
    }
}
