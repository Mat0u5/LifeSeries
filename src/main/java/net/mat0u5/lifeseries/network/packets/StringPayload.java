package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StringPayload(String name, String value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StringPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("string"));
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