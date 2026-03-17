package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.EntitySpawnReason;
//? if <= 1.20.3
//import net.minecraft.nbt.CompoundTag;

@Mixin(value = Mob.class, priority = 1)
public abstract class MobMixin {
    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    //? if <= 1.20.3 {
    /*private void initialize(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason spawnReason, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
    *///?} else {
    private void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
    //?}
        if (Main.isClientOrDisabled()) return;
        if (spawnReason == EntitySpawnReason.NATURAL) return;
        if (spawnReason == EntitySpawnReason.CHUNK_GENERATION) return;
        Mob mobEntity = ((Mob) (Object) this);
        mobEntity.addTag("notNatural");
    }
}
