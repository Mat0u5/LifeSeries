package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.registries.MobRegistry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class SuperPunch extends ToggleableSuperpower {
    private long ticks = 0;
    private Entity riding = null;
    private static final List<EntityType<?>> bannedSittingEntities = List.of(MobRegistry.SNAIL, MobRegistry.TRIVIA_BOT);

    public SuperPunch(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.SUPER_PUNCH;
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player != null) NetworkHandlerServer.sendVignette(player, -1);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player != null) {
            NetworkHandlerServer.sendVignette(player, 0);
            if (player.hasVehicle()) {
                player.dismountVehicle();
            }
        }
    }

    @Override
    public void tick() {
        ticks++;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        if (ticks % 5 == 0) {
            if (riding != null && !player.hasVehicle()) {
                syncEntityPassengers(riding, PlayerUtils.getServerWorld(player));
                riding = null;
            }
        }
    }

    public void tryRideEntity(Entity entity) {
        if (entity == null) return;
        if (bannedSittingEntities.contains(entity.getType())) return;
        ServerPlayerEntity rider = getPlayer();
        if (rider == null) return;

        if (entity.hasPassengers()) return;

        ServerWorld riderWorld = PlayerUtils.getServerWorld(rider);

        if (rider.hasVehicle()) {
            Entity vehicle = rider.getVehicle();
            rider.dismountVehicle();

            syncEntityPassengers(vehicle, riderWorld);
        }

        //? if <= 1.21.6 {
        boolean rideResult = rider.startRiding(entity, true);
        //?} else {
        /*boolean rideResult = rider.startRiding(entity, true, true);
        *///?}

        if (rideResult) {
            riding = entity;
            syncEntityPassengers(entity, riderWorld);
        }
    }

    private void syncEntityPassengers(Entity entity, ServerWorld world) {
        EntityPassengersSetS2CPacket passengersPacket = new EntityPassengersSetS2CPacket(entity);

        for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(world, entity.getBlockPos())) {
            trackingPlayer.networkHandler.sendPacket(passengersPacket);
        }

        if (entity instanceof ServerPlayerEntity ridingPlayer) {
            ridingPlayer.networkHandler.sendPacket(passengersPacket);
        }

        for (Entity passenger : entity.getPassengerList()) {
            if (passenger instanceof ServerPlayerEntity ridingPlayer) {
                ridingPlayer.networkHandler.sendPacket(passengersPacket);
            }
        }
    }
}
