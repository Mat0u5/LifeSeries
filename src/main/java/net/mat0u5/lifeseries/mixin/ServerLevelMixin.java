package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = ServerLevel.class, priority = 1)
public class ServerLevelMixin {

    @Inject(method = "broadcastEntityEvent", at = @At("HEAD"))
    public void broadcast(Entity entity, byte status, CallbackInfo ci) {
        if (status != (byte) 35 || currentSeason.getSeason() != Seasons.SECRET_LIFE || Main.modDisabled()) {
            return;
        }
        // This sound doesnt exist client-side, so it won't double
        PlayerUtils.playSoundWithSourceToPlayers(entity, SoundEvent.createVariableRangeEvent(ResourceLocation.parse("secretlife_normal_totem")), entity.getSoundSource(), 1, 1);
    }
}
