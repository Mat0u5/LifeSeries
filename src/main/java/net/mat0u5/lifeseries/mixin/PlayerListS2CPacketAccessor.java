package net.mat0u5.lifeseries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

@Mixin(value = ClientboundPlayerInfoUpdatePacket.class, priority = 1)
public interface PlayerListS2CPacketAccessor {
    @Mutable
    @Accessor
    void setEntries(List<ClientboundPlayerInfoUpdatePacket.Entry> entries);
}
