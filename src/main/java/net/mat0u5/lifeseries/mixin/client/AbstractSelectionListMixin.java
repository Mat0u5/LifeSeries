package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
//? if >= 1.21.9 {
import net.mat0u5.lifeseries.client.gui.config.ConfigListWidget;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(value = AbstractSelectionList.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class AbstractSelectionListMixin {

    //? if >= 1.21.9 {
    @Inject(method = "setSelected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;getY()I"), cancellable = true)
    public void canGlide(CallbackInfo ci) {
        AbstractSelectionList listWidget = (AbstractSelectionList) (Object) this;
        if (listWidget instanceof ConfigListWidget) {
            ci.cancel();
        }
    }
    //?}
}