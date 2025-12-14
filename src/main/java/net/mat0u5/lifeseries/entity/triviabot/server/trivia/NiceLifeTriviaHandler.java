package net.mat0u5.lifeseries.entity.triviabot.server.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.utils.other.Tuple;
import net.mat0u5.lifeseries.utils.world.ItemSpawner;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        //TODO loot table
        itemSpawner = new ItemSpawner();//TODO
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
    }
}
