package net.mat0u5.lifeseries.seasons.season.wildlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Callback;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class WildLifeCommands extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.WILD_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when playing Wild Life.");
    }

    public List<String> getAdminCommands() {
        return List.of("wildcard", "snail", "superpower", "hunger");
    }

    public List<String> getNonAdminCommands() {
        return List.of("snail");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("wildcard")
                .requires(PermissionManager::isAdmin)
                .then(literal("list")
                    .executes(context -> listWildcards(
                        context.getSource())
                    )
                )
                .then(literal("listActive")
                    .executes(context -> listActiveWildcards(
                        context.getSource())
                    )
                )
                .then(literal("activate")
                    .then(argument("wildcard", StringArgumentType.greedyString())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(suggestionsActivateWildcard(), builder))
                        .executes(context -> activateWildcard(
                            context.getSource(), StringArgumentType.getString(context, "wildcard"))
                        )
                    )
                )
                .then(literal("deactivate")
                    .then(argument("wildcard", StringArgumentType.greedyString())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(suggestionsDeactivateWildcard(), builder))
                        .executes(context -> deactivateWildcard(
                            context.getSource(), StringArgumentType.getString(context, "wildcard"))
                        )
                    )
                )
                .then(literal("choose")
                    .requires(source -> (NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer()) || (source.getEntity() == null)))
                    .executes(context -> chooseWildcard(
                        context.getSource())
                    )
                )
                .then(literal("finale")
                        .executes(context -> activateFinale(
                                context.getSource())
                        )
                )
                .then(literal("effect")
                        .then(literal("dots")
                                .executes(context -> effectDots(
                                        context.getSource())
                                )
                        )
                        .then(literal("makeItWild")
                                .executes(context -> effectMakeItWild(
                                        context.getSource())
                                )
                        )
                )
        );
        dispatcher.register(
            literal("snail")
                .then(literal("names")
                    .then(literal("set")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.player())
                            .then(argument("name", StringArgumentType.greedyString())
                                .executes(context -> setSnailName(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "name")))
                            )
                        )
                    )
                    .then(literal("reset")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.players())
                            .executes(context -> resetSnailName(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                    )
                    .then(literal("get")
                        .then(argument("player", EntityArgument.players())
                            .executes(context -> getSnailNames(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                    )
                    .then(literal("request")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(context -> requestSnailName(context.getSource(), StringArgumentType.getString(context, "name")))
                        )
                    )
                )
                .then(literal("textures")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> getSnailTexturesInfo(context.getSource()))
                    .then(literal("list")
                            .executes(context -> getSnailTextures(context.getSource()))
                    )
                    .then(literal("info")
                        .executes(context -> getSnailTexturesInfo(context.getSource()))
                    )
                    .then(literal("reload")
                            .executes(context -> snailTexturesReload(context.getSource()))
                    )
                )
        );
        dispatcher.register(
            literal("superpower")
                .requires(PermissionManager::isAdmin)
                .then(literal("set")
                    .then(argument("player", EntityArgument.players())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(Superpowers.getImplementedStr(), builder))
                            .executes(context -> setSuperpower(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "superpower")))
                        )
                    )
                )
                .then(literal("reset")
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> resetSuperpowers(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("randomize")
                    .then(argument("player", EntityArgument.players())
                            .executes(context -> setRandomSuperpowers(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                    .executes(context -> setRandomSuperpowers(context.getSource()))
                )
                .then(literal("get")
                    .then(argument("player", EntityArgument.player())
                        .executes(context -> getSuperpower(context.getSource(), EntityArgument.getPlayer(context, "player")))
                    )
                )
                .then(literal("skipCooldown")
                    .executes(context -> skipSuperpowerCooldown(context.getSource()))
                )
                .then(literal("force")
                    .then(argument("player", EntityArgument.players())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> SharedSuggestionProvider.suggest(Superpowers.getImplementedStr(), builder))
                            .executes(context -> assignSuperpower(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "superpower")))
                        )
                        .then(literal("reset")
                                .executes(context -> assignSuperpower(context.getSource(), EntityArgument.getPlayers(context, "player"), null))
                        )
                    )
                )
        );
        dispatcher.register(
            literal("hunger")
                .requires(PermissionManager::isAdmin)
                .then(literal("randomizeFood")
                        .executes(context -> randomizeFood(context.getSource()))
                )
        );
    }

    public int effectDots(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        WildcardManager.showDots();

        return 1;
    }

    public int effectMakeItWild(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        Callback.showEndingTitles();

        return 1;
    }

    public int activateFinale(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        WildcardManager.FINALE = true;
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_FINALE.get());

        return 1;
    }

    public int randomizeFood(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (!WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_HUNGER_INACTIVE.get());
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_HUNGER_RANDOMIZE_MANUAL.get());

        if (WildcardManager.activeWildcards.get(Wildcards.HUNGER) instanceof Hunger hungerWildcard) {
            hungerWildcard.newFoodRules();
        }

        return 1;
    }

    public int requestSnailName(CommandSourceStack source, String name) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        PlayerUtils.broadcastMessageToAdmins(ModifiableText.WILDLIFE_SNAIL_NAME_REQUEST.get(player, name));
        Component adminText = ModifiableText.WILDLIFE_SNAIL_NAME_REQUEST_PROMPT.get(TextUtils.runCommandText(TextUtils.formatString("/snail names set {} {}", player, name)));
        PlayerUtils.broadcastMessageToAdmins(adminText);
        return 1;
    }

    public int snailTexturesReload(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SNAIL_TEXTURES_RELOAD.get());
        SnailSkins.sendTextures();

        return 1;
    }

    public int getSnailTexturesInfo(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SNAIL_TEXTURE_INFO.get(TextUtils.openURLText("mat0u5.github.io/LifeSeries-docs/config/wild-life-snails")));

        return 1;
    }

    public int getSnailTextures(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        List<String> textures = SnailSkins.getAllSkins();
        if (textures.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SNAIL_TEXTURES_NONE.get());
            return -1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SNAIL_TEXTURES_LIST.get(textures));
        return 1;
    }

    public int chooseWildcard(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_GUI_ERROR.get());
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_WILDCARD_GUI_OPEN.get());
        SimplePackets.SELECT_WILDCARDS.target(source.getPlayer()).sendToClient();
        return 1;
    }

    public List<String> suggestionsDeactivateWildcard() {
        List<String> allWildcards = Wildcards.getActiveWildcardsStr();
        allWildcards.add("*");
        return allWildcards;
    }

    public List<String> suggestionsActivateWildcard() {
        List<String> allWildcards = Wildcards.getInactiveWildcardsStr();
        allWildcards.add("*");
        return allWildcards;
    }

    public int assignSuperpower(CommandSourceStack source, Collection<ServerPlayer> targets, String name) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (name == null) {
            for (ServerPlayer player : targets) {
                SuperpowersWildcard.preAssignedSuperpowers.remove(player.getUUID());
            }
            if (targets.size() == 1) {
                OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_ASSIGN_RESET_SINGLE.get(targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_ASSIGN_RESET_MULTIPLE.get(targets.size()));
            }
            return 1;
        }

        if (!Superpowers.getImplementedStr().contains(name)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_SUPERPOWER_INVALID.get());
            return -1;
        }
        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NULL) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_SUPERPOWER_INVALID.get());
            return -1;
        }

        for (ServerPlayer player : targets) {
            SuperpowersWildcard.preAssignedSuperpowers.put(player.getUUID(), superpower);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_ASSIGN_SINGLE.get(targets.iterator().next(), name));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_ASSIGN_MULTIPLE.get(targets.size(), name));
        }
        return 1;
    }

    public int skipSuperpowerCooldown(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ServerPlayer player = source.getPlayer();
        if (player == null) return -1;
        Superpower superpower = SuperpowersWildcard.getSuperpowerInstance(player);
        if (superpower == null) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_SUPERPOWER_INACTIVE.get());
            return -1;
        }
        superpower.cooldown = 0;
        SimplePackets.SUPERPOWER_COOLDOWN.target(player).sendToClient(0);

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_COOLDOWN.get());
        return 1;
    }

    public int setRandomSuperpowers(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        SuperpowersWildcard.rollRandomSuperpowers();
        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_RANDOMIZE.get());
        return 1;
    }

    public int setRandomSuperpowers(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        SuperpowersWildcard.rollRandomSuperpowers(new ArrayList<>(targets));
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_RANDOMIZE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_RANDOMIZE_MULTIPLE.get(targets.size()));
        }
        return 1;
    }

    public int resetSuperpowers(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayer player : targets) {
            SuperpowersWildcard.resetSuperpower(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_DEACTIVATE_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_DEACTIVATE_MULTIPLE.get(targets.size()));
        }
        return 1;
    }

    public int getSuperpower(CommandSourceStack source, ServerPlayer player) {
        if (checkBanned(source)) return -1;
        Superpowers superpower = SuperpowersWildcard.getSuperpower(player);
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SUPERPOWER_GET.get(player, superpower.getString()));
        return 1;
    }

    public int setSuperpower(CommandSourceStack source, Collection<ServerPlayer> targets, String name) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!Superpowers.getImplementedStr().contains(name)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_SUPERPOWER_INVALID.get());
            return -1;
        }

        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NULL) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_SUPERPOWER_INVALID.get());
            return -1;
        }

        for (ServerPlayer player : targets) {
            SuperpowersWildcard.setSuperpower(player, superpower);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_SET_SINGLE.get(targets.iterator().next(), name));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SUPERPOWER_SET_MULTIPLE.get(name, targets.size()));
        }
        return 1;
    }

    public int setSnailName(CommandSourceStack source, ServerPlayer player, String name) {
        if (checkBanned(source)) return -1;
        Snails.setSnailName(player, name);
        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SNAIL_NAME_SET.get(player, name));
        return 1;
    }

    public int resetSnailName(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayer player : targets) {
            Snails.resetSnailName(player);
        }

        if (targets.size() == 1) {
            ServerPlayer player = targets.iterator().next();
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SNAIL_NAME_RESET_SINGLE.get(player, ModifiableText.WILDLIFE_SNAIL_DEFAULT_NAME.getString(player)));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_SNAIL_NAME_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    public int getSnailNames(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (targets.size() == 1) {
            ServerPlayer player = targets.iterator().next();
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SNAIL_NAME_GET.get(player, Snails.getSnailName(player)));
        }
        else {
            for (ServerPlayer player : targets) {
                OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_SNAIL_NAME_GET.get(player, Snails.getSnailName(player)));
            }
        }
        return 1;
    }

    public int deactivateWildcard(CommandSourceStack source, String wildcardName) {
        if (checkBanned(source)) return -1;
        if (wildcardName.equalsIgnoreCase("*")) {
            WildcardManager.onSessionEnd();
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_WILDCARD_DEACTIVATE_ALL.get());
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_INVALID.get());
            return -1;
        }
        if (!WildcardManager.isActiveWildcard(wildcard)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_INVALID.get());
            return -1;
        }
        WildcardManager.fadedWildcard();
        Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
        wildcardInstance.deactivate();
        WildcardManager.activeWildcards.remove(wildcard);

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_WILDCARD_DEACTIVATE.get(wildcardName));
        NetworkHandlerServer.sendUpdatePackets();
        return 1;
    }

    public int activateWildcard(CommandSourceStack source, String wildcardName) {
        if (checkBanned(source)) return -1;
        if (wildcardName.equalsIgnoreCase("*")) {
            List<Wildcards> inactiveWildcards = Wildcards.getInactiveWildcards();
            for (Wildcards wildcard : inactiveWildcards) {
                if (wildcard == Wildcards.CALLBACK) continue;
                Wildcard wildcardInstance = wildcard.getInstance();
                if (wildcardInstance == null) continue;
                WildcardManager.activeWildcards.put(wildcard, wildcardInstance);
            }

            WildcardManager.showDots();
            TaskScheduler.scheduleTask(90, () -> {
                for (Wildcard wildcard : WildcardManager.activeWildcards.values()) {
                    if (wildcard.active) continue;
                    wildcard.activate();
                }
                WildcardManager.showRainbowCryptTitle(ModifiableText.WILDLIFE_WILDCARD_ACTIVATE_ALL_TITLE.getString());
            });
            NetworkHandlerServer.sendUpdatePackets();

            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_WILDCARD_ACTIVATE_ALL.get());
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_INVALID.get());
            return -1;
        }
        if (WildcardManager.isActiveWildcard(wildcard)) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_ACTIVATE_ERROR.get());
            return -1;
        }
        Wildcard actualWildcard = wildcard.getInstance();
        if (actualWildcard == null) {
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_WILDCARD_IMPLEMENT_ERROR.get());
            return -1;
        }
        TaskScheduler.scheduleTask(89, () -> WildcardManager.activeWildcards.put(wildcard, actualWildcard));
        WildcardManager.activateWildcards();

        OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_WILDCARD_ACTIVATE.get(wildcardName));
        return 1;
    }

    public int listWildcards(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_WILDCARD_AVAILABLE.get(Wildcards.getWildcardsStr()));
        return 1;
    }

    public int listActiveWildcards(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (Wildcards.getActiveWildcardsStr().isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_WILDCARD_ACTIVATED_NONE.get());
            return 1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, ModifiableText.WILDLIFE_WILDCARD_ACTIVATED.get(Wildcards.getActiveWildcardsStr()));
        return 1;
    }
}
