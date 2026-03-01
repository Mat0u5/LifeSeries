package net.mat0u5.lifeseries.seasons.season.wildlife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
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

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class WildLifeTriviaCommand extends Command {

    @Override
    public boolean isAllowed() {
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
                                .requires(source -> isAllowed())
                                .then(argument("player", EntityArgument.players())
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
                                .requires(source -> isAllowed())
                                .then(literal("spawn")
                                        .then(argument("player", EntityArgument.players())
                                                .executes(context -> spawnBotFor(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "player")
                                                ))
                                        )
                                )
                        )
                        .then(literal("punishment")
                                .requires(source -> isAllowed())
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
                                                                        "random"
                                                                        ,"slippery_ground"
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

    private static Random rnd = new Random();
    public int setPunishment(CommandSourceStack source, Collection<ServerPlayer> targets, String punishment) {
        if (checkBanned(source)) return -1;
        if (!CompatibilityManager.voicechatLoaded() && punishment.equals("robotic_voice")) {
            OtherUtils.sendCommandFailure(source, ModifiableText.MOD_SVC_MISSING_SERVER.get());
            return -1;
        }

        int totalSVC = 0;


        for (ServerPlayer player : targets) {
            String playerPunishment = punishment;

            if (punishment.equalsIgnoreCase("random")) {
                List<String> possibleValues = new ArrayList<>(List.of(
                        "slippery_ground", "hunger", "beeswarm", "moonjump", "binding_armor", "ravager", "hearts"
                        //? if >= 1.21 {
                        , "infestation"
                        //?}
                        //? if > 1.20.3 {
                        , "gigantification"
                        //?}
                ));
                if (VoicechatMain.isConnectedToSVC(player.getUUID())) {
                    possibleValues.add("robotic_voice");
                }
                playerPunishment = possibleValues.get(rnd.nextInt(possibleValues.size()));
            }

            switch (playerPunishment) {
                //? if >= 1.21 {
                case "infestation":
                    WildLifeTriviaHandler.curseInfestation(player);
                    break;
                //?}
                case "slippery_ground":
                    WildLifeTriviaHandler.curseSlipperyGround(player);
                    break;
                case "hunger":
                    WildLifeTriviaHandler.curseHunger(player);
                    break;
                case "beeswarm":
                    WildLifeTriviaHandler.curseBeeswarm(player, player.blockPosition());
                    break;
                //? if > 1.20.3 {
                case "gigantification":
                    WildLifeTriviaHandler.curseGigantification(player);
                    break;
                //?}
                case "moonjump":
                    WildLifeTriviaHandler.curseMoonjump(player);
                    break;
                case "robotic_voice":
                    if (VoicechatMain.isConnectedToSVC(player.getUUID())) {
                        WildLifeTriviaHandler.curseRoboticVoice(player);
                        totalSVC++;
                    }
                    break;
                case "binding_armor":
                    WildLifeTriviaHandler.curseBindingArmor(player);
                    break;
                case "ravager":
                    WildLifeTriviaHandler.curseRavager(player, player.blockPosition());
                    break;
                default:
                case "hearts":
                    WildLifeTriviaHandler.curseHearts(player);
                    break;
            }
        }

        if (punishment.equals("robotic_voice") && totalSVC == 0) {
            if (targets.size() == 1) {
                OtherUtils.sendCommandFailure(source, ModifiableText.MOD_SVC_MISSING.get(targets.iterator().next()));
            }
            else {
                OtherUtils.sendCommandFailure(source, ModifiableText.MOD_SVC_MISSING_ALL.get());
            }
            return -1;
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_PUNISHMENT_SET_SINGLE.get(targets.iterator().next(), punishment));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_PUNISHMENT_SET_MULTIPLE.get(targets.size(), punishment));
        }
        return 1;
    }

    public int clearPunishment(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            TriviaWildcard.resetPlayerPunishments(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_PUNISHMENT_CLEAR_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_PUNISHMENT_CLEAR_MULTIPLE.get(targets.size()));
        }
        return 1;
    }

    public int spawnBotFor(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;

        for (ServerPlayer player : targets) {
            TriviaWildcard.spawnBotFor(player);
        }

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_BOT_SPAWN_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_BOT_SPAWN_MULTIPLE.get(targets.size()));
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
            OtherUtils.sendCommandFailure(source, ModifiableText.WILDLIFE_TRIVIA_QUESTION_INVALID.get());
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
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_SET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_SET_MULTIPLE.get(targets.size()));
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
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, ModifiableText.WILDLIFE_TRIVIA_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
}
