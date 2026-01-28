package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.SkinManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SkinCommand extends Command {
    @Override
    public boolean isAllowed() {
        return Main.DEBUG;
    }

    @Override
    public Component getBannedText() {
        return Component.empty();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("changeskin")
                .requires(PermissionManager::isAdmin)

                .then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("username", StringArgumentType.string())
                                        .executes(context -> setSkin(context))
                                )
                        )
                )

                .then(Commands.literal("reset")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> resetSkin(context))
                        )
                )
        );
    }

    private static int setSkin(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            String username = StringArgumentType.getString(context, "username");

            context.getSource().sendSuccess(
                    () -> Component.literal("Changing " + targetPlayer.getName().getString() + "'s skin to " + username + "..."),
                    true
            );

            SkinManager.setSkin(targetPlayer, username).thenAccept(success -> {
                if (success) {
                    context.getSource().sendSuccess(
                            () -> Component.literal("Successfully changed skin!"),
                            true
                    );
                } else {
                    context.getSource().sendFailure(
                            Component.literal("Failed to change skin. Username may not exist.")
                    );
                }
            });

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int resetSkin(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");

            boolean success = SkinManager.resetSkin(targetPlayer);

            if (success) {
                context.getSource().sendSuccess(
                        () -> Component.literal("Reset " + targetPlayer.getName().getString() + "'s skin!"),
                        true
                );
            } else {
                context.getSource().sendFailure(
                        Component.literal("No modified skin found for this player.")
                );
            }

            return success ? 1 : 0;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}
