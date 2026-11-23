package net.mat0u5.lifeseries.client.features.inbetween;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.particle.InBetweenBackgroundParticle;
import net.mat0u5.lifeseries.client.particle.InBetweenParticle;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InBetweenEffectsClient {
	//Clouds / Fog
	private static final float CLOUD_NEAR = 1;
	private static final float CLOUD_FAR = 48;
	private static final float CLOUD_DENSITY = 0.04f;
	private static final float CLOUD_SPHERE_RADIUS = 10;
	private static final int CLOUD_COLOR = 0xFF454754;
	private static final float CLOUD_BRIGHTNESS = 0.65f;
	private static final double CLOUD_AVERAGE_SHADE = 0.6;
	private static final Vec3 CLOUD_FOG_COLOR = new Vec3(
			((CLOUD_COLOR >> 16) & 0xFF) / 255.0 * CLOUD_AVERAGE_SHADE * CLOUD_BRIGHTNESS,
			((CLOUD_COLOR >> 8) & 0xFF) / 255.0 * CLOUD_AVERAGE_SHADE * CLOUD_BRIGHTNESS,
			(CLOUD_COLOR & 0xFF) / 255.0 * CLOUD_AVERAGE_SHADE * CLOUD_BRIGHTNESS
	);
	private static final double CLOUD_SIZE = 14;
	private static final double CLOUD_FLOW_SPEED = 4;
	private static final double CLOUD_SWIRL_SPEED = 1.5;
	private static final float CLOUD_SWIRL = 1.4f;
	private static final float CLOUD_AHEAD_GLOW = 0.3f;
	private static final int CLOUD_SEGMENTS = 32;
	private static final int CLOUD_RINGS = 16;
	private static final double CLOUD_NOISE_PERIOD = 256;
	private static final double SWIRL_NOISE_PERIOD = 128;

	//Particle glow
	private static final int CORE_TEXELS_PER_PIXEL = 8;
	private static final double CORE_POWER = 3.5; // Higher = darker pixels stay more colored
	private static final double CORE_OWN_GAIN = 7.5; // Whitening in each pixel
	private static final double CORE_BLEED_GAIN = 23; // Whitening that bleeds into other pixels
	private static final double CORE_BLEED_SIGMA = 0.5; // In sprite pixels
	private static final double CORE_ENERGY_MAX = 24;
	private static final double GLOW_MARGIN = 1;
	private static final int GLOW_TEXELS_PER_SPRITE = 24;
	private static final double GLOW_NARROW_SIGMA = 0.09; // Around each pixel
	private static final double GLOW_NARROW_WEIGHT = 0.4;
	private static final double GLOW_WIDE_SIGMA = 0.3; // Around the whole particle
	private static final double GLOW_WIDE_WEIGHT = 1;
	private static final double GLOW_BRIGTHNESS_POWER = 1.5; // Higher = only the brigthest pixels glow
	private static final double GLOW_GAIN = 0.16;
	private static final double[] GLOW_TINT = {0.6, 0, 1.0}; //Magenta
	private static final double GLOW_REFERENCE_PIXELS = 64;
	private static final double GLOW_EDGE_FADE = 0.2;

	private static Vec3 pathDirection = new Vec3(0, 0, 1);
	private static final List<InBetweenBackgroundParticle> glowingParticles = new ArrayList<>();
	private static long particleTicks = 0;
	private static final Map<Identifier, Optional<SpriteGlow>> spriteGlows = new HashMap<>();
	@Nullable
	private static InBetweenParticle particle;
	@Nullable
	private static ClientLevel particleLevel;
	private static boolean active = false;

	private static final Matrix4f cloudParameters = new Matrix4f();
	@Nullable
	private static InBetweenMesh clouds;
	@Nullable
	private static InBetweenMesh depthClouds;
	private static boolean cloudsShown = false;
	private static boolean cloudsOverDepth = false;
	private static Vec3 cloudDrift = Vec3.ZERO;
	private static Vec3 swirlDrift = new Vec3(5.2, 1.3, 9.7);
	private static long lastFrameNanos = -1;

	private record SpriteGlow(Identifier core, @Nullable Identifier glow, int brigthness) {}

	public static void setPathYaw(double yaw) {
		double radians = Math.toRadians(yaw);
		pathDirection = new Vec3(-Math.sin(radians), 0, Math.cos(radians));
	}

	public static boolean isActive() {
		return active;
	}

	@Nullable
	public static Vec3 getFogColorIfActive() {
		if (!active || !cloudsShown) return null;
		return cloudsOverDepth ? Vec3.ZERO : CLOUD_FOG_COLOR;
	}

	public static boolean drawsParticle(InBetweenBackgroundParticle particle) {
		return active && InBetweenShaders.glowAvailable() && getSpriteGlow(particle.getGlowSprite()) != null;
	}

	public static void tick(LocalPlayer player) {
		Minecraft client = Minecraft.getInstance();
		ClientLevel level = client.level;
		active = level != null && InBetweenManager.isInInBetween(player);
		if (!active) {
			clear();
			return;
		}
		if (particle == null || !particle.isAlive() || particleLevel != level) {
			if (particle != null) particle.remove();
			particle = new InBetweenParticle(level, new AABB(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7), InBetweenEffectsClient::isActive, InBetweenEffectsClient::buildMeshes);
			particleLevel = level;
			client.particleEngine.add(particle);
		}
	}

	public static void clear() {
		active = false;
		cloudsShown = false;
		InBetweenTriviaClient.clear();
		if (particle != null) particle.remove();
		particle = null;
		particleLevel = null;
		glowingParticles.clear();
		lastFrameNanos = -1;
	}

	public static void onParticlesCleared() {
		particle = null;
		glowingParticles.clear();
	}

	public static void onResourcesReloaded() {
		for (Optional<SpriteGlow> spriteGlow : spriteGlows.values()) {
			spriteGlow.ifPresent(glow -> {
				Minecraft.getInstance().getTextureManager().release(glow.core());
				if (glow.glow() != null) Minecraft.getInstance().getTextureManager().release(glow.glow());
			});
		}
		spriteGlows.clear();
	}

	public static void addGlowingParticle(InBetweenBackgroundParticle particle) {
		glowingParticles.add(particle);
	}

	public static void onParticlesTicked() {
		particleTicks++;
	}

	public static long getParticleTicks() {
		return particleTicks;
	}

	private static List<InBetweenMesh> buildMeshes(InBetweenCameraView view) {
		List<InBetweenMesh> meshes = new ArrayList<>();
		if (!active) return meshes;
		InBetweenMesh clouds = buildClouds(view.position());
		if (clouds != null) meshes.add(clouds);
		buildGlow(view, meshes);
		InBetweenTriviaClient.addMeshes(view, meshes);
		return meshes;
	}

	@Nullable
	private static InBetweenMesh buildClouds(Vec3 camera) {
		cloudsOverDepth = InBetweenShaders.depthCloudsAvailable();
		cloudsShown = cloudsOverDepth || InBetweenShaders.cloudsAvailable();
		if (!cloudsShown) return null;
		updateClouds(camera);
		if (cloudsOverDepth) {
			if (depthClouds == null) depthClouds = createCloudSphere(InBetweenShaders.clouds(() -> cloudParameters, true));
			return depthClouds;
		}
		if (clouds == null) clouds = createCloudSphere(InBetweenShaders.clouds(() -> cloudParameters, false));
		return clouds;
	}

	private static void updateClouds(Vec3 camera) {
		long now = System.nanoTime();
		double seconds = lastFrameNanos < 0 ? 0 : Math.min(0.25,(now - lastFrameNanos) / 1.0E9);
		lastFrameNanos = now;
		double perBlock = 1 / CLOUD_SIZE;
		cloudDrift = wrap(cloudDrift.add(pathDirection.scale(CLOUD_FLOW_SPEED * perBlock * seconds)), CLOUD_NOISE_PERIOD);
		swirlDrift = wrap(swirlDrift.add(pathDirection.scale(CLOUD_SWIRL_SPEED * perBlock * 0.5 * seconds) ), SWIRL_NOISE_PERIOD);
		Vec3 clouds = wrap(camera.scale(perBlock).add(cloudDrift), CLOUD_NOISE_PERIOD);
		Vec3 swirls = wrap(camera.scale(perBlock * 0.5).add(swirlDrift), SWIRL_NOISE_PERIOD);
		cloudParameters.set(
				(float) clouds.x, (float) clouds.y, (float) clouds.z, (float) perBlock,
				(float) swirls.x, (float) swirls.y, (float) swirls.z, CLOUD_SWIRL,
				(float) pathDirection.x, (float) pathDirection.y, (float) pathDirection.z, CLOUD_BRIGHTNESS,
				CLOUD_AHEAD_GLOW, CLOUD_NEAR, CLOUD_FAR, CLOUD_DENSITY
		);
	}

	static Vec3 wrap(Vec3 value, double period) {
		return new Vec3(value.x - period * Math.floor(value.x / period),value.y - period * Math.floor(value.y / period),  value.z - period * Math.floor(value.z / period));
	}

	private static InBetweenMesh createCloudSphere(RenderType renderType) {
		InBetweenMesh mesh = new InBetweenMesh(renderType, InBetweenMesh.ORDER_CLOUDS, false);
		for (int ring = 0; ring < CLOUD_RINGS; ring++) {
			for (int segment = 0; segment < CLOUD_SEGMENTS; segment++) {
				addSpherePoint(mesh, ring, segment);
				addSpherePoint(mesh, ring + 1, segment);
				addSpherePoint(mesh, ring + 1, segment + 1);
				addSpherePoint(mesh, ring, segment + 1);
			}
		}
		return mesh;
	}

	private static void addSpherePoint(InBetweenMesh mesh, int ring, int segment) {
		double polar = Math.PI * ring / CLOUD_RINGS;
		double azimuth = Math.PI * 2 * segment / CLOUD_SEGMENTS;
		mesh.add(Math.sin(polar) * Math.cos(azimuth) * CLOUD_SPHERE_RADIUS, Math.cos(polar) * CLOUD_SPHERE_RADIUS, Math.sin(polar) * Math.sin(azimuth) * CLOUD_SPHERE_RADIUS, 0, 0, CLOUD_COLOR);
	}

	private static void buildGlow(InBetweenCameraView view, List<InBetweenMesh> meshes) {
		if (glowingParticles.isEmpty() || !InBetweenShaders.glowAvailable()) return;
		glowingParticles.removeIf(particle -> !particle.isStillGlowing());

		Map<Identifier, InBetweenMesh> meshPerTexture = new LinkedHashMap<>();
		for (InBetweenBackgroundParticle particle : glowingParticles) {
			float alpha = particle.getGlowAlpha(view.partialTick());
			SpriteGlow glow = getSpriteGlow(particle.getGlowSprite());
			if (alpha <= 0.004f || glow == null) continue;
			Vec3 position = particle.getGlowPosition(view.partialTick()).subtract(view.position());
			float size = particle.getQuadSize(view.partialTick());
			int alphaBits = Math.round(Math.min(1, alpha) * 255) << 24;
			addQuad(meshPerTexture, glow.core(), view, position, size, alphaBits);
			if (glow.glow() != null) addQuad(meshPerTexture, glow.glow(), view, position, size * (float) (1 + 2 * GLOW_MARGIN), alphaBits | (glow.brigthness() * 0x010101));
		}
		meshes.addAll(meshPerTexture.values());
	}

	private static void addQuad(Map<Identifier, InBetweenMesh> meshPerTexture, Identifier texture, InBetweenCameraView view, Vec3 position, float size, int color) {
		InBetweenMesh mesh = meshPerTexture.computeIfAbsent(texture, id -> new InBetweenMesh(InBetweenShaders.glow(id), InBetweenMesh.ORDER_LIGHT, false));
		Vector3f right = new Vector3f(view.left()).mul(-size);
		Vector3f up = new Vector3f(view.up()).mul(size);
		mesh.add(position.x + right.x - up.x, position.y + right.y - up.y, position.z + right.z - up.z, 1, 1, color);
		mesh.add(position.x + right.x + up.x, position.y + right.y + up.y, position.z + right.z + up.z, 1, 0, color);
		mesh.add(position.x - right.x + up.x, position.y - right.y + up.y, position.z - right.z + up.z, 0, 0, color);
		mesh.add(position.x - right.x - up.x, position.y - right.y - up.y, position.z - right.z - up.z, 0, 1, color);
	}

	@Nullable
	private static SpriteGlow getSpriteGlow(Identifier sprite) {
		Optional<SpriteGlow> cached = spriteGlows.get(sprite);
		if (cached != null) return cached.orElse(null);
		SpriteGlow glow = null;
		try {
			glow = createSpriteGlow(sprite);
		} catch (Exception e) {
			LifeSeries.LOGGER.error("[In-Between] Failed to create the glow of particle {}", sprite, e);
		}
		spriteGlows.put(sprite, Optional.ofNullable(glow));
		return glow;
	}

	@Nullable
	private static SpriteGlow createSpriteGlow(Identifier sprite) throws Exception {
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(sprite);
		if (resource.isEmpty()) return null;
		try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
			int width = image.getWidth();
			int height = image.getHeight() > width && image.getHeight() % width == 0 ? width : image.getHeight();
			String path = sprite.getPath().replace(".png", "");

			Identifier coreId = IdentifierHelper.lifeseries("dynamic/in_between_glow_core/" + path);
			InBetweenShaders.registerLinearTexture(coreId, "In-Between particle core", createCore(image, width, height));

			int size = (int) Math.round((1 + 2 * GLOW_MARGIN) * GLOW_TEXELS_PER_SPRITE);
			double[] glow = createGlow(image, width, height, size);
			double peak = 0;
			for (double value : glow) peak = Math.max(peak, value);
			int brigthness = (int) Math.round(Math.min(1, peak) * 255);
			if (brigthness <= 0) return new SpriteGlow(coreId, null, 0);

			double scale = 255 / (brigthness / 255.0);
			NativeImage texture = new NativeImage(size, size, false);
			for (int ty = 0; ty < size; ty++) {
				for (int tx = 0; tx < size; tx++) {
					int i = (ty * size + tx) * 3;
					int red = (int) Math.round(Math.min(255, glow[i] * scale));
					int green = (int) Math.round(Math.min(255, glow[i + 1] * scale));
					int blue = (int) Math.round(Math.min(255, glow[i + 2] * scale));
					InBetweenShaders.setPixel(texture, tx, ty, (red << 16) | (green << 8) | blue);
				}
			}
			Identifier glowId = IdentifierHelper.lifeseries("dynamic/in_between_glow/" + path);
			InBetweenShaders.registerLinearTexture(glowId, "In-Between particle glow", texture);
			return new SpriteGlow(coreId, glowId, brigthness);
		}
	}

	private static NativeImage createCore(NativeImage image, int width, int height) {
		double[] energy = new double[width * height * 3];
		boolean[] lit = new boolean[width * height];
		for (int py = 0; py < height; py++) {
			for (int px = 0; px < width; px++) {
				int argb = InBetweenShaders.getPixel(image, px, py);
				double alpha = ((argb >>> 24) & 0xFF) / 255.0;
				int i = py * width + px;
				lit[i] = alpha > 0;
				for (int c = 0; c < 3; c++) {
					energy[i * 3 + c] = alpha * Math.pow(((argb >> (16 - 8 * c)) & 0xFF) / 255.0, CORE_POWER);
				}
			}
		}
		double[][] bleedX = bleedTable(width);
		double[][] bleedY = bleedTable(height);
		NativeImage core = new NativeImage(width * CORE_TEXELS_PER_PIXEL, height * CORE_TEXELS_PER_PIXEL, true);
		for (int ty = 0; ty < core.getHeight(); ty++) {
			for (int tx = 0; tx < core.getWidth(); tx++) {
				int own = (ty / CORE_TEXELS_PER_PIXEL) * width + tx / CORE_TEXELS_PER_PIXEL;
				if (!lit[own]) continue; // Stays transparent
				int argb = 0xFF000000;
				for (int c = 0; c < 3; c++) {
					double bleed = 0;
					for (int py = 0; py < height; py++) {
						for (int px = 0; px < width; px++) {
							bleed += bleedY[ty][py] * bleedX[tx][px] * energy[(py * width + px) * 3 + c];
						}
					}
					double value = (CORE_OWN_GAIN * energy[own * 3 + c] + CORE_BLEED_GAIN * bleed) / CORE_ENERGY_MAX;
					argb |= (int) Math.round(Math.sqrt(Math.min(1, value)) * 255) << (16 - 8 * c);
				}
				InBetweenShaders.setPixel(core, tx, ty, argb);
			}
		}
		return core;
	}

	private static double[][] bleedTable(int pixels) {
		int texels = pixels * CORE_TEXELS_PER_PIXEL;
		double[][] table = new double[texels][pixels];
		double norm = 1 / (CORE_TEXELS_PER_PIXEL * CORE_BLEED_SIGMA * Math.sqrt(2 * Math.PI));
		for (int t = 0; t < texels; t++) {
			for (int s = 0; s < texels; s++) {
				double distance = (t - s) / (double) CORE_TEXELS_PER_PIXEL;
				table[t][s / CORE_TEXELS_PER_PIXEL] += norm * Math.exp(-distance * distance / (2 * CORE_BLEED_SIGMA * CORE_BLEED_SIGMA));
			}
		}
		return table;
	}

	private static double[] createGlow(NativeImage image, int width, int height, int size) {
		double[] source = new double[width * height * 3];
		double pixelArea = GLOW_REFERENCE_PIXELS / (width * height);
		for (int py = 0; py < height; py++) {
			for (int px = 0; px < width; px++) {
				int argb = InBetweenShaders.getPixel(image, px, py);
				double red = ((argb >> 16) & 0xFF) / 255.0;
				double green = ((argb >> 8) & 0xFF) / 255.0;
				double blue = (argb & 0xFF) / 255.0;
				double luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue;
				double weight = ((argb >>> 24) & 0xFF) / 255.0 * Math.pow(luminance,GLOW_BRIGTHNESS_POWER) * pixelArea;
				int i = (py * width + px) * 3;
				source[i] = red * weight;
				source[i + 1] = green * weight;
				source[i + 2] = blue * weight;
			}
		}
		double[][] narrowX = gaussianTable(size, width, GLOW_NARROW_SIGMA);
		double[][] narrowY = gaussianTable(size, height, GLOW_NARROW_SIGMA);
		double[][] wideX = gaussianTable(size, width, GLOW_WIDE_SIGMA);
		double[][] wideY = gaussianTable(size, height, GLOW_WIDE_SIGMA);
		double[] glow = new double[size * size * 3];
		for (int ty = 0; ty < size; ty++) {
			double edgeY = Math.abs((ty + 0.5) / size * 2 - 1);
			for (int tx = 0; tx < size; tx++) {
				double edgeX = Math.abs((tx + 0.5) / size * 2 - 1);
				double fade = Math.max(0, Math.min(1, (Math.max(edgeX, edgeY) - (1 - GLOW_EDGE_FADE)) / GLOW_EDGE_FADE));
				double keep = GLOW_GAIN * (1 - fade * fade * (3 - 2 * fade));
				int o = (ty * size + tx) * 3;
				for (int py = 0; py < height; py++) {
					for (int px = 0; px < width; px++) {
						double factor = keep * (GLOW_NARROW_WEIGHT*narrowY[ty][py]*narrowX[tx][px] + GLOW_WIDE_WEIGHT*wideY[ty][py]*wideX[tx][px]);
						int i = (py * width + px) * 3;
						glow[o] += factor * source[i] * GLOW_TINT[0];
						glow[o + 1] += factor * source[i + 1] * GLOW_TINT[1];
						glow[o + 2] += factor * source[i + 2] * GLOW_TINT[2];
					}
				}
			}
		}
		return glow;
	}

	private static double[][] gaussianTable(int texels, int pixels, double sigma) {
		double[][] table = new double[texels][pixels];
		for (int t = 0; t < texels; t++) {
			double position = -GLOW_MARGIN + (t + 0.5) / GLOW_TEXELS_PER_SPRITE;
			for (int p = 0; p < pixels; p++) {
				double distance = position - (p + 0.5) / pixels;
				table[t][p] = Math.exp(-distance * distance / (2 * sigma * sigma));
			}
		}
		return table;
	}
}
