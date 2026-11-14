package net.mat0u5.lifeseries.config.modifiable;

import java.util.List;

import static net.mat0u5.lifeseries.config.modifiable.ModifiableSound.*;

public enum SoundBehavior {

	// Playing one of these stops the rest
	ONLY_PLAY_LATEST(
			WILDLIFE_TRIVIA_INTRO,
			WILDLIFE_TRIVIA_SUSPENSE,
			WILDLIFE_TRIVIA_SUSPENSE_END,
			WILDLIFE_TRIVIA_ANALYZING,

			NICELIFE_SANTABOT_INTRODUCTION_LONG,
			NICELIFE_SANTABOT_INTRODUCTION_SHORT,
			NICELIFE_SANTABOT_INTRO,
			NICELIFE_SANTABOT_SUSPENSE,
			NICELIFE_SANTABOT_SUSPENSE_END,
			NICELIFE_SANTABOT_ANALYZING,
			NICELIFE_SANTABOT_INCORRECT,
			NICELIFE_SANTABOT_VOTE,
			NICELIFE_SANTABOT_TURN
	),

	// Plays only the closest instance of the same sound.
	ONLY_CLOSEST(
			WILDLIFE_TRIVIA_INTRO,
			WILDLIFE_TRIVIA_SUSPENSE,
			WILDLIFE_TRIVIA_SUSPENSE_END
	),

	// Entity bound sound that follows the local player. Requires ONLY_CLOSEST
	FOLLOW_PLAYER_ON_MASTER(
			WILDLIFE_TRIVIA_SUSPENSE_END
	),

	// Looping sounds
	LOOP(
			NICELIFE_SANTABOT_INTRO,
			NICELIFE_SANTABOT_SUSPENSE,
			NICELIFE_SANTABOT_VOTE
	),

	// Looping entity bound sounds
	LOOP_ENTITY_BOUND(
			WILDLIFE_TRIVIA_SUSPENSE
	),

	// Automatically plays another sound once the first one ends on its own.
	FOLLOWED_BY(
			new FollowUpSound(NICELIFE_SANTABOT_INTRODUCTION_LONG, NICELIFE_SANTABOT_INTRO, 0.65f, 1),
			new FollowUpSound(NICELIFE_SANTABOT_INTRODUCTION_SHORT, NICELIFE_SANTABOT_INTRO, 0.65f, 1)
	);

	public record FollowUpSound(ModifiableSound sound, ModifiableSound followUp, float volume, float pitch) {}

	private final List<ModifiableSound> sounds;
	private final List<FollowUpSound> followUps;

	SoundBehavior(ModifiableSound... sounds) {
		this.sounds = List.of(sounds);
		this.followUps = List.of();
	}

	SoundBehavior(FollowUpSound... followUps) {
		this.followUps = List.of(followUps);
		this.sounds = this.followUps.stream().map(FollowUpSound::sound).toList();
	}

	public List<ModifiableSound> getSounds() {
		return sounds;
	}

	public List<FollowUpSound> getFollowUps() {
		return followUps;
	}

	public static SoundBehavior fromName(String name) {
		for (SoundBehavior behavior : values()) {
			if (behavior.name().equalsIgnoreCase(name)) {
				return behavior;
			}
		}
		return null;
	}
}
