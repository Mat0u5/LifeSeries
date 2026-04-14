package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.events.Events;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

@Mixin(value = ServerPlayerGameMode.class, priority = 1)
public class ServerPlayerGameModeMixin {
    @Inject(at = @At("RETURN"), method = "useItemOn")
    private void onInteractBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (LifeSeries.isClientOrDisabled()) return;
        currentSeason.onUpdatedInventory(player);
    }

    @Inject(at = @At("RETURN"), method = "useItem")
    private void onInteractItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (LifeSeries.isClientOrDisabled()) return;
        currentSeason.onUpdatedInventory(player);
    }

    @Final
    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    @Inject(at = @At("HEAD"), method = "handleBlockBreakAction", cancellable = true)
    public void startBlockBreak(BlockPos pos, ServerboundPlayerActionPacket.Action playerAction, Direction direction, int worldHeight, int i, CallbackInfo info) {
        if (playerAction != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) return;
        InteractionResult result = Events.onBlockAttack(player, level, InteractionHand.MAIN_HAND, pos, direction);

        if (result != InteractionResult.PASS) {
            // The client might have broken the block on its side, so make sure to let it know.
            this.player.connection.send(new ClientboundBlockUpdatePacket(level, pos));

            if (level.getBlockState(pos).hasBlockEntity()) {
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity != null) {
                    Packet<ClientGamePacketListener> updatePacket = blockEntity.getUpdatePacket();

                    if (updatePacket != null) {
                        this.player.connection.send(updatePacket);
                    }
                }
            }

            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "useItemOn", cancellable = true)
    public void interactBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> info) {
        InteractionResult result = Events.onBlockUse(player, level, hand, blockHitResult);

        if (result != InteractionResult.PASS) {
            info.setReturnValue(result);
            info.cancel();
            return;
        }
    }
    //? if >= 1.21.2 {
    @Inject(at = @At("HEAD"), method = "useItem", cancellable = true)
    public void interactItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        InteractionResult result = Events.onItemUse(player, level, hand);

        if (result != InteractionResult.PASS) {
            info.setReturnValue(result);
            info.cancel();
            return;
        }
    }
    //?}
}
