package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.EmptyPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

public class SimpleEmptyPacket extends SimplePacket<SimpleEmptyPacket, EmptyPayload> {

    public SimpleEmptyPacket(String name) {
        super(name);
    }

    public void sendToServer() {
        sendPacketToServer(generatePayload());
    }

    public void sendToClient() {
        sendPacketToClient(generatePayload());
    }

    public EmptyPayload generatePayload() {
        return new EmptyPayload(this.name);
    }
}
