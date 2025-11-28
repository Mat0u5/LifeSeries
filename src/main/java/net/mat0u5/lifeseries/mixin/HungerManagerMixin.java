package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.interfaces.IHungerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = FoodData.class, priority = 1)
public class HungerManagerMixin implements IHungerManager {
    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;

    @Unique
    private ServerPlayer ls$player;

    @Unique
    @Override
    public int ls$getFoodLevel() {
        return foodLevel;
    }

    @Unique
    @Override
    public float ls$getSaturationLevel() {
        return saturationLevel;
    }

    @Unique
    @Override
    public void ls$setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Unique
    @Override
    public void ls$setSaturationLevel(float saturationLevel) {
        this.saturationLevel = saturationLevel;
    }

    @Unique
    private float ls$prevFoodLevel;
    @Unique
    private float ls$prevSaturationLevel;

    @Inject(method = "tick", at = @At("HEAD"))
    //? if <= 1.21 {
    private void updateHead(Player player, CallbackInfo ci) {
    //?} else {
    /*private void updateHead(ServerPlayer player, CallbackInfo ci) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            this.ls$player = serverPlayer;
        }
        ls$prevFoodLevel = this.foodLevel;
        ls$prevSaturationLevel = this.saturationLevel;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    //? if <= 1.21 {
    private void updateTail(Player player, CallbackInfo ci) {
    //?} else {
    /*private void updateTail(ServerPlayer player, CallbackInfo ci) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (ls$prevFoodLevel != foodLevel || ls$prevSaturationLevel != saturationLevel) {
            ls$emitUpdate();
        }
    }

    @Inject(method = "setFoodLevel", at = @At("TAIL"))
    private void setFoodLevel(CallbackInfo ci) {
        ls$emitUpdate();
    }

    @Inject(method = "setSaturation", at = @At("TAIL"))
    private void setSaturationLevel(CallbackInfo ci) {
        ls$emitUpdate();
    }

    //? if <= 1.20.3 {
    /*@Inject(method = "eat(IF)V", at = @At("TAIL"))
    private void addInternal(CallbackInfo ci) {
        ls$emitUpdate();
    }
    *///?} else {
    @Inject(method = "add", at = @At("TAIL"))
    private void addInternal(CallbackInfo ci) {
        ls$emitUpdate();
    }
    //?}

    @Unique
    private void ls$emitUpdate() {
        if (!Main.isLogicalSide() || ls$player == null || Main.modDisabled()) return;
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.updateFoodFrom(ls$player);
        }
    }
}
