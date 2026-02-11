package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class Superpower {
    public boolean active = false;
    public long cooldown = 0;
    private final UUID playerUUID;
    public Superpower(ServerPlayer player) {
        playerUUID = player.getUUID();
        SessionTranscript.newSuperpower(player, getSuperpower());
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(playerUUID);
    }

    public abstract Superpowers getSuperpower();

    public int getCooldownMillis() {
        return 1000;
    }

    public void tick() {}

    public void onKeyPressed() {
        if (System.currentTimeMillis() < cooldown) {
            sendCooldownPacket();
            return;
        }
        activate();
    }

    public void activate() {
        active = true;
        cooldown(getCooldownMillis());
        triggerActivated();
    }

    public void deactivate() {
        active = false;
    }

    public void turnOff() {
        deactivate();
        SimplePackets.SUPERPOWER_COOLDOWN.target(getPlayer()).sendToClient(0);
    }

    public void cooldown(int millis) {
        cooldown = System.currentTimeMillis() + millis;
    }

    public void sendCooldownPacket() {
        SimplePackets.SUPERPOWER_COOLDOWN.target(getPlayer()).sendToClient(cooldown);
    }

    public void triggerActivated() {
        ServerPlayer player = getPlayer();
        if (player != null) {
            DatapackIntegration.EVENT_SUPERPOWER_TRIGGER.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("SuperpowerIndex", String.valueOf(getSuperpower().getIndex()))
            ));
        }
    }
}
