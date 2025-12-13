package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 1.21.9 {
/*import net.minecraft.client.input.CharacterEvent;
*///?}

@Mixin(value = InBedChatScreen.class, priority = 1)
public abstract class InBedChatScreenMixin {
    @Accessor("leaveBedButton")
    abstract Button ls$leaveBedButton();

    @Inject(method = "charTyped", at = @At("HEAD"))
    //? if <= 1.21.6 {
    private void unfocusButton(char c, int i, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*private void unfocusButton(CharacterEvent input, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        InBedChatScreen chatScreen = (InBedChatScreen) (Object) this;
        ls$leaveBedButton().setFocused(false);
        if (chatScreen instanceof ChatScreenAccessor accessor) {
            chatScreen.setFocused(accessor.ls$input());
        }
    }
}
