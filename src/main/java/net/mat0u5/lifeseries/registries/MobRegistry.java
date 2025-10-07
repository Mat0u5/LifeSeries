package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mat0u5.lifeseries.entity.newsnail.NewSnail;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

//? if <= 1.21 {
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
//?}
//? if >= 1.21.2 {
/*import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.registry.RegistryKey;
*///?}

public class MobRegistry {

    //? if <= 1.21 {
    public static final EntityType<Snail> SNAIL = register(
            Snail.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(Snail::new)
                    .spawnGroup(SpawnGroup.MONSTER)
                    .dimensions(EntityDimensions.changing(0.5f, 0.6f))
                    .trackRangeChunks(10)
                    .defaultAttributes(Snail::createAttributes)
    );
    public static final EntityType<PathFinder> PATH_FINDER = register(
            PathFinder.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(PathFinder::new)
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .dimensions(EntityDimensions.changing(0.5f, 0.6f))
                    .trackRangeChunks(10)
                    .defaultAttributes(PathFinder::createAttributes)
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = register(
            TriviaBot.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(TriviaBot::new)
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .dimensions(EntityDimensions.changing(0.65f, 1.8f))
                    .trackRangeChunks(10)
                    .defaultAttributes(TriviaBot::createAttributes)
    );

    private static <T extends Entity> EntityType<T> register(Identifier id, FabricEntityTypeBuilder<T> builder) {
        EntityType<T> type = builder.build();
        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }
     //?} else {
    
    /*public static final EntityType<Snail> SNAIL = register(
            Snail.ID,
            FabricEntityType.Builder.createMob(Snail::new, SpawnGroup.MONSTER, x -> x
                            .defaultAttributes(Snail::createAttributes))
                        .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(10)
    );
    public static final EntityType<PathFinder> PATH_FINDER = register(
            PathFinder.ID,
            FabricEntityType.Builder.createMob(PathFinder::new, SpawnGroup.AMBIENT, x -> x
                            .defaultAttributes(PathFinder::createAttributes))
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(10)
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = register(
            TriviaBot.ID,
            FabricEntityType.Builder.createMob(TriviaBot::new, SpawnGroup.AMBIENT, x -> x
                            .defaultAttributes(TriviaBot::createAttributes))
                        .dimensions(0.65f, 1.8f)
                    .maxTrackingRange(10)
    );

    private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> builder) {
        EntityType<T> type = builder.build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), id));

        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }
    *///?}

    public static final EntityType<NewSnail> NEW_SNAIL = Registry.register(
            Registries.ENTITY_TYPE
            , NewSnail.ID
            , EntityType.Builder.create(NewSnail::new, SpawnGroup.MONSTER)
                    .dimensions(0.65f, 1.8f)
                    .maxTrackingRange(10*16)
                    .build()
    );

    public static void registerMobs() {
        FabricDefaultAttributeRegistry.register(NEW_SNAIL, NewSnail.createAttributes());
    }
}

