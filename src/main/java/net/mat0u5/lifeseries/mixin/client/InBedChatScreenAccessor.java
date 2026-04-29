package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InBedChatScreen.class, priority = 2)
public interface InBedChatScreenAccessor {
    @Accessor("leaveBedButton")
    abstract Button ls$leaveBedButton();
}
