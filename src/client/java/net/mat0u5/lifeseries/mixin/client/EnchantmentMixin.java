package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Enchantment.class, priority = 1)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    private int clampedEnchants(int original) {
        Minecraft minecraft = Minecraft.getInstance();
        Enchantment enchantment = (Enchantment) (Object) this;
        if (minecraft.level == null || enchantment == null) return original;
        if (LifeSeriesClient.lvl1ClampedEnchants == null || LifeSeriesClient.lvl1ClampedEnchants.isEmpty()) return original;
        try {
            Registry<Enchantment> enchantmentRegistry = Minecraft.getInstance().level.registryAccess()
                    //? if <=1.21 {
                    /*.registryOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));
                     *///?} else
                    .lookupOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));
            ResourceKey<Enchantment> enchant = enchantmentRegistry.getResourceKey(enchantment).orElseThrow();
            //? if <= 1.21.9 {
            /*String name = enchant.location().toString();
            *///?} else {
            String name = enchant.identifier().toString();
            //?}
            if (LifeSeriesClient.lvl1ClampedEnchants.contains(name)) {
                return 1;
            }
        }catch(Exception ignored) {}

        return original;
    }
}
