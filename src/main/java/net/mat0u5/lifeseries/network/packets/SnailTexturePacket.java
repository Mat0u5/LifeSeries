package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SnailTexturePacket(String skinName, byte[] textureData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SnailTexturePacket> ID =
            new CustomPacketPayload.Type<>(IdentifierHelper.mod("snail_texture"));

    public static final StreamCodec<FriendlyByteBuf, SnailTexturePacket> CODEC =
            StreamCodec.ofMember(SnailTexturePacket::write, SnailTexturePacket::new);

    public SnailTexturePacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readByteArray());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(skinName);
        buf.writeByteArray(textureData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}