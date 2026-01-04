package net.mat0u5.lifeseries.mixin.client;


import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Particle.class, priority = 1)
public interface ParticleAccessor {
    @Accessor("age")
    int ls$getAge();
}
