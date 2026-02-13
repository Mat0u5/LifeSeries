package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.Formatted;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

import static net.mat0u5.lifeseries.Main.currentSeason;

public enum ModifiableText {
    //TODO test if the newlines work properly

    GIVELIFE_DOUBLELIFE_ACCEPT("Your soulmate wants to give a life to {}.\nClick {} to accept the request.", List.of("Player", "ClickHere"))
    ,GIVELIFE_RECEIVE_SELF("You received a life from {}", List.of("Player"))
    ,GIVELIFE_RECEIVE_OTHER("{} received a life from {}", List.of("Receiver", "Giver"))
    ,GIVELIFE_RECEIVE_SELF_TITLE("You received a life")
    ,GIVELIFE_RECEIVE_SELF_TITLE_SUBTITLE("from {}", List.of("Player"))

    ,GIVEHEART_ERROR_SELF("Nice Try.")
    ,GIVEHEART_ERROR_MULTIPLE("You have already gifted a heart this session")
    ,GIVEHEART_ERROR_DEAD("That player is not alive")
    ,GIVEHEART_SEND("You have gifted a heart to {}", List.of("Player"))
    ,GIVEHEART_RECEIVE("{} gave you a heart", List.of("Player"))

    ,WILDLIFE_TRIVIA_RECEIVE_EFFECT(Formatted.LOOSELY_STYLED, " §a§l+ §7{}§6 {}", List.of("effect name", "amplifier"))
    ,WILDLIFE_SNAIL_TEXTURE_INFO(Formatted.LOOSELY_STYLED,"§fClick {}§f to open the Snail Textures info page in the Wiki.", List.of("ClickHere"))

    ,DOUBLELIFE_TELEPORT("§6Woosh!")
    ,DOUBLELIFE_TELEPORT_SUCCESS("Randomly distributed players.")

    ,PASTLIFE_SESSION_START("§7Past Life session started:\n§7 Type §f\"/pastlife boogeyman\"§7 to have the Boogeyman in this session.\n§7 Type §f\"/pastlife society\"§7 to have the Secret Society in this session.\n§7 Or type §f\"/pastlife pickRandom\"§7 if you want the game to pick randomly.\n")

    ,SECRETLIFE_TASK_MISSING("§cYou do not have a secret task book in your inventory.")
    ,SECRETLIFE_TASK_MISSING_OTHER("{} does not have a task book in their inventory nor a pre-assigned task", List.of("Player"))
    ,SECRETLIFE_TASK_PRESENT("{} has a task book in their inventory", List.of("Player"))
    ,SECRETLIFE_TASK_PREASSIGNED("{} has a pre-assigned task", List.of("Player"))
    ,SECRETLIFE_TASK_READFAIL("Failed to read task contents")
    ,SECRETLIFE_TASK_SHOW("§7Click {}§7 to show the task they have.", List.of("ClickHere"))
    ,SECRETLIFE_TASK_SHOW_PAST("§7Click {}§7 to see what {}§7's task was.", List.of("ClickHere", "Player"))
    ,SECRETLIFE_SECRETKEEPER_INUSE("§cSomeone else is using the Secret Keeper right now.")
    ,SECRETLIFE_TASK_PENDING("{} wants to succeed their task.", List.of("Player"))
    ,SECRETLIFE_TASK_PENDING_ACCEPT("§7Click {}§7 to confirm this action.", List.of("ClickHere"))
    ,SECRETLIFE_TASK_PENDING_NOTIFICATION("§cYour task confirmation needs to be approved by an admin.")
    ,SECRETLIFE_TASK_SUCCEED("{}§a succeeded their task.", List.of("Player"))
    ,SECRETLIFE_TASK_REROLL_HARD_FAIL("§cYou cannot re-roll a Hard task.")
    ,SECRETLIFE_TASK_REROLL_HARD_FAIL_RED("§cYou cannot re-roll a Hard task. If you want your red task instead, click the Fail button.")
    ,SECRETLIFE_TASK_NOT_SUBMITTED(Formatted.LOOSELY_STYLED,"§4{}§c still {} not submitted / failed a task this session.", List.of("Players", "has/have"))

    ,SESSION_ERROR_START("§cThe session has not started")
    ,SESSION_START_PROMPT("\nUse §b'/session timer set <time>'§f to set the desired session time.\nAfter that, use §b'/session start'§f to start the session.")

    ,MUTED_TRIVIABOT("<Trivia Bot> No phoning a friend allowed!")
    ,MUTED_WATCHER("Watchers aren't allowed to talk in chat! Admins can change this behavior in the config.")
    ,MUTED_DEADPLAYER("Dead players aren't allowed to talk in chat! Admins can change this behavior in the config.")

    ,WATCHER_JOIN("§7§nYou are now a Watcher.\n\n§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe - this is very useful for spectators and for admins.")
    ,WATCHER_LEAVE("§7You are no longer a Watcher.")
    ,WATCHER_LIST(Formatted.LOOSELY_STYLED,"Current Watchers: §7{}", List.of("Watchers"))

    ,BOOGEYMAN_MESSAGE("§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman.")
    ,BOOGEYMAN_NOTICE_ADDED("§c [NOTICE] You are now a Boogeyman!")
    ,BOOGEYMAN_NOTICE_REMOVED("§c [NOTICE] You are no longer a Boogeyman!")
    ,BOOGEYMAN_NOTICE_RESET("§c [NOTICE] Your Boogeyman  fail/cure status has been reset")
    ,BOOGEYMAN_KILLS_REQUIRED("§7You need {} {}§7 to be cured of the curse.", List.of("kills amount", "kill/kills"))
    ,BOOGEYMAN_LATEJOIN("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!")
    ,BOOGEYMAN_LIST("Current Boogeymen: {}", List.of("list"))
    ,BOOGEYMAN_FAIL_NOTICE("§cYou only have 5 minutes left to kill someone as the Boogeyman before you fail!")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT1("§7You are the boogeyman.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT2("§7You must by any means necessary kill a §agreen§7 or §eyellow§7 name\n§7by direct action to be cured of the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT3("§7If you fail, you will become a §cred name§7.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT4("§7Other players may defend themselves.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT5("§7Voluntary sacrifices will not cure the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT6("§7You need {} {}§7 to be cured of the curse.", List.of("kills amount", "kill/kills"))

    ,SEASON_COMMANDS_ADMIN(Formatted.LOOSELY_STYLED, "§7{} commands: §r{}", List.of("season", "commands"))
    ,SEASON_COMMANDS(Formatted.LOOSELY_STYLED, "§7{} non-admin commands: §r{}", List.of("season", "commands"))
    ,SEASON_INVALID("That is not a valid season!")
    ,SEASON_INVALID_HELP(Formatted.PLAIN, "You must choose one of the following: {}", List.of("season names"))
    ,SEASON_SELECT_WARNING("§7WARNING: you have already selected a season, changing it might cause some saved data to be lost (lives, ...)\n§7If you are sure, use '§f/lifeseries setSeries <season> confirm§7'")
    ,SEASON_CHANGE(Formatted.LOOSELY_STYLED,"§aSuccessfully changed the season to {}.", List.of("season"))

    ,SESSION_STARTED(Formatted.LOOSELY_STYLED,"§6Session started! §7[{}]\n§f/session timer showDisplay§7 - toggles a session timer on your screen.", List.of("session length"))
    ,SESSION_ACTION_ENTRY(Formatted.LOOSELY_STYLED,"§7- {}", List.of("action name"))
    ,SESSION_ACTION_ENTRY_LONG(Formatted.LOOSELY_STYLED,"§7- {} §f[{}]", List.of("action name", "trigger time"))

    ,NICELIFE_NICELIST_START_TITLE_PT1("§aThese players are on...")
    ,NICELIFE_NICELIST_START_TITLE_PT2("§aTHE NICE LIST")
    ,NICELIFE_NICELIST_START_INFO_PT1("\n §6[§e!§6]§7 At sunset, players on the nice list will vote to give a §2non-pink§7 name a life.\n")
    ,NICELIFE_NICELIST_START_INFO_PT2(" §6[§e!§6]§7 The majority of the §dpinks§7 must vote for the same player for the life to be given.\n")
    ,NICELIFE_NICELIST_START_INFO_PT3(" §6[§e!§6]§7 Pink names are not allowed to be targeted by any other players, including §creds§7.\n")
    ,NICELIFE_NICELIST_START_INFO_PT4(" §6[§e!§6]§7 You are on the nice list. Type {}§7 to choose who you would like to give a life to.\n", List.of("ClickHere"))
    ,NICELIFE_NICELIST_START_INFO_PT5(" §6[§e!§6]§7 You can change your vote at anytime, but the results will be locked in at sunset.\n")
    ,NICELIFE_NICELIST_REMOVED(" §6[§e!§6]§7 You are no longer on the Nice List")
    ,NICELIFE_NICELIST_VOTE("\n §6[§e!§6]§7 You voted for {}§7.\n", List.of("Player"))
    ,NICELIFE_NICELIST_VOTE_REMINDER("\n§7Don't forget to {}§7!\n", List.of("ClickHere"))
    ,NICELIFE_NICELIST_VOTE_TITLE("Vote for who should get a life")
    ,NICELIFE_TRIVIA_VOTE_NICELIST("Vote for who's been nice")
    ,NICELIFE_TRIVIA_VOTE_NAUGHTYLIST("Vote for who's been naughty")
    ,NICELIFE_SLEEP_FAIL_LATE("You can't seem to sleep right now")
    ,NICELIFE_SLEEP_FAIL_EARLY("You are too excited to fall asleep")

    ,NICELIFE_NAUGHTYLIST_START_TITLE_PT1("§cThese players are on...")
    ,NICELIFE_NAUGHTYLIST_START_TITLE_PT2("§cTHE NAUGHTY LIST")
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT1(Formatted.LOOSELY_STYLED,"\n §6[§e!§6]§7 You have voted for {} {} to be on the §cNAUGHTY LIST§7.\n", List.of("count", "person/people"))
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT2(" §6[§e!§6]§7 People on the §cnaughty list§7 have a purple name and can be killed.\n")
    ,NICELIFE_NAUGHTYLIST_START_INFO_PT3(" §6[§e!§6]§7 They return to their previous colour at sunset. They can defend themselves.\n")

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

    ,SUBIN_END(Formatted.LOOSELY_STYLED, "§6You are no longer subbing in for {}", List.of("Player"))
    ,SUBIN_END_OTHER(Formatted.LOOSELY_STYLED, "§6{} is no longer subbing in for you", List.of("Player"))
    ,SUBIN_ERROR_ALREADY_SUBBING(Formatted.PLAIN, "{} is already subbing in for {}", List.of("Player", "Subin"))
    ,SUBIN_ERROR_ALREADY_SUBBED(Formatted.PLAIN, "{} is already being subbed in for by {}", List.of("Player", "Subbed"))
    ,SUBIN_ERROR_MISSING(Formatted.PLAIN, "{} is not subbing in for anyone", List.of("Player"))
    ,SUBIN_LIST_ENTRY(Formatted.LOOSELY_STYLED," §7{} is subbinng in for {}", List.of("Player", "Subin"))

    ,CLAIMKILL_ERROR_NODEATH(Formatted.PLAIN, "{} did not die in the last 2 minutes. Or they might have been killed by a player directly.", List.of("Player"))
    ,CLAIMKILL_ERROR_SELF("You cannot claim credit for your own death :P")
    ,CLAIMKILL("{}§7 claims credit for {}§7's death.", List.of("Killer", "Victim"))
    ,CLAIMKILL_VALIDATE("§7Click {}§7 to accept the claim if you think it's valid.", List.of("ClickHere"))

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

    ,SOULMATE_ERROR_EXISTS(Formatted.PLAIN, "{} already has a soulmate", List.of("Player"))
    ,SOULMATE_ERROR_FORCE_EXISTS(Formatted.PLAIN, "{} is already forced with someone", List.of("Player"))
    ,SOULMATE_ERROR_MISSING(Formatted.PLAIN, "{} does not have a soulmate", List.of("Player"))
    ,SOULMATE_ERROR_OFFLINE(Formatted.PLAIN, "{} 's soulmate is not online right now", List.of("Player"))


    ,SECRETLIFE_HEART_GAIN(Formatted.LOOSELY_STYLED,"§c+{} {}", List.of("amount", "heart/hearts"))
    ,SECRETLIFE_TASK_NAME("{}'s Secret Task", List.of("Player"))
    ,SECRETLIFE_TASK_AUTHOR("Secret Keeper")
    ,WILDLIFE_SNAIL_DEFAULT_NAME(Formatted.PLAIN,"{}'s Snail", List.of("Player"))



    ,CLAIMKILL_ACCEPT("{}§7's kill claim on {}§7 was accepted.", List.of("Killer", "Victim"))

    ,GIVELIFE_ERROR_NONE("You do not have any lives to give")
    ,GIVELIFE_ERROR_SELF("You cannot give lives to yourself")
    ,GIVELIFE_ERROR_NOT_ENOUGH("You cannot give away any more lives")
    ,GIVELIFE_ERROR_TOO_MANY("That player cannot receive any more lives")
    ,GIVELIFE_ERROR_SOULMATE("You cannot give lives to your soulmate")

    ,SERIES_DISABLE("The Life Series has been {}", List.of("enabled/disabled"))

    ,SEASON_CHANGING("§7Changing the season to {}§7...", List.of("season"))
    ,SEASON_CHANGED(Formatted.LOOSELY_STYLED,"§aSuccessfully changed the season to {}", List.of("season"))//TODO test
    ,SEASON_GET("Current season: {}", List.of("season"))
    ,MOD_VERSION("Mod version: {}", List.of("version"))
    ,MOD_ERROR_GENERAL("§cSomething went wrong")

    ,LIFESKINS_SKIN_SET("Set {}'s skin to {}", List.of("Player", "username"))
    ,LIFESKINS_USERNAME_SET("Set {}'s username to {}", List.of("Player", "username"))
    ,LIFESKINS_NICKNAME_SET("Set {}'s nickname to {}", List.of("Player", "nickname"))
    ,LIFESKINS_NICKNAME_RESET("Reset {}'s nickname", List.of("Player"))

    ,SESSION_PAUSE_QUEUE("The session will pause at {} for {}", List.of("time", "duration"))
    ,SESSION_END_INFO("The session ends in {}", List.of("time"))
    ,SESSION_SKIP("Skipped {} in the session length", List.of("time"))
    ,SESSION_LENGTH_SET("The session length has been set to {}", List.of("time"))
    ,SESSION_LENGTH_ADD("Added {} to the session length", List.of("time"))
    ,SESSION_LENGTH_REMOVE("Removed {} from the session length", List.of("time"))

    ,SIDETITLE_SINGLE("Showing new side title for {}", List.of("Player"))
    ,SIDETITLE_MULTIPLE("Showing new side title for {} players", List.of("number of targets"))

    ,WATCHER_ADD_SINGLE("{} is now a Watcher", List.of("Player"))
    ,WATCHER_ADD_MULTIPLE("{} targets are now Watchers", List.of("number of targets"))
    ,WATCHER_REMOVE_SINGLE("{} is no longer a Watcher", List.of("Player"))
    ,WATCHER_REMOVE_MULTIPLE("{} targets are no longer Watchers", List.of("number of targets"))

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

    ,FINAL_DEATH("{} ran out of lives.", List.of("Player"))
    ,FINAL_DEATH_TITLE("", List.of("Player"))
    ,FINAL_DEATH_TITLE_SUBTITLE("ran out of lives!", List.of("Player"))

    ,LIVES_RANDOMIZE_RESULT("{}§a {}.", List.of("amount", "life/lives"))

    ,SEASON_KILL_GAINLIFE("{}§7 gained a life for killing {}.", List.of("Killer", "Victim"))
    ,SEASON_KILL_UNJUSTIFIED("§c [Unjustified Kill?] {}§7 was killed by {}", List.of("Killer", "Victim"))

    ,DOUBLELIFE_SOULMATE_TITLE_UNKNOWN("§a????")
    ,DOUBLELIFE_SOULMATE_TITLE_PLAYER("{}", List.of("Player"))

    ,DOUBLELIFE_SOULMATE_PREVENT("{}'s soulmate now cannot be {} when the next randomization happens.", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_FORCE("{}'s soulmate will be {} when the next randomization happens.", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_SET("{}'s soulmate is now {}", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_GET("{}'s soulmate is {}", List.of("Player", "Soulmate"))
    ,DOUBLELIFE_SOULMATE_RESET_SINGLE("{}'s soulmate was reset", List.of("Player"))
    ,DOUBLELIFE_SOULMATE_RESET_MULTIPLE("Soulmate was reset for {} targets", List.of("number of targets"))

    ,LIMITEDLIFE_CHANGE_COLOR("{}§7 is now a {} name§7.", List.of("Player", "color"))

    ,NICELIFE_NICELIST_EMPTY("The Nice List is empty")
    ,NICELIFE_NICELIST_LIST("Nice List: {}", List.of("list"))
    ,NICELIFE_NICELIST_ADD_SINGLE("Added {} to the Nice List", List.of("Player"))
    ,NICELIFE_NICELIST_ADD_MULTIPLE("Added {} targets to the Nice List", List.of("number of targets"))
    ,NICELIFE_NICELIST_REMOVE_SINGLE("Removed {} from the Nice List", List.of("Player"))
    ,NICELIFE_NICELIST_REMOVE_MULTIPLE("Removed {} targets from the Nice List", List.of("number of targets"))
    ,NICELIFE_NAUGHTYLIST_EMPTY("The Naughty List is empty")
    ,NICELIFE_NAUGHTYLIST_LIST("Naughty List: {}", List.of("list"))
    ,NICELIFE_NAUGHTYLIST_ADD_SINGLE("Added {} to the Naughty List", List.of("Player"))
    ,NICELIFE_NAUGHTYLIST_ADD_MULTIPLE("Added {} targets to the Naughty List", List.of("number of targets"))
    ,NICELIFE_NAUGHTYLIST_REMOVE_SINGLE("Removed {} from the Naughty List", List.of("Player"))
    ,NICELIFE_NAUGHTYLIST_REMOVE_MULTIPLE("Removed {} targets from the Naughty List", List.of("number of targets"))

    ,NICELIFE_VOTE_SET("Next midnight vote will be '{}'", List.of("vote"))
    ,NICELIFE_WAKEUP_SINGLE("Woke up {}", List.of("Player"))
    ,NICELIFE_WAKEUP_MULTIPLE("Woke up {}", List.of("Player"))

ModifiableText.NAME.get(
/*
,NAME("")
,NAME("", List.of("Player"))
,NAME("", List.of("number of targets"))
*/

    ;


    final Formatted formatted;
    final String name;
    final String defaultValue;
    final List<String> args;

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
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
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
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {

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
            ModifiableTextManager.register(modifiableText.name, modifiableText.getRegisterDefaultValue(), modifiableText.getRegisterArgs());
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
