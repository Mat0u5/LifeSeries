package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.*;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
 //?} else {
/*import net.minecraft.resources.Identifier;
*///?}

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


    public static List<ItemStack> getRandomItemsFromLootTable(MinecraftServer server, ServerLevel level, ServerPlayer player
          //? if <= 1.21.9 {
            , ResourceLocation lootTableId, boolean silent) {
          //?} else {
            /*, Identifier lootTableId, boolean silent) {
          *///?}
        if (server == null || level == null || player == null) return new ArrayList<>();
        try {
            //? if <= 1.21 {
            LootParams parameters = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.COMMAND);
            //?} else {
            /*LootParams parameters = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.COMMAND);
            *///?}

            //? if <= 1.20.3 {
            LootTable lootTable = level.getServer().getLootData().getLootTable(lootTableId);
            //?} else {
            /*LootTable lootTable = level.getServer()
                    .reloadableRegistries()
                    .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
            *///?}

            if (lootTable == null) {
                if (!silent) Main.LOGGER.error("Loot table not found: " + lootTableId);
                return new ArrayList<>();
            }

            List<ItemStack> generatedLoot = lootTable.getRandomItems(parameters);

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
