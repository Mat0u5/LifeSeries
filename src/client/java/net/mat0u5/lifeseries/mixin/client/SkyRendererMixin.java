package net.mat0u5.lifeseries.mixin.client;

//? if < 1.21.9 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class SkyRendererMixin {
    //Empty class to avoid mixin errors
}
//?} else {

/*import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class)
public class SkyRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void customSkyColor(ClientLevel clientLevel, float f, Camera camera, SkyRenderState skyRenderState, CallbackInfo ci) {
        skyRenderState.skyColor = ClientRenderer.modifyColor(skyRenderState.skyColor, MainClient.skyColor, MainClient.skyColorSetMode, null);
    }
}
*///?}