package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//? if <= 26.3 {
/*import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
@Mixin(value = Snowball.class, priority = 1)
*///?} else {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableBallProjectile;
@Mixin(value = ThrowableBallProjectile.class, priority = 1)
//?}
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class SnowballMixin {
    //? if <= 1.21 {
    /*@Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean angrySnowmanDamage(Entity instance, DamageSource damageSource, float f) {
        Snowball snowball = (Snowball) (Object) this;
        if (snowball.getOwner() instanceof AngrySnowman) {
            f = 0.01f;
        }
        return instance.hurt(damageSource, f);
    }
    *///?} else if <= 26.3 {
    /*@Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void angrySnowmanDamage(Entity instance, DamageSource damageSource, float f) {
        Snowball snowball = (Snowball) (Object) this;
        if (snowball.getOwner() instanceof AngrySnowman) {
            f = 0.01f;
        }
        instance.hurt(damageSource, f);
    }
    *///?} else {
    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean angrySnowmanDamage(Entity instance, ServerLevel serverLevel, DamageSource damageSource, float f) {
        ThrowableBallProjectile projectile = (ThrowableBallProjectile) (Object) this;
        if (projectile.getOwner() instanceof AngrySnowman) {
            f = 0.01f;
        }
        return instance.hurtServer(serverLevel, damageSource, f);
    }
    //?}
}
