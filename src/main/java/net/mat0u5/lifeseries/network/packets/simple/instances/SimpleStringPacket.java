package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.StringPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleStringPacket extends SimplePacket<SimpleStringPacket, StringPayload> {
    public SimpleStringPacket(String name, BiConsumer<ServerPlayer, StringPayload> serverReceive, Consumer<StringPayload> clientReceive) {
        super(name, serverReceive, clientReceive);
    }

    public void sendToServer(String value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(String value) {
        sendPacketToClient(generatePayload(value));
    }

    public StringPayload generatePayload(String value) {
        return new StringPayload(this.name, value);
    }
}
