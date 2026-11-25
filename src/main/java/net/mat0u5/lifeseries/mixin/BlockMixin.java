package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.MoreDropsAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

//? if >= 26.1
import net.minecraft.world.item.ItemInstance;

@Mixin(value = Block.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class BlockMixin {
	//? if <= 1.21.11 {
	/*@Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("HEAD"))
	private static void startMiningDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfoReturnable<List<ItemStack>> cir) {
	*///?} else {
	@Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", at = @At("HEAD"))
	private static void startMiningDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemInstance tool, CallbackInfoReturnable<List<ItemStack>> cir) {
	//?}
		MoreDropsAbility.capturedEntity = entity;
	}

	//? if <= 1.21.11 {
	/*@Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"))
	private static void endMiningDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, CallbackInfoReturnable<List<ItemStack>> cir) {
	*///?} else {
	@Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", at = @At("RETURN"))
	private static void endMiningDrops(BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemInstance tool, CallbackInfoReturnable<List<ItemStack>> cir) {
	//?}
		MoreDropsAbility.capturedEntity = null;
	}
}
