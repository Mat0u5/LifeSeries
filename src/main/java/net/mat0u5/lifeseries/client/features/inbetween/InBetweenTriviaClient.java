package net.mat0u5.lifeseries.client.features.inbetween;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaHandler;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

//? if <= 1.20.3 {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.FireworkRocketItem;
*///?} else {
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.component.FireworkExplosion;
//?}
//? if >= 1.21.9
import net.minecraft.core.particles.ColorParticleOption;

public class InBetweenTriviaClient {
	private static final double SOURCE_HEIGHT = 36;
	private static final double SOURCE_SPACING = 20;
	private static final double SOURCE_RADIUS = 0.4;
	private static final double SPREAD = 0.016;
	private static final double DANCE_RADIUS = 5;
	private static final double[][] DANCE = {
			{1.9, 0.0, 3.0, 1.3, 1.6, 2.1, 2.6, 0.4},
			{1.7, 2.6, 2.7, 4.0, 2.1, 0.9, 3.2, 3.3},
			{2.2, 4.4, 2.5, 2.2, 1.8, 5.1, 3.1, 1.7}
	};
	private static final int[] COLORS = {0xFF3D26, 0xFFD60D, 0x38FF2B};
	private static final double BEAM_DENSITY = 0.2;
	private static final double BEAM_BRIGHTNESS = 0.5;
	private static final double WHITE_BEAM_BRIGHTNESS = 0.9;
	private static final double POOL_BRIGHTNESS = 0.45;
	private static final double WHITE_POOL_BRIGHTNESS = 0.7;
	private static final double POOL_SOFT_START = 0.35;
	private static final double POOL_LIFT = 0.03;
	private static final int POOL_TEXELS = 64;
	private static final double BEAM_MARGIN = 0.3;
	private static final double INSIDE_MARGIN = 0.75;
	private static final double DUST_SCALE = 1 / 3.0;
	private static final Vec3 DUST_DRIFT = new Vec3(0.12, -0.04, 0.08);
	private static final double NOISE_PERIOD = 128;
	private static final double TURN_ON_SECONDS = 0.3;
	private static final double TO_BOT_SECONDS = 0.5;
	private static final double FADE_SECONDS = InBetweenTriviaHandler.LANDED_FADE / 20.0;
	private static final double MAX_SECONDS = 60;

	private static final double FIREWORK_SIDE = 7;
	private static final double FIREWORK_HEIGHT = 7;
	private static final int[] FIREWORK_COLORS = {DyeColor.RED.getFireworkColor(), DyeColor.YELLOW.getFireworkColor(), DyeColor.LIME.getFireworkColor()};
	private static final Identifier FACE_TEXTURE = IdentifierHelper.lifeseries("textures/effect/triviabot_firework.png");
	private static final double FACE_PIXEL = 0.3;
	private static final double SPARK_FRICTION = 0.91;

	private static final Matrix4f[] frames = {new Matrix4f(), new Matrix4f(), new Matrix4f()};
	private static final RenderType[] beamTypes = new RenderType[3];
	@Nullable
	private static Identifier poolTexture;
	@Nullable
	private static Show show;

	public static void onPacket(List<String> values) {
		if (values.isEmpty()) return;
		try {
			switch (values.get(0)) {
				case "lights" -> show = new Show(new Vec3(Double.parseDouble(values.get(1)), Double.parseDouble(values.get(2)), Double.parseDouble(values.get(3))), Float.parseFloat(values.get(4)));
				case "track" -> {
					if (show != null) show.track(Integer.parseInt(values.get(1)));
				}
				case "fade" -> {
					if (show != null && show.fadeStart < 0) show.fadeStart = show.age();
				}
				case "stop" -> show = null;
				case "firework" -> createFireworks(values.get(1));
			}
		} catch (Exception e) {
			LifeSeries.LOGGER.warn("[In-Between] Invalid trivia packet {}", values);
		}
	}

	public static void clear() {
		show = null;
	}

	public static void addMeshes(InBetweenCameraView view, List<InBetweenMesh> meshes) {
		ClientLevel level = Minecraft.getInstance().level;
		if (show == null || level == null) return;
		double age = show.age();
		Vec3 bot = show.getBotTarget(level, age, view.partialTick());
		double light = show.getLight(age);
		if (light <= 0.004) return;
		double white = show.getWhiteness(age);
		boolean beams = InBetweenShaders.spotlightsAvailable();
		InBetweenMesh pools = InBetweenShaders.glowAvailable() ? new InBetweenMesh(InBetweenShaders.glow(getPoolTexture()), InBetweenMesh.ORDER_LIGHT, false) : null;
		for (int lamp = 0; lamp < 3; lamp++) {
			Vec3 source = show.getSource(lamp);
			Vec3 target = show.getDanceTarget(lamp, age);
			if (bot != null) target = target.lerp(bot, white);
			Vec3 axis = target.subtract(source).normalize();
			if (axis.y > -0.05) continue;
			if (beams) {
				InBetweenMesh beam = buildBeam(lamp, view, source, axis, show.floor.y, age, getColor(lamp, white, BEAM_BRIGHTNESS, WHITE_BEAM_BRIGHTNESS, light));
				if (beam != null) meshes.add(beam);
			}
			if (pools != null) addPool(pools, view.position(), source, axis, show.floor.y, getColor(lamp, white, POOL_BRIGHTNESS, WHITE_POOL_BRIGHTNESS, light));
		}
		if (pools != null && !pools.isEmpty()) meshes.add(pools);
	}

	private static int getColor(int lamp, double white, double brigthness, double whiteBrightness, double light) {
		int color = (int) Math.round(light * 255) << 24;
		for (int shift = 0; shift <= 16; shift += 8) {
			double channel = ((COLORS[lamp] >> shift) & 0xFF) / 255.0 * brigthness;
			channel += (whiteBrightness - channel) * white;
			color |= (int) Math.round(Math.max(0, Math.min(1, channel)) * 255) << shift;
		}
		return color;
	}

	@Nullable
	private static InBetweenMesh buildBeam(int lamp, InBetweenCameraView view, Vec3 source, Vec3 axis, double floorY, double seconds, int color) {
		Vec3 camera = view.position();
		double vertical = -axis.y;
		double length = (source.y - floorY) / vertical;
		double tilt = Math.sqrt(Math.max(0, 1 - vertical * vertical)) / vertical;
		double reach = length + (SOURCE_RADIUS + SPREAD * length) * tilt + 1;
		double near = SOURCE_RADIUS + BEAM_MARGIN;
		double far = SOURCE_RADIUS + SPREAD * reach + BEAM_MARGIN;
		double slope = (far - near) / reach;
		Vec3 side = Math.abs(axis.y) > 0.999 ? new Vec3(1, 0, 0) : axis.cross(new Vec3(0, 1, 0)).normalize();
		Vec3 up = side.cross(axis);

		Vec3 origin = source.subtract(camera);
		Vec3 dust = InBetweenEffectsClient.wrap(camera.scale(DUST_SCALE).add(DUST_DRIFT.scale(seconds)), NOISE_PERIOD);
		frames[lamp].set(
				(float) origin.x, (float) origin.y, (float) origin.z, (float) SOURCE_RADIUS,
				(float) axis.x, (float) axis.y, (float) axis.z, (float) SPREAD,
				(float) (floorY - camera.y), (float) BEAM_DENSITY, (float) dust.x, (float) dust.y,
				(float) dust.z, (float) DUST_SCALE, 0f, 1f
		);
		if (beamTypes[lamp] == null) {
			Matrix4f frame = frames[lamp];
			beamTypes[lamp] = InBetweenShaders.spotlight(() -> frame);
		}

		InBetweenMesh mesh = new InBetweenMesh(beamTypes[lamp], InBetweenMesh.ORDER_LIGHT, false);
		Vec3 fromSource = camera.subtract(source);
		double cx = fromSource.dot(side);
		double cy = fromSource.dot(up);
		double cz = fromSource.dot(axis);
		double half = near + slope * Math.max(0, Math.min(reach, cz)) + INSIDE_MARGIN;
		if (cz > -INSIDE_MARGIN && cz < reach + INSIDE_MARGIN && Math.abs(cx) < half && Math.abs(cy) < half) {
			mesh.addScreenQuad(view, color);
			return mesh;
		}
		double[][] corners = {
				{-near, -near, 0}, {near, -near, 0}, {near, near, 0}, {-near, near, 0},
				{-far, -far, reach}, {far, -far, reach}, {far, far, reach}, {-far, far, reach}
		};
		Vec3[] frame = {side, up, axis};
		if (cz < 0) addFace(mesh, origin, frame, corners, color, 0, 1, 2, 3);
		if (cz > reach) addFace(mesh, origin, frame, corners, color, 4, 7, 6, 5);
		if (cx - slope * cz > near) addFace(mesh, origin, frame, corners, color, 1, 5, 6, 2);
		if (-cx - slope * cz > near) addFace(mesh, origin, frame, corners, color, 0, 3, 7, 4);
		if (cy - slope * cz > near) addFace(mesh, origin, frame, corners, color, 3, 2, 6, 7);
		if (-cy - slope * cz > near) addFace(mesh, origin, frame, corners, color, 0, 4, 5, 1);
		return mesh.isEmpty() ? null : mesh;
	}

	private static void addFace(InBetweenMesh mesh, Vec3 origin, Vec3[] frame, double[][] corners, int color, int... indices) {
		for (int index : indices) {
			Vec3 corner = origin.add(frame[0].scale(corners[index][0])).add(frame[1].scale(corners[index][1])).add(frame[2].scale(corners[index][2]));
			mesh.add(corner.x, corner.y, corner.z, 0, 0, color);
		}
	}

	private static void addPool(InBetweenMesh mesh, Vec3 camera, Vec3 source, Vec3 axis, double floorY, int color) {
		double vertical = -axis.y;
		double length = (source.y - floorY) / vertical;
		double radius = SOURCE_RADIUS + SPREAD * length;
		Vec3 center = source.add(axis.scale(length)).subtract(camera).add(0, POOL_LIFT, 0);
		Vec3 flat = new Vec3(axis.x, 0, axis.z);
		Vec3 along = flat.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : flat.normalize();
		Vec3 across = new Vec3(-along.z, 0, along.x).scale(radius);
		along = along.scale(radius / vertical);
		mesh.add(center.x - along.x - across.x, center.y, center.z - along.z - across.z, 0, 0, color);
		mesh.add(center.x + along.x - across.x, center.y, center.z + along.z - across.z, 1, 0, color);
		mesh.add(center.x + along.x + across.x, center.y, center.z + along.z + across.z, 1, 1, color);
		mesh.add(center.x - along.x + across.x, center.y, center.z - along.z + across.z, 0, 1, color);
	}

	private static Identifier getPoolTexture() {
		if (poolTexture != null) return poolTexture;
		NativeImage image = new NativeImage(POOL_TEXELS, POOL_TEXELS, false);
		for (int y = 0; y < POOL_TEXELS; y++) {
			for (int x = 0; x < POOL_TEXELS; x++) {
				double dx = (x + 0.5) / POOL_TEXELS * 2 - 1;
				double dy = (y + 0.5) / POOL_TEXELS * 2 - 1;
				double edge = smooth((Math.sqrt(dx * dx + dy * dy) - POOL_SOFT_START) / (1 - POOL_SOFT_START));
				InBetweenShaders.setPixel(image, x, y, (int) Math.round(255 * (1 - edge)) * 0x010101);
			}
		}
		poolTexture = IdentifierHelper.lifeseries("dynamic/in_between_spotlight_pool");
		InBetweenShaders.registerLinearTexture(poolTexture, "In-Between spotlight", image);
		return poolTexture;
	}

	private static double smooth(double progress) {
		double t = Math.max(0, Math.min(1, progress));
		return t * t * (3 - 2 * t);
	}

	private static void createFireworks(String shape) {
		Minecraft client = Minecraft.getInstance();
		ClientLevel level = client.level;
		if (show == null || level == null || client.player == null) return;
		Vec3 center = show.floor.add(0, FIREWORK_HEIGHT, 0);
		for (int side = -1; side <= 1; side += 2) {
			Vec3 pos = center.add(show.right.scale(side * FIREWORK_SIDE));
			if (shape.equals("face")) createFace(client, level, pos, show.right);
			else createFirework(level, pos, shape.toUpperCase(Locale.ROOT));
		}
	}

	private static void createFirework(ClientLevel level, Vec3 pos, String shape) {
		//? if <= 1.20.3 {
		/*CompoundTag explosion = new CompoundTag();
		explosion.putByte("Type", (byte) FireworkRocketItem.Shape.valueOf(shape).getId());
		explosion.putIntArray("Colors", FIREWORK_COLORS);
		ListTag explosions = new ListTag();
		explosions.add(explosion);
		CompoundTag fireworks = new CompoundTag();
		fireworks.put("Explosions", explosions);
		level.createFireworks(pos.x, pos.y, pos.z, 0, 0, 0, fireworks);
		*///?} else {
		List<FireworkExplosion> explosions = List.of(new FireworkExplosion(FireworkExplosion.Shape.valueOf(shape), IntList.of(FIREWORK_COLORS), IntList.of(), false, false));
		//? if <= 26.2 {
		//level.createFireworks(pos.x, pos.y, pos.z, 0, 0, 0, explosions);
		//?} else {
		level.createFireworks(pos.x, pos.y, pos.z, 0, 0, 0, explosions, true);
		//?}
		//?}
	}

	private static void createFace(Minecraft client, ClientLevel level, Vec3 pos, Vec3 right) {
		Optional<Resource> resource = client.getResourceManager().getResource(FACE_TEXTURE);
		if (resource.isEmpty() || client.player == null) return;
		try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int argb = InBetweenShaders.getPixel(image, x, y);
					if ((argb >>> 24) < 128) continue;
					Vec3 offset = right.scale((x + 0.5 - image.getWidth() / 2.0) * FACE_PIXEL).add(0, (image.getHeight() / 2.0 - y - 0.5) * FACE_PIXEL, 0);
					Vec3 speed = offset.scale(1 - SPARK_FRICTION);
					Particle spark = client.particleEngine.createParticle(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
					if (spark instanceof SimpleAnimatedParticle coloured) coloured.setColor(argb & 0xFFFFFF);
				}
			}
		} catch (Exception e) {
			LifeSeries.LOGGER.error("[In-Between] Failed to create the trivia bot firework", e);
			return;
		}
		boolean far = client.player.getEyePosition().distanceToSqr(pos) >= 256;
		level.playLocalSound(pos.x, pos.y, pos.z, far ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.AMBIENT, 20, 0.95f + level.getRandom().nextFloat() * 0.1f, true);
		//? if <= 1.21.6 {
		/*Particle flash = client.particleEngine.createParticle(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0, 0, 0);
		if (flash != null) flash.setColor(1, 1, 1);
		*///?} else {
		client.particleEngine.createParticle(ColorParticleOption.create(ParticleTypes.FLASH, DyeColor.WHITE.getFireworkColor()), pos.x, pos.y, pos.z, 0, 0, 0);
		//?}
	}

	private static class Show {
		private final Vec3 floor;
		private final Vec3 right;
		private final Vec3 back;
		private final long start = System.nanoTime();
		private int botId = -1;
		private double trackStart = -1;
		private double fadeStart = -1;
		@Nullable
		private Vec3 lastBotTarget;

		private Show(Vec3 floor, float yaw) {
			double radians = Math.toRadians(yaw);
			this.floor = floor;
			this.right = new Vec3(Math.cos(radians), 0, Math.sin(radians));
			this.back = new Vec3(Math.sin(radians), 0, -Math.cos(radians));
		}

		private double age() {
			return (System.nanoTime() - start) / 1.0E9;
		}

		private void track(int botId) {
			this.botId = botId;
			this.trackStart = age();
		}

		private Vec3 getSource(int lamp) {
			return floor.add(right.scale((lamp - 1) * SOURCE_SPACING)).add(0, SOURCE_HEIGHT, 0);
		}

		private Vec3 getDanceTarget(int lamp, double seconds) {
			double[] wave = DANCE[lamp];
			double x = 0.55 * Math.sin(wave[0] * seconds + wave[1]) + 0.3 * Math.sin(wave[2] * seconds + wave[3]);
			double z = 0.55 * Math.sin(wave[4] * seconds + wave[5]) + 0.3 * Math.sin(wave[6] * seconds + wave[7]);
			double distance = Math.max(1, Math.sqrt(x * x + z * z));
			return floor.add(right.scale(x / distance * DANCE_RADIUS)).add(back.scale(z / distance * DANCE_RADIUS));
		}

		@Nullable
		private Vec3 getBotTarget(ClientLevel level, double age, float partialTick) {
			if (trackStart < 0) return null;
			Entity bot = level.getEntity(botId);
			if (bot instanceof TriviaBot && !bot.isRemoved()) {
				lastBotTarget = bot.getPosition(partialTick).add(0, bot.getBbHeight() / 2, 0);
			}
			else if (lastBotTarget != null && fadeStart < 0) {
				fadeStart = age;
			}
			return lastBotTarget != null ? lastBotTarget : floor.add(0, InBetweenTriviaManager.BOT_DROP_HEIGHT + 0.9, 0);
		}

		private double getLight(double age) {
			if (fadeStart < 0 && age > MAX_SECONDS) fadeStart = age;
			double off = fadeStart < 0 ? 0 : smooth((age - fadeStart) / FADE_SECONDS);
			return smooth(age / TURN_ON_SECONDS) * (1 - off);
		}

		private double getWhiteness(double age) {
			return trackStart < 0 ? 0 : smooth((age - trackStart) / TO_BOT_SECONDS);
		}
	}
}
