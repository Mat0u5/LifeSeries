package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = ServerPlayerGameMode.class, priority = 1)
public class ServerPlayerInteractionManagerMixin {
    @Inject(at = @At("RETURN"), method = "useItemOn")
    private void onInteractBlock(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        currentSeason.onUpdatedInventory(player);
    }

    @Inject(at = @At("RETURN"), method = "useItem")
    private void onInteractItem(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        currentSeason.onUpdatedInventory(player);
    }
}
