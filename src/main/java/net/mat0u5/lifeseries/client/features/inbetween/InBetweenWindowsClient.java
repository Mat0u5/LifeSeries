package net.mat0u5.lifeseries.client.features.inbetween;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenMemorySound;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenWindowSound;
import net.mat0u5.lifeseries.client.particle.InBetweenParticle;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
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
import java.util.List;

//? if >= 1.21.11
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class InBetweenWindowsClient {
	private static final double PICTURE_DEPTH = 3.8;
	private static final double PICTURE_ANGLE = 50;
	private static final double PICTURE_SQUEEZE_START = 0.6;
	private static final int PICTURE_SUBDIVISIONS = 3;
	private static final double DARK_START = 20;
	private static final double DARK_END = 25;
	private static final int MIN_PICTURE_ALPHA = 26;
	private static final long AFTER_DOWNLOAD_FADE_IN_MILLIS = 1500;
	private static final double SOUND_DISTANCE = 10;
	private static final int SOUND_START_DELAY = 100;
	private static final int SOUND_REPEAT_DELAY = 100;
	private static final double WIND_DISTANCE = 20;
	private static final double WIND_VOLUME = 0.5;
	private static final Identifier WIND_SOUND = IdentifierHelper.lifeseries("finallife_window_wind");

	private static final double RAY_SOURCE_DEPTH = 30;
	private static final double RAY_FADE_START = 8;
	private static final double RAY_FAR_START = 14;
	private static final double RAY_LENGTH = 16;
	private static final int RAY_TEXELS_PER_CELL = 16;
	private static final double RAY_COLOR_BLUR = 0.75;
	private static final double RAY_BEAM_DETAIL = 1.6;
	private static final double RAY_BEAM_DARKEST = 0.18;
	private static final int RAY_MARGIN_CELLS = 1;
	private static final double RAY_EDGE_SOFTNESS = 0.05;
	private static final double RAY_INSIDE_MARGIN = 0.75;

	private static final List<ClientWindow> windows = new ArrayList<>();
	@Nullable
	private static InBetweenWindowSound wind;
	private static int windCooldown = 0;
	private static int rayTextureCounter = 0;

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
		for (ClientWindow window : windows) {
			window.releaseRays();
		}
		windows.clear();
	}

	public static void onParticlesCleared() {
		for (ClientWindow window : windows) {
			window.particle = null;
		}
	}

	private static RenderType imageRenderType(Identifier texture) {
		//? if <= 1.21.9 {
		//return RenderType.text(texture);
		//?} else {
		return RenderTypes.text(texture);
		//?}
	}

	private record Axis(double[] splits, double[] texture) {}

	public static class ClientWindow {
		private final Vec3 center;
		private final Vec3 normal;
		private final Vec3 right;
		private final String imageHash;
		@Nullable
		private final String soundHash;
		private final float cellSize;
		private final boolean[][] openCells;
		private final float width;
		private final float height;
		private final double rayHalfWidth;
		private final double rayHalfHeight;
		private final AABB boundingBox;
		private final Vec3 soundSource;

		@Nullable
		private InBetweenParticle particle;
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

		private final Matrix4f frame = new Matrix4f();
		@Nullable
		private Identifier rayTexture;
		@Nullable
		private RenderType raysRenderType;
		private boolean rayTextureFailed = false;

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
			this.rayHalfWidth = width / 2.0 + RAY_MARGIN_CELLS * cellSize;
			this.rayHalfHeight = height / 2.0 + RAY_MARGIN_CELLS * cellSize;
			double rayReach = RAY_LENGTH * (1 + Math.max(rayHalfWidth, rayHalfHeight) / RAY_SOURCE_DEPTH);
			this.boundingBox = new AABB(center, center).inflate(Math.max(width, height) / 2 + 1 + rayReach);
			this.soundSource = center.add(normal.scale(3));
			if (InBetweenMemoryCache.getImage(imageHash) != null) lastImageShownTime = 0;
		}

		public boolean isActive() {
			return active;
		}

		private void tick(Minecraft client, ClientLevel level, LocalPlayer player) {
			active = true;
			Vec3 eyes = player.getEyePosition();
			currentSoundVolume = getVolume(eyes, SOUND_DISTANCE);
			currentWindVolume = getVolume(eyes, WIND_DISTANCE);
			if (particle == null || !particle.isAlive() || particleLevel != level) {
				if (particle != null) particle.remove();
				particle = new InBetweenParticle(level, boundingBox, this::isActive, this::buildMeshes);
				particleLevel = level;
				client.particleEngine.add(particle);
			}
			tickSound(client, player);
		}

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

		private void releaseRays() {
			if (rayTexture != null) Minecraft.getInstance().getTextureManager().release(rayTexture);
			rayTexture = null;
			raysRenderType = null;
		}

		private List<InBetweenMesh> buildMeshes(InBetweenCameraView view) {
			List<InBetweenMesh> meshes = new ArrayList<>(2);
			InBetweenMemoryCache.LoadedImage image = InBetweenMemoryCache.getImage(imageHash);
			if (image == null) return meshes;
			long now = System.currentTimeMillis();
			if (lastImageShownTime < 0) lastImageShownTime = now;
			double progress = lastImageShownTime == 0 ? 1 : Math.min(1,(now - lastImageShownTime)/(double) AFTER_DOWNLOAD_FADE_IN_MILLIS);
			appear = progress * progress * (3 - 2*progress);

			InBetweenMesh picture = buildPicture(image, view.position(), view.eye());
			if (picture != null) meshes.add(picture);
			InBetweenMesh rays = buildRays(image, view);
			if (rays != null) meshes.add(rays);
			return meshes;
		}

		@Nullable
		private InBetweenMesh buildPicture(InBetweenMemoryCache.LoadedImage image, Vec3 camera, Vec3 eye) {
			Vec3 toCamera = eye.subtract(center);
			double distance = toCamera.dot(normal);
			if (distance <= 0.001) return null;
			double dark = Math.max(0, Math.min(1, (camera.distanceTo(center) - DARK_START) / (DARK_END - DARK_START)));
			int alpha = (int) Math.round(255 * appear * (1 - dark * dark * (3 - 2 * dark)));
			if (alpha < MIN_PICTURE_ALPHA) return null;

			double aspect = (double) image.width() / image.height();
			double pictureDepth = PICTURE_DEPTH * width;
			double reach = 2 * pictureDepth * Math.tan(Math.toRadians(PICTURE_ANGLE));
			double pictureWidth = Math.max(width + reach, (height + reach) * aspect);
			double parallax = pictureDepth / distance;
			Axis columns = getAxis(width, toCamera.dot(right), parallax, pictureWidth, image.width());
			Axis rows = getAxis(height, toCamera.y, parallax, pictureWidth / aspect, image.height());

			InBetweenMesh mesh = new InBetweenMesh(imageRenderType(image.texture()), InBetweenMesh.ORDER_IMAGES, true);
			int color = (alpha << 24) | 0xFFFFFF;
			for (int row = 0; row < rows.splits().length - 1; row++) {
				int cellRow = (int) ((height / 2 - (rows.splits()[row] + rows.splits()[row + 1]) / 2) / cellSize);
				for (int column = 0; column < columns.splits().length - 1; column++) {
					int cellColumn = (int) (((columns.splits()[column] + columns.splits()[column + 1]) / 2 + width /2) / cellSize);
					if (cellRow < 0 || cellRow >= openCells.length || cellColumn < 0 || cellColumn >= openCells[cellRow].length) continue;
					if (!openCells[cellRow][cellColumn]) continue;
					addCorner(mesh, camera, columns, column, rows, row, color);
					addCorner(mesh, camera, columns, column + 1, rows, row, color);
					addCorner(mesh, camera, columns, column + 1, rows, row + 1, color);
					addCorner(mesh, camera, columns, column, rows, row + 1, color);
				}
			}
			return mesh;
		}

		private void addCorner(InBetweenMesh mesh, Vec3 camera, Axis columns, int column, Axis rows, int row, int color) {
			double x = columns.splits()[column];
			double y = rows.splits()[row];
			mesh.add(center.x + right.x * x - camera.x, center.y + y - camera.y, center.z + right.z * x - camera.z, columns.texture()[column], 1 - rows.texture()[row], color);
		}

		private Axis getAxis(double size, double camera, double parallax, double pictureSize, int pixels) {
			int parts = (int) Math.round(size / cellSize) * PICTURE_SUBDIVISIONS;
			double[] splits = new double[parts + 1];
			for (int part = 0; part < parts; part++) {
				splits[part] = -size / 2 + part * cellSize / PICTURE_SUBDIVISIONS;
			}
			splits[parts] = size / 2;

			double[] texture = new double[splits.length];
			for (int i = 0; i < splits.length; i++) {
				double hit = splits[i] + (splits[i] - camera) * parallax;
				texture[i] = 0.5 + 0.5 * squeeze(hit / (pictureSize / 2) )*(1 - 1.0 / pixels);
			}
			return new Axis(splits, texture);
		}

		private static double squeeze(double position) {
			double distance = Math.abs(position);
			if (distance <= PICTURE_SQUEEZE_START) return position;
			double room = 1 - PICTURE_SQUEEZE_START;
			return Math.copySign(PICTURE_SQUEEZE_START + room * (1 - Math.exp(-(distance - PICTURE_SQUEEZE_START) / room)), position);
		}

		@Nullable
		private InBetweenMesh buildRays(InBetweenMemoryCache.LoadedImage image, InBetweenCameraView view) {
			if (appear <= 0 || !InBetweenShaders.raysAvailable() || !createRays(image)) return null;
			Vec3 camera = view.position();
			updateFrame(camera);
			InBetweenMesh mesh = new InBetweenMesh(raysRenderType, InBetweenMesh.ORDER_LIGHT, false);
			int color = ((int) Math.round(255 * Math.min(1, appear)) << 24) | 0xFFFFFF;
			Vec3 toCamera = camera.subtract(center);
			double cx = toCamera.dot(right);
			double cy = toCamera.y;
			double cz = toCamera.dot(normal);
			double widthAtCamera = rayHalfWidth * (cz + RAY_SOURCE_DEPTH) / RAY_SOURCE_DEPTH;
			double heightAtCamera = rayHalfHeight * (cz + RAY_SOURCE_DEPTH) / RAY_SOURCE_DEPTH;
			boolean inside = cz > -RAY_INSIDE_MARGIN && cz < RAY_LENGTH + RAY_INSIDE_MARGIN
					&& Math.abs(cx) < widthAtCamera + RAY_INSIDE_MARGIN && Math.abs(cy) < heightAtCamera + RAY_INSIDE_MARGIN;
			if (inside) {
				mesh.addScreenQuad(view, color);
				return mesh;
			}

			Vec3 origin = center.subtract(camera);
			double spread = (RAY_LENGTH + RAY_SOURCE_DEPTH) / RAY_SOURCE_DEPTH;
			double farWidth = rayHalfWidth * spread;
			double farHeight = rayHalfHeight * spread;
			double[][] corners = {
					{-rayHalfWidth, -rayHalfHeight, 0}, {rayHalfWidth, -rayHalfHeight, 0}, {rayHalfWidth, rayHalfHeight, 0}, {-rayHalfWidth, rayHalfHeight, 0},
					{-farWidth, -farHeight, RAY_LENGTH}, {farWidth, -farHeight, RAY_LENGTH}, {farWidth, farHeight, RAY_LENGTH}, {-farWidth, farHeight, RAY_LENGTH}
			};
			double slopeX = rayHalfWidth / RAY_SOURCE_DEPTH;
			double slopeY = rayHalfHeight / RAY_SOURCE_DEPTH;
			if (cz < 0) addFace(mesh, origin, corners, color, 0, 1, 2, 3);
			if (cz > RAY_LENGTH) addFace(mesh, origin, corners, color, 4, 7, 6, 5);
			if (cx - slopeX * cz > rayHalfWidth) addFace(mesh, origin, corners, color, 1, 5, 6, 2);
			if (-cx - slopeX * cz > rayHalfWidth) addFace(mesh, origin, corners, color, 0, 3, 7, 4);
			if (cy - slopeY * cz > rayHalfHeight) addFace(mesh, origin, corners, color, 3, 2, 6, 7);
			if (-cy - slopeY * cz > rayHalfHeight) addFace(mesh, origin, corners, color, 0, 4, 5, 1);
			return mesh.isEmpty() ? null : mesh;
		}

		private void addFace(InBetweenMesh mesh, Vec3 origin, double[][] corners, int color, int... indices) {
			for (int index : indices) {
				double[] corner = corners[index];
				mesh.add(origin.x + right.x * corner[0] + normal.x * corner[2], origin.y + corner[1], origin.z + right.z * corner[0] + normal.z * corner[2], 0, 0, color);
			}
		}

		private void updateFrame(Vec3 camera) {
			double ox = center.x - camera.x;
			double oy = center.y - camera.y;
			double oz = center.z - camera.z;
			double far = Math.max(0,Math.min(1,(camera.distanceTo(center) - RAY_FAR_START) / (DARK_END - RAY_FAR_START)));
			frame.set(
					(float) right.x, (float) right.z, (float) -(ox * right.x + oz * right.z), (float) -(ox * normal.x + oz * normal.z),
					(float) -oy, (float) rayHalfWidth, (float) rayHalfHeight, (float) RAY_SOURCE_DEPTH,
					(float) RAY_LENGTH, (float) RAY_FADE_START, (float) (far * far * (3 - 2 * far)), 0f,
					0f, 0f, 0f, 1f
			);
		}

		private boolean createRays(InBetweenMemoryCache.LoadedImage image) {
			if (raysRenderType != null) return true;
			if (rayTextureFailed) return false;
			NativeImage picture = InBetweenShaders.getPixels(image.texture());
			if (picture == null) return false;
			try {
				int textureWidth = (openCells[0].length + 2 * RAY_MARGIN_CELLS) * RAY_TEXELS_PER_CELL;
				int textureHeight = (openCells.length + 2 * RAY_MARGIN_CELLS) * RAY_TEXELS_PER_CELL;
				float[] mask = softMask(textureWidth, textureHeight);
				NativeImage rays = new NativeImage(textureWidth, textureHeight, false);
				int samples = 4;
				for (int ty = 0; ty < textureHeight; ty++) {
					double windowY = rayHalfHeight - (ty + 0.5) * 2 * rayHalfHeight / textureHeight;
					for (int tx = 0; tx < textureWidth; tx++) {
						double windowX = -rayHalfWidth + (tx + 0.5) * 2 * rayHalfWidth / textureWidth;
						int red = 0, green = 0, blue = 0;
						for (int sy = 0; sy < samples; sy++) {
							for (int sx = 0; sx < samples; sx++) {
								double x = windowX + ((sx + 0.5) / samples - 0.5) * RAY_COLOR_BLUR;
								double y = windowY + ((sy + 0.5) / samples - 0.5) * RAY_COLOR_BLUR;
								int px = clamp((int) ((0.5 + x / width) * picture.getWidth()), picture.getWidth());
								int py = clamp((int) ((0.5 - y / height) * picture.getHeight()), picture.getHeight());
								int argb = InBetweenShaders.getPixel(picture, px, py);
								red += (argb >> 16) & 0xFF;
								green += (argb >> 8) & 0xFF;
								blue += argb & 0xFF;
							}
						}
						int count = samples * samples;
						red = (red + count / 2) / count;
						green = (green + count / 2) / count;
						blue = (blue + count / 2) / count;
						int alpha = (int) Math.round(beamBrightness(windowX, windowY) * mask[ty * textureWidth + tx] * 255);
						InBetweenShaders.setPixel(rays, tx, ty, (alpha << 24) | (red << 16) | (green << 8) | blue);
					}
				}
				Identifier id = IdentifierHelper.lifeseries("dynamic/in_between_rays/" + (rayTextureCounter++));
				InBetweenShaders.registerLinearTexture(id, "In-Between window rays", rays);
				rayTexture = id;
				raysRenderType = InBetweenShaders.rays(id, () -> frame);
				return true;
			} catch (Exception e) {
				LifeSeries.LOGGER.error("[In-Between] Failed to create the light of window {}", InBetweenMemoryManager.shortHash(imageHash), e);
				rayTextureFailed = true;
				releaseRays();
				return false;
			}
		}

		private double beamBrightness(double x, double y) {
			double streaks = 0.5 * valueNoise(x * RAY_BEAM_DETAIL, y * RAY_BEAM_DETAIL, 11)
					+ 0.3 * valueNoise(x * RAY_BEAM_DETAIL * 2.4, y * RAY_BEAM_DETAIL * 2.4, 47)
					+ 0.2 * valueNoise(x * RAY_BEAM_DETAIL * 4.5, y * RAY_BEAM_DETAIL * 4.5, 71);
			double t = Math.max(0, Math.min(1, (streaks - 0.3) / 0.45));
			return RAY_BEAM_DARKEST + (1 - RAY_BEAM_DARKEST) * t * t * (3 - 2 * t);
		}

		private static double valueNoise(double x, double y, int seed) {
			int cellX = (int) Math.floor(x);
			int cellY = (int) Math.floor(y);
			double fx = x - cellX;
			double fy = y - cellY;
			fx = fx * fx * (3 - 2 * fx);
			fy = fy * fy * (3 - 2 * fy);
			double top = hash(cellX, cellY, seed) * (1 - fx) + hash(cellX + 1, cellY, seed) * fx;
			double bottom = hash(cellX, cellY + 1, seed) * (1 - fx) + hash(cellX + 1, cellY + 1, seed) * fx;
			return top * (1 - fy) + bottom * fy;
		}

		private static double hash(int x, int y, int seed) {
			long h = x * 0x27D4EB2FL + y * 0x165667B1L + seed * 0x9E3779B9L;
			h = (h ^ (h >>> 15)) * 0x85EBCA77L;
			h = (h ^ (h >>> 13)) * 0xC2B2AE3DL;
			h ^= h >>> 16;
			return (h & 0xFFFFFF) / (double) 0xFFFFFF;
		}

		private static int clamp(int value, int size) {
			return Math.max(0, Math.min(size - 1, value));
		}

		private float[] softMask(int textureWidth, int textureHeight) {
			float[] mask = new float[textureWidth * textureHeight];
			int reach = (int) Math.ceil(RAY_EDGE_SOFTNESS * 3) + 1;
			for (int ty = 0; ty < textureHeight; ty++) {
				double y = (ty + 0.5) / RAY_TEXELS_PER_CELL - RAY_MARGIN_CELLS;
				int row = (int) Math.floor(y);
				for (int tx = 0; tx < textureWidth; tx++) {
					double x = (tx + 0.5) / RAY_TEXELS_PER_CELL - RAY_MARGIN_CELLS;
					int column = (int) Math.floor(x);
					double nearest = Double.MAX_VALUE;
					for (int r = Math.max(0, row - reach); r <= Math.min(openCells.length - 1, row + reach); r++) {
						for (int c = Math.max(0, column - reach); c <= Math.min(openCells[r].length - 1, column + reach); c++) {
							if (!openCells[r][c]) continue;
							double dx = Math.max(0, Math.max(c - x, x - (c + 1)));
							double dy = Math.max(0, Math.max(r - y, y - (r + 1)));
							nearest = Math.min(nearest, dx * dx + dy * dy);
						}
					}
					mask[ty * textureWidth + tx] = nearest == Double.MAX_VALUE ? 0 : (float) Math.exp(-nearest / (2 * RAY_EDGE_SOFTNESS * RAY_EDGE_SOFTNESS));
				}
			}
			return mask;
		}
	}
}
