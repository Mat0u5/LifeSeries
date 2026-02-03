package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import static net.mat0u5.lifeseries.Main.currentSeason;
//? if <= 1.21.4
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.5
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <= 1.21.9 {
/*import net.minecraft.world.entity.npc.WanderingTraderSpawner;
*///?} else {
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
//?}

@Mixin(value = WanderingTraderSpawner.class, priority = 1)
public class WanderingTraderSpawnerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21.4 {
    /*public void spawn(ServerLevel level, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason.getSeason() == Seasons.SIMPLE_LIFE) {
            cir.setReturnValue(0);
        }
    }
    *///?} else {
    //? if <= 1.21.6 {
    /*public void spawn(ServerLevel level, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
    *///?} else {
    public void spawn(ServerLevel level, boolean spawnMonsters, CallbackInfo ci) {
    //?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason.getSeason() == Seasons.SIMPLE_LIFE) {
            ci.cancel();
        }
    }
    //?}
}
