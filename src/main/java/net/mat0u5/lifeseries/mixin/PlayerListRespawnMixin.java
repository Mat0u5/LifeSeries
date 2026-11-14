package net.mat0u5.lifeseries.mixin;
//? if <= 1.20.5 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerList.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class PlayerListRespawnMixin {
    @Unique @Nullable private static InBetweenInstance ls$inBetween;
    @Unique @Nullable private static ServerLevel ls$originalRespawnLevel;
    @Unique private static boolean ls$movedToSpawn;

    @Inject(method = "respawn", at = @At("HEAD"))
    private void prepareInBetween(ServerPlayer player, boolean keepEverything, CallbackInfoReturnable<ServerPlayer> cir) {
        ls$inBetween = keepEverything ? null : InBetweenManager.prepareInBetweenInstance(player);
        ls$originalRespawnLevel = null;
        ls$movedToSpawn = false;
    }

    @ModifyVariable(method = "respawn", at = @At("STORE"), ordinal = 1)
    private ServerLevel changeRespawnLevel(ServerLevel level) {
        ls$originalRespawnLevel = level;
        return ls$inBetween != null ? ls$inBetween.level : level;
    }

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState readRespawnBlockInOriginalLevel(ServerLevel level, BlockPos pos, Operation<BlockState> original) {
        if (ls$inBetween != null && ls$originalRespawnLevel != null) level = ls$originalRespawnLevel;
        return original.call(level, pos);
    }

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"))
    private void keepRespawnDimension(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos, float angle, boolean forced, boolean sendMessage, Operation<Void> original) {
        if (ls$inBetween != null && ls$originalRespawnLevel != null) dimension = ls$originalRespawnLevel.dimension();
        original.call(player, dimension, pos, angle, forced, sendMessage);
    }

    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;noCollision(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean respawnInInBetween(ServerLevel level, Entity entity, Operation<Boolean> original) {
        if (ls$inBetween != null && !ls$movedToSpawn) {
            ls$movedToSpawn = true;
            Vec3 spawn = ls$inBetween.getSpawnPos();
            entity.moveTo(spawn.x, spawn.y, spawn.z, ls$inBetween.getSpawnYaw(), 0);
        }
        return original.call(level, entity);
    }

    @Inject(method = "respawn", at = @At("RETURN"))
    private void finishRespawn(ServerPlayer player, boolean keepEverything, CallbackInfoReturnable<ServerPlayer> cir) {
        ls$inBetween = null;
        ls$originalRespawnLevel = null;
    }
}
*///?} else {

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if <= 1.21 {
//import net.minecraft.world.level.portal.DimensionTransition;
//?} else {
import net.minecraft.world.level.portal.TeleportTransition;
//?}

@Mixin(value = PlayerList.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class PlayerListRespawnMixin {
    //? if <= 1.21 {
    /*@WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)Lnet/minecraft/world/level/portal/DimensionTransition;"))
    private DimensionTransition changeRespawn(ServerPlayer player, boolean keepInventory, DimensionTransition.PostDimensionTransition postTeleportTransition, Operation<DimensionTransition> original) {
        DimensionTransition originalReturn = original.call(player, keepInventory, postTeleportTransition);
        if (keepInventory) return originalReturn; // Not a death
        InBetweenInstance inBetween = InBetweenManager.prepareInBetweenInstance(player);
        if (inBetween == null) return originalReturn;
        return new DimensionTransition(inBetween.level, inBetween.getSpawnPos(), Vec3.ZERO, inBetween.getSpawnYaw(), 0, originalReturn.missingRespawnBlock(), originalReturn.postDimensionTransition());
    }
    *///?} else {
    @WrapOperation(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnPositionAndUseSpawnBlock(ZLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)Lnet/minecraft/world/level/portal/TeleportTransition;"))
    private TeleportTransition changeRespawn(ServerPlayer player, boolean consumeSpawnBlock, TeleportTransition.PostTeleportTransition postTeleportTransition, Operation<TeleportTransition> original) {
        TeleportTransition originalReturn = original.call(player, consumeSpawnBlock, postTeleportTransition);
        if (!consumeSpawnBlock) return originalReturn; // Not a death
        InBetweenInstance inBetween = InBetweenManager.prepareInBetweenInstance(player);
        if (inBetween == null) return originalReturn;
        return new TeleportTransition(inBetween.level, inBetween.getSpawnPos(), Vec3.ZERO, inBetween.getSpawnYaw(), 0, originalReturn.missingRespawnBlock(), originalReturn.asPassenger(), originalReturn.relatives(), originalReturn.postTeleportTransition());
    }
    //?}
}
//?}
