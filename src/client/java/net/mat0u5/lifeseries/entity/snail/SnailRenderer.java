package net.mat0u5.lifeseries.entity.snail;

import net.mat0u5.lifeseries.Main;


//? if <= 1.21 {

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.MobEntityRenderer;
public class SnailRenderer extends MobEntityRenderer<Snail, SnailModel<Snail>> {
    public SnailRenderer(EntityRendererFactory.Context context) {
        super(context, new SnailModel<>(context.getPart(SnailModel.SNAIL)), 0.35f);
    }

    @Override
    public Identifier getTexture(Snail entity) {
        return Identifier.of(Main.MOD_ID, "textures/entity/snail/test.png");
    }

    @Override
    public void render(Snail entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.util.Identifier;

public class SnailRenderer extends AgeableMobEntityRenderer<Snail, SnailRenderState, SnailModel> {
    public SnailRenderer(EntityRendererFactory.Context context) {
        super(context, new SnailModel(context.getPart(SnailModel.SNAIL)), new SnailModel(context.getPart(SnailModel.SNAIL)), 0.35f);
    }

    @Override
    public SnailRenderState createRenderState() {
        return new SnailRenderState();
    }

    @Override
    public Identifier getTexture(SnailRenderState state) {
        return Identifier.of(Main.MOD_ID, "textures/entity/snail/test.png");
    }

    public void updateRenderState(Snail snail, SnailRenderState state, float f) {
        super.updateRenderState(snail, state, f);
        state.walkAnimationState.copyFrom(snail.clientData.walkAnimationState);
        state.glideAnimationState.copyFrom(snail.clientData.glideAnimationState);
        state.flyAnimationState.copyFrom(snail.clientData.flyAnimationState);
        state.stopFlyAnimationState.copyFrom(snail.clientData.stopFlyAnimationState);
        state.startFlyAnimationState.copyFrom(snail.clientData.startFlyAnimationState);
        state.idleAnimationState.copyFrom(snail.clientData.idleAnimationState);
        state.attacking = snail.isAttacking();
        state.flying = snail.isFlying();
        state.gliding = snail.isGliding();
        state.landing = snail.isLanding();
        state.mining = snail.isMining();
    }
}
*///?}
