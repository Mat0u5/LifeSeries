package net.mat0u5.lifeseries.entity.newsnail;

import net.mat0u5.lifeseries.Main;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NewSnailRenderer extends MobEntityRenderer<NewSnail, NewSnailModel<NewSnail>> {
    public NewSnailRenderer(EntityRendererFactory.Context context) {
        super(context, new NewSnailModel<>(context.getPart(NewSnailModel.NEWSNAIL)), 0.75f);
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
