package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
*///?} else {
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
//?}
//? if >= 1.21.4
import net.minecraft.client.renderer.item.ItemModelResolver;

//? if <= 1.21 {
//@Mixin(value = HumanoidArmorLayer.class, priority = 1)
//?} else {
@Mixin(value = HumanoidMobRenderer.class, priority = 1)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ArmorRenderMixin {
    //? if <= 1.21 {
    /*@Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void hideArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        if (ClientRenderer.shouldHideEquipmentClient(entity, slot, entity.getItemBySlot(slot))) {
            ci.cancel();
        }
    }
    *///?} else if <= 1.21.2 {
    /*@Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void hideArmor(LivingEntity entity, HumanoidRenderState state, float partialTick, CallbackInfo ci) {
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.HEAD, state.headItem)) state.headItem = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.CHEST, state.chestItem)) state.chestItem = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.LEGS, state.legsItem)) state.legsItem = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.FEET, state.feetItem)) state.feetItem = ItemStack.EMPTY;
    }
    *///?} else {
    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void hideArmor(LivingEntity entity, HumanoidRenderState state, float partialTick, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.HEAD, state.headEquipment)) state.headEquipment = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.CHEST, state.chestEquipment)) state.chestEquipment = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.LEGS, state.legsEquipment)) state.legsEquipment = ItemStack.EMPTY;
        if (ClientRenderer.shouldHideEquipmentClient(entity, EquipmentSlot.FEET, state.feetEquipment)) state.feetEquipment = ItemStack.EMPTY;
    }
    //?}
}
