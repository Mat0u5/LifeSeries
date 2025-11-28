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
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

@Mixin(value = EntityRenderer.class, priority = 1)
//? if <= 1.21 {
public class EntityRendererMixin<T extends Entity> {
//?} else {
/*public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
*///?}

    //? if <= 1.20.3 {
    /*@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), index = 1)
    public Component render(Component text) {
        return ClientUtils.getPlayerName(text);
    }
    *///?} else if <= 1.21 {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"), index = 1)
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
    @Inject(method = "affectedByCulling", at = @At("HEAD"), cancellable = true)
    public void stopCulling(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof IMorph morph && morph.isFromMorph()) {
            cir.setReturnValue(false);
            return;
        }
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getOrCreateComponent(player.getUUID());
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
    *///?}
}
