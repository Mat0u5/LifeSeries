package net.mat0u5.lifeseries.mixin;
//? if < 1.21 {
/*import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public interface WindChargeItemMixin {
    //Empty class to avoid mixin errors
}
*///?} else {
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if <= 1.21 {
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
//?}
//? if >= 1.21.2
/*import net.minecraft.world.InteractionResult;*/

@Mixin(value = WindChargeItem.class, priority = 1)
public class WindChargeItemMixin {
    @Inject(method = "use", at = @At("RETURN"))
            //? if <= 1.21 {
    public void use(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        //?} else
        /*public void use(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {*/
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (user instanceof ServerPlayer player) {
            if (currentSeason.getSeason() != Seasons.WILD_LIFE) return;
            if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) return;

            TaskScheduler.scheduleTask(1, () -> {
                player.getInventory().add(Items.WIND_CHARGE.getDefaultInstance());
                player.getInventory().setChanged();
                PlayerUtils.updatePlayerInventory(player);
            });
        }
    }
}

//?}