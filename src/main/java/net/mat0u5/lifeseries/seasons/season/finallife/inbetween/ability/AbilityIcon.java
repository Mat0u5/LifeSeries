package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

// Items are shown via an item display, textures are drawn client-side
public class AbilityIcon {
	private final ItemStack itemStack;
	@Nullable
	private final Identifier texture;

	private AbilityIcon(ItemStack itemStack, @Nullable Identifier texture) {
		this.itemStack = itemStack;
		this.texture = texture;
	}

	public static AbilityIcon item(ItemLike item) {
		return new AbilityIcon(new ItemStack(item), null);
	}

	public static AbilityIcon item(ItemStack itemStack) {
		return new AbilityIcon(itemStack, null);
	}

	// Vanilla effect - "speed"
	public static AbilityIcon effect(String effectName) {
		return new AbilityIcon(ItemStack.EMPTY, IdentifierHelper.vanilla("textures/mob_effect/" + effectName + ".png"));
	}

	// assets/lifeseries/textures/ability/<name>.png
	public static AbilityIcon custom(String name) {
		return new AbilityIcon(ItemStack.EMPTY, IdentifierHelper.lifeseries("textures/ability/" + name + ".png"));
	}

	public boolean isTexture() {
		return this.texture != null;
	}

	@Nullable
	public Identifier texture() {
		return this.texture;
	}

	public ItemStack itemStack() {
		return this.itemStack.copy();
	}
}
