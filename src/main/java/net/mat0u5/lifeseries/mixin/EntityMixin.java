package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.snails.Snails;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = Entity.class, priority = 1)
public abstract class EntityMixin {
    @Inject(method = "getAir", at = @At("RETURN"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if (Main.isLogicalSide()) {
            if (currentSeries instanceof WildLife) {
                if (Snail.SHOULD_DROWN_PLAYER) {
                    if (WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
                        Entity entity = (Entity) (Object) this;
                        if (entity instanceof PlayerEntity player && !player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                            if (!Snails.snails.containsKey(player.getUuid())) return;
                            Snail snail = Snails.snails.get(player.getUuid());
                            if (snail == null) return;
                            int snailAir = snail.getAir();
                            int initialAir = cir.getReturnValue();
                            if (snailAir < initialAir) {
                                cir.setReturnValue(snailAir);
                            }
                        }
                    }
                }
            }
        }
        else if (MainClient.snailAir < 300) {
            Entity entity = (Entity) (Object) this;
            if (entity instanceof PlayerEntity player && !player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                int initialAir = cir.getReturnValue();
                if (MainClient.snailAir < initialAir) {
                    cir.setReturnValue(MainClient.snailAir);
                }
            }
        }
    }

    //? if <= 1.21 {
    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    public void dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    //?} else {
        /*@Inject(method = "dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
                at = @At("HEAD"), cancellable = true)
        public void dropStack(ServerWorld world, ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    *///?}
        if (!Main.isLogicalSide()) return;
        if (currentSeries instanceof WildLife) {
            Entity entity = (Entity) (Object) this;
            if (entity instanceof EvokerEntity && stack.isOf(Items.TOTEM_OF_UNDYING)) {
                cir.setReturnValue(null);
            }
        }
    }


    //? if >= 1.21.2 {
    /*@WrapOperation(
            method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z")
    )
    private boolean allowRidingPlayers(EntityType instance, Operation<Boolean> original) {
        if( instance == EntityType.PLAYER) {
            return true;
        } else {
            return original.call(instance);
        }
    }
    *///?}
}