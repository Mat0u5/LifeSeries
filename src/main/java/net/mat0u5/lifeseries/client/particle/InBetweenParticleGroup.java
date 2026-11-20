package net.mat0u5.lifeseries.client.particle;

//? if <= 1.21.6 {
/*public class InBetweenParticleGroup {
}
*///?} else {

import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InBetweenParticleGroup extends ParticleGroup<InBetweenParticle> {
	public InBetweenParticleGroup(final ParticleEngine engine) {
		super(engine);
	}

	@Override
	public @NotNull ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
		List<InBetweenMesh> meshes = new ArrayList<>();
		for (InBetweenParticle particle : this.particles) {
			if (frustum.isVisible(particle.getBoundingBox())) meshes.addAll(particle.buildMeshes(camera, partialTickTime));
		}
		return new State(meshes);
	}

	private record State(List<InBetweenMesh> meshes) implements ParticleGroupRenderState {
		@Override
		public void submit(SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
			PoseStack poseStack = new PoseStack();
			for (InBetweenMesh mesh : meshes) {
				submitNodeCollector.order(mesh.order).submitCustomGeometry(poseStack, mesh.renderType, (pose, consumer) -> mesh.render(pose.pose(), consumer));
			}
		}
	}
}
//?}
