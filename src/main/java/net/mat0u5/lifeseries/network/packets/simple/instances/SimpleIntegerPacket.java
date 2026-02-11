package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.IntPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleIntegerPacket extends SimplePacket<SimpleIntegerPacket, IntPayload> {

    public SimpleIntegerPacket(String name) {
        super(name);
    }

    public void sendToServer(int value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(int value) {
        sendPacketToClient(generatePayload(value));
    }

    public IntPayload generatePayload(int value) {
        return new IntPayload(this.name, value);
    }
}
