package net.mat0u5.lifeseries.seasons.season.finallife;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.command.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FinalLifeCommands extends Command {
	@Override
	public boolean isAllowed() {
		return LifeSeries.isSeason(Seasons.FINAL_LIFE);
	}
	@Override
	public Component getBannedText() {
		return Component.nullToEmpty("This command is only available when playing Final Life.");
	}

	public List<String> getAdminCommands() {
		return List.of("finallife");
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				literal("finallife")
		);
	}

}
