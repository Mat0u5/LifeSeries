package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.config.entry.ConfigRef;
//? if > 1.20.3 {
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
//?}

public class ExtraReachAbility extends Ability {
	//? if > 1.20.3 {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_EXTRA_REACH_HEARTS);
	private static final AttributeUtils.AttributeHolder ENTITY_INTERACTION_RANGE = new AttributeUtils.AttributeHolder(Attributes.ENTITY_INTERACTION_RANGE);
	//?}
	public ExtraReachAbility() {
		//? if <= 1.20.3 {
		//super(Abilities.EMPTY);
		//?} else {
		super(Abilities.EXTRA_REACH);
		//?}
	}

	@Override
	protected int loadHeartsChange() {
		//? if <= 1.20.3 {
		//return 0;
		//?} else {
		return HEALTH.get();
		//?}
	}
	//? if > 1.20.3 {
	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		AttributeUtils.AttributeEntityInstance range = ENTITY_INTERACTION_RANGE.of(player);
		range.set(range.getDefaultValue() + 0.5);
	}

	@Override
	public void onEnd() {
		super.onEnd();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		ENTITY_INTERACTION_RANGE.of(player).reset();
	}
	//?}
}
