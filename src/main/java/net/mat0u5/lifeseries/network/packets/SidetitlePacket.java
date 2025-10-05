package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record SidetitlePacket(Text text) implements CustomPayload {

    public static final CustomPayload.Id<SidetitlePacket> ID = new CustomPayload.Id<>(Identifier.of(Main.MOD_ID, PacketNames.SIDETITLE.getName()));
    public static final PacketCodec<RegistryByteBuf, SidetitlePacket> CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, SidetitlePacket::text, SidetitlePacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
