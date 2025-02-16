package net.mat0u5.lifeseries.registries;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerSpawnEggItem;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.pathfinder.PathFinder;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
        PolymerEntityUtils.registerType(type);
        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addSpawnEgg(EntityType type, Item item) {
        Item spawnEgg = new PolymerSpawnEggItem(type, item, new Item.Settings());
        registerItem(Identifier.of(Main.MOD_ID, EntityType.getId(type).getPath() + "_spawn_egg"), spawnEgg);
    }

    private static void registerItem(Identifier identifier, Item item) {
        Registry.register(Registries.ITEM, identifier, item);
        SPAWN_EGGS.putIfAbsent(identifier, item);
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
        PolymerEntityUtils.registerType(type);

        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }

    private static void addSpawnEgg(EntityType<? extends MobEntity> type, Item vanillaItem) {
        register(Identifier.of(Main.MOD_ID,
                EntityType.getId(type).getPath() + "_spawn_egg"),
                properties -> new PolymerSpawnEggItem(type, vanillaItem, (Item.Settings) properties));
    }

    static public <T extends Item> void register(Identifier identifier, Function<Item.Settings, T> function) {
        var x = function.apply(new Item.Settings().maxCount(64).registryKey(RegistryKey.of(Registries.ITEM.getKey(), identifier)));
        Registry.register(Registries.ITEM, identifier, x);
        SPAWN_EGGS.putIfAbsent(identifier, x);
    }
    *///?}

    public static void registerMobs() {
        addSpawnEgg(SNAIL, Items.HORSE_SPAWN_EGG);
        addSpawnEgg(TRIVIA_BOT, Items.STRIDER_SPAWN_EGG);
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(Main.MOD_ID, "spawn-eggs"), ITEM_GROUP);
    }

    public static final Object2ObjectOpenHashMap<Identifier, Item> SPAWN_EGGS = new Object2ObjectOpenHashMap<>();
    public static final ItemGroup ITEM_GROUP = new ItemGroup.Builder (null, -1)
            .displayName(Text.literal("Life Series"))
            .icon(Items.BAT_SPAWN_EGG::getDefaultStack)
            .entries((parameters, output) -> SPAWN_EGGS.values().forEach(output::add))
            .build();
}

