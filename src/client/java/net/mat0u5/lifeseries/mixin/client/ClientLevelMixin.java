package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.features.Morph;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21
//import net.minecraft.world.phys.Vec3;
//? if <= 1.21.9 {
/*import net.mat0u5.lifeseries.render.ClientRenderer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.LifeSeriesClient;
*///?}

@Mixin(value = ClientLevel.class, priority = 1)
public class ClientLevelMixin {

    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void tick(Entity entity, CallbackInfo ci) {
        if (LifeSeries.modFullyDisabled()) return;
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                Morph.clientTick(morphComponent);
            }
        }
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER))
    private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
        if (LifeSeries.modFullyDisabled()) return;
        if (passenger instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                Morph.clientTick(morphComponent);
            }
        }
    }

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
