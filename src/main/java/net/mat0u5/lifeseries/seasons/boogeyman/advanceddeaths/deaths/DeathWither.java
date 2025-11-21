package net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.deaths;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeath;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeaths;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DeathWither extends AdvancedDeath {
    public DeathWither(ServerPlayer player) {
        super(player);
    }

    @Override
    public AdvancedDeaths getDeathType() {
        return AdvancedDeaths.WITHER;
    }

    @Override
    protected int maxTime() {
        return 400;
    }

    @Override
    protected DamageSource damageSource(ServerPlayer player) {
        return player.damageSources().wither();
    }

    @Override
    protected void tick(ServerPlayer player) {
        MobEffectInstance witherEffect = new MobEffectInstance(MobEffects.WITHER, -1, 2, false, false, false);
        player.addEffect(witherEffect);
        if (player.hurtTime == 10 && ticks < 80) {
            player.ls$playNotifySound(SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1, 1);
        }
    }

    @Override
    protected void begin(ServerPlayer player) {
    }

    @Override
    protected void end() {
        if (playerNotFound()) return;
        ServerPlayer player = getPlayer();
        player.removeEffect(MobEffects.WITHER);
    }
}
