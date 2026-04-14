package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.config.WorldConfig;
import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.utils.interfaces.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
//? if >= 1.20.3 {
import net.mat0u5.lifeseries.render.ClientRenderer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.TimeDilation;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}
//? if >= 26.1 {
import net.minecraft.world.level.gamerules.GameRules;
import java.util.Optional;
//?}

@Mixin(value = Minecraft.class, priority = 1)
public abstract class MinecraftMixin implements IMinecraft {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        LifeSeriesClient.onInitializeClient_();
    }

    //? if >= 1.20.3 {
    @Inject(method = "getTickTargetMillis", at = @At("HEAD"), cancellable = true)
    private void getTargetMillisPerTick(float millis, CallbackInfoReturnable<Float> cir) {
        if (LifeSeries.modFullyDisabled()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            TickRateManager tickManager = client.level.tickRateManager();
            if (LifeSeriesClient.TIME_DILATION_TIMESTAMP != 0) {
                long timeSinceDilationActivate = System.currentTimeMillis() - LifeSeriesClient.TIME_DILATION_TIMESTAMP;
                if (timeSinceDilationActivate < 14000) {
                    if (timeSinceDilationActivate < 1000) {
                        float tickRate = 1000/(1-(timeSinceDilationActivate / 4050.0f));
                        cir.setReturnValue(tickRate);
                        ClientRenderer.isGameFullyFrozen = false;
                        return;
                    }
                    if (timeSinceDilationActivate <= 10000) {
                        cir.setReturnValue(500000f);
                        ClientRenderer.isGameFullyFrozen = true;
                        return;
                    }
                    float tickRate = 1000/((timeSinceDilationActivate-10000) / 4050.0f);
                    cir.setReturnValue(tickRate);
                    ClientRenderer.isGameFullyFrozen = false;
                    return;
                }
            }
            if (tickManager.runsNormally()) {
                float mspt = Math.max(TimeDilation.MIN_PLAYER_MSPT, tickManager.millisecondsPerTick());
                cir.setReturnValue(mspt);
                ClientRenderer.isGameFullyFrozen = false;
                return;
            }
        }
        cir.setReturnValue(millis);
        ClientRenderer.isGameFullyFrozen = false;
    }
    //?}


    @Inject(method = "doWorldLoad", at = @At("HEAD"))
    //? if <= 1.20.2 {
    /*private void acknowledgeWorldLoad(String string, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    *///?} else if <= 1.21.11 {
    /*private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    *///?} else {
    private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, boolean newWorld, CallbackInfo ci) {
    //?}
        if (LifeSeries.modFullyDisabled()) return;
        WorldConfig worldConfig = new WorldConfig(levelStorageAccess);
        if (worldConfig.acknowledged()) return;
        worldConfig.setProperty("acknowledged", "true");
    }

    @Unique
    @Override
    public Screen ls$getScreen() {
        Minecraft self = (Minecraft) (Object) this;
        //? if <= 26.1 {
        return self.screen;
         //?} else {
        /*return self.gui.screen();
        *///?}
    }

    @Unique
    @Override
    public void ls$setScreen(Screen screen) {
        Minecraft self = (Minecraft) (Object) this;
        //? if <= 26.1 {
        self.setScreen(screen);
         //?} else {
        /*self.gui.setScreen(screen);
        *///?}
    }

    @Inject(at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.AFTER), method = "destroy")
    private void onStopping(CallbackInfo ci) {
        ClientEvents.onClientStopping((Minecraft) (Object) this);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;", shift = At.Shift.AFTER, ordinal = 0, opcode = Opcodes.PUTFIELD), method = "run")
    private void onStart(CallbackInfo ci) {
        ClientEvents.onClientStart((Minecraft) (Object) this);
    }
}
