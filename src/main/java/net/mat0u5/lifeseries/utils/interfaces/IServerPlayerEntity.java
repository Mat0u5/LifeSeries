package net.mat0u5.lifeseries.utils.interfaces;

import org.jetbrains.annotations.Nullable;

public interface IServerPlayerEntity {
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
}
