package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenWindowsClient;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.9 {
import net.mat0u5.lifeseries.client.particle.InBetweenWindowParticle;
import net.mat0u5.lifeseries.client.particle.InBetweenWindowParticleGroup;
import net.mat0u5.lifeseries.client.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.client.particle.TriviaSpiritParticleGroup;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
//?}

@Mixin(value = ParticleEngine.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public abstract class ParticleEngineMixin {

    //? if >= 1.21.9 {
    @Inject(method = "clearParticles", at = @At("TAIL"))
    private void onClearParticles(CallbackInfo ci) {
        InBetweenWindowsClient.onParticlesCleared();
    }

    @Accessor("particles")
    abstract Map<ParticleRenderType, ParticleGroup<?>> ls$particles();

    @Inject(method = "createParticleGroup", at = @At("HEAD"), cancellable = true)
    private void triviaSpriteGroup(ParticleRenderType particleRenderType, CallbackInfoReturnable<ParticleGroup<?>> cir) {
        ParticleEngine engine = (ParticleEngine) (Object) this;
        if (particleRenderType == TriviaSpiritParticle.GROUP) {
            cir.setReturnValue(new TriviaSpiritParticleGroup(engine));
        }
        else if (particleRenderType == InBetweenWindowParticle.GROUP) {
            cir.setReturnValue(new InBetweenWindowParticleGroup(engine));
        }
    }
    @Inject(method = "extract", at = @At("TAIL"))
    private void extractTriviaSpriteGroup(ParticlesRenderState particlesRenderState, Frustum frustum, Camera camera, float f, CallbackInfo ci) {
        for (ParticleRenderType group : List.of(TriviaSpiritParticle.GROUP, InBetweenWindowParticle.GROUP)) {
            ParticleGroup<?> particleGroup = ls$particles().get(group);
            if (particleGroup != null && !particleGroup.isEmpty()) {
                particlesRenderState.add(particleGroup.extractRenderState(frustum, camera, f));
            }
        }
    }
    //?}
}
