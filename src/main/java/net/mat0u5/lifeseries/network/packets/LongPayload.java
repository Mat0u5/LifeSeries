package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record LongPayload(String name, long number) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("long");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeLong(number);
    }

    public static LongPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        long number = buf.readLong();
        return new LongPayload(name, number);
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

public record LongPayload(String name, long number) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LongPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("long"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LongPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LongPayload::name,
            ByteBufCodecs.VAR_LONG, LongPayload::number,
            LongPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}