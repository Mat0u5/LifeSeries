package net.mat0u5.lifeseries.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.mat0u5.lifeseries.Main;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class Command {
    public abstract boolean isAllowed();
    public abstract Component getBannedText();
    public abstract void register(CommandDispatcher<CommandSourceStack> dispatcher);
    public List<String> getAdminCommands() {
        return List.of();
    }
    public List<String> getNonAdminCommands() {
        return List.of();
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        register(dispatcher);
    }

    public boolean checkBanned(CommandSourceStack source) {
        if (Main.modDisabled()) {
            source.sendFailure(Component.nullToEmpty("The Life Series mod is disabled!"));
            source.sendFailure(Component.nullToEmpty("Enable with \"/lifeseries enable\""));
            return true;
        }
        if (isAllowed()) return false;
        source.sendFailure(getBannedText());
        return true;
    }

    /*
    public boolean isAllowed(ServerCommandSource source) {
        return isAllowed();
    }

    public boolean isAllowedAndAdmin(ServerCommandSource source) {
        return isAllowed() && PermissionManager.isAdmin(source);
    }
    */

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String string) {
        return Commands.literal(string);
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return Commands.argument(name, type);
    }
}
