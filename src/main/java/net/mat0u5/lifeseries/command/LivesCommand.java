package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.livesManager;

//? if > 1.20.2
import net.minecraft.world.scores.PlayerScoreEntry;

public class LivesCommand extends Command {

    public boolean isAllowedNormal() {
        return isAllowed() && isNormalLife();
    }

    public boolean isAllowedLimited() {
        return isAllowed() && !isNormalLife();
    }

    public boolean isNormalLife() {
        return currentSeason.getSeason() != Seasons.LIMITED_LIFE;
    }

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when you have selected a Season.");
    }

    public List<String> getAdminCommands() {
        return List.of("lives");
    }

    public List<String> getNonAdminCommands() {
        return List.of("lives");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("lives")
            .executes(context -> showLives(context.getSource()))
            .then(literal("reload")
                .requires(PermissionManager::isAdmin)
                .executes(context -> reloadLives(
                    context.getSource())
                )
            )
            .then(literal("add")
                .requires(PermissionManager::isAdmin)
                .then(argument("player", EntityArgument.players())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgument.getPlayers(context, "player"), 1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("30m", "1h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), false, false)
                        )
                    )
                )
            )
            .then(literal("remove")
                .requires(PermissionManager::isAdmin)
                .then(argument("player", EntityArgument.players())
                    .executes(context -> lifeManager(
                        context.getSource(), EntityArgument.getPlayers(context, "player"), -1, false)
                    )
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"), -IntegerArgumentType.getInteger(context, "amount"), false)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("30m", "1h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), false, true)
                        )
                    )
                )
            )
            .then(literal("set")
                .requires(PermissionManager::isAdmin)
                .then(argument("player", EntityArgument.players())
                    .then(argument("amount", IntegerArgumentType.integer(0))
                        .requires(source -> isAllowedNormal())
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount"), true)
                        )
                    )
                    .then(argument("time", StringArgumentType.greedyString())
                        .requires(source -> isAllowedLimited())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("8h", "16h", "24h"), builder))
                        .executes(context -> lifeManager(
                            context.getSource(), EntityArgument.getPlayers(context, "player"),
                            StringArgumentType.getString(context, "time"), true, false)
                        )
                    )
                )
            )
            .then(literal("get")
                .requires(PermissionManager::isAdmin)
                .then(argument("player", EntityArgument.player())
                    .executes(context -> getLivesFor(
                        context.getSource(), EntityArgument.getPlayer(context, "player"))
                    )
                )
                .then(literal("*")
                    .executes(context -> getAllLives(
                            context.getSource())
                    )
                )
            )
            .then(literal("reset")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                            .executes(context -> resetLives(
                                    context.getSource(), EntityArgument.getPlayers(context, "player"))
                            )
                    )
            )
            .then(literal("resetAll")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> resetAllLives(
                            context.getSource())
                    )
            )
            .then(literal("rollLives")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> assignRandomLives(
                            context.getSource(), PlayerUtils.getAllPlayers()
                    ))
                    .then(argument("players", EntityArgument.players())
                            .executes(context -> assignRandomLives(
                                    context.getSource(), EntityArgument.getPlayers(context, "players")
                            ))
                    )
            )
        );
    }

    public int showLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        boolean normalLife = isNormalLife();

        ServerPlayer self = source.getPlayer();

        if (self == null) return -1;
        if (!self.ls$hasAssignedLives()) {
            String timeOrLives = normalLife ? "lives" : "time";
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("You have not been assigned any {} yet", timeOrLives));
            return 1;
        }

        Integer playerLives = self.ls$getLives();
        
        if (normalLife) {
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("You have {} {}", livesManager.getFormattedLives(playerLives), TextUtils.pluralize("life", "lives", playerLives)));
        }
        else {
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("You have {} left", livesManager.getFormattedLives(playerLives)));
        }

        if (playerLives == null || playerLives <= 0) {
            OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("Womp womp."));
        }

        return 1;
    }

    public int getAllLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        boolean normalLife = isNormalLife();
        String timeOrLives = normalLife ? "lives" : "time";

        if (!ScoreboardUtils.existsObjective(LivesManager.SCOREBOARD_NAME)) {
            source.sendFailure(TextUtils.format("Nobody has been assigned {} yet", timeOrLives));
            return -1;
        }

        //? if <= 1.20.2 {
        /*Collection<Score> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
        *///?} else {
        Collection<PlayerScoreEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
        //?}
        if (entries.isEmpty()) {
            source.sendFailure(TextUtils.format("Nobody has been assigned {} yet", timeOrLives));
            return -1;
        }
        String timeOrLives2 = normalLife ? "Lives" : "Times";

        MutableComponent text = TextUtils.format("Assigned {}: \n", timeOrLives2);
        //? if <= 1.20.2 {
        /*for (Score entry : entries) {
            String name = entry.getOwner();
            int lives = entry.getScore();
        *///?} else {
        for (PlayerScoreEntry entry : entries) {
            String name = entry.owner();
            int lives = entry.value();
        //?}
            if (name.startsWith("`")) continue;
            ChatFormatting color = livesManager.getColorForLives(lives);
            if (normalLife) {
                text.append(TextUtils.format("{} has {} {}\n", Component.literal(name).withStyle(color), livesManager.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
            }
            else {
                text.append(TextUtils.format("{} has {} left\n", Component.literal(name).withStyle(color), livesManager.getFormattedLives(lives)));
            }
        }

        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getLivesFor(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;
        boolean normalLife = isNormalLife();
        String timeOrLives = normalLife ? "lives" : "time";

        if (!target.ls$hasAssignedLives()) {
            source.sendFailure(TextUtils.formatPlain("{} has not been assigned any {}", target, timeOrLives));
            return -1;
        }
        Integer lives = target.ls$getLives();
        if (normalLife) {
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{} has {} {}", target, livesManager.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
        }
        else {
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{} has {} left", target,livesManager.getFormattedLives(lives)));
        }
        return 1;
    }

    public int reloadLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        boolean normalLife = isNormalLife();
        String timeOrLives = normalLife ? "lives" : "times";

        OtherUtils.sendCommandFeedback(source, TextUtils.formatLoosely("§7Reloading {}...", timeOrLives));
        currentSeason.reloadAllPlayerTeams();
        return 1;
    }

    public int lifeManager(CommandSourceStack source, Collection<ServerPlayer> targets, String timeArgument, boolean setNotGive, boolean reverse) {

        Time amount = OtherUtils.parseTimeFromArgument(timeArgument);
        if (amount == null || !amount.isPresent()) {
            source.sendFailure(Component.literal(SessionCommand.INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        if (reverse) amount.multiply(-1);

        return lifeManager(source, targets, amount.getSeconds(), setNotGive);
    }

    public int lifeManager(CommandSourceStack source, Collection<ServerPlayer> targets, int amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;
        boolean normalLife = isNormalLife();
        String timeOrLives = normalLife ? "lives" : "time";

        if (setNotGive) {

            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s {} to {}", targets.iterator().next(), timeOrLives, livesManager.getFormattedLives(amount)));
            }
            else {
                OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {} to {} for {} targets", timeOrLives, livesManager.getFormattedLives(amount), targets.size()));
            }

            for (ServerPlayer player : targets) {
                player.ls$setLives(amount);
            }
        }
        else {

            String addOrRemove = amount >= 0 ? "Added" : "Removed";
            String timeOrLives2 = Math.abs(amount)==1?"life":"lives";
            if (!normalLife) {
                timeOrLives2 = Time.seconds(Math.abs(amount)).formatLong();
            }
            String toOrFrom = amount >= 0 ? "to" : "from";

            if (targets.size() == 1) {
                if (normalLife) {
                    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} {}", addOrRemove, Math.abs(amount), timeOrLives2, toOrFrom, targets.iterator().next()));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {}", addOrRemove, timeOrLives2, toOrFrom, targets.iterator().next()));
                }
            }
            else {
                if (normalLife) {
                    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} {} targets", addOrRemove, Math.abs(amount), timeOrLives2, toOrFrom, targets.size()));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, TextUtils.format("{} {} {} {} targets", addOrRemove, timeOrLives2, toOrFrom, targets.size()));
                }
            }

            for (ServerPlayer player : targets) {
                player.ls$addLives(amount);
            }
        }
        if (currentSeason instanceof DoubleLife doubleLife) {
            targets.forEach(doubleLife::syncSoulboundLives);
        }
        return 1;
    }

    public int resetLives(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;
        String timeOrLives = isNormalLife() ? "lives" : "time";

        targets.forEach(livesManager::resetPlayerLife);

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s {}", targets.iterator().next(), timeOrLives));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {} of {} targets", timeOrLives, targets.size()));
        }

        return 1;
    }

    public int resetAllLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        String timeOrLives = isNormalLife() ? "lives" : "times";

        livesManager.resetAllPlayerLives();
        OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset everyone's {}", timeOrLives));
        return 1;
    }

    public int assignRandomLives(CommandSourceStack source, Collection<ServerPlayer> players) {
        if (checkBanned(source)) return -1;
        if (players == null || players.isEmpty()) return -1;
        String timeOrLives = isNormalLife() ? "lives" : "times";

        if (players.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Assigning random {} to {}§7...", timeOrLives, players.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Assigning random {} to {}§7 targets...", timeOrLives, players.size()));
        }

        livesManager.assignRandomLives(new ArrayList<>(players));

        return 1;
    }
}
