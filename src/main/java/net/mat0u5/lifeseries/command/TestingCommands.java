package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestingCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        if (VersionControl.isDevVersion()) {
            dispatcher.register(
                literal("ls")
                    .requires(PermissionManager::isAdmin)
                    .then(literal("test")
                        .executes(context -> test(context.getSource()))
                    )
                    .then(literal("test1")
                        .executes(context -> test1(context.getSource()))
                    )
                    .then(literal("test2")
                        .executes(context -> test2(context.getSource()))
                    )
                    .then(literal("test3")
                            .executes(context -> test3(context.getSource()))
                    )
                    .then(literal("players")
                        .then(argument("amount", IntegerArgumentType.integer())
                            .executes(context -> spawnPlayers(context.getSource(), IntegerArgumentType.getInteger(context, "amount")))
                        )
                    )
            );
        }

    }

    public static int test(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("v.1.3.6.25"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("1.3.6.25"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-1.3.6.25"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("1.3.6.25-personname"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-1.3.6.25-personname-two"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-test-1.3.6.25-personname-two"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-test-...1.3.-personname-two"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-test-...1..3.-personname-two"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-test-...1......3.-personname-two"))));

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("1.4.0"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-1.3.7.30"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-1.4.0-pre1"))));
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of(String.valueOf(VersionControl.getModVersionInt("dev-1.4.0-pre2"))));

        return 1;
    }

    public static int test1(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 1"));

        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test0: {}", AdvancedDeathsManager.getRandomDeaths(player, 0)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test1: {}", AdvancedDeathsManager.getRandomDeaths(player, 1)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test2: {}", AdvancedDeathsManager.getRandomDeaths(player, 2)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test3: {}", AdvancedDeathsManager.getRandomDeaths(player, 3)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test4: {}", AdvancedDeathsManager.getRandomDeaths(player, 4)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test5: {}", AdvancedDeathsManager.getRandomDeaths(player, 5)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test6: {}", AdvancedDeathsManager.getRandomDeaths(player, 6)));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test7: {}", AdvancedDeathsManager.getRandomDeaths(player, 7)));

        return 1;
    }

    public static int test2(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 2"));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test: {}", PlayerUtils.getAllPlayers()));

        return 1;
    }

    public static int test3(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("Test Command 3"));

        return 1;
    }

    public static int spawnPlayers(ServerCommandSource source, int amount) {
        for (int i = 1; i <= amount; i++) {
            OtherUtils.executeCommand(TextUtils.formatString("player Test{} spawn in survival", i));
        }
        return 1;
    }
}
