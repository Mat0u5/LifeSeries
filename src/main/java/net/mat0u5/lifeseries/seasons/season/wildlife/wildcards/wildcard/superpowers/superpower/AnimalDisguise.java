package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.List;


public class AnimalDisguise extends ToggleableSuperpower {

    public static boolean SHOW_ARMOR = false;
    public static boolean SHOW_HANDS = true;

    public AnimalDisguise(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.ANIMAL_DISGUISE;
    }
    List<EntityType<?>> defaultRandom = List.of(EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG);
    List<EntityType<?>> bannedEntities = List.of(
            //TODO
            //MobRegistry.PATH_FINDER, MobRegistry.TRIVIA_BOT, MobRegistry.SNAIL,
            EntityType.PLAYER, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.ARMOR_STAND,
            EntityType.AXOLOTL, EntityType.DOLPHIN
    );

    @Override
    public void activate() {
        super.activate();

        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        EntityType<?> morph = null;
        if (lookingAt != null)  {
            if (lookingAt instanceof LivingEntity livingEntity &&
                    !(lookingAt instanceof PlayerEntity)) {
                if (!bannedEntities.contains(lookingAt.getType())) {
                    morph = lookingAt.getType();
                }
            }
        }
        if (morph == null) {
            morph = defaultRandom.get(player.getRandom().nextInt(defaultRandom.size()));
        }
        PlayerUtils.getServerWorld(player).playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, SoundCategory.MASTER, 1, 1);

        EntityType<?> finalMorph = morph;

        MorphManager.setMorph(player, finalMorph);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        PlayerUtils.getServerWorld(player).playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.MASTER, 1, 1);

        MorphManager.resetMorph(player);
    }

    public void onTakeDamage() {
        deactivate();
    }
}
