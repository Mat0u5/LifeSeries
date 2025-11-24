package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static net.mat0u5.lifeseries.Main.blacklist;

//? if < 1.20.5 {
/*import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.Map;
*///?} else {
import net.minecraft.core.component.DataComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import java.util.Optional;
//?}

@Mixin(value = AnvilMenu.class, priority = 1)
public abstract class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("TAIL"))
    private void modifyAnvilResultName(CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (blacklist == null) return;
        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) (Object) this;
        Container outputInventory = accessor.getOutput();

        ItemStack resultStack = outputInventory.getItem(0);
        if (ItemStackUtils.hasCustomComponentEntry(resultStack, "NoAnvil") ||
            ItemStackUtils.hasCustomComponentEntry(resultStack, "NoModifications")) {
            outputInventory.setItem(0, ItemStack.EMPTY);
        }

        if (!resultStack.isEnchanted()) return;

        //? if < 1.20.5 {
        /*EnchantmentHelper.setEnchantments(blacklist.clampAndBlacklistEnchantments(EnchantmentHelper.getEnchantments(resultStack)), resultStack);
        if (ItemStackUtils.hasCustomComponentEntry(resultStack, "NoMending")) {
            for (Map.Entry<Enchantment, Integer> enchant : EnchantmentHelper.getEnchantments(resultStack).entrySet()) {
                Enchantment actualEnchant = enchant.getKey();
                if (actualEnchant == Enchantments.MENDING) {
                    outputInventory.setItem(0, ItemStack.EMPTY);
                }
            }
        }
        *///?} else {
        resultStack.set(DataComponents.ENCHANTMENTS, blacklist.clampAndBlacklistEnchantments(resultStack.getEnchantments()));
        if (ItemStackUtils.hasCustomComponentEntry(resultStack, "NoMending")) {
            for (Entry<Holder<Enchantment>> enchant : resultStack.getEnchantments().entrySet()) {
                Optional<ResourceKey<Enchantment>> enchantRegistry = enchant.getKey().unwrapKey();
                if (enchantRegistry.isEmpty()) continue;
                if (enchantRegistry.get() == Enchantments.MENDING) {
                    outputInventory.setItem(0, ItemStack.EMPTY);
                }
            }
        }
        //?}

    }
}
