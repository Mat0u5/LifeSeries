package net.mat0u5.lifeseries.client.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.client.network.NetworkHandlerClient;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientPacketHandler {
    public static <T extends CustomPacketPayload> void handle(T payload, IPayloadContext context) {
        //? if <= 1.20.3 {
        /^context.workHandler().execute(() -> {
            NetworkHandlerClient.onCustomPayload(payload);
        });
        ^///?} else {
        context.enqueueWork(() -> {
            NetworkHandlerClient.onCustomPayload(payload);
        });
        //?}
    }
}

*///?}