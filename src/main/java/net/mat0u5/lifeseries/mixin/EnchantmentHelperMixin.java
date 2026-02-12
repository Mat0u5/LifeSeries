package net.mat0u5.lifeseries.mixin;

import com.google.common.collect.Lists;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.List;
import java.util.Optional;

import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.seasonConfig;
//? if >= 1.21.2
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
//? if <= 1.20.5 {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
*///?} else {
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import java.util.ArrayList;
import java.util.stream.Stream;
//?}
//? if > 1.20 && <= 1.20.5 {
/*import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlagSet;
*///?}

@Mixin(value = EnchantmentHelper.class, priority = 1)
public class EnchantmentHelperMixin {
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    //? if <= 1.20.3 {
    /*private static void getPossibleEntries(int level, ItemStack stack, boolean bl, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
    *///?} else if <= 1.20.5 {
    /*private static void getPossibleEntries(FeatureFlagSet featureFlagSet, int level, ItemStack stack, boolean bl, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
    *///?} else {
    private static void getPossibleEntries(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
    //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (Main.server == null) return;

        if (ItemStackUtils.hasCustomComponentEntry(stack, "NoEnchants") || ItemStackUtils.hasCustomComponentEntry(stack, "NoModifications")) {
            cir.setReturnValue(Lists.<EnchantmentInstance>newArrayList());
            return;
        }
        //? if <= 1.20.5 {

        /*if (seasonConfig.CUSTOM_ENCHANTER_ALGORITHM.get()) {
            ls$customEnchantmentTableAlgorithm(level, stack, bl, cir);
        }
        else {
            ls$blacklistEnchantments(level, stack, bl, cir);
        }
        *///?} else {
        if (seasonConfig.CUSTOM_ENCHANTER_ALGORITHM.get()) {
            ls$customEnchantmentTableAlgorithm(level, stack, possibleEnchantments, cir);
        }
        else {
            ls$blacklistEnchantments(level, stack, possibleEnchantments, cir);
        }
        //?}

    }

    //? if <= 1.20.5 {
    /*@Unique
    private static void ls$blacklistEnchantments(int level, ItemStack stack, boolean bl, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl2 = stack.is(Items.BOOK);

        for(Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            Optional<ResourceKey<Enchantment>> key = BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment);
            if (key.isPresent() && blacklist.getBannedEnchants().contains(key.get())) {
                continue;
            }
            //? if <= 1.20 {
            /^if ((!enchantment.isTreasureOnly() || bl) && enchantment.isDiscoverable() && (enchantment.category.canEnchant(item) || bl2)) {
            ^///?} else {
            if ((!enchantment.isTreasureOnly() || bl) && enchantment.isDiscoverable() && (enchantment.canEnchant(stack) || bl2)) {
            //?}
                for(int j = enchantment.getMaxLevel(); j > enchantment.getMinLevel() - 1; --j) {
                    if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                        boolean clamped = false;
                        var clampedEnchants = blacklist.getClampedEnchants();
                        for (int clampLevel = 1; clampLevel <= 4; clampLevel++) {
                            var levelClamped = clampedEnchants.get(clampLevel);
                            if (levelClamped == null) continue;
                            if (key.isPresent() && levelClamped.contains(key.get())) {
                                list.add(new EnchantmentInstance(enchantment, clampLevel));
                                clamped = true;
                                break;
                            }
                        }
                        if (!clamped) {
                            list.add(new EnchantmentInstance(enchantment, j));
                        }
                        break;
                    }
                }
            }
        }

        cir.setReturnValue(list);
    }
    @Unique
    private static void ls$customEnchantmentTableAlgorithm(int level, ItemStack stack, boolean bl, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl2 = stack.is(Items.BOOK);

        for(Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            Optional<ResourceKey<Enchantment>> key = BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment);
            //? if <= 1.20 {
            /^if ((!enchantment.isTreasureOnly() || bl) && enchantment.isDiscoverable() && (enchantment.category.canEnchant(item) || bl2)) {
            ^///?} else {
            if ((!enchantment.isTreasureOnly() || bl) && enchantment.isDiscoverable() && (enchantment.canEnchant(stack) || bl2)) {
            //?}
                boolean clamped = false;
                var clampedEnchants = blacklist.getClampedEnchants();
                for (int clampLevel = 1; clampLevel <= 4; clampLevel++) {
                    var levelClamped = clampedEnchants.get(clampLevel);
                    if (levelClamped == null) continue;
                    if (key.isPresent() && levelClamped.contains(key.get())) {
                        list.add(new EnchantmentInstance(enchantment, clampLevel));
                        clamped = true;
                        break;
                    }
                }
                if (!clamped) {
                    for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                        if (j == 1) {
                            if (enchantment.getMaxLevel() <= 3 || level < 4) {
                                list.add(new EnchantmentInstance(enchantment, j));
                            }
                        }
                        else if (j == 2 && level > 4 && enchantment.getMaxLevel() > 3) {
                            list.add(new EnchantmentInstance(enchantment, j));
                        }
                        else if (j == 2 && level > 6 && enchantment.getMaxLevel() >= 3) {
                            list.add(new EnchantmentInstance(enchantment, j));
                        }
                        else if (j == 3 && level > 6 && enchantment.getMaxLevel() > 3) {
                            list.add(new EnchantmentInstance(enchantment, j));
                        }
                    }
                }
            }
        }

        cir.setReturnValue(list);
    }
    *///?} else {
    @Unique
    private static void ls$blacklistEnchantments(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.<EnchantmentInstance>newArrayList();
        boolean bl = stack.is(Items.BOOK);
        //? if <= 1.20.5 {
        /*possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentHolder -> {
        Enchantment enchantmentx = enchantmentHolder.value();
        Enchantment enchantment = enchantmentx;
        Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentHolder.unwrapKey();
        *///?} else {
        possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
        Enchantment enchantment = (Enchantment)enchantmentx.value();
        Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentx.unwrapKey();
        //?}
            boolean isRegistryPresent = enchantRegistryKey.isPresent();
            if (isRegistryPresent && !blacklist.getBannedEnchants().contains(enchantRegistryKey.get())) {
                for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                    if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                        boolean clamped = false;
                        var clampedEnchants = blacklist.getClampedEnchants();
                        for (int clampLevel = 1; clampLevel <= 4; clampLevel++) {
                            var levelClamped = clampedEnchants.get(clampLevel);
                            if (levelClamped == null) continue;
                            if (levelClamped.contains(enchantRegistryKey.get())) {
                                list.add(new EnchantmentInstance(enchantmentx, clampLevel));
                                clamped = true;
                                break;
                            }
                        }
                        if (!clamped) {
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
        //? if <= 1.20.5 {
        /*possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentHolder -> {
        Enchantment enchantmentx = enchantmentHolder.value();
        Enchantment enchantment = enchantmentx;
        Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentHolder.unwrapKey();
        *///?} else {
        possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
        Enchantment enchantment = (Enchantment)enchantmentx.value();
        Optional<ResourceKey<Enchantment>> enchantRegistryKey = enchantmentx.unwrapKey();
        //?}
            if (enchantRegistryKey.isPresent() && !blacklist.getBannedEnchants().contains(enchantRegistryKey.get())) {
                boolean clamped = false;
                var clampedEnchants = blacklist.getClampedEnchants();
                for (int clampLevel = 1; clampLevel <= 4; clampLevel++) {
                    var levelClamped = clampedEnchants.get(clampLevel);
                    if (levelClamped == null) continue;
                    if (levelClamped.contains(enchantRegistryKey.get())) {
                        list.add(new EnchantmentInstance(enchantmentx, clampLevel));
                        clamped = true;
                        break;
                    }
                }
                if (!clamped) {
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
    //?}

    //? if <= 1.20.5 {
    /*@Inject(
            method = "doPostDamageEffects", at = @At("HEAD")
    )
    private static void onTargetDamaged(LivingEntity victimEntity, Entity sourceEntity, CallbackInfo ci) {
    *///?} else {
    @Inject(
            method = "doPostAttackEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD")
    )
    private static void onTargetDamaged(ServerLevel level, Entity victimEntity, DamageSource damageSource, CallbackInfo ci) {
    //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (!(victimEntity instanceof ServerPlayer victim)) return;
        //? if <= 1.20.5 {
        /*if (sourceEntity == null) return;
        if (!SuperpowersWildcard.hasActivatedPower(victim, Superpowers.SUPER_PUNCH)) return;
        sourceEntity.hurt(victim.damageSources().thorns(victim), 1F);
        *///?} else {
        if (damageSource == null) return;
        if (damageSource.getEntity() == null) return;
        if (!SuperpowersWildcard.hasActivatedPower(victim, Superpowers.SUPER_PUNCH)) return;
        //? if <= 1.21 {
        /*damageSource.getEntity().hurt(victim.damageSources().thorns(victim), 1F);
        *///?} else {
        damageSource.getEntity().hurtServer(victim.ls$getServerLevel(), victim.damageSources().thorns(victim), 1F);
        //?}
        //?}
    }
}
