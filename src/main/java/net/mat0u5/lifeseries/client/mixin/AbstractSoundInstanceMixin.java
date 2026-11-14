package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.utils.ClientSounds;
import net.mat0u5.lifeseries.config.modifiable.SoundBehavior;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSoundInstance.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class AbstractSoundInstanceMixin {
    @Inject(method = "isLooping", at = @At("HEAD"), cancellable = true)
    private void isRepeatable(CallbackInfoReturnable<Boolean> cir) {
        if (LifeSeries.modDisabled()) return;
        AbstractSoundInstance soundInstance = (AbstractSoundInstance) (Object) this;
        String soundId = ClientSounds.getSoundId(soundInstance);
        if (ClientSounds.hasBehavior(soundId, SoundBehavior.LOOP)) {
            cir.setReturnValue(true);
        }
        else if (soundInstance instanceof EntityBoundSoundInstance && ClientSounds.hasBehavior(soundId, SoundBehavior.LOOP_ENTITY_BOUND)) {
            cir.setReturnValue(true);
        }
    }
}
