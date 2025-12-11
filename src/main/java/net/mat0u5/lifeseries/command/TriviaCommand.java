package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.entity.triviabot.server.TriviaHandler;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class TriviaCommand extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.WILD_LIFE;
    }

    public boolean isWildLife() {
        return currentSeason.getSeason() == Seasons.WILD_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available in Wild Life.");
    }

    public List<String> getAdminCommands() {
        return List.of("trivia");
    }

    public List<String> getNonAdminCommands() {
        return List.of();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("trivia")
                        .requires(PermissionManager::isAdmin)
                        .then(literal("assign")
                                .then(argument("player", EntityArgument.players())
                                        .requires(context -> this.isWildLife())
                                        .then(argument("difficulty", StringArgumentType.string())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("easy","normal","hard"), builder))

                                                .then(argument("question", StringArgumentType.greedyString())
                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(this.getTriviaSuggestionsStr(StringArgumentType.getString(context, "difficulty")), builder))
                                                        .executes(context -> setTrivia(
                                                                        context.getSource(),
                                                                        EntityArgument.getPlayers(context, "player"),
                                                                        StringArgumentType.getString(context, "difficulty"),
                                                                        StringArgumentType.getString(context, "question")
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("reset")
                                                .executes(context -> resetTrivia(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "player")
                                                ))
                                        )
                                )
                        )
                        .then(literal("bot")
                                .then(literal("spawnFor")
                                        .then(argument("player", EntityArgument.players())
                                                .executes(context -> spawnBotFor(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "player")
                                                ))
                                        )
                                )
                        )
                        .then(literal("punishment")
                                .then(literal("clear")
                                        .then(argument("player", EntityArgument.players())
                                                .executes(context -> clearPunishment(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "player")
                                                ))
                                        )
                                )
                                .then(literal("set")
                                        .then(argument("player", EntityArgument.players())
                                                .then(argument("punishment", StringArgumentType.string())
                                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                                List.of(
                                                                        "slippery_ground"
                                                                        ,"hunger"
                                                                        ,"beeswarm"
                                                                        ,"moonjump"
                                                                        ,"robotic_voice"
                                                                        ,"binding_armor"
                                                                        ,"ravager"
                                                                        ,"hearts"
                                                                        //? if >= 1.21 {
                                                                        ,"infestation"
                                                                        //?}
                                                                        //? if > 1.20.3 {
                                                                        ,"gigantification"
                                                                        //?}
                                                                ), builder))

                                                        .executes(context -> setPunishment(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "player"),
                                                                StringArgumentType.getString(context, "punishment")
                                                        ))
                                                )
                                        )
                                )
                        )
        );
    }

    public int setPunishment(CommandSourceStack source, Collection<ServerPlayer> targets, String punishment) {
        if (checkBanned(source)) return -1;
        if (!CompatibilityManager.voicechatLoaded() && punishment.equals("robotic_voice")) {
            source.sendFailure(Component.nullToEmpty("The server does not have Simple Voice Chat installed"));
            return -1;
        }

        int totalSVC = 0;
        for (ServerPlayer player : targets) {
            switch (punishment) {

                //? if >= 1.21 {
                case "infestation":
                    TriviaHandler.curseInfestation(player);
                    break;
                //?}
                case "slippery_ground":
                    TriviaHandler.curseSlipperyGround(player);
                    break;
                case "hunger":
                    TriviaHandler.curseHunger(player);
                    break;
                case "beeswarm":
                    TriviaHandler.curseBeeswarm(player, player.blockPosition());
                    break;
                //? if > 1.20.3 {
                case "gigantification":
                    TriviaHandler.curseGigantification(player);
                    break;
                //?}
                case "moonjump":
                    TriviaHandler.curseMoonjump(player);
                    break;
                case "robotic_voice":
                    if (VoicechatMain.isConnectedToSVC(player.getUUID())) {
                        TriviaHandler.curseRoboticVoice(player);
                        totalSVC++;
                    }
                    break;
                case "binding_armor":
                    TriviaHandler.curseBindingArmor(player);
                    break;
                case "ravager":
                    TriviaHandler.curseRavager(player, player.blockPosition());
                    break;
                default:
                case "hearts":
                    TriviaHandler.curseHearts(player);
                    break;
            }
        }

        if (punishment.equals("robotic_voice") && totalSVC == 0) {
            if (targets.size() == 1) {
                source.sendFailure(TextUtils.format("{} does not have Simple Voice Chat installed", targets.iterator().next()));
            }
            else {
                source.sendFailure(Component.nullToEmpty("None of the targets have Simple Voice Chat installed"));
            }
            return -1;
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Punished {} with {}", targets.iterator().next(), punishment));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Punished {} targets with {}", targets.size(), punishment));
        }
        return 1;
    }

    public int clearPunishment(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            TriviaWildcard.resetPlayerPunishments(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Cleared {}'s trivia punishments", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Cleared trivia punishments for {} targets", targets.size()));
        }
        return 1;
    }

    public int spawnBotFor(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        for (ServerPlayer player : targets) {
            TriviaWildcard.spawnBotFor(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Spawned a trivia bot for {}", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Spawned a trivia bot for {} targets", targets.size()));
        }

        return 1;
    }


    public List<String> getTriviaSuggestionsStr(String questionType) {
        List<String> result = new ArrayList<>();
        for (TriviaQuestion question : getTriviaQuestions(questionType)) {
            result.add(question.getQuestion());
        }
        return result;
    }

    public List<TriviaQuestion> getTriviaQuestions(String questionType) {
        List<TriviaQuestion> result = new ArrayList<>();
        OtherUtils.log("Getting trivia suggestions");
        TriviaQuestionManager manager = null;
        if (questionType.equalsIgnoreCase("easy")) {
            manager = TriviaWildcard.easyTrivia;
        }
        else if (questionType.equalsIgnoreCase("normal")) {
            manager = TriviaWildcard.normalTrivia;
        }
        else if (questionType.equalsIgnoreCase("hard")) {
            manager = TriviaWildcard.hardTrivia;
        }
        if (manager != null) {
            try {
                result.addAll(manager.getTriviaQuestions());
            }catch(Exception e) {}
        }
        return result;
    }

    private int setTrivia(CommandSourceStack source, Collection<ServerPlayer> targets, String difficulty, String question) {
        if (checkBanned(source)) return -1;

        TriviaQuestion triviaQuestion = null;
        for (TriviaQuestion possibleQuestion : getTriviaQuestions(difficulty)) {
            if (possibleQuestion.getQuestion().equals(question)) {
                triviaQuestion = possibleQuestion;
                break;
            }
        }

        if (triviaQuestion == null) {
            source.sendFailure(Component.nullToEmpty("Could not find trivia with that question."));
            return -1;
        }

        int difficultyInt = 1;
        if (difficulty.equalsIgnoreCase("normal")) difficultyInt = 2;
        if (difficulty.equalsIgnoreCase("hard")) difficultyInt = 3;

        for (ServerPlayer player : targets) {
            UUID uuid = player.getUUID();
            TriviaWildcard.preAssignedTrivia.put(uuid, new Tuple<>(difficultyInt, triviaQuestion));
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Assigned {}'s trivia", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Assigned trivia of {} targets", targets.size()));
        }

        return 1;
    }

    private int resetTrivia(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            UUID uuid = player.getUUID();
            TriviaWildcard.preAssignedTrivia.remove(uuid);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset {}'s assigned trivia", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("Reset assigned trivia of {} targets", targets.size()));
        }

        return 1;
    }
}
