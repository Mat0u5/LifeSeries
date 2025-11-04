package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Gui.class, priority = 2)
public interface GuiAccessor {
    @Accessor("titleFadeInTime")
    int ls$titleFadeInTicks();

    @Accessor("titleStayTime")
    int ls$titleStayTicks();

    @Accessor("titleFadeOutTime")
    int ls$titleFadeOutTicks();

    @Accessor("titleTime")
    int ls$titleRemainTicks();

    @Accessor("titleTime")
    void ls$setTitleRemainTicks(int ticks);
}
