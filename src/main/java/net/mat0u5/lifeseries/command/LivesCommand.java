package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
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
        ServerPlayer self = source.getPlayer();

        if (self == null) return -1;
        if (!self.ls$hasAssignedLives()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIVES_UNASSIGNED.get());
            return 1;
        }

        Integer playerLives = self.ls$getLives();

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIVES_GET_SELF.get(livesManager.getFormattedLives(playerLives), TextUtils.pluralize("life", "lives", playerLives)));

        if (playerLives == null || playerLives <= 0) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIVES_GET_SELF_NONE.get());
        }

        return 1;
    }

    public int getAllLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!ScoreboardUtils.existsObjective(LivesManager.SCOREBOARD_NAME)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.LIVES_UNASSIGNED_ALL.get());
            return -1;
        }

        //? if <= 1.20.2 {
        /*Collection<Score> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
        *///?} else {
        Collection<PlayerScoreEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
        //?}
        if (entries.isEmpty()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.LIVES_UNASSIGNED_ALL.get());
            return -1;
        }

        MutableComponent text = ModifiableText.LIVES_ASSIGNED_LIST.get().copy();
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
            text.append(ModifiableText.LIVES_ASSIGNED_LIST_ENTRY.get(Component.literal(name).withStyle(color), livesManager.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
        }

        OtherUtils.sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getLivesFor(CommandSourceStack source, ServerPlayer target) {
        if (checkBanned(source)) return -1;
        if (target == null) return -1;

        if (!target.ls$hasAssignedLives()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.LIVES_UNASSIGNED_OTHER.get(target));
            return -1;
        }
        Integer lives = target.ls$getLives();
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.LIVES_ASSIGNED_LIST_ENTRY.get(target, livesManager.getFormattedLives(lives), TextUtils.pluralize("life", "lives", lives)));
        return 1;
    }

    public int reloadLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RELOADING.get());
        currentSeason.reloadAllPlayerTeams();
        return 1;
    }

    public int lifeManager(CommandSourceStack source, Collection<ServerPlayer> targets, String timeArgument, boolean setNotGive, boolean reverse) {

        Time amount = OtherUtils.parseTimeFromArgument(timeArgument);
        if (amount == null || !amount.isPresent()) {
            OtherUtils.sendCommandFailure(source, Component.literal(SessionCommand.INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        if (reverse) amount.multiply(-1);

        return lifeManager(source, targets, amount.getSeconds(), setNotGive);
    }

    public int lifeManager(CommandSourceStack source, Collection<ServerPlayer> targets, int amount, boolean setNotGive) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;
        boolean normalLife = isNormalLife();

        if (setNotGive) {

            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_SET_SINGLE.get(targets.iterator().next(), livesManager.getFormattedLives(amount)));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_SET_MULTIPLE.get(livesManager.getFormattedLives(amount), targets.size()));
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
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_CHANGE_SINGLE.get(addOrRemove, Math.abs(amount), timeOrLives2, toOrFrom, targets.iterator().next()));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_CHANGE_SINGLE.get(addOrRemove, timeOrLives2, toOrFrom, targets.iterator().next()));
                }
            }
            else {
                if (normalLife) {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_CHANGE_MULTIPLE.get(addOrRemove, Math.abs(amount), timeOrLives2, toOrFrom, targets.size()));
                }
                else {
                    OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_CHANGE_MULTIPLE.get(addOrRemove, timeOrLives2, toOrFrom, targets.size()));
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

        targets.forEach(livesManager::resetPlayerLife);

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int resetAllLives(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        livesManager.resetAllPlayerLives();
        OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RESET_EVERYONE.get());
        return 1;
    }

    public int assignRandomLives(CommandSourceStack source, Collection<ServerPlayer> players) {
        if (checkBanned(source)) return -1;
        if (players == null || players.isEmpty()) return -1;

        if (players.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RANDOMIZE_SINGLE.get(players.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.LIVES_RANDOMIZE_MULTIPLE.get(players.size()));
        }

        livesManager.assignRandomLives(new ArrayList<>(players));

        return 1;
    }
}
