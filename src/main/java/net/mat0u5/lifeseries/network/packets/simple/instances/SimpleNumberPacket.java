package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.NumberPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleNumberPacket extends SimplePacket<SimpleNumberPacket, NumberPayload> {

    public SimpleNumberPacket(String name) {
        super(name);
    }

    public void sendToServer(double value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(double value) {
        sendPacketToClient(generatePayload(value));
    }

    public NumberPayload generatePayload(double value) {
        return new NumberPayload(this.name, value);
    }
}
