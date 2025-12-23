package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.gui.trivia.NewQuizScreen;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemInHandRenderer.class, priority = 1)
public class ItemInHandRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At("HEAD"), index = 2, argsOnly = true)
    private ItemStack noHandItem(ItemStack value) {
        if (!Main.modDisabled() && MainClient.clientCurrentSeason == Seasons.NICE_LIFE && (Minecraft.getInstance().screen instanceof EmptySleepScreen || Minecraft.getInstance().screen instanceof NewQuizScreen)) {
            return ItemStack.EMPTY;
        }
        return value;
    }
}