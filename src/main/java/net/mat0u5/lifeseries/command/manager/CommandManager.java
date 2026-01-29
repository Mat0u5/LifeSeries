package net.mat0u5.lifeseries.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.*;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanCommand;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLifeCommands;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeCommands;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaCommand;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLifeCommands;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLifeCommands;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeTriviaCommand;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeCommands;
import net.mat0u5.lifeseries.seasons.secretsociety.SocietyCommands;
import net.mat0u5.lifeseries.seasons.subin.SubInCommands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    public static List<Command> commands = new ArrayList<>();
    private static void loadCommands() {
        commands.add(new LifeSeriesCommand());
        commands.add(new SessionCommand());
        commands.add(new LivesCommand());
        commands.add(new ClaimKillCommand());

        commands.add(new BoogeymanCommand());
        commands.add(new GivelifeCommand());
        commands.add(new WatcherCommand());
        commands.add(new SocietyCommands());
        commands.add(new SubInCommands());

        commands.add(new DoubleLifeCommands());
        commands.add(new SecretLifeCommands());
        commands.add(new WildLifeCommands());
        commands.add(new PastLifeCommands());
        commands.add(new NiceLifeCommands());

        commands.add(new WildLifeTriviaCommand());
        commands.add(new NiceLifeTriviaCommand());

        commands.add(new SelfMessageCommand());
        commands.add(new SideTitleCommand());
        commands.add(new TestingCommands());
        commands.add(new OtherCommands());
        commands.add(new LifeSkinsCommand());
    }

    public static void registerAllCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, net.minecraft.commands.Commands.CommandSelection registrationEnvironment) {
        if (commands.isEmpty()) {
            loadCommands();
        }
        for (Command command : commands) {
            command.register(dispatcher, commandRegistryAccess);
        }
    }
}
