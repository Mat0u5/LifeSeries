package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.gui.trivia.VotingScreen;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if > 1.21.2 {
import net.minecraft.client.renderer.item.ItemStackRenderState;
//?}
//? if <= 1.21.2
//import net.minecraft.world.item.ItemStack;

@Mixin(value = ItemInHandLayer.class, priority = 1)
public class ItemInHandLayerMixin {
    //? if <= 1.21 {
    /*@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), index = 2, argsOnly = true)
    private ItemStack noHandItem(ItemStack value) {
    *///?} else if <= 1.21.2 {
    /*@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), index = 3, argsOnly = true)
    private ItemStack noHandItem(ItemStack value) {
    *///?} else if <= 1.21.6 {
    /*@ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), index = 2, argsOnly = true)
    private ItemStackRenderState noHandItem(ItemStackRenderState value) {
    *///?} else {
    @ModifyVariable(method = "submitArmWithItem", at = @At("HEAD"), index = 2, argsOnly = true)
    private ItemStackRenderState noHandItem(ItemStackRenderState value) {
    //?}
        if (!LifeSeries.modDisabled() && LifeSeriesClient.clientCurrentSeason == Seasons.NICE_LIFE && (Minecraft.getInstance().ls$getScreen() instanceof EmptySleepScreen || Minecraft.getInstance().ls$getScreen() instanceof NewQuizScreen || Minecraft.getInstance().ls$getScreen() instanceof VotingScreen)) {
            //? if <= 1.21.2 {
            /*return ItemStack.EMPTY;
            *///?} else {
            value.clear();
            return value;
            //?}
        }
        return value;
    }
}