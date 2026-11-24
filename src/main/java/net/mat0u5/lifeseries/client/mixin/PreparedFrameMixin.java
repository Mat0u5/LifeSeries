package net.mat0u5.lifeseries.client.mixin;

//? if = 26.2 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FeatureRenderDispatcher.PreparedFrame.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class PreparedFrameMixin {
	@Inject(method = "executeTranslucent", at = @At("HEAD"))
	private void copyInBetweenDepth(CallbackInfo ci) {
		InBetweenShaders.copySceneDepth();
	}
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class PreparedFrameMixin {
	//Empty class to avoid mixin errors
}
//?}
