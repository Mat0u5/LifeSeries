package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.StringListPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;

import java.util.List;

public class SimpleStringListPacket extends SimplePacket<SimpleStringListPacket, StringListPayload> {

    public SimpleStringListPacket(String name) {
        super(name);
    }

    public void sendToServer(List<String> value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(List<String> value) {
        sendPacketToClient(generatePayload(value));
    }

    public StringListPayload generatePayload(List<String> value) {
        return new StringListPayload(this.name, value);
    }
}
