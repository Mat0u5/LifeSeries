package net.mat0u5.lifeseries.mixin;
//? if < 1.21.6 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class WaypointTransmitterMixin {
    //Empty class to avoid mixin errors
}
*///?} else {
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

//? if !forge {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value = WaypointTransmitter.class, priority = 1)
public interface WaypointTransmitterMixin {
*///?} else {
import org.spongepowered.asm.mixin.injection.Redirect;
@Mixin(value = LivingEntity.class, priority = 3)
public class WaypointTransmitterMixin {
//?}

    //? if !forge {
    /*@Inject(method = "doesSourceIgnoreReceiver", at = @At("HEAD"), cancellable = true)
    private static void cannotReceive(LivingEntity sourceEntity, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        if (LifeSeries.isClientOrDisabled()) return;
     *///?} else {
    @Redirect(method = "makeWaypointConnectionWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/waypoints/WaypointTransmitter;doesSourceIgnoreReceiver(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/server/level/ServerPlayer;)Z"))
    private static boolean cannotReceive(LivingEntity sourceEntity, ServerPlayer receiver) {
        if (LifeSeries.isClientOrDisabled()) return WaypointTransmitter.doesSourceIgnoreReceiver(sourceEntity, receiver);
    //?}
        if (sourceEntity instanceof ServerPlayer source) {
            boolean showLocatorBar = currentSeason.LOCATOR_BAR;
            if (!showLocatorBar && currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULMATE_LOCATOR_BAR) {
                UUID receiverSoulmateUUID = doubleLife.getSoulmateUUID(receiver.getUUID());
                showLocatorBar = source.getUUID().equals(receiverSoulmateUUID);
            }

            if (!showLocatorBar && currentSeason.boogeymanManager.BOOGEYMAN_ENABLED && currentSeason.boogeymanManager.BOOGEYMAN_LOCATOR_BAR && currentSeason.boogeymanManager.isBoogeyman(receiver)) {
                showLocatorBar = true;
            }

            if (!showLocatorBar) {
                //? if !forge {
                /*cir.setReturnValue(true);
                *///?} else {
                return true;
                //?}
            }
            // Else do vanilla logic
        }
        //? if forge {
        return WaypointTransmitter.doesSourceIgnoreReceiver(sourceEntity, receiver);
        //?}
    }
}
//?}