package net.mat0u5.lifeseries.seasons.season.nicelife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class NiceLifeTriviaCommand extends Command {

    @Override
    public boolean isAllowed() {
        return currentSeason.getSeason() == Seasons.NICE_LIFE;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available in Nice Life.");
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
                        .then(literal("set")
                                .requires(source -> isAllowed())
                                .then(argument("question", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(this.getTriviaSuggestionsStr(), builder))
                                        .executes(context -> setTrivia(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "question")
                                                )
                                        )
                                )
                                .then(literal("reset")
                                        .executes(context -> resetTrivia(
                                                context.getSource()
                                        ))
                                )
                        )
        );
    }


    public List<String> getTriviaSuggestionsStr() {
        List<String> result = new ArrayList<>();
        for (TriviaQuestion question : getTriviaQuestions()) {
            result.add(question.getQuestion());
        }
        return result;
    }

    public List<TriviaQuestion> getTriviaQuestions() {
        List<TriviaQuestion> result = new ArrayList<>();
        TriviaQuestionManager manager = NiceLifeTriviaManager.triviaQuestions;
        if (manager != null) {
            try {
                result.addAll(manager.getTriviaQuestions());
            }catch(Exception e) {}
        }
        return result;
    }

    private int setTrivia(CommandSourceStack source, String question) {
        if (checkBanned(source)) return -1;

        TriviaQuestion triviaQuestion = null;
        for (TriviaQuestion possibleQuestion : getTriviaQuestions()) {
            if (possibleQuestion.getQuestion().equals(question)) {
                triviaQuestion = possibleQuestion;
                break;
            }
        }

        if (triviaQuestion == null) {
            source.sendFailure(Component.nullToEmpty("Could not find trivia with that question."));
            return -1;
        }

        NiceLifeTriviaManager.preAssignedTrivia = triviaQuestion;

        OtherUtils.sendCommandFeedback(source, Component.literal("Successfuly assigned trivia"));

        return 1;
    }

    private int resetTrivia(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        NiceLifeTriviaManager.preAssignedTrivia = null;

        OtherUtils.sendCommandFeedback(source, Component.literal("Reset assigned trivia"));

        return 1;
    }
}
