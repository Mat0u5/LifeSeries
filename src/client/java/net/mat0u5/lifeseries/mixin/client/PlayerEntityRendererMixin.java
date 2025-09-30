package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if <= 1.21 {
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mat0u5.lifeseries.Main;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(value = PlayerEntityRenderer.class, priority = 1)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    public void replaceRendering(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (Main.modFullyDisabled()) return;
        if (MainClient.invisiblePlayers.containsKey(abstractClientPlayerEntity.getUuid())) {
            long time = MainClient.invisiblePlayers.get(abstractClientPlayerEntity.getUuid());
            if (time > System.currentTimeMillis() || time == -1) {
                ci.cancel();
                return;
            }
        }

        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity.getUuid());
        LivingEntity dummy = morphComponent.getDummy();
        if(morphComponent.isMorphed() && dummy != null) {
            ci.cancel();
        }
    }
}
//?} else {
/*import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;

//? if <= 1.21.6 {
@Mixin(value = EntityRenderDispatcher.class, priority = 1)
//?} else {
/^@Mixin(value = EntityRenderManager.class, priority = 1)
^///?}
public class PlayerEntityRendererMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void render(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity playerEntity) {
            if (MainClient.invisiblePlayers.containsKey(playerEntity.getUuid())) {
                long time = MainClient.invisiblePlayers.get(playerEntity.getUuid());
                if (time > System.currentTimeMillis() || time == -1) {
                    cir.setReturnValue(false);
                }
            }

            MorphComponent morphComponent = MorphManager.getOrCreateComponent(playerEntity);
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
*///?}