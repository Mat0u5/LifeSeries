package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.interfaces.IPlayerManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ProfileManager;
import net.mat0u5.lifeseries.utils.player.RealUUID;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.*;

//? if <= 1.21.5
//import net.minecraft.nbt.CompoundTag;
//? if >= 1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;
//?}
//? if >= 1.21.9 {
import net.minecraft.server.players.NameAndId;
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
        Integer startingLives = ((IPlayer) player).ls$getLives();
        UUID realUUID = ProfileManager.getRealUUID(player).get();
        GameProfile playerProfile = player.getGameProfile();

        UUID targetProfileId = getId(targetProfile);
        String targetProfileName = getName(targetProfile);
        for (SubIn subIn : new ArrayList<>(subIns)) {
            UUID substituterId = getId(subIn.substituter());
            UUID substituteeId = getId(subIn.target());

            if (substituterId.equals(targetProfileId) || substituteeId.equals(targetProfileId) || 
                    substituterId.equals(realUUID) || substituteeId.equals(realUUID)
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
            ((IPlayer) player).ls$setLives(subInLives);
        }

        if (CHANGE_SKIN  || CHANGE_NAME) {
            ProfileManager.ProfileChange skinChange = CHANGE_SKIN ? ProfileManager.ProfileChange.set(targetProfileName) : ProfileManager.ProfileChange.original();
            ProfileManager.ProfileChange nameChange = CHANGE_NAME ? ProfileManager.ProfileChange.set(targetProfileName) : ProfileManager.ProfileChange.original();
            ProfileManager.ProfileChange uuidChange = ProfileManager.ProfileChange.set(targetProfileId);
            ProfileManager.modifyProfile(player, skinChange, nameChange, uuidChange);
        }
        currentSeason.usernameChanged(player);
    }

    public static void reload() {
        CHANGE_SKIN = seasonConfig.SUBIN_CHANGE_SKIN.get();
        CHANGE_NAME = seasonConfig.SUBIN_CHANGE_USERNAME.get();
        for (SubIn subIn : subIns) {
            ServerPlayer player = PlayerUtils.getRealPlayer(getId(subIn.substituter()));
            reloadPlayerProfile(player);
            ServerPlayer target = PlayerUtils.getRealPlayer(getId(subIn.target()));
            reloadPlayerProfile(target);
        }
    }

    public static void reloadPlayerProfile(ServerPlayer player) {
        if (player == null) return;

        GameProfile targetProfile = getTargetPlayer(player);
        UUID targetProfileId = getId(targetProfile);
        String targetProfileName = getName(targetProfile);
        if (targetProfileId == null || targetProfileName == null) return;

        boolean skinFine = ProfileManager.hasChangedSkin(player) == CHANGE_SKIN;
        boolean nameFine = ProfileManager.hasChangedName(player) == CHANGE_NAME;
        boolean idFine = player.getUUID().equals(targetProfileId);

        if (skinFine && nameFine && idFine) {
            return;
        }

        ProfileManager.ProfileChange skinChange = CHANGE_SKIN ? ProfileManager.ProfileChange.set(targetProfileName) : ProfileManager.ProfileChange.original();
        ProfileManager.ProfileChange nameChange = CHANGE_NAME ? ProfileManager.ProfileChange.set(targetProfileName) : ProfileManager.ProfileChange.original();
        ProfileManager.ProfileChange uuidChange = ProfileManager.ProfileChange.set(targetProfileId);
        ProfileManager.modifyProfile(player, skinChange, nameChange, uuidChange);
    }

    public static void removeSubIn(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        UUID realPlayerUUID = ProfileManager.getRealUUID(player).get();
        for (SubIn subIn : new ArrayList<>(subIns)) {
            if (getId(subIn.substituter()).equals(playerUUID) || getId(subIn.target()).equals(playerUUID) || getId(subIn.substituter()).equals(realPlayerUUID) || getId(subIn.target()).equals(realPlayerUUID)) {
                removeSubIn(subIn);
            }
        }
    }

    private static void removeSubIn(SubIn subIn) {
        ServerPlayer player1 = PlayerUtils.getRealPlayer(getId(subIn.substituter()));
        ServerPlayer player2 = PlayerUtils.getRealPlayer(getId(subIn.target()));

        ProfileManager.resetPlayer(player1).thenRun(() -> {
            if (player1 != null) {
                ((IPlayer) player1).ls$message(ModifiableText.SUBIN_END_NOTIFY.get(getName(subIn.target())));
            }
            if (player2 != null) {
                ((IPlayer) player2).ls$message(ModifiableText.SUBIN_END_OTHER.get(getName(subIn.substituter())));
            }

            savePlayer(player1);
            subIns.remove(subIn);
            loadPlayer(player1);
            loadPlayer(player2);
            if (player1 != null) {
                Integer startingLives = subIn.startingLives();
                livesManager.setPlayerLives(player1, startingLives, true);
            }

            if (player1 != null) {
                currentSeason.usernameChanged(player1);
            }
            if (player2 != null) {
                currentSeason.usernameChanged(player2);
            }
        });
    }

    private static void savePlayer(ServerPlayer player) {
        if (player == null || server == null) return;

        if (server.getPlayerList() instanceof IPlayerManager iPlayerManager) {
            iPlayerManager.ls$savePlayerData(player);
        }
    }

    private static void loadPlayer(ServerPlayer player) {
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
            NameAndId nameAndId = player.nameAndId();
            GameProfile subbedProfile = getTargetPlayer(player);
            if (subbedProfile != null) {
                nameAndId = new NameAndId(subbedProfile);
            }
            Optional<CompoundTag> data = iPlayerManager.ls$getSaveHandler().load(nameAndId);
            Optional<ValueInput> optional = data.map(playerData -> TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), playerData));
            optional.ifPresent(readView -> {
                player.load(readView);
                PlayerUtils.teleport(player, player.position());
            });
            //?}
        }
    }

    public static boolean isSubbingIn(Player player) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        if (realUUID == null) return false;
        for (SubIn subIn : subIns) {
            if (getId(subIn.substituter()).equals(realUUID.get())) return true;
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

    public static GameProfile getTargetPlayer(Player player) {
        RealUUID realUUID = ProfileManager.getRealUUID(player);
        if (realUUID == null) return null;
        for (SubIn subIn : subIns) {
            if (getId(subIn.substituter()).equals(realUUID.get())) return subIn.target();
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

    public record SubIn(GameProfile substituter, GameProfile target, Integer startingLives) {
    }
}
