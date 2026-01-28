package net.mat0u5.lifeseries.entity.angrysnowman;

import net.mat0u5.lifeseries.utils.interfaces.IEntityRenderState;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import java.util.Random;
import java.util.UUID;

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
*///?} else {
import net.minecraft.resources.Identifier;
 //?}

//? if >= 1.21.2 {
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
 //?}
//? if >= 1.21.4 {
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
 //?}

public class AngrySnowmanRenderer extends SnowGolemRenderer {
    public static int SKIN_VARIATIONS = 8;

    public AngrySnowmanRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    //? if <= 1.21 {
    /*public ResourceLocation getTextureLocation(SnowGolem entity) {
    *///?} else if <= 1.21.3 {
    /*public ResourceLocation getTextureLocation(LivingEntityRenderState state) {
    *///?} else if <= 1.21.9 {
    /*public ResourceLocation getTextureLocation(SnowGolemRenderState state) {
    *///?} else {
    public Identifier getTextureLocation(SnowGolemRenderState state) {
    //?}
            UUID uuid = null;
            //? if <= 1.21 {
            /*uuid  = entity.getUUID();
            *///?} else {
            if (state instanceof IEntityRenderState stateAccessor) {
                uuid = stateAccessor.ls$getEntity().getUUID();
            }
            //?}
            int variation = 0;
            if (uuid != null) {
                variation = new Random(uuid.hashCode()).nextInt(SKIN_VARIATIONS);
            }
            return IdentifierHelper.mod("textures/entity/angrysnowman/variation_"+variation+".png");
        }
}
