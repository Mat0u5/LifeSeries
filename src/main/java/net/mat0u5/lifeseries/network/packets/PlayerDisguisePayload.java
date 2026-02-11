package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PlayerDisguisePayload(String hiddenUUID, String hiddenName, String shownUUID, String shownName) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("player_disguise");
    public static final PacketType<PlayerDisguisePayload> TYPE = PacketType.create(ID, PlayerDisguisePayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(hiddenUUID);
        buf.writeUtf(hiddenName);
        buf.writeUtf(shownUUID);
        buf.writeUtf(shownName);
    }

    public static PlayerDisguisePayload read(FriendlyByteBuf buf) {
        String hiddenUUID = buf.readUtf();
        String hiddenName = buf.readUtf();
        String shownUUID = buf.readUtf();
        String shownName = buf.readUtf();
        return new PlayerDisguisePayload(hiddenUUID, hiddenName, shownUUID, shownName);
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

public record PlayerDisguisePayload(String hiddenUUID, String hiddenName, String shownUUID, String shownName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerDisguisePayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("player_disguise"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDisguisePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerDisguisePayload::hiddenUUID,
            ByteBufCodecs.STRING_UTF8, PlayerDisguisePayload::hiddenName,
            ByteBufCodecs.STRING_UTF8, PlayerDisguisePayload::shownUUID,
            ByteBufCodecs.STRING_UTF8, PlayerDisguisePayload::shownName,
            PlayerDisguisePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}