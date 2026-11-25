package net.mat0u5.lifeseries.seasons.season.finallife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.manager.CustomCommand;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.AbilityManager;
import net.mat0u5.matlib.utils.other.TextUtils;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class FinalLifeCommands extends CustomCommand {
	@Override
	public boolean isAllowed() {
		return LifeSeries.isSeason(Seasons.FINAL_LIFE);
	}
	@Override
	public Component getBannedText() {
		return Component.nullToEmpty("This command is only available when playing Final Life.");
	}

	public List<String> getAdminCommands() {
		return List.of("inbetween", "ability");
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				literal("inbetween")
						.requires(PermissionManager::isAdmin)
						.then(literal("enter")
								.executes(context -> enterInBetween(context.getSource()))
								.then(argument("player", EntityArgument.players())
										.executes(context -> enterInBetween(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
						.then(literal("exit")
								.executes(context -> exitInBetween(context.getSource()))
								.then(argument("player", EntityArgument.players())
										.executes(context -> exitInBetween(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
						.then(literal("deaths")
								.executes(context -> getDeaths(context.getSource()))
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
		dispatcher.register(
				literal("ability")
						.requires(PermissionManager::isAdmin)
						.then(literal("get")
								.executes(context -> getAbilities(context.getSource()))
								.then(argument("player", EntityArgument.player())
										.executes(context -> getAbilities(context.getSource(), EntityArgument.getPlayer(context, "player")))
								)
						)
						.then(literal("add")
								.then(argument("player", EntityArgument.players())
										.then(argument("ability", StringArgumentType.word())
												.suggests((context, builder) -> SharedSuggestionProvider.suggest(getAbilityNamesSuggestion(), builder))
												.executes(context -> addAbility(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "ability")))
										)
								)
						)
						.then(literal("remove")
								.then(argument("player", EntityArgument.players())
										.then(argument("ability", StringArgumentType.word())
												.suggests((context, builder) -> SharedSuggestionProvider.suggest(getAbilityNamesSuggestion(), builder))
												.executes(context -> removeAbility(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "ability")))
										)
								)
						)
						.then(literal("clear")
								.then(argument("player", EntityArgument.players())
										.executes(context -> clearAbilities(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
		);
	}

	public int enterInBetween(CommandSourceStack source) {
		List<ServerPlayer> players = new ArrayList<>();
		ServerPlayer self = source.getPlayer();
		if (self != null) players.add(self);
		return enterInBetween(source, players);
	}

	public int exitInBetween(CommandSourceStack source) {
		List<ServerPlayer> players = new ArrayList<>();
		ServerPlayer self = source.getPlayer();
		if (self != null) players.add(self);
		return exitInBetween(source, players);
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

	public int getDeaths(CommandSourceStack source) {
		ServerPlayer self = source.getPlayer();
		if (self == null) return -1;
		return getDeaths(source, self);
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

	private static List<String> getAbilityNamesSuggestion() {
		List<String> names = new ArrayList<>();
		for (Abilities ability : Abilities.values()) {
			if (ability != Abilities.EMPTY) names.add(ability.name().toLowerCase(Locale.ROOT));
		}
		return names;
	}

	@Nullable
	private static Abilities parseAbility(CommandSourceStack source, String name) {
		for (Abilities ability : Abilities.values()) {
			if (ability != Abilities.EMPTY && ability.name().equalsIgnoreCase(name)) return ability;
		}
		sendCommandFailure(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_UNKNOWN.get(name));
		return null;
	}

	public int getAbilities(CommandSourceStack source) {
		ServerPlayer self = source.getPlayer();
		if (self == null) return -1;
		return getAbilities(source, self);
	}

	public int getAbilities(CommandSourceStack source, ServerPlayer player) {
		if (checkBanned(source)) return -1;
		List<Ability> abilities = AbilityManager.getPlayerAbilities(player);
		if (abilities.isEmpty()) {
			sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_GET_NONE.get(player));
			return 1;
		}
		List<String> titles = new ArrayList<>();
		for (Ability ability : abilities) {
			titles.add(ability.ability().title());
		}
		sendCommandFeedbackQuiet(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_GET.get(player, String.join(", ", titles)));
		return 1;
	}

	public int addAbility(CommandSourceStack source, Collection<ServerPlayer> targets, String name) {
		if (checkBanned(source)) return -1;
		Abilities ability = parseAbility(source, name);
		if (ability == null) return -1;
		if (targets.size() == 1) {
			ServerPlayer player = targets.iterator().next();
			if (AbilityManager.hasAbility(player, ability)) {
				sendCommandFailure(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_ADD_ALREADY.get(player, ability.title()));
				return -1;
			}
		}

		int added = 0;
		for (ServerPlayer player : targets) {
			if (AbilityManager.hasAbility(player, ability)) continue;
			AbilityManager.addAbility(player, ability, false);
			added++;
		}
		if (targets.size() == 1) {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_ADD_SINGLE.get(targets.iterator().next(), ability.title()));
		}
		else {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_ADD_MULTIPLE.get(added, ability.title()));
		}
		return 1;
	}

	public int removeAbility(CommandSourceStack source, Collection<ServerPlayer> targets, String name) {
		if (checkBanned(source)) return -1;
		Abilities ability = parseAbility(source, name);
		if (ability == null) return -1;
		if (targets.size() == 1) {
			ServerPlayer player = targets.iterator().next();
			if (!AbilityManager.hasAbility(player, ability)) {
				sendCommandFailure(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_REMOVE_MISSING.get(player, ability.title()));
				return -1;
			}
		}

		int removed = 0;
		for (ServerPlayer player : targets) {
			if (!AbilityManager.hasAbility(player, ability)) continue;
			AbilityManager.removeAbility(player, ability);
			removed++;
		}
		if (targets.size() == 1) {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_REMOVE_SINGLE.get(ability.title(), targets.iterator().next()));
		}
		else {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_REMOVE_MULTIPLE.get(ability.title(), removed));
		}
		return 1;
	}

	public int clearAbilities(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if (checkBanned(source)) return -1;
		if (targets.size() == 1) {
			ServerPlayer player = targets.iterator().next();
			if (AbilityManager.getPlayerAbilities(player).isEmpty()) {
				sendCommandFailure(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_GET_NONE.get(player));
				return -1;
			}
		}

		int cleared = 0;
		for (ServerPlayer player : targets) {
			if (AbilityManager.getPlayerAbilities(player).isEmpty()) continue;
			AbilityManager.clearAbilities(player);
			cleared++;
		}
		if (targets.size() == 1) {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_CLEAR_SINGLE.get(targets.iterator().next()));
		}
		else {
			sendCommandFeedback(source, ModifiableText.FINALLIFE_ABILITIES_COMMAND_CLEAR_MULTIPLE.get(cleared));
		}
		return 1;
	}
}
