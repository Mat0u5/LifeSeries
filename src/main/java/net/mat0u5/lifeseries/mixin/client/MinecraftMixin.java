package net.mat0u5.lifeseries.mixin.client;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.WorldConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 26.1 {
import net.minecraft.world.level.gamerules.GameRules;
import java.util.Optional;
//?}

@Mixin(value = Minecraft.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class MinecraftMixin {

    @Inject(method = "doWorldLoad", at = @At("HEAD"))
    //? if <= 1.20.2 {
    //private void acknowledgeWorldLoad(String string, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    //?} else if <= 1.21.11 {
    //private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, boolean bl, CallbackInfo ci) {
    //?} else {
    private void acknowledgeWorldLoad(LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, boolean newWorld, CallbackInfo ci) {
    //?}
        if (LifeSeries.modFullyDisabled()) return;
        WorldConfig worldConfig = new WorldConfig(levelStorageAccess);
        if (worldConfig.acknowledged()) return;
        worldConfig.setProperty("acknowledged", "true");
    }
}
