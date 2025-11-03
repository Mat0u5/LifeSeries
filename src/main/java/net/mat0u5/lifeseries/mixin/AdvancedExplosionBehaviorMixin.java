package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = SimpleExplosionDamageCalculator.class, priority = 1)
public class AdvancedExplosionBehaviorMixin {
    @Inject(method = "getKnockbackMultiplier", at = @At("RETURN"), cancellable = true)
    public void getKnockbackModifier(Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (entity instanceof ServerPlayer player) {
            if (currentSeason.getSeason() != Seasons.WILD_LIFE) return;
            if (player.getAbilities().flying) return;
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
                cir.setReturnValue(3f); // Default is 1.22f
            }
        }
    }
}
