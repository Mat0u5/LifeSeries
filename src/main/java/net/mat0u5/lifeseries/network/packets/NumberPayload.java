package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NumberPayload(String name, double number) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<NumberPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("number"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NumberPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, NumberPayload::name,
            ByteBufCodecs.DOUBLE, NumberPayload::number,
            NumberPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}