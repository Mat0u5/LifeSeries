package net.mat0u5.lifeseries.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.ShieldBashClient;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 26.2 {
/*import net.minecraft.client.renderer.ItemInHandRenderer;
@Mixin(value = ItemInHandRenderer.class)
*///?} else {
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
@Mixin(value = FirstPersonHandsAndItemsRenderer.class)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ItemInHandRendererMixin {
    //? if <= 26.1 {
    /*@Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER, ordinal = 0))
    *///?} else {
    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER, ordinal = 0))
    //?}
    private void onArmAnim(CallbackInfo ci, @Local(argsOnly = true) PoseStack poseStack, @Local(argsOnly = true) InteractionHand hand) {
        ShieldBashClient.applyShieldThrust(poseStack, hand);
    }
}
