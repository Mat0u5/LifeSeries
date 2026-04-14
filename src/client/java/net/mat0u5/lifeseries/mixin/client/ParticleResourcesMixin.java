package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21.6 {
/*import net.minecraft.client.particle.ParticleEngine;
@Mixin(value = ParticleEngine.class)
*///?} else {
import net.minecraft.client.particle.ParticleResources;
@Mixin(value = ParticleResources.class)
//?}
public class ParticleResourcesMixin {
    @Inject(method = "registerProviders", at = @At("TAIL"))
    private void lifeseries$registerParticles(CallbackInfo ci) {
        //? if <= 1.21.6 {
        /*ParticleEngine self = (ParticleEngine)(Object)this;
         *///?} else {
        ParticleResources self = (ParticleResources)(Object)this;
        //?}
        self.register(ParticleRegistry.TRIVIA_SPIRIT, new TriviaSpiritParticle.Provider());
    }
}
