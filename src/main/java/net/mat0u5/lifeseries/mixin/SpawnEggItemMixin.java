package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.seasonConfig;

@Mixin(value = SpawnEggItem.class, priority = 1)
public abstract class SpawnEggItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void preventSpawnerModification(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (context.getPlayer() instanceof ServerPlayer) {
            if (seasonConfig.SPAWN_EGG_ALLOW_ON_SPAWNER.get()) return;
            Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
            if (block != Blocks.SPAWNER) return;
            if (context.getPlayer() == null) return;
            if (context.getPlayer().isCreative() && seasonConfig.CREATIVE_IGNORE_BLACKLIST.get()) return;
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
