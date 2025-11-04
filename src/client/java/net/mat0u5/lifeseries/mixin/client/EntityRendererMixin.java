package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
//? if >= 1.21.2 {
/*import net.mat0u5.lifeseries.utils.interfaces.IEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(value = EntityRenderer.class, priority = 1)
//? if <= 1.21 {
public class EntityRendererMixin<T extends Entity> {
//?} else {
/*public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
*///?}

    //? if <= 1.21 {
    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"),
            index = 1
    )
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    //?} else if <= 1.21.6 {
    /*@ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            index = 1
    )
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
     *///?}


    //? if >= 1.21.2 {
    /*@Inject(method = "extractRenderState", at = @At("HEAD"))
    public void injectEntity(T entity, S state, float tickProgress, CallbackInfo ci) {
        if (state instanceof IEntityRenderState accessor) {
            accessor.ls$update(entity, tickProgress);
        }
    }
    *///?}
}
