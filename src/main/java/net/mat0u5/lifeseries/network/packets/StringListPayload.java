package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record StringListPayload(String name, List<String> value) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("stringlist");
    public static final PacketType<StringListPayload> TYPE = PacketType.create(ID, StringListPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeInt(value.size());
        for (String str : value) {
            buf.writeUtf(str);
        }
    }

    public static StringListPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int size = buf.readInt();
        List<String> value = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            value.add(buf.readUtf());
        }
        return new StringListPayload(name, value);
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
//?} else {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record StringListPayload(String name, List<String> value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StringListPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("stringlist"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StringListPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StringListPayload::name,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), StringListPayload::value,
            StringListPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
*///?}