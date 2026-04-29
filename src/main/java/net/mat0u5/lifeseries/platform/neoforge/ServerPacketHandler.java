package net.mat0u5.lifeseries.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerPacketHandler {
    public static <T extends CustomPacketPayload> void handle(T payload, IPayloadContext context) {
        //? if <= 1.20.3 {
        /^context.workHandler().execute(() -> {
            NetworkHandlerServer.onCustomPayload(payload, context.player().orElse(null));
        });
        ^///?} else {
        context.enqueueWork(() -> {
            NetworkHandlerServer.onCustomPayload(payload, context.player());
        });
        //?}
    }
}

*///?}