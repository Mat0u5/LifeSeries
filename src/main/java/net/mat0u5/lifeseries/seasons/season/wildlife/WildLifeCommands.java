package net.mat0u5.lifeseries.seasons.season.wildlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.SnailSkins;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
    public Text getBannedText() {
        return Text.of("This command is only available when playing Wild Life.");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
                        .suggests((context, builder) -> CommandSource.suggestMatching(suggestionsActivateWildcard(), builder))
                        .executes(context -> activateWildcard(
                            context.getSource(), StringArgumentType.getString(context, "wildcard"))
                        )
                    )
                )
                .then(literal("deactivate")
                    .then(argument("wildcard", StringArgumentType.greedyString())
                        .suggests((context, builder) -> CommandSource.suggestMatching(suggestionsDeactivateWildcard(), builder))
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
        );
        dispatcher.register(
            literal("snail")
                .then(literal("names")
                    .then(literal("set")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgumentType.player())
                            .then(argument("name", StringArgumentType.greedyString())
                                .executes(context -> setSnailName(context.getSource(), EntityArgumentType.getPlayer(context, "player"), StringArgumentType.getString(context, "name")))
                            )
                        )
                    )
                    .then(literal("reset")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgumentType.players())
                            .executes(context -> resetSnailName(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                        )
                    )
                    .then(literal("get")
                        .then(argument("player", EntityArgumentType.players())
                            .executes(context -> getSnailNames(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
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
                    .then(argument("player", EntityArgumentType.players())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> CommandSource.suggestMatching(Superpowers.getImplementedStr(), builder))
                            .executes(context -> setSuperpower(context.getSource(), EntityArgumentType.getPlayers(context, "player"), StringArgumentType.getString(context, "superpower")))
                        )
                    )
                )
                .then(literal("reset")
                        .then(argument("player", EntityArgumentType.players())
                                .executes(context -> resetSuperpowers(context.getSource(), EntityArgumentType.getPlayers(context, "player")))
                        )
                )
                .then(literal("setRandom")
                    .executes(context -> setRandomSuperpowers(context.getSource()))
                )
                .then(literal("get")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(context -> getSuperpower(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                    )
                )
                .then(literal("skipCooldown")
                    .executes(context -> skipSuperpowerCooldown(context.getSource()))
                )
                .then(literal("assignForRandomization")
                    .then(argument("player", EntityArgumentType.players())
                        .then(argument("superpower", StringArgumentType.string())
                            .suggests((context, builder) -> CommandSource.suggestMatching(Superpowers.getImplementedStr(), builder))
                            .executes(context -> assignSuperpower(context.getSource(), EntityArgumentType.getPlayers(context, "player"), StringArgumentType.getString(context, "superpower")))
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

    public int activateFinale(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        WildcardManager.FINALE = true;
        OtherUtils.sendCommandFeedbackQuiet(source, Text.of("All wildcards will act as if the finale (so the Callback wildcard) was activated."));

        return 1;
    }

    public int randomizeFood(ServerCommandSource source) {
        if (checkBanned(source)) return -1;

        if (!WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            source.sendError(Text.of("The Hunger wildcard is not active right now."));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Text.of("§7Randomizing food..."));

        if (WildcardManager.activeWildcards.get(Wildcards.HUNGER) instanceof Hunger hungerWildcard) {
            hungerWildcard.newFoodRules();
        }

        return 1;
    }

    public int requestSnailName(ServerCommandSource source, String name) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        PlayerUtils.broadcastMessageToAdmins(TextUtils.format("{}§7 requests their snail name to be §f{}§7", player, name));
        Text adminText = TextUtils.format("§7Click {}§7 to accept.", TextUtils.runCommandText(TextUtils.formatString("/snail names set {} {}", player, name)));
        PlayerUtils.broadcastMessageToAdmins(adminText);
        return 1;
    }

    public int snailTexturesReload(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        OtherUtils.sendCommandFeedback(source, Text.of("§7Reloading snail textures..."));
        SnailSkins.sendTextures();

        return 1;
    }

    public int getSnailTexturesInfo(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;

        NetworkHandlerServer.sendStringPacket(player, PacketNames.SNAIL_TEXTURES_INFO ,"");

        return 1;
    }

    public int getSnailTextures(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        List<String> textures = SnailSkins.getAllSkins();
        if (textures.isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7No snail skins have been added yet. Run '§f/snail textures info§7' to learn how to add them."));
            return -1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("§7The following skins have been found: §f{}", textures));
        return 1;
    }

    public int chooseWildcard(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            source.sendError(Text.of("You must have the Life Series mod installed §nclient-side§r to open the wildcard GUI"));
            return -1;
        }

        OtherUtils.sendCommandFeedback(source, Text.of("§7Opening the Wildcard selection GUI..."));
        NetworkHandlerServer.sendStringPacket(source.getPlayer(), PacketNames.SELECT_WILDCARDS, "true");
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

    public int assignSuperpower(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String name) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;
        if (!Superpowers.getImplementedStr().contains(name)) {
            source.sendError(Text.of("That superpower doesn't exist"));
            return -1;
        }
        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NULL) {
            source.sendError(Text.of("That superpower doesn't exist"));
            return -1;
        }

        for (ServerPlayerEntity player : targets) {
            SuperpowersWildcard.assignedSuperpowers.put(player.getUuid(), superpower);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Forced {}'s superpower to be {} when the next superpower randomization happens", targets.iterator().next(), name));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Forced the superpower of {} targets to be {} when the next superpower randomization happens", targets.size(), name));
        }
        return 1;
    }

    public int skipSuperpowerCooldown(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return -1;
        Superpower superpower = SuperpowersWildcard.getSuperpowerInstance(player);
        if (superpower == null) {
            source.sendError(Text.of("You do not have an active superpower"));
            return -1;
        }
        superpower.cooldown = 0;
        NetworkHandlerServer.sendLongPacket(player, PacketNames.SUPERPOWER_COOLDOWN, 0);

        OtherUtils.sendCommandFeedback(source, Text.of("Your superpower cooldown has been skipped"));
        return 1;
    }

    public int setRandomSuperpowers(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        SuperpowersWildcard.rollRandomSuperpowers();
        OtherUtils.sendCommandFeedback(source, Text.of("Randomized everyone's superpowers"));
        return 1;
    }

    public int resetSuperpowers(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayerEntity player : targets) {
            SuperpowersWildcard.resetSuperpower(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Deactivated {}'s superpower", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Deactivated the superpower of {} targets", targets.size()));
        }
        return 1;
    }

    public int getSuperpower(ServerCommandSource source, ServerPlayerEntity player) {
        if (checkBanned(source)) return -1;
        Superpowers superpower = SuperpowersWildcard.getSuperpower(player);
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{}'s superpower is: {}", player,  superpower.getString()));
        return 1;
    }

    public int setSuperpower(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String name) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (!Superpowers.getImplementedStr().contains(name)) {
            source.sendError(Text.of("That superpower doesn't exist"));
            return -1;
        }

        Superpowers superpower = Superpowers.fromString(name);
        if (superpower == Superpowers.NULL) {
            source.sendError(Text.of("That superpower doesn't exist"));
            return -1;
        }

        for (ServerPlayerEntity player : targets) {
            SuperpowersWildcard.setSuperpower(player, superpower);
        }
        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s superpower to {}", targets.iterator().next(), name));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Set the superpower to {} for {} targets", name, targets.size()));
        }
        return 1;
    }

    public int setSnailName(ServerCommandSource source, ServerPlayerEntity player, String name) {
        if (checkBanned(source)) return -1;
        Snails.setSnailName(player, name);
        OtherUtils.sendCommandFeedback(source, TextUtils.format("Set {}'s snail name to {}", player, name));
        return 1;
    }

    public int resetSnailName(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        for (ServerPlayerEntity player : targets) {
            Snails.resetSnailName(player);
        }

        if (targets.size() == 1) {
            ServerPlayerEntity player = targets.iterator().next();
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s snail name to {}'s Snail", player, player.getNameForScoreboard()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset the snail name for {} targets", targets.size()));
        }

        return 1;
    }

    public int getSnailNames(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        if (checkBanned(source)) return -1;
        if (targets == null || targets.isEmpty()) return -1;

        if (targets.size() == 1) {
            ServerPlayerEntity player = targets.iterator().next();
            OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{}'s snail is called {}", player, Snails.getSnailName(player)));
        }
        else {
            for (ServerPlayerEntity player : targets) {
                OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("{}'s snail is called {}", player, Snails.getSnailName(player)));
            }
        }
        return 1;
    }

    public int deactivateWildcard(ServerCommandSource source, String wildcardName) {
        if (checkBanned(source)) return -1;
        if (wildcardName.equalsIgnoreCase("*")) {
            WildcardManager.onSessionEnd();
            OtherUtils.sendCommandFeedback(source, Text.of("Deactivated all wildcards"));
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            source.sendError(Text.of("That Wildcard doesn't exist"));
            return -1;
        }
        if (!WildcardManager.isActiveWildcard(wildcard)) {
            source.sendError(Text.of("That Wildcard is not active"));
            return -1;
        }
        WildcardManager.fadedWildcard();
        Wildcard wildcardInstance = WildcardManager.activeWildcards.get(wildcard);
        wildcardInstance.deactivate();
        WildcardManager.activeWildcards.remove(wildcard);

        OtherUtils.sendCommandFeedback(source, TextUtils.format("Deactivated {}", wildcardName));
        NetworkHandlerServer.sendUpdatePackets();
        return 1;
    }

    public int activateWildcard(ServerCommandSource source, String wildcardName) {
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
                WildcardManager.showRainbowCryptTitle("All wildcards are active!");
            });
            NetworkHandlerServer.sendUpdatePackets();

            OtherUtils.sendCommandFeedback(source, Text.of("Activated all wildcards (Except Callback)"));
            return 1;
        }
        Wildcards wildcard = Wildcards.getFromString(wildcardName);
        if (wildcard == Wildcards.NULL) {
            source.sendError(Text.of("That Wildcard doesn't exist"));
            return -1;
        }
        if (WildcardManager.isActiveWildcard(wildcard)) {
            source.sendError(Text.of("That Wildcard is already active"));
            return -1;
        }
        Wildcard actualWildcard = wildcard.getInstance();
        if (actualWildcard == null) {
            source.sendError(Text.of("That Wildcard has not been implemented yet"));
            return -1;
        }
        TaskScheduler.scheduleTask(89, () -> WildcardManager.activeWildcards.put(wildcard, actualWildcard));
        WildcardManager.activateWildcards();

        OtherUtils.sendCommandFeedback(source, TextUtils.format("Activated {}", wildcardName));
        return 1;
    }

    public int listWildcards(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Available Wildcards: {}", Wildcards.getWildcardsStr()));
        return 1;
    }

    public int listActiveWildcards(ServerCommandSource source) {
        if (checkBanned(source)) return -1;
        if (Wildcards.getActiveWildcardsStr().isEmpty()) {
            OtherUtils.sendCommandFeedbackQuiet(source, Text.of("§7There are no active Wildcards right now. \nYou will be able to select a Wildcard when you start a session, or you can use '§f/wildcard activate <wildcard>§7' to activate a specific Wildcard right now."));
            return 1;
        }
        OtherUtils.sendCommandFeedbackQuiet(source, TextUtils.format("Activated Wildcards: {}", Wildcards.getActiveWildcardsStr()));
        return 1;
    }
}
