package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    //? if <= 1.21.9 {
    /*@Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("TAIL"))
    private void afterInitScreen(Minecraft minecraft, int width, int height, CallbackInfo ci) {
        ClientEvents.onScreenOpen(Minecraft.getInstance(), (Screen) (Object) this, width, height);
    }
    @Inject(method = "resize", at = @At("TAIL"))
    private void afterResizeScreen(Minecraft minecraft, int width, int height, CallbackInfo ci) {
        ClientEvents.onScreenOpen(Minecraft.getInstance(), (Screen) (Object) this, width, height);
    }
    *///?} else {
    @Inject(method = "init(II)V", at = @At("TAIL"))
    private void afterInitScreen(int width, int height, CallbackInfo ci) {
        ClientEvents.onScreenOpen(Minecraft.getInstance(), (Screen) (Object) this, width, height);
    }
    @Inject(method = "resize", at = @At("TAIL"))
    private void afterResizeScreen(int width, int height, CallbackInfo ci) {
        ClientEvents.onScreenOpen(Minecraft.getInstance(), (Screen) (Object) this, width, height);
    }
    //?}
}
