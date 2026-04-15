package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EmptyPayload(String name) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("empty");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
    }

    public static EmptyPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        return new EmptyPayload(name);
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

public record EmptyPayload(String name) implements CustomPacketPayload {

    public static final Type<EmptyPayload> ID = new Type<>(IdentifierHelper.mod("empty"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EmptyPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EmptyPayload::name,
            EmptyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}