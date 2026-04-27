package net.mat0u5.lifeseries.client.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.client.network.NetworkHandlerClient;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientPacketHandler {
    public static <T extends CustomPacketPayload> void handle(T payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            NetworkHandlerClient.onCustomPayload(payload);
        });
    }
}

//?}