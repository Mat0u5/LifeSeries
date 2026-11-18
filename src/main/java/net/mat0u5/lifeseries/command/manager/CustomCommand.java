package net.mat0u5.lifeseries.command.manager;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.*;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanCommand;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLifeCommands;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeCommands;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaCommand;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLifeCommands;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLifeCommands;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeCommands;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLifeTriviaCommand;
import net.mat0u5.lifeseries.seasons.secretsociety.SocietyCommands;
import net.mat0u5.lifeseries.seasons.subin.SubInCommands;
import net.mat0u5.lifeseries.seasons.util.WatcherManager;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.matlib.command.Command;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public abstract class CustomCommand extends Command {
	public List<String> getAdminCommands() {
		return List.of();
	}
	public List<String> getNonAdminCommands() {
		return List.of();
	}

	public boolean checkBanned(CommandSourceStack source) {
		if (LifeSeries.modDisabled()) {
			sendCommandFailure(source, ModifiableText.MOD_DISABLED_ERROR.get());
			return true;
		}
		return super.checkBanned(source);
	}

	public static boolean isAdminOrWatcher(CommandSourceStack source) {
		if (PermissionManager.isAdmin(source)) return true;
		ServerPlayer self = source.getPlayer();
		return self != null && WatcherManager.WATCHERS_SEE_BOOGEY_AND_SOCIETY.get() && ((IPlayer) self).ls$isWatcher();
	}

	public boolean checkAdminOrWatcher(CommandSourceStack source) {
		if (isAdminOrWatcher(source)) return false;
		sendCommandFailure(source, Component.nullToEmpty("You do not have permission to use this command."));
		return true;
	}

	private static List<Command> cache = null;
	public static List<Command> getAllCommands() {
		if (cache != null) return cache;

		cache = List.of(
				new LifeSeriesCommand()
				,new SessionCommand()
				,new LivesCommand()
				,new ClaimKillCommand()
				,new BoogeymanCommand()
				,new GivelifeCommand()
				,new WatcherCommand()
				,new SocietyCommands()
				,new SubInCommands()
				,new DoubleLifeCommands()
				,new SecretLifeCommands()
				,new WildLifeCommands()
				,new PastLifeCommands()
				,new NiceLifeCommands()
				,new WildLifeTriviaCommand()
				,new NiceLifeTriviaCommand()
				,new SelfMessageCommand()
				,new SideTitleCommand()
				,new TestingCommands()
				,new OtherCommands()
				,new LifeSkinsCommand()
		);
		return cache;
	}
}
