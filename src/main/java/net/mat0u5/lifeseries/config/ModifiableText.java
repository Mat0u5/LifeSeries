package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.Formatted;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static net.mat0u5.lifeseries.Main.currentSeason;

public enum ModifiableText {
    MOD_VERSION("Mod version: {}", List.of("version"))
    ,MOD_ERROR_GENERAL("§cSomething went wrong")
    ,MOD_SVC_MISSING("{} does not have Simple Voice Chat installed", List.of("Player"))
    ,MOD_SVC_MISSING_SERVER("The server does not have Simple Voice Chat installed")
    ,MOD_SVC_MISSING_ALL("None of the targets have Simple Voice Chat installed")
    ,MOD_RELOAD("§7Reloading the Life Series...")
    ,MOD_DISABLED_ERROR("The Life Series mod is disabled!\nEnable with \"/lifeseries enable\"")
    ,TRANSCRIPT_COPY("§7Click {}§7 to copy the session transcript.", List.of("ClickHere"))
    ,CONFIG_UPDATED("§7Config has been successfully updated.")
    ,CONFIG_GUI_OPENING("§7Opening the config GUI...")
    ,COUNTDOWN_COLOR_3("§a3")
    ,COUNTDOWN_COLOR_2("§e2")
    ,COUNTDOWN_COLOR_1("§c1")
    ,COUNTDOWN_RED_3("§c3")
    ,COUNTDOWN_RED_2("§c2")
    ,COUNTDOWN_RED_1("§c1")
    ,COUNTDOWN_GREEN_3("§a3")
    ,COUNTDOWN_GREEN_2("§a2")
    ,COUNTDOWN_GREEN_1("§a1")

    ,SEASON_COMMANDS_ADMIN(Formatted.LOOSELY_STYLED, "§7{} commands: §r{}", List.of("season", "commands"))
    ,SEASON_COMMANDS(Formatted.LOOSELY_STYLED, "§7{} non-admin commands: §r{}", List.of("season", "commands"))
    ,SEASON_INVALID("That is not a valid season!")
    ,SEASON_INVALID_HELP(Formatted.PLAIN, "You must choose one of the following: {}", List.of("season names"))
    ,SEASON_SELECT_WARNING("§7WARNING: you have already selected a season, changing it might cause some saved data to be lost (lives, ...)\n§7If you are sure, use '§f/lifeseries setSeries <season> confirm§7'")
    ,SEASON_CHANGE(Formatted.LOOSELY_STYLED,"§aSuccessfully changed the season to {}.", List.of("season"))
    ,SEASON_CHANGING("§7Changing the season to {}§7...", List.of("season"))
    ,SEASON_CHANGED(Formatted.LOOSELY_STYLED,"§aSuccessfully changed the season to {}", List.of("season"))//TODO test
    ,SEASON_GET("Current season: {}", List.of("season"))
    ,SEASON_KILL_GAINLIFE("{}§7 gained a life for killing {}.", List.of("Killer", "Victim"))
    ,SEASON_KILL_UNJUSTIFIED("§c [Unjustified Kill?] {}§7 was killed by {}", List.of("Killer", "Victim"))
    ,SEASON_SELECTION_GUI("§7Opening the season selection GUI...")

    ,SESSION_STARTED(Formatted.LOOSELY_STYLED,"§6Session started! §7[{}]\n§f/session timer showDisplay§7 - toggles a session timer on your screen.", List.of("session length"))
    ,SESSION_ACTION_ENTRY(Formatted.LOOSELY_STYLED,"§7- {}", List.of("action name"))
    ,SESSION_ACTION_ENTRY_LONG(Formatted.LOOSELY_STYLED,"§7- {} §f[{}]", List.of("action name", "trigger time"))
    ,SERIES_DISABLE("The Life Series has been {}", List.of("enabled/disabled"))
    ,SESSION_PAUSE_QUEUE("The session will pause at {} for {}", List.of("time", "duration"))
    ,SESSION_END_INFO("The session ends in {}", List.of("time"))
    ,SESSION_SKIP("Skipped {} in the session length", List.of("time"))
    ,SESSION_LENGTH_SET("The session length has been set to {}", List.of("time"))
    ,SESSION_LENGTH_ADD("Added {} to the session length", List.of("time"))
    ,SESSION_LENGTH_REMOVE("Removed {} from the session length", List.of("time"))
    ,SESSION_ERROR_START("§cThe session has not started")
    ,SESSION_START_PROMPT("\nUse §b'/session timer set <time>'§f to set the desired session time.\nAfter that, use §b'/session start'§f to start the session.")
    ,SESSION_ERROR_TIME_UNSET("The session time is not set! Use '/session timer set <time>' to set the session time.")
    ,SESSION_ERROR_STARTED("The session has already started")
    ,SESSION_ERROR_NOTSTARTED("The session has not yet started")
    ,SESSION_START_FAIL("Could not start session")
    ,SESSION_STARTING("§7Starting session...")
    ,SESSION_STOPPING("§7Stopping session...")
    ,SESSION_PAUSING("§7Pausing session...")
    ,SESSION_UNPAUSING("§7Unpausing session...")
    ,SESSION_ENDED("§6The session has ended!")
    ,SESSION_PAUSED("§6Session paused!")
    ,SESSION_UNPAUSED("§6Session unpaused!")
    ,SESSION_WARNING_5MIN("§6Session ends in 5 minutes!")
    ,SESSION_WARNING_30MIN("§6Session ends in 30 minutes!")
    ,SESSION_START_TITLE("§aThe timer has begun!")
    ,SESSION_PAUSE_QUEUE_RESET("Reset all queued pauses")
    ,SESSION_TIMER_DISPLAY_NOTSTARTED("§7Session has not started")
    ,SESSION_TIMER_DISPLAY_PAUSE("§7Session has been paused")
    ,SESSION_TIMER_DISPLAY_END("§7Session has ended")
    ,SESSION_TIMER_DISPLAY("§7{}", List.of("time"))
    ,SESSION_ACTIONS("§7Queued session actions:")
    ,SESSION_ACTION_SOCIETY("Begin Secret Society")
    ,SESSION_ACTION_BOOGEYMAN("Choose Boogeymen")
    ,SESSION_ACTION_ASSIGN_LIVES("Assign lives if necessary")
    ,SESSION_ACTION_ASSIGN_SOULMATES(Seasons.DOUBLE_LIFE, "Assign Soulmates if necessary")
    ,SESSION_ACTION_RANDOM_TP(Seasons.DOUBLE_LIFE, "Random teleport distribution")
    ,SESSION_ACTION_TASKS(Seasons.SECRET_LIFE, "Assign Tasks")
    ,SESSION_ACTION_WILDCARD(Seasons.WILD_LIFE, "Activate Wildcard")
    ,TICK_FREEZE("§7The game is frozen")
    ,TICK_UNFREEZE("§7The game is no longer frozen.")

    ,LIVES_UNASSIGNED("You have not been assigned any lives yet")
    ,LIVES_UNASSIGNED_ALL("Nobody has been assigned lives yet")
    ,LIVES_UNASSIGNED_OTHER("{} has not been assigned any lives", List.of("Player"))
    ,LIVES_GET_SELF("You have {} {}", List.of("Amount", "life/lives"))
    ,LIVES_GET_SELF_NONE("Womp womp.")
    ,LIVES_ASSIGNED_LIST("Assigned Lives: \n")
    ,LIVES_ASSIGNED_LIST_ENTRY("{} has {} {}\n", List.of("Player", "amount", "life/lives"))
    ,LIVES_RELOADING("§7Reloading lives...")
    ,LIVES_SET_SINGLE("Set {}'s lives to {}", List.of("Player", "amount"))
    ,LIVES_SET_MULTIPLE("Set lives to {} for {} targets", List.of("amount", "number of targets"))
    ,LIVES_CHANGE_SINGLE("{} {} {} {} {}", List.of("Added/Removed", "Amount", "life/lives", "to/from", "Player"))
    ,LIVES_CHANGE_MULTIPLE("{} {} {} {} {} targets", List.of("Added/Removed", "amount", "life/lives", "to/from", "number of targets"))
    ,LIVES_RESET_SINGLE("Reset {}'s lives", List.of("Player"))
    ,LIVES_RESET_MULTIPLE("Reset lives of {} targets", List.of("number of targets"))
    ,LIVES_RESET_EVERYONE("Reset everyone's lives")
    ,LIVES_RANDOMIZE_SINGLE("§7Assigning random lives to {}§7...", List.of("Player"))
    ,LIVES_RANDOMIZE_MULTIPLE("§7Assigning random lives to {}§7 targets...", List.of("number of targets"))
    ,LIVES_RANDOMIZE_RESULT("{}§a {}.", List.of("amount", "life/lives"))
    ,LIVES_RANDOMIZE_TITLE("§7You will have...")
    ,FINAL_DEATH("{} ran out of lives.", List.of("Player"))
    ,FINAL_DEATH_TITLE("{}", List.of("Player"))
    ,FINAL_DEATH_TITLE_SUBTITLE("ran out of lives!")
    ,MUTED_DEADPLAYER("Dead players aren't allowed to talk in chat! Admins can change this behavior in the config.")

    ,PLAYER_ERROR_WATCHER("That player is a Watcher")
    ,PLAYER_ERROR_DEAD("That player is not alive")
    ,TARGET_ERROR_MISSING("No target was found")

    ,GIVELIFE_DOUBLELIFE_ACCEPT("Your soulmate wants to give a life to {}.\nClick {} to accept the request.", List.of("Player", "ClickHere"))
    ,GIVELIFE_RECEIVE_SELF("You received a life from {}", List.of("Player"))
    ,GIVELIFE_RECEIVE_OTHER("{} received a life from {}", List.of("Receiver", "Giver"))
    ,GIVELIFE_RECEIVE_SELF_TITLE("You received a life")
    ,GIVELIFE_RECEIVE_SELF_TITLE_SUBTITLE("from {}", List.of("Player"))
    ,GIVELIFE_DOUBLELIFE_INFO("§7Your soulmate must accept your request to give a life to this player.")

    ,WATCHER_JOIN("§7§nYou are now a Watcher.\n\n§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe - this is very useful for spectators and for admins.")
    ,WATCHER_LEAVE("§7You are no longer a Watcher.")
    ,WATCHER_ERROR_NONE("There are no Watchers right now")
    ,WATCHER_ADD_SINGLE("{} is now a Watcher", List.of("Player"))
    ,WATCHER_ADD_MULTIPLE("{} targets are now Watchers", List.of("number of targets"))
    ,WATCHER_REMOVE_SINGLE("{} is no longer a Watcher", List.of("Player"))
    ,WATCHER_REMOVE_MULTIPLE("{} targets are no longer Watchers", List.of("number of targets"))
    ,WATCHER_LIST(Formatted.LOOSELY_STYLED,"Current Watchers: §7{}", List.of("Watchers"))
    ,MUTED_WATCHER("Watchers aren't allowed to talk in chat! Admins can change this behavior in the config.")
    ,WATCHER_INFO("§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe.\n§7This is very useful for spectators and for admins.")

    ,SOCIETY_INITIATE_REMINDER("§7When you are alone, type \"/initiate\"")
    ,SOCIETY_INITIATED_PT1("§7You have been chosen to be part of the §csecret society§7.")
    ,SOCIETY_INITIATED_GROUP_PT1(Formatted.LOOSELY_STYLED, "§7There {} §c{}§7 other {}. Find them.", List.of("is/are", "member amount", "member/s"))
    ,SOCIETY_INITIATED_GROUP_PT2(Formatted.LOOSELY_STYLED, "§7Together, secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", List.of("kills needed", "Player/s"))
    ,SOCIETY_INITIATED_GROUP_PT3("§7Find the other members with the secret word:")
    ,SOCIETY_INITIATED_GROUP_PT4(Formatted.LOOSELY_STYLED, "§d\"{}\"", List.of("secret word"))
    ,SOCIETY_INITIATED_ALONE_PT1("§7You are alone.")
    ,SOCIETY_INITIATED_ALONE_PT2(Formatted.LOOSELY_STYLED, "§7Secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", List.of("kills needed", "Player/s"))
    ,SOCIETY_INITIATED_PT2("§7Type \"/society success\" when you complete your goal.")
    ,SOCIETY_INITIATED_PT3("§7Don't tell anyone else about the society.")
    ,SOCIETY_INITIATED_PT4("§7If you fail...")
    ,SOCIETY_INITIATED_PUNISHMENT(Formatted.LOOSELY_STYLED, "§7Type \"/society fail\", and you all lose §c{} {}§7.", List.of("lives lost", "life/lives"))
    ,SOCIETY_NOTICE_ADDED("§c [NOTICE] You are now a Secret Society member!")
    ,SOCIETY_NOTICE_REMOVED("§c [NOTICE] You are no longer a Secret Society member!")
    ,SOCIETY_OTHER_MEMBER_ADDED("A player has been added to the Secret Society.")
    ,SOCIETY_OTHER_MEMBER_REMOVED("A player has been removed from the Secret Society.")
    ,SOCIETY_MEMBERS(Formatted.LOOSELY_STYLED,"Secret Society Members: §7{}", List.of("members"))
    ,SOCIETY_MEMBER_ERROR("That player is not a Member")
    ,SOCIETY_MEMBER_ERROR_DUPLICATE("That player already a Member")
    ,SOCIETY_MEMBER_ERROR_NONE("The are no Secret Society members")
    ,SOCIETY_ERROR_ENDED("The Secret Society has already ended")
    ,SOCIETY_ERROR_STARTED("The Secret Society has not started yet")
    ,SOCIETY_START_PROMPT("§7Use '/society begin' or '/society begin <secret_word>' to start.")
    ,SOCIETY_STARTING("§7Starting the Secret Society...")
    ,SOCIETY_ENDING("§7Ending the Secret Society...")
    ,SOCIETY_MEMBER_ERROR_SELF("You are not a member of the Secret Society")
    ,SOCIETY_MEMBER_ERROR_FAILBYPASS("§7Use the §f\"/society fail §lconfirm\"§l§7 command to bypass this")
    ,SOCIETY_MEMBER_ERROR_SUCCESSBYPASS("§7Use the §f\"/society success §lconfirm\"§l§7 command to bypass this")
    ,SOCIETY_MEMBER_ERROR_INITIALIZE("You have not been initiated")
    ,SOCIETY_MEMBER_ERROR_INITIALIZED("You have already been initiated")
    ,SOCIETY_NOT_ENDED("§c The Secret Society has not been ended by any Member!\n§c Run \"/society members list\" to see the Members.")
    ,SOCIETY_CALLS_PT1("§cThe Society calls")
    ,SOCIETY_CALLS_PT2("§cThe Society calls.")
    ,SOCIETY_CALLS_PT3("§cThe Society calls..")
    ,SOCIETY_CALLS_PT4("§cThe Society calls...")
    ,SOCIETY_CALLS_PT5_TITLE("")
    ,SOCIETY_CALLS_PT5_SUBTITLE("§cTake yourself somewhere quiet")
    ,SOCIETY_END_SUCCESS_PT1_TITLE("")
    ,SOCIETY_END_SUCCESS_PT2_TITLE("")
    ,SOCIETY_END_SUCCESS_PT3_TITLE("")
    ,SOCIETY_END_SUCCESS_PT1_SUBTITLE("§aThe Society is pleased")
    ,SOCIETY_END_SUCCESS_PT2_SUBTITLE("§aYou will not be punished")
    ,SOCIETY_END_SUCCESS_PT3_SUBTITLE("§cYou are still sworn to secrecy")
    ,SOCIETY_END_FAIL_PT1_TITLE("")
    ,SOCIETY_END_FAIL_PT2_TITLE("")
    ,SOCIETY_END_FAIL_PT3_TITLE("")
    ,SOCIETY_END_FAIL_PT1_SUBTITLE("§cThe Society is displeased")
    ,SOCIETY_END_FAIL_PT2_SUBTITLE("§cYou will be punished")
    ,SOCIETY_END_FAIL_PT3_SUBTITLE("§cYou are still sworn to secrecy")

    ,SUBIN_END_NOTIFY(Formatted.LOOSELY_STYLED, "§6You are no longer subbing in for {}", List.of("Player"))
    ,SUBIN_END_OTHER(Formatted.LOOSELY_STYLED, "§6{} is no longer subbing in for you", List.of("Player"))
    ,SUBIN_ERROR_ALREADY_SUBBING(Formatted.PLAIN, "{} is already subbing in for {}", List.of("Player", "Subin"))
    ,SUBIN_ERROR_ALREADY_SUBBED(Formatted.PLAIN, "{} is already being subbed in for by {}", List.of("Player", "Subbed"))
    ,SUBIN_ERROR_MISSING(Formatted.PLAIN, "{} is not subbing in for anyone", List.of("Player"))
    ,SUBIN_LIST_ENTRY(Formatted.LOOSELY_STYLED," §7{} is subbinng in for {}", List.of("Player", "Subin"))
    ,SUBIN_START("{} is now subbing in for {}", List.of("Player", "Subin"))
    ,SUBIN_STOP("{} is no longer subbing in for {}", List.of("Player", "Subin"))
    ,SUBIN_ERROR_FETCH("Failed to fetch target profile\nMake sure the target player has logged on the server at least once")
    ,SUBIN_ERROR_ONLINE("Online players cannot be subbed in for")
    ,SUBIN_ERROR_NONE("There are no sub ins yet")
    ,SUBIN_CURRENT("§7Current sub ins:")

    ,CLAIMKILL_ERROR_NODEATH(Formatted.PLAIN, "{} did not die in the last 2 minutes. Or they might have been killed by a player directly.", List.of("Player"))
    ,CLAIMKILL_ERROR_SELF("You cannot claim credit for your own death :P")
    ,CLAIMKILL("{}§7 claims credit for {}§7's death.", List.of("Killer", "Victim"))
    ,CLAIMKILL_VALIDATE("§7Click {}§7 to accept the claim if you think it's valid.", List.of("ClickHere"))
    ,CLAIMKILL_ACCEPT("{}§7's kill claim on {}§7 was accepted.", List.of("Killer", "Victim"))

    ,GIVELIFE_ERROR_NONE("You do not have any lives to give")
    ,GIVELIFE_ERROR_SELF("You cannot give lives to yourself")
    ,GIVELIFE_ERROR_NOT_ENOUGH("You cannot give away any more lives")
    ,GIVELIFE_ERROR_TOO_MANY("That player cannot receive any more lives")
    ,GIVELIFE_ERROR_SOULMATE("You cannot give lives to your soulmate")

    ,LIFESKINS_SKIN_SET("Set {}'s skin to {}", List.of("Player", "username"))
    ,LIFESKINS_USERNAME_SET("Set {}'s username to {}", List.of("Player", "username"))
    ,LIFESKINS_NICKNAME_SET("Set {}'s nickname to {}", List.of("Player", "nickname"))
    ,LIFESKINS_NICKNAME_RESET("Reset {}'s nickname", List.of("Player"))

    ,SIDETITLE_SINGLE("Showing new side title for {}", List.of("Player"))
    ,SIDETITLE_MULTIPLE("Showing new side title for {} players", List.of("number of targets"))

    ,BOOGEYMAN_MESSAGE("§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman.")
    ,BOOGEYMAN_NOTICE_ADDED("§c [NOTICE] You are now a Boogeyman!")
    ,BOOGEYMAN_NOTICE_REMOVED("§c [NOTICE] You are no longer a Boogeyman!")
    ,BOOGEYMAN_NOTICE_RESET("§c [NOTICE] Your Boogeyman  fail/cure status has been reset")
    ,BOOGEYMAN_KILLS_REQUIRED("§7You need {} {}§7 to be cured of the curse.", List.of("kills amount", "kill/kills"))
    ,BOOGEYMAN_LATEJOIN("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!")
    ,BOOGEYMAN_LIST("Current Boogeymen: {}", List.of("list"))
    ,BOOGEYMAN_FAIL_NOTICE("§cYou only have 5 minutes left to kill someone as the Boogeyman before you fail!")
    ,BOOGEYMAN_FAIL_SELF("{}§7 voulentarily failed themselves as the Boogeyman. They have been consumed by the curse.", List.of("Player"))
    ,BOOGEYMAN_FAIL_OTHER_SINGLE("§7Failing Boogeyman for {}§7...", List.of("Player"))
    ,BOOGEYMAN_FAIL_OTHER_MULTIPLE("§7Failing Boogeyman for {} targets§7...", List.of("number of targets"))
    ,BOOGEYMAN_RESET_SINGLE("§7Resetting Boogeyman cure/failure for {}§7...", List.of("Player"))
    ,BOOGEYMAN_RESET_MULTIPLE("§7Resetting Boogeyman cure/failure for {} targets§7...", List.of("number of targets"))
    ,BOOGEYMAN_CURE_SINGLE("§7Curing {}§7...", List.of("Player"))
    ,BOOGEYMAN_CURE_MULTIPLE("§7Curing {} targets§7...", List.of("number of targets"))
    ,BOOGEYMAN_ADD_SINGLE("{} is now a Boogeyman", List.of("Player"))
    ,BOOGEYMAN_ADD_MULTIPLE("{} targets are now Boogeymen", List.of("number of targets"))
    ,BOOGEYMAN_REMOVE_SINGLE("{} is no longer a Boogeyman", List.of("Player"))
    ,BOOGEYMAN_REMOVE_MULTIPLE("{} targets are no longer Boogeymen", List.of("number of targets"))
    ,BOOGEYMAN_LIST_REMAINING("Remaining Boogeymen: {}", List.of("list"))
    ,BOOGEYMAN_LIST_CURED("Cured Boogeymen: {}", List.of("list"))
    ,BOOGEYMAN_LIST_FAILED("Failed Boogeymen: {}", List.of("list"))
    ,BOOGEYMAN_CURE("{}§7 is cured of the Boogeyman curse!", List.of("Player"))
    ,BOOGEYMAN_CURE_GAINLIFE("{}§7 is cured of the Boogeyman curse and gained a life for succeeding!", List.of("Player"))
    ,BOOGEYMAN_FAIL("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.", List.of("Player"))
    ,BOOGEYMAN_FAIL_ADVANCEDDEATH("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been consumed by the curse.", List.of("Player"))
    ,BOOGEYMAN_FAIL_NOTIFY_TITLE("§cYou have failed.")
    ,BOOGEYMAN_FAIL_ADVANCEDDEATH_NOTIFY_TITLE("§cThe curse consumes you..")
    ,BOOGEYMAN_ERROR_NOTBOOGEY("You are not a Boogeyman")
    ,BOOGEYMAN_ERROR_NOTBOOGEY_OTHER("That player is not a Boogeyman")
    ,BOOGEYMAN_ERROR_IS("That player is already a Boogeyman")
    ,BOOGEYMAN_ERROR_ALREADY_FAILED("You have already failed")
    ,BOOGEYMAN_ERROR_ALREADY_CURED("You have already been cured")
    ,BOOGEYMAN_ALREADY_FAILED("§7You were the Boogeyman, but you have already §cfailed§7.")
    ,BOOGEYMAN_ALREADY_CURED("§7You were the Boogeyman, and you have already been §acured§7.")
    ,BOOGEYMAN_IS("§cYou are the Boogeyman.")
    ,BOOGEYMAN_SELFFAIL_WARNING("Warning: This will cause you to fail as the Boogeyman\nRun \"/boogeyman selfFail §lconfirm§r\" to confirm this action.")
    ,BOOGEYMAN_SELFFAIL("§7Failing as the Boogeyman...")
    ,BOOGEYMAN_CLEAR("All Boogeymen have been cleared")
    ,BOOGEYMAN_RANDOMIZE("§7Choosing random Boogeymen...")
    ,BOOGEYMAN_NOTICE_5MIN("§cThe Boogeyman is being chosen in 5 minutes.")
    ,BOOGEYMAN_NOTICE_1MIN("§cThe Boogeyman is being chosen in 1 minute.")
    ,BOOGEYMAN_CHOSEN("§cA new boogeyman is about to be chosen.")
    ,BOOGEYMAN_CHOSEN_NEW("§cThe Boogeyman is about to be chosen.")
    ,BOOGEYMAN_ROLL("§eYou are...")
    ,BOOGEYMAN_ROLL_BOOGEY("§cThe Boogeyman.")
    ,BOOGEYMAN_ROLL_NORMAL("§aNOT the Boogeyman.")
    ,BOOGEYMAN_CURE_TITLE("§aYou are cured!")
    ,BOOGEYMAN_FAIL_ADVANCEDDEATH_FINISH_PT1("§cYour lives are taken...")
    ,BOOGEYMAN_FAIL_ADVANCEDDEATH_FINISH_PT2("§c...Now take theirs.")


    ,DOUBLELIFE_TELEPORT(Seasons.DOUBLE_LIFE, "§6Woosh!")
    ,DOUBLELIFE_TELEPORT_SUCCESS(Seasons.DOUBLE_LIFE, "Randomly distributed players.")
    ,DOUBLELIFE_SOULMATE_TITLE_UNKNOWN(Seasons.DOUBLE_LIFE, "§a????")
    ,DOUBLELIFE_SOULMATE_TITLE_PLAYER(Seasons.DOUBLE_LIFE, "{}", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_PREVENT(Seasons.DOUBLE_LIFE, "{}'s soulmate now cannot be {} when the next randomization happens.", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_FORCE(Seasons.DOUBLE_LIFE, "{}'s soulmate will be {} when the next randomization happens.", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_SET(Seasons.DOUBLE_LIFE, "{}'s soulmate is now {}", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_GET(Seasons.DOUBLE_LIFE, "{}'s soulmate is {}", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_RESET_SINGLE(Seasons.DOUBLE_LIFE, "{}'s soulmate was reset", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_RESET_MULTIPLE(Seasons.DOUBLE_LIFE, "Soulmate was reset for {} targets", List.of("number of targets"))
    ,DOUBLELIFE_SOULMATE_ERROR_EXISTS(Seasons.DOUBLE_LIFE, Formatted.PLAIN, "{} already has a soulmate", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_ERROR_FORCE_EXISTS(Seasons.DOUBLE_LIFE, Formatted.PLAIN, "{} is already forced with someone", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_ERROR_MISSING(Seasons.DOUBLE_LIFE, Formatted.PLAIN, "{} does not have a soulmate", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_ERROR_OFFLINE(Seasons.DOUBLE_LIFE, Formatted.PLAIN, "{} 's soulmate is not online right now", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_PREVENT_RESET(Seasons.DOUBLE_LIFE, "Soulmate prevent entries were reset")
    ,DOUBLELIFE_SOULMATE_FORCE_RESET(Seasons.DOUBLE_LIFE, "Soulmate force entries were reset")
    ,DOUBLELIFE_SOULMATE_ERROR_DUPLICATE(Seasons.DOUBLE_LIFE, "You cannot specify the same player twice")
    ,DOUBLELIFE_SOULMATE_RESET(Seasons.DOUBLE_LIFE, "All soulmate entries were reset")
    ,DOUBLELIFE_SOULMATE_NONE(Seasons.DOUBLE_LIFE, "There are no soulmates currently assigned")
    ,DOUBLELIFE_SOULMATE_ROLLING(Seasons.DOUBLE_LIFE, "§7Rolling soulmates...")
    ,DOUBLELIFE_UNPAIRED(Seasons.DOUBLE_LIFE, "[Double Life] {} was not paired with anyone.", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_TITLE(Seasons.DOUBLE_LIFE, "§aYour soulmate is...")
    ,DOUBLELIFE_LASTPAIR_PT1_TITLE(Seasons.DOUBLE_LIFE, "")
    ,DOUBLELIFE_LASTPAIR_PT1_SUBTITLE(Seasons.DOUBLE_LIFE, "§aYour fate is your own...")
    ,DOUBLELIFE_LASTPAIR_PT2_TITLE(Seasons.DOUBLE_LIFE, "")
    ,DOUBLELIFE_LASTPAIR_PT2_SUBTITLE(Seasons.DOUBLE_LIFE, "§cThere can only be one winner.")

    ,LIMITEDLIFE_CHANGE_COLOR(Seasons.LIMITED_LIFE, "{}§7 is now a {} name§7.", List.of("Player", "color"))
    ,LIMITEDLIFE_SESSION_DISPLAY_DIVIDER(Seasons.LIMITED_LIFE, "  |  ")

    ,SECRETLIFE_TASK_MISSING(Seasons.SECRET_LIFE, "§cYou do not have a secret task book in your inventory.")
    ,SECRETLIFE_TASK_MISSING_OTHER(Seasons.SECRET_LIFE, "{} does not have a task book in their inventory nor a pre-assigned task", List.of("Player"))
    ,SECRETLIFE_TASK_PRESENT(Seasons.SECRET_LIFE, "{} has a task book in their inventory", List.of("Player"))
    ,SECRETLIFE_TASK_PREASSIGNED(Seasons.SECRET_LIFE, "{} has a pre-assigned task", List.of("Player"))
    ,SECRETLIFE_TASK_READFAIL(Seasons.SECRET_LIFE, "Failed to read task contents")
    ,SECRETLIFE_TASK_SHOW(Seasons.SECRET_LIFE, "§7Click {}§7 to show the task they have.", List.of("ClickHere"))
    ,SECRETLIFE_TASK_SHOW_PAST(Seasons.SECRET_LIFE, "§7Click {}§7 to see what {}§7's task was.", List.of("ClickHere", "Player"))
    ,SECRETLIFE_SECRETKEEPER_INUSE(Seasons.SECRET_LIFE, "§cSomeone else is using the Secret Keeper right now.")
    ,SECRETLIFE_TASK_PENDING(Seasons.SECRET_LIFE, "{} wants to succeed their task.", List.of("Player"))
    ,SECRETLIFE_TASK_PENDING_ACCEPT(Seasons.SECRET_LIFE, "§7Click {}§7 to confirm this action.", List.of("ClickHere"))
    ,SECRETLIFE_TASK_PENDING_NOTIFICATION(Seasons.SECRET_LIFE, "§cYour task confirmation needs to be approved by an admin.")
    ,SECRETLIFE_TASK_SUCCEED(Seasons.SECRET_LIFE, "{}§a succeeded their task.", List.of("Player"))
    ,SECRETLIFE_TASK_REROLL_HARD_FAIL(Seasons.SECRET_LIFE, "§cYou cannot re-roll a Hard task.")
    ,SECRETLIFE_TASK_REROLL_HARD_FAIL_RED(Seasons.SECRET_LIFE, "§cYou cannot re-roll a Hard task. If you want your red task instead, click the Fail button.")
    ,SECRETLIFE_TASK_NOT_SUBMITTED(Seasons.SECRET_LIFE, Formatted.LOOSELY_STYLED,"§4{}§c still {} not submitted / failed a task this session.", List.of("Players", "has/have"))
    ,SECRETLIFE_HEART_GAIN(Seasons.SECRET_LIFE, Formatted.LOOSELY_STYLED,"§c+{} {}", List.of("amount", "heart/hearts"))
    ,SECRETLIFE_TASK_NAME(Seasons.SECRET_LIFE, "{}'s Secret Task", List.of("Player"))
    ,SECRETLIFE_TASK_AUTHOR(Seasons.SECRET_LIFE, "Secret Keeper")
    ,SECRETLIFE_TASK_SET(Seasons.SECRET_LIFE, "Changed {}'s task", List.of("Player"))
    ,SECRETLIFE_TASK_PREASSIGN(Seasons.SECRET_LIFE, "Pre-assigned {}'s task for randomization\n§7They will be given the task book once you / the game rolls the tasks", List.of("Player"))
    ,SECRETLIFE_TASK_SET_MULTIPLE(Seasons.SECRET_LIFE, "Changed or pre-assigned task of {} targets", List.of("number of targets"))
    ,SECRETLIFE_TASK_SET_RANDOM_SINGLE(Seasons.SECRET_LIFE, "Assigning random task to {}", List.of("Player"))
    ,SECRETLIFE_TASK_SET_RANDOM_MULTIPLE(Seasons.SECRET_LIFE, "Assigning random tasks to {} targets", List.of("number of targets"))
    ,SECRETLIFE_TASK_REMOVE_SINGLE(Seasons.SECRET_LIFE, "Removed task book from {}", List.of("Player"))
    ,SECRETLIFE_TASK_REMOVE_MULTIPLE(Seasons.SECRET_LIFE, "Removed task book from {} targets", List.of("number of targets"))
    ,SECRETLIFE_TASK_SUCCESS_SINGLE(Seasons.SECRET_LIFE, "§7Succeeding task for {}§7...", List.of("Player"))
    ,SECRETLIFE_TASK_SUCCESS_MULTIPLE(Seasons.SECRET_LIFE, "§7Succeeding task for {}§7 targets...", List.of("number of targets"))
    ,SECRETLIFE_TASK_FAIL_SINGLE(Seasons.SECRET_LIFE, "§7Failing task for {}§7...", List.of("Player"))
    ,SECRETLIFE_TASK_FAIL_MULTIPLE(Seasons.SECRET_LIFE, "§7Failing task for {}§7 targets...", List.of("number of targets"))
    ,SECRETLIFE_TASK_REROLL_SINGLE(Seasons.SECRET_LIFE, "§7Rerolling task for {}§7...", List.of("Player"))
    ,SECRETLIFE_TASK_REROLL_MULTIPLE(Seasons.SECRET_LIFE, "§7Rerolling task for {}§7 targets...", List.of("number of targets"))
    ,SECRETLIFE_HEALTH_GET_SELF_DEAD(Seasons.SECRET_LIFE, "You're dead...")
    ,SECRETLIFE_HEALTH_GET_SELF(Seasons.SECRET_LIFE, "You have {} health", List.of("amount"))
    ,SECRETLIFE_HEALTH_GET_LIST(Seasons.SECRET_LIFE, "Health of targets:")
    ,SECRETLIFE_HEALTH_GET_OTHER_DEAD(Seasons.SECRET_LIFE, "{} is dead", List.of("Player"))
    ,SECRETLIFE_HEALTH_GET_OTHER(Seasons.SECRET_LIFE, "{} has {} health", List.of("Player", "amount"))
    ,SECRETLIFE_HEALTH_SET_SINGLE(Seasons.SECRET_LIFE, "Set {}'s health to {}", List.of("Player", "amount"))
    ,SECRETLIFE_HEALTH_SET_MULTIPLE(Seasons.SECRET_LIFE, "Set the health of {} targets to {}", List.of("Player", "amount"))
    ,SECRETLIFE_HEALTH_MODIFY_SINGLE(Seasons.SECRET_LIFE, "{} {} health {} {}", List.of("Added/Removed", "amount", "to/from", "Player"))
    ,SECRETLIFE_HEALTH_MODIFY_MULTIPLE(Seasons.SECRET_LIFE, "{} {} health {} {} targets", List.of("Added/Removed", "amount", "to/from", "number of targets"))
    ,SECRETLIFE_HEALTH_RESET_SINGLE(Seasons.SECRET_LIFE, "Reset {}'s health to the default", List.of("Player"))
    ,SECRETLIFE_HEALTH_RESET_MULTIPLE(Seasons.SECRET_LIFE, "Reset the health to default for {} targets", List.of("number of targets"))
    ,SECRETLIFE_TASK_REROLL(Seasons.SECRET_LIFE,"{}§7 re-rolled their easy task.", List.of("Player"))
    ,SECRETLIFE_TASK_FAIL(Seasons.SECRET_LIFE,"{}§c failed their task.", List.of("Player"))
    ,SECRETLIFE_GIVEHEART_RESET_SINGLE(Seasons.SECRET_LIFE, "Reset {}'s gifted hearts", List.of("Player"))
    ,SECRETLIFE_GIVEHEART_RESET_MULTIPLE(Seasons.SECRET_LIFE, "Reset the gifted hearts of {} targets", List.of("number of targets"))
    ,SECRETLIFE_GIVEHEART_ERROR_SELF(Seasons.SECRET_LIFE, "Nice Try.")
    ,SECRETLIFE_GIVEHEART_ERROR_MULTIPLE(Seasons.SECRET_LIFE, "You have already gifted a heart this session")
    ,SECRETLIFE_GIVEHEART_ERROR_DEAD(Seasons.SECRET_LIFE, "That player is not alive")
    ,SECRETLIFE_GIVEHEART_SEND(Seasons.SECRET_LIFE, "You have gifted a heart to {}", List.of("Player"))
    ,SECRETLIFE_GIVEHEART_RECEIVE(Seasons.SECRET_LIFE, "{} gave you a heart", List.of("Player"))
    ,SECRETLIFE_TASK_LOCATIONS(Seasons.SECRET_LIFE, "Changing Secret Life locations...")
    ,SECRETLIFE_TASK_ERROR_BOOK_MISSING(Seasons.SECRET_LIFE, "No task books were found")
    ,SECRETLIFE_TASK_WARNING_5MIN(Seasons.SECRET_LIFE, "§7Go submit / fail your secret tasks if you haven't!")
    ,SECRETLIFE_TASK_WARNING_30MIN(Seasons.SECRET_LIFE, "§7You better start finishing your secret tasks if you haven't already!")
    ,SECRETLIFE_TASK_TITLE(Seasons.SECRET_LIFE, "§cYour secret is...")
    ,SECRETLIFE_TASK_REROLL_PT1(Seasons.SECRET_LIFE, "§2§lThe reward is more")
    ,SECRETLIFE_TASK_REROLL_PT2(Seasons.SECRET_LIFE, "§a§lThe risk is great")
    ,SECRETLIFE_TASK_REROLL_PT3(Seasons.SECRET_LIFE, "§e§lLet me open the door")
    ,SECRETLIFE_TASK_REROLL_PT4(Seasons.SECRET_LIFE, "§c§lAccept your fate")

    ,WILDLIFE_SNAIL_TEXTURE_INFO(Seasons.WILD_LIFE, Formatted.LOOSELY_STYLED,"§fClick {}§f to open the Snail Textures info page in the Wiki.", List.of("ClickHere"))
    ,WILDLIFE_SNAIL_DEFAULT_NAME(Seasons.WILD_LIFE, Formatted.PLAIN,"{}'s Snail", List.of("Player"))
    ,WILDLIFE_SNAIL_NAME_REQUEST(Seasons.WILD_LIFE, "{}§7 requests their snail name to be §f{}§7", List.of("Player", "name"))
    ,WILDLIFE_SNAIL_NAME_REQUEST_PROMPT(Seasons.WILD_LIFE, "§7Click {}§7 to accept.", List.of("ClickHere"))
    ,WILDLIFE_SNAIL_TEXTURES_LIST(Seasons.WILD_LIFE, "§7The following skins have been found: §f{}", List.of("list"))
    ,WILDLIFE_SNAIL_NAME_SET(Seasons.WILD_LIFE, "Set {}'s snail name to {}", List.of("Player", "name"))
    ,WILDLIFE_SNAIL_NAME_RESET_SINGLE(Seasons.WILD_LIFE, "Reset {}'s snail name to {}", List.of("Player", "name"))
    ,WILDLIFE_SNAIL_NAME_RESET_MULTIPLE(Seasons.WILD_LIFE, "Reset the snail name for {} targets", List.of("number of targets"))
    ,WILDLIFE_SNAIL_NAME_GET(Seasons.WILD_LIFE, "{}'s snail is called {}", List.of("Player", "name"))
    ,WILDLIFE_SUPERPOWER_ASSIGN_RESET_SINGLE(Seasons.WILD_LIFE, "Reset {}'s superpower assignment", List.of("Player"))
    ,WILDLIFE_SUPERPOWER_ASSIGN_RESET_MULTIPLE(Seasons.WILD_LIFE, "Reset the superpower assignment of {} targets", List.of("number of targets"))
    ,WILDLIFE_SUPERPOWER_ASSIGN_SINGLE(Seasons.WILD_LIFE, "Forced {}'s superpower to be {} when the next superpower randomization happens", List.of("Player", "power"))
    ,WILDLIFE_SUPERPOWER_ASSIGN_MULTIPLE(Seasons.WILD_LIFE, "Forced the superpower of {} targets to be {} when the next superpower randomization happens", List.of("number of targets", "power"))
    ,WILDLIFE_SUPERPOWER_RANDOMIZE_SINGLE(Seasons.WILD_LIFE, "Randomized {}'s superpower", List.of("Player"))
    ,WILDLIFE_SUPERPOWER_RANDOMIZE_MULTIPLE(Seasons.WILD_LIFE, "Randomized the superpower of {} targets", List.of("number of targets"))
    ,WILDLIFE_SUPERPOWER_DEACTIVATE_SINGLE(Seasons.WILD_LIFE, "Deactivated {}'s superpower", List.of("Player"))
    ,WILDLIFE_SUPERPOWER_DEACTIVATE_MULTIPLE(Seasons.WILD_LIFE, "Deactivated the superpower of {} targets", List.of("number of targets"))
    ,WILDLIFE_SUPERPOWER_GET(Seasons.WILD_LIFE, "{}'s superpower is: {}", List.of("Player", "power"))
    ,WILDLIFE_SUPERPOWER_SET_SINGLE(Seasons.WILD_LIFE, "Set {}'s superpower to {}", List.of("Player", "power"))
    ,WILDLIFE_SUPERPOWER_SET_MULTIPLE(Seasons.WILD_LIFE, "Set the superpower to {} for {} targets", List.of("power", "number of targets"))
    ,WILDLIFE_POWER_MIMIC(Seasons.WILD_LIFE, "Mimicked superpower of {}", List.of("Player"))
    ,WILDLIFE_POWER_PLAYERDISGUISE(Seasons.WILD_LIFE, "Copied DNA of {} — Press again to disguise", List.of("Player"))
    ,WILDLIFE_WILDCARD_DEACTIVATE(Seasons.WILD_LIFE, "Deactivated {}", List.of("wildcard"))
    ,WILDLIFE_WILDCARD_ACTIVATE(Seasons.WILD_LIFE, "Activated {}", List.of("wildcard"))
    ,WILDLIFE_WILDCARD_AVAILABLE(Seasons.WILD_LIFE, "Available Wildcards: {}", List.of("list"))
    ,WILDLIFE_WILDCARD_ACTIVATED(Seasons.WILD_LIFE, "Activated Wildcards: {}", List.of("list"))
    ,WILDLIFE_WILDCARD_CHOOSE(Seasons.WILD_LIFE, "The {} wildcard has been selected for this session.\n§7Use the §f'/wildcard choose' §7 command if you want to change it.", List.of("wildcard"))
    ,WILDLIFE_TRIVIA_RECEIVE_EFFECT(Seasons.WILD_LIFE, Formatted.LOOSELY_STYLED, " §a§l+ §7{}§6 {}", List.of("effect name", "amplifier"))
    ,WILDLIFE_TRIVIA_PUNISHMENT_SET_SINGLE(Seasons.WILD_LIFE, "Punished {} with {}", List.of("Player", "punishment"))
    ,WILDLIFE_TRIVIA_PUNISHMENT_SET_MULTIPLE(Seasons.WILD_LIFE, "Punished {} targets with {}", List.of("number of targets", "punishment"))
    ,WILDLIFE_TRIVIA_PUNISHMENT_CLEAR_SINGLE(Seasons.WILD_LIFE, "Cleared {}'s trivia punishments", List.of("Player"))
    ,WILDLIFE_TRIVIA_PUNISHMENT_CLEAR_MULTIPLE(Seasons.WILD_LIFE, "Cleared trivia punishments for {} targets", List.of("number of targets"))
    ,WILDLIFE_TRIVIA_BOT_SPAWN_SINGLE(Seasons.WILD_LIFE, "Spawned a trivia bot for {}", List.of("Player"))
    ,WILDLIFE_TRIVIA_BOT_SPAWN_MULTIPLE(Seasons.WILD_LIFE, "Spawned a trivia bot for {} targets", List.of("number of targets"))
    ,WILDLIFE_TRIVIA_SET_SINGLE(Seasons.WILD_LIFE, "Assigned {}'s trivia", List.of("Player"))
    ,WILDLIFE_TRIVIA_SET_MULTIPLE(Seasons.WILD_LIFE, "Assigned trivia of {} targets", List.of("number of targets"))
    ,WILDLIFE_TRIVIA_RESET_SINGLE(Seasons.WILD_LIFE, "Reset {}'s assigned trivia", List.of("Player"))
    ,WILDLIFE_TRIVIA_RESET_MULTIPLE(Seasons.WILD_LIFE, "Reset assigned trivia of {} targets", List.of("number of targets"))
    ,MUTED_TRIVIABOT(Seasons.WILD_LIFE, "<Trivia Bot> No phoning a friend allowed!")
    ,WILDLIFE_FINALE(Seasons.WILD_LIFE, "All wildcards will act as if the finale (so the Callback wildcard) was activated.")
    ,WILDLIFE_HUNGER_INACTIVE(Seasons.WILD_LIFE, "The Hunger wildcard is not active right now.")
    ,WILDLIFE_HUNGER_RANDOMIZE_TITLE(Seasons.WILD_LIFE, "")
    ,WILDLIFE_HUNGER_RANDOMIZE_SUBTITLE(Seasons.WILD_LIFE, "§7Food is about to be randomised...")
    ,WILDLIFE_HUNGER_RANDOMIZE_MANUAL(Seasons.WILD_LIFE, "§7Randomizing food...")
    ,WILDLIFE_SNAIL_TEXTURES_RELOAD(Seasons.WILD_LIFE, "§7Reloading snail textures...")
    ,WILDLIFE_SNAIL_TEXTURES_NONE(Seasons.WILD_LIFE, "§7No snail skins have been added yet. Run '§f/snail textures info§7' to learn how to add them.")
    ,WILDLIFE_WILDCARD_GUI_ERROR(Seasons.WILD_LIFE, "You must have the Life Series mod installed §nclient-side§c to open the wildcard GUI")
    ,WILDLIFE_WILDCARD_GUI_OPEN(Seasons.WILD_LIFE, "§7Opening the Wildcard selection GUI...")
    ,WILDLIFE_SUPERPOWER_INVALID(Seasons.WILD_LIFE, "That superpower doesn't exist")
    ,WILDLIFE_SUPERPOWER_INACTIVE(Seasons.WILD_LIFE, "You do not have an active superpower")
    ,WILDLIFE_SUPERPOWER_COOLDOWN(Seasons.WILD_LIFE, "Your superpower cooldown has been skipped")
    ,WILDLIFE_SUPERPOWER_RANDOMIZE(Seasons.WILD_LIFE, "Randomized everyone's superpowers")
    ,WILDLIFE_WILDCARD_DEACTIVATE_ALL(Seasons.WILD_LIFE, "Deactivated all wildcards")
    ,WILDLIFE_WILDCARD_INVALID(Seasons.WILD_LIFE, "That Wildcard doesn't exist")
    ,WILDLIFE_WILDCARD_ACTIVATE_ALL_TITLE(Seasons.WILD_LIFE, "All wildcards are active!")
    ,WILDLIFE_WILDCARD_ACTIVATE_ALL(Seasons.WILD_LIFE, "Activated all wildcards (Except Callback)")
    ,WILDLIFE_WILDCARD_ACTIVATE_ERROR(Seasons.WILD_LIFE, "That Wildcard is already active")
    ,WILDLIFE_WILDCARD_IMPLEMENT_ERROR(Seasons.WILD_LIFE, "That Wildcard has not been implemented yet")
    ,WILDLIFE_WILDCARD_ACTIVATED_NONE(Seasons.WILD_LIFE, "§7There are no active Wildcards right now. \nYou will be able to select a Wildcard when you start a session, or you can use '§f/wildcard activate <wildcard>§7' to activate a specific Wildcard right now.")
    ,WILDLIFE_TRIVIA_QUESTION_INVALID(Seasons.WILD_LIFE, "Could not find trivia with that question.")
    ,WILDLIFE_SNAIL_INFO(Seasons.WILD_LIFE, "§7Use the §f'/snail ...'§7 command to modify snail names and to get info on how to change snail textures.")
    ,WILDLIFE_TRIVIA_NOTICE_START(Seasons.DOUBLE_LIFE, "§7You must start a session for trivia bots to spawn!")
    ,WILDLIFE_TRIVIA_NOTICE(Seasons.DOUBLE_LIFE, "§7You can modify the trivia questions in the config files (./config/lifeseries/wildlife/*-trivia)")
    ,WILDLIFE_WILDCARD_WARNING_2MIN(Seasons.WILD_LIFE, "§7A Wildcard will be activated in 2 minutes!")
    ,WILDLIFE_WILDCARD_FADED(Seasons.WILD_LIFE, "§7A Wildcard has faded...")
    ,WILDLIFE_WILDCARD_DOTS_1(Seasons.WILD_LIFE, "§a§l,")
    ,WILDLIFE_WILDCARD_DOTS_2(Seasons.WILD_LIFE, "§a§l, §e§l,")
    ,WILDLIFE_WILDCARD_DOTS_3(Seasons.WILD_LIFE, "§a§l, §e§l, §c§l,")
    ,WILDLIFE_MAKEITWILD_PT1(Seasons.WILD_LIFE, "§7The ending is §cyours§7...")
    ,WILDLIFE_MAKEITWILD_PT2(Seasons.WILD_LIFE, "§cMake")
    ,WILDLIFE_MAKEITWILD_PT3(Seasons.WILD_LIFE, "§cMake §eit")
    ,WILDLIFE_MAKEITWILD_PT4(Seasons.WILD_LIFE, "§cMake §eit §a§lWILD")
    ,WILDLIFE_SUPERPOWES_DEAD(Seasons.WILD_LIFE, "Dead players can't use superpowers!")
    ,WILDLIFE_POWER_MIMIC_ERROR(Seasons.WILD_LIFE, "You cannot mimic that power.")
    ,WILDLIFE_POWER_MIMIC_NOPLAYER(Seasons.WILD_LIFE, "You are not looking at a player.")
    ,WILDLIFE_POWER_MIMIC_NOPOWER(Seasons.WILD_LIFE, "That player does not have a superpower.")
    ,WILDLIFE_POWER_NECROMANCY_ERROR(Seasons.WILD_LIFE, "There are no dead players.")
    ,WILDLIFE_POWER_PLAYERDISGUISE_ERROR(Seasons.WILD_LIFE, "You are not looking at a player.")
    ,WILDLIFE_POWER_TELEPORTATION_ERROR(Seasons.WILD_LIFE, "There is nothing to teleport to.")
    ,WILDLIFE_SNAIL_TRIVIA_SNAIL_NAME(Seasons.WILD_LIFE, "VHSnail")

    ,PASTLIFE_SESSION_START(Seasons.PAST_LIFE, "§7Past Life session started:\n§7 Type §f\"/pastlife boogeyman\"§7 to have the Boogeyman in this session.\n§7 Type §f\"/pastlife society\"§7 to have the Secret Society in this session.\n§7 Or type §f\"/pastlife pickRandom\"§7 if you want the game to pick randomly.\n")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT1(Seasons.PAST_LIFE, "§7You are the boogeyman.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT2(Seasons.PAST_LIFE, "§7You must by any means necessary kill a §agreen§7 or §eyellow§7 name\n§7by direct action to be cured of the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT3(Seasons.PAST_LIFE, "§7If you fail, you will become a §cred name§7.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT4(Seasons.PAST_LIFE, "§7Other players may defend themselves.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT5(Seasons.PAST_LIFE, "§7Voluntary sacrifices will not cure the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT6(Seasons.PAST_LIFE, "§7You need {} {}§7 to be cured of the curse.", List.of("kills amount", "kill/kills"))
    ,PASTLIFE_TWIST_FAIL(Seasons.PAST_LIFE, "Picking failed")
    ,PASTLIFE_TWIST_RANDOM(Seasons.PAST_LIFE, "§7Randomly picking the Boogeyman or the Secret Society...")
    ,PASTLIFE_TWIST_SOCIETY_ERROR_DISABLED(Seasons.PAST_LIFE, "The Secret Society is disabled in the config")
    ,PASTLIFE_TWIST_SOCIETY_ERROR_ENDED(Seasons.PAST_LIFE, "The Secret Society has already ended")
    ,PASTLIFE_TWIST_SOCIETY_ERROR_STARTED(Seasons.PAST_LIFE, "The Secret Society has already started")
    ,PASTLIFE_TWIST_SOCIETY_ERROR_QUEUED(Seasons.PAST_LIFE, "The Secret Society is already queued")
    ,PASTLIFE_TWIST_SOCIETY(Seasons.PAST_LIFE, "Added the Secret Society to queued session actions")
    ,PASTLIFE_TWIST_BOOGEYMAN_ERROR_DISABLED(Seasons.PAST_LIFE, "The Boogeyman is disabled in the config")
    ,PASTLIFE_TWIST_BOOGEYMAN_ERROR_CHOSEN(Seasons.PAST_LIFE, "The Boogeyman has already been chosen")
    ,PASTLIFE_TWIST_BOOGEYMAN_ERROR_QUEUED(Seasons.PAST_LIFE, "The Boogeyman is already queued")
    ,PASTLIFE_TWIST_BOOGEYMAN(Seasons.PAST_LIFE, "Added the Boogeyman to queued session actions")

    ,NICELIFE_NICELIST_START_TITLE_PT1(Seasons.NICE_LIFE, "§aThese players are on...")
    ,NICELIFE_NICELIST_START_TITLE_PT2(Seasons.NICE_LIFE, "§aTHE NICE LIST")
    ,NICELIFE_NICELIST_START_INFO_PT1(Seasons.NICE_LIFE, "\n §6[§e!§6]§7 At sunset, players on the nice list will vote to give a §2non-pink§7 name a life.\n")
    ,NICELIFE_NICELIST_START_INFO_PT2(Seasons.NICE_LIFE, " §6[§e!§6]§7 The majority of the §dpinks§7 must vote for the same player for the life to be given.\n")
    ,NICELIFE_NICELIST_START_INFO_PT3(Seasons.NICE_LIFE, " §6[§e!§6]§7 Pink names are not allowed to be targeted by any other players, including §creds§7.\n")
    ,NICELIFE_NICELIST_START_INFO_PT4(Seasons.NICE_LIFE, " §6[§e!§6]§7 You are on the nice list. Type {}§7 to choose who you would like to give a life to.\n", List.of("ClickHere"))
    ,NICELIFE_NICELIST_START_INFO_PT5(Seasons.NICE_LIFE, " §6[§e!§6]§7 You can change your vote at anytime, but the results will be locked in at sunset.\n")
    ,NICELIFE_NICELIST_REMOVED(Seasons.NICE_LIFE, " §6[§e!§6]§7 You are no longer on the Nice List")
    ,NICELIFE_NICELIST_VOTE(Seasons.NICE_LIFE, "\n §6[§e!§6]§7 You voted for {}§7.\n", List.of("Player"))
    ,NICELIFE_NICELIST_VOTE_REMINDER(Seasons.NICE_LIFE, "\n§7Don't forget to {}§7!\n", List.of("ClickHere"))
    ,NICELIFE_NICELIST_VOTE_TITLE(Seasons.NICE_LIFE, "Vote for who should get a life")
    ,NICELIFE_NICELIST_EMPTY(Seasons.NICE_LIFE, "The Nice List is empty")
    ,NICELIFE_NICELIST_LIST(Seasons.NICE_LIFE, "Nice List: {}", List.of("list"))
    ,NICELIFE_NICELIST_ADD_SINGLE(Seasons.NICE_LIFE, "Added {} to the Nice List", List.of("Player"))
    ,NICELIFE_NICELIST_ADD_MULTIPLE(Seasons.NICE_LIFE, "Added {} targets to the Nice List", List.of("number of targets"))
    ,NICELIFE_NICELIST_REMOVE_SINGLE(Seasons.NICE_LIFE, "Removed {} from the Nice List", List.of("Player"))
    ,NICELIFE_NICELIST_REMOVE_MULTIPLE(Seasons.NICE_LIFE, "Removed {} targets from the Nice List", List.of("number of targets"))
    ,NICELIFE_NAUGHTYLIST_START_TITLE_PT1(Seasons.NICE_LIFE, "§cThese players are on...")
    ,NICELIFE_NAUGHTYLIST_START_TITLE_PT2(Seasons.NICE_LIFE, "§cTHE NAUGHTY LIST")
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT1(Seasons.NICE_LIFE, Formatted.LOOSELY_STYLED,"\n §6[§e!§6]§7 You have voted for {} {} to be on the §cNAUGHTY LIST§7.\n", List.of("count", "person/people"))
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT2(Seasons.NICE_LIFE, " §6[§e!§6]§7 People on the §cnaughty list§7 have a purple name and can be killed.\n")
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT3(Seasons.NICE_LIFE, " §6[§e!§6]§7 They return to their previous colour at sunset. They can defend themselves.\n")
    ,NICELIFE_NAUGHTYLIST_EMPTY(Seasons.NICE_LIFE, "The Naughty List is empty")
    ,NICELIFE_NAUGHTYLIST_LIST(Seasons.NICE_LIFE, "Naughty List: {}", List.of("list"))
    ,NICELIFE_NAUGHTYLIST_ADD_SINGLE(Seasons.NICE_LIFE, "Added {} to the Naughty List", List.of("Player"))
    ,NICELIFE_NAUGHTYLIST_ADD_MULTIPLE(Seasons.NICE_LIFE, "Added {} targets to the Naughty List", List.of("number of targets"))
    ,NICELIFE_NAUGHTYLIST_REMOVE_SINGLE(Seasons.NICE_LIFE, "Removed {} from the Naughty List", List.of("Player"))
    ,NICELIFE_NAUGHTYLIST_REMOVE_MULTIPLE(Seasons.NICE_LIFE, "Removed {} targets from the Naughty List", List.of("number of targets"))
    ,NICELIFE_TRIVIA_VOTE_NICELIST(Seasons.NICE_LIFE, "Vote for who's been nice")
    ,NICELIFE_TRIVIA_VOTE_NAUGHTYLIST(Seasons.NICE_LIFE, "Vote for who's been naughty")
    ,NICELIFE_SLEEP_FAIL_LATE(Seasons.NICE_LIFE, "You can't seem to sleep right now")
    ,NICELIFE_SLEEP_FAIL_EARLY(Seasons.NICE_LIFE, "You are too excited to fall asleep")
    ,NICELIFE_VOTE_SET(Seasons.NICE_LIFE, "Next midnight vote will be '{}'", List.of("vote"))
    ,NICELIFE_WAKEUP_SINGLE(Seasons.NICE_LIFE, "Woke up {}", List.of("Player"))
    ,NICELIFE_WAKEUP_MULTIPLE(Seasons.NICE_LIFE, "Woke up {}", List.of("Player"))
    ,NICELIFE_NICELIST_ERROR(Seasons.NICE_LIFE, "The Nice List is not currently in progress")
    ,NICELIFE_NOTSLEEPING(Seasons.NICE_LIFE, "You are not sleeping")
    ,NICELIFE_NOT_NIGHT(Seasons.NICE_LIFE, "It is not night time")
    ,NICELIFE_VOTE_ERROR_UNKNOWN(Seasons.NICE_LIFE, "Vote type not found")
    ,NICELIFE_VOTE_ERROR_NICELIST_MISSING(Seasons.NICE_LIFE, "You are not on the nice list")
    ,NICELIFE_VOTE_ERROR_NICELIST_PROGRESS(Seasons.NICE_LIFE, "Nice list voting is not in progress")
    ,NICELIFE_VOTE_ERROR_DEAD(Seasons.NICE_LIFE, "Dead players cannot vote")
    ,NICELIFE_VOTE_ERROR_WATCHER(Seasons.NICE_LIFE, "Watchers cannot vote")
    ,NICELIFE_VOTE_ERROR_TARGET(Seasons.NICE_LIFE, "There are no players to vote for")
    ,NICELIFE_TRIVIA_NOTFOUND(Seasons.NICE_LIFE, "Could not find trivia with that question")
    ,NICELIFE_TRIVIA_ASSIGNED(Seasons.NICE_LIFE, "Successfuly assigned trivia")
    ,NICELIFE_TRIVIA_RESET(Seasons.NICE_LIFE, "Reset assigned trivia")
    ,NICELIFE_TRIVIA_ALL_WRONG_PT1(Seasons.NICE_LIFE, "§f<§2§mTrivia§m§2 Santa Bot§f>§4 WRONG! WRONG! WRONG! ALL WRONG!")
    ,NICELIFE_TRIVIA_ALL_WRONG_PT2(Seasons.NICE_LIFE, "§f<§2§mTrivia§m§2 Santa Bot§f>§4 SNOW MUST GO ON!")
    ,NICELIFE_REDWINTER_PT1(Seasons.NICE_LIFE, "§eThe last yellow falls..")
    ,NICELIFE_REDWINTER_PT2(Seasons.NICE_LIFE, "§cRed winter is here..")
    ,NICELIFE_NAUGHTYLIST_END_TITLE(Seasons.NICE_LIFE, "§cPlayers return to normal in...")
    ,NICELIFE_NICELIST_VOTE_END_TITLE(Seasons.NICE_LIFE, "§cThe nice vote will end in...")
    ,NICELIFE_VOTE_COUNTDOWN_3(Seasons.NICE_LIFE, "§23..")
    ,NICELIFE_VOTE_COUNTDOWN_2(Seasons.NICE_LIFE, "§e2..")
    ,NICELIFE_VOTE_COUNTDOWN_1(Seasons.NICE_LIFE, "§c1..")
    ,NICELIFE_NICELIST_VOTE_ERROR_INSUFFICIENT(Seasons.NICE_LIFE, "§cInsufficient votes")
    ,NICELIFE_NICELIST_VOTE_ERROR_AGREEMENT(Seasons.NICE_LIFE, "§cNo agreement reached")
    ,NICELIFE_NICELIST_VOTE_RESULT(Seasons.NICE_LIFE, "§2The winner is...")

//ModifiableText.NAME.get(

    /*
    ,NAME("")
    ,DOUBLELIFE_(Seasons.DOUBLE_LIFE, "")
    ,SECRETLIFE_(Seasons.SECRET_LIFE, "")
    ,WILDLIFE_(Seasons.WILD_LIFE, "")
    ,NICELIFE_(Seasons.NICE_LIFE, "")

    ,NAME("", List.of("Player"))
    ,NAME_SINGLE("", List.of("Player"))
    ,NAME_MULTIPLE("", List.of("number of targets"))
    */
    ;

    final Formatted formatted;
    final String name;
    final String defaultValue;
    final List<String> args;
    final Seasons requiredSeason;

    ModifiableText(String defaultValue) {
        this(Formatted.STYLED, defaultValue);
    }
    ModifiableText(String defaultValue, List<String> args) {
        this(Formatted.STYLED, defaultValue, args);
    }
    ModifiableText(Formatted formatted, String defaultValue) {
        this(formatted, defaultValue, null);
    }
    ModifiableText(Formatted formatted, String defaultValue, List<String> args) {
        this(null, formatted, defaultValue, args);
    }

    ModifiableText(Seasons requiredSeason, String defaultValue) {
        this(requiredSeason, Formatted.STYLED, defaultValue);
    }
    ModifiableText(Seasons requiredSeason, String defaultValue, List<String> args) {
        this(requiredSeason, Formatted.STYLED, defaultValue, args);
    }
    ModifiableText(Seasons requiredSeason, Formatted formatted, String defaultValue) {
        this(requiredSeason, formatted, defaultValue, null);
    }

    ModifiableText(Seasons requiredSeason, Formatted formatted, String defaultValue, List<String> args) {
        this.requiredSeason = requiredSeason;
        this.formatted = formatted;
        this.name = this.name().toLowerCase(Locale.ROOT).replace("_",".");
        this.defaultValue = ModifiableTextManager.fromMinecraftColorFormatting(defaultValue.replace("{}","%s"));
        this.args = args;
    }

    public Component get(Object... args) {
        return ModifiableTextManager.get(this.formatted, this.name, args);
    }

    public String getString(Object... args) {
        return get(args).getString();
    }

    public Formatted getFormatted() {
        return formatted;
    }

    public String getRegisterDefaultValue() {
        if (currentSeason != null && currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            String modified = null;

            if (this == GIVELIFE_RECEIVE_OTHER) modified = "{} received {} from {}";
            else if (this == GIVELIFE_RECEIVE_SELF) modified = "You received {} from {}";
            else if (this == GIVELIFE_RECEIVE_SELF_TITLE) modified = "You received {}";
            else if (this == BOOGEYMAN_MESSAGE) modified = "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, your time will be dropped to the next color. All loyalties and friendships are removed while you are the Boogeyman.";
            else if (this == LIVES_UNASSIGNED) modified = "You have not been assigned any time yet";
            else if (this == LIVES_GET_SELF) modified = "You have {} left";
            else if (this == LIVES_UNASSIGNED_ALL) modified = "Nobody has been assigned time yet";
            else if (this == LIVES_ASSIGNED_LIST) modified = "Assigned Times: \n";
            else if (this == LIVES_ASSIGNED_LIST_ENTRY) modified = "{} has {} left\n";
            else if (this == LIVES_UNASSIGNED_OTHER) modified = "{} has not been assigned any time";
            else if (this == LIVES_RELOADING) modified = "§7Reloading times...";
            else if (this == LIVES_SET_SINGLE) modified = "Set {}'s time to {}";
            else if (this == LIVES_SET_MULTIPLE) modified = "Set time to {} for {} targets";
            else if (this == LIVES_CHANGE_SINGLE) modified = "{} {} {} {}";
            else if (this == LIVES_CHANGE_MULTIPLE) modified = "{} {} {} {} targets";
            else if (this == LIVES_RESET_SINGLE) modified = "Reset {}'s time";
            else if (this == LIVES_RESET_MULTIPLE) modified = "Reset time of {} targets";
            else if (this == LIVES_RESET_EVERYONE) modified = "Reset everyone's time";
            else if (this == LIVES_RANDOMIZE_SINGLE) modified = "§7Assigning random time to {}§7...";
            else if (this == LIVES_RANDOMIZE_MULTIPLE) modified = "§7Assigning random time to {}§7 targets...";
            else if (this == GIVELIFE_ERROR_NONE) modified = "You do not have any time to give";
            else if (this == GIVELIFE_ERROR_SELF) modified = "You cannot give time to yourself";
            else if (this == GIVELIFE_ERROR_NOT_ENOUGH) modified = "You cannot give away any more time";
            else if (this == GIVELIFE_ERROR_TOO_MANY) modified = "That player cannot receive any more time";
            else if (this == GIVELIFE_ERROR_SOULMATE) modified = "You cannot give time to your soulmate";
            else if (this == FINAL_DEATH) modified = "{} ran out of time.";
            else if (this == FINAL_DEATH_TITLE_SUBTITLE) modified = "ran out of time!";
            else if (this == LIVES_RANDOMIZE_RESULT) modified = "{}§a to live.";
            else if (this == BOOGEYMAN_FAIL) modified = "{}§7 failed to kill a player while being the §cBoogeyman§7. Their time has been dropped to {}";

            if (modified != null) {
                return ModifiableTextManager.fromMinecraftColorFormatting(modified.replace("{}","%s"));
            }
        }
        return this.defaultValue;
    }

    public List<String> getRegisterArgs() {
        if (currentSeason != null && currentSeason.getSeason() == Seasons.LIMITED_LIFE) {

            if (this == GIVELIFE_RECEIVE_OTHER) return List.of("Receiver", "time", "Giver");
            else if (this == GIVELIFE_RECEIVE_SELF) return List.of("time", "Player");
            else if (this == GIVELIFE_RECEIVE_SELF_TITLE) return List.of("time");
            else if (this == LIVES_GET_SELF) return List.of("time");
            else if (this == LIVES_ASSIGNED_LIST_ENTRY) return List.of("Player", "time");
            else if (this == LIVES_SET_SINGLE) return List.of("Player", "time");
            else if (this == LIVES_SET_MULTIPLE) return List.of("time", "number of targets");
            else if (this == LIVES_CHANGE_SINGLE) return List.of("Added/Removed", "time", "to/from", "Player");
            else if (this == LIVES_CHANGE_MULTIPLE) return List.of("Added/removed", "time", "to/from", "number of targets");
            else if (this == LIVES_RANDOMIZE_RESULT) return List.of("amount");
            else if (this == BOOGEYMAN_FAIL) return List.of("Player", "time");

        }
        return this.args;
    }

    public static void registerAllTexts() {
        for (ModifiableText modifiableText : ModifiableText.values()) {
            String defaultValue = modifiableText.getRegisterDefaultValue();

            /*
            // Tests for any argument count mismatch errors
            int argsInValue = 0;

            int index = 0;
            while ((index = defaultValue.indexOf("%s", index)) != -1) {
                argsInValue++;
                index += 2;
            }

            int args = 0;
            if (modifiableText.getRegisterArgs() != null) {
                args = modifiableText.getRegisterArgs().size();
            }
            if (argsInValue != args) {
                Main.LOGGER.error("Args count mismatch in " + modifiableText.name);
            }
            */

            if (modifiableText.requiredSeason != null && currentSeason != null && currentSeason.getSeason() != modifiableText.requiredSeason) continue;
            ModifiableTextManager.register(modifiableText.name, defaultValue, modifiableText.getRegisterArgs());
        }
    }
    public static ModifiableText fromName(String name) {
        if (name == null) return null;
        if (name.startsWith("text.")) name = name.substring(5);
        for (ModifiableText modifiableText : ModifiableText.values()) {
            if (modifiableText.name.equals(name)) {
                return modifiableText;
            }
        }
        return null;
    }
}
