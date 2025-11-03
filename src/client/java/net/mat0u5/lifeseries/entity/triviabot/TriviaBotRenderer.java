package net.mat0u5.lifeseries.entity.triviabot;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.Main;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
public class TriviaBotRenderer extends MobRenderer<TriviaBot, TriviaBotModel<TriviaBot>> {
    public TriviaBotRenderer(EntityRendererProvider.Context context) {
        super(context, new TriviaBotModel<>(context.bakeLayer(TriviaBotModel.TRIVIA_BOT)), 0.45f);
    }

    @Override
    public ResourceLocation getTexture(TriviaBot entity) {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/entity/triviabot/triviabot.png");
    }

    @Override
    public void render(TriviaBot entity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.util.Identifier;

public class TriviaBotRenderer extends AgeableMobEntityRenderer<TriviaBot, TriviaBotRenderState, TriviaBotModel> {
    public TriviaBotRenderer(EntityRendererFactory.Context context) {
        super(context, new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), 0.45f);
    }

    @Override
    public TriviaBotRenderState createRenderState() {
        return new TriviaBotRenderState();
    }

    @Override
    public Identifier getTexture(TriviaBotRenderState state) {
        return Identifier.of(Main.MOD_ID, "textures/entity/triviabot/triviabot.png");
    }

    public void updateRenderState(TriviaBot triviaBot, TriviaBotRenderState state, float f) {
        super.updateRenderState(triviaBot, state, f);

        state.glideAnimationState.copyFrom(triviaBot.clientData.glideAnimationState);
        state.idleAnimationState.copyFrom(triviaBot.clientData.idleAnimationState);
        state.walkAnimationState.copyFrom(triviaBot.clientData.walkAnimationState);
        state.countdownAnimationState.copyFrom(triviaBot.clientData.countdownAnimationState);
        state.analyzingAnimationState.copyFrom(triviaBot.clientData.analyzingAnimationState);
        state.answerCorrectAnimationState.copyFrom(triviaBot.clientData.answerCorrectAnimationState);
        state.answerIncorrectAnimationState.copyFrom(triviaBot.clientData.answerIncorrectAnimationState);
        state.snailTransformAnimationState.copyFrom(triviaBot.clientData.snailTransformAnimationState);
    }
}
*///?}
