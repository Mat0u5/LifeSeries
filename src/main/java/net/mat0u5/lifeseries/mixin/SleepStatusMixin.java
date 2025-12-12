package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = SleepStatus.class, priority = 1)
public abstract class SleepStatusMixin {
    @Inject(method = "areEnoughDeepSleeping", at = @At("RETURN"), cancellable = true)
    public void canResetTime(int percentage, List<ServerPlayer> players, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
            for (ServerPlayer player : players) {
                if (!player.isSleepingLongEnough()) return;
                if (SuperpowersWildcard.hasActivePower(player, Superpowers.TIME_CONTROL)) {
                    cir.setReturnValue(true);
                }
            }
        }
        if (currentSeason.getSeason() == Seasons.NICE_LIFE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "areEnoughSleeping", at = @At("RETURN"), cancellable = true)
    public void canSkipNight(int percentage, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) return;
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (!player.isSleeping()) return;
            if (SuperpowersWildcard.hasActivePower(player, Superpowers.TIME_CONTROL)) {
                cir.setReturnValue(true);
            }
        }
    }
}
