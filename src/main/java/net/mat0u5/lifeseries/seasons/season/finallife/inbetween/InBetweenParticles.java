package net.mat0u5.lifeseries.seasons.season.finallife.inbetween;

import net.mat0u5.lifeseries.network.LifeSeriesNetworkHandlerServer;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InBetweenParticles {
	private static final double RING_RADIUS = 1.5;
	private static final Random random = new Random();

	private final InBetweenInstance inBetween;
	public InBetweenParticles(InBetweenInstance inBetween) {
		this.inBetween = inBetween;
	}

	public void tick(ServerPlayer player) {
		spawnPathParticles(player);
		spawnAmbientParticles(player);
		spawnRingParticles(player);
	}

	@Nullable
	private Vec3 getPointNearPath(Vec3 playerPos, double maxDistance) {
		if (inBetween.pathPoints.size() < 2) return null;
		double[] distanceMap = new double[inBetween.pathPoints.size()];
		for (int i = 1; i < inBetween.pathPoints.size(); i++) {
			distanceMap[i] = distanceMap[i - 1] + inBetween.pathPoints.get(i).distanceTo(inBetween.pathPoints.get(i - 1));
		}
		double totalLength = distanceMap[distanceMap.length - 1];
		if (totalLength <= 0) return null;

		double maxDistanceSqr = maxDistance * maxDistance;
		for (int attempt = 0; attempt < 6; attempt++) {
			double randomDistance = random.nextDouble() * totalLength;
			int segmentNumber = 1;
			while (segmentNumber < distanceMap.length-1 && distanceMap[segmentNumber] < randomDistance) {
				segmentNumber++;
			}

			double segmentLength = distanceMap[segmentNumber] - distanceMap[segmentNumber -1];
			double segmentProgress = segmentLength <= 0 ? 0 : (randomDistance - distanceMap[segmentNumber -1]) / segmentLength;
			Vec3 randomPoint = inBetween.pathPoints.get(segmentNumber - 1).lerp(inBetween.pathPoints.get(segmentNumber), segmentProgress);
			if (randomPoint.distanceToSqr(playerPos) <= maxDistanceSqr) {
				return randomPoint;
			}
		}
		return null;
	}

	private void spawnPathParticles(ServerPlayer player) {
		ParticleOptions particle = random.nextDouble() < 0.3 ? ParticleRegistry.IN_BETWEEN_PATH : ParticleRegistry.IN_BETWEEN_PATH_SMALL; // 30% large particles, 70% small.
		int countPerTick = 2;
		for (int i = 0; i < countPerTick; i++) {
			Vec3 point = getPointNearPath(player.position(), 50);
			if (point == null) return;
			double x = point.x + OtherUtils.clamp(random.nextGaussian(), -2, 2) * 0.4;
			double y = point.y + random.nextDouble() * 0.1;
			double z = point.z + OtherUtils.clamp(random.nextGaussian(), -2, 2) * 0.4;
			InBetweenParticles.sendParticle(player, particle, true, x, y, z, 1, 0, 0, 0, 0);
		}
	}

	private void spawnAmbientParticles(ServerPlayer player) {
		ParticleOptions particle = InBetweenManager.getAmbientParticles(inBetween.deathNumber);
		if (particle == null) return;
		int rangeXZ = 100;
		int rangeY = 50;
		for (int i = 0; i < 12; i++) {
			double x = player.getX() + (random.nextDouble() - 0.5) * rangeXZ;
			double y = player.getY() + (random.nextDouble() - 0.3) * rangeY;
			double z = player.getZ() + (random.nextDouble() - 0.5) * rangeXZ;
			InBetweenParticles.sendParticle(player, particle, false, x, y, z, 1, 0, 0, 0, 1.0f);
		}
	}

	private void spawnRingParticles(ServerPlayer player) {
		ParticleOptions ring = ParticleTypes.END_ROD;
		ParticleOptions accent = ParticleRegistry.IN_BETWEEN_RING_ACCENT;
		List<Vec3> centers = inBetween.getChoiceCenters();
		int ringCount = Math.min(inBetween.choices.size(), centers.size());
		//Calculate the middle position, to decide which way the choice rings should spin.
		double middleXPosition = 0;
		for (int i = 0; i < ringCount; i++) {
			middleXPosition += centers.get(i).x / ringCount;
		}
		for (int i = 0; i < ringCount; i++) {
			Vec3 center = centers.get(i);
			if (center.distanceToSqr(player.position()) > 64 * 64) continue;
			boolean rightSide = center.x > middleXPosition + 0.01;
			double baseAngle = inBetween.age * (1 / 36.0) * (Math.PI * 2); // 36 ticks per revolution
			if (!rightSide) {
				baseAngle = Math.PI - baseAngle;
			}

			/*
			int ageInterpolationCount = 2;
			for (int ageInterpolation = 0; ageInterpolation < ageInterpolationCount; ageInterpolation++) {
				double interpolatedAge = age + (1.0 / ageInterpolationCount)*ageInterpolation;
			}
			*/

			if (ring != null) {
				double angle = baseAngle;
				sendParticle(player, ring, true,
						center.x + Math.cos(angle) * RING_RADIUS, center.y + 0.05, center.z + Math.sin(angle) * RING_RADIUS,
						1, 0, 0, 0, 0);
			}
			if (accent != null) {
				double angle = baseAngle/2;
				sendParticle(player, accent, true,
						center.x + Math.cos(angle) * RING_RADIUS, center.y + 0.85, center.z + Math.sin(angle) * RING_RADIUS,
						1, 0, 0, 0, 0);
				double angle2 = angle + Math.PI;
				sendParticle(player, accent, true,
						center.x + Math.cos(angle2) * RING_RADIUS, center.y + 0.85, center.z + Math.sin(angle2) * RING_RADIUS,
						1, 0, 0, 0, 0);
			}
		}
	}

	public static boolean canSeeParticle(ServerPlayer player, ParticleOptions options) {
		Identifier id = BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType());
		if (id == null) return false;
		if (id.getNamespace().equals("minecraft")) return true;
		return LifeSeriesNetworkHandlerServer.wasHandshakeSuccessful(player);
	}

	public static void sendParticle(ServerPlayer player, ParticleOptions options, boolean force, double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
		if (options == null || !canSeeParticle(player, options)) return;
		ServerLevel level = ((IPlayer) player).ls$getServerLevel();
		//? if <= 1.21.2 {
		//level.sendParticles(player, options, force, x, y, z, count, dx, dy, dz, speed);
		//?} else {
		level.sendParticles(player, options, force, false, x, y, z, count, dx, dy, dz, speed);
		//?}
	}
}
