package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SnailTexturePacket(String skinName, byte[] textureData) implements CustomPayload {
    public static final CustomPayload.Id<SnailTexturePacket> ID =
            new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, "snail_texture"));

    public static final PacketCodec<PacketByteBuf, SnailTexturePacket> CODEC =
            PacketCodec.of(SnailTexturePacket::write, SnailTexturePacket::new);

    public SnailTexturePacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readByteArray());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(skinName);
        buf.writeByteArray(textureData);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}