package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.BooleanPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleBooleanPacket extends SimplePacket<SimpleBooleanPacket, BooleanPayload> {
    public SimpleBooleanPacket(String name, BiConsumer<ServerPlayer, BooleanPayload> serverReceive, Consumer<BooleanPayload> clientReceive) {
        super(name, serverReceive, clientReceive);
    }

    public void sendToServer(boolean value) {
        sendPacketToServer(generatePayload(value));
    }

    public void sendToClient(boolean value) {
        sendPacketToClient(generatePayload(value));
    }

    public BooleanPayload generatePayload(boolean value) {
        return new BooleanPayload(this.name, value);
    }
}
