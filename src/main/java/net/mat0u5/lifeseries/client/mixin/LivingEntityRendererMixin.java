package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

//? if <= 1.21 {
/*import net.mat0u5.lifeseries.client.features.abilities.AbilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

@Mixin(value = LivingEntityRenderer.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public abstract class LivingEntityRendererMixin {
	//? if <= 1.21 {
	/*@Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
	private void healthVisionHiddenName(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		boolean healthOnly = !cir.getReturnValue() && AbilitiesClient.healthVisionText(entity, Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity)) != null;
		AbilitiesClient.healthOnlyEntity = healthOnly ? entity : null;
		if (healthOnly) cir.setReturnValue(true);
	}
	*///?}
}
