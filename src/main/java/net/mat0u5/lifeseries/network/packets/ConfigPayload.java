package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ConfigPayload(String configType, String id, int index, String name, String description, List<String> args) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "config"));
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