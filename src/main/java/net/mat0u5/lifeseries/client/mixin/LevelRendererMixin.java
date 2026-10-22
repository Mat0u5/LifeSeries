package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LevelRenderer.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class LevelRendererMixin {
    /**
     * For >= 26.3, located in:
     * {@link CloudRendererMixin}
     */
    //? if >= 1.21.11 <= 26.2 {
    /*@ModifyVariable(method = "addCloudsPass", at = @At("HEAD"), index = 7, argsOnly = true)
    private int setCloudColor(int value) {
        return ClientRenderer.modifyColor(value, LifeSeriesClient.cloudColor, LifeSeriesClient.cloudColorSetMode, LifeSeriesClient.cachedFogRenderColor);
    }
    *///?}
}
