package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MobRegistry {
    //? if <= 1.21 {
    public static final EntityType<Snail> SNAIL = register(
            Snail.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Snail::new)
                    .spawnGroup(MobCategory.MONSTER)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.6f))
                    .trackRangeBlocks(512)
                    .defaultAttributes(Snail::createAttributes)
    );
    public static final EntityType<PathFinder> PATH_FINDER = register(
            PathFinder.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(PathFinder::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.5f, 0.6f))
                    .trackRangeBlocks(0)
                    .defaultAttributes(PathFinder::createAttributes)
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = register(
            TriviaBot.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(TriviaBot::new)
                    .spawnGroup(MobCategory.AMBIENT)
                    .dimensions(EntityDimensions.scalable(0.65f, 1.8f))
                    .trackRangeBlocks(512)
                    .defaultAttributes(TriviaBot::createAttributes)
    );

    private static <T extends Entity> EntityType<T> register(ResourceLocation id, FabricEntityTypeBuilder<T> builder) {
        EntityType<T> type = builder.build();
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }
    //?} else {
    /*public static final EntityType<Snail> SNAIL = register(
            Snail.ID,
            FabricEntityType.Builder.createMob(Snail::new, SpawnGroup.MONSTER, x -> x
                            .defaultAttributes(Snail::createAttributes))
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(512)
    );
    public static final EntityType<PathFinder> PATH_FINDER = register(
            PathFinder.ID,
            FabricEntityType.Builder.createMob(PathFinder::new, SpawnGroup.AMBIENT, x -> x
                            .defaultAttributes(PathFinder::createAttributes))
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(0)
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = register(
            TriviaBot.ID,
            FabricEntityType.Builder.createMob(TriviaBot::new, SpawnGroup.AMBIENT, x -> x
                            .defaultAttributes(TriviaBot::createAttributes))
                    .dimensions(0.65f, 1.8f)
                    .maxTrackingRange(512)
    );

    private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), id));
        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }
    *///?}

    public static void registerMobs() {
    }
}

