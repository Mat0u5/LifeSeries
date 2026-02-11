package net.mat0u5.lifeseries.network.packets.simple;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
*///?} else {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//? if <= 1.20.3 {
/*public abstract class SimplePacket<T extends SimplePacket<T, U>, U extends FabricPacket> {
*///?} else {
public abstract class SimplePacket<T extends SimplePacket<T, U>, U extends CustomPacketPayload> {
//?}
    private List<ServerPlayer> targets = null;
    protected final String name;
    private BiConsumer<ServerPlayer, U> serverReceive = null;
    private Consumer<U> clientReceive = null;

    protected SimplePacket(String name) {
        this.name = name;
        if (SimplePackets.registeredPackets.containsKey(this.name)) {
            Main.LOGGER.error("Simple packet duplicate key: "+this.name);
        }
        SimplePackets.registeredPackets.put(this.name, this);
    }

    public void setClientReceive(Consumer<U> clientReceive) {
        this.clientReceive = clientReceive;
    }

    public void setServerReceive(BiConsumer<ServerPlayer, U> serverReceive) {
        this.serverReceive = serverReceive;
    }

    //? if <= 1.20.3 {
    /*public void receiveClient(FabricPacket payload) {
    *///?} else {
    public void receiveClient(CustomPacketPayload payload) {
    //?}
        if (clientReceive == null) return;
        try {
            U uPayload = (U) payload;
            clientReceive.accept(uPayload);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //? if <= 1.20.3 {
    /*public void receiveServer(ServerPlayer context, FabricPacket payload) {
    *///?} else {
    public void receiveServer(ServerPlayer context, CustomPacketPayload payload) {
    //?}
        if (serverReceive == null) return;
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

    //? if <= 1.20.3 {
    /*protected void sendPacketToServer(FabricPacket packet) {
    *///?} else {
    protected void sendPacketToServer(CustomPacketPayload packet) {
    //?}
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

    //? if <= 1.20.3 {
    /*protected void sendPacketToClient(FabricPacket packet) {
    *///?} else {
    protected void sendPacketToClient(CustomPacketPayload packet) {
    //?}
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