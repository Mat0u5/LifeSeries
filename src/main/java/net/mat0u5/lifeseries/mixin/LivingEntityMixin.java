package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.WindCharge;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static net.mat0u5.lifeseries.Main.blacklist;
import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.2
/*import net.minecraft.world.entity.monster.creaking.Creaking;*/

//? if = 1.21.2 {
/*import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
*///?}

//? if >= 1.21.9 {
/*import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.world.entity.decoration.Mannequin;
*///?}

@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityMixin {
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void onHealHead(float amount, CallbackInfo info) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (!(currentSeason instanceof SecretLife secretLife)) return;
        if (!secretLife.canChangeHealth()) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer) {
            info.cancel();
        }
    }

    @Inject(method = "heal", at = @At("TAIL"))
    private void onHeal(float amount, CallbackInfo info) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer player) {
            if (player.ls$isWatcher()) return;
            currentSeason.onPlayerHeal(player, amount);
        }
    }

    //? if <= 1.21 {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
     //?} else {
    /*@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void hurtServer(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        //? if >= 1.21.2 {
        /*if (entity instanceof Creaking creaking) {
            creaking.hurtTime = 20;
        }
        *///?}

        ItemStack weapon = source.getWeaponItem();
        if (amount <= WindCharge.MAX_MACE_DAMAGE) return;
        if (weapon == null) return;
        if (weapon.isEmpty()) return;
        if (!weapon.is(Items.MACE)) return;
        if (!ItemStackUtils.hasCustomComponentEntry(weapon, "WindChargeSuperpower")) return;
        //? if <= 1.21 {
        cir.setReturnValue(entity.hurt(source, WindCharge.MAX_MACE_DAMAGE));
         //?} else
        /*cir.setReturnValue(entity.hurtServer(world, source, WindCharge.MAX_MACE_DAMAGE));*/
    }

    //? if = 1.21.2 {
    /*@Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    public void isEntityLookingAtMe(LivingEntity entity, double d, boolean bl, boolean visualShape
            , Predicate<LivingEntity> predicate, DoubleSupplier[] entityYChecks, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity me = (LivingEntity) (Object) this;
        if (me instanceof Creaking creaking) {
            if (creaking.isAlliedTo(entity)) cir.setReturnValue(false);
        }
    }
    *///?}

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    public void addStatusEffect(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer) {
            if (!effect.isAmbient() && !effect.showIcon() && !effect.isVisible()) return;
            if (blacklist.getBannedEffects().contains(effect.getEffect())) {
                cir.setReturnValue(false);
            }
        }
    }

    /*
        Superpowers
     */

    @Unique
    private DamageSource ls$lastDamageSource;


    //? if <= 1.21 {
    @Inject(method = "hurt", at = @At("HEAD"))
    private void captureDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.ls$lastDamageSource = source;
    }
    //?} else {
    /*@Inject(method = "hurtServer", at = @At("HEAD"))
    private void captureDamageSource(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.ls$lastDamageSource = source;
    }
    *///?}

    @ModifyArg(
            //? if <= 1.21 {
            method = "hurt",
            //?} else {
            /*method = "hurtServer",
            *///?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"),
            index = 0
    )
    private double modifyKnockback(double strength) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return strength;
        if (ls$lastDamageSource == null) return strength;

        DamageSource source = ls$lastDamageSource;
        if (source.getEntity() instanceof ServerPlayer attacker &&
                source.type() == attacker.damageSources().playerAttack(attacker).type() &&
                SuperpowersWildcard.hasActivatedPower(attacker, Superpowers.SUPER_PUNCH)) {
            return 3;
        }
        return strength;
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void onDrop(ServerLevel world, DamageSource damageSource, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        Events.onEntityDropItems((LivingEntity) (Object) this, damageSource);
    }

    //? if <= 1.21 {
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"))
    //?} else {
    /*@Inject(method = "checkTotemDeathProtection", at = @At("HEAD"))
    *///?}
    private void stopFakeTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (Main.modDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (ItemStackUtils.hasCustomComponentEntry(entity.getMainHandItem(), "FakeTotem")) {
            entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (ItemStackUtils.hasCustomComponentEntry(entity.getOffhandItem(), "FakeTotem")) {
            entity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }
    }


    //? if >= 1.21.9 {
    /*@Inject(method = "tick", at = @At("HEAD"))
    public void tickMannequin(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Mannequin mannequin && mannequin instanceof MannequinAccessor mannequinAccessor && mannequin.tickCount < 0) {
            if (entity.tickCount % 20 == 0) {
                boolean triggered = false;
                ServerPlayer player = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().partialProfile().id());
                if (player != null) {
                    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
                            projection.clone = mannequin;
                            triggered = true;
                        }
                    }
                }
                if (!triggered) {
                    entity.discard();
                }
            }
        }
    }
    @Inject(method = "hurtServer", at = @At("HEAD"))
    public void damageMannequin(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Mannequin mannequin && mannequin instanceof MannequinAccessor mannequinAccessor && mannequin.tickCount < 0) {
            ServerPlayer player = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().partialProfile().id());
            if (player != null) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                    if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
                        projection.onDamageClone(world, source, amount);
                    }
                }
            }
        }
    }
    *///?}
}
