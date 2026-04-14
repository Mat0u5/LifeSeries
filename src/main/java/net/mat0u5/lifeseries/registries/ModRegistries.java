package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.utils.other.TextUtils;

public class ModRegistries {
    /**
     * Other server-side registries in:
     * {@link net.mat0u5.lifeseries.mixin.CommandsMixin}
     */
    public static void registerModStuff() {
        TextUtils.setEmotes();
        MobRegistry.registerMobs();
        ParticleRegistry.registerParticles();
    }
}
