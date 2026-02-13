package net.mat0u5.lifeseries.seasons.subin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.9
import net.minecraft.server.players.NameAndId;

public class SubInCommands extends Command {
    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when you have selected a Season.");
    }

    public List<String> getAdminCommands() {
        return List.of("subin");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("subin")
                .requires(PermissionManager::isAdmin)
                    .then(literal("add")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("subin", StringArgumentType.string())
                                        .executes(context -> addSubIn(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "subin"))
                                        )
                                )
                        )
                    )
                    .then(literal("remove")
                            .then(argument("player", EntityArgument.player())
                                    .executes(context -> removeSubIn(
                                            context.getSource(),
                                            EntityArgument.getPlayer(context, "player"))
                                    )
                            )
                    )
                    .then(literal("list")
                            .executes(context -> listSubIns(context.getSource()))
                    )
        );
    }

    public int addSubIn(CommandSourceStack source, ServerPlayer player, String target) {
        if (checkBanned(source)) return -1;

        GameProfile targetProfile = null;
        //? if <= 1.21.6 {
        /*if (source.getServer().getProfileCache() != null) {
            Optional<GameProfile> opt = source.getServer().getProfileCache().get(target);
            if (opt.isPresent()) {
                targetProfile = opt.get();
            }
        *///?} else {
        if (source.getServer().services().nameToIdCache() != null) {
            Optional<NameAndId> opt = source.getServer().services().nameToIdCache().get(target);
            if (opt.isPresent()) {
                NameAndId playerConfigEntry = opt.get();
                targetProfile = new GameProfile(playerConfigEntry.id(), playerConfigEntry.name());
            }
        //?}
        }
        if (targetProfile == null) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_FETCH.get());
            return -1;
        }

        ServerPlayer targetPlayer = PlayerUtils.getPlayer(target);
        if (targetPlayer != null) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_ONLINE.get());
            return -1;
        }

        if (SubInManager.isSubbingIn(player.getUUID())) {
            GameProfile profile = SubInManager.getSubstitutedPlayer(player.getUUID());
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_ALREADY_SUBBING.get(player, OtherUtils.profileName(profile)));
            return -1;
        }

        if (SubInManager.isBeingSubstituted(OtherUtils.profileId(targetProfile))) {
            GameProfile profile = SubInManager.getSubstitutingPlayer(OtherUtils.profileId(targetProfile));
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_ALREADY_SUBBED.get(target, OtherUtils.profileName(profile)));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, ModifiableText.SUBIN_START.get(player, target));
        SubInManager.addSubIn(player, targetProfile);

        return 1;
    }

    public int removeSubIn(CommandSourceStack source, ServerPlayer player) {
        if (checkBanned(source)) return -1;

        if (!SubInManager.isSubbingIn(player.getUUID())) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_MISSING.get(player));
            return -1;
        }

        GameProfile profile = SubInManager.getSubstitutedPlayer(player.getUUID());

        OtherUtils.sendCommandFeedback(source, ModifiableText.SUBIN_STOP.get(player, OtherUtils.profileName(profile)));
        SubInManager.removeSubIn(player);
        return 1;
    }

    public int listSubIns(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (SubInManager.subIns.isEmpty()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.SUBIN_ERROR_NONE.get());
            return -1;
        }

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SUBIN_CURRENT.get());

        for (SubInManager.SubIn subIn : SubInManager.subIns) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.SUBIN_LIST_ENTRY.get(OtherUtils.profileName(subIn.substituter()), OtherUtils.profileName(subIn.target())));
        }

        return 1;
    }
}
