package net.mat0u5.lifeseries.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerPacketHandler {
    public static <T extends CustomPacketPayload> void handle(T payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            NetworkHandlerServer.onCustomPayload(payload, context.player());
        });
    }
}

//?}