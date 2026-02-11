package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.LongPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleLongPacket extends SimplePacket<SimpleLongPacket, LongPayload> {

    public SimpleLongPacket(String name) {
        super(name);
    }

    public void sendToServer(long value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(long value) {
        sendPacketToClient(generatePayload(value));
    }

    public LongPayload generatePayload(long value) {
        return new LongPayload(this.name, value);
    }
}
