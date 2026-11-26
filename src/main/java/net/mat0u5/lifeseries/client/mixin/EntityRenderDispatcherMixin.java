package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

//? if <= 1.21.6 {
/*import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(value = EntityRenderDispatcher.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class EntityRenderDispatcherMixin {
	// In SubmitNodeCollectionMixin for >= 1.21.9
	//? if <= 1.21.6 {
	/*@Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
	private static void hideDashGhostShadow(CallbackInfo ci) {
		if (DashClient.isRenderingGhost()) ci.cancel();
	}

	@Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
	private void hideDashGhostFlame(CallbackInfo ci) {
		if (DashClient.isRenderingGhost()) ci.cancel();
	}
	*///?}
}
