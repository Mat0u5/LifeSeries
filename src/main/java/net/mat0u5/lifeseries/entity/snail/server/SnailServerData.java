package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.entity.PlayerBoundEntity;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;

import java.util.UUID;
import static net.mat0u5.lifeseries.Main.livesManager;

@SuppressWarnings("resource")
public class SnailServerData implements PlayerBoundEntity {
    public static final RegistryKey<DamageType> SNAIL_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Main.MOD_ID, "snail"));
    public final Snail snail;

    public SnailServerData(Snail snail) {
        this.snail = snail;
    }

    private UUID boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayerEntity player) {
        resetAirPacket();
        updateSnailName();
        updateSkin(player);
        snail.setBoundPlayerDead(player.ls$isDead());
    }

    @Override
    public void setBoundPlayerUUID(UUID uuid) {
        boundPlayerUUID = uuid;
    }

    @Override
    public UUID getBoundPlayerUUID() {
        return boundPlayerUUID;
    }

    @Override
    public boolean shouldPathfind() {
        if (snail.getSnailWorld().isClient()) return false;
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return false;
        if (player.isCreative()) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;
        if (Events.joiningPlayers.contains(player.getUuid())) return false;
        if (player.isSpectator() && !SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return false;
        return true;
    }

    public int dontAttackFor = 0;
    public int despawnPlayerChecks = 0;
    public Text snailName;
    private int lastAir = 0;

    public int getJumpRangeSquared() {
        if (isNerfed()) return 9;
        return Snail.JUMP_RANGE_SQUARED;
    }

    public void updateSnailName() {
        if (!hasBoundPlayer()) return;
        snailName = Text.of(Snails.getSnailName(getBoundPlayer()));
    }

    public void tick() {
        if (snail.getSnailWorld().isClient()) return;
        snail.pathfinding.tick();
        if (despawnChecks()) return;
        ServerPlayerEntity boundPlayer = getBoundPlayer();
        LivingEntity boundEntity = getBoundEntity();

        if (dontAttackFor > 0) dontAttackFor--;

        if (snail.age % 20 == 0) {
            updateSnailName();
            snail.setBoundPlayerDead(boundPlayer.ls$isDead());
        }

        if (boundEntity != null && shouldPathfind() && snail.getBoundingBox().expand(0.05).intersects(boundEntity.getBoundingBox())) {
            killBoundEntity(boundEntity);
        }

        if (boundPlayer != null && boundEntity != null) {
            if (Snail.SHOULD_DROWN_PLAYER && !snail.isFromTrivia()) {
                int currentAir = snail.getAir();
                if (boundEntity.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                    currentAir = snail.getMaxAir();
                }
                if (lastAir != currentAir) {
                    lastAir = currentAir;
                    sendAirPacket(boundPlayer, currentAir);
                }
                if (currentAir == 0) damageFromDrowning(boundEntity);
            }
        }

        handleHighVelocity();
        chunkLoading();
        snail.sounds.playSounds();
        snail.clearStatusEffects();
    }

    public boolean despawnChecks() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null || (player.isSpectator() && player.ls$isDead())) {
            despawnPlayerChecks++;
        }
        else {
            despawnPlayerChecks = 0;
        }

        if (despawnPlayerChecks > 200) {
            despawn();
            return true;
        }
        if (snail.age % 10 == 0) {
            if (!snail.isFromTrivia()) {
                if (!Snails.snails.containsValue(snail) || !WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
                    despawn();
                    return true;
                }
            }
            else {
                if (!WildcardManager.isActiveWildcard(Wildcards.TRIVIA) || snail.age >= 36000) {
                    despawn();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNerfed() {
        if (snail.isFromTrivia()) return true;
        if (WildcardManager.FINALE) return true;
        return Wildcard.isFinale();
    }

    public void setFromTrivia() {
        snail.setFromTrivia(true);
        dontAttackFor = 100;
        snail.sounds.playAttackSound();
    }

    public void chunkLoading() {
        if (snail.getSnailWorld() instanceof ServerWorld world) {
            //? if <= 1.21.4 {
            world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(snail.getBlockPos()), 2, snail.getBlockPos());
            //?} else {
            /*world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(snail.getBlockPos()), 2);
             *///?}
        }
    }

    public void despawn() {
        resetAirPacket();
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        snail.pathfinding.killPathFinders();
        if (snail.getSnailWorld() instanceof ServerWorld world) {
            //? if <= 1.21 {
            snail.kill();
            //?} else {
            /*snail.kill(world);
             *///?}
        }
        snail.discard();
    }

    public void resetAirPacket() {
        ServerPlayerEntity player = getBoundPlayer();
        if (player != null) {
            sendAirPacket(player, 300);
        }
    }

    public void sendAirPacket(ServerPlayerEntity player, int amount) {
        NetworkHandlerServer.sendNumberPacket(player, PacketNames.SNAIL_AIR, amount);
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

    public void killBoundEntity(Entity entity) {
        World world = entity.ls$getEntityWorld();
        if (world instanceof ServerWorld serverWorld) {
            if (entity instanceof ServerPlayerEntity player) {
                player.setAttacker(snail);
            }
            //? if <=1.21 {
            DamageSource damageSource = new DamageSource(serverWorld.getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE).entryOf(SNAIL_DAMAGE));
            entity.damage(damageSource, 1000);
            //?} else {
            /*DamageSource damageSource = new DamageSource(serverWorld.getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SNAIL_DAMAGE));
            entity.damage(serverWorld, damageSource, 1000);
            *///?}
        }
    }

    public void damageFromDrowning(Entity entity) {
        if (!entity.isAlive()) return;
        World world = entity.ls$getEntityWorld();
        if (world instanceof ServerWorld serverWorld) {
            if (entity instanceof ServerPlayerEntity player) {
                player.setAttacker(snail);
            }
            //? if <=1.21 {
            DamageSource damageSource = new DamageSource(serverWorld.getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.DROWN));
            entity.damage(damageSource, 2);
            //?} else {
            /*DamageSource damageSource = new DamageSource(serverWorld.getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN));
            entity.damage(serverWorld, damageSource, 2);
            *///?}
            if (!entity.isAlive() && entity instanceof ServerPlayerEntity) {
                despawn();
            }
        }
    }

    public Text getDefaultName() {
        if (snail.isFromTrivia()) return Text.of("VHSnail");
        if (snailName == null) return snail.getType().getName();
        if (snailName.getString().isEmpty()) return snail.getType().getName();
        return snailName;
    }


    public void updateSkin(PlayerEntity player) {
        if (player == null) return;
        String skinName = player.getNameForScoreboard().toLowerCase();
        if (SubInManager.isSubbingIn(player.getUuid())) {
            skinName = OtherUtils.profileName(SubInManager.getSubstitutedPlayer(player.getUuid())).toLowerCase();
        }
        snail.setSkinName(skinName);
    }
}
