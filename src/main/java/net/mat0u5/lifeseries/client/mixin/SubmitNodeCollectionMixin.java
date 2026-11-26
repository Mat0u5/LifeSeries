package net.mat0u5.lifeseries.client.mixin;

//? if >= 1.21.9 {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 26.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
//?}
//? if >= 26.3 {
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
//?} else if >= 26.2 {
/*import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
*///?}

@Mixin(value = SubmitNodeCollection.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class SubmitNodeCollectionMixin {
	//? if >= 26.2 {
	@Shadow
	@Final
	public SimpleFeatureRenderPhase afterTerrain;
	//?}

	@Inject(method = {"submitShadow", "submitNameTag", "submitText", "submitFlame", "submitItem"}, at = @At("HEAD"), cancellable = true)
	private void hideOnDashGhosts(CallbackInfo ci) {
		if (DashClient.isRenderingGhost()) ci.cancel();
	}

	//? if <= 26.1 {
	//@ModifyVariable(method = {"submitModel", "submitModelPart"}, at = @At("HEAD"), argsOnly = true, ordinal = 2)
	//?} else {
	@ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	//?}
	private int dashGhostTint(int tintedColor) {
		return DashClient.isRenderingGhost() ? DashClient.ghostColor(tintedColor) : tintedColor;
	}

	//? if >= 26.3 {
	@WrapOperation(method = "submitModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/phase/FeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/SubmitNode;)V"))
	private void dashGhostsAfterTerrain(FeatureRenderPhase<?> phase, SubmitNode submit, Operation<Void> original) {
		if (DashClient.isRenderingGhost()) afterTerrain.submit(submit);
		else original.call(phase, submit);
	}
	//?} else if >= 26.2 {
	/*@WrapOperation(method = "submitModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/phase/TranslucentFeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/TranslucentSubmit;)V"))
	private void dashGhostsAfterTerrain(TranslucentFeatureRenderPhase phase, TranslucentSubmit submit, Operation<Void> original) {
		if (DashClient.isRenderingGhost()) afterTerrain.submit(submit);
		else original.call(phase, submit);
	}
	*///?}
}
//?} else {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class SubmitNodeCollectionMixin {
	//Empty class to avoid mixin errors
}
*///?}
