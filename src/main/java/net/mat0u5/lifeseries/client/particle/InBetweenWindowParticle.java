package net.mat0u5.lifeseries.client.particle;

import net.mat0u5.lifeseries.client.features.inbetween.InBetweenWindowsClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.Vec3;

//? if <= 1.21.6 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenWindowsClient.WindowMesh;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
*///?}
//? if >= 1.21.4 <= 1.21.6 {
//import com.mojang.blaze3d.vertex.PoseStack;
//?}
//? if <= 1.21.2 {
//import net.minecraft.client.Minecraft;
//?}

// Renders one In-Between window, it lives as long as the window is shown. It's a particle, since those have a custom render path on every version.
public class InBetweenWindowParticle extends Particle {
	//? if >= 1.21.9 <= 26.1 {
	//public static final ParticleRenderType GROUP = new ParticleRenderType("LIFESERIES_IN_BETWEEN_WINDOWS");
	//?} else if >= 26.2{
	public static final ParticleRenderType GROUP = new ParticleRenderType("LIFESERIES_IN_BETWEEN_WINDOWS", "IBW");
	//?}

	public final InBetweenWindowsClient.ClientWindow window;

	public InBetweenWindowParticle(ClientLevel level, InBetweenWindowsClient.ClientWindow window) {
		super(level, window.getBoundingBox().getCenter().x, window.getBoundingBox().getCenter().y, window.getBoundingBox().getCenter().z);
		this.window = window;
		this.setBoundingBox(window.getBoundingBox());
	}

	@Override
	public void tick() {
		if (!window.isActive()) remove();
	}

	public static Vec3 getCameraPos(Camera camera) {
		//? if <= 1.21.9 {
		//return camera.getPosition();
		//?} else {
		return camera.position();
		//?}
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
		WindowMesh mesh = window.buildMesh(getCameraPos(camera));
		if (mesh == null) return;
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		Matrix4f pose = new Matrix4f();
		RenderType imageType = InBetweenWindowsClient.imageRenderType(mesh.texture);
		InBetweenWindowsClient.renderImage(pose, bufferSource.getBuffer(imageType), mesh);
		bufferSource.endBatch(imageType);
	}
	*///?} else if <= 1.21.6 {
	/*@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTick) {
	}

	@Override
	public void renderCustom(PoseStack poseStack, MultiBufferSource bufferSource, Camera camera, float partialTick) {
		WindowMesh mesh = window.buildMesh(getCameraPos(camera));
		if (mesh == null) return;
		Matrix4f pose = poseStack.last().pose();
		InBetweenWindowsClient.renderImage(pose, bufferSource.getBuffer(InBetweenWindowsClient.imageRenderType(mesh.texture)), mesh);
	}
	*///?}
	// ^ Handled in the particle group in
}
