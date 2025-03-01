package net.mat0u5.lifeseries.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    public void spawnRandomItem(ServerWorld world, Vec3d pos) {
        spawnRandomItemForPlayer(world, pos, null);
    }
    public void spawnRandomItemForPlayer(ServerWorld world, Vec3d pos, ServerPlayerEntity player) {
        ItemStack randomItem = getRandomItem();
        ItemStackUtils.spawnItemForPlayer(world, pos, randomItem, player);
    }
    public void spawnRandomItemForPlayerWithVelocity(ServerWorld world, Vec3d pos, Vec3d velocity, ServerPlayerEntity player) {
        ItemStack randomItem = getRandomItem();
        ItemStackUtils.spawnItemForPlayerWithVelocity(world, pos, randomItem, player, velocity);
    }
}
