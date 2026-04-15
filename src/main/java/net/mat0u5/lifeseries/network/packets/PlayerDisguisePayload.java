package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PlayerDisguisePayload(String hiddenUUID, String hiddenName, String shownUUID, String shownName) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("player_disguise");

    @Override
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