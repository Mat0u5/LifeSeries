package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if <= 1.21
//import net.minecraft.world.entity.MobSpawnType;
//? if >= 1.21.2
import net.minecraft.world.entity.EntitySpawnReason;

@Mixin(value = Mob.class, priority = 1)
public abstract class MobMixin {
    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    //? if <= 1.21 {

    /*//? if <= 1.20.3 {
    /^private void initialize(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnReason, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
    ^///?} else {
    private void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
    //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (spawnReason == MobSpawnType.NATURAL) return;
        if (spawnReason == MobSpawnType.CHUNK_GENERATION) return;
    *///?} else {
        private void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
            if (!Main.isLogicalSide() || Main.modDisabled()) return;
            if (spawnReason == EntitySpawnReason.NATURAL) return;
            if (spawnReason == EntitySpawnReason.CHUNK_GENERATION) return;
    //?}
        Mob mobEntity = ((Mob) (Object) this);
        mobEntity.addTag("notNatural");
    }
}
