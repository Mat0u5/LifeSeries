package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.blacklist;

//? if >= 1.21.2
/*import net.minecraft.server.level.ServerLevel;*/

@Mixin(value = MobEffect.class, priority = 1)
public class StatusEffectMixin {
    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    public void applyInstantEffect(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
    //?} else {
    /*public void applyInstantEffect(ServerLevel world, Entity effectEntity, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        MobEffect effect = (MobEffect) (Object) this;
        if (target instanceof ServerPlayer) {
            if (blacklist.getBannedEffects().contains(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    public void applyInstantEffect(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*public void applyInstantEffect(ServerLevel world, LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        MobEffect effect = (MobEffect) (Object) this;
        if (entity instanceof ServerPlayer) {
            if (blacklist.getBannedEffects().contains(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))) {
                cir.setReturnValue(false);
            }
        }
    }
}
