package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.mat0u5.lifeseries.mixin.DefaultAttributesAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

//? if >= 1.21.2 {
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//?}
//? if <= 26.1 {
/*import net.minecraft.world.level.block.Blocks;
*///?} else {
import net.minecraft.tags.BlockTags;
//?}

public class MobRegistry {
    //? if >= 1.21.2 {
    public static final ResourceKey<EntityType<?>> SNAIL_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Snail.ID
    );
    public static final ResourceKey<EntityType<?>> TRIVIA_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            TriviaBot.ID
    );
    public static final ResourceKey<EntityType<?>> SNOWMAN_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            AngrySnowman.ID
    );
    //?}
    public static final EntityType<Snail> SNAIL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Snail.ID,
            EntityType.Builder.of(Snail::new, MobCategory.MONSTER)
                    .sized(0.5f, 0.6f)
                    .clientTrackingRange(512)
                    //? if <= 1.21 {
                    /*.build(Snail.ID.toString())
                    *///?} else {
                    .build(SNAIL_KEY)
                    //?}
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TriviaBot.ID,
            EntityType.Builder.of(TriviaBot::new, MobCategory.AMBIENT)
                    .sized(0.65f, 1.8f)
                    .clientTrackingRange(512)
                    //? if <= 1.21 {
                    /*.build(TriviaBot.ID.toString())
                    *///?} else {
                    .build(TRIVIA_KEY)
                     //?}
    );
    public static final EntityType<AngrySnowman> ANGRY_SNOWMAN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            AngrySnowman.ID,
            EntityType.Builder.of(AngrySnowman::new, MobCategory.MISC)
                    //? if <= 26.1 {
                    /*.immuneTo(Blocks.POWDER_SNOW)
                    *///?} else {
                    .immuneTo(BlockTags.POLAR_BEAR_IMMUNE_TO)
                    //?}

                    .sized(0.7F, 1.9F)

                    //? if >= 1.20.5 {
                    .eyeHeight(1.7F)
                    //?}
                    .clientTrackingRange(8)
                    //? if <= 1.21 {
                    /*.build(AngrySnowman.ID.toString())
                    *///?} else {
                    .build(SNOWMAN_KEY)
                     //?}
    );

    public static void registerMobs() {

    }

    public static void registerAttributes() {
        register(SNAIL, Snail.createAttributes().build());
        register(TRIVIA_BOT, TriviaBot.createAttributes().build());
        register(ANGRY_SNOWMAN, AngrySnowman.createAttributes().build());
    }

    public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
        if (DefaultAttributesAccessor.getRegistry().put(type, container) != null) {
            LifeSeries.LOGGER.debug("Overriding existing registration for entity type {}", BuiltInRegistries.ENTITY_TYPE.getKey(type));
        }
    }
}

