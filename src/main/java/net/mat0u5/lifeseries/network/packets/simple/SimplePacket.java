package net.mat0u5.lifeseries.network.packets.simple;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SimplePacket<U extends CustomPacketPayload, V> {
    protected final String name;
    private BiConsumer<ServerPlayer, U> serverReceive = null;
    private Consumer<U> clientReceive = null;

    protected SimplePacket(String name) {
        this.name = name;
        if (SimplePackets.registeredPackets.containsKey(this.name)) {
            LifeSeries.LOGGER.error("Simple packet duplicate key: "+this.name);
        }
        SimplePackets.registeredPackets.put(this.name, this);
    }

    protected abstract U generatePayload(V value);

    /**
     * Receiving
     */

    public void setClientReceive(Consumer<U> clientReceive) {
        this.clientReceive = clientReceive;
    }

    public void setServerReceive(BiConsumer<ServerPlayer, U> serverReceive) {
        this.serverReceive = serverReceive;
    }

    public void receiveClient(CustomPacketPayload payload) {
        if (clientReceive == null) return;
        try {
            U uPayload = (U) payload;
            clientReceive.accept(uPayload);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveServer(ServerPlayer context, CustomPacketPayload payload) {
        if (serverReceive == null) return;
        try {
            U uPayload = (U) payload;
            serverReceive.accept(context, uPayload);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Client -> Server
     */
    public void sendToServer(V value) {
        sendPacketToServer(generatePayload(value));
    }

    private void sendPacketToServer(CustomPacketPayload packet) {
        if (packet == null) {
            LifeSeries.LOGGER.error("Packet was not initialized correctly.");
            return;
        }

        if (LifeSeries.clientHelper != null) {
            LifeSeries.clientHelper.sendPacket(packet);
        }
    }


    /**
     * Server -> Client
     */
    public void sendToAllClients(V value) {
        sendPacketToClient(generatePayload(value), PlayerUtils.getAllPlayers());
    }

    public void sendToClient(V value, ServerPlayer target) {
        if (target == null) return;
        sendPacketToClient(generatePayload(value), List.of(target));
    }

    public void sendToClient(V value, @NotNull List<ServerPlayer> targets) {
        sendPacketToClient(generatePayload(value), targets);
    }

    private void sendPacketToClient(CustomPacketPayload packet, @NotNull List<ServerPlayer> targets) {
        if (packet == null) {
            LifeSeries.LOGGER.error("Packet was not initialized correctly.");
            return;
        }

        if (targets == null) return;

        for (ServerPlayer player : targets) {
            if (player == null) continue;
            NetworkHandlerServer.sendPacket(player, packet);
        }
    }
}