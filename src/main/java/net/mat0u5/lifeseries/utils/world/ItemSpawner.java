package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.Main;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.*;
//? if <= 1.21
import net.minecraft.loot.context.LootContextParameterSet;
//? if >= 1.21.2
/*import net.minecraft.loot.context.LootWorldContext;*/

public class ItemSpawner {
    HashMap<ItemStack, Integer> lootTable = new HashMap<>();
    private static final Random random = new Random();

    public void addItem(ItemStack item, int weight) {
        lootTable.put(item.copy(), weight);
    }

    public ItemStack getRandomItem() {
        if (lootTable.isEmpty()) {
            return null;
        }

        int totalWeight = lootTable.values().stream().mapToInt(Integer::intValue).sum();

        int randomWeight = random.nextInt(totalWeight);

        for (Map.Entry<ItemStack, Integer> entry : lootTable.entrySet()) {
            randomWeight -= entry.getValue();
            if (randomWeight < 0) {
                return entry.getKey().copy();
            }
        }

        return null;
    }

    public static List<ItemStack> getRandomItemsFromLootTable(MinecraftServer server, ServerWorld world, ServerPlayerEntity player, Identifier lootTableId) {
        if (server == null || world == null || player == null) return new ArrayList<>();
        try {
            //? if <= 1.21 {
            LootContextParameterSet parameters = new LootContextParameterSet.Builder(world)
                    .add(LootContextParameters.ORIGIN, player.ls$getEntityPos())
                    .add(LootContextParameters.THIS_ENTITY, player)
                    .build(LootContextTypes.COMMAND);
            //?} else {
            /*LootWorldContext parameters = new LootWorldContext.Builder(world)
                    .add(LootContextParameters.ORIGIN, player.ls$getEntityPos())
                    .add(LootContextParameters.THIS_ENTITY, player)
                    .build(LootContextTypes.COMMAND);
            *///?}

            LootTable lootTable = world.getServer()
                    .getReloadableRegistries()
                    .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

            if (lootTable == null) {
                Main.LOGGER.error("Loot table not found: " + lootTableId);
                return new ArrayList<>();
            }

            List<ItemStack> generatedLoot = lootTable.generateLoot(parameters);

            if (generatedLoot == null || generatedLoot.isEmpty()) {
                Main.LOGGER.error("No loot generated from table: " + lootTableId);
                return new ArrayList<>();
            }

            return generatedLoot;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
