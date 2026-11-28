package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnockbackResistanceAbility extends Ability {
	private static final AttributeUtils.AttributeHolder KNOCKBACK_RESISTANCE = new AttributeUtils.AttributeHolder(Attributes.KNOCKBACK_RESISTANCE);
	public KnockbackResistanceAbility() {
		super(Abilities.KNOCKBACK_RESISTANCE);
	}

	@Override
	public void tick() {
		super.tick();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		KNOCKBACK_RESISTANCE.of(player).set(0.75);
	}

	@Override
	public void onEnd() {
		super.onEnd();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		KNOCKBACK_RESISTANCE.of(player).reset();
	}
}
