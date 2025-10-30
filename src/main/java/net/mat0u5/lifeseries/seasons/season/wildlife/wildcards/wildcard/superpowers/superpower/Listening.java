package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listening extends ToggleableSuperpower {
    public static final double MAX_RANGE = 20;
    public static List<UUID> listeningPlayers = new ArrayList<>();
    public Vec3d lookingAt = null;
    private long ticks = 0;

    public Listening(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.LISTENING;
    }

    @Override
    public void tick() {
        ticks++;
        if (!active) return;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        if (ticks % 5 == 0) {
            updateLooking();
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        player.playSoundToPlayer(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, SoundCategory.MASTER, 1, 1);
        NetworkHandlerServer.sendVignette(player, -1);
        listeningPlayers.add(player.getUuid());
        updateLooking();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        NetworkHandlerServer.sendVignette(player, 0);
        listeningPlayers.remove(player.getUuid());
        player.playSoundToPlayer(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.MASTER, 1, 1);
    }

    public void updateLooking() {
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        Entity lookingAtEntity = PlayerUtils.getEntityLookingAt(player, 100);
        if (lookingAtEntity != null) {
            lookingAt = lookingAtEntity.ls$getEntityPos();
        } else {
            lookingAt = PlayerUtils.getPosLookingAt(player, 300);
        }
    }
}