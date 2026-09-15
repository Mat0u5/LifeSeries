package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.utils.other.LSIdentifierHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleRegistry {
    public static final SimpleParticleType TRIVIA_SPIRIT = new SimpleParticleType(false) {};

    public static void registerParticles() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                LSIdentifierHelper.lifeseries("trivia_spirit"),
                TRIVIA_SPIRIT
        );
    }
}
