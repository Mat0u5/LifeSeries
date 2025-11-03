package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HandshakePayload(String modVersionStr, int modVersion, String compatibilityStr, int compatibility) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HandshakePayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "handshake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HandshakePayload::modVersionStr,
            ByteBufCodecs.INT, HandshakePayload::modVersion,
            ByteBufCodecs.STRING_UTF8, HandshakePayload::compatibilityStr,
            ByteBufCodecs.INT, HandshakePayload::compatibility,
            HandshakePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}