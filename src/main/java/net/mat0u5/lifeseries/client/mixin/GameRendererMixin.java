package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

//? if >= 1.21.6 {
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(value = GameRenderer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class GameRendererMixin {
//? if >= 1.21.6 {
	//? if <= 26.1 {
	//@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
	//?} else {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V"))
	//?}
	private void copyInBetweenScene(CallbackInfo ci) {
		InBetweenShaders.copyScene();
	}
//?}
}
