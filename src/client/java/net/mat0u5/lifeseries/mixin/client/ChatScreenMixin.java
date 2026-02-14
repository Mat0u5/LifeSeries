package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 1.21.9 {
import net.minecraft.client.input.KeyEvent;
//?}

@Mixin(value = ChatScreen.class, priority = 1)
public class ChatScreenMixin {
    //1.21.11- logic is in ChatScreenMixin
    //?if >= 26.1 {
    /*@Inject(method = "keyPressed", at = @At("HEAD"))
    private void unfocusButton(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        ChatScreen chatScreen = (ChatScreen) (Object) this;
        if (chatScreen instanceof InBedChatScreen) {
            if (chatScreen instanceof InBedChatScreenAccessor accessor) {
                accessor.ls$leaveBedButton().setFocused(false);
            }
            if (chatScreen instanceof ChatScreenAccessor accessor) {
                chatScreen.setFocused(accessor.ls$input());
            }
        }
    }
    *///?}
}
