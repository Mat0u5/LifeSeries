package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.interfaces.IEntityDataSaver;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
*///?}

@Mixin(value = Entity.class, priority = 1)
public abstract class EntityMixin implements IEntityDataSaver, IMorph {
    /*
    private NbtCompound persistentData;
    @Override
    public NbtCompound getPersistentData() {
        if (persistentData == null) {
            persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (persistentData != null) {
            nbt.put("lifeseries", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("lifeseries")) {
            persistentData = nbt.getCompound("lifeseries");
        }
    }
    */

    @Unique
    private boolean ls$fromMorph = false;

    @Unique
    @Override
    public void setFromMorph(boolean fromMorph) {
        this.ls$fromMorph = fromMorph;
    }

    @Unique
    @Override
    public boolean isFromMorph() {
        return ls$fromMorph;
    }

    @Inject(method = "getAir", at = @At("RETURN"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife) {
            if (!Snail.SHOULD_DROWN_PLAYER) return;
            if (!WildcardManager.isActiveWildcard(Wildcards.SNAILS)) return;
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

    //? if <= 1.21 {
    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    public void dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    //?} else {
        /*@Inject(method = "dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
                at = @At("HEAD"), cancellable = true)
        public void dropStack(ServerWorld world, ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife) {
            Entity entity = (Entity) (Object) this;
            if (entity instanceof EvokerEntity && stack.isOf(Items.TOTEM_OF_UNDYING)) {
                cir.setReturnValue(null);
            }
        }
    }


    //? if >= 1.21.2 {
    /*//? if <= 1.21.6 {
    @WrapOperation(
            method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z")
    )
    //?} else {
    /^@WrapOperation(
            method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z")
    )
    ^///?}
    private boolean allowRidingPlayers(EntityType instance, Operation<Boolean> original) {
        if(instance == EntityType.PLAYER) {
            return true;
        } else {
            return original.call(instance);
        }
    }
    *///?}
}