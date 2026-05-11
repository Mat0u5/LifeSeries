package net.mat0u5.lifeseries.platform.neoforge;
//? if neoforge {

/*import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
//? if <= 1.20.3 {
/^import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.function.Function;
^///?} else {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.minecraft.network.codec.StreamCodec;
//?}

//? if <= 1.20.3 {
/^@net.neoforged.fml.common.Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.Mod.EventBusSubscriber.Bus.MOD)
^///?} else if <= 1.21.2 {
/^@EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
^///?} else {
@EventBusSubscriber(modid = LifeSeries.MOD_ID)
//?}
public class NeoForgeNetworkRegistration {
//? if <= 1.20.3 {
/^@SubscribeEvent
public static void registerPackets(RegisterPayloadHandlerEvent event) {
    final IPayloadRegistrar registrar = event.registrar(LifeSeries.MOD_ID)
            .versioned("1")
            .optional();

    NetworkHandlerServer.PAYLOAD_READERS.forEach((id, reader) -> {
        registerPacket(registrar, id, reader);
    });
}

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerPacket(
            IPayloadRegistrar registrar,
            Identifier id,
            Function<FriendlyByteBuf, CustomPacketPayload> reader) {

        // 1.20.4 playBidirectional takes (ID, Reader, Handler)
        registrar.play(
                id,
                (buf) -> (T) reader.apply(buf),
                (payload, context) -> {
                    if (context.flow().isServerbound()) {
                        ServerPacketHandler.handle(payload, context);
                    } else if (LifeSeries.hasClient()) {
                        LifeSeries.clientHelper.handlePacket(payload, context);
                    }
                }
        );
    }
^///?} else {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        for (CustomPacketPayload.TypeAndCodec<? super RegistryFriendlyByteBuf, ? extends CustomPacketPayload> packetInfo : NetworkHandlerServer.PAYLOADS) {
            registerPacketHelper(registrar, packetInfo);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerPacketHelper(
            PayloadRegistrar registrar,
            CustomPacketPayload.TypeAndCodec<?, ?> packetInfo) {

        CustomPacketPayload.Type<T> type = (CustomPacketPayload.Type<T>) packetInfo.type();
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec = (StreamCodec<? super RegistryFriendlyByteBuf, T>) packetInfo.codec();

        //? if <= 1.21.6 {
        /^registrar.playBidirectional(
                type,
                codec,
                (payload, context) -> {
                    if (context.flow().isServerbound()) {
                        ServerPacketHandler.handle(payload, context);
                    }
                    else if (LifeSeries.hasClient()) {
                        LifeSeries.clientHelper.handlePacket(payload, context);
                    }
                }
        );
        ^///?} else {
        registrar.playBidirectional(
                type,
                codec,
                ServerPacketHandler::handle,
                LifeSeries.hasClient() ? LifeSeries.clientHelper::handlePacket : null
        );
        //?}
    }
//?}
}

*///?}