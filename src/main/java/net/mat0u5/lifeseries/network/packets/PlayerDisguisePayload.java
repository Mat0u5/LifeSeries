package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerDisguisePayload(String name, String hiddenUUID, String hiddenName, String shownUUID, String shownName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerDisguisePayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, PacketNames.PLAYER_DISGUISE.getName()));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDisguisePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerDisguisePayload::name,
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