package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, priority = 1)
public abstract class MobEntityMixin {
    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void initialize(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (spawnReason == MobSpawnType.NATURAL) return;
        if (spawnReason == MobSpawnType.CHUNK_GENERATION) return;
        Mob mobEntity = ((Mob) (Object) this);
        mobEntity.addTag("notNatural");
    }
}
