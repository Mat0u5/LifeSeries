package net.mat0u5.lifeseries.particle;

//? if <= 1.21.6 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotModel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
//? if <= 1.21 {
/^import net.minecraft.util.FastColor;
 ^///?} else {
import net.minecraft.util.ARGB;
//?}

public class TriviaSpiritParticle extends Particle {
    private final Model model;
    private final RenderType renderType;

    TriviaSpiritParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
        this.model = new TriviaBotModel(Minecraft.getInstance().getEntityModels().bakeLayer(TriviaBotModel.TRIVIA_BOT), true);
        this.renderType = RenderType.entityTranslucent(TriviaBot.SANTABOT_TEXTURE);

        this.gravity = 0.0f;
        this.lifetime = 80;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
            //? if <= 1.21.2 {
    /^public void render(VertexConsumer vertexConsumer0, Camera camera, float f) {
     ^///?} else {
    public void renderCustom(PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, float f) {
        //?}
        float g = ((float)this.age + f) / (float)this.lifetime;
        float h = 0.05f + 0.5f * Mth.sin(g * (float)Math.PI);
        //? if < 1.21 {
        //?} else if <= 1.21 {
        /^int i = FastColor.ARGB32.colorFromFloat(h, 1.0f, 1.0f, 1.0f);
         ^///?} else {
        int i = ARGB.colorFromFloat(h, 1.0F, 1.0F, 1.0F);
        //?}

        //? if <= 1.21.2 {
        /^PoseStack poseStack = new PoseStack();
         ^///?} else {
        poseStack.pushPose();
        //?}

        poseStack.mulPose(camera.rotation());
        //? if <= 1.20.5 {
        /^poseStack.mulPose(Axis.XP.rotationDegrees(-60.0f + 150.0f * g));
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        ^///?} else {
        poseStack.mulPose(Axis.XP.rotationDegrees(60.0f - 150.0f * g));
        poseStack.scale(1.0f, -1.0f, -1.0f);
        //?}
        poseStack.translate(0.0f, 0, 1f);

        //? if <= 1.21.2 {
        /^MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
         ^///?}
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.renderType);
        //? if <= 1.20.5 {
        /^this.model.renderToBuffer(poseStack, vertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, h);
         ^///?} else {
        this.model.renderToBuffer(poseStack, vertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY, i);
        //?}

        //? if <= 1.21.2 {
        /^bufferSource.endBatch();
         ^///?} else {
        poseStack.popPose();
        //?}
    }

    //? if >= 1.21.4 {
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
    }
    //?}

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                       double d, double e, double f, double g, double h, double i) {
            return new TriviaSpiritParticle(clientLevel, d, e, f);
        }
    }
}
*///?} else {

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBotModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
//? if <= 1.21.9 {
/*import net.minecraft.client.renderer.RenderType;
*///?} else {
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?}

public class TriviaSpiritParticle extends Particle {
    protected final TriviaSpiritParticleModel model;
    protected final RenderType renderType;

    public static final ParticleRenderType GROUP = new ParticleRenderType("TRIVIA_SPIRITS");

    private TriviaSpiritParticle(final ClientLevel level, final double x, final double y, final double z) {
        super(level, x, y, z);
        TriviaBotModel particleModel = new TriviaBotModel(Minecraft.getInstance().getEntityModels().bakeLayer(TriviaBotModel.TRIVIA_BOT), true);
        this.model = new TriviaSpiritParticleModel(particleModel.root());

        //? if <= 1.21.9 {
        /*this.renderType = RenderType.entityTranslucent(TriviaBot.SANTABOT_TEXTURE);
        *///?} else {
        this.renderType = RenderTypes.entityTranslucent(TriviaBot.SANTABOT_TEXTURE);
        //?}
        this.gravity = 0.0F;
        this.lifetime = 80;
    }

    @Override
    public ParticleRenderType getGroup() {
        return GROUP;
    }

    public class TriviaSpiritParticleModel extends Model<Unit> {
        public TriviaSpiritParticleModel(ModelPart modelPart) {
            //? if <= 1.21.9 {
            /*super(modelPart, RenderType::entityCutoutNoCull);
            *///?} else if <= 1.21.11 {
            super(modelPart, RenderTypes::entityCutoutNoCull);
            //?} else {
            /*super(modelPart, RenderTypes::entityCutout);
            *///?}
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(
                final SimpleParticleType options,
                final ClientLevel level,
                final double x,
                final double y,
                final double z,
                final double xAux,
                final double yAux,
                final double zAux,
                final RandomSource random
        ) {
            return new TriviaSpiritParticle(level, x, y, z);
        }
    }
}
//?}