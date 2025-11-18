package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.seasons.season.secretlife.TaskTypes;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.ScoreHolder;

public class DatapackIntegration {
    public static final String SCOREBOARD_WILDCARDS = "Wildcards";
    public static final String SCOREBOARD_SUPERPOWERS = "PlayerSuperpowers";
    public static final String SCOREBOARD_SESSION_INFO = "Session";
    public static final String SCOREBOARD_TASK_DIFFICULTY = "TaskDifficulty";

    public static void createScoreboards() {
        ScoreboardUtils.createObjective(SCOREBOARD_WILDCARDS);
        ScoreboardUtils.createObjective(SCOREBOARD_SUPERPOWERS);
        ScoreboardUtils.createObjective(SCOREBOARD_SESSION_INFO);
        ScoreboardUtils.createObjective(SCOREBOARD_TASK_DIFFICULTY);
    }

    public static void initWildcards() {
        for (Wildcards wildcard : Wildcards.getWildcards()) {
            ScoreboardUtils.setScore(ScoreHolder.forNameOnly(wildcard.getStringName()), SCOREBOARD_WILDCARDS, 0);
        }
    }

    public static void activateWildcard(Wildcards wildcard) {
        int index = wildcard.getIndex();
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly(wildcard.getStringName()), SCOREBOARD_WILDCARDS, 1);
    }

    public static void deactivateWildcard(Wildcards wildcard) {
        int index = wildcard.getIndex();
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly(wildcard.getStringName()), SCOREBOARD_WILDCARDS, 0);
    }

    public static void initSuperpowers() {
        ScoreboardUtils.removeObjective(SCOREBOARD_SUPERPOWERS);
        ScoreboardUtils.createObjective(SCOREBOARD_SUPERPOWERS);
    }

    public static void activateSuperpower(ServerPlayer player, Superpowers power) {
        int index = power.getIndex();
        ScoreboardUtils.setScore(player, SCOREBOARD_SUPERPOWERS, index);
    }

    public static void deactivateSuperpower(ServerPlayer player) {
        ScoreboardUtils.setScore(player, SCOREBOARD_SUPERPOWERS, 0);
    }

    public static void setPlayerTask(ServerPlayer player, TaskTypes type) {
        int index = 0;
        if (type == TaskTypes.EASY) index = 1;
        else if (type == TaskTypes.HARD) index = 2;
        else if (type == TaskTypes.RED) index = 3;
        ScoreboardUtils.setScore(player, SCOREBOARD_TASK_DIFFICULTY, index);
    }

    public static void changeSessionStatus(SessionStatus status) {
        int index = 0;
        if (status == SessionStatus.NOT_STARTED) index = 1;
        if (status == SessionStatus.STARTED) index = 2;
        if (status == SessionStatus.PAUSED) index = 3;
        if (status == SessionStatus.FINISHED) index = 4;
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly("Status"), SCOREBOARD_SESSION_INFO, index);
    }
    public static void setSessionLength(int ticks) {
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly("Length"), SCOREBOARD_SESSION_INFO, ticks);
    }
    public static void setSessionTimePassed(int ticks) {
        ScoreboardUtils.setScore(ScoreHolder.forNameOnly("PassedTime"), SCOREBOARD_SESSION_INFO, ticks);
    }
}
