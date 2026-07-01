package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.BooleanPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleBooleanPacket extends SimplePacket<BooleanPayload, Boolean> {

    public SimpleBooleanPacket(String name) {
        super(name);
    }

    @Override
    public BooleanPayload generatePayload(Boolean value) {
        if (value == null) return null;
        return new BooleanPayload(this.name, value);
    }
}
