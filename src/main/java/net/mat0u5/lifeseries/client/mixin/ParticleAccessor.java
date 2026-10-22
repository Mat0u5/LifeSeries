package net.mat0u5.lifeseries.client.mixin;


import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Particle.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public interface ParticleAccessor {
    @Accessor("age")
    int ls$getAge();
}
