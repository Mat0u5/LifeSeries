package net.mat0u5.lifeseries.utils.player;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.Collections;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class ScoreboardUtils {

    public static void createObjective(String name) {
        createObjective(name, name, ObjectiveCriteria.DUMMY);
    }

    public static void createObjective(String name, String displayName, ObjectiveCriteria criterion) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard.getObjective(name) != null) return;
        //? if <= 1.20 {
        scoreboard.addObjective(name, criterion, Component.literal(displayName), criterion.getDefaultRenderType());
        //?} else {
        /*scoreboard.addObjective(name, criterion, Component.literal(displayName), criterion.getDefaultRenderType(), false, null);
        *///?}
    }

    public static boolean existsObjective(String name) {
        if (server == null) return false;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getObjective(name) != null;
    }

    public static Objective getObjective(String name) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getObjective(name);
    }

    //? if <= 1.20 {
    public static Objective getObjectiveInSlot(int slot) {
    //?} else {
    /*public static Objective getObjectiveInSlot(DisplaySlot slot) {
    *///?}
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.getDisplayObjective(slot);
    }

    //? if <= 1.20 {
    public static void setObjectiveInSlot(int slot, String name) {
    //?} else {
    /*public static void setObjectiveInSlot(DisplaySlot slot, String name) {
    *///?}
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.setDisplayObjective(slot, scoreboard.getObjective(name));
    }

    public static void removeObjective(String name) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(name);
        if (objective == null) return;
        scoreboard.removeObjective(objective);
    }

    //? if > 1.20 {
    /*public static void setScore(String holder, String objectiveName, int score) {
        setScore(ScoreHolder.forNameOnly(holder), objectiveName, score);
    }
    *///?}

    //? if <= 1.20 {
    public static void setScore(String holder, String objectiveName, Integer score) {
    //?} else {
    /*public static void setScore(ScoreHolder holder, String objectiveName, Integer score) {
    *///?}
        if (livesManager != null && livesManager.LIVES_SYSTEM_DISABLED && objectiveName.equals(LivesManager.SCOREBOARD_NAME)) {
            return;
        }
        if (score == null) {
            resetScore(holder, objectiveName);
            return;
        }
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        //? if <= 1.20 {
        scoreboard.getOrCreatePlayerScore(holder, objective).setScore(score);
        //?} else {
        /*scoreboard.getOrCreatePlayerScore(holder, objective).set(score);
        *///?}
    }

    //? if <= 1.20 {
    public static void setScore(ServerPlayer holder, String objectiveName, int score) {
        setScore(holder.getScoreboardName(), objectiveName, score);
    }
    public static Collection<Score> getScores(String objectiveName) {
        if (server == null) return Collections.emptyList();
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return Collections.emptyList();
        return scoreboard.getPlayerScores(objective);
    }

    public static Integer getScore(ServerPlayer player, String objectiveName) {
        return getScore(player.getScoreboardName(), objectiveName);
    }

    public static Integer getScore(String holder, String objectiveName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return -1;
        Score score = scoreboard.getOrCreatePlayerScore(holder, objective);
        if (score == null) return null;
        return score.getScore();
    }

    public static void resetScore(ServerPlayer player, String objectiveName) {
        resetScore(player.getScoreboardName(), objectiveName);
    }

    public static void resetScore(String holder, String objectiveName) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        scoreboard.resetPlayerScore(holder, objective);
    }
    //?} else {
    /*public static Collection<PlayerScoreEntry> getScores(String objectiveName) {
        if (server == null) return Collections.emptyList();
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return Collections.emptyList();
        return scoreboard.listPlayerScores(objective);
    }

    public static Integer getScore(String holder, String objectiveName) {
        return getScore(ScoreHolder.forNameOnly(holder), objectiveName);
    }
    public static Integer getScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return -1;
        ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(holder, objective);
        if (score == null) return null;
        return score.value();
    }

    public static void resetScore(String holder, String objectiveName) {
        resetScore(ScoreHolder.forNameOnly(holder), objectiveName);
    }

    public static void resetScore(ScoreHolder holder, String objectiveName) {
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        scoreboard.resetSinglePlayerScore(holder, objective);
    }
    *///?}
}
