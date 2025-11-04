package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StringPayload(String name, String value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StringPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "string"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StringPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StringPayload::name,
            ByteBufCodecs.STRING_UTF8, StringPayload::value,
            StringPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}