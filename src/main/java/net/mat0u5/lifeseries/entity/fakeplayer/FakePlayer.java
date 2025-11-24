package net.mat0u5.lifeseries.entity.fakeplayer;

/*
 * This file includes code from the Fabric Carpet project: https://github.com/gnembon/fabric-carpet
 *
 * Used and modified under the MIT License.
 */

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

//?if > 1.20 {
/*import net.minecraft.server.level.ClientInformation;
*///?}

//? if <= 1.21.6 {

//?if <= 1.20 {
import java.util.concurrent.atomic.AtomicReference;
//?} else {
/*import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.network.CommonListenerCookie;
*///?}
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
//?}

@SuppressWarnings("EntityConstructor")
public class FakePlayer extends ServerPlayer {
    //?if <= 1.20 {
    private FakePlayer(MinecraftServer server, ServerLevel levelIn, GameProfile profile) {
        super(server, levelIn, profile);
    }
    //?} else {
    /*private FakePlayer(MinecraftServer server, ServerLevel levelIn, GameProfile profile, ClientInformation cli) {
        super(server, levelIn, profile, cli);
    }
    *///?}
    //? if <= 1.21.6 {
    private static final Set<String> spawning = new HashSet<>();
    public Runnable fixStartingPosition = () -> {};
    public UUID shadow;

    public static CompletableFuture<FakePlayer> createFake(
            String username, MinecraftServer server, Vec3 pos, double yaw, double pitch,
            ResourceKey<Level> dimensionId, GameType gamemode, boolean flying, Inventory inv,
            UUID shadow) {
        ServerLevel levelIn = server.getLevel(dimensionId);
        GameProfileCache.setUsesAuthentication(false);
        GameProfile gameprofile = null;
        try {
            if (server.getProfileCache() != null) {
                Optional<GameProfile> opt = server.getProfileCache().get(username);
                if (opt.isPresent()) {
                    gameprofile = opt.get();
                }
            }
        } catch(Exception ignored) {}
        finally {
            GameProfileCache.setUsesAuthentication(server.isDedicatedServer() && server.isPublished());
        }
        if (gameprofile == null)
        {
            gameprofile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(username), username);
        }
        GameProfile finalGP = gameprofile;

        String name = gameprofile.getName();
        spawning.add(name);

        CompletableFuture<FakePlayer> future = new CompletableFuture<>();

        fetchGameProfile(server, name).whenCompleteAsync((profile, throwable) -> {
            spawning.remove(name);
            if (throwable != null) {
                future.completeExceptionally(throwable);
                return;
            }
            GameProfile current = finalGP;
            if (profile.isPresent()) current = profile.get();

            //?if <= 1.20 {
            FakePlayer instance = new FakePlayer(server, levelIn, current);
            //?} else {
            /*FakePlayer instance = new FakePlayer(server, levelIn, current, ClientInformation.createDefault());
            *///?}
            //? if <= 1.21.4 {
            instance.fixStartingPosition = () -> instance.moveTo(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            //?} else {
            /*instance.fixStartingPosition = () -> instance.snapTo(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            *///?}
            FakeClientConnection connection = new FakeClientConnection(PacketFlow.SERVERBOUND);
            //?if <= 1.20 {
            server.getPlayerList().placeNewPlayer(connection, instance);
            //?} else {
            /*CommonListenerCookie data =  new CommonListenerCookie(current, 0, instance.clientInformation(), true);
            server.getPlayerList().placeNewPlayer(connection, instance, data);
            *///?}
            PlayerUtils.teleport(instance, levelIn, pos, (float) yaw, (float) pitch);
            instance.setHealth(20.0F);
            instance.unsetRemoved();
            instance.setGameMode(gamemode);
            server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(instance, (byte) (instance.getYRot() * 256 / 360)));
            instance.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f);
            instance.getAbilities().flying = flying;

            instance.getInventory().replaceWith(inv);
            instance.getInventory().setChanged();
            instance.getInventory().tick();
            instance.containerMenu.broadcastChanges();

            instance.shadow = shadow;
            instance.removeAllEffects();
            instance.setSharedFlagOnFire(false);
            instance.setRemainingFireTicks(0);
            //instance.setCustomName(displayName);
            future.complete(instance);
        }, server);

        return future;
    }

    private static CompletableFuture<Optional<GameProfile>> fetchGameProfile(MinecraftServer server, final String name) {
        //?if <= 1.20 {
        GameProfile gameprofile;
        try {
            gameprofile = server.getProfileCache().get(name).orElse(null);
        }
        finally {
            GameProfileCache.setUsesAuthentication(server.isDedicatedServer() && server.usesAuthentication());
        }
        if (gameprofile == null)
        {
            gameprofile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(name), name);
        }
        if (gameprofile.getProperties().containsKey("textures"))
        {
            AtomicReference<GameProfile> result = new AtomicReference<>();
            SkullBlockEntity.updateGameprofile(gameprofile, result::set);
            gameprofile = result.get();
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(gameprofile));
        //?} else {
        /*return SkullBlockEntity.fetchGameProfile(name);
        *///?}
    }

    @Override
    public String getIpAddress() {
        return "127.0.0.1";
    }

    @Override
    public boolean allowsListing() {
        return false;
    }
    @Override
    public void tick() {
        if (tickCount % 20 == 0) {
            boolean triggered = false;
            if (shadow != null) {
                ServerPlayer player = PlayerUtils.getPlayer(shadow);
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
                //?if <= 1.20 {
                connection.onDisconnect(Component.empty());
                //?} else {
                /*connection.onDisconnect(new DisconnectionDetails(Component.empty()));
                *///?}
            }
        }
        //
        if (tickCount % 10 == 0)
        {
            this.connection.resetPosition();
        }
        try
        {
            super.tick();
            doTick();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
            //? if <= 1.21 {
    public boolean hurt(DamageSource source, float amount) {
     //?} else {
    /*public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        *///?}
        if (shadow != null) {
            ServerPlayer player = PlayerUtils.getPlayer(shadow);
            if (player != null) {
                if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
                    if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection projection) {
                        //? if <= 1.21 {
                        projection.onDamageClone(source, amount);
                         //?} else {
                        /*projection.onDamageClone(level, source, amount);
                        *///?}
                    }
                }
            }
        }
        //? if <= 1.21 {
        return super.hurt(source, amount);
         //?} else {
        /*return super.hurtServer(level, source, amount);
        *///?}

    }
    //?}
}
