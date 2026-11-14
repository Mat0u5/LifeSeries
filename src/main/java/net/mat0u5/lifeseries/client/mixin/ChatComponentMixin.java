package net.mat0u5.lifeseries.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;

//? if <= 1.21.11 {
/*import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

@Mixin(value = ChatComponent.class)
@MixinEnvironment(value = "client", type = MixinEnvironment.Env.CLIENT)
public class ChatComponentMixin {
    //? if <= 1.21.11 {
    /*@Inject(method = "isChatHidden", at = @At("HEAD"), cancellable = true)
    private void inBetweenChatIsolation(CallbackInfoReturnable<Boolean> cir) {
        if (LifeSeries.modDisabled()) return;
        if (InBetweenManager.shouldIsolateChat(Minecraft.getInstance().player) && !LifeSeriesClient.isAdmin) {
            cir.setReturnValue(true);
        }
    }
    *///?}
}
