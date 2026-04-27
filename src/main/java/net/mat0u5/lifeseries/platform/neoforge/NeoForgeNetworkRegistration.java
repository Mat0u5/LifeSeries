package net.mat0u5.lifeseries.platform.neoforge;
//? neoforge {

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

//? if <= 1.20.3 {
/*import net.minecraft.resources.Identifier;
 *///?} else {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
//?}

//? if <= 1.20.3 {
/*@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.Mod.EventBusSubscriber.Bus.MOD)
*///?} else if <= 1.21.2 {
/*@EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
*///?} else {
@EventBusSubscriber(modid = LifeSeries.MOD_ID)
//?}
public class NeoForgeNetworkRegistration {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        // Iterate through your list of payloads
        for (CustomPacketPayload.TypeAndCodec<? super RegistryFriendlyByteBuf, ? extends CustomPacketPayload> packetInfo : NetworkHandlerServer.PAYLOADS) {
            registerPacketHelper(registrar, packetInfo);
        }
    }

    // This helper method bypasses the "capture of ?" Java compiler error
    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerPacketHelper(
            PayloadRegistrar registrar,
            CustomPacketPayload.TypeAndCodec<?, ?> packetInfo) {

        CustomPacketPayload.Type<T> type = (CustomPacketPayload.Type<T>) packetInfo.type();
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec = (StreamCodec<? super RegistryFriendlyByteBuf, T>) packetInfo.codec();

        // Register the codec to the network, and attach ONLY the server handler
        registrar.playBidirectional(
                type,
                codec,
                ServerPacketHandler::handle,
                null // The client handler MUST be null here to prevent server crashes
        );
    }
}

//?}