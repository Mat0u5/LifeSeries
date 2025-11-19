package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//? if <= 1.21.9 {
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
//?} else {
/*import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
*///?}

@Mixin(value = ThrownEnderpearl.class, priority = 1)
public class ThrownEnderpearlMixin {

    //? if <= 1.21 {
    @ModifyArg(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 1
    )
    //?} else {
    /*@ModifyArg(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 2
    )
    *///?}
        private float onTargetDamaged(float amount) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return amount;
        ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;
        if (!(pearl.getOwner() instanceof ServerPlayer owner)) return amount;
        if (!SuperpowersWildcard.hasActivePower(owner, Superpowers.TELEPORTATION)) return amount;
        return 0;
    }
}
