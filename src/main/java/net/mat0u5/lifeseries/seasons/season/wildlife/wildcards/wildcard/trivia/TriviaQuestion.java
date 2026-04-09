package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import java.util.List;

public class TriviaQuestion {
    private String question;
    private List<String> answers;
    private int correct_answer_index;

    public TriviaQuestion(String question, List<String> answers, int correct_answer_index) {
        this.question = question;
        this.answers = answers;
        this.correct_answer_index = correct_answer_index;
        if (this.question != null) {
            this.question = this.question.trim();
        }
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

    public static TriviaQuestion getDefault() {
        return new TriviaQuestion("Something seems to have gone wrong!", List.of("placeholder1","placeholder2","placeholder3","placeholder4"), 0);
    }
}
