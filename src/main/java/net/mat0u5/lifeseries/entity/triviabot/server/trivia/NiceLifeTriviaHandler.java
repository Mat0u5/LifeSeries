package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.minecraft.server.level.ServerPlayer;

public class NiceLifeTriviaHandler extends TriviaHandler {
    public static ItemSpawner itemSpawner;
    public NiceLifeTriviaHandler(TriviaBot bot) {
        super(bot);
    }
    public Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayer boundPlayer) {
        return null; //TODO
    }

    public void setTimeBasedOnDifficulty(int difficulty) {
        //TODO
    }
    public static void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();//TODO
    }
}
