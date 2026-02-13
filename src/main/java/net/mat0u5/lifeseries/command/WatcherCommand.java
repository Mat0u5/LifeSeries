package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class WatcherCommand extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() != Seasons.UNASSIGNED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when you have selected a series.");
    }

    public List<String> getAdminCommands() {
        return List.of("watcher");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("watcher")
                .requires(PermissionManager::isAdmin)
                .executes(context -> info(context.getSource()))
                .then(literal("info")
                    .executes(context -> info(context.getSource()))
                )
                .then(literal("list")
                        .executes(context -> listWatchers(context.getSource()))
                )
                .then(literal("add")
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> addWatchers(
                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                        )
                    )
                )
                .then(literal("remove")
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> removeWatchers(
                            context.getSource(), EntityArgument.getPlayers(context, "player"))
                        )
                    )
                )
        );
    }

    public int info(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WATCHER_INFO.get());
        return 1;
    }

    public int listWatchers(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (WatcherManager.getWatchers().isEmpty()) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WATCHER_ERROR_NONE.get());
            return -1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WATCHER_LIST.get(WatcherManager.getWatchers()));
        return 1;
    }

    public int addWatchers(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        targets.forEach(WatcherManager::addWatcher);
        WatcherManager.reloadWatchers();

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WATCHER_ADD_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WATCHER_ADD_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int removeWatchers(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        targets.forEach(WatcherManager::removeWatcher);
        WatcherManager.reloadWatchers();

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WATCHER_REMOVE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WATCHER_REMOVE_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
}
