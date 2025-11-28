package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 1)
public abstract class LivingEntityRendererMixin {
    //Located in EntityRenderDispatcher in <= 1.20.3
    //? if > 1.20.3 && <= 1.21 {
    @Inject(method = "getShadowRadius(Lnet/minecraft/world/entity/LivingEntity;)F", at = @At("HEAD"), cancellable = true)
    public <T extends LivingEntity> void stopShadow(T livingEntity, CallbackInfoReturnable<Float> cir){
        if (Main.modFullyDisabled()) return;
        if (livingEntity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null && morphComponent.isMorphed()) {
                cir.setReturnValue(0.0F);
            }
        }
    }
    //?}
}
