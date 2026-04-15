package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SidetitlePacket(Component text) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("sidetitle");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(text);
    }

    public static SidetitlePacket read(FriendlyByteBuf buf) {
        Component text = buf.readComponent();
        return new SidetitlePacket(text);
    }

    @Override
    public Identifier id() {
        return ID;
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