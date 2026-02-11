package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.currentSession;

public class SessionCommand extends Command {
    public static final String INVALID_TIME_FORMAT_ERROR = "Invalid time format. Use h, m, s for hours, minutes, and seconds.";

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when you have selected a Season.");
    }

    public List<String> getAdminCommands() {
        return List.of("session");
    }

    public List<String> getNonAdminCommands() {
        return List.of("session");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("session")
                .then(literal("start")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> startSession(
                        context.getSource()
                    ))
                )
                .then(literal("stop")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> stopSession(
                        context.getSource()
                    ))
                )
                .then(literal("pause")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> pauseSession(
                        context.getSource()
                    ))
                    .then(literal("queue")
                        .then(literal("reset")
                            .executes(context -> pauseQueueReset(
                                    context.getSource()
                            ))
                        )
                        .then(argument("time", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("1h","1h30m","2h"), builder))
                                .then(argument("duration", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("5m", "10m"), builder))
                                        .executes(context -> pauseQueue(
                                                context.getSource(), StringArgumentType.getString(context, "time"), StringArgumentType.getString(context, "duration")
                                        ))
                                )
                        )
                    )
                )
                .then(literal("timer")
                    .then(literal("set")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("time", StringArgumentType.greedyString())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("1h","1h30m","2h"), builder))
                            .executes(context -> setTime(
                                context.getSource(), StringArgumentType.getString(context, "time")
                            ))
                        )
                    )
                    .then(literal("add")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("time", StringArgumentType.greedyString())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("30m", "1h"), builder))
                            .executes(context -> addTime(
                                context.getSource(), StringArgumentType.getString(context, "time")
                            ))
                        )
                    )
                    .then(literal("fastforward")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("time", StringArgumentType.greedyString())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("5m"), builder))
                            .executes(context -> skipTime(
                                context.getSource(), StringArgumentType.getString(context, "time")
                            ))
                        )
                    )
                    .then(literal("remove")
                        .requires(PermissionManager::isAdmin)
                            .then(argument("time", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("5m"), builder))
                                    .executes(context -> removeTime(
                                            context.getSource(), StringArgumentType.getString(context, "time")
                                    ))
                            )
                    )
                    .then(literal("remaining")
                        .executes(context -> getTime(
                            context.getSource()
                        ))
                    )
                    .then(literal("showDisplay")
                        .executes(context -> displayTimer(
                            context.getSource()
                        ))
                    )
                )

        );
    }
    public int pauseQueue(CommandSourceStack source, String timeArgument1, String timeArgument2) {
        if (checkBanned(source)) return -1;

        Time pauseAt = OtherUtils.parseTimeFromArgument(timeArgument1);
        if (pauseAt == null || !pauseAt.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        Time pauseFor = OtherUtils.parseTimeFromArgument(timeArgument2);
        if (pauseFor == null || !pauseFor.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, TextUtils.format("The session will pause at {} for {}", pauseAt.formatLong(), pauseFor.formatLong()));
        currentSession.queuePause(pauseAt, pauseFor);
        return 1;
    }

    public int pauseQueueReset(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Reset all queued pauses"));

        currentSession.discardAllQueuedPauses();
        return 1;
    }

    public int getTime(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.validTime()) {
            source.sendFailure(Component.nullToEmpty("The session time has not been set yet"));
            return -1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("The session ends in {}",currentSession.getRemainingTimeStr()));
        return 1;
    }

    public int displayTimer(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        final ServerPlayer self = source.getPlayer();

        if (self == null) return -1;
        if (NetworkHandlerServer.wasHandshakeSuccessful(self)) {
            SimplePackets.TOGGLE_TIMER.target(self).sendToClient();
        }

        boolean isInDisplayTimer = currentSession.isInDisplayTimer(self);
        if (isInDisplayTimer) currentSession.removeFromDisplayTimer(self);
        else currentSession.addToDisplayTimer(self);
        return 1;
    }

    public int startSession(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!currentSession.validTime()) {
            source.sendFailure(Component.nullToEmpty("The session time is not set! Use '/session timer set <time>' to set the session time."));
            return -1;
        }
        if (currentSession.statusStarted()) {
            source.sendFailure(Component.nullToEmpty("The session has already started"));
            return -1;
        }
        if (currentSession.statusPaused()) {
            OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Unpausing session..."));
            currentSession.sessionPause();
            return 1;
        }

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("Starting session..."));
        if (!currentSession.sessionStart()) {
            source.sendFailure(Component.nullToEmpty("Could not start session"));
            return -1;
        }

        return 1;
    }

    public int stopSession(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (currentSession.statusNotStarted() || currentSession.statusFinished()) {
            source.sendFailure(Component.nullToEmpty("The session has not yet started"));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Stopping session..."));
        currentSession.sessionEnd();
        return 1;
    }

    public int pauseSession(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (currentSession.statusNotStarted() || currentSession.statusFinished()) {
            source.sendFailure(Component.nullToEmpty("The session has not yet started"));
            return -1;
        }

        if (currentSession.statusPaused()) {
            OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Unpausing session..."));
            currentSession.sessionPause();
        }
        else {
            OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Pausing session..."));
            currentSession.queuePause(currentSession.getPassedTime(), Time.hours(10_000));
        }

        return 1;
    }

    public int skipTime(CommandSourceStack source, String timeArgument) {
        if (checkBanned(source)) return -1;

        Time timeTotal = OtherUtils.parseTimeFromArgument(timeArgument);
        if (timeTotal == null || !timeTotal.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        OtherUtils.sendCommandFeedback(source, TextUtils.format("Skipped {} in the session length", timeTotal.formatLong()));
        currentSession.passTime(timeTotal);
        return 1;
    }

    public int setTime(CommandSourceStack source, String timeArgument) {
        if (checkBanned(source)) return -1;

        Time timeTotal = OtherUtils.parseTimeFromArgument(timeArgument);
        if (timeTotal == null || !timeTotal.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        currentSession.setSessionLength(timeTotal);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("The session length has been set to {}", timeTotal.formatLong()));
        return 1;
    }

    public int addTime(CommandSourceStack source, String timeArgument) {
        if (checkBanned(source)) return -1;

        Time timeTotal = OtherUtils.parseTimeFromArgument(timeArgument);
        if (timeTotal == null || !timeTotal.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        currentSession.addSessionLength(timeTotal);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("Added {} to the session length", timeTotal.formatLong()));
        return 1;
    }

    public int removeTime(CommandSourceStack source, String timeArgument) {
        if (checkBanned(source)) return -1;

        Time timeTotal = OtherUtils.parseTimeFromArgument(timeArgument);
        if (timeTotal == null || !timeTotal.isPresent()) {
            source.sendFailure(Component.literal(INVALID_TIME_FORMAT_ERROR));
            return -1;
        }
        currentSession.removeSessionLength(timeTotal);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("Removed {} from the session length", timeTotal.formatLong()));
        return 1;
    }
}

