package net.mat0u5.lifeseries.client.mixin;

//? if = 26.1 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FeatureRenderDispatcher.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class FeatureRenderDispatcherMixin {
	@Shadow @Final private SubmitNodeStorage submitNodeStorage;
	@Shadow @Final private MultiBufferSource.BufferSource bufferSource;
	@Shadow @Final private OutlineBufferSource outlineBufferSource;
	@Shadow @Final private MultiBufferSource.BufferSource crumblingBufferSource;
	@Shadow @Final private ModelFeatureRenderer modelFeatureRenderer;

	@Inject(method = "renderTranslucentParticles", at = @At("HEAD"))
	private void renderDashGhosts(CallbackInfo ci) {
		DashClient.renderDelayedGhosts();
		submitNodeStorage.getSubmitsPerOrder().forEach((order, collection) -> {
			if (DashClient.isGhostOrder(order)) {
				modelFeatureRenderer.renderTranslucent(collection, bufferSource, outlineBufferSource, crumblingBufferSource);
			}
		});
		bufferSource.endBatch();
	}
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class FeatureRenderDispatcherMixin {
	//Empty class to avoid mixin errors
}
//?}
