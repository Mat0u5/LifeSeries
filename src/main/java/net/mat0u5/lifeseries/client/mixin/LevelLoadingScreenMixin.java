package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenTransition;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21.6 {
//import net.minecraft.client.gui.screens.ReceivingLevelScreen;
//?} else {
import net.minecraft.client.gui.screens.LevelLoadingScreen;
//?}

//? if <= 1.21.6 {
//@Mixin(ReceivingLevelScreen.class)
//?} else {
@Mixin(LevelLoadingScreen.class)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class LevelLoadingScreenMixin {
	//? if <= 1.20 {
	//@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	//?} else if <= 1.21.11 {
	//@Inject(method = {"render", "renderBackground"}, at = @At("HEAD"), cancellable = true)
	//?} else {
	@Inject(method = {"extractRenderState", "extractBackground"}, at = @At("HEAD"), cancellable = true)
	//?}
	private void coverLoadingScreen(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		if (!InBetweenTransition.shouldCoverLoadingScreen()) return;
		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), InBetweenTransition.getCoverColor());
		ci.cancel();
	}
}
