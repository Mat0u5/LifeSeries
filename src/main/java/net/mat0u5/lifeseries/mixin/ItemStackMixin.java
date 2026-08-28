package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.LifeSeries.currentSession;

//? if >= 1.20.5 {
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
//?}
//? if <= 1.20.3 {
/*import net.minecraft.world.entity.LivingEntity;
import java.util.function.Consumer;
*///?} else if <= 1.20.5 {
/*import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
*///?} else if <= 1.21 {
/*import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
*///?} else {
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
//?}

@Mixin(value = ItemStack.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ItemStackMixin {
    //isSameItemSameTags in 1.20, but i think it'll work fine without it?
    //? if >= 1.20.5 {
    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void areItemsAndComponentsEqual(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(otherStack.getItem()) || LifeSeries.modDisabled()) return;

        if (stack.isEmpty() && otherStack.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }
        if (stack.equals(otherStack)) {
            cir.setReturnValue(true);
            return;
        }
        PatchedDataComponentMap comp1 = new PatchedDataComponentMap(stack.getComponents());
        PatchedDataComponentMap comp2 = new PatchedDataComponentMap(otherStack.getComponents());

        comp1.set(DataComponents.FOOD, stack.getPrototype().get(DataComponents.FOOD));
        comp2.set(DataComponents.FOOD, stack.getPrototype().get(DataComponents.FOOD));
        //? if >= 1.21.2 {
        comp1.set(DataComponents.CONSUMABLE, stack.getPrototype().get(DataComponents.CONSUMABLE));
        comp2.set(DataComponents.CONSUMABLE, stack.getPrototype().get(DataComponents.CONSUMABLE));
         //?}
        if (Objects.equals(comp1, comp2)) {
            cir.setReturnValue(true);
            return;
        }

        boolean componentsEqual = true;

        Set<DataComponentType<?>> allTypes = new HashSet<>();
        allTypes.addAll(comp1.keySet());
        allTypes.addAll(comp2.keySet());

        for (DataComponentType<?> type : allTypes) {
            if (type.equals(DataComponents.FOOD)) continue;
            //? if >= 1.21.2
            if (type.equals(DataComponents.CONSUMABLE)) continue;

            Object value1 = comp1.get(type);
            Object value2 = comp2.get(type);

            if (!Objects.equals(value1, value2)) {
                componentsEqual = false;
                break;
            }
        }

        cir.setReturnValue(componentsEqual);
    }
    //?}

    //? if <= 1.20.3 {
    /*@Inject(method = "hurtAndBreak", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void cancelItemDamage(int i, LivingEntity livingEntity, Consumer<T> consumer, CallbackInfo ci) {
    *///?} else if <= 1.20.5 {
    /*@Inject(method = "hurtAndBreak(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void cancelItemDamage(int i, RandomSource randomSource, ServerPlayer serverPlayer, Runnable runnable, CallbackInfo ci) {
    *///?} else if <= 1.21 {
    /*@Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void cancelItemDamage(int i, ServerLevel serverLevel, @Nullable ServerPlayer serverPlayer, Consumer<Item> consumer, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
    private void cancelItemDamage(int newDamage, @Nullable ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
    //?}
        if (LifeSeries.isClientOrDisabled() || !Season.ONLY_LOSE_DURABILITY_IN_SESSION) return;
        if (!currentSession.statusStarted()) {
            ci.cancel();
        }
    }

}
