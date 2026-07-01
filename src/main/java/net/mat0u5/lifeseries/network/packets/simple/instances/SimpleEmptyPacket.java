package net.mat0u5.lifeseries.network.packets.simple.instances;

import net.mat0u5.lifeseries.network.packets.EmptyPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleEmptyPacket extends SimplePacket<SimpleEmptyPacket, EmptyPayload, Object> {

    public SimpleEmptyPacket(String name) {
        super(name);
    }

    public void sendToServer() {
        this.sendToServer(new Object());
    }

    public void sendToAllClients() {
        this.sendToAllClients(new Object());
    }

    public void sendToClient(ServerPlayer target) {
        this.sendToClient(new Object(), target);
    }

    public void sendToClient(@NotNull List<ServerPlayer> targets) {
        this.sendToClient(new Object(), targets);
    }

    public EmptyPayload generatePayload(Object value) {
        return new EmptyPayload(this.name);
    }
}
