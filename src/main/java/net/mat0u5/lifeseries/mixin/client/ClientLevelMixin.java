package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.21
//import net.minecraft.world.phys.Vec3;
//? if <= 1.21.9 {
/*import net.mat0u5.lifeseries.client.render.ClientRenderer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
*///?}

@Mixin(value = ClientLevel.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ClientLevelMixin {
    //? if <= 1.21.9 {

    /*@ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    //? if <= 1.21 {
    /^private Vec3 customSkyColor(Vec3 original) {
    ^///?} else {
    private int customSkyColor(int original) {
    //?}
        return ClientRenderer.modifyColor(original, LifeSeriesClient.skyColor, LifeSeriesClient.skyColorSetMode, null);
    }


    @ModifyReturnValue(method = "getCloudColor", at = @At("RETURN"))
    //? if <= 1.21 {
    /^private Vec3 customCloudColor(Vec3 original) {
     ^///?} else {
    private int customCloudColor(int original) {
    //?}
    return ClientRenderer.modifyColor(original, LifeSeriesClient.cloudColor, LifeSeriesClient.cloudColorSetMode, LifeSeriesClient.cachedFogRenderColor);
    }

    *///?}
}
