package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class SnailServerData {
    public static final RegistryKey<DamageType> SNAIL_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Main.MOD_ID, "snail"));
    public final Snail snail;

    public SnailServerData(Snail snail) {
        this.snail = snail;
    }

    public UUID boundPlayerUUID;
    public boolean navigationInit = false;
    public int dontAttackFor = 0;
    public int nullPlayerChecks = 0;
    public Text snailName;
    private int lastAir = 0;

    public int getJumpRangeSquared() {
        if (isNerfed()) return 9;
        return Snail.JUMP_RANGE_SQUARED;
    }

    public void setBoundPlayer(PlayerEntity player) {
        if (player == null) return;
        sendAirPacket();
        boundPlayerUUID = player.getUuid();
        updateSnailName();
    }

    public void updateSnailName() {
        if (getBoundPlayer() == null) return;
        snailName = Text.of(Snails.getSnailName(getBoundPlayer()));
    }

    public boolean tick() {
        if (snail.isPaused()) {
            snail.getNavigation().stop();
            return false;
        }

        if (dontAttackFor > 0) {
            dontAttackFor--;
        }

        if (nullPlayerChecks > 200 && !snail.isFromTrivia()) {
            despawn();
        }

        if (snail.age % 20 == 0) {
            updateSnailName();
        }

        if (snail.age % 50 == 0) {
            if (!snail.isFromTrivia()) {
                if (!Snails.snails.containsValue(snail) || !WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
                    despawn();
                }
            }
            else {
                if (!WildcardManager.isActiveWildcard(Wildcards.TRIVIA) || snail.age >= 36000) {
                    despawn();
                }
            }
        }
        PlayerEntity boundPlayer = getBoundPlayer();
        if (boundPlayer != null) {
            if (snail.getBoundingBox().expand(0.05).intersects(boundPlayer.getBoundingBox())) {
                killBoundPlayer();
            }
            if (snail.age % 100 == 0 || !navigationInit) {
                navigationInit = true;
                snail.pathfinding.updateMoveControl();
                snail.pathfinding.updateNavigation();
            }
            else if (snail.age % 21 == 0) {
                snail.pathfinding.updateMovementSpeed();
            }
            else if (snail.age % 5 == 0) {
                snail.pathfinding.updateNavigationTarget();
            }
        }

        if (Snail.SHOULD_DROWN_PLAYER && !snail.isFromTrivia() && getBoundPlayer() != null) {
            int currentAir = snail.getAir();
            if (getBoundPlayer().hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                currentAir = snail.getMaxAir();
            }
            if (lastAir != currentAir) {
                lastAir = currentAir;
                sendAirPacket(currentAir);
            }
            if (currentAir == 0) damageFromDrowning();
        }

        handleHighVelocity();
        snail.pathfinding.updatePathFinders();
        chunkLoading();
        snail.sounds.playSounds();
        snail.clearStatusEffects();
        return true;
    }

    public boolean isNerfed() {
        if (snail.isFromTrivia()) return true;
        return WildcardManager.isActiveWildcard(Wildcards.CALLBACK);
    }

    public void setFromTrivia() {
        snail.setFromTrivia(true);
        dontAttackFor = 100;
        snail.sounds.playAttackSound();
    }

    public void chunkLoading() {
        if (snail.getWorldEntity() instanceof ServerWorld world) {
            addTicket(world);
        }
    }

    public void addTicket(ServerWorld world) {
        //? if <= 1.21.4 {
        world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(snail.getBlockPos()), 2, snail.getBlockPos());
        //?} else {
        /*world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(snail.getBlockPos()), 2);
         *///?}
    }

    public void despawn() {
        sendAirPacket();
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        snail.pathfinding.killPathFinders();
        if (snail.getWorldEntity() instanceof ServerWorld world) {
            //? if <= 1.21 {
            snail.kill();
            //?} else {
            /*snail.kill(world);
             *///?}
        }
        snail.discard();
    }

    public void sendAirPacket() {
        sendAirPacket(300);
    }

    public void sendAirPacket(int amount) {
        NetworkHandlerServer.sendNumberPacket(getServerBoundPlayer(), PacketNames.SNAIL_AIR, amount);
    }

    public void handleHighVelocity() {
        Vec3d velocity = snail.getVelocity();
        if (velocity.y > 0.15) {
            snail.setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            snail.setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void killBoundPlayer() {
        if (snail.getWorldEntity().isClient()) return;
        ServerPlayerEntity player = getServerBoundPlayer();
        if (player == null) return;

        ServerWorld world = PlayerUtils.getServerWorld(player);

        //? if <=1.21 {
        DamageSource damageSource = new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE).entryOf(SNAIL_DAMAGE));
        player.setAttacker(snail);
        player.damage(damageSource, 1000);
        //?} else {
        /*DamageSource damageSource = new DamageSource(world.getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SNAIL_DAMAGE));
        player.setAttacker(snail);
        player.damage(world, damageSource, 1000);
        *///?}
    }

    public void damageFromDrowning() {
        if (snail.getWorldEntity().isClient()) return;
        ServerPlayerEntity player = getServerBoundPlayer();
        if (player == null) return;
        if (!player.isAlive()) return;
        ServerWorld world = PlayerUtils.getServerWorld(player);
        //? if <=1.21 {
        DamageSource damageSource = new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.DROWN));
        player.setAttacker(snail);
        player.damage(damageSource, 2);
        //?} else {
        /*DamageSource damageSource = new DamageSource(world.getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN));
        player.setAttacker(snail);
        player.damage(world, damageSource, 2);
        *///?}
        if (!player.isAlive()) {
            despawn();
        }
    }

    public Text getDefaultName() {
        if (snail.isFromTrivia()) return Text.of("VHSnail");
        if (snailName == null) return snail.getType().getName();
        if (snailName.getString().isEmpty()) return snail.getType().getName();
        return snailName;
    }

    @Nullable
    public PlayerEntity getBoundPlayer() {
        return getServerBoundPlayer();
    }

    @Nullable
    public ServerPlayerEntity getServerBoundPlayer() {
        if (snail.getWorldEntity().isClient()) return null;
        if (server == null) return null;
        ServerPlayerEntity player = PlayerUtils.getPlayer(boundPlayerUUID);
        if (player == null || (player.isSpectator() && livesManager.isDead(player))) {
            nullPlayerChecks++;
            return null;
        }

        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AstralProjection astralProjection) {
                //? if <= 1.21.6 {
                return astralProjection.clone;
                //?} else {
                /*return null; //TODO
                *///?}
            }
        }

        nullPlayerChecks = 0;
        if (player.isCreative()) return null;
        if (player.isSpectator()) return null;
        if (livesManager.isDead(player)) return null;
        if (Events.joiningPlayers.contains(player.getUuid())) return null;
        return player;
    }

    @Nullable
    public PlayerEntity getActualBoundPlayer() {
        if (snail.getWorldEntity().isClient()) return null;
        if (server == null) return null;
        return PlayerUtils.getPlayer(boundPlayerUUID);
    }
}
