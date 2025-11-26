package net.mat0u5.lifeseries.network.packets;
//? if <= 1.20.3 {
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record TriviaQuestionPayload(String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) implements FabricPacket {

    public static final ResourceLocation ID = IdentifierHelper.mod("triviaquestion");
    public static final PacketType<TriviaQuestionPayload> TYPE = PacketType.create(ID, TriviaQuestionPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(question);
        buf.writeInt(difficulty);
        buf.writeLong(timestamp);
        buf.writeInt(timeToComplete);
        buf.writeInt(answers.size());
        for (String answer : answers) {
            buf.writeUtf(answer);
        }
    }

    public static TriviaQuestionPayload read(FriendlyByteBuf buf) {
        String question = buf.readUtf();
        int difficulty = buf.readInt();
        long timestamp = buf.readLong();
        int timeToComplete = buf.readInt();
        int answersSize = buf.readInt();
        List<String> answers = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            answers.add(buf.readUtf());
        }
        return new TriviaQuestionPayload(question, difficulty, timestamp, timeToComplete, answers);
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

public record TriviaQuestionPayload(String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TriviaQuestionPayload> ID = new CustomPacketPayload.Type<>(IdentifierHelper.mod("triviaquestion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriviaQuestionPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TriviaQuestionPayload::question,
            ByteBufCodecs.INT, TriviaQuestionPayload::difficulty,
            ByteBufCodecs.VAR_LONG, TriviaQuestionPayload::timestamp,
            ByteBufCodecs.INT, TriviaQuestionPayload::timeToComplete,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), TriviaQuestionPayload::answers,
            TriviaQuestionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
*///?}