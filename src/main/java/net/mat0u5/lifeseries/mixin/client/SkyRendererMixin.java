package net.mat0u5.lifeseries.mixin.client;

//? if <= 1.21.9 {
/*import net.minecraft.server.MinecraftServer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class SkyRendererMixin {
    //Empty class to avoid mixin errors
}
*///?} else {

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.state.level.SkyRenderState;

@Mixin(value = SkyRenderer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class SkyRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void customSkyColor(ClientLevel clientLevel, float f, Camera camera, SkyRenderState skyRenderState, CallbackInfo ci) {
        skyRenderState.skyColor = ClientRenderer.modifyColor(skyRenderState.skyColor, LifeSeriesClient.skyColor, LifeSeriesClient.skyColorSetMode, null);
    }
}
//?}