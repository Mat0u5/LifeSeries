
package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityBoundSoundInstance.class, priority = 2)
public abstract class EntityBoundSoundInstanceMixin {
    @Inject(method = "canPlaySound", at = @At("HEAD"), cancellable = true)
    public void canPlay(CallbackInfoReturnable<Boolean> cir) {
        if (Main.modFullyDisabled()) return;
        EntityBoundSoundInstance instance = (EntityBoundSoundInstance) (Object) this;
        if (instance.getLocation().getPath().contains("wildlife_trivia")) {
            cir.setReturnValue(true);
        }
    }
}
