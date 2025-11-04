package net.mat0u5.lifeseries.network.packets;


import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record StringListPayload(String name, List<String> value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StringListPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("stringlist"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StringListPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StringListPayload::name,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), StringListPayload::value,
            StringListPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}