package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SidetitlePacket(Component text) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SidetitlePacket> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod(PacketNames.SIDETITLE.getName()));
    public static final StreamCodec<RegistryFriendlyByteBuf, SidetitlePacket> CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, SidetitlePacket::text, SidetitlePacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
