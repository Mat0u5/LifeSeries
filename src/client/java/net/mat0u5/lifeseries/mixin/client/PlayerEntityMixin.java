package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
//? if >= 1.21.2 {
//?}

@Mixin(value = Player.class, priority = 2)
public class PlayerEntityMixin {
    //? if >= 1.21.2 {
    /*@Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (Main.modFullyDisabled()) return;
        if (ClientUtils.shouldPreventGliding()) {
            cir.setReturnValue(false);
        }
    }
    *///?}
}
