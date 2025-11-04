package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
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

import java.util.EnumSet;
import java.util.Set;

public class Teleportation extends Superpower {
    private long ticks = 0;

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
        ticks++;
        if (ticks % 2400 == 0) {
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
        ServerLevel playerWorld = PlayerUtils.getServerWorld(player);
        Vec3 playerPos = player.ls$getEntityPos();
        boolean teleported = false;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 100);
        if (lookingAt != null)  {
            if (lookingAt instanceof ServerPlayer lookingAtPlayer) {
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
                    ServerLevel lookingAtPlayerWorld = PlayerUtils.getServerWorld(lookingAtPlayer);
                    Vec3 lookingAtPlayerPos = lookingAtPlayer.ls$getEntityPos();

                    spawnTeleportParticles(playerWorld, playerPos);
                    spawnTeleportParticles(lookingAtPlayerWorld, lookingAtPlayerPos);

                    ServerLevel storedWorld = playerWorld;
                    Vec3 storedPos = playerPos;
                    float storedYaw = player.getYRot();
                    float storedPitch = player.getXRot();

                    PlayerUtils.teleport(player, lookingAtPlayerWorld, lookingAtPlayerPos, lookingAtPlayer.getYRot(), lookingAtPlayer.getXRot());
                    PlayerUtils.teleport(lookingAtPlayer, storedWorld, storedPos, storedYaw, storedPitch);

                    playTeleportSound(playerWorld, playerPos);
                    playTeleportSound(lookingAtPlayerWorld, lookingAtPlayerPos);

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
                playTeleportSound(playerWorld, playerPos);
                spawnTeleportParticles(playerWorld, playerPos);

                PlayerUtils.teleport(player, lookingAtPos);

                playTeleportSound(playerWorld, playerPos);
                spawnTeleportParticles(playerWorld, playerPos);

                teleported = true;
            }
        }

        if (!teleported) {
            PlayerUtils.displayMessageToPlayer(player, Component.literal("There is nothing to teleport to."), 65);
            return;
        }
        super.activate();
    }

    public void spawnTeleportParticles(ServerLevel world, Vec3 pos) {
        world.sendParticles(
                ParticleTypes.PORTAL,
                pos.x(), pos.y()+0.9, pos.z(),
                40, 0.3, 0.5, 0.3, 0
        );
    }
    public void playTeleportSound(ServerLevel world, Vec3 pos) {
        world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.PLAYER_TELEPORT, SoundSource.MASTER, 1, 1);
    }
}
