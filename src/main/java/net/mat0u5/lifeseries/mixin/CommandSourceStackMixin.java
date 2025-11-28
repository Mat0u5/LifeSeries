package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = CommandSourceStack.class, priority = 1)
public abstract class CommandSourceStackMixin {

    //? if <= 1.20.2 {
    /*@Accessor
    public abstract boolean isSilent();
    *///?}

    @Inject(method = "sendSuccess", at = @At("HEAD"))
    public void sendFeedback(Supplier<Component> feedbackSupplier, boolean broadcastToOps, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (!broadcastToOps) return;
        Component text = feedbackSupplier.get();
        String sourceStr = "null";
        CommandSourceStack source = (CommandSourceStack) (Object) this;
        //? if <= 1.20.2 {
        /*if (isSilent()) return;
        *///?} else {
        if (source.isSilent()) return;
        //?}
        if (!source.isPlayer()) {
            sourceStr = "console";
        }
        if (source.getPlayer() != null) {
            sourceStr = source.getPlayer().getName().getString();
        }
        SessionTranscript.addMessageWithTime(TextUtils.formatString("[COMMAND (source: {})] {}", sourceStr, text.getString()));
    }
}
