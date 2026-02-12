package net.mat0u5.lifeseries.mixin;

import com.mojang.datafixers.util.Either;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.utils.interfaces.IServerPlayer;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.NicknameManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

import static net.mat0u5.lifeseries.Main.*;

//? if >= 1.21.11 {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
//?}
//? if >= 1.21.2
import java.util.Collection;

@Mixin(value = ServerPlayer.class, priority = 1)
public class ServerPlayerMixin implements IServerPlayer {

    @Inject(method = "openMenu", at = @At("HEAD"))
    private void onInventoryOpen(@Nullable MenuProvider factory, CallbackInfoReturnable<OptionalInt> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        ServerPlayer player = ls$get();
        if (blacklist == null) return;
        
        TaskScheduler.scheduleTask(1, () -> {
            player.containerMenu.getItems().forEach(itemStack -> blacklist.processItemStack(player, itemStack));
            PlayerUtils.updatePlayerInventory(player);
        });
    }

    //? if <= 1.21.6 {
    /*@Inject(method = "sendSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"), cancellable = true)
    private void sendMessageToClient(Component message, boolean overlay, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        ServerPlayer player = ls$get();
        if (player instanceof FakePlayer) {
            ci.cancel();
        }
    }

    @Inject(method = "acceptsSystemMessages", at = @At("HEAD"), cancellable = true)
    private void acceptsMessage(boolean overlay, CallbackInfoReturnable<Boolean> cir) {
        if (Main.modFullyDisabled()) return;
        ServerPlayer player = ls$get();
        if (player instanceof FakePlayer) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "acceptsChatMessages", at = @At("HEAD"), cancellable = true)
    private void acceptsChatMessage(CallbackInfoReturnable<Boolean> cir) {
        if (Main.modFullyDisabled()) return;
        ServerPlayer player = ls$get();
        if (player instanceof FakePlayer) {
            cir.setReturnValue(false);
        }
    }
    *///?}

    //Located in the Player class in 26.1+
    //? if <= 1.21.11 {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackEntity(Entity target, CallbackInfo ci) {
        if (Main.modDisabled()) return;
        ServerPlayer player = ls$get();
        currentSeason.onUpdatedInventory(player);
    }
    //?}

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void onStatusEffectApplied(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        ls$onUpdatedEffects(effect, true);
    }

    //? if <= 1.21 {
    /*@Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void onStatusEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        ls$onUpdatedEffects(effect, false);
    }
    *///?} else {
    @Inject(method = "onEffectsRemoved", at = @At("TAIL"))
    private void onStatusEffectRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        for (MobEffectInstance effect : effects) {
            ls$onUpdatedEffects(effect, false);
        }
    }
    //?}

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void onStatusEffectUpgraded(MobEffectInstance effect, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        ls$onUpdatedEffects(effect, true);
    }


    @Unique
    private boolean ls$processing = false;

    @Unique
    private void ls$onUpdatedEffects(MobEffectInstance effect, boolean add) {
        if (ls$processing || Main.modDisabled()) {
            return;
        }
        ServerPlayer player = ls$get();
        ls$processing = true;
        try {
            if (currentSeason instanceof DoubleLife doubleLife) {
                doubleLife.syncStatusEffectsFrom(player, effect, add);
            }
        }finally {
            ls$processing = false;
        }
    }
    
    @Unique
    private ServerPlayer ls$get() {
        return (ServerPlayer) (Object) this;
    }


    /*
        Injected Interface
     */
    @Unique @Override @Nullable
    public Integer ls$getLives() {
        return livesManager.getPlayerLives(ls$get());
    }

    @Unique @Override
    public boolean ls$hasAssignedLives() {
        return livesManager.hasAssignedLives(ls$get());
    }

    @Unique @Override
    public boolean ls$isAlive() {
        return livesManager.isAlive(ls$get());
    }

    @Unique @Override
    public boolean ls$isDead() {
        return livesManager.isDead(ls$get());
    }

    @Unique @Override
    public void ls$addLives(int amount) {
        livesManager.addToPlayerLives(ls$get(), amount);
    }
    @Unique @Override
    public void ls$addLife() {
        livesManager.addPlayerLife(ls$get());
    }
    @Unique @Override
    public void ls$removeLife() {
        livesManager.removePlayerLife(ls$get());
    }

    @Unique @Override
    public void ls$setLives(int lives) {
        livesManager.setPlayerLives(ls$get(), lives);
    }

    @Unique @Override
    public boolean ls$isOnLastLife(boolean fallback) {
        return livesManager.isOnLastLife(ls$get(), fallback);
    }

    @Unique @Override
    public boolean ls$isOnSpecificLives(int check, boolean fallback) {
        return livesManager.isOnSpecificLives(ls$get(), check, fallback);
    }

    @Unique @Override
    public boolean ls$isOnAtLeastLives(int check, boolean fallback) {
        return livesManager.isOnAtLeastLives(ls$get(), check, fallback);
    }


    @Unique @Override
    public boolean ls$isWatcher() {
        return WatcherManager.isWatcher(ls$get());
    }

    @Unique @Override
    public void ls$hurt(DamageSource source, float amount) {
        //? if <= 1.21 {
        /*ls$get().hurt(source, amount);
        *///?} else {
        ls$get().hurtServer(ls$getServerLevel(), source, amount);
         //?}
    }
    @Unique @Override
    public void ls$hurt(ServerLevel level, DamageSource source, float amount) {
        //? if <= 1.21 {
        /*ls$get().hurt(source, amount);
        *///?} else {
        ls$get().hurtServer(level, source, amount);
         //?}
    }

    @Unique @Override
    public ServerLevel ls$getServerLevel() {
        //? if <= 1.21.5 {
        /*return ls$get().serverLevel();
        *///?} else {
        return ls$get().level();
         //?}
    }

    @Unique @Override
    public void ls$playNotifySound(SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        ServerPlayer self = ls$get();
        //? if <= 1.21.9 {
        /*self.playNotifySound(sound, soundSource, volume, pitch);
        *///?} else {
        self.connection
                .send(
                        new ClientboundSoundPacket(
                                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundSource, self.getX(), self.getY(), self.getZ(), volume, pitch, self.getRandom().nextLong()
                        )
                );
        //?}
    }


    @Inject(method = "startSleepInBed", at = @At("HEAD"), cancellable = true)
    private void cancelStartSleep(BlockPos blockPos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        if (!Main.modDisabled() && currentSeason.getSeason() == Seasons.NICE_LIFE) {
            if (NiceLifeTriviaManager.triviaInProgress) {
                cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
                ls$get().sendSystemMessage(ModifiableText.NICELIFE_SLEEP_FAIL_LATE.get(), true);
            }
        }
    }

    @Inject(method = "getTabListDisplayName", at = @At("TAIL"), cancellable = true)
    private void customNickname(CallbackInfoReturnable<Component> cir) {
        try {
            Component nickname = NicknameManager.getNicknameText(ls$get().getUUID());

            if (nickname != null) {
                Component formattedName = PlayerTeam.formatNameForTeam(ls$get().getTeam(), nickname);
                cir.setReturnValue(formattedName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
