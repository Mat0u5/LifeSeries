package net.mat0u5.lifeseries.client.features.abilities;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.SecondWindAbility;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.List;

//? if <= 1.21
//import com.mojang.blaze3d.systems.RenderSystem;
//? if >= 1.21.2 && <= 1.21.5
//import net.minecraft.client.renderer.rendertype.RenderType;
//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines;

public class SecondWindClient {
	private static final Identifier GLOW_TEXTURE = IdentifierHelper.lifeseries("textures/misc/second_wind_glow.png");
	private static final int BORDER_COLOR = 0xFFB600;
	private static final int FLASH_COLOR = 0xFFFFFF;
	private static final int GLOW_COLOR = 0xFFC844;
	private static final float PULSE_ALPHA = 0.12f; // At the heartbeat peak
	private static final float PULSE_EDGE_ALPHA = 0.04f;
	private static final float EDGE_ALPHA = 0.03f; // Between the heartbeats
	private static final float PULSE_RISE = 0.1f;
	private static final float PULSE_HOLD = 0.05f;
	private static final float PULSE_FALL = 0.12f;

	//All times are in nanos
	private static final long FADE_IN = 200_000_000L;
	private static final long FADE_OUT = 150_000_000L;
	private static final long END_DELAY = 250_000_000L; // After the last heartbeat
	private static final long FLASH = 100_000_000L; // White borders when it ends
	private static final long HEARTBEAT_TIMEOUT = 1_500_000_000L; // Ends the effect if the heartbeats stop arriving
	private static final int THRESHOLD_HEART = (SecondWindAbility.TRIGGER_HEALTH + 1) / 2 - 1; // The fifth heart
	private static final int[][] OUTLINE_FILL_ORDER = {{4, 1}, {5, 0}, {6, 0}, {7, 1}, {8, 2}, {8, 3}, {8, 4}, {7, 5}, {6, 6}, {5, 7}, {4, 8}, {3, 7}, {2, 6}, {1, 5}, {0, 4}, {0, 3}, {0, 2}, {1, 1}, {2, 0}, {3, 0}};
	private static final long START = System.nanoTime();

	private static long cooldownStart = -1;
	private static long cooldownEnd = -1;
	private static long effectStart = -1;
	private static long effectEnd = -1;
	private static long lastHeartbeat = -1;

	private static final int[] heartX = new int[64];
	private static final int[] heartY = new int[64];
	private static int heartCount;

	public static void onPayload(List<String> payload) {
		if (payload.isEmpty()) return;
		long now = timeSinceStart();
		try {
			String action = payload.get(0);
			if (action.equalsIgnoreCase("trigger")) {
				effectEnd = -1;
				lastHeartbeat = -1;
				effectStart = now;
				int cooldown = Integer.parseInt(payload.get(1));
				setCooldown(now, cooldown, cooldown);
			}
			else if (action.equalsIgnoreCase("heartbeat")) {
				if (effectStart < 0) effectStart = now;
				lastHeartbeat = now;
				if (Integer.parseInt(payload.get(1)) <= 0) effectEnd = now + END_DELAY;
			}
			else if (action.equalsIgnoreCase("stop")) {
				clearEffect();
			}
			else if (action.equalsIgnoreCase("cooldown")) {
				setCooldown(now, Integer.parseInt(payload.get(1)), Integer.parseInt(payload.get(2)));
			}
		} catch (Exception ignored) {}
	}

	private static void setCooldown(long now, int remainingTicks, int totalTicks) {
		if (remainingTicks <= 0 || totalTicks <= 0) {
			cooldownStart = -1;
			cooldownEnd = -1;
			return;
		}
		cooldownStart = now - (totalTicks - remainingTicks) * 50_000_000L;
		cooldownEnd = now + remainingTicks * 50_000_000L;
	}

	public static void renderGlowVignette(GuiGraphicsExtractor graphics) {
		long now = timeSinceStart();
		update(now);
		if (effectStart < 0) return;
		Minecraft client = Minecraft.getInstance();
		//? if <= 26.1 {
		//if (client.options.hideGui) return;
		//?} else {
		if (client.gui.hud.isHidden()) return;
		//?}
		float pulse = getPulseStep(now);
		float edgeAlpha = EDGE_ALPHA * sustained(now) + PULSE_EDGE_ALPHA * pulse;
		if (pulse <= 0 && edgeAlpha <= 0) return;

		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		//? if <= 1.21.5 {
		/*graphics.pose().pushPose();
		graphics.pose().translate(0, 0, -500); // Behind the hotbar, which is at z -90 on old versions
		*///?}
		if (pulse > 0) graphics.fill(0, 0, width, height, argb(PULSE_ALPHA * pulse, GLOW_COLOR));
		if (edgeAlpha > 0) {
			//? if <= 1.21 {
			/*RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			graphics.setColor((GLOW_COLOR >> 16 & 0xFF) / 255f, (GLOW_COLOR >> 8 & 0xFF) / 255f, (GLOW_COLOR & 0xFF) / 255f, edgeAlpha);
			graphics.blit(GLOW_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, width, height);
			graphics.setColor(1, 1, 1, 1);
			RenderSystem.disableBlend();
			*///?} else if <= 1.21.5 {
			//graphics.blit(RenderType::guiTextured, GLOW_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, width, height, argb(edgeAlpha, GLOW_COLOR));
			//?} else {
			graphics.blit(RenderPipelines.GUI_TEXTURED, GLOW_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, width, height, argb(edgeAlpha, GLOW_COLOR));
			//?}
		}
		//? if <= 1.21.5
		//graphics.pose().popPose();
	}

	public static void startHeartRender() {
		heartCount = 0;
	}

	public static void onRenderHeartContainer(int x, int y) {
		if (heartCount >= heartX.length) return;
		heartX[heartCount] = x;
		heartY[heartCount] = y;
		heartCount++;
	}

	public static void renderHeartBorders(GuiGraphicsExtractor graphics, float maxHealth) {
		int count = heartCount;
		heartCount = 0;
		if (count == 0 || !LifeSeries.isSeason(Seasons.FINAL_LIFE)) return;
		long now = timeSinceStart();
		update(now);
		if (effectStart >= 0) {
			boolean flash = effectEnd >= 0 && now >= effectEnd;
			if (!flash || now < effectEnd + FLASH) {
				for (int i = 0; i < count; i++) {
					drawBorder(graphics, heartX[i], heartY[i], flash ? FLASH_COLOR : BORDER_COLOR, 1);
				}
				return;
			}
		}
		if (!AbilitiesClient.isActive(Abilities.SECOND_WIND) || THRESHOLD_HEART >= Mth.ceil(maxHealth / 2)) return;
		int index = count - 1 - THRESHOLD_HEART;
		if (index < 0) return;
		drawBorder(graphics, heartX[index], heartY[index], BORDER_COLOR, cooldownProgress(now));
	}

	private static void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int color, float progress) {
		for (int i = 0; i < OUTLINE_FILL_ORDER.length; i++) {
			float alpha = Mth.clamp(progress * OUTLINE_FILL_ORDER.length - i, 0, 1);
			if (alpha <= 0) return;
			int px = x + OUTLINE_FILL_ORDER[i][0];
			int py = y + OUTLINE_FILL_ORDER[i][1];
			graphics.fill(px, py, px + 1, py + 1, argb(alpha, color));
		}
	}

	private static float cooldownProgress(long now) {
		if (cooldownEnd < 0 || now >= cooldownEnd) return 1;
		return Mth.clamp((float) (now - cooldownStart) / (cooldownEnd - cooldownStart), 0, 1);
	}

	private static float sustained(long now) {
		float fade = smoothstepInterpolation((float) (now - effectStart) / FADE_IN);
		if (effectEnd >= 0 && now > effectEnd) fade *= 1 - smoothstepInterpolation((float) (now - effectEnd) / FADE_OUT);
		return fade;
	}

	private static float getPulseStep(long now) {
		if (lastHeartbeat < 0) return 0;
		float seconds = (now - lastHeartbeat) / 1.0E9f;
		if (seconds < PULSE_RISE) return smoothstepInterpolation(seconds / PULSE_RISE);
		seconds -= PULSE_RISE;
		if (seconds < PULSE_HOLD) return 1;
		return 1 - smoothstepInterpolation((seconds - PULSE_HOLD) / PULSE_FALL);
	}

	private static void update(long now) {
		if (effectStart < 0) return;
		long lastSignal = Math.max(effectStart, lastHeartbeat);
		if (effectEnd < 0 && now - lastSignal > HEARTBEAT_TIMEOUT) {
			effectEnd = lastSignal + HEARTBEAT_TIMEOUT;
		}
		if (effectEnd >= 0 && now > effectEnd + Math.max(FLASH, FADE_OUT)) {
			clearEffect();
		}
	}

	private static void clearEffect() {
		effectStart = -1;
		effectEnd = -1;
		lastHeartbeat = -1;
	}

	public static void reset() {
		clearEffect();
		cooldownStart = -1;
		cooldownEnd = -1;
		heartCount = 0;
	}

	private static long timeSinceStart() {
		return System.nanoTime() - START;
	}

	private static float smoothstepInterpolation(float x) {
		x = Mth.clamp(x, 0, 1);
		return x * x * (3 - 2*x);
	}

	private static int argb(float alpha, int rgb) {
		return (Math.round(Mth.clamp(alpha, 0, 1) * 255) << 24) | (rgb & 0xFFFFFF);
	}
}
