package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class ShadowPlay extends Superpower {
    public ShadowPlay(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.SHADOW_PLAY;
    }

    @Override
    public int getCooldownMillis() {
        return 30000;
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel playerLevel = player.ls$getServerLevel();
        List<ServerPlayer> affectedPlayers = playerLevel.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(10), playerEntity -> playerEntity.distanceTo(player) <= 10);
        MobEffectInstance blindness = new MobEffectInstance(MobEffects.BLINDNESS, 100, 0);
        MobEffectInstance invis = new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false, false);
        affectedPlayers.remove(player);
        for (ServerPlayer affectedPlayer : affectedPlayers) {
            affectedPlayer.addEffect(blindness);
            affectedPlayer.ls$getServerLevel().sendParticles(
                    ParticleTypes.SMOKE,
                    affectedPlayer.getX(), affectedPlayer.getY()+0.9, affectedPlayer.getZ(),
                    40, 0.3, 0.5, 0.3, 0
            );
        }
        player.addEffect(invis);
        playerLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHULKER_SHOOT, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendPlayerInvisible(player.getUUID(), System.currentTimeMillis()+3000);
    }
}
