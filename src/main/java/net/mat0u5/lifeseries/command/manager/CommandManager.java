package net.mat0u5.lifeseries.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.lifeseries.command.*;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanCommand;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLifeCommands;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLifeCommands;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLifeCommands;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeCommands;
import net.mat0u5.lifeseries.seasons.secretsociety.SocietyCommands;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static List<Command> commands = new ArrayList<>();
    private static void loadCommands() {
        commands.add(new DoubleLifeCommands());
        commands.add(new SecretLifeCommands());
        commands.add(new WildLifeCommands());
        commands.add(new PastLifeCommands());

        commands.add(new LivesCommand());
        commands.add(new SessionCommand());
        commands.add(new BoogeymanCommand());
        commands.add(new ClaimKillCommand());
        commands.add(new LifeSeriesCommand());
        commands.add(new GivelifeCommand());
        commands.add(new SelfMessageCommand());
        commands.add(new WatcherCommand());
        commands.add(new SocietyCommands());
        commands.add(new TestingCommands());
    }

    public static void registerAllCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, net.minecraft.server.command.CommandManager.RegistrationEnvironment registrationEnvironment) {
        loadCommands();
        for (Command command : commands) {
            command.register(dispatcher);
        }
    }
}
