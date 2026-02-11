package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.WeightedRandomizer;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static net.mat0u5.lifeseries.Main.server;

public class TestingCommands extends Command {

    @Override
    public boolean isAllowed() {
        return VersionControl.isDevVersion();
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing a dev version.");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (VersionControl.isDevVersion()) {
            dispatcher.register(
                literal("lsdev")
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

    public int test(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;


        return 1;
    }

    public int test1(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        DatapackIntegration.EVENT_PLAYER_LEAVE.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("PosX", String.valueOf(player.blockPosition().getX())),
                new DatapackIntegration.Events.MacroEntry("PosY", String.valueOf(player.blockPosition().getY())),
                new DatapackIntegration.Events.MacroEntry("PosZ", String.valueOf(player.blockPosition().getZ()))
        ));

        return 1;
    }

    public int test2(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("Test Command 2"));
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Test: {}", PlayerUtils.getAllPlayers()));

        return 1;
    }

    public int test3(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, Component.nullToEmpty("Test Command 3"));

        System.out.println("=== Original Example: Range 0-9, Lives 1-3 ===");
        WeightedRandomizer randomizer = new WeightedRandomizer();

        randomizer.testDistribution(0, 9, 1, 4, 1.5);

        // Test different example: Range 1-100 with 1-5 difficulty levels
        System.out.println("\n=== Different Example: Range 1-100, Difficulty 1-5 ===");
        randomizer.testDistribution(1, 100, 1, 5, 1);

        // Test edge case: Range 0-1 with 1-2 states
        System.out.println("\n=== Edge Case: Range 0-1, States 1-2 ===");
        randomizer.testDistribution(0, 1, 1, 2, 1);

        return 1;
    }

    public int spawnPlayers(CommandSourceStack source, int amount) {
        if (checkBanned(source)) return -1;
        for (int i = 1; i <= amount; i++) {
            OtherUtils.executeCommand(TextUtils.formatString("player Test{} spawn in survival", i));
        }
        return 1;
    }
}
