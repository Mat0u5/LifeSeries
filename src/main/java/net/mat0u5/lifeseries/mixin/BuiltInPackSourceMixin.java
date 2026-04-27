package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.utils.other.ModBuiltInPacks;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
public class BuiltInPackSourceMixin {
    //? if fabric {
    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void addBuiltInDatapacks(Consumer<Pack> consumer, CallbackInfo ci) {
        if ((Object) this instanceof ServerPacksSource) {
            ModBuiltInPacks.loadPacks(consumer, PackType.SERVER_DATA);
        }
    }
    //?}
}