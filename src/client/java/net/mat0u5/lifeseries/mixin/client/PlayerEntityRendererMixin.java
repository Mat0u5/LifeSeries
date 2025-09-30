package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.21 {
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mat0u5.lifeseries.Main;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
//?}

//? if > 1.21.6 {
/*import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.text.Text;
import net.mat0u5.lifeseries.utils.ClientUtils;
*///?}

@Mixin(value = PlayerEntityRenderer.class, priority = 1)
public abstract class PlayerEntityRendererMixin {

    //? if <= 1.21 {
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
    //?}

    //? if > 1.21.6 {
    /*@ModifyArg(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"),
            index = 3
    )
    public Text render(Text text) {
        return ClientUtils.getPlayerName(text);
    }
    *///?}
}