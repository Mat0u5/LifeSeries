package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.matlib.utils.other.TextUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class InBetweenChoice {
	@Nullable
	private final Ability ability; // null for an empty ring

	private InBetweenChoice(@Nullable Ability ability) {
		this.ability = ability;
	}

	public static InBetweenChoice of(Abilities ability) {
		return new InBetweenChoice(ability.createAbility());
	}

	public static InBetweenChoice empty() {
		return new InBetweenChoice(null);
	}

	public boolean isEmpty() {
		return this.ability == null;
	}

	@Nullable
	public Ability ability() {
		return this.ability;
	}

	@Nullable
	public AbilityIcon icon() {
		return this.ability == null ? null : this.ability.ability().icon();
	}

	public Component subtitleFallback() {
		double hearts = getHearts();
		if (hearts == 0) {
			return Component.literal("No Extra Hearts");
		}
		String amount = hearts % 1 == 0 ? String.valueOf((int) hearts) : String.valueOf(hearts);
		return Component.literal((hearts > 0 ? "+" : "") + amount + TextUtils.pluralize(" Heart", hearts));
	}

	public Component title() {
		return this.ability == null ? Component.empty() : Component.literal(this.ability.ability().title());
	}

	public double getHearts() {
		return this.ability == null ? 0 : this.ability.getHealthChange() / 2.0;
	}
}
