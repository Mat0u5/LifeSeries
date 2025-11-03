package net.mat0u5.lifeseries.mixin;

import com.google.common.collect.Lists;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.seasonConfig;
//? if >= 1.21.2
/*import net.mat0u5.lifeseries.utils.player.PlayerUtils;*/

@Mixin(value = EnchantmentHelper.class, priority = 1)
public class EnchantmentHelperMixin {
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void getPossibleEntries(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (Main.server == null) return;

        if (ItemStackUtils.hasCustomComponentEntry(stack, "NoEnchants") || ItemStackUtils.hasCustomComponentEntry(stack, "NoModifications")) {
            cir.setReturnValue(Lists.<EnchantmentInstance>newArrayList());
            return;
        }

        if (seasonConfig.CUSTOM_ENCHANTER_ALGORITHM.get(seasonConfig)) {
            ls$customEnchantmentTableAlgorithm(level, stack, possibleEnchantments, cir);
        }
        else {
            ls$blacklistEnchantments(level, stack, possibleEnchantments, cir);
        }
    }

    @Unique
    private static void ls$blacklistEnchantments(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.<EnchantmentInstance>newArrayList();
        boolean bl = stack.is(Items.BOOK);
        possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = (Enchantment)enchantmentx.value();
            Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentx.unwrapKey();
            boolean isRegistryPresent = enchantRegistryKey.isPresent();
            if (isRegistryPresent && !blacklist.getBannedEnchants().contains(enchantRegistryKey.get())) {
                for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                    if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                        if (isRegistryPresent && blacklist.getClampedEnchants().contains(enchantRegistryKey.get())) {
                            list.add(new EnchantmentInstance(enchantmentx, 1));
                        }
                        else {
                            list.add(new EnchantmentInstance(enchantmentx, j));
                        }
                        break;
                    }
                }
            }
        });
        cir.setReturnValue(list);
    }

    @Unique
    private static void ls$customEnchantmentTableAlgorithm(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = new ArrayList<>();
        boolean bl = stack.is(Items.BOOK);
        possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = (Enchantment)enchantmentx.value();
            Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentx.unwrapKey();
            if (enchantRegistryKey.isPresent() && !blacklist.getBannedEnchants().contains(enchantRegistryKey.get())) {
                if (blacklist.getClampedEnchants().contains(enchantRegistryKey.get())) {
                    list.add(new EnchantmentInstance(enchantmentx, 1));
                }
                else {
                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                        if (j == 1) {
                            if (enchantment.getMaxLevel() <= 3 || level < 4) {
                                list.add(new EnchantmentInstance(enchantmentx, j));
                            }
                        }
                        else if (j == 2 && level > 4 && enchantment.getMaxLevel() > 3) {
                            list.add(new EnchantmentInstance(enchantmentx, j));
                        }
                        else if (j == 2 && level > 6 && enchantment.getMaxLevel() >= 3) {
                            list.add(new EnchantmentInstance(enchantmentx, j));
                        }
                        else if (j == 3 && level > 6 && enchantment.getMaxLevel() > 3) {
                            list.add(new EnchantmentInstance(enchantmentx, j));
                        }
                    }
                }
            }
        });
        cir.setReturnValue(list);
    }

    @Inject(
            method = "doPostAttackEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD")
    )
    private static void onTargetDamaged(ServerLevel world, Entity victimEntity, DamageSource damageSource, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (!(victimEntity instanceof ServerPlayer victim)) return;
        if (damageSource == null) return;
        if (damageSource.getEntity() == null) return;
        if (!SuperpowersWildcard.hasActivatedPower(victim, Superpowers.SUPER_PUNCH)) return;
        //? if <= 1.21 {
        damageSource.getEntity().hurt(victim.damageSources().thorns(victim), 1F);
        //?} else {
        /*damageSource.getAttacker().damage(PlayerUtils.getServerWorld(victim), victim.getDamageSources().thorns(victim), 1F);
         *///?}
    }
}
