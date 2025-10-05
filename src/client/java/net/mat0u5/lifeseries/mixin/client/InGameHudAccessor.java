package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InGameHud.class, priority = 2)
public interface InGameHudAccessor {
    @Accessor("titleFadeInTicks")
    int ls$titleFadeInTicks();

    @Accessor("titleStayTicks")
    int ls$titleStayTicks();

    @Accessor("titleFadeOutTicks")
    int ls$titleFadeOutTicks();

    @Accessor("titleRemainTicks")
    int ls$titleRemainTicks();

    @Accessor("titleRemainTicks")
    void ls$setTitleRemainTicks(int ticks);
}
