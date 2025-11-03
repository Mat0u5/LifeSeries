package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractSelectionList.class, priority = 1)
public class EntryListWidgetMixin  {

    //? if >= 1.21.9 {
    /*@Inject(method = "setSelected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;getY()I"), cancellable = true)
    public void canGlide(CallbackInfo ci) {
        EntryListWidget listWidget = (EntryListWidget) (Object) this;
        if (listWidget instanceof ConfigListWidget) {
            ci.cancel();
        }
    }
    *///?}
}