package net.mat0u5.lifeseries.utils.world;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


import static net.mat0u5.lifeseries.Main.server;
//? if <= 1.21
//import net.minecraft.world.item.EnchantedBookItem;
//? if >= 1.21.2
import net.minecraft.world.item.enchantment.EnchantmentHelper;
//? if >= 1.21.5
import java.util.Optional;

//? if <= 1.20.3 {
//?} else {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
//?}

public class ItemStackUtils {
    //? if <= 1.20.3 {

    /*public static void setCustomComponentInt(ItemStack itemStack, String componentKey, int value) {
        if (itemStack == null) return;
        CompoundTag currentNbt = itemStack.getOrCreateTag();
        currentNbt.putInt(componentKey,value);
        itemStack.setTag(currentNbt);
    }

    public static void setCustomComponentBoolean(ItemStack itemStack, String componentKey, boolean value) {
        if (itemStack == null) return;
        CompoundTag currentNbt = itemStack.getOrCreateTag();
        currentNbt.putBoolean(componentKey,value);
        itemStack.setTag(currentNbt);
    }

    public static void setCustomComponentString(ItemStack itemStack, String componentKey, String value) {
        if (itemStack == null) return;
        CompoundTag currentNbt = itemStack.getTag();
        currentNbt.putString(componentKey,value);
        itemStack.setTag(currentNbt);
    }

    public static String getCustomComponentString(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CompoundTag currentNbt = itemStack.getTag();
        if (currentNbt == null) return null;
        if (!currentNbt.contains(componentKey)) return null;
        return currentNbt.getString(componentKey);

    }

    public static Integer getCustomComponentInt(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CompoundTag currentNbt = itemStack.getTag();
        if (currentNbt == null) return null;
        if (!currentNbt.contains(componentKey)) return null;
        return currentNbt.getInt(componentKey);
    }

    public static Byte getCustomComponentByte(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CompoundTag currentNbt = itemStack.getTag();
        if (currentNbt == null) return null;
        if (!currentNbt.contains(componentKey)) return null;
        return currentNbt.getByte(componentKey);
    }

    public static Boolean getCustomComponentBoolean(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CompoundTag currentNbt = itemStack.getTag();
        if (currentNbt == null) return null;
        if (!currentNbt.contains(componentKey)) return null;
        return currentNbt.getBoolean(componentKey);
    }

    public static boolean hasCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        if (itemStack == null) return false;
        CompoundTag currentNbt = itemStack.getTag();
        if (currentNbt == null) return false;
        return currentNbt.contains(componentEntry);
    }

    public static void removeCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null) return;
        if (!nbt.contains(componentEntry)) return;
        nbt.remove(componentEntry);
        itemStack.setTag(nbt);
    }
    *///?} else {
    public static void addLoreToItemStack(ItemStack itemStack, List<Component> lines) {
        List<Component> loreLines = getLore(itemStack);
        if (lines != null && !lines.isEmpty()) loreLines.addAll(lines);
        ItemLore lore = new ItemLore(loreLines);
        itemStack.set(DataComponents.LORE, lore);
    }

    public static List<Component> getLore(ItemStack itemStack) {
        ItemLore lore = itemStack.get(DataComponents.LORE);
        if (lore == null) return new ArrayList<>();
        List<Component> lines = lore.lines();
        if (lines == null) return new ArrayList<>();
        if (lines.isEmpty()) return new ArrayList<>();
        return lines;
    }

    public static void setCustomComponentInt(ItemStack itemStack, String componentKey, int value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putInt(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }

    public static void setCustomComponentBoolean(ItemStack itemStack, String componentKey, boolean value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putBoolean(componentKey, value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }

    public static void setCustomComponentString(ItemStack itemStack, String componentKey, String value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putString(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }

    public static String getCustomComponentString(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        /*return nbtComp.getString(componentKey);
        *///?} else {
        Optional<String> optional = nbtComp.getString(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        //?}

    }

    public static Integer getCustomComponentInt(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        /*return nbtComp.getInt(componentKey);
        *///?} else {
        Optional<Integer> optional = nbtComp.getInt(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        //?}
    }

    public static Byte getCustomComponentByte(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        /*return nbtComp.getByte(componentKey);
        *///?} else {
        Optional<Byte> optional = nbtComp.getByte(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        //?}
    }

    public static Boolean getCustomComponentBoolean(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        /*return nbtComp.getBoolean(componentKey);
        *///?} else {
        Optional<Boolean> optional = nbtComp.getBoolean(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        //?}
    }

    public static boolean hasCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        if (itemStack == null) return false;
        CustomData nbt = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return false;
        //? if <= 1.21.6 {
        /*return nbt.contains(componentEntry);
        *///?} else {
        return nbt.copyTag().contains(componentEntry);
         //?}
    }

    public static void removeCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        CustomData nbt = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return;
        //? if <= 1.21.6 {
        /*if (!nbt.contains(componentEntry)) return;
        *///?} else {
        if (!nbt.copyTag().contains(componentEntry)) return;
         //?}
        CompoundTag nbtComp = nbt.copyTag();
        nbtComp.remove(componentEntry);
        if (nbtComp.isEmpty()) {
            itemStack.set(DataComponents.CUSTOM_DATA, itemStack.getPrototype().get(DataComponents.CUSTOM_DATA));
        }
        else {
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
        }
    }
    //?}

    public static ItemEntity spawnItem(ServerLevel level, Vec3 position, ItemStack stack) {
        return spawnItemForPlayer(level, position, stack, null);
    }

    public static ItemEntity spawnItemForPlayer(ServerLevel level, Vec3 position, ItemStack stack, Player player) {
        if (level == null || stack.isEmpty()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(level, position.x, position.y, position.z, stack);
        itemEntity.setPickUpDelay(20);
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().x()/4, 0.2, itemEntity.getDeltaMovement().z()/4);
        if (player != null) itemEntity.setTarget(player.getUUID());

        level.addFreshEntity(itemEntity);
        return itemEntity;
    }
    public static ItemEntity spawnItemForPlayerWithVelocity(ServerLevel level, Vec3 position, ItemStack stack, Player player, Vec3 velocity) {
        if (level == null || stack.isEmpty()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(level, position.x, position.y, position.z, stack);
        itemEntity.setPickUpDelay(20);
        itemEntity.setDeltaMovement(velocity);
        if (player != null) itemEntity.setTarget(player.getUUID());

        level.addFreshEntity(itemEntity);
        return itemEntity;
    }

    public static ItemStack createEnchantedBook(ResourceKey<Enchantment> enchantment, int level) {
        if (server == null) return null;
        //? if <= 1.20.5 {
        /*Holder<Enchantment> entry = getEnchantmentEntry(enchantment);
        if (entry == null) return null;
        ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(entry.value(), level)
        );
        return enchantedBook;
        *///?} else if <= 1.21 {
        /*Holder<Enchantment> entry = getEnchantmentEntry(enchantment);
        ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(entry, level)
        );
        return enchantedBook;
        *///?} else {
        Holder<Enchantment> entry = getEnchantmentEntry(enchantment);
        ItemStack enchantedBook = EnchantmentHelper.createBook(
                new EnchantmentInstance(entry, level)
        );
        return enchantedBook;
        //?}
    }

    @Nullable
    public static Holder<Enchantment> getEnchantmentEntry(ResourceKey<Enchantment> enchantment) {
        if (server == null) return null;
        //? if <= 1.21 {
        /*return server.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantment);
        *///?} else {
        return server.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantment);
        //?}
    }
}
