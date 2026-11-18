package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InBetweenTriviaPath {
	private static final double PLAYER_STOP_DISTANCE = 3;

	@Nullable
	public static List<Vec3> getPath(TriviaBot bot) {
		ServerPlayer player = bot.serverData.getBoundPlayer();
		if (player == null) return null;
		InBetweenInstance instance = InBetweenManager.getInstance(player);
		if (instance == null) return null;
		List<Vec3> path = instance.inBetweenBuilder.pathPoints;
		return path.isEmpty() ? null : path;
	}

	@Nullable
	public static Vec3 getNextTarget(List<Vec3> path, Vec3 botPos, Vec3 playerPos) {
		if (path.size() < 2) return null;
		if (botPos.distanceTo(playerPos) < PLAYER_STOP_DISTANCE) return null;
		double botProgress = getProgress(path, botPos);
		double remaining = getProgress(path, playerPos) - botProgress;
		if (Math.abs(remaining) < 2.5) return null;
		return getPointAt(path, botProgress + Math.signum(remaining) * 2);
	}

	@Nullable
	public static Vec3 getClosestPathPoint(List<Vec3> path, Vec3 pos) {
		Vec3 closest = null;
		double closestDistance = Double.MAX_VALUE;
		for (Vec3 point : path) {
			double distance = point.distanceToSqr(pos);
			if (distance < closestDistance) {
				closestDistance = distance;
				closest = point;
			}
		}
		return closest;
	}

	private static double getProgress(List<Vec3> path, Vec3 pos) {
		double closestDistance = Double.MAX_VALUE;
		double closestProgress = 0;
		double walked = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			Vec3 start = path.get(i);
			Vec3 segment = path.get(i + 1).subtract(start);
			double lengthSqr = segment.lengthSqr();
			double t = lengthSqr < 1.0E-6 ? 0 : Mth.clamp(pos.subtract(start).dot(segment) / lengthSqr, 0, 1);
			double distance = start.add(segment.scale(t)).distanceToSqr(pos);
			double length = Math.sqrt(lengthSqr);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestProgress = walked + t * length;
			}
			walked += length;
		}
		return closestProgress;
	}

	private static Vec3 getPointAt(List<Vec3> path, double progress) {
		if (progress <= 0) return path.get(0);
		double walked = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			Vec3 start = path.get(i);
			Vec3 end = path.get(i + 1);
			double length = start.distanceTo(end);
			if (walked + length >= progress) {
				return length < 1.0E-6 ? start : start.lerp(end, (progress - walked) / length);
			}
			walked += length;
		}
		return path.get(path.size() - 1);
	}
}
