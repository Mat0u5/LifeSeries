package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listening extends ToggleableSuperpower {
    public static final double MAX_RANGE = 20;
    public static List<UUID> listeningPlayers = new ArrayList<>();
    public Vec3 lookingAt = null;
    private Time timer = Time.zero();

    public Listening(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.LISTENING;
    }

    @Override
    public void tick() {
        timer.tick();
        if (!active) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        if (timer.isMultipleOf(Time.ticks(5))) {
            updateLooking();
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        player.ls$playNotifySound(SoundEvents.PUFFER_FISH_BLOW_UP, SoundSource.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, -1);
        listeningPlayers.add(player.getUUID());
        updateLooking();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        NetworkHandlerServer.sendVignette(player, 0);
        listeningPlayers.remove(player.getUUID());
        player.ls$playNotifySound(SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.MASTER, 1, 1);
    }

    public void updateLooking() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        Entity lookingAtEntity = PlayerUtils.getEntityLookingAt(player, 100);
        if (lookingAtEntity != null) {
            lookingAt = lookingAtEntity.position();
        } else {
            lookingAt = PlayerUtils.getPosLookingAt(player, 300);
        }
    }
}