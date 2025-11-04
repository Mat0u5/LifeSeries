package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class SelfMessageCommand extends Command {

    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("selfmsg")
                .then(argument("message", StringArgumentType.greedyString())
                    .executes(context -> execute(
                        context.getSource(),
                        StringArgumentType.getString(context ,"message")
                    ))
                )
        );

    }

    public int execute(CommandSourceStack source, String string) {
        if (checkBanned(source)) return -1;
        source.sendSystemMessage(Component.nullToEmpty(string));
        return 1;
    }
}
