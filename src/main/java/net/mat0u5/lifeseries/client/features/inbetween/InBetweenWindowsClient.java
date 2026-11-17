package net.mat0u5.lifeseries.client.features.inbetween;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenMemorySound;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenWindowSound;
import net.mat0u5.lifeseries.client.particle.InBetweenWindowParticle;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window.InBetweenMemoryManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//? if >= 1.21.11
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class InBetweenWindowsClient {
	private static final double PARALLAX_DEPTH = 7; // How far the picture is
	private static final double EDGE_FADE = 0.05; // Percentage of the image being faded
	private static final int FADE_STEPS = 5;
	private static final double PICTURE_ANGLE = 45;
	private static final long AFTER_DOWNLOAD_FADE_IN_MILLIS = 1500;
	private static final double SOUND_DISTANCE = 10;
	private static final int SOUND_START_DELAY = 100; // First time before the sound starts
	private static final int SOUND_REPEAT_DELAY = 100; // Before the sound repeats
	private static final double WIND_DISTANCE = 20;
	private static final double WIND_VOLUME = 0.5;
	private static final Identifier WIND_SOUND = IdentifierHelper.lifeseries("finallife_window_wind");
	private static final int FULL_BRIGHT = 0xF000F0;

	private static final List<ClientWindow> windows = new ArrayList<>();
	@Nullable
	private static InBetweenWindowSound wind;
	private static int windCooldown = 0;

	public static void onWindowsPacket(List<String> payload) {
		setWindows(payload);
	}

	public static void setWindows(List<String> entries) {
		clear();
		for (String entry : entries) {
			String[] split = entry.split(";");
			if (split.length != 8) {
				LifeSeries.LOGGER.warn("[In-Between] Invalid window entry '{}'", entry);
				continue;
			}
			try {
				Vec3 windowPos = new Vec3(
						Double.parseDouble(split[0]),
						Double.parseDouble(split[1]),
						Double.parseDouble(split[2])
				);
				float yaw = Float.parseFloat(split[3]);
				String imageHash = split[4];
				String soundHash = InBetweenMemoryManager.isValidHash(split[5]) ? split[5] : null;
				float cellSize = Float.parseFloat(split[6]);
				String[] shape = split[7].split("/");
				if (!InBetweenMemoryManager.isValidHash(imageHash) || cellSize <= 0 || cellSize > 4 || !split[7].contains("#")) continue;
				windows.add(new ClientWindow(windowPos, yaw, imageHash, soundHash, shape, cellSize));
			} catch (Exception e) {
				LifeSeries.LOGGER.warn("[In-Between] Failed parsing window entry {}", entry);
			}
		}
	}

	public static void tick(LocalPlayer player) {
		if (windows.isEmpty()) return;
		Minecraft client = Minecraft.getInstance();
		ClientLevel level = client.level;
		if (level == null || !InBetweenManager.isInInBetween(player)) {
			hideWindows();
			return;
		}
		for (ClientWindow window : windows) {
			window.tick(client, level, player);
		}
		tickWind(client);
	}

	private static void tickWind(Minecraft client) {
		if (wind != null && client.getSoundManager().isActive(wind)) return;
		if (windCooldown <= 20) {
			windCooldown++;
			return;
		}
		windCooldown = 0;
		wind = new InBetweenWindowSound(WIND_SOUND, SoundSource.AMBIENT, true, InBetweenWindowsClient::getWindVolume);
		client.getSoundManager().play(wind);
	}

	private static double getWindVolume() {
		double volume = 0;
		for (ClientWindow window : windows) {
			volume = Math.max(volume, window.currentWindVolume);
		}
		return WIND_VOLUME * volume;
	}

	private static boolean isMemoryPlaying(SoundManager soundManager) {
		for (ClientWindow window : windows) {
			if (window.sound != null && window.currentSoundVolume > 0 && soundManager.isActive(window.sound)) return true;
		}
		return false;
	}

	private static void hideWindows() {
		for (ClientWindow window : windows) {
			window.hide();
		}
		if (wind != null) {
			Minecraft.getInstance().getSoundManager().stop(wind);
			wind = null;
		}
	}

	public static void clear() {
		hideWindows();
		windows.clear();
	}

	public static void onParticlesCleared() {
		for (ClientWindow window : windows) {
			window.particle = null;
		}
	}

	public static RenderType imageRenderType(Identifier texture) {
		//? if <= 1.21.9 {
		//return RenderType.text(texture);
		//?} else {
		return RenderTypes.text(texture);
		//?}
	}

	public static void renderImage(Matrix4f pose, VertexConsumer consumer, WindowMesh mesh) {
		float[] data = mesh.vertices;
		for (int i = 0; i < mesh.count; i++) {
			int y = i * WindowMesh.VARS;
			int brightness = Math.round(data[y + 5] * 255);
			//? if <= 1.20.5 {
			//consumer.vertex(pose, data[y], data[y + 1], data[y + 2]).color(brightness, brightness, brightness, 255).uv(data[y + 3], data[y + 4]).uv2(FULL_BRIGHT).endVertex();
			//?} else {
			consumer.addVertex(pose, data[y], data[y + 1], data[y + 2]).setColor(brightness, brightness, brightness, 255).setUv(data[y + 3], data[y + 4]).setLight(FULL_BRIGHT);
			//?}
		}
	}

	public static class WindowMesh {
		private static final int VARS = 6; // x, y, z, u, v, brightness
		public final Identifier texture;
		private float[] vertices = new float[VARS * 1024];
		private int count = 0;

		private WindowMesh(Identifier texture) {
			this.texture = texture;
		}

		private void add(double x, double y, double z, double u, double v, double brightness) {
			if ((count + 1) * VARS > vertices.length) vertices = Arrays.copyOf(vertices, vertices.length * 2);
			int i = count * VARS;
			vertices[i] = (float) x;
			vertices[i + 1] = (float) y;
			vertices[i + 2] = (float) z;
			vertices[i + 3] = (float) u;
			vertices[i + 4] = (float) v;
			vertices[i + 5] = (float) brightness;
			count++;
		}
	}

	private record Axis(double[] splits, double[] texture, double[] brightness) {}

	public static class ClientWindow {
		private final Vec3 center;
		private final Vec3 normal; // Points to the side of the viewer
		private final Vec3 right;
		private final String imageHash;
		@Nullable
		private final String soundHash;
		private final float cellSize;
		private final boolean[][] openCells; // [row][column]
		private final float width;
		private final float height;
		private final AABB boundingBox;
		private final Vec3 soundSource;

		@Nullable
		private InBetweenWindowParticle particle;
		@Nullable
		private ClientLevel particleLevel;
		@Nullable
		private InBetweenMemorySound sound;
		private int soundDelay = SOUND_START_DELAY;
		private double currentSoundVolume = 0;
		private double currentWindVolume = 0;
		private boolean active = true;
		private long lastImageShownTime = -1;
		private double appear = 1;

		private ClientWindow(Vec3 pos, float yaw, String imageHash, @Nullable String soundHash, String[] shape, float cellSize) {
			double yawRad = Math.toRadians(yaw);
			this.normal = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
			this.right = new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
			this.center = pos.add(normal.scale(0.01));
			this.imageHash = imageHash;
			this.soundHash = soundHash;
			this.cellSize = cellSize;

			int columns = 0;
			for (String row : shape) columns = Math.max(columns, row.length());
			this.openCells = new boolean[shape.length][columns];
			for (int row = 0; row < shape.length; row++) {
				for (int column = 0; column < shape[row].length(); column++) {
					openCells[row][column] = shape[row].charAt(column) == '#';
				}
			}
			this.width = columns * cellSize;
			this.height = shape.length * cellSize;
			this.boundingBox = new AABB(center, center).inflate(Math.max(width, height) / 2 + 1);
			this.soundSource = center.add(normal.scale(3));
			if (InBetweenMemoryCache.getImage(imageHash) != null) lastImageShownTime = 0;
		}

		public boolean isActive() {
			return active;
		}

		public AABB getBoundingBox() {
			return boundingBox;
		}

		private void tick(Minecraft client, ClientLevel level, LocalPlayer player) {
			active = true;
			Vec3 eyes = player.getEyePosition();
			currentSoundVolume = getVolume(eyes, SOUND_DISTANCE);
			currentWindVolume = getVolume(eyes, WIND_DISTANCE);
			if (particle == null || !particle.isAlive() || particleLevel != level) {
				if (particle != null) particle.remove();
				particle = new InBetweenWindowParticle(level, this);
				particleLevel = level;
				client.particleEngine.add(particle);
			}
			tickSound(client, player);
		}

		// The memory sound starts after the player has been close to the front of the window for SOUND_DELAY ticks,
		// then it repeats REPEAT_DELAY ticks after it ended. The echo is part of the sound, so it counts too.
		// Only one memory sound starts at a time, so it waits until the other one ends or is too far away to hear. They never get cut off.
		private void tickSound(Minecraft client, LocalPlayer player) {
			if (soundHash == null) return;
			SoundManager soundManager = client.getSoundManager();
			if (sound != null) {
				if (soundManager.isActive(sound)) return;
				sound = null;
				soundDelay = SOUND_REPEAT_DELAY;
			}
			if (currentSoundVolume <= 0 || player.getEyePosition().subtract(center).dot(normal) <= 0) {
				soundDelay = Math.max(soundDelay, SOUND_START_DELAY);
				return;
			}
			if (soundDelay > 0) {
				soundDelay--;
				return;
			}
			if (!InBetweenMemoryCache.isAvailable(soundHash)) return; // Still downloading
			if (isMemoryPlaying(soundManager)) return;
			sound = new InBetweenMemorySound(soundHash, () -> currentSoundVolume);
			soundManager.play(sound);
		}

		private double getVolume(Vec3 pos, double range) {
			double distance = pos.distanceTo(soundSource) / range;
			return Math.max(0, 1 - distance * distance);
		}

		private void hide() {
			active = false;
			currentSoundVolume = 0;
			currentWindVolume = 0;
			if (particle != null) {
				particle.remove();
				particle = null;
			}
			particleLevel = null;
			if (sound != null) {
				Minecraft.getInstance().getSoundManager().stop(sound);
				sound = null;
			}
		}

		@Nullable
		public WindowMesh buildMesh(Vec3 camera) {
			Vec3 toCamera = camera.subtract(center);
			double distance = toCamera.dot(normal);
			if (distance <= 0.001) return null;
			InBetweenMemoryCache.LoadedImage image = InBetweenMemoryCache.getImage(imageHash);
			if (image == null) return null;
			long now = System.currentTimeMillis();
			if (lastImageShownTime < 0) lastImageShownTime = now;
			double progress = lastImageShownTime == 0 ? 1 : Math.min(1, (now - lastImageShownTime) / (double) AFTER_DOWNLOAD_FADE_IN_MILLIS);
			appear = progress * progress * (3 - 2 * progress);

			double aspect = (double) image.width() / image.height();
			double reach = 2 * PARALLAX_DEPTH * Math.tan(Math.toRadians(PICTURE_ANGLE));
			double pictureWidth = Math.max(width + reach, (height + reach) * aspect);
			double pictureHeight = pictureWidth / aspect;
			double fadeSize = EDGE_FADE * pictureHeight;

			Axis columns = getAxis(width, toCamera.dot(right), distance, pictureWidth, fadeSize, image.width());
			Axis rows = getAxis(height, toCamera.y, distance, pictureHeight, fadeSize, image.height());

			WindowMesh mesh = new WindowMesh(image.texture());
			for (int row = 0; row < rows.splits().length - 1; row++) {
				int cellRow = (int) ((height / 2 - (rows.splits()[row] + rows.splits()[row + 1]) / 2) / cellSize);
				for (int column = 0; column < columns.splits().length - 1; column++) {
					int cellColumn = (int) (((columns.splits()[column] + columns.splits()[column + 1]) / 2 + width / 2) / cellSize);
					if (cellRow < 0 || cellRow >= openCells.length || cellColumn < 0 || cellColumn >= openCells[cellRow].length) continue;
					if (!openCells[cellRow][cellColumn]) continue;
					addCorner(mesh, camera, columns, column, rows, row);
					addCorner(mesh, camera, columns, column + 1, rows, row);
					addCorner(mesh, camera, columns, column + 1, rows, row + 1);
					addCorner(mesh, camera, columns, column, rows, row + 1);
				}
			}
			return mesh;
		}

		private void addCorner(WindowMesh mesh, Vec3 camera, Axis columns, int column, Axis rows, int row) {
			double x = columns.splits()[column];
			double y = rows.splits()[row];
			double brightness = columns.brightness()[column] * rows.brightness()[row] * appear;
			mesh.add(center.x + right.x * x - camera.x, center.y + y - camera.y, center.z + right.z * x - camera.z, columns.texture()[column], 1 - rows.texture()[row], brightness);
		}

		private Axis getAxis(double size, double camera, double distance, double pictureSize, double fadeSize, int pixels) {
			List<Double> splitList = new ArrayList<>();
			int cells = (int) Math.round(size / cellSize);
			for (int cell = 0; cell < cells; cell++) {
				splitList.add(-size / 2 + cell * cellSize);
			}
			splitList.add(size / 2);

			double scale = 1 + PARALLAX_DEPTH / distance;
			for (int step = 0; step <= FADE_STEPS; step++) {
				double picturePos = pictureSize / 2 - fadeSize * step / FADE_STEPS;
				double minSplit = (-picturePos + camera * (scale - 1)) / scale;
				double maxSplit = (picturePos + camera * (scale - 1)) / scale;
				if (Math.abs(minSplit) < size / 2) splitList.add(minSplit);
				if (Math.abs(maxSplit) < size / 2) splitList.add(maxSplit);
			}

			double[] splits = new double[splitList.size()];
			for (int i = 0; i < splits.length; i++) {
				splits[i] = splitList.get(i);
			}
			Arrays.sort(splits);

			double[] texture = new double[splits.length];
			double[] brightness = new double[splits.length];
			for (int i = 0; i < splits.length; i++) {
				double hit = splits[i] * scale - camera * (scale - 1);
				double picturePos = Math.max(-0.5, Math.min(0.5, hit / pictureSize));
				texture[i] = 0.5 + picturePos * (1 - 1.0 / pixels);
				double fade = Math.max(0, Math.min(1, (pictureSize / 2 - Math.abs(hit)) / fadeSize));
				brightness[i] = fade * fade * (3 - 2 * fade);
			}
			return new Axis(splits, texture, brightness);
		}
	}
}
