package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.20.5 {
/*import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
*///?}

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin {
    //?if < 1.20.5 {
    @Accessor("foodProperties")
    public abstract FoodProperties foodProperties();
    //?}
    //? if >= 1.20.5 {
    /*@Accessor("components")
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
            if (!Hunger.nonEdible.contains(item)) {
                PatchedDataComponentMap components = new PatchedDataComponentMap(normalComponents());
                Hunger.defaultFoodComponents(item, components);
                cir.setReturnValue(components);
            }
        }
    }
    *///?}

    @Inject(method = "isEdible", at = @At("HEAD"))
    public void isEdible(CallbackInfoReturnable<Boolean> cir) {
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
            if (!Hunger.nonEdible.contains(item)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            Item item = (Item) (Object) this;
            //? if < 1.20.5 {
            Hunger.finishUsing(item, foodProperties() != null, user);
            //?} else {
            /*Hunger.finishUsing(item, normalComponents(), user);
            *///?}
        }
    }
}