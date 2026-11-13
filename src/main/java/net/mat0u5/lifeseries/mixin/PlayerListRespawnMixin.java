package net.mat0u5.lifeseries.mixin;
//? if <= 1.20.5 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PlayerList.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class PlayerListRespawnMixin {
    //Empty class to avoid mixin errors
    //TODO 1.20 in between respawn support.
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
