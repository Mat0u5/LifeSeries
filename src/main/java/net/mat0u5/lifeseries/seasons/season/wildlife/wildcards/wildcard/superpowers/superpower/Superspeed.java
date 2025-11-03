package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static net.mat0u5.lifeseries.Main.server;

public class Superspeed extends ToggleableSuperpower {

    public static boolean STEP_UP = false;

    public Superspeed(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.SUPERSPEED;
    }

    @Override
    public void tick() {
        if (!active) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        MobEffectInstance hunger = new MobEffectInstance(MobEffects.HUNGER, 219, 4, false, false, false);
        player.addEffect(hunger);
        player.getFoodData().setSaturation(0);
        if (player.getFoodData().getFoodLevel() <= 6) {
            deactivate();
        }
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        if (player.getFoodData().getFoodLevel() <= 6) {
            //? if <= 1.21 {
            player.playNotifySound(SoundEvents.GENERIC_EAT, SoundSource.MASTER, 1, 1);
            //?} else {
            /*player.playNotifySound(SoundEvents.GENERIC_EAT.value(), SoundSource.MASTER, 1, 1);
            *///?}
            return;
        }
        player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 1, 1);
        slowlySetSpeed(player, 0.35, 60);
        NetworkHandlerServer.sendVignette(player, -1);
        if (STEP_UP) {
            AttributeUtils.setStepHeight(player, 1);
        }
        super.activate();
    }

    @Override
    public int activateCooldownMillis() {
        return 3050;
    }

    @Override
    public void deactivate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.playNotifySound(SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 1, 1);
        slowlySetSpeed(player, AttributeUtils.DEFAULT_PLAYER_MOVEMENT_SPEED, 30);
        if (!WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            player.removeEffect(MobEffects.HUNGER);
            MobEffectInstance hunger = new MobEffectInstance(MobEffects.HUNGER, 30, 4, false, false, false);
            player.addEffect(hunger);
        }
        NetworkHandlerServer.sendVignette(player, 0);
        AttributeUtils.resetStepHeight(player);
        super.deactivate();
    }

    public static void slowlySetSpeed(ServerPlayer player, double speed, int ticks) {
        if (server == null) return;
        double currentSpeed = AttributeUtils.getMovementSpeed(player);
        double step = (speed - currentSpeed) / ticks;
        for (int i = 0; i < ticks; i++) {
            int finalI = i;
            TaskScheduler.scheduleTask(i, () -> AttributeUtils.setMovementSpeed(player, currentSpeed + (step * finalI)));
        }
        TaskScheduler.scheduleTask(ticks+1, () -> AttributeUtils.setMovementSpeed(player, speed));
    }
}
