package net.mat0u5.lifeseries.client.features.inbetween;

import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

//? if <= 1.21.6
//import net.minecraft.client.gui.screens.ReceivingLevelScreen;

public class InBetweenTransition {
	private static final double SWALLOW_SECONDS = 4.0;
	private static final double FADE_SECONDS = 2.0;
	private static final double MIN_COVER_SECONDS = 0.4;
	private static final double MAX_COVER_SECONDS = 10;
	private static final double MAX_FADE_STEP_SECONDS = 0.05;

	private enum Stage {NONE, SWALLOW, COVER, FADE}

	private static Stage stage = Stage.NONE;
	private static boolean black = false;
	private static double fadeSeconds = 0;
	private static long lastUpdate = -1;
	private static float clockOffset = 0;
	private static long stageStart = 0;
	private static boolean fadeQueued = false;
	private static long readySince = -1;
	@Nullable
	private static Object lastLevel = null;
	@Nullable
	private static String fadeSound = null;

	public static void onPacket(String value) {
		if (value.startsWith("sound:")) queueFadeSound(value.substring(6));
		else if (value.equals("swallow")) startSwallow();
		else if (value.equals("fade_in")) startFadeIn(false);
		else if (value.equals("fade_in_black")) startFadeIn(true);
	}

	private static void startSwallow() {
		setStage(Stage.SWALLOW);
		black = false;
		clockOffset = (float) (Math.random() * 8);
		fadeQueued = false;
	}

	private static void startFadeIn(boolean toBlack) {
		if (stage != Stage.COVER && stage != Stage.SWALLOW) {
			setStage(Stage.COVER);
			black = toBlack;
		}
		fadeQueued = true;
		readySince = -1;
	}

	private static void queueFadeSound(String soundId) {
		fadeSound = soundId;
		if (stage != Stage.COVER && stage != Stage.SWALLOW) playFadeSound();
	}

	private static void playFadeSound() {
		if (fadeSound == null) return;
		SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.parse(fadeSound));
		fadeSound = null;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1, 1));
	}

	public static void stopTransition() {
		setStage(Stage.NONE);
		fadeQueued = false;
		fadeSound = null;
	}

	private static void setStage(Stage newStage) {
		stage = newStage;
		stageStart = System.nanoTime();
		fadeSeconds = 0;
	}

	private static double secondsInStage() {
		return (System.nanoTime() - stageStart) / 1.0E9;
	}

	public static boolean shouldCoverLoadingScreen() {
		update();
		return stage == Stage.SWALLOW || stage == Stage.COVER;
	}

	public static boolean isCovering() {
		return shouldCoverLoadingScreen() || isLoadingScreen(RenderUtils.getScreen());
	}

	public static int getCoverColor() {
		return black ? 0xFF000000 : 0xFFFFFFFF;
	}

	private static void update() {
		long now = System.nanoTime();
		double step = lastUpdate < 0 ? 0 : Math.min((now - lastUpdate) / 1.0E9, MAX_FADE_STEP_SECONDS);
		lastUpdate = now;
		if (stage == Stage.NONE) return;
		if (stage == Stage.FADE) fadeSeconds += step;
		Minecraft client = Minecraft.getInstance();
		if (client.level != lastLevel) {
			lastLevel = client.level;
			readySince = -1;
		}
		double seconds = secondsInStage();
		if (stage == Stage.SWALLOW && seconds >= SWALLOW_SECONDS) {
			setStage(Stage.COVER);
		}
		else if (stage == Stage.COVER) {
			boolean ready = client.level != null && client.player != null && !isLoadingScreen(RenderUtils.getScreen());
			if (!ready) readySince = -1;
			else if (readySince < 0) readySince = now;
			boolean loaded = readySince >= 0 && (now - readySince) / 1.0E9 >= MIN_COVER_SECONDS;
			if ((fadeQueued && loaded) || seconds >= MAX_COVER_SECONDS) {
				fadeQueued = false;
				setStage(Stage.FADE);
				playFadeSound();
			}
		}
		else if (stage == Stage.FADE && (fadeSeconds >= FADE_SECONDS || seconds >= FADE_SECONDS + 5)) {
			setStage(Stage.NONE);
		}
	}

	private static boolean isLoadingScreen(@Nullable Screen screen) {
		if (screen instanceof LevelLoadingScreen) return true;
		//? if <= 1.21.6
		//if (screen instanceof ReceivingLevelScreen) return true;
		return false;
	}

	public static void render(GuiGraphicsExtractor graphics) {
		update();
		if (stage == Stage.NONE) return;
		double seconds = secondsInStage();
		float cover = 0;
		float blur = 0;
		float swallow = 0;
		switch (stage) {
			case SWALLOW -> swallow = (float) Math.min(1, seconds / SWALLOW_SECONDS);
			case COVER -> cover = 1;
			case FADE -> {
				cover = (float) Math.max(0, 1 - fadeSeconds / FADE_SECONDS);
				blur = cover;
			}
		}
		if (cover <= 0 && swallow <= 0) return;

		if (cover < 1 && InBetweenShaders.screenAvailable()) {
			int color = ((black ? 0x80 : 0xFF) << 24) | (channel(cover) << 16) | (channel(blur) << 8) | channel(swallow);
			Minecraft client = Minecraft.getInstance();
			float aspect = (float) client.getWindow().getWidth() / Math.max(1, client.getWindow().getHeight());
			InBetweenShaders.drawScreen(graphics, color, clockOffset + (float) seconds, aspect);
			return;
		}
		float alpha = cover < 1 ? Math.max(cover, swallow * swallow) : 1;
		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), (channel(alpha) << 24) | (getCoverColor() & 0xFFFFFF));
	}

	private static int channel(float value) {
		return Math.round(Math.max(0, Math.min(1, value)) * 255);
	}
}
