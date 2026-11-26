package net.mat0u5.lifeseries.client.mixin;

//? if >= 1.21.9 <= 1.21.11 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParticlesRenderState.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ParticlesRenderStateMixin {
	@Inject(method = "submit", at = @At("HEAD"))
	private void submitDashGhosts(CallbackInfo ci) {
		DashClient.renderDelayedGhosts();
	}
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ParticlesRenderStateMixin {
	//Empty class to avoid mixin errors
}
//?}
