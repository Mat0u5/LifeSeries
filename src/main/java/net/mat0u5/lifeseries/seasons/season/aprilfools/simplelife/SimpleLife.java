package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

//? if <= 1.20 {
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.NaturalSpawner;
//?} else {
/*import net.minecraft.world.item.trading.ItemCost;
*///?}

//? if <= 1.21.9 {
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
//?} else {
/*import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
*///?}

public class SimpleLife extends ThirdLife {
    private Random rnd = new Random();
    private int checkCooldown = 0;
    @Override
    public Seasons getSeason() {
        return Seasons.SIMPLE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new SimpleLifeConfig();
    }

    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = 1200; //1 Minute
            ServerLevel level = server.overworld();
            if (level == null) return;
            int traderCount = 0;
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof WanderingTrader) {
                    traderCount++;
                }
            }
            if (traderCount == 0) checkCooldown = 1200; //1 Minute
            if (traderCount == 1) checkCooldown = 3600; //3 Minutes
            if (traderCount >= 2) checkCooldown = 200;
            if (traderCount >= 3) return;
            for (int i = 0; i < 5; i++) {
                if (trySpawnTrader(level)) {
                    break;
                }
            }
        }
    }

    public boolean trySpawnTrader(ServerLevel level) {
        Player playerEntity = level.getRandomPlayer();
        if (playerEntity == null) {
            return true;
        } else {
            BlockPos blockPos = playerEntity.blockPosition();
            PoiManager pointOfInterestStorage = level.getPoiManager();
            Optional<BlockPos> optional = pointOfInterestStorage.find((poiType) -> {
                return poiType.is(PoiTypes.MEETING);
            }, (pos) -> {
                return true;
            }, blockPos, 64, PoiManager.Occupancy.ANY);
            BlockPos blockPos2 = (BlockPos)optional.orElse(blockPos);
            BlockPos blockPos3 = this.getNearbySpawnPos(level, blockPos2, 64);
            if (blockPos3 != null && this.doesNotSuffocateAt(level, blockPos3)) {
                WanderingTrader wanderingTraderEntity = LevelUtils.spawnEntity(EntityType.WANDERING_TRADER, level, blockPos3);
                if (wanderingTraderEntity != null) {
                    for(int j = 0; j < 2; ++j) {
                        this.spawnLlama(level, wanderingTraderEntity, 4);
                    }

                    wanderingTraderEntity.setDespawnDelay(12000);

                    MerchantOffers offers = wanderingTraderEntity.getOffers();
                    offers.clear();
                    //? if <= 1.20 {
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 5), ItemStack.EMPTY, Items.IRON_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 40), ItemStack.EMPTY, Items.WATER_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 40), ItemStack.EMPTY, Items.LAVA_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 1), ItemStack.EMPTY, Items.SAND.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 1), ItemStack.EMPTY, Items.GRAVEL.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 5), ItemStack.EMPTY, Items.GOLD_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 1), ItemStack.EMPTY, Items.REDSTONE.getDefaultInstance(), 0, 999999, 0, 0, 0));

                    int rand = rnd.nextInt(2);
                    if (rand == 0) offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 32), ItemStack.EMPTY, Items.OAK_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    if (rand == 1) offers.add(new MerchantOffer(new ItemStack(Items.DIRT, 32), ItemStack.EMPTY, Items.SPRUCE_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    //?} else {
                    /*offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 5), Optional.empty(), Items.IRON_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 40), Optional.empty(), Items.WATER_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(), Items.SAND.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(), Items.GRAVEL.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 5), Optional.empty(), Items.GOLD_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(), Items.REDSTONE.getDefaultInstance(), 0, 999999, 0, 0, 0));

                    int rand = rnd.nextInt(2);
                    if (rand == 0) offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 32), Optional.empty(), Items.OAK_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    if (rand == 1) offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 32), Optional.empty(), Items.SPRUCE_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
                    *///?}

                    wanderingTraderEntity.overrideOffers(offers);
                    wanderingTraderEntity.addTag("SimpleLifeTrader");
                    return true;
                }
            }

            return false;
        }
    }

    private void spawnLlama(ServerLevel level, WanderingTrader wanderingTrader, int range) {
        BlockPos blockPos = this.getNearbySpawnPos(level, wanderingTrader.blockPosition(), range);
        if (blockPos != null) {
            TraderLlama traderLlamaEntity = LevelUtils.spawnEntity(EntityType.TRADER_LLAMA, level, blockPos);
            if (traderLlamaEntity != null) {
                traderLlamaEntity.setLeashedTo(wanderingTrader, true);
            }
        }
    }

    private BlockPos getNearbySpawnPos(LevelReader world, BlockPos pos, int range) {
        BlockPos blockPos = null;
        //? if > 1.20 {
        /*SpawnPlacementType spawnLocation = SpawnPlacements.getPlacementType(EntityType.WANDERING_TRADER);
        *///?}

        for(int i = 0; i < 10; ++i) {
            int j = pos.getX() + rnd.nextInt(range * 2) - range;
            int k = pos.getZ() + rnd.nextInt(range * 2) - range;
            int l = world.getHeight(Heightmap.Types.WORLD_SURFACE, j, k);
            BlockPos blockPos2 = new BlockPos(j, l, k);
            //? if <= 1.20 {
            if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, world, blockPos2, EntityType.WANDERING_TRADER)) {
            //?} else {
            /*if (spawnLocation.isSpawnPositionOk(world, blockPos2, EntityType.WANDERING_TRADER)) {
            *///?}
                blockPos = blockPos2;
                break;
            }
        }

        return blockPos;
    }

    private boolean doesNotSuffocateAt(BlockGetter world, BlockPos pos) {
        Iterator var3 = BlockPos.betweenClosed(pos, pos.offset(1, 2, 1)).iterator();

        BlockPos blockPos;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            blockPos = (BlockPos)var3.next();
        } while(world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty());

        return false;
    }
}
