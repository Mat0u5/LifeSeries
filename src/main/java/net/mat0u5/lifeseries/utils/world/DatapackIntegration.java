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

    public static final Events EVENT_PLAYER_JOIN = Events.PLAYER_JOIN;
    public static final Events EVENT_PLAYER_LEAVE = Events.PLAYER_LEAVE;
    public static final Events EVENT_PLAYER_DEATH = Events.PLAYER_DEATH;
    public static final Events EVENT_PLAYER_PVP_KILLED = Events.PLAYER_PVP_KILLED;
    public static final Events EVENT_CLAIM_KILL = Events.CLAIM_KILL;
    public static final Events EVENT_SESSION_CHANGE_STATUS = Events.SESSION_CHANGE_STATUS;
    public static final Events EVENT_BOOGEYMAN_ADDED = Events.BOOGEYMAN_ADDED;
    public static final Events EVENT_BOOGEYMAN_CURE_REWARD = Events.BOOGEYMAN_CURE_REWARD;
    public static final Events EVENT_BOOGEYMAN_FAIL_REWARD = Events.BOOGEYMAN_FAIL_REWARD;
    public static final Events EVENT_SOCIETY_MEMBER_ADDED = Events.SOCIETY_MEMBER_ADDED;
    public static final Events EVENT_SOCIETY_SUCCESS_REWARD = Events.SOCIETY_SUCCESS_REWARD;
    public static final Events EVENT_SOCIETY_FAIL_REWARD = Events.SOCIETY_FAIL_REWARD;
    public static final Events EVENT_TASK_SUCCEED = Events.TASK_SUCCEED;
    public static final Events EVENT_TASK_FAIL = Events.TASK_FAIL;
    public static final Events EVENT_TASK_REROLL = Events.TASK_REROLL;
    public static final Events EVENT_WILDCARD_ACTIVATE = Events.WILDCARD_ACTIVATE;
    public static final Events EVENT_WILDCARD_DEACTIVATE = Events.WILDCARD_DEACTIVATE;
    public static final Events EVENT_TRIVIA_BOT_SPAWN = Events.TRIVIA_BOT_SPAWN;

    public static void reload() {
        EVENT_PLAYER_JOIN.reload();
        EVENT_PLAYER_LEAVE.reload();
        EVENT_PLAYER_DEATH.reload();
        EVENT_PLAYER_PVP_KILLED.reload();
        EVENT_CLAIM_KILL.reload();
        EVENT_SESSION_CHANGE_STATUS.reload();
        EVENT_BOOGEYMAN_ADDED.reload();
        EVENT_BOOGEYMAN_CURE_REWARD.reload();
        EVENT_BOOGEYMAN_FAIL_REWARD.reload();
        EVENT_SOCIETY_MEMBER_ADDED.reload();
        EVENT_SOCIETY_SUCCESS_REWARD.reload();
        EVENT_SOCIETY_FAIL_REWARD.reload();
        EVENT_TASK_SUCCEED.reload();
        EVENT_TASK_FAIL.reload();
        EVENT_TASK_REROLL.reload();
        EVENT_WILDCARD_ACTIVATE.reload();
        EVENT_WILDCARD_DEACTIVATE.reload();
        EVENT_TRIVIA_BOT_SPAWN.reload();
    }

    public static List<Events> getAllEvents() {
        return List.of(
                EVENT_PLAYER_JOIN
                ,EVENT_PLAYER_LEAVE
                ,EVENT_PLAYER_DEATH
                ,EVENT_PLAYER_PVP_KILLED
                ,EVENT_CLAIM_KILL
                ,EVENT_SESSION_CHANGE_STATUS
                ,EVENT_BOOGEYMAN_ADDED
                ,EVENT_BOOGEYMAN_CURE_REWARD
                ,EVENT_BOOGEYMAN_FAIL_REWARD
                ,EVENT_SOCIETY_MEMBER_ADDED
                ,EVENT_SOCIETY_SUCCESS_REWARD
                ,EVENT_SOCIETY_FAIL_REWARD
                ,EVENT_TASK_SUCCEED
                ,EVENT_TASK_FAIL
                ,EVENT_TASK_REROLL
                ,EVENT_WILDCARD_ACTIVATE
                ,EVENT_WILDCARD_DEACTIVATE
                ,EVENT_TRIVIA_BOT_SPAWN
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
        PLAYER_JOIN("player_join", "Player Join", "Triggers when a player joins the game.", false),
        PLAYER_LEAVE("player_leave", "Player Leave", "Triggers when a player leaves the game.", false),
        PLAYER_DEATH("player_death_punishment", "Player Death §7Punishment", "Triggers when a player dies.", true),
        PLAYER_PVP_KILLED("player_pvp_killed_reward", "Player PvP Killed §7Reward", "Triggers when a killer kills a victim.", true),
        CLAIM_KILL("claim_kill", "Claim Kill §7Reward", "Triggers when a killer uses '/claimkill' for killing a victim", true),
        SESSION_CHANGE_STATUS("session_status_change", "Session Change Status", "Triggers when the session changes status.", false),
        BOOGEYMAN_ADDED("boogeyman_added", "Boogeyman Added", "Triggers when a boogeyman is added.", false),
        BOOGEYMAN_CURE_REWARD("boogeyman_cure_reward", "Boogeyman Cure §7Reward", "Triggers when a boogeyman cures", true),
        BOOGEYMAN_FAIL_REWARD("boogeyman_fail_reward", "Boogeyman Fail §7Punishment", "Triggers when a boogeyman fails.", true),
        SOCIETY_MEMBER_ADDED("society_member_added", "Society Member Added", "Triggers when a secret society member is added.", false),
        SOCIETY_SUCCESS_REWARD("society_success_reward", "Secret Society Success §7Reward", "Triggers when a society member succeeds.", true),
        SOCIETY_FAIL_REWARD("society_fail_reward", "Secret Society Fail §7Punishment", "Triggers when a society member fails.", true),
        TASK_SUCCEED("task_succeed", "Task Succeed", "Triggers when a player succeeds their task in Secret Life.", false),
        TASK_FAIL("task_fail", "Task Fail", "Triggers when a player fails their task in Secret Life.", false),
        TASK_REROLL("task_reroll", "Task Reroll", "Triggers when a player rerolls their task in Secret Life.", false),
        WILDCARD_ACTIVATE("wildcard_activate", "Wildcard Activate", "Triggers when a wildcard activates.", false),
        WILDCARD_DEACTIVATE("wildcard_deactivate", "Wildcard Deactivate", "Triggers when a wildcard activates.", false),
        TRIVIA_BOT_SPAWN("trivia_bot_spawn", "Trivia Bot Spawn", "Triggers when a trivia bot spawns for a player.", false);

        @Nullable
        String command;
        @Nullable
        String canceled;
        final String eventName;
        final String displayName;
        final String description;
        final boolean cancellable;

        Events(String eventName, String displayName, String description, boolean cancellable) {
            this.eventName = "event_"+eventName;
            this.displayName = displayName;
            this.cancellable = cancellable;

            if (!cancellable) {
                this.description = description+"\nNot cancellable.";
            }
            else {
                this.description = description;
            }
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
