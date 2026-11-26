package net.mat0u5.lifeseries.client.mixin;

//? if >= 1.21.9 {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = SubmitNodeStorage.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class SubmitNodeStorageMixin {
	@ModifyVariable(method = "order(I)Lnet/minecraft/client/renderer/SubmitNodeCollection;", at = @At("HEAD"), argsOnly = true)
	private int dashGhostOrder(int order) {
		return DashClient.ghostOrder(order);
	}
}
//?} else {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class SubmitNodeStorageMixin {
	//Empty class to avoid mixin errors
}
*///?}
