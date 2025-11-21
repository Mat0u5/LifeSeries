package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

public interface IServerPlayer {
    String error = "This method should be overridden in the mixin";

    @Nullable default Integer ls$getLives()                             { throw new UnsupportedOperationException(error); }
    default boolean ls$hasAssignedLives()                               { throw new UnsupportedOperationException(error); }
    default boolean ls$isAlive()                                        { throw new UnsupportedOperationException(error); }
    default boolean ls$isDead()                                         { throw new UnsupportedOperationException(error); }
    default void ls$addLives(int amount)                                { throw new UnsupportedOperationException(error); }
    default void ls$addLife()                                           { throw new UnsupportedOperationException(error); }
    default void ls$removeLife()                                        { throw new UnsupportedOperationException(error); }
    default void ls$setLives(int lives)                                 { throw new UnsupportedOperationException(error); }
    default boolean ls$isOnLastLife(boolean fallback)                   { throw new UnsupportedOperationException(error); }
    default boolean ls$isOnSpecificLives(int check, boolean fallback)   { throw new UnsupportedOperationException(error); }
    default boolean ls$isOnAtLeastLives(int check, boolean fallback)    { throw new UnsupportedOperationException(error); }

    default boolean ls$isWatcher()                                      { throw new UnsupportedOperationException(error); }

    default void ls$hurt(DamageSource source, float amount)             { throw new UnsupportedOperationException(error); }
    default void ls$hurt(ServerLevel level, DamageSource source, float amount)             { throw new UnsupportedOperationException(error); }
    default ServerLevel ls$getServerLevel()                             { throw new UnsupportedOperationException(error); }
    default void ls$playNotifySound(SoundEvent sound, SoundSource soundSource, float volume, float pitch) { throw new UnsupportedOperationException(error); }
}
