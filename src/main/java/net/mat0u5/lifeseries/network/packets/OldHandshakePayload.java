package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OldHandshakePayload(String modVersionStr, int modVersion, String compatibilityStr, int compatibility) implements CustomPacketPayload {
    public static final String OLD_PACKET_VERSION_BREAKOFF = "1.5.10";
    public static final Identifier ID = IdentifierHelper.lifeseries("handshake");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modVersionStr);
        buf.writeInt(modVersion);
        buf.writeUtf(compatibilityStr);
        buf.writeInt(compatibility);
    }

    public static OldHandshakePayload read(FriendlyByteBuf buf) {
        String modVersionStr = buf.readUtf();
        int modVersion = buf.readInt();
        String compatibilityStr = buf.readUtf();
        int compatibility = buf.readInt();
        return new OldHandshakePayload(modVersionStr, modVersion, compatibilityStr, compatibility);
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

public record OldHandshakePayload(String modVersionStr, int modVersion, String compatibilityStr, int compatibility) implements CustomPacketPayload {
    public static final String OLD_PACKET_VERSION_BREAKOFF = "1.5.10";
    public static final CustomPacketPayload.Type<OldHandshakePayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.lifeseries( "handshake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OldHandshakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OldHandshakePayload::modVersionStr,
            ByteBufCodecs.INT, OldHandshakePayload::modVersion,
            ByteBufCodecs.STRING_UTF8, OldHandshakePayload::compatibilityStr,
            ByteBufCodecs.INT, OldHandshakePayload::compatibility,
            OldHandshakePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}