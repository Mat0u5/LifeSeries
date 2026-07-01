package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.NumberPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleNumberPacket extends SimplePacket<NumberPayload, Double> {

    public SimpleNumberPacket(String name) {
        super(name);
    }

    public NumberPayload generatePayload(Double value) {
        if (value == null) return null;
        return new NumberPayload(this.name, value);
    }
}
