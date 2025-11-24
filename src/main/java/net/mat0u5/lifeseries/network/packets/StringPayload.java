package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record StringPayload(String name, String value) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("string");
    public static final PacketType<StringPayload> TYPE = PacketType.create(ID, StringPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(value);
    }

    public static StringPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        String value = buf.readUtf();
        return new StringPayload(name, value);
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
*///?}