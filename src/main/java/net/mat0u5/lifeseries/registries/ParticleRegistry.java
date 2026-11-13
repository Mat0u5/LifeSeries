package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.events.common.CommonRegistryEvents;
import net.mat0u5.matlib.registries.util.IdentifiedParticle;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;

public class ParticleRegistry {
    public static final SimpleParticleType TRIVIA_SPIRIT = new SimpleParticleType(false) {};
    public static final SimpleParticleType IN_BETWEEN_BACKGROUND = new SimpleParticleType(false) {};
    public static final SimpleParticleType IN_BETWEEN_BACKGROUND_TRIVIA = new SimpleParticleType(false) {};
    public static final SimpleParticleType IN_BETWEEN_RING_ACCENT = new SimpleParticleType(false) {};
    public static final SimpleParticleType IN_BETWEEN_PATH = new SimpleParticleType(false) {};
    public static final SimpleParticleType IN_BETWEEN_PATH_SMALL = new SimpleParticleType(false) {};

    public static void registerParticles() {
        CommonRegistryEvents.PARTICLE.register(() -> List.of(
                new IdentifiedParticle(TRIVIA_SPIRIT, IdentifierHelper.lifeseries("trivia_spirit")),
                new IdentifiedParticle(IN_BETWEEN_BACKGROUND, IdentifierHelper.lifeseries("in_between_background")),
                new IdentifiedParticle(IN_BETWEEN_BACKGROUND_TRIVIA, IdentifierHelper.lifeseries("in_between_background_trivia")),
                new IdentifiedParticle(IN_BETWEEN_RING_ACCENT, IdentifierHelper.lifeseries("in_between_ring_accent")),
                new IdentifiedParticle(IN_BETWEEN_PATH, IdentifierHelper.lifeseries("in_between_path")),
                new IdentifiedParticle(IN_BETWEEN_PATH_SMALL, IdentifierHelper.lifeseries("in_between_path_small"))
        ));
    }
}
