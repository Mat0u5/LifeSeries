package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatComponent.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ChatComponentMixin {
    //? if <= 1.21.11 {
    /*@Inject(method = "render", at = @At("HEAD"), cancellable = true)
    *///?} else {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    //?}
    private void inBetweenChatIsolation(CallbackInfo ci) {
        if (LifeSeries.modDisabled()) return;
        if (InBetweenManager.shouldIsolateChat(Minecraft.getInstance().player) && !(RenderUtils.getScreen() instanceof ChatScreen)) {
            ci.cancel();
        }
    }
}
