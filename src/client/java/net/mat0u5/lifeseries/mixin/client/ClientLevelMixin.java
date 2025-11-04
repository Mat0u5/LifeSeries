package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
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
}
