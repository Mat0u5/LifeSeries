package net.mat0u5.lifeseries.network.packets.simple;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SimplePacket<T extends SimplePacket<T, U>, U extends CustomPacketPayload> {
    private List<ServerPlayer> targets = null;
    protected final String name;
    private final BiConsumer<ServerPlayer, U> serverReceive;
    private final Consumer<U> clientReceive;

    protected SimplePacket(String name, BiConsumer<ServerPlayer, U> serverReceive, Consumer<U> clientReceive) {
        this.name = name;
        this.serverReceive = serverReceive;
        this.clientReceive = clientReceive;
        SimplePackets.registeredPackets.put(this.name, this);
    }

    public void receiveClient(CustomPacketPayload payload) {
        try {
            U uPayload = (U) payload;
            clientReceive.accept(uPayload);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void receiveServer(ServerPlayer context, CustomPacketPayload payload) {
        try {
            U uPayload = (U) payload;
            serverReceive.accept(context, uPayload);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public T target(ServerPlayer player) {
        targets = List.of(player);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T target(List<ServerPlayer> players) {
        targets = players;
        return (T) this;
    }

    protected void sendPacketToServer(CustomPacketPayload packet) {
        if (packet == null) {
            Main.LOGGER.error("Packet was not initialized correctly.");
            targets = null;
            return;
        }

        if (Main.hasClient() && Main.clientHelper != null) {
            Main.clientHelper.sendPacket(packet);
        }
        targets = null;
    }

    protected void sendPacketToClient(CustomPacketPayload packet) {
        if (packet == null) {
            Main.LOGGER.error("Packet was not initialized correctly.");
            targets = null;
            return;
        }

        if (targets == null) {
            targets = PlayerUtils.getAllPlayers();
        }
        for (ServerPlayer player : targets) {
            if (player == null) continue;
            ServerPlayNetworking.send(player, packet);
        }
        targets = null;
    }
}