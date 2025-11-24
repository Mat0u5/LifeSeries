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

public record ConfigPayload(String configType, String id, int index, String name, String description, List<String> args) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("config");
    public static final PacketType<ConfigPayload> TYPE = PacketType.create(ID, ConfigPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(configType);
        buf.writeUtf(id);
        buf.writeInt(index);
        buf.writeUtf(name);
        buf.writeUtf(description);
        buf.writeInt(args.size());
        for (String arg : args) {
            buf.writeUtf(arg);
        }
    }

    public static ConfigPayload read(FriendlyByteBuf buf) {
        String configType = buf.readUtf();
        String id = buf.readUtf();
        int index = buf.readInt();
        String name = buf.readUtf();
        String description = buf.readUtf();
        int argsSize = buf.readInt();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < argsSize; i++) {
            args.add(buf.readUtf());
        }
        return new ConfigPayload(configType, id, index, name, description, args);
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

public record ConfigPayload(String configType, String id, int index, String name, String description, List<String> args) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ConfigPayload::configType,
            ByteBufCodecs.STRING_UTF8, ConfigPayload::id,
            ByteBufCodecs.INT, ConfigPayload::index,
            ByteBufCodecs.STRING_UTF8, ConfigPayload::name,
            ByteBufCodecs.STRING_UTF8, ConfigPayload::description,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ConfigPayload::args,
            ConfigPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
*///?}