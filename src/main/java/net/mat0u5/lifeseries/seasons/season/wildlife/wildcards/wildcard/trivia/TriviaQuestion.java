package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TriviaQuestion {
    private String question;
    private List<String> answers;
    private int correct_answer_index;
    @Nullable
    private String player; // Optional

    public TriviaQuestion(String question, List<String> answers, int correct_answer_index) {
        this(question, answers, correct_answer_index, null);
    }

    public TriviaQuestion(String question, List<String> answers, int correct_answer_index, @Nullable String player) {
        this.question = question;
        this.answers = answers;
        this.correct_answer_index = correct_answer_index;
        if (this.question != null) {
            this.question = this.question.trim();
        }
        this.player = cleanPlayerName(player);
    }

    public String getQuestion() {
        if (this.question != null) {
            this.question = this.question.trim();
        }
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswerIndex() {
        return correct_answer_index;
    }

    @Nullable
    public String getPlayer() {
        return cleanPlayerName(player);
    }

    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    public boolean isForPlayer(@Nullable String playerName) {
        String questionPlayer = getPlayer();
        return questionPlayer != null && playerName != null && questionPlayer.equalsIgnoreCase(playerName.trim());
    }

    @Nullable
    private static String cleanPlayerName(@Nullable String player) {
        if (player == null) return null;
        player = player.trim();
        return player.isEmpty() ? null : player;
    }

    public static TriviaQuestion getDefault() {
        return new TriviaQuestion("Something seems to have gone wrong!", List.of("placeholder1","placeholder2","placeholder3","placeholder4"), 0);
    }
}
