package net.mat0u5.lifeseries.entity.snail.server;

import net.mat0u5.lifeseries.entity.PlayerBoundEntity;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("resource")
public class SnailServerData implements PlayerBoundEntity {
    public static final ResourceKey<DamageType> SNAIL_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, IdentifierHelper.mod("snail"));
    public final Snail snail;

    public SnailServerData(Snail snail) {
        this.snail = snail;
    }

    private UUID boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayer player) {
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
        if (snail.level().isClientSide()) return false;
        ServerPlayer player = getBoundPlayer();
        if (player == null) return false;
        if (player.isCreative()) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;
        if (Events.joiningPlayers.contains(player.getUUID())) return false;
        if (player.isSpectator() && !SuperpowersWildcard.hasActivatedPower(player, Superpowers.ASTRAL_PROJECTION)) return false;
        return true;
    }

    public int dontAttackFor = 0;
    public int despawnPlayerChecks = 0;
    public Component snailName;
    private int lastAir = 0;

    public int getJumpRangeSquared() {
        if (isNerfed()) return 9;
        return Snail.JUMP_RANGE_SQUARED;
    }

    public void updateSnailName() {
        if (!hasBoundPlayer()) return;
        snailName = Component.nullToEmpty(Snails.getSnailName(getBoundPlayer()));
    }

    public void tick() {
        if (snail.level().isClientSide()) return;
        snail.pathfinding.tick();
        if (despawnChecks()) return;
        ServerPlayer boundPlayer = getBoundPlayer();
        LivingEntity boundEntity = getBoundEntity();

        if (dontAttackFor > 0) dontAttackFor--;

        if (snail.tickCount % 20 == 0) {
            updateSnailName();
            if (boundPlayer != null) {
                snail.setBoundPlayerDead(boundPlayer.ls$isDead());
            }
        }

        if (boundEntity != null && shouldPathfind() && snail.getBoundingBox().inflate(0.05).intersects(boundEntity.getBoundingBox())) {
            killBoundEntity(boundEntity);
        }

        if (boundPlayer != null && boundEntity != null) {
            if (Snail.SHOULD_DROWN_PLAYER && !snail.isFromTrivia()) {
                int currentAir = snail.getAirSupply();
                if (boundEntity.hasEffect(MobEffects.WATER_BREATHING)) {
                    currentAir = snail.getMaxAirSupply();
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
        if (!Snail.ALLOW_POTION_EFFECTS) {
            snail.removeAllEffects();
        }
    }

    public boolean despawnChecks() {
        ServerPlayer player = getBoundPlayer();
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
        if (snail.tickCount % 10 == 0) {
            if (!snail.isFromTrivia()) {
                if (!Snails.snails.containsValue(snail) || !WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
                    despawn();
                    return true;
                }
            }
            else {
                if (snail.tickCount >= 36000) {
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
        if (snail.level() instanceof ServerLevel level) {
            //? if <= 1.21.4 {
            /*level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(snail.blockPosition()), 2, snail.blockPosition());
            *///?} else if <= 1.21.11 {
            level.getChunkSource().addTicketWithRadius(TicketType.PORTAL, LevelUtils.chunkPosFromBlockPos(snail.blockPosition()), 2);
            //?}
        }
    }

    public void despawn() {
        resetAirPacket();
        if (boundPlayerUUID != null) {
            TriviaWildcard.bots.remove(boundPlayerUUID);
        }
        snail.pathfinding.cleanup();

        if (snail.level() instanceof ServerLevel level) {
            //? if <= 1.21 {
            /*snail.kill();
            *///?} else {
            snail.kill(level);
             //?}
        }
        snail.discard();
    }

    public void resetAirPacket() {
        ServerPlayer player = getBoundPlayer();
        if (player != null) {
            sendAirPacket(player, 300);
        }
    }

    public void sendAirPacket(ServerPlayer player, int amount) {
        SimplePackets.SNAIL_AIR.target(player).sendToClient(amount);
    }

    public void handleHighVelocity() {
        Vec3 velocity = snail.getDeltaMovement();
        if (velocity.y > 0.15) {
            snail.setDeltaMovement(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            snail.setDeltaMovement(velocity.x,-0.15,velocity.z);
        }
    }

    public void killBoundEntity(Entity entity) {
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof ServerPlayer player) {
                player.setLastHurtByMob(snail);
            }
            //? if <=1.21 {
            /*DamageSource damageSource = new DamageSource(serverLevel.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SNAIL_DAMAGE));
            entity.hurt(damageSource, 1000);
            *///?} else {
            DamageSource damageSource = new DamageSource(serverLevel.registryAccess()
                    .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SNAIL_DAMAGE));
            entity.hurtServer(serverLevel, damageSource, 1000);
            //?}
        }
    }

    public void damageFromDrowning(Entity entity) {
        if (!entity.isAlive()) return;
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof ServerPlayer player) {
                player.setLastHurtByMob(snail);
            }
            //? if <=1.21 {
            /*DamageSource damageSource = new DamageSource(serverLevel.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.DROWN));
            entity.hurt(damageSource, 2);
            *///?} else {
            DamageSource damageSource = new DamageSource(serverLevel.registryAccess()
                    .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN));
            entity.hurtServer(serverLevel, damageSource, 2);
            //?}
            if (!entity.isAlive() && entity instanceof ServerPlayer) {
                despawn();
            }
        }
    }

    public Component getDefaultName() {
        if (snail.isFromTrivia()) return Component.nullToEmpty("VHSnail");
        if (snailName == null) return snail.getType().getDescription();
        if (snailName.getString().isEmpty()) return snail.getType().getDescription();
        return snailName;
    }


    public void updateSkin(Player player) {
        if (player == null) return;
        String skinName = player.getScoreboardName().toLowerCase(Locale.ROOT);
        if (SubInManager.isSubbingIn(player.getUUID())) {
            skinName = OtherUtils.profileName(SubInManager.getSubstitutedPlayer(player.getUUID())).toLowerCase(Locale.ROOT);
        }
        snail.setSkinName(skinName);
    }
}
