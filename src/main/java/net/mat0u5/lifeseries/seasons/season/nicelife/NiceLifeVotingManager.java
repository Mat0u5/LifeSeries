package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.livesManager;

public class NiceLifeVotingManager {
    public static Random rnd = new Random();
    public static TriviaVoteType voteType = TriviaVoteType.NAUGHTY_LIST;
    public static double NICE_LIST_CHANCE = 0.5; //TODO config
    public static Time VOTING_TIME = Time.minutes(10); //TODO config
    public static boolean REDS_ON_NAUGHTY_LIST = false;
    public static List<UUID> allowedToVote = new ArrayList<>();
    public static List<UUID> playersVoted = new ArrayList<>();

    public enum TriviaVoteType {
        NICE_LIST,
        NAUGHTY_LIST,
        NONE //TODO logic for choosing this
    }

    public static void reset() {
        allowedToVote.clear();
        playersVoted.clear();
    }

    public static void chooseVote() {
        voteType = NiceLifeVotingManager.TriviaVoteType.NAUGHTY_LIST;
        if (rnd.nextDouble() <= NICE_LIST_CHANCE) {
            if (livesManager.anyPlayersOnLives(2) || REDS_ON_NAUGHTY_LIST) {
                voteType = NiceLifeVotingManager.TriviaVoteType.NICE_LIST;
            }
        }
    }


    public static void handleVote(ServerPlayer player, String vote) {

    }

    public static void endVoting() {
        //TODO
        reset();
    }
}
