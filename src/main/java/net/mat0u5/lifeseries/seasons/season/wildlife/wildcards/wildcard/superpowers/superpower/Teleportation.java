package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Set;

public class Teleportation extends Superpower {
    private long ticks = 0;

    public Teleportation(ServerPlayerEntity player) {
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
            ServerPlayerEntity player = getPlayer();
            if (player != null) {
                int pearls = player.getInventory().count(Items.ENDER_PEARL);
                int givePearls = 2;
                if (pearls == 15) givePearls = 1;
                if (pearls >= 16) givePearls = 0;
                if (givePearls > 0) {
                    player.getInventory().insertStack(new ItemStack(Items.ENDER_PEARL, givePearls));
                }
            }
        }
    }

    @Override
    public void activate() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        Vec3d playerPos = player.ls$getEntityPos();
        boolean teleported = false;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 100);
        if (lookingAt != null)  {
            if (lookingAt instanceof ServerPlayerEntity lookingAtPlayer) {
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
                    ServerWorld lookingAtPlayerWorld = PlayerUtils.getServerWorld(lookingAtPlayer);
                    Vec3d lookingAtPlayerPos = lookingAtPlayer.ls$getEntityPos();

                    spawnTeleportParticles(playerWorld, playerPos);
                    spawnTeleportParticles(lookingAtPlayerWorld, lookingAtPlayerPos);

                    Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                    ServerWorld storedWorld = playerWorld;
                    Vec3d storedPos = playerPos;
                    float storedYaw = player.getYaw();
                    float storedPitch = player.getPitch();

                    PlayerUtils.teleport(player, lookingAtPlayerWorld, lookingAtPlayerPos, lookingAtPlayer.getYaw(), lookingAtPlayer.getPitch());
                    PlayerUtils.teleport(lookingAtPlayer, storedWorld, storedPos, storedYaw, storedPitch);

                    playTeleportSound(playerWorld, playerPos);
                    playTeleportSound(lookingAtPlayerWorld, lookingAtPlayerPos);

                    StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 3);
                    lookingAtPlayer.addStatusEffect(resistance);

                    teleported = true;
                }
            }
        }

        if (!teleported) {
            Vec3d lookingAtPos = PlayerUtils.getPosLookingAt(player, 100);
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
            PlayerUtils.displayMessageToPlayer(player, Text.literal("There is nothing to teleport to."), 65);
            return;
        }
        super.activate();
    }

    public void spawnTeleportParticles(ServerWorld world, Vec3d pos) {
        world.spawnParticles(
                ParticleTypes.PORTAL,
                pos.getX(), pos.getY()+0.9, pos.getZ(),
                40, 0.3, 0.5, 0.3, 0
        );
    }
    public void playTeleportSound(ServerWorld world, Vec3d pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.MASTER, 1, 1);
    }
}
