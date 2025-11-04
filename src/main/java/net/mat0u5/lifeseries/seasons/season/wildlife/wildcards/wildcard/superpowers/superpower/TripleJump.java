package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class TripleJump extends ToggleableSuperpower {
    public boolean isInAir = false;
    private int onGroundTicks = 0;

    public TripleJump(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.TRIPLE_JUMP;
    }

    @Override
    public void tick() {
        ServerPlayer player = getPlayer();
        if (!active || player == null) {
            onGroundTicks = 0;
            return;
        }

        if (!player.onGround()) {
            //? if <= 1.21.4 {
            MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP, 219, 2, false, false, false);
            //?} else {
            /*MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP_BOOST, 219, 2, false, false, false);
            *///?}
            player.addEffect(jump);
            onGroundTicks = 0;
        }
        else {
            //? if <= 1.21.4 {
            player.removeEffect(MobEffects.JUMP);
            //?} else {
            /*player.removeEffect(MobEffects.JUMP_BOOST);
            *///?}
            onGroundTicks++;
        }

        if (!isInAir) {
            onGroundTicks = 0;
            return;
        }

        if (onGroundTicks >= 10) {
            isInAir = false;
            onGroundTicks = 0;
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.playNotifySound(SoundEvents.SLIME_JUMP, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, -1);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        //? if <= 1.21.4 {
        player.removeEffect(MobEffects.JUMP);
        //?} else {
        /*player.removeEffect(MobEffects.JUMP_BOOST);
        *///?}
        player.playNotifySound(SoundEvents.SLIME_SQUISH, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, 0);
    }
}
