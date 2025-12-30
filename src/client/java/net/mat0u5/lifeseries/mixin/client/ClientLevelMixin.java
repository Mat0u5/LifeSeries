package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.features.Morph;
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLevel.class, priority = 1)
public class ClientLevelMixin {

    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void tick(Entity entity, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (entity instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                Morph.clientTick(morphComponent);
            }
        }
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER))
    private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        if (passenger instanceof Player player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                Morph.clientTick(morphComponent);
            }
        }
    }

    //? if <= 1.21.9 {

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    //? if <= 1.21 {
    private Vec3 customSkyColor(Vec3 original) {
    //?} else {
    /*private int customSkyColor(int original) {
    *///?}
        return ClientRenderer.modifyColor(original, MainClient.skyColor, MainClient.skyColorSetMode, null);
    }


    @ModifyReturnValue(method = "getCloudColor", at = @At("RETURN"))
    //? if <= 1.21 {
    private Vec3 customCloudColor(Vec3 original) {
     //?} else {
    /*private int customCloudColor(int original) {
    *///?}
    return ClientRenderer.modifyColor(original, MainClient.cloudColor, MainClient.cloudColorSetMode, MainClient.cachedFogRenderColor);
    }

    //?}
}
