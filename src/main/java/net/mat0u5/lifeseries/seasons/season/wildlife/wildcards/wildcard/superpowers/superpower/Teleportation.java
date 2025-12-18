package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class Teleportation extends Superpower {
    private Time timer = Time.zero();

    public Teleportation(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.TELEPORTATION;
    }

    @Override
    public int getCooldownMillis() {
        return 5000;
    }

    @Override
    public void tick() {
        timer.tick();
        if (timer.isMultipleOf(Time.minutes(2))) {
            ServerPlayer player = getPlayer();
            if (player != null) {
                int pearls = player.getInventory().countItem(Items.ENDER_PEARL);
                int givePearls = 2;
                if (pearls == 15) givePearls = 1;
                if (pearls >= 16) givePearls = 0;
                if (givePearls > 0) {
                    player.getInventory().add(new ItemStack(Items.ENDER_PEARL, givePearls));
                }
            }
        }
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel playerLevel = player.ls$getServerLevel();
        Vec3 playerPos = player.position();
        boolean teleported = false;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 100);
        if (lookingAt != null)  {
            if (lookingAt instanceof ServerPlayer lookingAtPlayer) {
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
                    ServerLevel lookingAtPlayerLevel = lookingAtPlayer.ls$getServerLevel();
                    Vec3 lookingAtPlayerPos = lookingAtPlayer.position();

                    spawnTeleportParticles(playerLevel, playerPos);
                    spawnTeleportParticles(lookingAtPlayerLevel, lookingAtPlayerPos);

                    ServerLevel storedLevel = playerLevel;
                    Vec3 storedPos = playerPos;
                    float storedYaw = player.getYRot();
                    float storedPitch = player.getXRot();

                    LevelUtils.teleport(player, lookingAtPlayerLevel, lookingAtPlayerPos, lookingAtPlayer.getYRot(), lookingAtPlayer.getXRot());
                    LevelUtils.teleport(lookingAtPlayer, storedLevel, storedPos, storedYaw, storedPitch);

                    playTeleportSound(playerLevel, playerPos);
                    playTeleportSound(lookingAtPlayerLevel, lookingAtPlayerPos);

                    //? if <= 1.21.4 {
                    MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3);
                    //?} else {
                    /*MobEffectInstance resistance = new MobEffectInstance(MobEffects.RESISTANCE, 100, 3);
                    *///?}
                    lookingAtPlayer.addEffect(resistance);

                    teleported = true;
                }
            }
        }

        if (!teleported) {
            Vec3 lookingAtPos = PlayerUtils.getPosLookingAt(player, 100);
            if (lookingAtPos != null) {
                playTeleportSound(playerLevel, playerPos);
                spawnTeleportParticles(playerLevel, playerPos);

                PlayerUtils.teleport(player, lookingAtPos);

                playTeleportSound(playerLevel, playerPos);
                spawnTeleportParticles(playerLevel, playerPos);

                teleported = true;
            }
        }

        if (!teleported) {
            PlayerUtils.displayMessageToPlayer(player, Component.literal("There is nothing to teleport to."), 65);
            return;
        }
        super.activate();
    }

    public void spawnTeleportParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(
                ParticleTypes.PORTAL,
                pos.x(), pos.y()+0.9, pos.z(),
                40, 0.3, 0.5, 0.3, 0
        );
    }
    public void playTeleportSound(ServerLevel level, Vec3 pos) {
        //? if <= 1.20.2 {
        /*level.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 1, 1);
        *///?} else {
        level.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.PLAYER_TELEPORT, SoundSource.MASTER, 1, 1);
        //?}
    }
}
