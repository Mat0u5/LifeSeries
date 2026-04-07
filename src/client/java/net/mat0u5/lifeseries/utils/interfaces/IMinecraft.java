package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.client.gui.screens.Screen;

public interface IMinecraft {
    String error = "This method should be overridden in the mixin";

    default Screen ls$getScreen()                             { throw new UnsupportedOperationException(error); }
    default void ls$setScreen(Screen screen)                  { throw new UnsupportedOperationException(error); }
}
