package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.utils.other.ModBuiltInPacks;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class BuiltInPackSourceMixin {
    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void addBuiltInResourcepacks(Consumer<Pack> consumer, CallbackInfo ci) {
        if ((Object) this instanceof ClientPackSource) {
            ModBuiltInPacks.loadPacks(consumer, PackType.CLIENT_RESOURCES);
        }
    }
}