package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenEffectsClient;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21 {
/*import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
@Mixin(value = GameRenderer.class)
*///?} else {
import net.minecraft.client.renderer.ShaderManager;
@Mixin(value = ShaderManager.class)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ShaderManagerMixin {
	//? if <= 1.21 {
	/*@Inject(method = "reloadShaders", at = @At("TAIL"))
	private void onShadersReloaded(ResourceProvider resources, CallbackInfo ci) {
		InBetweenShaders.reloadShaders(resources);
		InBetweenEffectsClient.onResourcesReloaded();
	}
	*///?} else {
	@Inject(method = "apply", at = @At("TAIL"))
	private void onShadersReloaded(CallbackInfo ci) {
		InBetweenShaders.onShadersReloaded();
		InBetweenEffectsClient.onResourcesReloaded();
	}
	//?}
}
