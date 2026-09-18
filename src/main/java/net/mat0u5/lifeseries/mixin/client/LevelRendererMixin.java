package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LevelRenderer.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class LevelRendererMixin {
    /**
     * For >= 26.3, located in:
     * {@link net.mat0u5.lifeseries.mixin.client.CloudRendererMixin}
     */
    //? if >= 1.21.11 <= 26.2 {
    @ModifyVariable(method = "addCloudsPass", at = @At("HEAD"), index = 7, argsOnly = true)
    private int setCloudColor(int value) {
        return ClientRenderer.modifyColor(value, LifeSeriesClient.cloudColor, LifeSeriesClient.cloudColorSetMode, LifeSeriesClient.cachedFogRenderColor);
    }
    //?}
}
