package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class SideTitleCommand extends Command {
    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.empty();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {}

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            literal("title")
                .requires(PermissionManager::isAdmin)
                .then(argument("targets", EntityArgument.players())
                        .then(literal("side")
                                //? if <= 1.20.3 {
                                .then(argument("title", ComponentArgument.textComponent())
                                //?} else {
                                /*.then(argument("title", ComponentArgument.textComponent(registryAccess))
                                *///?}
                                        .executes(context -> executeTitle(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                //? if <= 1.21.4 {
                                                ComponentArgument.getComponent(context, "title")
                                                //?} else {
                                                /*ComponentArgument.getRawComponent(context, "title")
                                                *///?}
                                        ))
                                )
                        )
                )
        );
    }

    private int executeTitle(CommandSourceStack source, Collection<ServerPlayer> targets, Component title) throws CommandSyntaxException {
        for(ServerPlayer player : targets) {
            NetworkHandlerServer.sideTitle(player, ComponentUtils.updateForEntity(source, title, player, 0));
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Showing new side title for {}", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Showing new side title for {} players", targets.size()));
        }

        return targets.size();
    }

}
