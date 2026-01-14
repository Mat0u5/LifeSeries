package net.mat0u5.lifeseries.network.packets;

//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record VoteScreenPayload(String name, boolean requiresSleep, boolean closesWithEsc, boolean showTimer, List<String> players) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("votescreen");
    public static final PacketType<VoteScreenPayload> TYPE = PacketType.create(ID, VoteScreenPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeBoolean(requiresSleep);
        buf.writeBoolean(closesWithEsc);
        buf.writeBoolean(showTimer);
        buf.writeInt(players.size());
        for (String answer : players) {
            buf.writeUtf(answer);
        }
    }

    public static VoteScreenPayload read(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        boolean requiresSleep = buf.readBoolean();
        boolean closesWithEsc = buf.readBoolean();
        boolean showTimer = buf.readBoolean();
        int playersSize = buf.readInt();
        List<String> players = new ArrayList<>();
        for (int i = 0; i < playersSize; i++) {
            players.add(buf.readUtf());
        }
        return new VoteScreenPayload(name, requiresSleep, closesWithEsc, showTimer, players);
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

import java.util.List;

public record VoteScreenPayload(String name, boolean requiresSleep, boolean closesWithEsc, boolean showTimer, List<String> players) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<VoteScreenPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("votescreen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VoteScreenPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, VoteScreenPayload::name,
            ByteBufCodecs.BOOL, VoteScreenPayload::requiresSleep,
            ByteBufCodecs.BOOL, VoteScreenPayload::closesWithEsc,
            ByteBufCodecs.BOOL, VoteScreenPayload::showTimer,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), VoteScreenPayload::players,
            VoteScreenPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
//?}