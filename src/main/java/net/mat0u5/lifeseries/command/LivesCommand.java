package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.Stats;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.*;


public class LivesCommand {

    public static boolean isAllowed() {
        return currentSeries.getSeries() != SeriesList.LIMITED_LIFE && currentSeries.getSeries() != SeriesList.UNASSIGNED;
    }

    public static boolean checkBanned(ServerCommandSource source) {
        if (isAllowed()) return false;
        source.sendError(Text.of("This command is not available during Limited Life."));
        return true;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(
            literal("lives")
            .executes(context -> showLives(context.getSource()))
            .then(literal("reload")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .executes(context -> reloadLives(
                    context.getSource())
                )
            )
            .then(literal("add")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player"), 1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                )
            )
            .then(literal("remove")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player"), -1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), -IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                )
            )
            .then(literal("set")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.player())
                    .then(argument("amount", IntegerArgumentType.integer(0))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount"), true)
                        )
                    )
                )
            )
            .then(literal("get")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("player", EntityArgumentType.player())
                    .executes(context -> getLivesFor(
                        context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                    )
                )
            )
            .then(literal("reset")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .then(argument("player", EntityArgumentType.player())
                            .executes(context -> resetLives(
                                    context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                            )
                    )
            )
            .then(literal("resetAll")
                    .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> resetAllLives(
                            context.getSource())
                    )
            )
        );
    }

    public static int showLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        final ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;
        if (!currentSeries.hasAssignedLives(self)) {
            self.sendMessage(Text.of("You have not been assigned any lives yet."));
            return 1;
        }

        Integer playerLives = currentSeries.getPlayerLives(self);
        self.sendMessage(Text.literal("You have ").append(currentSeries.getFormattedLives(playerLives)).append(Text.of((playerLives==1?" life.":" lives."))));
        if (playerLives <= 0) {
            self.sendMessage(Text.of("Womp womp."));
        }

        return 1;
    }

    public static int getLivesFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (!currentSeries.hasAssignedLives(target)) {
            source.sendError(Text.of(target.getNameForScoreboard()+" has not been assigned any lives."));
            return -1;
        }
        Integer lives = currentSeries.getPlayerLives(target);
        MutableText pt1 = Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" has "));
        Text pt2 = currentSeries.getFormattedLives(lives);
        Text pt3 = Text.of(" "+(Math.abs(lives)==1?"life":"lives")+".");
        source.sendMessage(pt1.append(pt2).append(pt3));
        return 1;
    }

    public static int reloadLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        currentSeries.reloadAllPlayerTeams();
        return 1;
    }

    public static int lifeManager(ServerCommandSource source, ServerPlayerEntity target, int amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (setNotGive) {
            currentSeries.setPlayerLives(target,amount);
            Text finalText = Text.literal("Set ").append(target.getStyledDisplayName()).append(Text.of("'s lives to " + amount + "."));
            source.sendMessage(finalText);
            Stats.addMessageWithTime("[COMMAND] "+finalText.getString());
        }
        else {
            currentSeries.addToPlayerLives(target,amount);
            String pt1 = amount >= 0 ? "Added" : "Removed";
            String pt2 = " "+Math.abs(amount)+" ";
            String pt3 = Math.abs(amount)==1?"life":"lives";
            String pt4 = amount >= 0 ? " to " : " from ";
            Text finalText = Text.of(pt1+pt2+pt3+pt4).copy().append(target.getStyledDisplayName()).append(".");
            source.sendMessage(finalText);
            Stats.addMessageWithTime("[COMMAND] "+finalText.getString());
        }
        return 1;
    }

    public static int resetLives(ServerCommandSource source, ServerPlayerEntity target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        currentSeries.resetPlayerLife(target);

        source.sendMessage(Text.literal("Reset ").append(target.getStyledDisplayName()).append(Text.of("'s lives.")));
        Stats.resetLives(target);
        return 1;
    }

    public static int resetAllLives(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        currentSeries.resetAllPlayerLives();

        source.sendMessage(Text.literal("Reset everyone's lives."));
        Stats.resetAllLives();
        return 1;
    }
}
