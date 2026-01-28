package net.mat0u5.lifeseries.particle;

//? if < 1.21.9 {
/*public class TriviaSpiritParticleGroup {
}
*///?} else {

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mat0u5.lifeseries.mixin.client.ParticleAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//? if <= 1.21.9 {
/*import net.minecraft.client.renderer.RenderType;
 *///?} else {
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

public class TriviaSpiritParticleGroup extends ParticleGroup<TriviaSpiritParticle> {
    public TriviaSpiritParticleGroup(final ParticleEngine engine) {
        super(engine);
    }

    @Override
    public @NotNull ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
        return new TriviaSpiritParticleGroup.State(
                this.particles
                        .stream()
                        .map(particle -> TriviaSpiritParticleGroup.TriviaSpiritParticleRenderState.fromParticle(particle, camera, partialTickTime))
                        .toList()
        );
    }

    private record TriviaSpiritParticleRenderState(Model<Unit> model, PoseStack poseStack, RenderType renderType, int color) {
        public static TriviaSpiritParticleGroup.TriviaSpiritParticleRenderState fromParticle(
                final TriviaSpiritParticle particle, final Camera camera, final float partialTickTime
        ) {
            int age = 0;
            if (particle instanceof ParticleAccessor accessor) {
                age = accessor.ls$getAge();
            }

            float ageScale = (age + partialTickTime) / particle.getLifetime();
            float alpha = 0.05F + 0.5F * Mth.sin(ageScale * (float) Math.PI);
            int color = ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            poseStack.mulPose(camera.rotation());
            poseStack.mulPose(Axis.XP.rotationDegrees(60.0F - 150.0F * ageScale));
            poseStack.scale(1.0f, -1.0f, -1.0f);
            poseStack.translate(0.0f, 0, 1f);
            return new TriviaSpiritParticleRenderState(particle.model, poseStack, particle.renderType, color);
        }
    }

    private record State(List<TriviaSpiritParticleRenderState> states) implements ParticleGroupRenderState {
        @Override
        public void submit(SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
            for (TriviaSpiritParticleRenderState state : this.states) {
                submitNodeCollector.submitModel(
                        state.model, Unit.INSTANCE, state.poseStack, state.renderType, 15728880, OverlayTexture.NO_OVERLAY, state.color, null, 0, null
                );
            }
        }
    }
}
//?}

