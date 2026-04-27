package net.mat0u5.lifeseries.client.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
//? if <= 1.21.6 {
/*import net.neoforged.neoforge.network.handling.IPayloadContext;
*///?} else {
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}

//? if <= 1.20.3 {
/*@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = net.neoforged.fml.common.Mod.EventBusSubscriber.Bus.MOD)
*///?} else if <= 1.21.2 {
/*@EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
*///?} else if > 1.21.6 {
@EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT)
//?}
public class NeoForgeClientNetworkRegistration {
    //? if <= 1.21.6 {
    /*public static <T extends CustomPacketPayload> void handleClientPacket(T payload, IPayloadContext context) {
        ClientPacketHandler.handle(payload, context);
    }
    *///?} else {
    @SubscribeEvent
    public static void registerClientHandlers(RegisterClientPayloadHandlersEvent event) {
        for (CustomPacketPayload.TypeAndCodec<?, ?> packetInfo : NetworkHandlerServer.PAYLOADS) {
            registerClientHelper(event, packetInfo);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerClientHelper(
            RegisterClientPayloadHandlersEvent event,
            CustomPacketPayload.TypeAndCodec<?, ?> packetInfo) {

        CustomPacketPayload.Type<T> type = (CustomPacketPayload.Type<T>) packetInfo.type();

        event.register(type, ClientPacketHandler::handle);
    }
    //?}
}

//?}