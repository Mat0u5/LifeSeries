package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OtherCommands extends Command {
    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.empty();
    }

    public List<String> getAdminCommands() {
        return List.of();
    }
    public List<String> getNonAdminCommands() {
        return List.of();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("envcolor")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("type", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("fog", "sky", "cloud"), builder))
                                .then(argument("modify", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("set", "add"), builder))
                                        .then(argument("red", IntegerArgumentType.integer(-255, 255))
                                                .then(argument("green", IntegerArgumentType.integer(-255, 255))
                                                        .then(argument("blue", IntegerArgumentType.integer(-255, 255))
                                                                .executes(context -> changeEnvColor(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "type"),
                                                                        IntegerArgumentType.getInteger(context, "red"),
                                                                        IntegerArgumentType.getInteger(context, "green"),
                                                                        IntegerArgumentType.getInteger(context, "blue"),
                                                                        StringArgumentType.getString(context, "modify").equalsIgnoreCase("set")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("reset")
                                        .executes(context -> resetEnvColor(context.getSource(), StringArgumentType.getString(context, "type")))
                                )
                        )
        );
    }

    private int changeEnvColor(CommandSourceStack source, String type, int red, int green, int blue, boolean setMode) {
        if (checkBanned(source)) return -1;

        if (type.equalsIgnoreCase("fog")) {
            Season.setFogColor(new Vec3(red, green, blue), setMode);
        }
        if (type.equalsIgnoreCase("sky")) {
            Season.setSkyColor(new Vec3(red, green, blue), setMode);
        }
        if (type.equalsIgnoreCase("cloud")) {
            Season.setCloudColor(new Vec3(red, green, blue), setMode);
        }
        return 1;
    }
    private int resetEnvColor(CommandSourceStack source, String type) {
        if (checkBanned(source)) return -1;

        if (type.equalsIgnoreCase("fog")) {
            Season.setFogColor(null, false);
        }
        if (type.equalsIgnoreCase("sky")) {
            Season.setSkyColor(null, false);
        }
        if (type.equalsIgnoreCase("cloud")) {
            Season.setCloudColor(null, false);
        }

        return 1;
    }
}
