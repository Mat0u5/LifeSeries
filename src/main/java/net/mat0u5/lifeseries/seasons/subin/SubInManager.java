package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

//? if <= 1.21.5
//import net.minecraft.nbt.CompoundTag;
//? if >= 1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;
//?}
//? if >= 1.21.9 {
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.nbt.CompoundTag;
//?}

public class SubInManager {
    public static List<SubIn> subIns = new ArrayList<>();
    public static boolean CHANGE_NAME = true;
    public static boolean CHANGE_SKIN = true;

    private static UUID getId(GameProfile profile) {
        return OtherUtils.profileId(profile);
    }
    private static String getName(GameProfile profile) {
        return OtherUtils.profileName(profile);
    }

    public static void addSubIn(ServerPlayer player, GameProfile targetProfile) {
        Integer startingLives = player.ls$getLives();
        UUID playerUUID = player.getUUID();
        GameProfile playerProfile = player.getGameProfile();

        UUID targetProfileId = getId(targetProfile);
        String targetProfileName = getName(targetProfile);
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
        player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));

        Integer subInLives = livesManager.getScoreLives(targetProfileName);
        if (subInLives == null) {
            livesManager.resetPlayerLife(player);
        }
        else {
            player.ls$setLives(subInLives);
        }

        if (!CHANGE_SKIN && !CHANGE_NAME) return;
        ProfileManager.ProfileChange skinChange = CHANGE_SKIN ? ProfileManager.ProfileChange.SET.withInfo(targetProfileName) : ProfileManager.ProfileChange.ORIGINAL;
        ProfileManager.ProfileChange nameChange = CHANGE_NAME ? ProfileManager.ProfileChange.SET.withInfo(targetProfileName) : ProfileManager.ProfileChange.ORIGINAL;
        ProfileManager.modifyProfile(player, skinChange, nameChange);
    }

    public static void reload() {
        CHANGE_SKIN = seasonConfig.SUBIN_CHANGE_SKIN.get(seasonConfig);
        CHANGE_NAME = seasonConfig.SUBIN_CHANGE_USERNAME.get(seasonConfig);
        for (SubIn subIn : subIns) {
            ServerPlayer player = PlayerUtils.getPlayer(getId(subIn.substituter()));
            reloadPlayerProfile(player);
        }
    }

    public static void reloadPlayerProfile(ServerPlayer player) {
        if (player == null) return;

        if ((ProfileManager.hasChangedSkin(player) == CHANGE_SKIN) && (ProfileManager.hasChangedName(player) == CHANGE_NAME)) {
            return;
        }

        String targetProfileName = getName(getSubstitutedPlayer(player.getUUID()));
        if (targetProfileName == null) return;

        ProfileManager.ProfileChange skinChange = CHANGE_SKIN ? ProfileManager.ProfileChange.SET.withInfo(targetProfileName) : ProfileManager.ProfileChange.ORIGINAL;
        ProfileManager.ProfileChange nameChange = CHANGE_NAME ? ProfileManager.ProfileChange.SET.withInfo(targetProfileName) : ProfileManager.ProfileChange.ORIGINAL;
        ProfileManager.modifyProfile(player, skinChange, nameChange);
    }

    public static void removeSubIn(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        for (SubIn subIn : new ArrayList<>(subIns)) {
            if (getId(subIn.substituter()).equals(playerUUID) || getId(subIn.target()).equals(playerUUID)) {
                removeSubIn(subIn);
            }
        }
    }

    private static void removeSubIn(SubIn subIn) {
        ServerPlayer player1 = PlayerUtils.getPlayer(getId(subIn.substituter()));
        ServerPlayer player2 = PlayerUtils.getPlayer(getId(subIn.target()));

        ProfileManager.resetPlayer(player1).thenRun(() -> {
            if (player1 != null) {
                player1.sendSystemMessage(TextUtils.formatLoosely("§6You are no longer subbing in for {}", getName(subIn.target())));
            }
            if (player2 != null) {
                player2.sendSystemMessage(TextUtils.formatLoosely("§6{} is no longer subbing in for you", getName(subIn.substituter())));
            }

            savePlayer(player1);
            subIns.remove(subIn);
            loadPlayer(player1);
            loadPlayer(player2);
            if (player1 != null) {
                Integer startingLives = subIn.startingLives();
                player1.ls$setLives(startingLives);
            }
        });
    }

    public static void savePlayer(ServerPlayer player) {
        if (player == null || server == null) return;

        if (server.getPlayerList() instanceof IPlayerManager iPlayerManager) {
            iPlayerManager.ls$savePlayerData(player);
        }
    }

    public static void loadPlayer(ServerPlayer player) {
        if (player == null || server == null) return;

        if (server.getPlayerList() instanceof IPlayerManager iPlayerManager) {
            //? if <= 1.20.3 {
            /*CompoundTag nbt = iPlayerManager.ls$getSaveHandler().load(player);
            if (nbt != null) {
                player.load(nbt);
                PlayerUtils.teleport(player, player.position());
            }
            *///?} else if < 1.21.6 {
            /*Optional<CompoundTag> data = iPlayerManager.ls$getSaveHandler().load(player);
            data.ifPresent(nbt -> {
                player.load(nbt);
                PlayerUtils.teleport(player, player.position());
            });
            *///?} else if <= 1.21.6 {
            /*Optional<ValueInput> data = iPlayerManager.ls$getSaveHandler().load(player, ProblemReporter.DISCARDING);
            data.ifPresent(nbt -> {
                player.load(nbt);
                PlayerUtils.teleport(player, player.position());
            });
            *///?} else {
            Optional<CompoundTag> data = iPlayerManager.ls$getSaveHandler().load(player.nameAndId());
            Optional<ValueInput> optional = data.map(playerData -> TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), playerData));
            optional.ifPresent(readView -> {
                player.load(readView);
                PlayerUtils.teleport(player, player.position());
            });
            //?}
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
