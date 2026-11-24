package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 26.1 {
/*@Mixin(value = Minecraft.class)
*///?} else {
import net.minecraft.client.gui.Gui;
@Mixin(value = Gui.class)
//?}
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class OpenChatScreenMixin {
    @Inject(method = "openChatScreen", at = @At("HEAD"), cancellable = true)
    private void cancelOpenChat(CallbackInfo ci) {
        if (LifeSeries.modDisabled() || LifeSeriesClient.isAdmin) return;
        if (InBetweenManager.shouldIsolateChat(Minecraft.getInstance().player)) {
            ci.cancel();
        }
    }
}
