package net.mat0u5.lifeseries.seasons.season.finallife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.matlib.command.Command;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
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
				literal("inbetween")
						.requires(PermissionManager::isAdmin)
						.then(literal("enter")
								.then(argument("player", EntityArgument.players())
										.executes(context -> enterInBetween(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
						.then(literal("exit")
								.then(argument("player", EntityArgument.players())
										.executes(context -> exitInBetween(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
						.then(literal("deaths")
								.then(argument("player", EntityArgument.player())
										.executes(context -> getDeaths(context.getSource(), EntityArgument.getPlayer(context, "player")))
										.then(literal("set")
												.then(argument("amount", IntegerArgumentType.integer(0))
														.executes(context -> setDeaths(context.getSource(), EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "amount")))
												)
										)
								)
						)
		);
	}

	public int enterInBetween(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if (checkBanned(source)) return -1;
		int entered = 0;
		for (ServerPlayer player : targets) {
			if (InBetweenManager.enterInBetween(player)) entered++;
		}
		sendCommandFeedback(source, ModifiableText.FINALLIFE_INBETWEEN_COMMAND_ENTER.get(entered, TextUtils.pluralize("player", entered)));
		return entered;
	}

	public int exitInBetween(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if (checkBanned(source)) return -1;
		int exited = 0;
		for (ServerPlayer player : targets) {
			if (!InBetweenManager.isInInBetween(player) && !InBetweenManager.hasInBetweenInstance(player)) continue;
			InBetweenManager.exitInBetween(player, null);
			exited++;
		}
		sendCommandFeedback(source, ModifiableText.FINALLIFE_INBETWEEN_COMMAND_EXIT.get(exited, TextUtils.pluralize("player", exited)));
		return exited;
	}

	public int getDeaths(CommandSourceStack source, ServerPlayer player) {
		if (checkBanned(source)) return -1;
		int deaths = InBetweenManager.getDeaths(player);
		sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_INBETWEEN_COMMAND_DEATHS_GET.get(player, deaths, TextUtils.pluralize("death", deaths)));
		return deaths;
	}

	public int setDeaths(CommandSourceStack source, ServerPlayer player, int amount) {
		if (checkBanned(source)) return -1;
		InBetweenManager.setDeaths(player, amount);
		sendCommandFeedback(source, ModifiableText.FINALLIFE_INBETWEEN_COMMAND_DEATHS_SET.get(player, amount));
		return 1;
	}
}
