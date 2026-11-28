package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.*;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import static net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability.AbilityType.*;

public enum Abilities {
	EMPTY(EmptyAbility::new, AbilityIcon.item(Items.STONE), "Empty", ChatFormatting.RESET, PASSIVE, "", "")
	,AIR_JUMP(AirJumpAbility::new, AbilityIcon.effect("jump_boost"), "Air Jump", ChatFormatting.AQUA, ACTIVE, "Double jump in mid-air.", "• No fall damage for falls under 5 blocks after the jump.\n• Up to 3 charges that recharge over time.")
	,BLAST_RESISTANCE(BlastResistanceAbility::new, AbilityIcon.effect("absorption"), "Blast Resistance", ChatFormatting.GRAY, PASSIVE, "Take 75% less explosion damage.", "")
	,DASH(DashAbility::new, AbilityIcon.effect("speed"), "Dash", ChatFormatting.AQUA, ACTIVE, "Double-tap a movement key to dash 4 blocks in that direction.", "• Up to 3 charges that recharge over time.")
	//? if > 1.20.3 {
	,EXTRA_REACH(ExtraReachAbility::new, AbilityIcon.item(Items.RABBIT_FOOT), "Extra Reach", ChatFormatting.DARK_RED, PASSIVE, "+0.5 blocks of attack reach.", "")
	//?}
	,FEATHER_FALLING(FeatherFallingAbility::new, AbilityIcon.custom("slow_falling"), "Feather Falling", ChatFormatting.WHITE, PASSIVE, "Permanent Feather Falling IV.", "• This is in addition to your armour enchantments.")
	,FIRE_RESISTANCE(FireResistanceAbility::new, AbilityIcon.effect("fire_resistance"), "Fire Resistance", ChatFormatting.GOLD, PASSIVE, "Permanent Fire Resistance I.", "• This is in addition to your armour enchantments.")
	,HASTE(HasteAbility::new, AbilityIcon.effect("haste"), "Haste", ChatFormatting.GOLD, PASSIVE, "Permanent Haste II.", "")
	,HEALTH_VISION(HealthVisionAbility::new, AbilityIcon.custom("health_vision"), "Health Vision", ChatFormatting.RED, ACTIVE, "See other players' health underneath their name tags.", "")
	,KNOCKBACK_RESISTANCE(KnockbackResistanceAbility::new, AbilityIcon.item(Items.IRON_CHESTPLATE), "Knockback Resistance", ChatFormatting.GRAY, PASSIVE, "Take 75% less knockback.", "")
	,MORE_DROPS(MoreDropsAbility::new, AbilityIcon.effect("luck"), "More Drops", ChatFormatting.GREEN, PASSIVE, "Permanent Luck I, for more mob and block drops.", "")
	,PROJECTILE_PROTECTION(ProjectileProtectionAbility::new, AbilityIcon.item(Items.BOW), "Projectile Protection", ChatFormatting.DARK_RED, PASSIVE, "Take 50% less projectile damage.", "• This is in addition to your armour enchantments.")
	,RESISTANCE(ResistanceAbility::new, AbilityIcon.effect("resistance"), "Resistance", ChatFormatting.GRAY, PASSIVE, "Permanent Resistance I.", "• This is in addition to your armour enchantments.")
	,SATURATION(SaturationAbility::new, AbilityIcon.effect("saturation"), "Saturation", ChatFormatting.GOLD, PASSIVE, "Regenerate health twice as fast when your hunger bar is nearly full.", "") //TODO extended description
	,SECOND_WIND(SecondWindAbility::new, AbilityIcon.custom("second_wind"), "Second Wind", ChatFormatting.YELLOW, ACTIVE, "Unknown", "") //TODO description + color
	,SHIELD_BASH(ShieldBashAbility::new, AbilityIcon.custom("shield_bash"), "Shield Bash", ChatFormatting.DARK_RED, ACTIVE, "Launch back nearby players with a shield attack. ", "• Attack while blocking with a shield to activate.")
	//? if > 1.20.3 {
	,SMALLER(SmallerAbility::new, AbilityIcon.item(Items.CAKE), "Smaller", ChatFormatting.DARK_RED, PASSIVE, "Your model and hitbox are slightly smaller and harder to hit. ", "")
	//?}
	,SNAIL_PROTECT(EmptyAbility::new, AbilityIcon.custom("snail_protect"), "Snail Protect", ChatFormatting.DARK_RED, ACTIVE, "Unknown", "") //TODO description + color
	,SOULMATE(EmptyAbility::new, AbilityIcon.custom("soulmate"), "???", ChatFormatting.DARK_RED, RED, "???", "§ePress [{}] to activate")
	,SPEED(SpeedAbility::new, AbilityIcon.effect("speed"), "Speed", ChatFormatting.AQUA, PASSIVE, "Permanent Speed I", "")
	,THROW_SWORDS(ThrowSwordsAbility::new, AbilityIcon.item(Items.DIAMOND_SWORD), "Throw Swords", ChatFormatting.AQUA, ACTIVE, "Unknown", "") //TODO description + color

	//,TRIDENT(EmptyAbility::new, AbilityIcon.item(Items.TRIDENT), "Trident", ChatFormatting.AQUA, ACTIVE, "Unknown", "")

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
		if (instance instanceof EmptyAbility) {
			instance = new EmptyAbility(this); // Load the empty ability with the current ability
		}
		instance.reloadHeartsChange();
		return instance;
	}

	public boolean isEnabled() {
		FinalLifeConfig config = FinalLifeConfig.get();
		ConfigurableValue<Boolean> enabled = switch (this) {
			case AIR_JUMP -> config.ABILITY_AIR_JUMP_ENABLED;
			case BLAST_RESISTANCE -> config.ABILITY_BLAST_RESISTANCE_ENABLED;
			case DASH -> config.ABILITY_DASH_ENABLED;
			//? if > 1.20.3 {
			case EXTRA_REACH -> config.ABILITY_EXTRA_REACH_ENABLED;
			//?}
			case FEATHER_FALLING -> config.ABILITY_FEATHER_FALLING_ENABLED;
			case FIRE_RESISTANCE -> config.ABILITY_FIRE_RESISTANCE_ENABLED;
			case HASTE -> config.ABILITY_HASTE_ENABLED;
			case HEALTH_VISION -> config.ABILITY_HEALTH_VISION_ENABLED;
			case KNOCKBACK_RESISTANCE -> config.ABILITY_KNOCKBACK_RESISTANCE_ENABLED;
			case MORE_DROPS -> config.ABILITY_MORE_DROPS_ENABLED;
			case PROJECTILE_PROTECTION -> config.ABILITY_PROJECTILE_PROTECTION_ENABLED;
			case RESISTANCE -> config.ABILITY_RESISTANCE_ENABLED;
			case SATURATION -> config.ABILITY_SATURATION_ENABLED;
			case SECOND_WIND -> config.ABILITY_SECOND_WIND_ENABLED;
			case SHIELD_BASH -> config.ABILITY_SHIELD_BASH_ENABLED;
			//? if > 1.20.3 {
			case SMALLER -> config.ABILITY_SMALLER_ENABLED;
			//?}
			case SNAIL_PROTECT -> config.ABILITY_SNAIL_PROTECT_ENABLED;
			case SOULMATE -> config.ABILITY_SOULMATE_ENABLED;
			case SPEED -> config.ABILITY_SPEED_ENABLED;
			case THROW_SWORDS -> config.ABILITY_THROW_SWORDS_ENABLED;
			default -> null;
		};
		return enabled != null && enabled.get();
	}

	public int getHearts() {
		FinalLifeConfig config = FinalLifeConfig.get();
		ConfigurableValue<Integer> hearts = switch (this) {
			case AIR_JUMP -> config.ABILITY_AIR_JUMP_HEARTS;
			case BLAST_RESISTANCE -> config.ABILITY_BLAST_RESISTANCE_HEARTS;
			case DASH -> config.ABILITY_DASH_HEARTS;
			//? if > 1.20.3 {
			case EXTRA_REACH -> config.ABILITY_EXTRA_REACH_HEARTS;
			//?}
			case FEATHER_FALLING -> config.ABILITY_FEATHER_FALLING_HEARTS;
			case FIRE_RESISTANCE -> config.ABILITY_FIRE_RESISTANCE_HEARTS;
			case HASTE -> config.ABILITY_HASTE_HEARTS;
			case HEALTH_VISION -> config.ABILITY_HEALTH_VISION_HEARTS;
			case KNOCKBACK_RESISTANCE -> config.ABILITY_KNOCKBACK_RESISTANCE_HEARTS;
			case MORE_DROPS -> config.ABILITY_MORE_DROPS_HEARTS;
			case PROJECTILE_PROTECTION -> config.ABILITY_PROJECTILE_PROTECTION_HEARTS;
			case RESISTANCE -> config.ABILITY_RESISTANCE_HEARTS;
			case SATURATION -> config.ABILITY_SATURATION_HEARTS;
			case SECOND_WIND -> config.ABILITY_SECOND_WIND_HEARTS;
			case SHIELD_BASH -> config.ABILITY_SHIELD_BASH_HEARTS;
			//? if > 1.20.3 {
			case SMALLER -> config.ABILITY_SMALLER_HEARTS;
			//?}
			case SNAIL_PROTECT -> config.ABILITY_SNAIL_PROTECT_HEARTS;
			case SOULMATE -> config.ABILITY_SOULMATE_HEARTS;
			case SPEED -> config.ABILITY_SPEED_HEARTS;
			case THROW_SWORDS -> config.ABILITY_THROW_SWORDS_HEARTS;
			default -> null;
		};
		return hearts == null ? 0 : hearts.get();
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
