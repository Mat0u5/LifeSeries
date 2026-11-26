package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModelPart.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ModelPartMixin {
	//? if <= 1.20.5 {
	/*@ModifyVariable(method = "compile", at = @At("HEAD"), argsOnly = true, ordinal = 3)
	private float dashGhostAlpha(float alpha) {
		return DashClient.isRenderingGhost() ? DashClient.ghostAlpha(alpha) : alpha;
	}
	*///?} else {
	@ModifyVariable(method = "compile", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private int dashGhostAlpha(int color) {
		return DashClient.isRenderingGhost() ? DashClient.ghostColor(color) : color;
	}
	//?}
}
