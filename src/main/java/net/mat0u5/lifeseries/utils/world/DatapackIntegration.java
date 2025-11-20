package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.seasons.season.secretlife.TaskTypes;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.ScoreHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class DatapackIntegration {
    private static final String SCOREBOARD_WILDCARDS = "Wildcards";
    private static final String SCOREBOARD_SUPERPOWERS = "PlayerSuperpowers";
    private static final String SCOREBOARD_SESSION_INFO = "Session";
    private static final String SCOREBOARD_TASK_DIFFICULTY = "TaskDifficulty";

    public static final Events EVENT_TEST = Events.TEST;
    public static final Events EVENT_TEST2 = Events.TEST2;

    public static void reload() {
        EVENT_TEST.reload();
        EVENT_TEST2.reload();
    }

    public static List<Events> getAllEvents() {
        return List.of(
                EVENT_TEST
                ,EVENT_TEST2
        );
    }

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

    public enum Events {
        TEST("event_test", "Test Event", "Test Description", true),
        TEST2("event_test2", "Test Event2", "Test Description2", false);

        @Nullable
        String command;
        @Nullable
        String canceled;
        final String eventName;
        final String displayName;
        final String description;
        final boolean cancellable;

        Events(String eventName, String displayName, String description, boolean cancellable) {
            this.eventName = eventName;
            this.displayName = displayName;
            this.description = description;
            this.cancellable = cancellable;
        }

        public void reload() {
            command = seasonConfig.getProperty(eventName);
            if (cancellable) {
                canceled = seasonConfig.getOrCreateProperty(eventName+"_canceled", "false");
            }
        }

        public boolean isCanceled() {
            if (!cancellable) return false;
            return canceled != null && canceled.equalsIgnoreCase("true");
        }

        public boolean hasCommand() {
            return command != null && !command.isEmpty();
        }

        @NotNull
        public String getCanceled() {
            if (!cancellable || canceled == null) return "";
            return canceled;
        }

        public String getEventName() {
            return eventName;
        }

        @NotNull
        public String getCommand() {
            if (command == null) return "";
            return command;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }
}
