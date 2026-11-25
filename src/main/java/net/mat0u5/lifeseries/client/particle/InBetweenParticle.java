package net.mat0u5.lifeseries.client.particle;

import net.mat0u5.lifeseries.client.features.inbetween.InBetweenCameraView;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

//? if <= 1.21.6 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? if <= 1.21.2 {
/*import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
*///?} else if <= 1.21.6 {
//import com.mojang.blaze3d.vertex.PoseStack;
//?}

public class InBetweenParticle extends Particle {
	//? if >= 1.21.9 <= 26.1 {
	//public static final ParticleRenderType GROUP = new ParticleRenderType("LIFESERIES_IN_BETWEEN");
	//?} else if > 26.1 {
	public static final ParticleRenderType GROUP = new ParticleRenderType("LIFESERIES_IN_BETWEEN", "IB");
	//?}

	private final BooleanSupplier active;
	private final Function<InBetweenCameraView, List<InBetweenMesh>> meshes;

	public InBetweenParticle(ClientLevel level, AABB boundingBox, BooleanSupplier active, Function<InBetweenCameraView, List<InBetweenMesh>> meshes) {
		super(level, boundingBox.getCenter().x, boundingBox.getCenter().y, boundingBox.getCenter().z);
		this.active = active;
		this.meshes = meshes;
		this.setBoundingBox(boundingBox);
	}

	@Override
	public void tick() {
		if (!active.getAsBoolean()) remove();
	}

	public List<InBetweenMesh> buildMeshes(Camera camera, float partialTick) {
		return meshes.apply(InBetweenCameraView.of(camera, partialTick));
	}

	//? if <= 1.21.6 {
	/*@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}
	*///?} else {
	@Override
	public ParticleRenderType getGroup() {
		return GROUP;
	}
	//?}

	//? if <= 1.21.2 {
	/*@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTick) {
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		for (InBetweenMesh mesh : buildMeshes(camera, partialTick)) {
			if (mesh.fontText != null) {
				mesh.drawFontText(new Matrix4f(), bufferSource);
				bufferSource.endBatch();
				continue;
			}
			mesh.render(new Matrix4f(), bufferSource.getBuffer(mesh.renderType));
			bufferSource.endBatch(mesh.renderType);
		}
	}
	*///?} else if <= 1.21.6 {
	/*@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTick) {
	}

	@Override
	public void renderCustom(PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, float partialTick) {
		for (InBetweenMesh mesh : buildMeshes(camera, partialTick)) {
			if (mesh.fontText != null) mesh.drawFontText(poseStack.last().pose(), bufferSource);
			else mesh.render(poseStack.last().pose(), bufferSource.getBuffer(mesh.renderType));
		}
	}
	*///?}
	// ^ Handled in the particle group in newer versions
}
