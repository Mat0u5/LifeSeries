package net.mat0u5.lifeseries.entity.newsnail;

import net.mat0u5.lifeseries.Main;


//? if <= 1.21 {

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.MobEntityRenderer;
public class NewSnailRenderer extends MobEntityRenderer<NewSnail, NewSnailModel<NewSnail>> {
    public NewSnailRenderer(EntityRendererFactory.Context context) {
        super(context, new NewSnailModel<>(context.getPart(NewSnailModel.NEWSNAIL)), 0f);
    }

    @Override
    public Identifier getTexture(NewSnail entity) {
        return Identifier.of(Main.MOD_ID, "textures/entity/newsnail/newsnail.png");
    }

    @Override
    public void render(NewSnail entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
//?} else {
/*import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.util.Identifier;

public class NewSnailRenderer extends AgeableMobEntityRenderer<NewSnail, NewSnailRenderState, NewSnailModel> {
    public NewSnailRenderer(EntityRendererFactory.Context context) {
        super(context, new NewSnailModel(context.getPart(NewSnailModel.NEWSNAIL)), new NewSnailModel(context.getPart(NewSnailModel.NEWSNAIL)), 1f);
    }

    @Override
    public NewSnailRenderState createRenderState() {
        return new NewSnailRenderState();
    }

    @Override
    public Identifier getTexture(NewSnailRenderState state) {
        return Identifier.of(Main.MOD_ID, "textures/entity/newsnail/newsnail.png");
    }

    public void updateRenderState(NewSnail snail, NewSnailRenderState state, float f) {
        super.updateRenderState(snail, state, f);
        state.idleAnimationState.copyFrom(snail.idleAnimationState);
    }
}
*///?}
