package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Invisibility extends ToggleableSuperpower {
    public Invisibility(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.INVISIBILITY;
    }

    @Override
    public int deactivateCooldownMillis() {
        return 5000;
    }

    @Override
    public void tick() {
        if (!active) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        MobEffectInstance invis = new MobEffectInstance(MobEffects.INVISIBILITY, 219, 0, false, false, false);
        player.addEffect(invis);
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel playerWorld = PlayerUtils.getServerWorld(player);

        playerWorld.sendParticles(
                ParticleTypes.SMOKE,
                player.getX(), player.getY()+0.9, player.getZ(),
                40, 0.3, 0.5, 0.3, 0
        );

        playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHULKER_SHOOT, SoundSource.MASTER, 1, 1);
        sendInvisibilityPacket();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        PlayerUtils.getServerWorld(player).playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendPlayerInvisible(player.getUUID(), 0);
        player.removeEffect(MobEffects.INVISIBILITY);
    }

    public void sendInvisibilityPacket() {
        if (!this.active) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        NetworkHandlerServer.sendPlayerInvisible(player.getUUID(), -1);
    }

    public void onTakeDamage() {
        deactivate();
    }

    public void onAttack() {
        deactivate();
    }
}
