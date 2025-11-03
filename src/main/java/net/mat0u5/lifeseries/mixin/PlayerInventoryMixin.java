package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = Inventory.class, priority = 1)
public abstract class PlayerInventoryMixin {

    @Inject(method = "setChanged", at = @At("TAIL"))
    private void onMarkDirty(CallbackInfo info) {
        ls$onUpdatedInventory();
    }

    @Inject(method = "placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("TAIL"))
    private void onOffer(ItemStack stack, boolean notifiesClient, CallbackInfo info) {
        ls$onUpdatedInventory();
    }

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void onInsertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir != null && cir.getReturnValue()) {
            ls$onUpdatedInventory();
        }
    }

    @Inject(method = "removeFromSelected", at = @At("RETURN"))
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            ls$onUpdatedInventory();
        }
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void onRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (!cir.getReturnValue().isEmpty()) {
            ls$onUpdatedInventory();
        }
    }

    @Inject(method = "setItem", at = @At("TAIL"))
    private void onSetStack(int slot, ItemStack stack, CallbackInfo info) {
        ls$onUpdatedInventory();
    }

    @Unique
    private boolean ls$processing = false;
    @Unique
    private int ls$skippedCalls = 0;

    @Unique
    private void ls$onUpdatedInventory() {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (ls$processing) {
            ls$skippedCalls++;
            return;
        }
        ls$processing = true;
        Inventory inventory = (Inventory) (Object) this;
        Player player = inventory.player;
        try {
            if (player instanceof ServerPlayer serverPlayer) {
                currentSeason.onUpdatedInventory(serverPlayer);
            }
        }
        finally {
            ls$processing = false;
            //if (ls$skippedCalls != 0) OtherUtils.log(player.getNameForScoreboard()+" skipped " + ls$skippedCalls + " inventory updates.");
            ls$skippedCalls = 0;
        }
    }
}
