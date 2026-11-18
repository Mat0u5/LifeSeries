package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.manager.CustomCommand;
import net.mat0u5.lifeseries.config.modifiable.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class InBetweenTriviaCommand extends CustomCommand {

    @Override
    public boolean isAllowed() {
        return LifeSeries.isSeason(Seasons.FINAL_LIFE);
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available in Final Life.");
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
                        .then(literal("preassign")
                                .requires(source -> isAllowed())
                                .then(argument("player", EntityArgument.players())
                                        .then(argument("question", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(this.getTriviaSuggestionsStr(), builder))
                                                .executes(context -> setTrivia(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "player"),
                                                                StringArgumentType.getString(context, "question")
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
        TriviaQuestionManager manager = InBetweenTriviaManager.triviaQuestions;
        if (manager != null) {
            try {
                result.addAll(manager.getTriviaQuestions());
            }catch(Exception e) {}
        }
        return result;
    }

    private int setTrivia(CommandSourceStack source, Collection<ServerPlayer> targets, String question) {
        if (checkBanned(source)) return -1;

        TriviaQuestion triviaQuestion = null;
        for (TriviaQuestion possibleQuestion : getTriviaQuestions()) {
            if (possibleQuestion.getQuestion().equals(question)) {
                triviaQuestion = possibleQuestion;
                break;
            }
        }

        if (triviaQuestion == null) {
            sendCommandFailure(source, ModifiableText.FINALLIFE_TRIVIA_QUESTION_INVALID.get());
            return -1;
        }

        for (ServerPlayer player : targets) {
            UUID uuid = player.getUUID();
            InBetweenTriviaManager.preAssignedTrivia.put(uuid, triviaQuestion);
        }

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.FINALLIFE_TRIVIA_SET_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.FINALLIFE_TRIVIA_SET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }

    private int resetTrivia(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        for (ServerPlayer player : targets) {
            UUID uuid = player.getUUID();
            InBetweenTriviaManager.preAssignedTrivia.remove(uuid);
        }

        if (targets.size() == 1) {
            sendCommandFeedback(source, ModifiableText.FINALLIFE_TRIVIA_RESET_SINGLE.get(targets.iterator().next()));
        }
        else {
            sendCommandFeedback(source, ModifiableText.FINALLIFE_TRIVIA_RESET_MULTIPLE.get(targets.size()));
        }

        return 1;
    }
}
