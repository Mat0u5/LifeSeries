package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.PlayerDataStorage;

public interface IPlayerManager {
    PlayerDataStorage ls$getSaveHandler();
    void ls$savePlayerData(ServerPlayer player);
}
