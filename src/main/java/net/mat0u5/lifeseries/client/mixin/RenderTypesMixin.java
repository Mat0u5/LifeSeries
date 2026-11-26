package net.mat0u5.lifeseries.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <= 1.21.9 {
//@Mixin(value = RenderType.class)
//?} else {
import net.minecraft.client.renderer.rendertype.RenderTypes;
@Mixin(value = RenderTypes.class)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class RenderTypesMixin {
	//? if <= 1.21.11 {
	//@Inject(method = {"entitySolid", "entityCutout", "entityCutoutNoCull", "entityTranslucent", "armorCutoutNoCull"}, at = @At("HEAD"), cancellable = true)
	//?} else if <= 26.2 {
	//@Inject(method = {"entitySolid", "entityCutout", "entityCutoutCull", "entityTranslucent", "armorCutoutNoCull"}, at = @At("HEAD"), cancellable = true)
	//?} else {
	@Inject(method = {"entitySolid", "entityCutout", "entityCutoutCull", "entityTranslucent", "armorCutoutNoCull", "armorCutoutNoCullGlint", "armorTrim"}, at = @At("HEAD"), cancellable = true)
	//?}
	private static void dashGhostRenderType(CallbackInfoReturnable<RenderType> cir, @Local(argsOnly = true) Identifier texture) {
		if (DashClient.shouldReplaceRenderType()) cir.setReturnValue(DashClient.ghostRenderType(texture));
	}
}
