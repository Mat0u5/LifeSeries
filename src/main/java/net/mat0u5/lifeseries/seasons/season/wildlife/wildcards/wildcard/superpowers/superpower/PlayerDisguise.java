package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

//? if >= 1.21.9 {
/*import net.minecraft.entity.decoration.MannequinEntity;
import net.mat0u5.lifeseries.mixin.MannequinEntityAccessor;
*///?}

public class PlayerDisguise extends ToggleableSuperpower {

    private String copiedPlayerName = "";
    private String copiedPlayerUUID = "";

    public PlayerDisguise(ServerPlayerEntity player) {
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
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        Entity lookingAt = PlayerUtils.getEntityLookingAt(player, 50);
        if (lookingAt != null)  {
            //? if >= 1.21.9 {
            /*if (lookingAt instanceof MannequinEntity mannequin && mannequin instanceof MannequinEntityAccessor mannequinAccessor && mannequin.age < 0) {
                ServerPlayerEntity lookingAtPlayer = PlayerUtils.getPlayer(mannequinAccessor.ls$getMannequinProfile().getGameProfile().id());
                if (lookingAtPlayer != null) {
                    lookingAt = lookingAtPlayer;
                }
            }
            *///?}
            if (lookingAt instanceof ServerPlayerEntity lookingAtPlayer) {
                lookingAtPlayer = PlayerUtils.getPlayerOrProjection(lookingAtPlayer);
                if (!PlayerUtils.isFakePlayer(lookingAtPlayer)) {
                    copiedPlayerUUID = lookingAtPlayer.getUuidAsString();
                    copiedPlayerName = TextUtils.textToLegacyString(lookingAtPlayer.getStyledDisplayName());
                    player.playSoundToPlayer(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER, 0.3f, 1);
                    PlayerUtils.displayMessageToPlayer(player, TextUtils.format("Copied DNA of {}", lookingAtPlayer).append(Text.of(" — Press again to disguise")), 65);
                    return;
                }
            }
        }

        if (copiedPlayerName.isEmpty() || copiedPlayerUUID.isEmpty()) {
            PlayerUtils.displayMessageToPlayer(player, Text.of("You are not looking at a player."), 65);
            return;
        }

        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, SoundCategory.MASTER, 1, 1);
        Vec3d playerPos = player.ls$getEntityPos();
        playerWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                playerPos.getX(), playerPos.getY(), playerPos.getZ(),
                2, 0, 0, 0, 0
        );
        super.activate();
        sendDisguisePacket();
    }

    public void sendDisguisePacket() {
        if (!this.active) return;
        if (copiedPlayerName.isEmpty() || copiedPlayerUUID.isEmpty()) return;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        NetworkHandlerServer.sendPlayerDisguise(player.getUuid().toString(), player.getName().getString(), copiedPlayerUUID, copiedPlayerName);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        ServerWorld playerWorld = PlayerUtils.getServerWorld(player);
        playerWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.MASTER, 1, 1);
        Vec3d playerPos = player.ls$getEntityPos();
        playerWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                playerPos.getX(), playerPos.getY(), playerPos.getZ(),
                2, 0, 0, 0, 0
        );
        NetworkHandlerServer.sendPlayerDisguise(player.getUuid().toString(), player.getName().getString(), "", "");
    }

    public void onTakeDamage() {
        deactivate();
    }
}
