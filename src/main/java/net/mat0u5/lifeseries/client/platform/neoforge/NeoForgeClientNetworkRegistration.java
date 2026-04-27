package net.mat0u5.lifeseries.client.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

//? if <= 1.20.3 {
/*@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = net.neoforged.fml.common.Mod.EventBusSubscriber.Bus.MOD)
 *///?} else if <= 1.21.2 {
/*@EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
 *///?} else {
@EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT)
//?}
public class NeoForgeClientNetworkRegistration {
    @SubscribeEvent
    public static void registerClientHandlers(RegisterClientPayloadHandlersEvent event) {
        for (CustomPacketPayload.TypeAndCodec<?, ?> packetInfo : NetworkHandlerServer.PAYLOADS) {
            registerClientHelper(event, packetInfo);
        }
    }

    // Helper method to cast the generic Type safely
    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerClientHelper(
            RegisterClientPayloadHandlersEvent event,
            CustomPacketPayload.TypeAndCodec<?, ?> packetInfo) {

        CustomPacketPayload.Type<T> type = (CustomPacketPayload.Type<T>) packetInfo.type();

        // Attach the client-side execution logic to the packet type
        event.register(type, ClientPacketHandler::handle);
    }
}

//?}