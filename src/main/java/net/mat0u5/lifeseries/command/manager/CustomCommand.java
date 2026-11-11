package net.mat0u5.lifeseries.command.manager;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
//? if <= 26.3 {
/*import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.SharedSuggestionProvider;
*///?} else {
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DoubleArgument;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
//?}

@Deprecated
//Due to major 26.4 command changes
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

	//? if <= 26.3 {
	/*public static Predicate<CommandSourceStack> requireAdmin() {
		return PermissionManager::isAdmin;
	}

	public static Predicate<CommandSourceStack> requireIf(BooleanSupplier condition) {
		return source -> condition.getAsBoolean();
	}
	*///?} else {
	private static final PermissionCheck ADMIN_CHECK = new PermissionCheck.Require(Permissions.COMMANDS_ADMIN);

	public static Predicate<CommandSourceStack> requireAdmin() {
		return Commands.hasPermission(ADMIN_CHECK);
	}

	public static Predicate<CommandSourceStack> requireIf(BooleanSupplier condition) {
		return Commands.hasPermission(new ConditionCheck(condition));
	}

	private record ConditionCheck(BooleanSupplier condition) implements PermissionCheck {
		@Override
		public boolean check(PermissionSet permissions) {
			return condition.getAsBoolean();
		}

		@Override
		public MapCodec<ConditionCheck> codec() {
			return MapCodec.unit(this);
		}
	}
	//?}

	//? if <= 26.3 {
	/*public static ArgumentType<String> stringArg() {
		return StringArgumentType.string();
	}
	public static ArgumentType<String> greedyStringArg() {
		return StringArgumentType.greedyString();
	}
	public static ArgumentType<Integer> integerArg() {
		return IntegerArgumentType.integer();
	}
	public static ArgumentType<Integer> integerArg(int min) {
		return IntegerArgumentType.integer(min);
	}
	public static ArgumentType<Integer> integerArg(int min, int max) {
		return IntegerArgumentType.integer(min, max);
	}
	public static ArgumentType<Double> doubleArg(double min) {
		return DoubleArgumentType.doubleArg(min);
	}
	*///?} else {
	public static ArgumentType<String> stringArg() {
		return StringArgument.string();
	}
	public static ArgumentType<String> greedyStringArg() {
		return StringArgument.greedyString();
	}
	public static ArgumentType<Integer> integerArg() {
		return IntegerArgument.integer();
	}
	public static ArgumentType<Integer> integerArg(int min) {
		return IntegerArgument.integer(min);
	}
	public static ArgumentType<Integer> integerArg(int min, int max) {
		return IntegerArgument.integer(min, max);
	}
	public static ArgumentType<Double> doubleArg(double min) {
		return DoubleArgument.doubleArg(min);
	}
	//?}

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

	// Fixed list
	public SuggestionProvider<CommandSourceStack> suggestList(List<String> suggestions) {
		return suggestList(() -> suggestions);
	}

	// Re-evaluated on every request
	public SuggestionProvider<CommandSourceStack> suggestList(Supplier<? extends Iterable<String>> suggestions) {
		//? if <= 26.3 {
		/*return suggestCustom(builder -> SharedSuggestionProvider.suggest(suggestions.get(), builder));
		*///?} else {
		return suggestCustom(builder -> SuggestionContext.suggest(suggestions.get(), builder));
		//?}
	}

	// Re-evaluated on every request
	public static SuggestionProvider<CommandSourceStack> suggestCustom(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> provider) {
		//? if <= 26.3 {
		/*return (context, builder) -> provider.apply(builder);
		*///?} else {
		return SuggestionProviders.serverOnly((server, context, builder) -> provider.apply(builder));
		//?}
	}
}
