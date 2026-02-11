package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.EmptyPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleEmptyPacket extends SimplePacket<SimpleEmptyPacket, EmptyPayload> {
    public SimpleEmptyPacket(String name, BiConsumer<ServerPlayer, EmptyPayload> serverReceive, Consumer<EmptyPayload> clientReceive) {
        super(name, serverReceive, clientReceive);
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
