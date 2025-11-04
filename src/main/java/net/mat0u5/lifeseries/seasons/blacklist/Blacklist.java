package net.mat0u5.lifeseries.seasons.blacklist;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.mat0u5.lifeseries.Main.seasonConfig;
import static net.mat0u5.lifeseries.Main.server;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

public class Blacklist {
    //? if <= 1.21.9 {
    public List<ResourceLocation> loadedListItemIdentifier;
    //?} else {
    /*public List<Identifier> loadedListItemIdentifier;
    *///?}
    private List<Item> loadedListItem;
    private List<Block> loadedListBlock;
    private List<ResourceKey<Enchantment>> loadedListEnchants;
    private List<ResourceKey<Enchantment>> loadedBannedEnchants;

    private List<Holder<MobEffect>> loadedBannedEffects;
    
    public boolean CREATIVE_IGNORE_BLACKLIST = true;

    public List<String> loadItemBlacklist() {
        if (seasonConfig == null) return new ArrayList<>();
        String raw = seasonConfig.BLACKLIST_ITEMS.get(seasonConfig);
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public List<String> loadBlockBlacklist() {
        if (seasonConfig == null) return new ArrayList<>();
        String raw = seasonConfig.BLACKLIST_BLOCKS.get(seasonConfig);
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public List<String> loadClampedEnchants() {
        if (seasonConfig == null) return new ArrayList<>();
        String raw = seasonConfig.BLACKLIST_CLAMPED_ENCHANTS.get(seasonConfig);
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public List<String> loadBlacklistedEnchants() {
        if (seasonConfig == null) return new ArrayList<>();
        String raw = seasonConfig.BLACKLIST_BANNED_ENCHANTS.get(seasonConfig);
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public List<String> loadBannedPotions() {
        if (seasonConfig == null) return new ArrayList<>();
        String raw = seasonConfig.BLACKLIST_BANNED_POTION_EFFECTS.get(seasonConfig);
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        if (raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public List<Item> getItemBlacklist() {
        if (loadedListItem != null) return loadedListItem;
        List<Item> newList = new ArrayList<>();
        //? if <= 1.21.9 {
        List<ResourceLocation> newListIdentifier = new ArrayList<>();
        //?} else {
        /*List<Identifier> newListIdentifier = new ArrayList<>();
        *///?}

        if (seasonConfig != null) {
            if (!seasonConfig.SPAWNER_RECIPE.get(seasonConfig)) {
                newListIdentifier.add(IdentifierHelper.mod("spawner_recipe"));
            }
        }

        for (String itemId : loadItemBlacklist()) {
            if (!itemId.contains(":")) itemId = "minecraft:" + itemId;

            try {
                var id = IdentifierHelper.parse(itemId);
                ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

                // Check if the block exists in the registry
                //? if <= 1.21 {
                Item item = BuiltInRegistries.ITEM.get(key);
                //?} else {
                /*Item item = BuiltInRegistries.ITEM.getValue(key);
                *///?}
                if (item != null) {
                    newListIdentifier.add(id);
                    newList.add(item);
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid item: " + itemId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing item ID: " + itemId);
            }
        }

        loadedListItem = newList;
        loadedListItemIdentifier = newListIdentifier;
        return newList;
    }

    public List<Block> getBlockBlacklist() {
        if (loadedListBlock != null) return loadedListBlock;
        List<Block> newList = new ArrayList<>();

        for (String blockId : loadBlockBlacklist()) {
            if (!blockId.contains(":")) blockId = "minecraft:" + blockId;

            try {
                var id = IdentifierHelper.parse(blockId);
                ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(), id);

                // Check if the block exists in the registry
                //? if <= 1.21 {
                Block block = BuiltInRegistries.BLOCK.get(key);
                //?} else {
                /*Block block = BuiltInRegistries.BLOCK.getValue(key);
                *///?}
                if (block != null) {
                    newList.add(block);
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid block: " + blockId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing block ID: " + blockId);
            }
        }

        loadedListBlock = newList;
        return newList;
    }

    public List<ResourceKey<Enchantment>> getClampedEnchants() {
        if (server == null) return new ArrayList<>();

        if (loadedListEnchants != null) return loadedListEnchants;
        List<ResourceKey<Enchantment>> newList = new ArrayList<>();

        Registry<Enchantment> enchantmentRegistry = server.registryAccess()

                //? if <=1.21 {
                .registryOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));
                 //?} else
                /*.lookupOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));*/


        for (String enchantmentId : loadClampedEnchants()) {
            if (!enchantmentId.contains(":")) enchantmentId = "minecraft:" + enchantmentId;

            try {
                var id = IdentifierHelper.parse(enchantmentId);
                //? if <= 1.21 {
                Enchantment enchantment = enchantmentRegistry.get(id);
                //?} else {
                /*Enchantment enchantment = enchantmentRegistry.getValue(id);
                *///?}

                if (enchantment != null) {
                    newList.add(enchantmentRegistry.getResourceKey(enchantment).orElseThrow());
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid enchantment: " + enchantmentId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing enchantment ID: " + enchantmentId);
            }
        }

        loadedListEnchants = newList;
        return newList;
    }

    public List<ResourceKey<Enchantment>> getBannedEnchants() {
        if (server == null) return new ArrayList<>();

        if (loadedBannedEnchants != null) return loadedBannedEnchants;
        List<ResourceKey<Enchantment>> newList = new ArrayList<>();

        Registry<Enchantment> enchantmentRegistry = server.registryAccess()

                //? if <=1.21 {
                .registryOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));
        //?} else
        /*.lookupOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("enchantment")));*/


        for (String enchantmentId : loadBlacklistedEnchants()) {
            if (!enchantmentId.contains(":")) enchantmentId = "minecraft:" + enchantmentId;

            try {
                var id = IdentifierHelper.parse(enchantmentId);
                //? if <= 1.21 {
                Enchantment enchantment = enchantmentRegistry.get(id);
                //?} else {
                /*Enchantment enchantment = enchantmentRegistry.getValue(id);
                *///?}

                if (enchantment != null) {
                    newList.add(enchantmentRegistry.getResourceKey(enchantment).orElseThrow());
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid enchantment: " + enchantmentId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing enchantment ID: " + enchantmentId);
            }
        }

        loadedBannedEnchants = newList;
        return newList;
    }

    public List<Holder<MobEffect>> getBannedEffects() {
        if (server == null) return new ArrayList<>();

        if (loadedBannedEffects != null) return loadedBannedEffects;
        List<Holder<MobEffect>> newList = new ArrayList<>();

        Registry<MobEffect> effectsRegistry = server.registryAccess()
        //? if <=1.21 {
        .registryOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("mob_effect")));
        //?} else
        /*.lookupOrThrow(ResourceKey.createRegistryKey(IdentifierHelper.vanilla("mob_effect")));*/

        for (String potionId : loadBannedPotions()) {
            if (!potionId.contains(":")) potionId = "minecraft:" + potionId;

            try {
                var id = IdentifierHelper.parse(potionId);
                //? if <= 1.21 {
                MobEffect enchantment = effectsRegistry.get(id);
                //?} else {
                /*MobEffect enchantment = effectsRegistry.getValue(id);
                *///?}

                if (enchantment != null) {
                    newList.add(effectsRegistry.wrapAsHolder(enchantment));
                } else {
                    OtherUtils.throwError("[CONFIG] Invalid effect: " + potionId);
                }
            } catch (Exception e) {
                OtherUtils.throwError("[CONFIG] Error parsing effect ID: " + potionId);
            }
        }

        loadedBannedEffects = newList;
        return newList;
    }

    public void reloadBlacklist() {
        if (Main.server == null) return;

        CREATIVE_IGNORE_BLACKLIST = seasonConfig.CREATIVE_IGNORE_BLACKLIST.get(seasonConfig);
        
        loadedListItem = null;
        loadedListBlock = null;
        loadedListEnchants = null;
        loadedBannedEnchants = null;
        loadedBannedEffects = null;
        getItemBlacklist();
        getBlockBlacklist();
        getClampedEnchants();
        getBannedEnchants();
        getBannedEffects();
    }

    public InteractionResult onBlockUse(ServerPlayer player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isCreative() && CREATIVE_IGNORE_BLACKLIST) return InteractionResult.PASS;
        processItemStack(player, player.getItemInHand(hand));
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState block = level.getBlockState(blockPos);
        if (block.isAir()) return InteractionResult.PASS;
        if (getBlockBlacklist().contains(block.getBlock())) {
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onBlockAttack(ServerPlayer player, Level level, BlockPos pos) {
        if (player.isCreative() && CREATIVE_IGNORE_BLACKLIST) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.PASS;
        BlockState block = level.getBlockState(pos);
        if (block.isAir()) return InteractionResult.PASS;
        if (getBlockBlacklist().contains(block.getBlock())) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public void onCollision(ServerPlayer player, ItemStack stack, CallbackInfo ci) {
        if (player.isCreative() && CREATIVE_IGNORE_BLACKLIST) return;
        processItemStack(player, stack);
    }

    public void onInventoryUpdated(ServerPlayer player) {
        if (Main.server == null) return;
        Inventory inventory = player.getInventory();
        if (player.isCreative() && CREATIVE_IGNORE_BLACKLIST) return;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            processItemStack(player, inventory.getItem(i));
        }
        PlayerUtils.updatePlayerInventory(player);
    }

    public boolean isBlacklistedItemSimple(ItemStack itemStack) {
        return getItemBlacklist().contains(itemStack.getItem());
    }

    public boolean isBlacklistedItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (getItemBlacklist().contains(item)) return true;
        if (item != Items.POTION && item != Items.LINGERING_POTION && item != Items.SPLASH_POTION) return false;

        PotionContents potions = itemStack.getComponents().get(DataComponents.POTION_CONTENTS);
        if (potions == null) return false;
        for (MobEffectInstance effect : potions.getAllEffects()) {
            if (getBannedEffects().contains(effect.getEffect())) return true;
        }
        return false;
    }

    public void processItemStack(ServerPlayer player, ItemStack itemStack) {
        if (player.isCreative() && CREATIVE_IGNORE_BLACKLIST) return;
        if (itemStack.isEmpty()) return;
        if (itemStack.getItem() == Items.AIR) return;
        if (isBlacklistedItem(itemStack) && !ItemStackUtils.hasCustomComponentEntry(itemStack, "IgnoreBlacklist")) {
            itemStack.setCount(0);
            player.getInventory().tick();
            return;
        }

        if (ItemStackUtils.hasCustomComponentEntry(itemStack, "FromSuperpower")) {
            boolean remove = true;
            if (ItemStackUtils.hasCustomComponentEntry(itemStack, "WindChargeSuperpower")) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
                    remove = false;
                }
            }
            //? if >= 1.21.2 {
            /*if (ItemStackUtils.hasCustomComponentEntry(itemStack, "FlightSuperpower")) {
                if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
                    remove = false;
                }
            }
            *///?}
            if (remove) {
                itemStack.setCount(0);
                player.getInventory().tick();
                return;
            }
            return;
        }
        ItemEnchantments enchants = itemStack.getComponents().get(DataComponents.ENCHANTMENTS);
        ItemEnchantments enchantsStored = itemStack.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
        if (enchants != null) {
            itemStack.set(DataComponents.ENCHANTMENTS, clampAndBlacklistEnchantments(enchants));
        }
        if (enchantsStored != null) {
            itemStack.set(DataComponents.STORED_ENCHANTMENTS, clampAndBlacklistEnchantments(enchantsStored));
        }
    }

    public ItemEnchantments clampAndBlacklistEnchantments(ItemEnchantments enchants) {
        ItemEnchantments afterBlacklist = blacklistEnchantments(enchants);
        clampEnchantments(afterBlacklist);
        return afterBlacklist;
    }

    public ItemEnchantments blacklistEnchantments(ItemEnchantments enchants) {
        if (enchants.isEmpty()) return enchants;
        List<ResourceKey<Enchantment>> banned = getBannedEnchants();
        if (banned.isEmpty()) return enchants;
        List<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>>> toRemove = new ArrayList<>();
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> enchant : enchants.entrySet()) {
            Optional<ResourceKey<Enchantment>> enchantRegistry = enchant.getKey().unwrapKey();
            if (enchantRegistry.isEmpty()) continue;
            if (banned.contains(enchantRegistry.get())) {
                toRemove.add(enchant);
            }
        }
        if (toRemove.isEmpty()) return enchants;
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> enchant : enchants.entrySet()) {
            if (toRemove.contains(enchant)) continue;
            builder.upgrade(enchant.getKey(), enchant.getIntValue());
        }

        return builder.toImmutable();
    }

    public void clampEnchantments(ItemEnchantments enchants) {
        List<ResourceKey<Enchantment>> clamp = getClampedEnchants();
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Holder<Enchantment>> enchant : enchants.entrySet()) {
            Optional<ResourceKey<Enchantment>> enchantRegistry = enchant.getKey().unwrapKey();
            if (enchantRegistry.isEmpty()) continue;
            if (clamp.contains(enchantRegistry.get())) {
                enchant.setValue(1);
            }
        }
    }
}