package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChatScreen.class, priority = 1)
public interface ChatScreenAccessor {
    @Accessor("input")
    EditBox ls$input();
}
