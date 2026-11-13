package net.mat0u5.lifeseries.client.particle;

import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

//? if <= 1.21.6 {
/*import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
*///?} else {
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.RandomSource;
//?}

//? if <= 1.21.6 {
//public class InBetweenSquareParticle extends TextureSheetParticle {
//?} else {
public class InBetweenSquareParticle extends SingleQuadParticle {
//?}
    public static final float NORMAL_SIZE = 0.03F;
    public static final float SMALL_SIZE = NORMAL_SIZE * 0.5F;
    private static final int PARTICLE_LIGHT = 0xF000F0;

    private InBetweenSquareParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float size) {
        //? if <= 1.21.6 {
        /*super(level, x, y, z);
        this.setSprite(sprite);
        *///?} else {
        super(level, x, y, z, sprite);
        //?}
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize = size;
        this.lifetime = (int) (15+random.nextDouble()*15);
        this.setColor(1.0F, 1.0F, 1.0F);
        this.setAlpha(1.0F);
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

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final float size;

        public Provider(float size) {
            this.size = size;
        }

        //? if <= 1.21.6 {
        /*@Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        *///?} else {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
        //?}
            TextureAtlasSprite sprite = ClientRenderer.getParticleSprite(IdentifierHelper.lifeseries("in_between_square"));
            if (ClientRenderer.isMissing(sprite)) return null;
            return new InBetweenSquareParticle(level, x, y, z, sprite, size);
        }
    }
}
