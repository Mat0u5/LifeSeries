package net.mat0u5.lifeseries.entity.triviabot;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

//? if <= 1.21 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
public class TriviaBotRenderer extends MobRenderer<TriviaBot, TriviaBotModel<TriviaBot>> {
    public TriviaBotRenderer(EntityRendererProvider.Context context) {
        super(context, new TriviaBotModel<>(context.bakeLayer(TriviaBotModel.TRIVIA_BOT)), 0.45f);
    }

    @Override
    public ResourceLocation getTextureLocation(TriviaBot entity) {
        if (entity.santaBot()) {
            return TriviaBot.SANTABOT_TEXTURE;
        }
        return TriviaBot.DEFAULT_TEXTURE;
    }

    @Override
    public void render(TriviaBot entity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.renderer.entity.AgeableMobRenderer;

public class TriviaBotRenderer extends AgeableMobRenderer<TriviaBot, TriviaBotRenderState, TriviaBotModel> {
    public TriviaBotRenderer(EntityRendererProvider.Context context) {
        super(context, new TriviaBotModel(context.bakeLayer(TriviaBotModel.TRIVIA_BOT)), new TriviaBotModel(context.bakeLayer(TriviaBotModel.TRIVIA_BOT)), 0.45f);
    }

    @Override
    public TriviaBotRenderState createRenderState() {
        return new TriviaBotRenderState();
    }

    @Override
    //? if <= 1.21.9 {
    public ResourceLocation getTextureLocation(TriviaBotRenderState state) {
    //?} else {
    /^public Identifier getTextureLocation(TriviaBotRenderState state) {
    ^///?}
        if (state.santaBot) {
            return TriviaBot.SANTABOT_TEXTURE;
        }
        return TriviaBot.DEFAULT_TEXTURE;
    }

    @Override
    public void extractRenderState(TriviaBot triviaBot, TriviaBotRenderState state, float f) {
        super.extractRenderState(triviaBot, state, f);

        state.glideAnimationState.copyFrom(triviaBot.clientData.glideAnimationState);
        state.idleAnimationState.copyFrom(triviaBot.clientData.idleAnimationState);
        state.walkAnimationState.copyFrom(triviaBot.clientData.walkAnimationState);
        state.countdownAnimationState.copyFrom(triviaBot.clientData.countdownAnimationState);
        state.analyzingAnimationState.copyFrom(triviaBot.clientData.analyzingAnimationState);
        state.answerCorrectAnimationState.copyFrom(triviaBot.clientData.answerCorrectAnimationState);
        state.answerIncorrectAnimationState.copyFrom(triviaBot.clientData.answerIncorrectAnimationState);
        state.snailTransformAnimationState.copyFrom(triviaBot.clientData.snailTransformAnimationState);

        state.santaAnalyzingAnimationState.copyFrom(triviaBot.clientData.santaAnalyzingAnimationState);
        state.santaAnswerCorrectAnimationState.copyFrom(triviaBot.clientData.santaAnswerCorrectAnimationState);
        state.santaAnswerIncorrectAnimationState.copyFrom(triviaBot.clientData.santaAnswerIncorrectAnimationState);
        state.santaFlyAnimationState.copyFrom(triviaBot.clientData.santaFlyAnimationState);
        state.santaGlideAnimationState.copyFrom(triviaBot.clientData.santaGlideAnimationState);
        state.santaIdleAnimationState.copyFrom(triviaBot.clientData.santaIdleAnimationState);
        state.santaWaveAnimationState.copyFrom(triviaBot.clientData.santaWaveAnimationState);

        state.faceAngryAnimationState.copyFrom(triviaBot.clientData.faceAngryAnimationState);
        state.faceHappyAnimationState.copyFrom(triviaBot.clientData.faceHappyAnimationState);

        state.santaBot = triviaBot.santaBot();
    }
}
*///?}
