package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class TripleJump extends ToggleableSuperpower {
    public static int COOLDOWN_MILLIS = 1000;
    public boolean isInAir = false;
    private Time onGround = Time.zero();

    public TripleJump(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.TRIPLE_JUMP;
    }

    @Override
    public int deactivateCooldownMillis() {
        return COOLDOWN_MILLIS;
    }

    @Override
    public void tick() {
        ServerPlayer player = getPlayer();
        if (!active || player == null) {
            onGround = Time.zero();
            return;
        }

        if (!player.onGround()) {
            MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP_BOOST, 219, 2, false, false, false);
            player.addEffect(jump);
            onGround = Time.zero();
        }
        else {
            player.removeEffect(MobEffects.JUMP_BOOST);
            onGround.tick();
        }

        if (!isInAir) {
            onGround = Time.zero();
            return;
        }

        if (onGround.isLarger(Time.ticks(10))) {
            isInAir = false;
            onGround = Time.zero();
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.ls$playNotifySound(SoundEvents.SLIME_JUMP, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, -1);
        SimplePackets.TRIPLE_JUMP.target(player).sendToClient(true);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.removeEffect(MobEffects.JUMP_BOOST);
        player.ls$playNotifySound(SoundEvents.SLIME_SQUISH, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, 0);
        SimplePackets.TRIPLE_JUMP.target(player).sendToClient(false);
    }
}
