package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record SidetitlePacket(Component text) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("sidetitle");
    public static final PacketType<SidetitlePacket> TYPE = PacketType.create(ID, SidetitlePacket::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(text);
    }

    public static SidetitlePacket read(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        return new SidetitlePacket(text);
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
*///?} else {
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SidetitlePacket(Component text) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SidetitlePacket> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("sidetitle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SidetitlePacket> CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, SidetitlePacket::text, SidetitlePacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}