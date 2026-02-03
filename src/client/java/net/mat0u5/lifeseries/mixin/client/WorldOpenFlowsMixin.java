package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.WorldConfig;
import net.mat0u5.lifeseries.gui.WorldWarningScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

//? if <= 1.20.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import java.io.IOException;
*///?} else {
    //?if <= 1.20.3 {
    /*import com.mojang.serialization.Dynamic;
    *///?} else {
    import net.minecraft.server.WorldStem;
    import net.minecraft.server.packs.repository.PackRepository;
    //?}
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(value = WorldOpenFlows.class, priority = 1)
public abstract class WorldOpenFlowsMixin {

    //? if <= 1.20.2 {
    /*@Invoker("doLoadLevel")
    abstract void ls$doLoadLevel(Screen screen, String string, boolean bl, boolean bl2);
    @WrapOperation(method = "doLoadLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;createWorldAccess(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;"))
    private LevelStorageSource.LevelStorageAccess verifyWorldOpen(WorldOpenFlows instance, String e, Operation<LevelStorageSource.LevelStorageAccess> originalCall, @Local(argsOnly = true) Screen screen, @Local(argsOnly = true, ordinal = 0) boolean bl,@Local(argsOnly = true, ordinal = 1) boolean bl2) {
        LevelStorageSource.LevelStorageAccess worldAccess = originalCall.call(instance, e);
        if (worldAccess == null) return worldAccess;
        if (Main.modFullyDisabled()) return worldAccess;
        WorldConfig worldConfig = new WorldConfig(worldAccess);
        if (worldConfig.acknowledged()) return worldAccess;
        ls$askForConfirmation(worldAccess, worldAccess.getLevelId(),
                () -> {
                    worldConfig.setProperty("acknowledged", "true");
                    ls$doLoadLevel(screen, e, bl, bl2);
                },
                () -> {
                    Minecraft.getInstance().setScreen(screen);
                }
        );
        try {
            worldAccess.close();
        } catch (IOException ignored) {}
        return null; // This "cancels" the original method.
    }
    *///?} else {

    //?if <= 1.20.3 {
    /*@Invoker("loadLevel")
    abstract void ls$openWorldCheckWorldStemCompatibility(LevelStorageSource.LevelStorageAccess levelStorageAccess, Dynamic<?> dynamic, boolean bl, boolean bl2, Runnable runnable);

    @Inject(method = "loadLevel", at = @At("HEAD"), cancellable = true)
    private void verifyWorldOpen(LevelStorageSource.LevelStorageAccess worldAccess, Dynamic<?> dynamic, boolean bl, boolean bl2, Runnable onCancel, CallbackInfo ci) {
    *///?} else {
    @Invoker("openWorldCheckWorldStemCompatibility")
    abstract void ls$openWorldCheckWorldStemCompatibility(final LevelStorageSource.LevelStorageAccess worldAccess, final WorldStem worldStem, final PackRepository packRepository, final Runnable onCancel);

    @Inject(method = "openWorldCheckWorldStemCompatibility", at = @At("HEAD"), cancellable = true)
    private void verifyWorldOpen(LevelStorageSource.LevelStorageAccess worldAccess, WorldStem worldStem, PackRepository packRepository, Runnable onCancel, CallbackInfo ci) {
    //?}
        if (Main.modFullyDisabled()) return;
        WorldConfig worldConfig = new WorldConfig(worldAccess);
        if (worldConfig.acknowledged()) return;
        ci.cancel();
        ls$askForConfirmation(worldAccess, worldAccess.getLevelId(),
                () -> {
                    worldConfig.setProperty("acknowledged", "true");
                    //?if <= 1.20.3 {
                    /*ls$openWorldCheckWorldStemCompatibility(worldAccess, dynamic, bl, bl2, onCancel);
                    *///?} else {
                    ls$openWorldCheckWorldStemCompatibility(worldAccess, worldStem, packRepository, onCancel);
                    //?}
                },
                () -> {
                    //?if > 1.20.3 {
                    worldStem.close();
                    //?}
                    worldAccess.safeClose();
                    onCancel.run();
                }
        );
    }
    //?}
    private void ls$askForConfirmation(final LevelStorageSource.LevelStorageAccess worldAccess, String levelId, final Runnable proceedCallback, final Runnable cancelCallback) {
        Minecraft.getInstance().setScreen(new WorldWarningScreen(levelId, cancelCallback, disable -> {
            if (disable) {
                Main.setDisabled(true);
            }

            proceedCallback.run();
        }));
    }
}
