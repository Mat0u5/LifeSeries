package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.utils.interfaces.IEntity;
import net.mat0u5.lifeseries.utils.interfaces.IEntityDataSaver;
import net.mat0u5.lifeseries.utils.interfaces.IMorph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
//?}
//? if <= 1.21.9 {
/*import net.minecraft.world.entity.monster.Evoker;
*///?} else {
import net.minecraft.world.entity.monster.illager.Evoker;
//?}

@Mixin(value = Entity.class, priority = 1)
public abstract class EntityMixin implements IEntityDataSaver, IMorph, IEntity {
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
    //? if <= 1.20.5 {
    /*@Shadow
    public abstract BlockPos getBlockPosBelowThatAffectsMyMovement();
    public BlockPos ls$getBlockPosBelowThatAffectsMyMovement() {
        return getBlockPosBelowThatAffectsMyMovement();
    }
    *///?} else {
    public BlockPos ls$getBlockPosBelowThatAffectsMyMovement() {
        Entity entity = (Entity) (Object) this;
        return entity.getBlockPosBelowThatAffectsMyMovement();
    }
    //?}



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

    @Inject(method = "getAirSupply", at = @At("RETURN"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife) {
            if (!Snail.SHOULD_DROWN_PLAYER) return;
            if (!WildcardManager.isActiveWildcard(Wildcards.SNAILS)) return;
            Entity entity = (Entity) (Object) this;
            if (entity instanceof Player player && !player.hasEffect(MobEffects.WATER_BREATHING)) {
                if (!Snails.snails.containsKey(player.getUUID())) return;
                Snail snail = Snails.snails.get(player.getUUID());
                if (snail == null) return;
                int snailAir = snail.getAirSupply();
                int initialAir = cir.getReturnValue();
                if (snailAir < initialAir) {
                    cir.setReturnValue(snailAir);
                }
            }
        }
    }

    //? if <= 1.21 {
    /*@Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    public void dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    *///?} else {
    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    public void dropStack(ServerLevel level, ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife) {
            Entity entity = (Entity) (Object) this;
            if (entity instanceof Evoker && stack.is(Items.TOTEM_OF_UNDYING)) {
                cir.setReturnValue(null);
            }
        }
    }


    //? if >= 1.21.2 {
    //? if <= 1.21.6 {
    /*@WrapOperation(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    *///?} else {
    @WrapOperation(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    //?}
    private boolean allowRidingPlayers(EntityType instance, Operation<Boolean> original) {
        if(instance == EntityType.PLAYER) {
            return true;
        } else {
            return original.call(instance);
        }
    }
    //?}

    @Inject(method = "considersEntityAsAlly", at = @At("HEAD"), cancellable = true)
    private void nonAllyPets(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof TamableAnimal animal) {
            //? if <= 1.21.4 {
            /*LivingEntity owner = animal.getOwner();
            *///?} else {
            LivingEntity owner = animal.getRootOwner();
            //?}
            Entity thisEntity = (Entity) (Object) this;
            if (owner != thisEntity) {
                cir.setReturnValue(false);
            }
        }
    }
}