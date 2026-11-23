package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.sound;

import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class InBetweenSounds {
	private static final double BACKGROUND_SOUND_START_DISTANCE = 60; // Distance at which the background music starts playing from the choices.
	private static final int WHISPER_DEATHS = 3; // Deaths with built-in whispers

	private final InBetweenInstance inBetween;
	private boolean backgroundSoundStarted = false;

	public InBetweenSounds(InBetweenInstance inBetween) {
		this.inBetween = inBetween;
	}

	public void tick(ServerPlayer player) {
		if (backgroundSoundStarted || !hasTrivia()) return;
		if (!InBetweenTriviaManager.isOver(inBetween) || !isNearChoices(player)) return;
		backgroundSoundStarted = true;
		sendToClient(player);
	}

	private boolean hasTrivia() {
		return InBetweenTriviaManager.hasTrivia(inBetween.deathNumber);
	}

	private boolean isNearChoices(ServerPlayer player) {
		for (Vec3 choice : inBetween.choiceManager.getChoiceCenters()) {
			if (player.position().distanceToSqr(choice) <= BACKGROUND_SOUND_START_DISTANCE * BACKGROUND_SOUND_START_DISTANCE) return true;
		}
		return false;
	}

	public void sendToClient(ServerPlayer player) {
		boolean trivia = hasTrivia();
		List<String> sounds = new ArrayList<>();
		if (!trivia || backgroundSoundStarted) sounds.add("background");
		if (!trivia) sounds.add("background_whisper");
		int whispers = getWhisperDeath(inBetween.deathNumber);
		if (whispers > 0) sounds.add("whispers;" + whispers);
		SimplePackets.IN_BETWEEN_SOUNDS.sendToClient(sounds, player);
	}

	public static void sendClientClearSounds(ServerPlayer player) {
		SimplePackets.IN_BETWEEN_SOUNDS.sendToClient(List.of(), player);
	}

	private static int getWhisperDeath(int deathNumber) {
		if (deathNumber <= WHISPER_DEATHS) return Math.max(1, deathNumber);
		boolean trivia = InBetweenTriviaManager.hasTrivia(deathNumber);
		for (int death = WHISPER_DEATHS; death >= 1; death--) {
			if (InBetweenTriviaManager.hasTrivia(death) == trivia) return death;
		}
		return 0;
	}
}
