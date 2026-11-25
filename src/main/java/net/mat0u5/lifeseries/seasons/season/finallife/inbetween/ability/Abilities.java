package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.EmptyAbility;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.SpeedAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import static net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability.AbilityType.*;

public enum Abilities {
	EMPTY(EmptyAbility::new, AbilityIcon.item(Items.STONE), "Empty", ChatFormatting.RESET, PASSIVE, "", "")
	,AIR_JUMP(EmptyAbility::new, AbilityIcon.effect("jump_boost"), "Air Jump", ChatFormatting.AQUA, ACTIVE, "Double jump in mid-air.", "• No fall damage for falls under 5 blocks after the jump.\n• Up to 3 charges that recharge over time.")
	,BLAST_RESISTANCE(EmptyAbility::new, AbilityIcon.effect("absorption"), "Blast Resistance", ChatFormatting.GRAY, PASSIVE, "Take 75% less explosion damage.", "")
	,DASH(EmptyAbility::new, AbilityIcon.effect("speed"), "Dash", ChatFormatting.AQUA, ACTIVE, "Double-tap a movement key to dash 4 blocks in that direction.", "")
	,EXTRA_REACH(EmptyAbility::new, AbilityIcon.item(Items.RABBIT_FOOT), "Extra Reach", ChatFormatting.DARK_RED, PASSIVE, "+0.5 blocks of attack reach.", "")
	,FEATHER_FALLING(EmptyAbility::new, AbilityIcon.custom("TODO"), "Feather Falling", ChatFormatting.WHITE, PASSIVE, "Permanent Feather Falling IV.", "• This is in addition to your armour enchantments.")
	,FIRE_RESISTANCE(EmptyAbility::new, AbilityIcon.effect("fire_resistance"), "Fire Resistance", ChatFormatting.GOLD, PASSIVE, "Permanent Fire Resistance I.", "• This is in addition to your armour enchantments.")
	,HASTE(EmptyAbility::new, AbilityIcon.effect("haste"), "Haste", ChatFormatting.GOLD, PASSIVE, "Permanent Haste II.", "")
	,HEALTH_VISION(EmptyAbility::new, AbilityIcon.custom("TODO"), "Health Vision", ChatFormatting.RED, ACTIVE, "See other players' health underneath their name tags.", "")
	,KNOCKBACK_RESISTANCE(EmptyAbility::new, AbilityIcon.item(Items.IRON_CHESTPLATE), "Knockback Resistance", ChatFormatting.GRAY, PASSIVE, "Take 75% less knockback.", "")
	,MORE_DROPS(EmptyAbility::new, AbilityIcon.effect("luck"), "More Drops", ChatFormatting.GREEN, PASSIVE, "Permanent Luck I, for more mob and block drops.", "")
	,PROJECTILE_PROTECTION(EmptyAbility::new, AbilityIcon.item(Items.BOW), "Projectile Protection", ChatFormatting.DARK_RED, PASSIVE, "Take 50% less projectile damage.", "• This is in addition to your armour enchantments.")
	,RESISTANCE(EmptyAbility::new, AbilityIcon.effect("resistance"), "Resistance", ChatFormatting.GRAY, PASSIVE, "Permanent Resistance I.", "• This is in addition to your armour enchantments.")
	,SATURATION(EmptyAbility::new, AbilityIcon.effect("saturation"), "Saturation", ChatFormatting.GOLD, PASSIVE, "Regenerate health twice as fast when your hunger bar is nearly full.", "")
	,SECOND_WIND(EmptyAbility::new, AbilityIcon.custom("TODO"), "Second Wind", ChatFormatting.YELLOW, ACTIVE, "TODO", "")
	,SHIELD_BASH(EmptyAbility::new, AbilityIcon.custom("TODO"), "Shield Bash", ChatFormatting.DARK_RED, ACTIVE, "Launch back nearby players with a shield attack. ", "• Attack while blocking with a shield to activate.")
	,SMALLER(EmptyAbility::new, AbilityIcon.item(Items.CAKE), "Smaller", ChatFormatting.DARK_RED, PASSIVE, "Your model and hitbox are slightly smaller and harder to hit. ", "")
	,SPEED(SpeedAbility::new, AbilityIcon.effect("speed"), "Speed", ChatFormatting.AQUA, PASSIVE, "Permanent Speed I.", "")

	//,TRIDENT(EmptyAbility::new, AbilityIcon.item(Items.TRIDENT), "Trident", ChatFormatting.AQUA, ACTIVE, "", "")

	;

	private final AbilityIcon icon;
	private final String title;
	private final String description;
	private final String additionalDescription;
	private final Supplier<Ability> supplier;
	private final ChatFormatting choiceColor;
	private final Ability.AbilityType abilityType;

	Abilities(Supplier<Ability> supplier, AbilityIcon icon, String title, ChatFormatting choiceColor, Ability.AbilityType abilityType, String description, String additionalDescription) {
		this.supplier = supplier;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.additionalDescription = additionalDescription;
		this.choiceColor = choiceColor;
		this.abilityType = abilityType;
	}

	public Ability createAbility() {
		Ability instance = supplier.get();
		instance.reloadHealthChange();
		return instance;
	}

	public AbilityIcon icon() {
		return this.icon;
	}

	public String title() {
		return this.title;
	}

	public ChatFormatting choiceColor() {
		return this.choiceColor;
	}

	public Ability.AbilityType abilityType() {
		return this.abilityType;
	}

	public String description() {
		return this.description;
	}

	public String additionalDescription() {
		return this.additionalDescription;
	}
}
