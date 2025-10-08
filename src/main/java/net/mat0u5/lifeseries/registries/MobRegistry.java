package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.mat0u5.lifeseries.entity.newsnail.NewSnail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

//? if <= 1.21 {
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.registry.RegistryKey;
//?}
//? if >= 1.21.2 {
/*import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.registry.RegistryKey;
*///?}

public class MobRegistry {
    //? if <= 1.21 {
    public static final EntityType<NewSnail> NEW_SNAIL = register(
            NewSnail.ID,
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(NewSnail::new)
                    .spawnGroup(SpawnGroup.MONSTER)
                    .dimensions(EntityDimensions.changing(0.5f, 0.6f))
                    .trackRangeBlocks(512)
                    .defaultAttributes(NewSnail::createAttributes)
    );

    private static <T extends Entity> EntityType<T> register(Identifier id, FabricEntityTypeBuilder<T> builder) {
        EntityType<T> type = builder.build();
        return Registry.register(Registries.ENTITY_TYPE, id, type);
    }
    //?} else {
    /*public static final EntityType<NewSnail> NEW_SNAIL = register(
            NewSnail.ID,
            FabricEntityType.Builder.createMob(NewSnail::new, SpawnGroup.MONSTER, x -> x
                            .defaultAttributes(NewSnail::createAttributes))
                    .dimensions(0.5f, 0.6f)
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

