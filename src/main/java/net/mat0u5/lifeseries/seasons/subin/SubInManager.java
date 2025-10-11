package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

//? if != 1.21.6
import net.minecraft.nbt.NbtCompound;
//? if >= 1.21.6 {
/*import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
*///?}

//? if >= 1.21.9 {
/*import net.minecraft.storage.NbtReadView;
*///?}

public class SubInManager {
    public static List<SubIn> subIns = new ArrayList<>();

    private static UUID getId(GameProfile profile) {
        return OtherUtils.profileId(profile);
    }
    private static String getName(GameProfile profile) {
        return OtherUtils.profileName(profile);
    }

    public static void addSubIn(ServerPlayerEntity player, GameProfile targetProfile) {
        Integer startingLives = livesManager.getPlayerLives(player);
        UUID playerUUID = player.getUuid();
        GameProfile playerProfile = player.getGameProfile();

        UUID targetProfileId = getId(targetProfile);
        for (SubIn subIn : new ArrayList<>(subIns)) {
            UUID substituterId = getId(subIn.substituter());
            UUID substituteeId = getId(subIn.target());

            if (substituterId.equals(targetProfileId) || substituteeId.equals(targetProfileId) || 
                    substituterId.equals(playerUUID) || substituteeId.equals(playerUUID)
            ) {
                removeSubIn(subIn);
            }
        }

        savePlayer(player);
        subIns.add(new SubIn(playerProfile, targetProfile, startingLives));
        loadPlayer(player);

        PlayerUtils.updatePlayerInventory(player);
        player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));

        Integer subInLives = livesManager.getScoreLives(getName(targetProfile));
        if (subInLives == null) {
            livesManager.resetPlayerLife(player);
        }
        else {
            livesManager.setPlayerLives(player, subInLives);
        }
    }

    public static void removeSubIn(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        for (SubIn subIn : new ArrayList<>(subIns)) {
            if (getId(subIn.substituter()).equals(playerUUID) || getId(subIn.target()).equals(playerUUID)) {
                removeSubIn(subIn);
            }
        }
    }

    private static void removeSubIn(SubIn subIn) {
        ServerPlayerEntity player1 = PlayerUtils.getPlayer(getId(subIn.substituter()));
        ServerPlayerEntity player2 = PlayerUtils.getPlayer(getId(subIn.target()));
        if (player1 != null) {
            player1.sendMessage(TextUtils.formatLoosely("§6You are no longer subbing in for {}", getName(subIn.target())));
        }
        if (player2 != null) {
            player2.sendMessage(TextUtils.formatLoosely("§6{} is no longer subbing in for you", getName(subIn.substituter())));
        }

        savePlayer(player1);
        subIns.remove(subIn);
        loadPlayer(player1);
        loadPlayer(player2);
        if (player1 != null) {
            Integer startingLives = subIn.startingLives();
            livesManager.setPlayerLives(player1, startingLives);
        }
    }

    public static void savePlayer(ServerPlayerEntity player) {
        if (player == null || server == null) return;

        if (server.getPlayerManager() instanceof IPlayerManager iPlayerManager) {
            iPlayerManager.ls$savePlayerData(player);
        }
    }

    public static void loadPlayer(ServerPlayerEntity player) {
        if (player == null || server == null) return;

        if (server.getPlayerManager() instanceof IPlayerManager iPlayerManager) {
            //? if < 1.21.6 {
            Optional<NbtCompound> data = iPlayerManager.ls$getSaveHandler().loadPlayerData(player);
            data.ifPresent(nbt -> {
                player.readNbt(nbt);
                PlayerUtils.teleport(player, WorldUtils.getEntityPos(player));
            });
            //?} else if <= 1.21.6 {
            /*Optional<ReadView> data = iPlayerManager.ls$getSaveHandler().loadPlayerData(player, ErrorReporter.EMPTY);
            data.ifPresent(nbt -> {
                player.readData(nbt);
                PlayerUtils.teleport(player, WorldUtils.getEntityPos(player));
            });
            *///?} else {
            /*Optional<NbtCompound> data = iPlayerManager.ls$getSaveHandler().loadPlayerData(player.getPlayerConfigEntry());
            Optional<ReadView> optional = data.map(playerData -> NbtReadView.create(ErrorReporter.EMPTY, server.getRegistryManager(), playerData));
            optional.ifPresent(readView -> {
                player.readData(readView);
                PlayerUtils.teleport(player, WorldUtils.getEntityPos(player));
            });
            *///?}
        }
    }

    public static boolean isSubbingIn(UUID uuid) {
        if (uuid == null) return false;
        for (SubIn subIn : subIns) {
            if (getId(subIn.substituter()).equals(uuid)) return true;
        }
        return false;
    }

    public static boolean isBeingSubstituted(UUID uuid) {
        if (uuid == null) return false;
        for (SubIn subIn : subIns) {
            if (getId(subIn.target()).equals(uuid)) return true;
        }
        return false;
    }

    public static GameProfile getSubstitutedPlayer(UUID uuid) {
        if (uuid == null) return null;
        for (SubIn subIn : subIns) {
            if (getId(subIn.substituter()).equals(uuid)) return subIn.target();
        }
        return null;
    }

    public static GameProfile getSubstitutingPlayer(UUID uuid) {
        if (uuid == null) return null;
        for (SubIn subIn : subIns) {
            if (getId(subIn.target()).equals(uuid)) return subIn.substituter();
        }
        return null;
    }

    public static UUID getSubstitutedPlayerUUID(UUID uuid) {
        GameProfile profile = getSubstitutedPlayer(uuid);
        if (profile == null) return null;
        return getId(profile);
    }

    public static UUID getSubstitutingPlayerUUID(UUID uuid) {
        GameProfile profile = getSubstitutingPlayer(uuid);
        if (profile == null) return null;
        return getId(profile);
    }

    public record SubIn(GameProfile substituter, GameProfile target, Integer startingLives) {
    }
}
