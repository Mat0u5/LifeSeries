package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;

public class SmallerAbility extends Ability {
	public SmallerAbility() {
		//? if <= 1.20.3 {
		//super(Abilities.EMPTY);
		//?} else {
		super(Abilities.SMALLER);
		//?}
	}
	//? if > 1.20.3 {
	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		AttributeUtils.SCALE.of(player).set(0.9);
	}

	@Override
	public void onEnd() {
		super.onEnd();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		AttributeUtils.SCALE.of(player).reset();
	}
	//?}
}
