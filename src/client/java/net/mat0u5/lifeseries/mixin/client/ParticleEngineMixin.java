package net.mat0u5.lifeseries.mixin.client;

//? if < 1.21.9 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class ParticleEngineMixin {
    //Empty class to avoid mixin errors
}
*///?} else {

import net.mat0u5.lifeseries.particle.TriviaSpiritParticle;
import net.mat0u5.lifeseries.particle.TriviaSpiritParticleGroup;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.ParticlesRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Accessor("particles")
    abstract Map<ParticleRenderType, ParticleGroup<?>> ls$particles();

    @Inject(method = "createParticleGroup", at = @At("HEAD"), cancellable = true)
    private void triviaSpriteGroup(ParticleRenderType particleRenderType, CallbackInfoReturnable<ParticleGroup<?>> cir) {
        ParticleEngine engine = (ParticleEngine) (Object) this;
        if (particleRenderType == TriviaSpiritParticle.GROUP) {
            cir.setReturnValue(new TriviaSpiritParticleGroup(engine));
        }
    }
    @Inject(method = "extract", at = @At("TAIL"))
    private void extractTriviaSpriteGroup(ParticlesRenderState particlesRenderState, Frustum frustum, Camera camera, float f, CallbackInfo ci) {
        ParticleGroup<?> particleGroup = ls$particles().get(TriviaSpiritParticle.GROUP);
        if (particleGroup != null && !particleGroup.isEmpty()) {
            particlesRenderState.add(particleGroup.extractRenderState(frustum, camera, f));
        }
    }
}
//?}
