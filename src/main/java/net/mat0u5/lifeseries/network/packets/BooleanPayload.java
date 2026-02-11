package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record BooleanPayload(String name, boolean value) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("boolean");
    public static final PacketType<BooleanPayload> TYPE = PacketType.create(ID, BooleanPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeBoolean(value);
    }

    public static BooleanPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        boolean value = buf.readBoolean();
        return new BooleanPayload(name, value);
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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BooleanPayload(String name, boolean value) implements CustomPacketPayload {

    public static final Type<BooleanPayload> ID = new Type<>(IdentifierHelper.mod("boolean"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BooleanPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BooleanPayload::name,
            ByteBufCodecs.BOOL, BooleanPayload::value,
            BooleanPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}