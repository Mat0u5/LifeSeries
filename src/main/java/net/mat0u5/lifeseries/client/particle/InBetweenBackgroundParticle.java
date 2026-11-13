package net.mat0u5.lifeseries.client.particle;

import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

//? if <= 1.21.6 {
/*import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import java.util.Random;
*///?} else {
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.RandomSource;
//?}

//? if <= 1.21.6 {
//public class InBetweenBackgroundParticle extends TextureSheetParticle {
//?} else {
public class InBetweenBackgroundParticle extends SingleQuadParticle {
//?}
    private static final int MAX_TEXTURES = 64;
    private static final int PARTICLE_LIGHT = 0xF000F0;
    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 20;

    private final float maxAlpha;
    private final float wobbleOffset;

    private InBetweenBackgroundParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        //? if <= 1.21.6 {
        /*super(level, x, y, z);
        this.setSprite(sprite);
        *///?} else {
        super(level, x, y, z, sprite);
        //?}
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = 0.98F;
        this.lifetime = 40 + this.random.nextInt(20);
        this.quadSize = 0.12F + this.random.nextFloat() * 0.08F;
        this.xd = (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = 0.04 + this.random.nextDouble() * 0.06;
        this.zd = (this.random.nextDouble() - 0.5) * 0.01;
        this.maxAlpha = 0.75F + this.random.nextFloat() * 0.25F;
        this.wobbleOffset = this.random.nextFloat() * (float) (Math.PI * 2);
        this.setAlpha(0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age < FADE_IN_TICKS) {
            this.setAlpha(this.maxAlpha * this.age / (float) FADE_IN_TICKS);
        }
        else if (this.age > this.lifetime - FADE_OUT_TICKS) {
            this.setAlpha(this.maxAlpha * Math.max(0, this.lifetime - this.age) / (float) FADE_OUT_TICKS);
        }
        else {
            this.setAlpha(this.maxAlpha);
        }
        this.yd += Mth.sin(this.age * 0.05F + this.wobbleOffset) * 0.0002;
    }

    //? if <= 1.21.6 {
    /*@Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    *///?} else {
    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }
    //?}

    //? if <= 1.21.11 {
    /*@Override
    public int getLightColor(float partialTick) {
        return PARTICLE_LIGHT;
    }
    *///?} else {
    @Override
    public int getLightCoords(float partialTick) {
        return PARTICLE_LIGHT;
    }
    //?}


    // Sprites are stored as 0.png, 1.png, ...
    private static List<TextureAtlasSprite> getSprites(String folder) {
        List<TextureAtlasSprite> sprites = new ArrayList<>();
        for (int i = 0; i < MAX_TEXTURES; i++) {
            TextureAtlasSprite sprite = ClientRenderer.getParticleSprite(IdentifierHelper.lifeseries(folder + "/" + i));
            if (ClientRenderer.isMissing(sprite)) break;
            sprites.add(sprite);
        }
        return sprites;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final String textureFolder;

        public Provider(String textureFolder) {
            this.textureFolder = textureFolder;
        }

        //? if <= 1.21.6 {
        /*private static final Random random = new Random();
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        *///?} else {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
        //?}
            //Create particle with random sprite out of the selection
            List<TextureAtlasSprite> sprites = getSprites(textureFolder);
            if (sprites.isEmpty()) return null;
            TextureAtlasSprite sprite = sprites.get(random.nextInt(sprites.size()));
            return new InBetweenBackgroundParticle(level, x, y, z, sprite);
        }
    }
}
