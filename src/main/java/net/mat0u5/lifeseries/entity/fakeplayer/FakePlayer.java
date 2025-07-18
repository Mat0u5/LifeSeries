package net.mat0u5.lifeseries.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;
/*
 * This file includes code from the Fabric Carpet project: https://github.com/gnembon/fabric-carpet
 *
 * Used and modified under the MIT License.
 */
@SuppressWarnings("EntityConstructor")
public class FakePlayer extends ServerPlayerEntity {
    private static final Set<String> spawning = new HashSet<>();
    public Runnable fixStartingPosition = () -> {};
    private UUID shadow;

    public static CompletableFuture<FakePlayer> createFake(
            String username, MinecraftServer server, Vec3d pos, double yaw, double pitch,
             RegistryKey<World> dimensionId, GameMode gamemode, boolean flying, PlayerInventory inv,
            UUID shadow) {
        ServerWorld worldIn = server.getWorld(dimensionId);
        UserCache.setUseRemote(false);
        GameProfile gameprofile = null;
        try {
            if (server.getUserCache() != null) {
                Optional<GameProfile> opt = server.getUserCache().findByName(username);
                if (opt.isPresent()) {
                    gameprofile = opt.get();
                }
            }
        } catch(Exception ignored) {}
        finally {
            UserCache.setUseRemote(server.isDedicated() && server.isRemote());
        }
        if (gameprofile == null)
        {
            gameprofile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
        }
        GameProfile finalGP = gameprofile;

        String name = gameprofile.getName();
        spawning.add(name);

        CompletableFuture<FakePlayer> future = new CompletableFuture<>();

        fetchGameProfile(name).whenCompleteAsync((profile, throwable) -> {
            spawning.remove(name);
            if (throwable != null) {
                future.completeExceptionally(throwable);
                return;
            }
            GameProfile current = finalGP;
            if (profile.isPresent()) current = profile.get();

            FakePlayer instance = new FakePlayer(server, worldIn, current, SyncedClientOptions.createDefault());
            instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            FakeClientConnection connection = new FakeClientConnection(NetworkSide.SERVERBOUND);
            ConnectedClientData data =  new ConnectedClientData(current, 0, instance.getClientOptions(), true);
            server.getPlayerManager().onPlayerConnect(connection, instance, data);
            //? if <= 1.21 {
            instance.teleport(worldIn, pos.x, pos.y, pos.z, EnumSet.noneOf(PositionFlag.class), (float) yaw, (float) pitch);
             //?} else {
            /*instance.teleport(worldIn, pos.x, pos.y, pos.z, EnumSet.noneOf(PositionFlag.class), (float) yaw, (float) pitch, false);
            *///?}
            instance.setHealth(20.0F);
            instance.unsetRemoved();
            instance.changeGameMode(gamemode);
            server.getPlayerManager().sendToAll(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.getYaw() * 256 / 360)));
            instance.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f);
            instance.getAbilities().flying = flying;

            instance.getInventory().clone(inv);
            instance.getInventory().markDirty();
            instance.getInventory().updateItems();
            instance.currentScreenHandler.sendContentUpdates();

            instance.shadow = shadow;
            instance.clearStatusEffects();
            instance.setOnFire(false);
            instance.setFireTicks(0);
            //instance.setCustomName(displayName);
            future.complete(instance);
        }, server);

        return future;
    }

    private static CompletableFuture<Optional<GameProfile>> fetchGameProfile(final String name) {
        return SkullBlockEntity.fetchProfileByName(name);
    }

    private FakePlayer(MinecraftServer server, ServerWorld worldIn, GameProfile profile, SyncedClientOptions cli) {
        super(server, worldIn, profile, cli);
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }

    @Override
    public boolean allowsServerListing() {
        return false;
    }
    @Override
    public void tick() {
        if (age % 20 == 0) {
            boolean triggered = false;
            if (shadow != null && getServer() != null) {
                ServerPlayerEntity player = getServer().getPlayerManager().getPlayer(shadow);
                if (player != null) {
                    if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
                            projection.clone = this;
                            triggered = true;
                        }
                    }
                }
            }
            if (!triggered) {
                networkHandler.onDisconnected(new DisconnectionInfo(Text.of("")));
            }
        }
        //
        if (age % 10 == 0)
        {
            this.networkHandler.syncWithPlayerPosition();
        }
        try
        {
            super.tick();
            playerTick();
        }
        catch (NullPointerException e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    @Override
    //? if <= 1.21 {
    public boolean damage(DamageSource source, float amount) {
     //?} else {
    /*public boolean damage(ServerWorld world, DamageSource source, float amount) {
    *///?}
        if (shadow != null && getServer() != null) {
            ServerPlayerEntity player = getServer().getPlayerManager().getPlayer(shadow);
            if (player != null) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                    if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
                        //? if <= 1.21 {
                        projection.onDamageClone(source, amount);
                         //?} else {
                        /*projection.onDamageClone(world, source, amount);
                        *///?}
                    }
                }
            }
        }
        //? if <= 1.21 {
        return super.damage(source, amount);
         //?} else {
        /*return super.damage(world, source, amount);
        *///?}

    }
}
