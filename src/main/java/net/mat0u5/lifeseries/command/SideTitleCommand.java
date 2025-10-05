package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.Collection;

public class SideTitleCommand extends Command {
    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Text getBannedText() {
        return Text.empty();
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {}

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            literal("title")
                .requires(PermissionManager::isAdmin)
                .then(argument("targets", EntityArgumentType.players())
                        .then(literal("side")
                                .then(argument("title", TextArgumentType.text(registryAccess))
                                        .executes(context -> executeTitle(
                                                context.getSource(),
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                TextArgumentType.getTextArgument(context, "title")
                                        ))
                                )
                        )
                )
        );
    }

    private int executeTitle(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title) throws CommandSyntaxException {
        for(ServerPlayerEntity player : targets) {
            NetworkHandlerServer.sideTitle(player, Texts.parse(source, title, player, 0));
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
