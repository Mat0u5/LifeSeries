package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
/*import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record LifeSkinsTexturePayload(String skinName, String teamName, boolean slim, byte[] textureData) implements CustomPacketPayload {

    public static final Identifier ID = IdentifierHelper.mod("lifeskins_texture");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(skinName);
        buf.writeUtf(teamName);
        buf.writeBoolean(slim);
        buf.writeByteArray(textureData);
    }

    public static LifeSkinsTexturePayload read(FriendlyByteBuf buf) {
        String skinName = buf.readUtf();
        String teamName = buf.readUtf();
        boolean slim = buf.readBoolean();
        byte[] textureData = buf.readByteArray();
        return new LifeSkinsTexturePayload(skinName, teamName, slim, textureData);
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

public record LifeSkinsTexturePayload(String skinName, String teamName, boolean slim, byte[] textureData) implements CustomPacketPayload {
    public static final Type<LifeSkinsTexturePayload> ID =
            new Type<>(IdentifierHelper.mod("lifeskins_texture"));

    public static final StreamCodec<FriendlyByteBuf, LifeSkinsTexturePayload> CODEC =
            StreamCodec.ofMember(LifeSkinsTexturePayload::write, LifeSkinsTexturePayload::new);

    public LifeSkinsTexturePayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readByteArray());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(skinName);
        buf.writeUtf(teamName);
        buf.writeBoolean(slim);
        buf.writeByteArray(textureData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}