package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.seasons.season.finallife.boogeyman.BoogeymanWeaponManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <= 1.21 {
/*import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
*///?} else {
import net.minecraft.world.InteractionResult;
//?}

@Mixin(value = ItemStack.class, priority = 2)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ItemStackBoogeymanMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    //private void ls$startChargingThrow(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
    //?} else {
    private void ls$startChargingThrow(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    //?}
        ItemStack stack = (ItemStack) (Object) this;
        BoogeymanWeaponManager.UseResult result = BoogeymanWeaponManager.onUseItem(stack, level, player);
        if (result == BoogeymanWeaponManager.UseResult.PASS) return;
        if (result == BoogeymanWeaponManager.UseResult.DENY) {
            //? if <= 1.21 {
            //cir.setReturnValue(InteractionResultHolder.pass(stack));
            //?} else {
            cir.setReturnValue(InteractionResult.PASS);
            //?}
            return;
        }
        // UseResult.ALLOW

        player.startUsingItem(hand);
        //? if <= 1.21 {
        //cir.setReturnValue(InteractionResultHolder.consume(stack));
        //?} else {
        cir.setReturnValue(InteractionResult.CONSUME);
        //?}
    }

    //? if <= 1.20.5 {
    /*@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void ls$throwUseDuration(CallbackInfoReturnable<Integer> cir) {
        if (BoogeymanWeaponManager.isBoogeymanWeapon((ItemStack) (Object) this)) {
            cir.setReturnValue(BoogeymanWeaponManager.CONSUME_DURATION);
        }
    }
    *///?} else if <= 1.21 {
    /*@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void ls$throwUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (BoogeymanWeaponManager.isBoogeymanWeapon((ItemStack) (Object) this)) {
            cir.setReturnValue(BoogeymanWeaponManager.CONSUME_DURATION);
        }
    }
    *///?}

    //? if <= 1.21 {
    /*@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void ls$throwAnimation(CallbackInfoReturnable<UseAnim> cir) {
        if (BoogeymanWeaponManager.isBoogeymanWeapon((ItemStack) (Object) this)) {
            cir.setReturnValue(UseAnim.SPEAR);
        }
    }
    *///?}

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void ls$releaseThrow(Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!BoogeymanWeaponManager.isBoogeymanWeapon(stack)) return;
        ci.cancel();
        BoogeymanWeaponManager.onStopUseItem(stack, level, entity, timeLeft);
    }
}
