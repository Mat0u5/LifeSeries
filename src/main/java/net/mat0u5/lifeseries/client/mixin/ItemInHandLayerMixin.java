package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.client.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.client.gui.trivia.QuizScreen;
import net.mat0u5.lifeseries.client.gui.trivia.VotingScreen;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//? if > 1.21.2 {
import net.minecraft.client.renderer.item.ItemStackRenderState;
//?}
//? if <= 1.21.2
//import net.minecraft.world.item.ItemStack;
//? if <= 1.21.6 {
/*import net.mat0u5.lifeseries.client.features.abilities.DashClient;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(value = ItemInHandLayer.class, priority = 1)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
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
        if (!LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.NICE_LIFE) && (RenderUtils.getScreen() instanceof EmptySleepScreen || (RenderUtils.getScreen() instanceof NewQuizScreen quizScreen && !quizScreen.shouldCloseOnEsc()) || (RenderUtils.getScreen() instanceof QuizScreen quizScreenOld && !quizScreenOld.shouldCloseOnEsc()) || RenderUtils.getScreen() instanceof VotingScreen)) {
            //? if <= 1.21.2 {
            //return ItemStack.EMPTY;
            //?} else {
            value.clear();
            return value;
            //?}
        }
        return value;
    }

    // In SubmitNodeCollectionMixin for >= 1.21.9
    //? if <= 1.21.6 {
    /*@Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideDashGhostItems(CallbackInfo ci) {
        if (DashClient.isRenderingGhost()) ci.cancel();
    }
    *///?}
}