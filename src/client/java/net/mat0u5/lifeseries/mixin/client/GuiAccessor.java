package net.mat0u5.lifeseries.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <= 26.1 {
import net.minecraft.client.gui.Gui;
@Mixin(value = Gui.class, priority = 2)
//?} else {
/*import net.minecraft.client.gui.Hud;
@Mixin(value = Hud.class, priority = 2)
*///?}
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
