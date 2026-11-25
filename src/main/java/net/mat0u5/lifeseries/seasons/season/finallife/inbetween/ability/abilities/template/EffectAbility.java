package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
//? if > 1.20.3
import net.minecraft.core.Holder;

public abstract class EffectAbility extends Ability {
	//? if <= 1.20.3 {
	//private final MobEffect effect;
	//?} else {
	private final Holder<MobEffect> effect;
	//?}

	private final int amplifier;

	//? if <= 1.20.3 {
	//public EffectAbility(Abilities ability, MobEffect effect, int amplifier) {
	//?} else {
	public EffectAbility(Abilities ability, Holder<MobEffect> effect, int amplifier) {
	//?}
		super(ability);
		this.effect = effect;
		this.amplifier = amplifier;
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null || player.isSpectator()) return;
		MobEffectInstance effect = player.getEffect(this.effect);
		if (effect != null && (effect.getAmplifier() > amplifier || !effect.endsWithin(20))) return;
		player.addEffect(new MobEffectInstance(this.effect, 100, this.amplifier, false, false, false));
	}

	@Override
	public void onEnd() {
		ServerPlayer player = getPlayer();
		if (player == null) return;
		MobEffectInstance effect = player.getEffect(this.effect);
		if (effect != null && effect.getAmplifier() == amplifier && effect.endsWithin(100) && !effect.showIcon()) {
			player.removeEffect(this.effect);
		}
	}
}
