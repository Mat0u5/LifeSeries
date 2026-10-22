package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.events.common.CommonRegistryEvents;
import net.mat0u5.matlib.registries.util.IdentifiedParticle;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;

public class ParticleRegistry {
    public static final SimpleParticleType TRIVIA_SPIRIT = new SimpleParticleType(false) {};

    public static void registerParticles() {
        CommonRegistryEvents.PARTICLE.register(() -> List.of(
                new IdentifiedParticle(TRIVIA_SPIRIT, IdentifierHelper.lifeseries("trivia_spirit"))
        ));
    }
}
