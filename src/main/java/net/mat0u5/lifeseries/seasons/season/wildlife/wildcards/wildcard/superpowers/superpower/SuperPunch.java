package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class SuperPunch extends ToggleableSuperpower {
    private long ticks = 0;
    private Entity riding = null;
    private static final List<EntityType<?>> bannedSittingEntities = List.of(MobRegistry.SNAIL, MobRegistry.TRIVIA_BOT);

    public SuperPunch(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.SUPER_PUNCH;
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayer player = getPlayer();
        if (player != null) NetworkHandlerServer.sendVignette(player, -1);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player != null) {
            NetworkHandlerServer.sendVignette(player, 0);
            if (player.isPassenger()) {
                player.removeVehicle();
                syncEntityPassengers(riding, player.ls$getServerLevel());
            }
        }
    }

    @Override
    public void tick() {
        ticks++;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        if (ticks % 5 == 0) {
            if (player.isPassenger() && player.getVehicle() != null && player.getVehicle().isSpectator()) {
                player.removeVehicle();
            }
            if (riding != null && !player.isPassenger()) {
                syncEntityPassengers(riding, player.ls$getServerLevel());
                riding = null;
            }
        }
    }

    public void tryRideEntity(Entity entity) {
        if (entity == null) return;
        if (bannedSittingEntities.contains(entity.getType())) return;
        ServerPlayer rider = getPlayer();
        if (rider == null) return;

        if (entity.isVehicle()) return;

        ServerLevel riderLevel = rider.ls$getServerLevel();

        if (rider.isPassenger()) {
            Entity vehicle = rider.getVehicle();
            rider.removeVehicle();

            syncEntityPassengers(vehicle, riderLevel);
        }

        //? if <= 1.21.6 {
        boolean rideResult = rider.startRiding(entity, true);
        //?} else {
        /*boolean rideResult = rider.startRiding(entity, true, true);
        *///?}

        if (rideResult) {
            riding = entity;
            syncEntityPassengers(entity, riderLevel);
        }
    }

    private void syncEntityPassengers(Entity entity, ServerLevel level) {
        ClientboundSetPassengersPacket passengersPacket = new ClientboundSetPassengersPacket(entity);

        for (ServerPlayer trackingPlayer : PlayerLookup.tracking(level, entity.blockPosition())) {
            trackingPlayer.connection.send(passengersPacket);
        }

        if (entity instanceof ServerPlayer ridingPlayer) {
            ridingPlayer.connection.send(passengersPacket);
        }

        for (Entity passenger : entity.getPassengers()) {
            if (passenger instanceof ServerPlayer ridingPlayer) {
                ridingPlayer.connection.send(passengersPacket);
            }
        }
    }
}
