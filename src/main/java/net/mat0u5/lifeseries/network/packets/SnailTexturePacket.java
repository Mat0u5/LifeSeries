package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SnailTexturePacket(String skinName, byte[] textureData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SnailTexturePacket> ID =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "snail_texture"));

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