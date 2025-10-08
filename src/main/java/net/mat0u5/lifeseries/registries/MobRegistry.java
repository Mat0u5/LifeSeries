package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mat0u5.lifeseries.entity.newsnail.NewSnail;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MobRegistry {
    //?if < 1.21.4 {
    public static EntityType<NewSnail> NEW_SNAIL = Registry.register(
            Registries.ENTITY_TYPE
            , NewSnail.ID
            , EntityType.Builder.create(NewSnail::new, SpawnGroup.MONSTER)
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(32*16)
                    .build()
    );

    public static void registerMobs() {
        FabricDefaultAttributeRegistry.register(NEW_SNAIL, NewSnail.createAttributes());
    }
    //?} else {
    /*public static final RegistryKey<Registry<EntityType<?>>> ENTITIES_KEY =
            RegistryKey.ofRegistry(Identifier.of("lifeseries", "entities"));

    public static final Registry<EntityType<?>> ENTITIES_REGISTRY =
            FabricRegistryBuilder.createSimple(ENTITIES_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final EntityType<NewSnail> NEW_SNAIL = Registry.register(
            ENTITIES_REGISTRY
            , NewSnail.ID
            , EntityType.Builder.create(NewSnail::new, SpawnGroup.MONSTER)
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(32*16)
                    .build()
    );

    public static void registerMobs() {
        FabricDefaultAttributeRegistry.register(NEW_SNAIL, NewSnail.createAttributes());
    }
    *///?}
}

