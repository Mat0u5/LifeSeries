package net.mat0u5.lifeseries.client.features.inbetween;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

//? if >= 1.21.9
import net.minecraft.client.entity.ClientAvatarState;

public record InBetweenCameraView(Vec3 position, Vector3f forward, Vector3f up, Vector3f left, Vec3 eye, float partialTick) {
	public static InBetweenCameraView of(Camera camera, float partialTick) {
		//? if <= 1.21.9 {
		/*Vec3 position = camera.getPosition();
		Vector3f forward = new Vector3f(camera.getLookVector());
		Vector3f up = new Vector3f(camera.getUpVector());
		Vector3f left = new Vector3f(camera.getLeftVector());
		*///?} else {
		Vec3 position = camera.position();
		Vector3f forward = new Vector3f(camera.forwardVector());
		Vector3f up = new Vector3f(camera.upVector());
		Vector3f left = new Vector3f(camera.leftVector());
		//?}
		Vector3f bob = bobbedEye(partialTick);
		Vec3 eye = bob == null ? position : position.add(
				-left.x * bob.x + up.x * bob.y - forward.x * bob.z,
				-left.y * bob.x + up.y * bob.y - forward.y * bob.z,
				-left.z * bob.x + up.z * bob.y - forward.z * bob.z
		);
		return new InBetweenCameraView(position, forward, up, left, eye, partialTick);
	}

	private static Vector3f bobbedEye(float partialTick) {
		Minecraft client = Minecraft.getInstance();
		if (!client.options.bobView().get()) return null;
		if (!(client.getCameraEntity() instanceof AbstractClientPlayer player)) return null;
		//? if <= 1.21.6 {
		/*float walk = -(player.walkDist + (player.walkDist - player.walkDistO) * partialTick);
		float bob = Mth.lerp(partialTick, player.oBob, player.bob);
		*///?} else {
		ClientAvatarState state = player.avatarState();
		float walk = state.getBackwardsInterpolatedWalkDistance(partialTick);
		float bob = state.getInterpolatedBob(partialTick);
		//?}
		if (bob == 0) return null;
		Matrix4f transform = new Matrix4f()
				.translate(Mth.sin(walk * (float) Math.PI) * bob *0.5F, -Math.abs(Mth.cos(walk * (float) Math.PI) * bob),0.0F)
				.rotateZ((float) Math.toRadians(Mth.sin(walk * (float) Math.PI) * bob * 3.0F))
				.rotateX((float) Math.toRadians(Math.abs(Mth.cos(walk * (float) Math.PI - 0.2F) * bob)*5.0F));
		return transform.invert().transformPosition(new Vector3f());
	}
}
