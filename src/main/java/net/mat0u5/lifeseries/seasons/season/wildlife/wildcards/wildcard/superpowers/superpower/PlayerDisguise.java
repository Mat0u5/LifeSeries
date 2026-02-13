package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
//? if >= 1.21.9 {
import net.mat0u5.lifeseries.mixin.MannequinAccessor;
import net.minecraft.world.entity.decoration.Mannequin;
//?}

public class PlayerDisguise extends ToggleableSuperpower {

    private String copiedPlayerName = "";
    private String copiedPlayerUUID = "";

    public PlayerDisguise(ServerPlayer player) {
        super(player);
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.PLAYER_DISGUISE;
    }

    @Override
    public int deactivateCooldownMillis() {
        return 10000;
    }

    @Override
    public void activate() {
        ServerPlayer player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        if (lookingAt != null)  {
            //? if >= 1.21.9 {
            if (lookingAt instanceof Mannequin mannequin && mannequin instanceof MannequinAccessor mannequinAccessor && mannequin.tickCount < 0) {
                ServerPlayer lookingAtPlayer = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().partialProfile().id());
                if (lookingAtPlayer != null) {
                    lookingAt = lookingAtPlayer;
                }
            }
            //?}
            if (lookingAt instanceof ServerPlayer lookingAtPlayer) {
                lookingAtPlayer = PlayerUtils.getPlayerOrProjection(lookingAtPlayer);
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
                    copiedPlayerUUID = lookingAtPlayer.getStringUUID();
                    copiedPlayerName = TextUtils.textToLegacyString(lookingAtPlayer.getDisplayName());
                    player.ls$playNotifySound(SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.MASTER, 0.3f, 1);
                    PlayerUtils.displayMessageToPlayer(player, ModifiableText.WILDLIFE_POWER_PLAYERDISGUISE.get(lookingAtPlayer), 65);
                    return;
                }
            }
        }

        if (copiedPlayerName.isEmpty() || copiedPlayerUUID.isEmpty()) {
            PlayerUtils.displayMessageToPlayer(player, Component.nullToEmpty("You are not looking at a player."), 65);
            return;
        }

        ServerLevel playerLevel = player.ls$getServerLevel();
        playerLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PUFFER_FISH_BLOW_UP, SoundSource.MASTER, 1, 1);
        Vec3 playerPos = player.position();
        playerLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                playerPos.x(), playerPos.y(), playerPos.z(),
                2, 0, 0, 0, 0
        );
        super.activate();
        sendDisguisePacket();
    }

    public void sendDisguisePacket() {
        if (!this.active) return;
        if (copiedPlayerName.isEmpty() || copiedPlayerUUID.isEmpty()) return;
        ServerPlayer player = getPlayer();
        if (player == null) return;
        NetworkHandlerServer.sendPlayerDisguise(player.getUUID().toString(), player.getName().getString(), copiedPlayerUUID, copiedPlayerName);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayer player = getPlayer();
        if (player == null) return;
        ServerLevel playerLevel = player.ls$getServerLevel();
        playerLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.MASTER, 1, 1);
        Vec3 playerPos = player.position();
        playerLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                playerPos.x(), playerPos.y(), playerPos.z(),
                2, 0, 0, 0, 0
        );
        NetworkHandlerServer.sendPlayerDisguise(player.getUUID().toString(), player.getName().getString(), "", "");
    }

    public void onTakeDamage() {
        deactivate();
    }
}
