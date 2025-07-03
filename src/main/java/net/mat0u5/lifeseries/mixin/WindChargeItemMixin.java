package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WindChargeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = WindChargeItem.class, priority = 1)
public class WindChargeItemMixin {
    @Inject(method = "use", at = @At("RETURN"))
    //? if <= 1.21 {
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    //?} else
    /*public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {*/
        if (!Main.isLogicalSide()) return;
        if (user instanceof ServerPlayerEntity player) {
            if (currentSeries.getSeries() == SeriesList.WILD_LIFE) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
                    TaskScheduler.scheduleTask(1, () -> {
                        player.getInventory().insertStack(Items.WIND_CHARGE.getDefaultStack());
                        player.getInventory().markDirty();
                        PlayerUtils.updatePlayerInventory(player);
                    });
                }
            }
        }
    }
}
