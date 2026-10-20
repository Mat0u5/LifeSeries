package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if <= 26.3 {
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Registry;
//?}

//? if <= 1.21.6 {
/*import net.minecraft.client.particle.ParticleEngine;
@Mixin(value = ParticleEngine.class)
*///?} else {
import net.minecraft.client.particle.ParticleResources;
@Mixin(value = ParticleResources.class)
//?}
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ParticleResourcesMixin {
    //? if <= 26.3 {
    /*@Inject(method = "registerProviders", at = @At("TAIL"))
    private void registerParticles(CallbackInfo ci) {
        //? if <= 1.21.6 {
        //ParticleEngine self = (ParticleEngine)(Object)this;
         //?} else {
        ParticleResources self = (ParticleResources)(Object)this;
        //?}
        self.register(ParticleRegistry.TRIVIA_SPIRIT, new TriviaSpiritParticle.Provider());
    }
    *///?} else {
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void registerParticles(Registry<ParticleProvider<?>> registry, CallbackInfoReturnable<ParticleProvider<?>> cir) {
        ParticleResources.register(registry, ParticleRegistry.TRIVIA_SPIRIT, new TriviaSpiritParticle.Provider());
    }
    //?}
}
