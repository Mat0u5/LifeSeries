package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HandshakePayload(String modVersionStr, int modVersion, String compatibilityStr, int compatibility) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("handshake");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modVersionStr);
        buf.writeInt(modVersion);
        buf.writeUtf(compatibilityStr);
        buf.writeInt(compatibility);
    }

    public static HandshakePayload read(FriendlyByteBuf buf) {
        String modVersionStr = buf.readUtf();
        int modVersion = buf.readInt();
        String compatibilityStr = buf.readUtf();
        int compatibility = buf.readInt();
        return new HandshakePayload(modVersionStr, modVersion, compatibilityStr, compatibility);
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
*///?} else {
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HandshakePayload(String modVersionStr, int modVersion, String compatibilityStr, int compatibility) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HandshakePayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod( "handshake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HandshakePayload::modVersionStr,
            ByteBufCodecs.INT, HandshakePayload::modVersion,
            ByteBufCodecs.STRING_UTF8, HandshakePayload::compatibilityStr,
            ByteBufCodecs.INT, HandshakePayload::compatibility,
            HandshakePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}