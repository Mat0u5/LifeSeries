package net.mat0u5.lifeseries.seasons.season.doublelife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class DoubleLifeCommands extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.DOUBLE_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing Double Life.");
    }

    public List<String> getAdminCommands() {
        return List.of("soulmate");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("soulmate")
                .requires(PermissionManager::isAdmin)
                .then(literal("get")
                    .then(argument("player", EntityArgument.player())
                        .executes(context -> getSoulmate(context.getSource(), EntityArgument.getPlayer(context, "player")))
                    )
                )
                .then(literal("set")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("soulmate", EntityArgument.player())
                                        .executes(context -> setSoulmate(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                EntityArgument.getPlayer(context, "soulmate")
                                        ))
                                )
                        )
                )
                .then(literal("force")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("soulmate", EntityArgument.player())
                                        .executes(context -> forceSoulmate(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                EntityArgument.getPlayer(context, "soulmate")
                                        ))
                                )
                        )
                        .then(literal("reset")
                                .executes(context -> forceSoulmate(
                                        context.getSource(), null, null
                                ))
                        )
                )
                .then(literal("prevent")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("soulmate", EntityArgument.player())
                                        .executes(context -> preventSoulmate(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                EntityArgument.getPlayer(context, "soulmate")
                                        ))
                                )
                        )
                        .then(literal("reset")
                                .executes(context -> preventSoulmate(
                                        context.getSource(), null, null
                                ))
                        )
                )
                .then(literal("list")
                    .executes(context -> listSoulmates(context.getSource()))
                )
                .then(literal("reset")
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> resetSoulmate(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("resetAll")
                    .executes(context -> resetAllSoulmates(context.getSource()))
                )
                .then(literal("randomize")
                    .executes(context -> rollSoulmates(context.getSource()))
                )
        );
    }

    public int preventSoulmate(CommandSourceStack source, ServerPlayer player, ServerPlayer soulmate) {
        if (checkBanned(source)) return -1;

        if (player == null && soulmate == null) {
            OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Soulmate prevent entries were reset"));
            return 1;
        }

        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (season.hasSoulmate(player)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }

        if (season.hasSoulmate(soulmate)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }
        if (player.getUUID() == soulmate.getUUID()) {
            source.sendFailure(Component.nullToEmpty("You cannot specify the same player twice"));
            return -1;
        }

        season.preventSoulmates(player,soulmate);

        OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_PREVENT.get(player, soulmate));
        return 1;
    }

    public int forceSoulmate(CommandSourceStack source, ServerPlayer player, ServerPlayer soulmate) {
        if (checkBanned(source)) return -1;

        if (player == null && soulmate == null) {
            OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Soulmate force entries were reset"));
            return 1;
        }

        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (season.hasSoulmate(player)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }

        if (season.hasSoulmate(soulmate)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }
        if (player.getUUID() == soulmate.getUUID()) {
            source.sendFailure(Component.nullToEmpty("You cannot specify the same player twice"));
            return -1;
        }

        if (DoubleLife.soulmatesForce.containsKey(player.getUUID()) || DoubleLife.soulmatesForce.containsValue(player.getUUID())) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_FORCE_EXISTS.get(player));
            return -1;
        }

        if (DoubleLife.soulmatesForce.containsKey(soulmate.getUUID()) || DoubleLife.soulmatesForce.containsValue(soulmate.getUUID())) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_FORCE_EXISTS.get(player));
            return -1;
        }

        season.forceSoulmates(player,soulmate);

        OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_FORCE.get(player, soulmate));
        return 1;
    }

    public int setSoulmate(CommandSourceStack source, ServerPlayer player, ServerPlayer soulmate) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (season.hasSoulmate(player)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }

        if (season.hasSoulmate(soulmate)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_EXISTS.get(player));
            return -1;
        }

        season.setSoulmate(player,soulmate);
        season.saveSoulmates();

        OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_SET.get(player, soulmate));
        return 1;
    }

    public int getSoulmate(CommandSourceStack source, ServerPlayer player) {
        if (checkBanned(source)) return -1;
        if (player == null) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        if (!season.hasSoulmate(player)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_MISSING.get(player));
            return -1;
        }
        if (!season.isSoulmateOnline(player)) {
            source.sendFailure(ModifiableText.DOUBLELIFE_SOULMATE_ERROR_OFFLINE.get(player));
            return -1;
        }

        ServerPlayer soulmate = season.getSoulmate(player);
        if (soulmate == null) return -1;

        OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_GET.get(player, soulmate));
        return 1;
    }

    public int resetSoulmate(CommandSourceStack source, Collection<ServerPlayer> players) {
        if (checkBanned(source)) return -1;
        if (players == null) return -1;
        if (players.isEmpty()) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        List<ServerPlayer> affected = new ArrayList<>();
        for (ServerPlayer player : players) {
            if (season.hasSoulmate(player)) {
                season.resetSoulmate(player);
                season.saveSoulmates();
                affected.add(player);
            }
        }

        if (affected.isEmpty()) {
            source.sendFailure(Component.nullToEmpty("No target was found"));
            return -1;
        }
        if (affected.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_RESET_SINGLE.get(affected.get(0)));
            return 1;
        }
        OtherUtils.sendCommandFeedback(source, ModifiableText.DOUBLELIFE_SOULMATE_RESET_MULTIPLE.get(affected.size()));
        return 1;
    }

    public int resetAllSoulmates(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        season.resetAllSoulmates();

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("All soulmate entries were reset"));
        return 1;
    }

    public int listSoulmates(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        DoubleLife season = ((DoubleLife) currentSeason);

        boolean noSoulmates = true;
        for (Map.Entry<UUID, UUID> entry : season.soulmatesOrdered.entrySet()) {
            noSoulmates = false;
            Object text1 = entry.getKey();
            Object text2 = entry.getValue();
            ServerPlayer player = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer soulmate = PlayerUtils.getPlayer(entry.getValue());
            if (player != null) text1 = player;
            if (soulmate != null) text2 = soulmate;

            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.DOUBLELIFE_SOULMATE_GET.get(text1, text2));
        }

        if (noSoulmates) {
            source.sendFailure(Component.nullToEmpty("There are no soulmates currently assigned"));
        }
        return 1;
    }

    public int rollSoulmates(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        DoubleLife season = ((DoubleLife) currentSeason);
        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Rolling soulmates..."));
        season.rollSoulmates();
        return 1;
    }
}
