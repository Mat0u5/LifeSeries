package net.mat0u5.lifeseries.client.particle;

//? if < 1.21.9 {
/*public class InBetweenWindowParticleGroup {
}
*///?} else {

import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenWindowsClient;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InBetweenWindowParticleGroup extends ParticleGroup<InBetweenWindowParticle> {
	public InBetweenWindowParticleGroup(final ParticleEngine engine) {
		super(engine);
	}

	@Override
	public @NotNull ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
		Vec3 cameraPos = InBetweenWindowParticle.getCameraPos(camera);
		List<InBetweenWindowsClient.WindowMesh> meshes = new ArrayList<>();
		for (InBetweenWindowParticle particle : this.particles) {
			if (!frustum.isVisible(particle.getBoundingBox())) continue;
			InBetweenWindowsClient.WindowMesh mesh = particle.window.buildMesh(cameraPos);
			if (mesh != null) meshes.add(mesh);
		}
		return new State(meshes);
	}

	private record State(List<InBetweenWindowsClient.WindowMesh> meshes) implements ParticleGroupRenderState {
		@Override
		public void submit(SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
			PoseStack poseStack = new PoseStack();
			for (InBetweenWindowsClient.WindowMesh mesh : meshes) {
				submitNodeCollector.submitCustomGeometry(
						poseStack,
						InBetweenWindowsClient.imageRenderType(mesh.texture),
						(pose, consumer) -> InBetweenWindowsClient.renderImage(pose.pose(), consumer, mesh)
				);
			}
		}
	}
}
//?}
