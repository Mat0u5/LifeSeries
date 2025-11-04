package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LongPayload(String name, long number) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LongPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "long"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LongPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LongPayload::name,
            ByteBufCodecs.VAR_LONG, LongPayload::number,
            LongPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}