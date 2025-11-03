package net.mat0u5.lifeseries.network.packets;

import net.mat0u5.lifeseries.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record TriviaQuestionPayload(String question, int difficulty, long timestamp, int timeToComplete, List<String> answers) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TriviaQuestionPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "triviaquestion"));
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