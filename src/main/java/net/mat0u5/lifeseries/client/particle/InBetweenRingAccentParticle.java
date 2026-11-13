package net.mat0u5.lifeseries.client.particle;

import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.concurrent.ThreadLocalRandom;

//? if <= 1.21.6 {
/*import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import java.util.Random;
*///?} else {
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.RandomSource;
//?}

// Mostly the same as PortalParticle, with some changes.
//? if <= 1.21.6 {
//public class InBetweenRingAccentParticle extends TextureSheetParticle {
//?} else {
public class InBetweenRingAccentParticle extends SingleQuadParticle {
//?}
    private static final int PORTAL_TEXTURES = 8;

    private InBetweenRingAccentParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        //? if <= 1.21.6 {
        /*super(level, x, y, z);
        this.setSprite(sprite);
        *///?} else {
        super(level, x, y, z, sprite);
        //?}
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.quadSize = 0.085F * (random.nextFloat() * 0.2F + 0.5F);
        float brightness = random.nextFloat() * 0.6F + 0.4F;
        this.setColor(brightness * 0.9F, brightness * 0.3F, brightness);
        this.lifetime = 43;
        this.xd = (random.nextDouble() - 0.5) * 0.003;
        this.yd = -(0.016 + random.nextDouble() * 0.003);
        this.zd = (random.nextDouble() - 0.5) * 0.003;
    }

    //? if <= 1.21.6 {
    /*@Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    *///?} else {
    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }
    //?}

    @Override
    public float getQuadSize(float partialTick) {
        float progress = 1.0F - (this.age + partialTick) / this.lifetime;
        return this.quadSize * (1.0F - progress * progress);
    }

    // Same "glows brighter towards the end" effect as the portal particle
    //? if <= 1.21.11 {
    /*@Override
    public int getLightColor(float partialTick) {
        return addBlockEmission(super.getLightColor(partialTick));
    }
    *///?} else {
    @Override
    public int getLightCoords(float partialTick) {
        return addBlockEmission(super.getLightCoords(partialTick));
    }
    //?}

    private int addBlockEmission(int light) {
        float emission = (float) this.age / this.lifetime;
        emission *= emission;
        emission *= emission;
        int block = Math.min((light & 0xFFFF) + (int) (Math.min(emission, 1.0F) * 240.0F), 240);
        return (light & 0xFFFF0000) | block;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        //? if <= 1.21.6 {
        /*private static final Random random = new Random();
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        *///?} else {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
        //?}
            int index = random.nextInt(PORTAL_TEXTURES);
            TextureAtlasSprite sprite = ClientRenderer.getParticleSprite(IdentifierHelper.of("minecraft", "generic_" + index));
            if (ClientRenderer.isMissing(sprite)) return null;
            return new InBetweenRingAccentParticle(level, x, y, z, sprite);
        }
    }
}
