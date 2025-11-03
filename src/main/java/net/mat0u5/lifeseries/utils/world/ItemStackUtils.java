package net.mat0u5.lifeseries.utils.world;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.phys.Vec3;

import static net.mat0u5.lifeseries.Main.server;

public class ItemStackUtils {
    public static void clearItemLore(ItemStack itemStack) {
        itemStack.remove(DataComponents.LORE);
    }

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

    public static ItemStack getHoldingItem(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem != null) {
            if (!mainHandItem.isEmpty()) return mainHandItem;
        }
        return player.getOffhandItem();
    }

    public static void setCustomComponentInt(ItemStack itemStack, String componentKey, int value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putInt(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }

    public static void setCustomComponentByte(ItemStack itemStack, String componentKey, byte value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putByte(componentKey,value);
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
        return nbtComp.getString(componentKey);
        //?} else {
        /*Optional<String> optional = nbtComp.getString(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        *///?}

    }

    public static Integer getCustomComponentInt(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        return nbtComp.getInt(componentKey);
        //?} else {
        /*Optional<Integer> optional = nbtComp.getInt(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        *///?}
    }

    public static Byte getCustomComponentByte(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        return nbtComp.getByte(componentKey);
        //?} else {
        /*Optional<Byte> optional = nbtComp.getByte(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        *///?}
    }

    public static Boolean getCustomComponentBoolean(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        //? if <= 1.21.4 {
        return nbtComp.getBoolean(componentKey);
        //?} else {
        /*Optional<Boolean> optional = nbtComp.getBoolean(componentKey);
        if (optional.isEmpty()) return null;
        return optional.get();
        *///?}
    }

    public static boolean hasCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        if (itemStack == null) return false;
        CustomData nbt = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return false;
        //? if <= 1.21.6 {
        return nbt.contains(componentEntry);
        //?} else {
        /*return nbt.copyNbt().contains(componentEntry);
        *///?}
    }

    public static void removeCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        CustomData nbt = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return;
        //? if <= 1.21.6 {
        if (!nbt.contains(componentEntry)) return;
        //?} else {
        /*if (!nbt.copyNbt().contains(componentEntry)) return;
        *///?}
        CompoundTag nbtComp = nbt.copyTag();
        nbtComp.remove(componentEntry);
        if (nbtComp.isEmpty()) {
            itemStack.set(DataComponents.CUSTOM_DATA, itemStack.getPrototype().get(DataComponents.CUSTOM_DATA));
        }
        else {
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
        }
    }

    public static void spawnItem(ServerLevel world, Vec3 position, ItemStack stack) {
        spawnItemForPlayer(world, position, stack, null);
    }

    public static void spawnItemForPlayer(ServerLevel world, Vec3 position, ItemStack stack, Player player) {
        if (world == null || stack.isEmpty()) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(world, position.x, position.y, position.z, stack);
        itemEntity.setPickUpDelay(20);
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().x()/4, 0.2, itemEntity.getDeltaMovement().z()/4);
        if (player != null) itemEntity.setTarget(player.getUUID());

        world.addFreshEntity(itemEntity);
    }
    public static void spawnItemForPlayerWithVelocity(ServerLevel world, Vec3 position, ItemStack stack, Player player, Vec3 velocity) {
        if (world == null || stack.isEmpty()) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(world, position.x, position.y, position.z, stack);
        itemEntity.setPickUpDelay(20);
        itemEntity.setDeltaMovement(velocity);
        if (player != null) itemEntity.setTarget(player.getUUID());

        world.addFreshEntity(itemEntity);
    }

    public static ItemStack createEnchantedBook(ResourceKey<Enchantment> enchantment, int level) {
        if (server == null) return null;
        //? if <=1.21 {
        Holder<Enchantment> entry = getEnchantmentEntry(enchantment);
        ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(entry, level)
        );
        return enchantedBook;
        //?} else {

        /*RegistryEntry<Enchantment> entry = getEnchantmentEntry(enchantment);
        ItemStack enchantedBook = EnchantmentHelper.getEnchantedBookWith(
                new EnchantmentLevelEntry(entry, level)
        );
        return enchantedBook;
        *///?}
    }

    @Nullable
    public static Holder<Enchantment> getEnchantmentEntry(ResourceKey<Enchantment> enchantment) {
        if (server == null) return null;
        //? if <=1.21 {
        return server.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantment);
        //?} else {
        /*return server.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(enchantment);
        *///?}
    }
}
