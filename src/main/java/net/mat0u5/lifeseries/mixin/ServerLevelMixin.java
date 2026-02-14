package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLife;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.currentSession;

//? if <= 1.20
//import net.minecraft.util.RandomSource;

//? if <= 1.21.9
//import net.minecraft.world.level.GameRules;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRules;

@Mixin(value = ServerLevel.class, priority = 1)
public class ServerLevelMixin {

    @Inject(method = "broadcastEntityEvent", at = @At("HEAD"))
    public void broadcast(Entity entity, byte status, CallbackInfo ci) {
        if (status != (byte) 35 || currentSeason.getSeason() != Seasons.SECRET_LIFE || Main.modDisabled()) {
            return;
        }
        // This sound doesnt exist client-side, so it won't double
        PlayerUtils.playSoundWithSourceToPlayers(entity, SoundEvent.createVariableRangeEvent(IdentifierHelper.parse("secretlife_normal_totem")), entity.getSoundSource(), 1, 1);
    }
    //? if <= 1.20 {
    /*@WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 1))
    public int customPrecipitation(RandomSource instance, int i, Operation<Integer> original) {
        if (Main.modDisabled() || currentSeason.getSeason() != Seasons.NICE_LIFE) {
            return original.call(instance, i);
        }
        return i;
    }
    *///?} else if <= 1.20.2 {
    /*@WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickIceAndSnow(ZLnet/minecraft/core/BlockPos;)V"))
    public void customPrecipitation(ServerLevel level, boolean bl, BlockPos pos, Operation<Void> original) {
        if (Main.modDisabled() || currentSeason.getSeason() != Seasons.NICE_LIFE) {
            original.call(level, bl, pos);
        }
    }
    *///?} else {
    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickPrecipitation(Lnet/minecraft/core/BlockPos;)V"))
    public void customPrecipitation(ServerLevel level, BlockPos pos, Operation<Void> original) {
        if (Main.modDisabled() || currentSeason.getSeason() != Seasons.NICE_LIFE) {
            original.call(level, pos);
        }
    }
    //?}
    @Inject(method = "tickChunk", at = @At(value = "HEAD"))
    public void customPrecipitation(LevelChunk levelChunk, int i, CallbackInfo ci) {
        if (!Main.modDisabled() && currentSeason.getSeason() == Seasons.NICE_LIFE) {
            ChunkPos chunkPos = levelChunk.getPos();
            ServerLevel level = (ServerLevel) (Object) this;
            for(int l = 0; l < i; ++l) {
                ((NiceLife)currentSeason).tickChunk(level, chunkPos);
            }
        }
    }
    @Inject(method = "announceSleepStatus", at = @At(value = "HEAD"), cancellable = true)
    public void dontAnnounce(CallbackInfo ci) {
        if (!Main.modDisabled() && currentSeason.getSeason() == Seasons.NICE_LIFE) {
            ci.cancel();
        }
    }
}
