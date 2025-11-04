package net.mat0u5.lifeseries.entity.fakeplayer;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;

/*
 * This file includes code from the Fabric Carpet project: https://github.com/gnembon/fabric-carpet
 *
 * Used and modified under the MIT License.
 */
public class FakeClientConnection extends Connection {
    public FakeClientConnection(PacketFlow side) {
        super(side);
    }
    @Override
    public void setReadOnly() {}
    @Override
    public void handleDisconnection() {}
    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) {}
    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> protocolInfo, T packetListener) {}
}