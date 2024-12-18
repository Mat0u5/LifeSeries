package net.mat0u5.lifeseries.series.secretlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.mat0u5.lifeseries.series.SeriesList;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SecretLifeCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        if (currentSeries.getSeries() != SeriesList.SECRET_LIFE) return;

        dispatcher.register(
            literal("health")
                .executes(context -> showHealth(context.getSource()))
                .then(literal("sync")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> syncHealth(
                        context.getSource())
                    )
                )
                .then(literal("add")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), 1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("remove")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> healthManager(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"), -1, false)
                        )
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), -DoubleArgumentType.getDouble(context, "amount"), false)
                            )
                        )
                    )
                )
                .then(literal("set")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", EntityArgumentType.player())
                        .then(argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(context -> healthManager(
                                context.getSource(), EntityArgumentType.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount"), true)
                            )
                        )
                    )
                )
                .then(literal("get")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getHealthFor(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                        )
                    )
                )
                .then(literal("reset")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> resetHealth(
                            context.getSource(), EntityArgumentType.getPlayer(context, "player"))
                        )
                    )
                )
                .then(literal("resetAll")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> resetAllHealth(
                        context.getSource())
                    )
                )
        );
    }
    public static int showHealth(ServerCommandSource source) {

        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();

        if (self == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeries;

        if (!secretLife.isAlive(self)) {
            self.sendMessage(Text.of("You're dead..."));
            return -1;
        }

        double playerHealth = secretLife.getRoundedHealth(self);
        self.sendMessage(Text.literal("You have ").append(Text.of(String.valueOf(playerHealth))).append(Text.of(" health.")));

        return 1;
    }
    public static int getHealthFor(ServerCommandSource source, ServerPlayerEntity target) {
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeries;
        MinecraftServer server = source.getServer();
        if (!secretLife.isAlive(target)) {
            source.sendMessage(Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" is dead.")));
            return -1;
        }

        MutableText pt1 = Text.literal("").append(target.getStyledDisplayName()).append(Text.literal(" has "));
        Text pt2 = Text.of(secretLife.getRoundedHealth(target)+" health.");
        source.sendMessage(pt1.append(pt2));
        return 1;
    }
    public static int syncHealth(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        SecretLife secretLife = (SecretLife) currentSeries;
        secretLife.syncAllPlayerHealth();
        return 1;
    }
    public static int healthManager(ServerCommandSource source, ServerPlayerEntity target, double amount, boolean setNotGive) {
        MinecraftServer server = source.getServer();
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeries;
        if (setNotGive) {
            secretLife.setPlayerHealth(target,amount);
            source.sendMessage(Text.literal("Set ").append(target.getStyledDisplayName()).append(Text.of("'s health to " + amount + ".")));
        }
        else {
            secretLife.addPlayerHealth(target,amount);
            String pt1 = amount >= 0 ? "Added" : "Removed";
            String pt2 = " "+Math.abs(amount)+" health";
            String pt3 = amount >= 0 ? " to " : " from ";
            source.sendMessage(Text.of(pt1+pt2+pt3).copy().append(target.getStyledDisplayName()).append("."));
        }
        return 1;
    }
    public static int resetHealth(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        if (target == null) return -1;

        SecretLife secretLife = (SecretLife) currentSeries;
        secretLife.resetPlayerHealth(target);

        source.sendMessage(Text.literal("Reset ").append(target.getStyledDisplayName()).append(Text.of("'s health to 30.")));
        return 1;
    }
    public static int resetAllHealth(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        SecretLife secretLife = (SecretLife) currentSeries;
        secretLife.resetAllPlayerHealth();

        source.sendMessage(Text.literal("Reset everyone's health to 30."));
        return 1;
    }
}
