package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleRegistry {
    public static final SimpleParticleType TRIVIA_SPIRIT = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                IdentifierHelper.mod("trivia_spirit"),
                TRIVIA_SPIRIT
        );
    }
}
