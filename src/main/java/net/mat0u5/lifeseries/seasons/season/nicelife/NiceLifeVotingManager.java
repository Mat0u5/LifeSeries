package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class NiceLifeVotingManager {
    public static Random rnd = new Random();
    public static TriviaVoteType voteType = TriviaVoteType.NAUGHTY_LIST;
    public static double NICE_LIST_CHANCE = 0.5;
    public static Time VOTING_TIME = Time.seconds(60);
    public static boolean REDS_ON_NAUGHTY_LIST = false;
    public static List<UUID> allowedToVote = new ArrayList<>();
    public static List<UUID> playersVoted = new ArrayList<>();
    public static Map<UUID, Integer> votes = new HashMap<>();

    public enum TriviaVoteType {
        NICE_LIST,
        NAUGHTY_LIST,
        NONE //TODO logic for choosing this
    }

    public static void reset() {
        allowedToVote.clear();
        playersVoted.clear();
        votes.clear();
    }

    public static void chooseVote() {
        voteType = TriviaVoteType.NAUGHTY_LIST;
        if (rnd.nextDouble() <= NICE_LIST_CHANCE) {
            if (livesManager.anyPlayersOnLives(2) || REDS_ON_NAUGHTY_LIST) {
                voteType = TriviaVoteType.NICE_LIST;
            }
        }
    }


    public static void handleVote(ServerPlayer player, String vote) {
        if (player == null) return;

        TriviaBot bot = NiceLifeTriviaManager.bots.get(player.getUUID());
        if (bot == null) return;
        if (!bot.isAlive()) return;
        if (!(bot.triviaHandler instanceof NiceLifeTriviaHandler triviaHandler)) return;
        if (triviaHandler.currentState != NiceLifeTriviaHandler.BotState.VOTING) return;
        triviaHandler.changeStateTo(NiceLifeTriviaHandler.BotState.LEAVING);


        if (!player.isSleeping()) return;
        if (player.ls$isDead()) return;
        if (!allowedToVote.contains(player.getUUID())) return;
        if (playersVoted.contains(player.getUUID())) return;
        if (vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (votedFor.ls$isDead()) return;
        if (voteType == TriviaVoteType.NICE_LIST && votedFor.ls$isOnSpecificLives(1, true) && !REDS_ON_NAUGHTY_LIST) return;


        playersVoted.add(player.getUUID());
        if (!votes.containsKey(votedFor.getUUID())) {
            votes.put(votedFor.getUUID(), 0);
        }
        votes.put(votedFor.getUUID(), votes.get(votedFor.getUUID())+1);
    }

    public static void endVoting() {
        //TODO
        reset();
    }
}
