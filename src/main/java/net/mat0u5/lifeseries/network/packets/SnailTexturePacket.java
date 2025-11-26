package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SnailTexturePacket(String skinName, byte[] textureData) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("snail_texture");
    public static final PacketType<SnailTexturePacket> TYPE = PacketType.create(ID, SnailTexturePacket::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(skinName);
        buf.writeByteArray(textureData);
    }

    public static SnailTexturePacket read(FriendlyByteBuf buf) {
        String skinName = buf.readUtf();
        byte[] textureData = buf.readByteArray();
        return new SnailTexturePacket(skinName, textureData);
    }

    public FriendlyByteBuf toFriendlyByteBuf() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        write(buf);
        return buf;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
//?} else {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
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
*///?}