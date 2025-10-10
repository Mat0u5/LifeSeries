package net.mat0u5.lifeseries.entity.triviabot;

import net.mat0u5.lifeseries.Main;


//? if <= 1.21 {

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.MobEntityRenderer;
public class TriviaBotRenderer extends MobEntityRenderer<TriviaBot, TriviaBotModel<TriviaBot>> {
    public TriviaBotRenderer(EntityRendererFactory.Context context) {
        super(context, new TriviaBotModel<>(context.getPart(TriviaBotModel.TRIVIA_BOT)), 0.5f);
    }

    @Override
    public Identifier getTexture(TriviaBot entity) {
        return Identifier.of(Main.MOD_ID, "textures/entity/triviabot/triviabot.png");
    }

    @Override
    public void render(TriviaBot entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.util.Identifier;

public class TriviaBotRenderer extends AgeableMobEntityRenderer<TriviaBot, TriviaBotRenderState, TriviaBotModel> {
    public TriviaBotRenderer(EntityRendererFactory.Context context) {
        super(context, new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), 0.5f);
    }

    @Override
    public TriviaBotRenderState createRenderState() {
        return new TriviaBotRenderState();
    }

    @Override
    public Identifier getTexture(TriviaBotRenderState state) {
        return Identifier.of(Main.MOD_ID, "textures/entity/triviaBot/triviabot.png");
    }

    public void updateRenderState(TriviaBot triviaBot, TriviaBotRenderState state, float f) {
        super.updateRenderState(triviaBot, state, f);

        state.glideAnimationState.copyFrom(triviaBot.glideAnimationState);
        state.idleAnimationState.copyFrom(triviaBot.idleAnimationState);
        state.walkAnimationState.copyFrom(triviaBot.walkAnimationState);
        state.countdownAnimationState.copyFrom(triviaBot.countdownAnimationState);
        state.analyzingAnimationState.copyFrom(triviaBot.analyzingAnimationState);
        state.answerCorrectAnimationState.copyFrom(triviaBot.answerCorrectAnimationState);
        state.answerIncorrectAnimationState.copyFrom(triviaBot.answerIncorrectAnimationState);
        state.snailTransformAnimationState.copyFrom(triviaBot.snailTransformAnimationState);
    }
}
*///?}
