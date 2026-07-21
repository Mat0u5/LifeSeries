package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TriviaTexturePacket(String skinName, byte[] textureData) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("trivia_texture");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(skinName);
        buf.writeByteArray(textureData);
    }

    public static TriviaTexturePacket read(FriendlyByteBuf buf) {
        String skinName = buf.readUtf();
        byte[] textureData = buf.readByteArray();
        return new TriviaTexturePacket(skinName, textureData);
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
*///?} else {
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TriviaTexturePacket(String skinName, byte[] textureData) implements CustomPacketPayload {
    public static final Type<TriviaTexturePacket> ID =
            new Type<>(IdentifierHelper.mod("trivia_texture"));

    public static final StreamCodec<FriendlyByteBuf, TriviaTexturePacket> CODEC =
            StreamCodec.ofMember(TriviaTexturePacket::write, TriviaTexturePacket::new);

    public TriviaTexturePacket(FriendlyByteBuf buf) {
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
//?}