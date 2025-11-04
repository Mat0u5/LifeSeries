package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.minecraft.server.level.ServerPlayer;

public abstract class ToggleableSuperpower extends Superpower {

    public ToggleableSuperpower(ServerPlayer player) {
        super(player);
    }

    @Override
    public void onKeyPressed() {
        if (System.currentTimeMillis() < cooldown) {
            sendCooldownPacket();
            return;
        }
        if (active) {
            deactivate();
        }
        else {
            activate();
        }
    }

    public int activateCooldownMillis() {
        return 50;
    }

    public int deactivateCooldownMillis() {
        return 1000;
    }

    @Override
    public void activate() {
        active = true;
        cooldown(activateCooldownMillis());
    }

    @Override
    public void deactivate() {
        active = false;
        cooldown(deactivateCooldownMillis());
    }
}
