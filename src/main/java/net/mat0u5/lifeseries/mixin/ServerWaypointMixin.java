package net.mat0u5.lifeseries.mixin;
//? if < 1.21.6 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class ServerWaypointMixin {
    //Empty class to avoid mixin errors
}
//?} else {
/*import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import net.mat0u5.lifeseries.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = WaypointTransmitter.class, priority = 1)
public interface ServerWaypointMixin {

    @Inject(method = "doesSourceIgnoreReceiver", at = @At("HEAD"), cancellable = true)
    private static void cannotReceive(LivingEntity source, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        if (Main.modDisabled()) return;
        if (source instanceof ServerPlayer sender) {
            if (currentSeason instanceof DoubleLife doubleLife && DoubleLife.SOULMATE_LOCATOR_BAR) {
                UUID receiverSoulmateUUID = doubleLife.getSoulmateUUID(receiver.getUUID());
                if (sender.getUUID().equals(receiverSoulmateUUID)) {
                    cir.setReturnValue(false);
                }
                else {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
*///?}