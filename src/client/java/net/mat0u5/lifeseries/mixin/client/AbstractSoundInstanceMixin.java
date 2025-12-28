package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSoundInstance.class, priority = 1)
public class AbstractSoundInstanceMixin {

    @Inject(method = "isLooping", at = @At("HEAD"), cancellable = true)
    private void isRepeatable(CallbackInfoReturnable<Boolean> cir) {
        if (Main.modFullyDisabled()) return;
        AbstractSoundInstance soundInstance = (AbstractSoundInstance) (Object) this;
        if (soundInstance instanceof EntityBoundSoundInstance entityTrackingSound) {
            //? if <= 1.21.9 {
            if (entityTrackingSound.getLocation().getPath().equalsIgnoreCase("wildlife_trivia_suspense") ||
                    entityTrackingSound.getLocation().getPath().equalsIgnoreCase("nicelife_santabot_suspense")) {
                cir.setReturnValue(true);
            }
            //?} else {
            /*if (entityTrackingSound.getIdentifier().getPath().equalsIgnoreCase("wildlife_trivia_suspense") ||
                    entityTrackingSound.getIdentifier().getPath().equalsIgnoreCase("nicelife_santabot_suspense")) {
                cir.setReturnValue(true);
            }
            *///?}
        }
    }
}
