package net.mat0u5.lifeseries.config.modifiable;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Locale;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

public enum ModifiableSound {
	ADVANCEDDEATH_ANVIL(SoundEvents.ANVIL_PLACE)
	,ADVANCEDDEATH_WITHER(SoundEvents.WITHER_SHOOT)
	,DEATH_FINAL(SoundEvents.LIGHTNING_BOLT_THUNDER)
	,LIVES_ROLL(SoundEvents.UI_BUTTON_CLICK)
	,LIVES_ROLL_ASSIGN(SoundEvents.END_PORTAL_SPAWN)
	,LIVES_RECEIVE(SoundEvents.AMETHYST_BLOCK_CHIME)

	,BOOGEYMAN_CURE(IdentifierHelper.mod("lastlife_boogeyman_cure"))
	,BOOGEYMAN_FAIL(IdentifierHelper.mod("lastlife_boogeyman_fail"))
	,BOOGEYMAN_NOTICE_5MIN(SoundEvents.LIGHTNING_BOLT_THUNDER)
	,BOOGEYMAN_NOTICE_1MIN(SoundEvents.LIGHTNING_BOLT_THUNDER)
	,BOOGEYMAN_NOTICE_CHOOSE(SoundEvents.LIGHTNING_BOLT_THUNDER)
	,BOOGEYMAN_NOTICE_PREPARE(SoundEvents.LIGHTNING_BOLT_THUNDER)
	,BOOGEYMAN_ROLL_3(SoundEvents.UI_BUTTON_CLICK)
	,BOOGEYMAN_ROLL_2(SoundEvents.UI_BUTTON_CLICK)
	,BOOGEYMAN_ROLL_1(SoundEvents.UI_BUTTON_CLICK)
	,BOOGEYMAN_ROLL(IdentifierHelper.mod("lastlife_boogeyman_wait"))
	,BOOGEYMAN_YES(IdentifierHelper.mod("lastlife_boogeyman_yes"))
	,BOOGEYMAN_NO(IdentifierHelper.mod("lastlife_boogeyman_no"))


	,DOUBLELIFE_SOULMATE_WAIT(Seasons.DOUBLE_LIFE, IdentifierHelper.mod("doublelife_soulmate_wait"))
	,DOUBLELIFE_SOULMATE_CHOSEN(Seasons.DOUBLE_LIFE, IdentifierHelper.mod("doublelife_soulmate_chosen"))
	,DOUBLELIFE_SOULMATE_ROLL_3(Seasons.DOUBLE_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,DOUBLELIFE_SOULMATE_ROLL_2(Seasons.DOUBLE_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,DOUBLELIFE_SOULMATE_ROLL_1(Seasons.DOUBLE_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,DOUBLELIFE_DISTRIBUTE(Seasons.DOUBLE_LIFE, SoundEvents.ENDERMAN_TELEPORT)

	,SECRETLIFE_TASK_SUCCEED(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_task_succeed"))
	,SECRETLIFE_TASK_FAIL(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_task_fail"))
	,SECRETLIFE_TASK_REROLL(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_task_reroll"))
	,SECRETLIFE_TASK_REROLL_TEXT(Seasons.SECRET_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,SECRETLIFE_TASK_TOTEM(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_task_totem"))
	,SECRETLIFE_GIFT_HEART(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_life"))
	,SECRETLIFE_TASK_ROLL_3(Seasons.SECRET_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,SECRETLIFE_TASK_ROLL_2(Seasons.SECRET_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,SECRETLIFE_TASK_ROLL_1(Seasons.SECRET_LIFE, SoundEvents.UI_BUTTON_CLICK)
	,SECRETLIFE_TASK(Seasons.SECRET_LIFE, IdentifierHelper.mod("secretlife_task"))
	,SECRETLIFE_ITEM_SPAWN(Seasons.SECRET_LIFE, SoundEvents.ITEM_PICKUP)
	//? if < 1.21 {
	/*,SECRETLIFE_ITEM_SPAWN_START(Seasons.SECRET_LIFE, SoundEvents.ITEM_FRAME_REMOVE_ITEM)
	*///?} else {
	,SECRETLIFE_ITEM_SPAWN_START(Seasons.SECRET_LIFE, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM)
	//?}

	,WILDLIFE_SUPERPOWERS(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_superpowers"))
	,WILDLIFE_TRIVIA_INTRO(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_intro"))
	,WILDLIFE_TRIVIA_SUSPENSE(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_suspense"))
	,WILDLIFE_TRIVIA_SUSPENSE_END(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_suspense_end"))
	,WILDLIFE_TRIVIA_ANALYZING(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_analyzing"))
	,WILDLIFE_TRIVIA_CORRECT(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_correct"))
	,WILDLIFE_TRIVIA_INCORRECT(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_trivia_incorrect"))
	,WILDLIFE_WILDCARD_DOTS_1(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_DIDGERIDOO)
	,WILDLIFE_WILDCARD_DOTS_2(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_DIDGERIDOO)
	,WILDLIFE_WILDCARD_DOTS_3(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_DIDGERIDOO)
	,WILDLIFE_WILDCARD_TITLE(Seasons.WILD_LIFE, SoundEvents.ZOMBIE_VILLAGER_CURE)
	,WILDLIFE_WILDCARD_MAKEITWILD_1(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_DIDGERIDOO)
	,WILDLIFE_WILDCARD_MAKEITWILD_2(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_DIDGERIDOO)
	,WILDLIFE_WILDCARD_MAKEITWILD_3(Seasons.WILD_LIFE, SoundEvents.ZOMBIE_VILLAGER_CURE)
	,WILDLIFE_WILDCARD_FADE(Seasons.WILD_LIFE, SoundEvents.BEACON_DEACTIVATE)
	,WILDLIFE_HUNGER_NEW_RULES_START(Seasons.WILD_LIFE, SoundEvents.NOTE_BLOCK_PLING)
	,WILDLIFE_HUNGER_NEW_RULES(Seasons.WILD_LIFE, SoundEvents.ELDER_GUARDIAN_CURSE)
	,WILDLIFE_MOBSWAP_SWAP_1(Seasons.WILD_LIFE, SoundEvents.ELDER_GUARDIAN_CURSE)
	,WILDLIFE_MOBSWAP_SWAP_2(Seasons.WILD_LIFE, SoundEvents.ZOMBIE_VILLAGER_CURE)
	,WILDLIFE_MOBSWAP_SPAWN(Seasons.WILD_LIFE, SoundEvents.CHICKEN_EGG)
	,WILDLIFE_TIME_SLOW_DOWN(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_time_slow_down"))
	,WILDLIFE_TIME_SPEED_UP(Seasons.WILD_LIFE, IdentifierHelper.mod("wildlife_time_speed_up"))
	,WILDLIFE_TRIVIA_CURSE(Seasons.WILD_LIFE, SoundEvents.ELDER_GUARDIAN_CURSE)
	//? if <= 1.21 {
	/*,WILDLIFE_SUPERPOWERS_ASTRAL_START(Seasons.WILD_LIFE, SoundEvents.EVOKER_PREPARE_ATTACK)
	*///?} else {
	,WILDLIFE_SUPERPOWERS_ASTRAL_START(Seasons.WILD_LIFE, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE)
	//?}
	,WILDLIFE_SUPERPOWERS_ASTRAL_END(Seasons.WILD_LIFE, SoundEvents.EVOKER_DEATH)
	,WILDLIFE_SUPERPOWERS_FLIGHT(Seasons.WILD_LIFE, SoundEvents.FIREWORK_ROCKET_LAUNCH)
	,WILDLIFE_SUPERPOWERS_LISTENING_START(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_UP)
	,WILDLIFE_SUPERPOWERS_LISTENING_END(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_OUT)
	,WILDLIFE_SUPERPOWERS_MIMICRY(Seasons.WILD_LIFE, SoundEvents.CHICKEN_EGG)
	,WILDLIFE_SUPERPOWERS_PLAYERDISGUISE_COPY(Seasons.WILD_LIFE, SoundEvents.RESPAWN_ANCHOR_CHARGE)
	,WILDLIFE_SUPERPOWERS_PLAYERDISGUISE_START(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_UP)
	,WILDLIFE_SUPERPOWERS_PLAYERDISGUISE_END(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_OUT)
	,WILDLIFE_SUPERPOWERS_ANIMALDISGUISE_START(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_UP)
	,WILDLIFE_SUPERPOWERS_ANIMALDISGUISE_END(Seasons.WILD_LIFE, SoundEvents.PUFFER_FISH_BLOW_OUT)
	,WILDLIFE_SUPERPOWERS_SUPERSPEED_FOOD(Seasons.WILD_LIFE, SoundEvents.GENERIC_EAT)
	,WILDLIFE_SUPERPOWERS_SUPERSPEED_START(Seasons.WILD_LIFE, SoundEvents.BEACON_ACTIVATE)
	,WILDLIFE_SUPERPOWERS_SUPERSPEED_END(Seasons.WILD_LIFE, SoundEvents.BEACON_DEACTIVATE)
	,WILDLIFE_SUPERPOWERS_INVISIBILITY_START(Seasons.WILD_LIFE, SoundEvents.SHULKER_SHOOT)
	,WILDLIFE_SUPERPOWERS_INVISIBILITY_END(Seasons.WILD_LIFE, SoundEvents.CHICKEN_EGG)
	,WILDLIFE_SUPERPOWERS_TRIPLEJUMP_START(Seasons.WILD_LIFE, SoundEvents.SLIME_JUMP)
	,WILDLIFE_SUPERPOWERS_TRIPLEJUMP_END(Seasons.WILD_LIFE, SoundEvents.SLIME_SQUISH)
	,WILDLIFE_SUPERPOWERS_WINDCHARGE_START(Seasons.WILD_LIFE, SoundEvents.ARROW_SHOOT)
	//? if >= 1.21 {
	,WILDLIFE_SUPERPOWERS_WINDCHARGE_END(Seasons.WILD_LIFE, SoundEvents.WIND_CHARGE_BURST)
	//?}
	,WILDLIFE_SUPERPOWERS_NECROMANCY(Seasons.WILD_LIFE, SoundEvents.WARDEN_EMERGE)
	,WILDLIFE_SUPERPOWERS_SHADOWPLAY(Seasons.WILD_LIFE, SoundEvents.SHULKER_SHOOT)
	,WILDLIFE_TRIVIA_BOT_SPAWN(Seasons.WILD_LIFE, SoundEvents.LIGHTNING_BOLT_THUNDER)
	//? if <= 1.20.2 {
	/*,WILDLIFE_SNAIL_TELEPORT(Seasons.WILD_LIFE, SoundEvents.ENDERMAN_TELEPORT)
	,WILDLIFE_TRIVIA_TELEPORT(Seasons.WILD_LIFE, SoundEvents.ENDERMAN_TELEPORT)
	,WILDLIFE_SUPERPOWERS_TELEPORTATION(Seasons.WILD_LIFE, SoundEvents.ENDERMAN_TELEPORT)
	*///?} else {
	,WILDLIFE_SNAIL_TELEPORT(Seasons.WILD_LIFE, SoundEvents.PLAYER_TELEPORT)
	,WILDLIFE_TRIVIA_TELEPORT(Seasons.WILD_LIFE, SoundEvents.PLAYER_TELEPORT)
	,WILDLIFE_SUPERPOWERS_TELEPORTATION(Seasons.WILD_LIFE, SoundEvents.PLAYER_TELEPORT)
	//?}
	,WILDLIFE_TRIVIA_SNAIL_TRANSFORM(Seasons.WILD_LIFE, SoundEvents.GENERIC_EXPLODE)

	,PASTLIFE_SOCIETY(Seasons.PAST_LIFE, IdentifierHelper.mod("pastlife_society"))
	,PASTLIFE_SOCIETY_END_MEMBER(Seasons.PAST_LIFE, IdentifierHelper.mod("pastlife_society_end_member"))

	,NICELIFE_SANTABOT_INTRO(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_intro"))
	,NICELIFE_SANTABOT_SUSPENSE(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_suspense"))
	,NICELIFE_SANTABOT_SUSPENSE_END(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_suspense_end"))
	,NICELIFE_SANTABOT_TURN(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_turn"))
	,NICELIFE_SANTABOT_AWAY(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_away"))
	,NICELIFE_SANTABOT_ANALYZING(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_analyzing"))
	,NICELIFE_SANTABOT_VOTE(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_vote"))
	,NICELIFE_NICELIST_INFO(Seasons.NICE_LIFE, SoundEvents.NOTE_BLOCK_BELL)
	,NICELIFE_NICELIST_VOTE(Seasons.NICE_LIFE, SoundEvents.NOTE_BLOCK_BELL)
	,NICELIFE_NAUGHTYLIST_INFO(Seasons.NICE_LIFE, SoundEvents.NOTE_BLOCK_BELL)
	,NICELIFE_MIDNIGHT_CHIMES(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_midnight_chimes"))
	,NICELIFE_RED_WINTER(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_red_winter"))
	,NICELIFE_SANTABOT_INTRODUCTION_LONG(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_introduction_long"))
	,NICELIFE_SANTABOT_INTRODUCTION_SHORT(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_introduction_short"))
	,NICELIFE_SANTABOT_INCORRECT_ALL_WRONG(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_santabot_incorrect_all_wrong"))
	,NICELIFE_VOTE_RESULT(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_vote_result"))
	,NICELIFE_NAUGHTYLIST(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_naughtylist"))
	,NICELIFE_NAUGHTYLIST_CLEAR(Seasons.NICE_LIFE, SoundEvents.CHICKEN_EGG)
	,NICELIFE_NICELIST_START(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_start"))
	,NICELIFE_NICELIST_PERSON(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_person"))
	,NICELIFE_NICELIST_COUNTDOWN_3(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_countdown_3"))
	,NICELIFE_NICELIST_COUNTDOWN_2(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_countdown_2"))
	,NICELIFE_NICELIST_COUNTDOWN_1(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_countdown_1"))
	,NICELIFE_NICELIST_END(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_nicelist_end"))
	,NICELIFE_NICELIST_VOTE_FAIL(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_naughtylist"))
	,NICELIFE_NICELIST_WINNER(Seasons.NICE_LIFE, SoundEvents.FIREWORK_ROCKET_LAUNCH)
	,NICELIFE_SNOWMAN_HIT(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_snowman_hit"))
	,NICELIFE_SNOWMAN_GROWL(Seasons.NICE_LIFE, IdentifierHelper.mod("nicelife_snowman_growl"))
	;

	final String name;
	final Seasons requiredSeason;
	final String defaultSoundId;
	String cachedSoundId;
	SoundEvent cachedSound;

	ModifiableSound(Identifier sound) {
		this(null, SoundEvent.createVariableRangeEvent(sound));
	}

	ModifiableSound(Holder.Reference<SoundEvent> soundRef) {
		this(null, soundRef.value());
	}

	ModifiableSound(SoundEvent sound) {
		this(null, sound);
	}

	ModifiableSound(Seasons season, Identifier sound) {
		this(season, SoundEvent.createVariableRangeEvent(sound));
	}

	ModifiableSound(Seasons season, Holder.Reference<SoundEvent> soundRef) {
		this(season, soundRef.value());
	}

	ModifiableSound(Seasons season, SoundEvent sound) {
		this.requiredSeason = season;
		this.name = this.name().toLowerCase(Locale.ROOT).replace("_",".");
		//~ if >= 1.21.2 '.getLocation()' -> '.location()' {
		String soundId = sound.location().toString();
		//~}
		this.defaultSoundId = soundId;
		this.cachedSoundId = soundId;
		this.cachedSound = sound;
	}

	public SoundEvent get() {
		return ModifiableSoundManager.get(this);
	}

	public void broadcast() {
		PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), this.get());
	}

	public void broadcast(float volume, float pitch) {
		PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), this.get(), volume, pitch);
	}

	public void play(ServerPlayer player) {
		PlayerUtils.playSoundToPlayer(player, this.get());
	}

	public void play(ServerPlayer player, float volume, float pitch) {
		PlayerUtils.playSoundToPlayer(player, this.get(), volume, pitch);
	}

	public void play(Collection<ServerPlayer> players) {
		PlayerUtils.playSoundToPlayers(players, this.get());
	}

	public void play(Collection<ServerPlayer> players, float volume, float pitch) {
		PlayerUtils.playSoundToPlayers(players, this.get(), volume, pitch);
	}

	public void playWithSource(Entity source, SoundSource soundCategory, float volume, float pitch) {
		PlayerUtils.playSoundWithSourceToPlayers(source, this.get(), soundCategory, volume, pitch);
	}

	public void playWithSource(Collection<ServerPlayer> players, Entity source, SoundSource soundCategory, float volume, float pitch) {
		PlayerUtils.playSoundWithSourceToPlayers(players, source, this.get(), soundCategory, volume, pitch);
	}

	public static void registerAllSounds() {
		for (ModifiableSound modifiableSound : ModifiableSound.values()) {
			if (modifiableSound.requiredSeason != null && currentSeason != null && currentSeason.getSeason() != modifiableSound.requiredSeason) continue;
			ModifiableSoundManager.register(modifiableSound.name, modifiableSound.defaultSoundId, modifiableSound.cachedSoundId);
		}
	}

	public static ModifiableSound fromName(String name) {
		if (name == null) return null;
		if (name.startsWith("sound.")) name = name.substring(6);
		for (ModifiableSound modifiableSound : ModifiableSound.values()) {
			if (modifiableSound.name.equals(name)) {
				return modifiableSound;
			}
		}
		return null;
	}
}
