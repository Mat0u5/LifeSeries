package net.mat0u5.lifeseries.utils.interfaces;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.UUID;

public interface IClientHelper {
    boolean isReplay();
    HandshakeStatus serverHandshake();
    boolean isRunningIntegratedServer();
    boolean isDisabledServerSide();
    boolean isMainClientPlayer(UUID uuid);
    Seasons getCurrentSeason();
    List<Wildcards> getActiveWildcards();
    void sendPacket(CustomPacketPayload payload);
}
