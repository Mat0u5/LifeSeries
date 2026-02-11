package net.mat0u5.lifeseries.utils.interfaces;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.enums.HandshakeStatus;
//? if <= 1.20.3 {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
*///?} else {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
 //?}

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
    //? if <= 1.20.3 {
    /*void sendPacket(FabricPacket payload);
    *///?} else {
    void sendPacket(CustomPacketPayload payload);
    //?}
}
