package net.mat0u5.lifeseries.client.features.inbetween;

import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenFileSound;
import net.mat0u5.lifeseries.client.features.inbetween.sound.InBetweenSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class InBetweenSoundsClient {
	private static final double BACKGROUND_VOLUME = 0.1;
	private static final double BACKGROUND_WHISPER_VOLUME = 0.25;
	private static final double WHISPER_VOLUME = 0.15;
	private static final int WHISPER_MIN_DELAY = 20;
	private static final int WHISPER_MAX_DELAY = 80;
	private static final double FADE_IN_STEP = 1 / 40.0;
	private static final double FADE_OUT_STEP = 1 / 80.0;
	private static final double WHISPER_FADE_STEP = 1 / 20.0;
	private static final String WHISPER_EVENT = "finallife_inbetween_whisper_death";
	private static final String WHISPER_FOLDER = "finallife/inbetween/whisper/death";
	// Played in this order first, then random ones from the sound event
	private static final Map<Integer, List<String>> WHISPER_ORDER = Map.of(
			1, List.of("you_are_here", "build_up", "build_up_get_stronger", "win", "look_to_the_past", "trust_no_one", "build_up", "look_to_the_past"),
			2, List.of("adapt_adapt", "bring_them_back", "bring_them_back_surprise_us", "try_this")
	);
	private static final Random random = new Random();

	private static final SoundLoop background = new SoundLoop(IdentifierHelper.lifeseries("finallife_inbetween_background"), BACKGROUND_VOLUME);
	private static final SoundLoop backgroundWhisper = new SoundLoop(IdentifierHelper.lifeseries("finallife_inbetween_background_whisper"), BACKGROUND_WHISPER_VOLUME);
	private static int whisperDeath = 0;
	private static int whisperIndex = 0;
	@Nullable
	private static InBetweenSound whisper;
	private static int whisperDelay = 0;
	private static double whisperFade = 0;
	private static boolean visible = false;

	public static void onPacket(List<String> entries) {
		background.enabled = entries.contains("background");
		backgroundWhisper.enabled = entries.contains("background_whisper");
		int death = 0;
		for (String entry : entries) {
			if (!entry.startsWith("whispers;")) continue;
			try {
				death = Integer.parseInt(entry.substring("whispers;".length()));
			} catch (NumberFormatException ignored) {}
		}
		if (death != whisperDeath) whisperIndex = 0;
		whisperDeath = death;
	}

	public static void tick(LocalPlayer player) {
		Minecraft client = Minecraft.getInstance();
		if (client.isPaused()) return;
		boolean wasVisible = visible;
		visible = InBetweenManager.isInInBetween(player) && !InBetweenTransition.isCovering();
		if (visible && !wasVisible) {
			SimplePackets.IN_BETWEEN_LOADED.sendToServer();
			whisperDelay = randomDelay();
		}
		SoundManager soundManager = client.getSoundManager();
		background.tick(soundManager, visible);
		backgroundWhisper.tick(soundManager, visible);
		tickWhispers(soundManager, player);
	}

	private static void tickWhispers(SoundManager soundManager, LocalPlayer player) {
		double target = visible ? InBetweenWindowsClient.getWhisperVolume(player.getEyePosition()) : 0;
		whisperFade = target > whisperFade ? Math.min(target, whisperFade + WHISPER_FADE_STEP) : Math.max(target, whisperFade - WHISPER_FADE_STEP);
		if (whisper != null) {
			if (soundManager.isActive(whisper)) return;
			whisper = null;
			whisperDelay = randomDelay();
		}
		if (!visible) return;
		if (whisperDelay > 0) {
			whisperDelay--;
			return;
		}
		if (target <= 0 || whisperDeath <= 0) return; // Near a window
		whisper = nextWhisper(soundManager);
		if (whisper != null) soundManager.play(whisper);
	}

	@Nullable
	private static InBetweenSound nextWhisper(SoundManager soundManager) {
		List<String> order = WHISPER_ORDER.getOrDefault(whisperDeath, List.of());
		if (whisperIndex < order.size()) {
			Identifier file = IdentifierHelper.lifeseries(WHISPER_FOLDER + whisperDeath + "/" + order.get(whisperIndex++));
			return new InBetweenFileSound(file, SoundSource.MASTER, false, 16, () -> WHISPER_VOLUME * whisperFade);
		}
		Identifier event = IdentifierHelper.lifeseries(WHISPER_EVENT + whisperDeath);
		if (!canPlay(soundManager, event)) return null;
		return new InBetweenSound(event, SoundSource.MASTER, false, () -> WHISPER_VOLUME * whisperFade);
	}

	private static int randomDelay() {
		return WHISPER_MIN_DELAY + random.nextInt(WHISPER_MAX_DELAY - WHISPER_MIN_DELAY + 1);
	}

	private static boolean canPlay(SoundManager soundManager, Identifier sound) {
		WeighedSoundEvents events = soundManager.getSoundEvent(sound);
		return events != null && events.getWeight() > 0;
	}

	public static void clear() {
		background.enabled = false;
		backgroundWhisper.enabled = false;
		background.stop();
		backgroundWhisper.stop();
		if (whisper != null) Minecraft.getInstance().getSoundManager().stop(whisper);
		whisper = null;
		whisperDeath = 0;
		whisperIndex = 0;
		whisperFade = 0;
		visible = false;
	}

	private static class SoundLoop {
		private final Identifier sound;
		private final double volume;
		private boolean enabled = false;
		@Nullable
		private InBetweenSound playing;
		private double fade = 0;

		private SoundLoop(Identifier sound, double volume) {
			this.sound = sound;
			this.volume = volume;
		}

		private void tick(SoundManager soundManager, boolean visible) {
			boolean available = visible && enabled;
			fade = available ? Math.min(1, fade + FADE_IN_STEP) : Math.max(0, fade - FADE_OUT_STEP);
			if (fade <= 0) {
				stop();
				return;
			}
			if (playing != null && soundManager.isActive(playing)) return;
			if (playing != null) fade = 0;
			playing = null;
			if (!available || !canPlay(soundManager, sound)) return;
			playing = new InBetweenSound(sound, SoundSource.AMBIENT, true, () -> volume * fade);
			soundManager.play(playing);
		}

		private void stop() {
			if (playing != null) Minecraft.getInstance().getSoundManager().stop(playing);
			playing = null;
			fade = 0;
		}
	}
}
