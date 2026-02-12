package net.mat0u5.lifeseries.utils.world;

import net.mat0u5.lifeseries.seasons.season.secretlife.TaskTypes;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.session.SessionStatus;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class DatapackIntegration {
    private static final String SCOREBOARD_WILDCARDS = "Wildcards";
    private static final String SCOREBOARD_SUPERPOWERS = "PlayerSuperpowers";
    private static final String SCOREBOARD_SESSION_INFO = "Session";
    private static final String SCOREBOARD_TASK_DIFFICULTY = "TaskDifficulty";

    public static final Events EVENT_PLAYER_JOIN = Events.PLAYER_JOIN;
    public static final Events EVENT_PLAYER_LEAVE = Events.PLAYER_LEAVE;
    public static final Events EVENT_PLAYER_TAKE_DAMAGE = Events.PLAYER_TAKE_DAMAGE;
    public static final Events EVENT_PLAYER_DEATH = Events.PLAYER_DEATH;
    public static final Events EVENT_PLAYER_FINAL_DEATH = Events.PLAYER_FINAL_DEATH;
    public static final Events EVENT_PLAYER_PVP_KILLED = Events.PLAYER_PVP_KILLED;
    public static final Events EVENT_CLAIM_KILL = Events.CLAIM_KILL;
    public static final Events EVENT_SESSION_CHANGE_STATUS = Events.SESSION_CHANGE_STATUS;
    public static final Events EVENT_SESSION_START = Events.SESSION_START;
    public static final Events EVENT_SESSION_PAUSE = Events.SESSION_PAUSE;
    public static final Events EVENT_SESSION_UNPAUSE = Events.SESSION_UNPAUSE;
    public static final Events EVENT_SESSION_END = Events.SESSION_END;
    public static final Events EVENT_BOOGEYMAN_ADDED = Events.BOOGEYMAN_ADDED;
    public static final Events EVENT_BOOGEYMAN_CURE_REWARD = Events.BOOGEYMAN_CURE_REWARD;
    public static final Events EVENT_BOOGEYMAN_FAIL_REWARD = Events.BOOGEYMAN_FAIL_REWARD;
    public static final Events EVENT_BOOGEYMAN_KILL = Events.BOOGEYMAN_KILL;
    public static final Events EVENT_SOCIETY_MEMBER_ADDED = Events.SOCIETY_MEMBER_ADDED;
    public static final Events EVENT_SOCIETY_SUCCESS_REWARD = Events.SOCIETY_SUCCESS_REWARD;
    public static final Events EVENT_SOCIETY_FAIL_REWARD = Events.SOCIETY_FAIL_REWARD;
    public static final Events EVENT_TASK_SUCCEED = Events.TASK_SUCCEED;
    public static final Events EVENT_TASK_FAIL = Events.TASK_FAIL;
    public static final Events EVENT_TASK_REROLL = Events.TASK_REROLL;
    public static final Events EVENT_WILDCARD_ACTIVATE = Events.WILDCARD_ACTIVATE;
    public static final Events EVENT_WILDCARD_DEACTIVATE = Events.WILDCARD_DEACTIVATE;
    public static final Events EVENT_TRIVIA_BOT_SPAWN = Events.TRIVIA_BOT_SPAWN;
    public static final Events EVENT_TRIVIA_BOT_OPEN = Events.TRIVIA_BOT_OPEN;
    public static final Events EVENT_TRIVIA_SUCCEED = Events.TRIVIA_SUCCEED;
    public static final Events EVENT_TRIVIA_FAIL = Events.TRIVIA_FAIL;
    public static final Events EVENT_SUPERPOWER_TRIGGER = Events.SUPERPOWER_TRIGGER;

    public static final Events EVENT_NICE_LIST_ADD = Events.NICE_LIST_ADD;
    public static final Events EVENT_NICE_LIST_END = Events.NICE_LIST_END;
    public static final Events EVENT_NAUGHTY_LIST_ADD = Events.NAUGHTY_LIST_ADD;
    public static final Events EVENT_NAUGHTY_LIST_END = Events.NAUGHTY_LIST_END;

    public static void reload() {
        EVENT_PLAYER_JOIN.reload();
        EVENT_PLAYER_LEAVE.reload();
        EVENT_PLAYER_TAKE_DAMAGE.reload();
        EVENT_PLAYER_DEATH.reload();
        EVENT_PLAYER_FINAL_DEATH.reload();
        EVENT_PLAYER_PVP_KILLED.reload();
        EVENT_CLAIM_KILL.reload();
        EVENT_SESSION_CHANGE_STATUS.reload();
        EVENT_SESSION_START.reload();
        EVENT_SESSION_PAUSE.reload();
        EVENT_SESSION_UNPAUSE.reload();
        EVENT_SESSION_END.reload();
        EVENT_BOOGEYMAN_ADDED.reload();
        EVENT_BOOGEYMAN_CURE_REWARD.reload();
        EVENT_BOOGEYMAN_FAIL_REWARD.reload();
        EVENT_BOOGEYMAN_KILL.reload();
        EVENT_SOCIETY_MEMBER_ADDED.reload();
        EVENT_SOCIETY_SUCCESS_REWARD.reload();
        EVENT_SOCIETY_FAIL_REWARD.reload();
        EVENT_TASK_SUCCEED.reload();
        EVENT_TASK_FAIL.reload();
        EVENT_TASK_REROLL.reload();
        EVENT_WILDCARD_ACTIVATE.reload();
        EVENT_WILDCARD_DEACTIVATE.reload();
        EVENT_TRIVIA_BOT_SPAWN.reload();
        EVENT_TRIVIA_BOT_OPEN.reload();
        EVENT_TRIVIA_SUCCEED.reload();
        EVENT_TRIVIA_FAIL.reload();
        EVENT_SUPERPOWER_TRIGGER.reload();
        EVENT_NICE_LIST_ADD.reload();
        EVENT_NICE_LIST_END.reload();
        EVENT_NAUGHTY_LIST_ADD.reload();
        EVENT_NAUGHTY_LIST_END.reload();
    }

    public static List<Events> getAllEvents() {
        return List.of(
                EVENT_PLAYER_JOIN
                ,EVENT_PLAYER_LEAVE
                ,EVENT_PLAYER_TAKE_DAMAGE
                ,EVENT_PLAYER_DEATH
                ,EVENT_PLAYER_FINAL_DEATH
                ,EVENT_PLAYER_PVP_KILLED
                ,EVENT_CLAIM_KILL
                ,EVENT_SESSION_CHANGE_STATUS
                ,EVENT_SESSION_START
                ,EVENT_SESSION_PAUSE
                ,EVENT_SESSION_UNPAUSE
                ,EVENT_SESSION_END
                ,EVENT_BOOGEYMAN_ADDED
                ,EVENT_BOOGEYMAN_CURE_REWARD
                ,EVENT_BOOGEYMAN_FAIL_REWARD
                ,EVENT_BOOGEYMAN_KILL
                ,EVENT_SOCIETY_MEMBER_ADDED
                ,EVENT_SOCIETY_SUCCESS_REWARD
                ,EVENT_SOCIETY_FAIL_REWARD
                ,EVENT_TASK_SUCCEED
                ,EVENT_TASK_FAIL
                ,EVENT_TASK_REROLL
                ,EVENT_WILDCARD_ACTIVATE
                ,EVENT_WILDCARD_DEACTIVATE
                ,EVENT_TRIVIA_BOT_SPAWN
                ,EVENT_TRIVIA_BOT_OPEN
                ,EVENT_TRIVIA_SUCCEED
                ,EVENT_TRIVIA_FAIL
                ,EVENT_SUPERPOWER_TRIGGER
                ,EVENT_NICE_LIST_ADD
                ,EVENT_NICE_LIST_END
                ,EVENT_NAUGHTY_LIST_ADD
                ,EVENT_NAUGHTY_LIST_END
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
            ScoreboardUtils.setScore(wildcard.getStringName(), SCOREBOARD_WILDCARDS, 0);
        }
    }

    public static void activateWildcard(Wildcards wildcard) {
        int index = wildcard.getIndex();
        ScoreboardUtils.setScore(wildcard.getStringName(), SCOREBOARD_WILDCARDS, 1);
        DatapackIntegration.EVENT_WILDCARD_ACTIVATE.trigger(new DatapackIntegration.Events.MacroEntry("Index", String.valueOf(index)));
    }

    public static void deactivateWildcard(Wildcards wildcard) {
        int index = wildcard.getIndex();
        ScoreboardUtils.setScore(wildcard.getStringName(), SCOREBOARD_WILDCARDS, 0);
        DatapackIntegration.EVENT_WILDCARD_DEACTIVATE.trigger(new DatapackIntegration.Events.MacroEntry("Index", String.valueOf(index)));
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

    public static void changeSessionStatus(SessionStatus prevStatus, SessionStatus status) {
        int index = 0;
        if (status == SessionStatus.NOT_STARTED) index = 1;
        if (status == SessionStatus.STARTED) index = 2;
        if (status == SessionStatus.PAUSED) index = 3;
        if (status == SessionStatus.FINISHED) index = 4;
        ScoreboardUtils.setScore("Status", SCOREBOARD_SESSION_INFO, index);
        DatapackIntegration.EVENT_SESSION_CHANGE_STATUS.trigger(
                new DatapackIntegration.Events.MacroEntry("Status", String.valueOf(index))
        );

        if (status == SessionStatus.FINISHED) DatapackIntegration.EVENT_SESSION_END.trigger();
        else if (status == SessionStatus.PAUSED && prevStatus != SessionStatus.PAUSED) DatapackIntegration.EVENT_SESSION_PAUSE.trigger();
        else if (prevStatus == SessionStatus.PAUSED && status != SessionStatus.PAUSED) DatapackIntegration.EVENT_SESSION_UNPAUSE.trigger();
        else if (status == SessionStatus.STARTED) DatapackIntegration.EVENT_SESSION_START.trigger();
    }
    public static void setSessionLength(Time time) {
        ScoreboardUtils.setScore("Length", SCOREBOARD_SESSION_INFO, time.getTicks());
    }
    public static void setSessionTimePassed(Time time) {
        ScoreboardUtils.setScore("PassedTime", SCOREBOARD_SESSION_INFO, time.getTicks());
    }

    public enum Events {
        PLAYER_JOIN("player_join", "Player Join", "Triggers when a player joins the game.\nAvailable macros: $(Player)", false),
        PLAYER_LEAVE("player_leave", "Player Leave", "Triggers when a player leaves the game.\nAvailable macros: $(Player)", false),
        PLAYER_TAKE_DAMAGE("player_take_damage", "Player Take Damage", "Triggers when a player takes damage.\nAvailable macros: $(Player), $(Amount)", false),
        PLAYER_DEATH("player_death_punishment", "Player Death §7Punishment", "Triggers when a player dies.\nAvailable macros: $(Player)", true),
        PLAYER_FINAL_DEATH("player_final_death", "Player Final Death", "Triggers when a player final dies.\nAvailable macros: $(Player)", false),
        PLAYER_PVP_KILLED("player_pvp_killed_reward", "Player PvP Killed §7Reward", "Triggers when a killer kills a victim.\nAvailable macros: $(Killer), $(Victim)", true),
        CLAIM_KILL("claim_kill", "Claim Kill §7Reward", "Triggers when a killer uses '/claimkill' for killing a victim.\nAvailable macros: $(Killer), $(Victim)", true),
        SESSION_CHANGE_STATUS("session_status_change", "Session Change Status", "Triggers when the session changes status.\nAvailable macros: $(Status)", false),
        SESSION_START("session_start", "Session Start", "Triggers when a session starts.", false),
        SESSION_PAUSE("session_pause", "Session Pause", "Triggers when a session is paused.", false),
        SESSION_UNPAUSE("session_unpause", "Session Unpause", "Triggers when a session is unpaused.", false),
        SESSION_END("session_end", "Session End", "Triggers when a session ends.", false),
        BOOGEYMAN_ADDED("boogeyman_added", "Boogeyman Added", "Triggers when a boogeyman is added.\nAvailable macros: $(Player)", false),
        BOOGEYMAN_CURE_REWARD("boogeyman_cure_reward", "Boogeyman Cure §7Reward", "Triggers when a boogeyman cures.\nAvailable macros: $(Player)", true),
        BOOGEYMAN_FAIL_REWARD("boogeyman_fail_reward", "Boogeyman Fail §7Punishment", "Triggers when a boogeyman fails.\nAvailable macros: $(Player)", true),
        BOOGEYMAN_KILL("boogeyman_kill", "Boogeyman Kill", "Triggers when a boogeyman kills someone.\nAvailable macros: $(Boogeyman), $(Victim)", false),
        SOCIETY_MEMBER_ADDED("society_member_added", "Society Member Added", "Triggers when a secret society member is added.\nAvailable macros: $(Player)", false),
        SOCIETY_SUCCESS_REWARD("society_success_reward", "Secret Society Success §7Reward", "Triggers when a society member succeeds.\nAvailable macros: $(Player)", true),
        SOCIETY_FAIL_REWARD("society_fail_reward", "Secret Society Fail §7Punishment", "Triggers when a society member fails.\nAvailable macros: $(Player)", true),
        TASK_SUCCEED("task_succeed", "Task Succeed", "Triggers when a player succeeds their task in Secret Life.\nAvailable macros: $(Player)", false),
        TASK_FAIL("task_fail", "Task Fail", "Triggers when a player fails their task in Secret Life.\nAvailable macros: $(Player)", false),
        TASK_REROLL("task_reroll", "Task Reroll", "Triggers when a player rerolls their task in Secret Life.\nAvailable macros: $(Player)", false),
        WILDCARD_ACTIVATE("wildcard_activate", "Wildcard Activate", "Triggers when a wildcard activates.\nAvailable macros: $(Index)", false),
        WILDCARD_DEACTIVATE("wildcard_deactivate", "Wildcard Deactivate", "Triggers when a wildcard activates.\nAvailable macros: $(Index)", false),
        TRIVIA_BOT_SPAWN("trivia_bot_spawn", "Trivia Bot Spawn", "Triggers when a trivia bot spawns for a player.\nAvailable macros: $(Player), $(TriviaBot)", false),
        TRIVIA_BOT_OPEN("trivia_bot_open", "Trivia Bot Open", "Triggers when a player opens a trivia bot.\nAvailable macros: $(Player), $(TriviaBot)", false),
        TRIVIA_SUCCEED("trivia_succeed", "Trivia Succeed", "Triggers when a player answers correctly in trivia.\nAvailable macros: $(Player), $(TriviaBot)", false),
        TRIVIA_FAIL("trivia_fail", "Trivia Fail", "Triggers when a player answers incorrectly in trivia.\nAvailable macros: $(Player), $(TriviaBot)", false),
        SUPERPOWER_TRIGGER("superpower_trigger", "Superpower Trigger", "Triggers when a player triggers their superpower.\nAvailable macros: $(Player), $(SuperpowerIndex)", false),
        NICE_LIST_ADD("nice_list_add", "Nice List Add Player", "Triggers when a player is added to the Nice List.\nAvailable macros: $(Player)", false),
        NICE_LIST_END("nice_list_end", "Nice List End", "Triggers when the Nice List ends and the voting results begin.", false),
        NAUGHTY_LIST_ADD("naughty_list_add", "Naughty List Add Player", "Triggers when a player is added to the Naughty List.\nAvailable macros: $(Player)", false),
        NAUGHTY_LIST_END("naughty_list_end", "Naughty List End", "Triggers when the Naughty List ends.", false);

        //TEMP("name", "Name", "Description\nAvailable macros: $(Player)", false);

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

        public void trigger() {
            trigger(List.of());
        }
        public void trigger(MacroEntry entry) {
            trigger(List.of(entry));
        }
        public void trigger(List<MacroEntry> macroEntries) {
            if (!hasCommand()) return;
            if (command == null) return;
            if (command.startsWith("/function ") || command.startsWith("function ")) {
                //? if < 1.20.2 {
                /*String runCommand = command;
                for (MacroEntry entry : macroEntries) {
                    runCommand = runCommand.replace("$("+entry.key()+")", entry.value);
                }
                OtherUtils.executeCommand(runCommand);
                *///?} else {
                List<String> macroStrings = new ArrayList<>();
                for (MacroEntry entry : macroEntries) {
                    boolean isNumber = OtherUtils.isNumber(entry.value());
                    if (isNumber) {
                        macroStrings.add(TextUtils.formatString("{}:{}", entry.key(), entry.value()));
                    }
                    else {
                        macroStrings.add(TextUtils.formatString("{}:\"{}\"", entry.key(), entry.value()));
                    }
                }
                if (macroStrings.isEmpty()) {
                    OtherUtils.executeCommand(command);
                }
                else {
                    OtherUtils.executeCommand(command + " {"+String.join(", ", macroStrings)+"}");
                }
                //?}
            }
            else {
                String runCommand = command;
                for (MacroEntry entry : macroEntries) {
                    runCommand = runCommand.replace("$("+entry.key()+")", entry.value);
                }
                OtherUtils.executeCommand(runCommand);
            }
        }

        public record MacroEntry(String key, String value) {}
    }
}
