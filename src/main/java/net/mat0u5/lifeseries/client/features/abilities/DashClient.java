package net.mat0u5.lifeseries.client.features.abilities;

import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.EffectRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >= 1.21.11
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class DashClient {
	private static final int DOUBLE_TAP_WINDOW_TICKS = 5;
	private static final int DASH_TICKS = 4; // Dash duration
	private static final double DASH_SPEED = 0.95; // Blocks per tick -> ~4 blocks in total
	private static final double END_SPEED = 0.25;

	private static final int[] FORWARD = {1, -1, 0, 0};
	private static final int[] STRAFE = {0, 0, 1, -1};

	private static final float BLUR_STRENGTH = 0.9f;
	private static final float BLUR_HOLD = 0.08f; // Seconds
	private static final float BLUR_FADE = 0.17f;

	//All times are in nanos
	private static final long TRAIL_SAVE_TIME = 400_000_000L;
	private static final long GHOST_LIFETIME = 350_000_000L;
	private static final double GHOST_SPACING = 0.5;
	private static final double MIN_GHOST_DISTANCE = 0.4;
	private static final float GHOST_ALPHA = 0.45f;
	private static final float DEPTH_PASS_ALPHA = 0.125f;
	private static final int DEPTH_PASS = 1;
	private static final int COLOR_PASS = 2;
	private static final int DEPTH_PASS_ORDER = 1000;
	private static final int COLOR_PASS_ORDER = 2000;

	private static final int LAST_TAP_OLD = Integer.MIN_VALUE/2;
	private static final boolean[] wasDown = new boolean[FORWARD.length];
	private static final int[] lastTap = new int[FORWARD.length];
	private static int ticks;
	private static int dashTicks;
	private static Vec3 dashDirection = Vec3.ZERO;
	private static long lastDash = -1;

	private static final Map<Integer, Trail> trails = new HashMap<>(); // Entity id -> trail
	private static float ghostAlpha = -1;
	private static int ghostPass;
	private static boolean creatingRenderType;
	private static final List<Runnable> delayedGhosts = new ArrayList<>();
	private static final int MAX_DELAYED_GHOSTS = 256;

	static {
		Arrays.fill(lastTap, LAST_TAP_OLD);
	}

	private record Ghost(double x, double y, double z, long time) {}

	private static class Trail {
		private final long start = System.nanoTime();
		private final List<Ghost> ghosts = new ArrayList<>();
		private Ghost lastSample;

		private void save(double x, double y, double z, long now) {
			Ghost sample = new Ghost(x, y, z, now);
			Ghost previous = lastSample;
			lastSample = sample;
			if (now - start > TRAIL_SAVE_TIME || previous == null || now <= previous.time) return;
			double speed = Math.sqrt(distanceSq(previous, x, y, z)) / ((now - previous.time) / 1.0E9);
			if (speed < 8) return; // Check if moving too slow
			if (!ghosts.isEmpty() && distanceSq(ghosts.get(ghosts.size() - 1), x, y, z) < GHOST_SPACING * GHOST_SPACING) return;
			ghosts.add(sample);
		}
	}

	private static double distanceSq(Ghost ghost, double x, double y, double z) {
		double dx = ghost.x - x;
		double dy = ghost.y - y;
		double dz = ghost.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public interface GhostRenderer {
		void render(double dx, double dy, double dz);
	}

	public static void onDashGhostsPayload(int entityId) {
		trails.put(entityId, new Trail());
	}

	public static void tick(LocalPlayer player) {
		ticks++;
		long now = System.nanoTime();
		trails.values().removeIf(trail -> now - trail.start > TRAIL_SAVE_TIME + GHOST_LIFETIME); // Remove old trails

		Options options = Minecraft.getInstance().options;
		//Store last kays down and last tap intervals
		KeyMapping[] keys = {options.keyUp, options.keyDown, options.keyLeft, options.keyRight};
		for (int i = 0; i < keys.length; i++) {
			boolean down = keys[i].isDown();
			boolean tapped = down && !wasDown[i];
			wasDown[i] = down;
			if (!tapped) continue;
			if (ticks - lastTap[i] <= DOUBLE_TAP_WINDOW_TICKS && tryDash(player, i)) {
				lastTap[i] = LAST_TAP_OLD;
			}
			else {
				lastTap[i] = ticks;
			}
		}
	}

	private static boolean tryDash(LocalPlayer player, int direction) {
		if (dashTicks > 0 || !canDash(player) || !AbilityChargesClient.hasCharge(Abilities.DASH)) return false;
		SimplePackets.DASH.sendToServer();
		if (!AbilityChargesClient.tryUse(Abilities.DASH, player)) return true;
		float yaw = player.getYRot() * Mth.DEG_TO_RAD;
		float sin = Mth.sin(yaw);
		float cos = Mth.cos(yaw);
		dashDirection = new Vec3(STRAFE[direction] * cos - FORWARD[direction] * sin, 0, FORWARD[direction] * cos + STRAFE[direction] * sin);
		dashTicks = DASH_TICKS + 1;
		lastDash = System.nanoTime();
		trails.put(player.getId(), new Trail()); // Own trail (can be seen in F5 for example)
		return true;
	}

	private static boolean canDash(LocalPlayer player) {
		return !player.isPassenger() && !player.isFallFlying() && !player.isSpectator() && !player.isSleeping() && !player.getAbilities().flying && !EffectRegistry.hasStickyFeet(player);
	}

	public static void applyMotion(LocalPlayer player) {
		if (dashTicks <= 0) return;
		dashTicks--;
		Vec3 movement = player.getDeltaMovement();
		if (dashTicks == 0) {
			player.setDeltaMovement(dashDirection.x * END_SPEED, movement.y, dashDirection.z * END_SPEED);
			return;
		}
		player.setDeltaMovement(dashDirection.x * DASH_SPEED, player.onGround() ? movement.y : 0, dashDirection.z * DASH_SPEED);
	}

	public static void renderBlur(GuiGraphicsExtractor graphics) {
		if (lastDash < 0) return;
		float seconds = (System.nanoTime() - lastDash) / 1.0E9f;
		float blur = BLUR_STRENGTH * Mth.clamp(1 - (seconds - BLUR_HOLD) / BLUR_FADE, 0, 1);
		if (blur <= 0) {
			lastDash = -1;
			return;
		}
		if (!InBetweenShaders.screenAvailable()) return;
		InBetweenShaders.drawScreen(graphics, 0xFF000000 | (Math.round(blur * 255) << 16), 0, 1);
	}

	// x, y, z is the CURRENT entity position, afterPass is run after each of the two passes
	public static void forEachGhost(Entity entity, double x, double y, double z, GhostRenderer renderer, Runnable afterPass) {
		if (trails.isEmpty() || isRenderingGhost()) return;
		Trail trail = trails.get(entity.getId());
		if (trail == null || entity.isInvisible()) return;
		long now = System.nanoTime();
		trail.save(x, y, z, now);
		List<Ghost> visible = new ArrayList<>();
		for (Ghost ghost : trail.ghosts) {
			if (now - ghost.time < GHOST_LIFETIME && distanceSq(ghost, x, y, z) >= MIN_GHOST_DISTANCE * MIN_GHOST_DISTANCE) {
				visible.add(ghost);
			}
		}
		if (visible.isEmpty()) return;
		try {
			for (int pass = DEPTH_PASS; pass <= COLOR_PASS; pass++) {
				ghostPass = pass;
				for (Ghost ghost : visible) {
					float alpha = GHOST_ALPHA * (1 - (float) (now - ghost.time) / GHOST_LIFETIME);
					if (pass == DEPTH_PASS) {
						ghostAlpha = Math.min(alpha, DEPTH_PASS_ALPHA);
					}
					else {
						if (alpha <= DEPTH_PASS_ALPHA) continue;
						ghostAlpha = 1 - (1 - alpha) / (1 - DEPTH_PASS_ALPHA);
					}
					renderer.render(ghost.x - x, ghost.y - y, ghost.z - z);
				}
				ghostAlpha = -1;
				afterPass.run();
			}
		} finally {
			ghostAlpha = -1;
			ghostPass = 0;
		}
	}

	public static boolean hasTrail(Entity entity) {
		return !trails.isEmpty() && !isRenderingGhost() && trails.containsKey(entity.getId());
	}

	public static void deferGhosts(Runnable render) {
		if (delayedGhosts.size() >= MAX_DELAYED_GHOSTS) delayedGhosts.clear();
		delayedGhosts.add(render);
	}

	public static void renderDelayedGhosts() {
		if (delayedGhosts.isEmpty()) return;
		List<Runnable> renders = new ArrayList<>(delayedGhosts);
		delayedGhosts.clear();
		renders.forEach(Runnable::run);
	}

	public static boolean isGhostOrder(int order) {
		return order >= DEPTH_PASS_ORDER;
	}

	public static boolean isRenderingGhost() {
		return ghostAlpha >= 0;
	}

	public static float ghostAlpha(float alpha) {
		return alpha * ghostAlpha;
	}

	public static int ghostColor(int argb) {
		return (Math.round((argb >>> 24) * ghostAlpha) << 24) | (argb & 0xFFFFFF);
	}

	public static int ghostOrder(int order) {
		if (!isRenderingGhost()) return order;
		return order + (ghostPass == DEPTH_PASS ? DEPTH_PASS_ORDER : COLOR_PASS_ORDER);
	}

	public static boolean shouldReplaceRenderType() {
		return isRenderingGhost() && !creatingRenderType;
	}

	public static RenderType ghostRenderType(Identifier texture) {
		boolean affectsOutline = ghostPass != DEPTH_PASS;
		creatingRenderType = true;
		try {
			//? if <= 1.21.9 {
			//return RenderType.entityTranslucent(texture, affectsOutline);
			//?} else {
			return RenderTypes.entityTranslucent(texture, affectsOutline);
			//?}
		} finally {
			creatingRenderType = false;
		}
	}

	public static void reset() {
		Arrays.fill(wasDown, false);
		Arrays.fill(lastTap, LAST_TAP_OLD);
		dashTicks = 0;
		lastDash = -1;
		trails.clear();
		delayedGhosts.clear();
		ghostAlpha = -1;
		ghostPass = 0;
	}
}
