package net.mat0u5.lifeseries.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.mat0u5.lifeseries.Main;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public abstract class Command {
    public abstract boolean isAllowed();
    public abstract Text getBannedText();
    public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher);

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        register(dispatcher);
    }

    public boolean checkBanned(ServerCommandSource source) {
        if (Main.modDisabled()) {
            source.sendError(Text.of("The Life Series mod is disabled!"));
            source.sendError(Text.of("Enable with \"/lifeseries enable\""));
            return true;
        }
        if (isAllowed()) return false;
        source.sendError(getBannedText());
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

    public static LiteralArgumentBuilder<ServerCommandSource> literal(String string) {
        return CommandManager.literal(string);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        return CommandManager.argument(name, type);
    }
}
