package net.mat0u5.lifeseries.entity.snail;

import net.mat0u5.lifeseries.features.SnailSkinsClient;
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
public class SnailRenderer extends MobRenderer<Snail, SnailModel<Snail>> {

    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context, new SnailModel<>(context.bakeLayer(SnailModel.SNAIL)), 0.35f);
    }

    @Override
    public ResourceLocation getTextureLocation(Snail entity) {
        if (entity.isFromTrivia()) return Snail.TRIVIA_TEXTURE;
        if (entity.isBoundPlayerDead()) return Snail.ZOMBIE_TEXTURE;

        ResourceLocation dynamicTexture = SnailSkinsClient.getSnailTexture(entity.getSkinName());
        if (dynamicTexture != null) return dynamicTexture;

        return Snail.DEFAULT_TEXTURE;
    }

    @Override
    public void render(Snail entity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.renderer.entity.AgeableMobRenderer;

public class SnailRenderer extends AgeableMobRenderer<Snail, SnailRenderState, SnailModel> {
    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context, new SnailModel(context.bakeLayer(SnailModel.SNAIL)), new SnailModel(context.bakeLayer(SnailModel.SNAIL)), 0.35f);
    }

    @Override
    public SnailRenderState createRenderState() {
        return new SnailRenderState();
    }

    @Override
    //? if <= 1.21.9 {
    public ResourceLocation getTextureLocation(SnailRenderState state) {
    //?} else {
    /^public Identifier getTextureLocation(SnailRenderState state) {
    ^///?}
        if (state.fromTrivia) return Snail.TRIVIA_TEXTURE;
        if (state.boundPlayerDead) return Snail.ZOMBIE_TEXTURE;

        var dynamicTexture = SnailSkinsClient.getSnailTexture(state.skinName);
        if (dynamicTexture != null) return dynamicTexture;

        return Snail.DEFAULT_TEXTURE;
    }

    @Override
    public void extractRenderState(Snail snail, SnailRenderState state, float f) {
        super.extractRenderState(snail, state, f);
        state.walkAnimationState.copyFrom(snail.clientData.walkAnimationState);
        state.glideAnimationState.copyFrom(snail.clientData.glideAnimationState);
        state.flyAnimationState.copyFrom(snail.clientData.flyAnimationState);
        state.stopFlyAnimationState.copyFrom(snail.clientData.stopFlyAnimationState);
        state.startFlyAnimationState.copyFrom(snail.clientData.startFlyAnimationState);
        state.idleAnimationState.copyFrom(snail.clientData.idleAnimationState);
        state.attacking = snail.isSnailAttacking();
        state.flying = snail.isSnailFlying();
        state.gliding = snail.isSnailGliding();
        state.landing = snail.isSnailLanding();
        state.mining = snail.isSnailMining();
        state.fromTrivia = snail.isFromTrivia();
        state.skinName = snail.getSkinName();
        state.boundPlayerDead = snail.isBoundPlayerDead();
    }
}
*///?}
