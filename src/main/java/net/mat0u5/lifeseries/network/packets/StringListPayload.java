package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record StringListPayload(String name, List<String> value) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("stringlist");

    @Override
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
//?}