package net.mat0u5.lifeseries.mixin.client;
//? if <= 26.2 {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface CloudRendererMixin {
    //Empty class to avoid mixin errors
}
//?} else {

/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CloudRenderer.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class CloudRendererMixin {

	/^*
	 * For < 26.3, located in:
	 * {@link net.mat0u5.lifeseries.mixin.client.LevelRendererMixin}
	 ^/
	@ModifyVariable(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", at = @At("HEAD"), index = 1, argsOnly = true)
	private int setCloudColor(int color) {
		return ClientRenderer.modifyColor(color, LifeSeriesClient.cloudColor, LifeSeriesClient.cloudColorSetMode, LifeSeriesClient.cachedFogRenderColor);
	}
}
*///?}