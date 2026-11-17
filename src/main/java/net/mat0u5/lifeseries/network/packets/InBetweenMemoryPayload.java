package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record InBetweenMemoryPayload(String hash, int totalSize, int offset, byte[] data) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.lifeseries("in_between_memory");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(hash);
        buf.writeVarInt(totalSize);
        buf.writeVarInt(offset);
        buf.writeByteArray(data);
    }

    public static InBetweenMemoryPayload read(FriendlyByteBuf buf) {
        return new InBetweenMemoryPayload(buf.readUtf(), buf.readVarInt(), buf.readVarInt(), buf.readByteArray());
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

public record InBetweenMemoryPayload(String hash, int totalSize, int offset, byte[] data) implements CustomPacketPayload {
    public static final Type<InBetweenMemoryPayload> ID = new Type<>(IdentifierHelper.lifeseries("in_between_memory"));

    public static final StreamCodec<FriendlyByteBuf, InBetweenMemoryPayload> CODEC =
            StreamCodec.ofMember(InBetweenMemoryPayload::write, InBetweenMemoryPayload::new);

    public InBetweenMemoryPayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readVarInt(), buf.readVarInt(), buf.readByteArray());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(hash);
        buf.writeVarInt(totalSize);
        buf.writeVarInt(offset);
        buf.writeByteArray(data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}
