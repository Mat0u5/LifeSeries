package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
//?}
//? if < 1.20.5 {
/*import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
*///?}
//? if <= 1.21.11
//import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin {
    //? if < 1.20.5 {
    /*@Accessor("foodProperties")
    public abstract FoodProperties ls$foodProperties();
    *///?}
    //? if >= 1.20.5 {
    //? if <= 1.21.11 {
    /*@Accessor("components")
    public abstract DataComponentMap normalComponents();
    *///?}

    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    public void getComponents(CallbackInfoReturnable<DataComponentMap> cir) {
        if (LifeSeries.modDisabled()) return;
        boolean isLogicalSide = LifeSeries.isLogicalSide();
        boolean hungerActive = false;
        if (isLogicalSide) {
            if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        else {
            if (LifeSeries.clientHelper != null &&
                    LifeSeries.clientHelper.getCurrentSeason() == Seasons.WILD_LIFE &&
                    LifeSeries.clientHelper.getActiveWildcards().contains(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        if (hungerActive) {
            Item item = (Item) (Object) this;
            if (!Hunger.nonEdible.contains(item)) {
                //? if <= 1.21.11 {
                /*PatchedDataComponentMap components = new PatchedDataComponentMap(normalComponents());
                *///?} else {
                PatchedDataComponentMap components = new PatchedDataComponentMap(item.builtInRegistryHolder().components());
                //?}
                Hunger.defaultFoodComponents(item, components);
                cir.setReturnValue(components);
            }
        }
    }
    //?}

    //? if < 1.20.5 {
    /*@Inject(method = "isEdible", at = @At("HEAD"), cancellable = true)
    public void isEdible(CallbackInfoReturnable<Boolean> cir) {
        Item item = (Item) (Object) this;
        cir.setReturnValue(item.getFoodProperties() != null);
    }

    @Inject(method = "getFoodProperties", at = @At("HEAD"), cancellable = true)
    public void makeEdible(CallbackInfoReturnable<FoodProperties> cir) {
        if (LifeSeries.modDisabled()) return;
        boolean isLogicalSide = LifeSeries.isLogicalSide();
        boolean hungerActive = false;
        if (isLogicalSide) {
            if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        else {
            if (LifeSeries.clientHelper != null &&
                    LifeSeries.clientHelper.getCurrentSeason() == Seasons.WILD_LIFE &&
                    LifeSeries.clientHelper.getActiveWildcards().contains(Wildcards.HUNGER)) {
                hungerActive = true;
            }
        }
        if (hungerActive) {
            Item item = (Item) (Object) this;
            if (!Hunger.nonEdible.contains(item)) {
                cir.setReturnValue(Foods.BREAD);
            }
        }
    }
    *///?}

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, Level level, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (LifeSeries.isClientOrDisabled()) return;
        if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            Item item = (Item) (Object) this;
            //? if < 1.20.5 {
            /*Hunger.finishUsing(item, ls$foodProperties() != null, user);
            *///?} else if <= 1.21.11 {
            /*Hunger.finishUsing(item, normalComponents(), user);
            *///?} else {
            Hunger.finishUsing(item, item.builtInRegistryHolder().components(), user);
            //?}
        }
    }
}