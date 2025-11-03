package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin {
    @Accessor("components")
    public abstract DataComponentMap normalComponents();

    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    public void getComponents(CallbackInfoReturnable<DataComponentMap> cir) {
        if (Main.modDisabled()) return;
        boolean isLogicalSide = Main.isLogicalSide();
        boolean hungerActive = false;
        if (isLogicalSide) {
            if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        else {
            if (Main.clientHelper != null &&
                    Main.clientHelper.getCurrentSeason() == Seasons.WILD_LIFE &&
                    Main.clientHelper.getActiveWildcards().contains(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        if (hungerActive) {
            Item item = (Item) (Object) this;
            PatchedDataComponentMap components = new PatchedDataComponentMap(normalComponents());
            Hunger.defaultFoodComponents(item, components);
            cir.setReturnValue(components);
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            Item item = (Item) (Object) this;
            Hunger.finishUsing(item, normalComponents(), user);
        }
    }
}