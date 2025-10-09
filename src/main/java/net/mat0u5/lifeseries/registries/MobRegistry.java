package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.snail.Snail;
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
                    .trackRangeBlocks(512)
                    .defaultAttributes(Snail::createAttributes)
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

