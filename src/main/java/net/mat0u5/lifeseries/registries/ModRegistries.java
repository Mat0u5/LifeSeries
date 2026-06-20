package net.mat0u5.lifeseries.registries;

public class ModRegistries {
    /**
     * Other server-side registries in:
     * {@link net.mat0u5.lifeseries.mixin.CommandsMixin}
     */
    public static void registerModStuff() {
        MobRegistry.registerMobs();
        ParticleRegistry.registerParticles();
    }
}
