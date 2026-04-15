package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record IntPayload(String name, int number) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("int");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeInt(number);
    }

    public static IntPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int number = buf.readInt();
        return new IntPayload(name, number);
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

public record IntPayload(String name, int number) implements CustomPacketPayload {
    public static final Type<IntPayload> ID = new Type<>(IdentifierHelper.mod("int"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IntPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, IntPayload::name,
            ByteBufCodecs.INT, IntPayload::number,
            IntPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}