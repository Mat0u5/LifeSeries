package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.seasons.season.Seasons;
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

    ,WILDLIFE_TRIVIA_RECEIVE_EFFECT(" §a§l+ §7{}§6 {}", List.of("Effect Name", "Roman Numeral")) //TODO check, this was formatted loosely

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

    ,SESSION_ERROR_START("§cThe session has not started")
    ,SESSION_START_PROMPT("\nUse §b'/session timer set <time>'§f to set the desired session time.\nAfter that, use §b'/session start'§f to start the session.")

    ,MUTED_TRIVIABOT("<Trivia Bot> No phoning a friend allowed!")
    ,MUTED_WATCHER("Watchers aren't allowed to talk in chat! Admins can change this behavior in the config.")
    ,MUTED_DEADPLAYER("Dead players aren't allowed to talk in chat! Admins can change this behavior in the config.")

    ,WATCHER_JOIN("§7§nYou are now a Watcher.\n\n§7Watchers are players that are online, but are not affected by most season mechanics. They can only observe - this is very useful for spectators and for admins.")
    ,WATCHER_LEAVE("§7You are no longer a Watcher.")

    ,BOOGEYMAN_MESSAGE("§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman.")
    ,BOOGEYMAN_NOTICE_ADDED("§c [NOTICE] You are now a Boogeyman!")
    ,BOOGEYMAN_NOTICE_REMOVED("§c [NOTICE] You are no longer a Boogeyman!")
    ,BOOGEYMAN_NOTICE_RESET("§c [NOTICE] Your Boogeyman  fail/cure status has been reset")
    ,BOOGEYMAN_KILLS_REQUIRED("§7You need {} {}§7 to be cured of the curse.", List.of("Kills Amount", "Kill/kills pluralization"))
    ,BOOGEYMAN_LATEJOIN("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!")
    ,BOOGEYMAN_LIST("Current Boogeymen: {}", List.of("List"))
    ,BOOGEYMAN_FAIL_NOTICE("§cYou only have 5 minutes left to kill someone as the Boogeyman before you fail!")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT1("§7You are the boogeyman.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT2("§7You must by any means necessary kill a §agreen§7 or §eyellow§7 name\n§7by direct action to be cured of the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT3("§7If you fail, you will become a §cred name§7.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT4("§7Other players may defend themselves.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT5("§7Voluntary sacrifices will not cure the curse.")
    ,BOOGEYMAN_PASTLIFE_MESSAGE_PT6("§7You need {} {}§7 to be cured of the curse.", List.of("Kills Amount", "Kill/kills pluralization"))

    ,SEASON_COMMANDS_ADMIN("§7{} commands: §r{}", List.of("Season", "Commands")) //TODO formatLoosely
    ,SEASON_COMMANDS("§7{} non-admin commands: §r{}", List.of("Season", "Commands")) //TODO formatLoosely

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

    ,SOCIETY_INITIATE_REMINDER("§7When you are alone, type \"/initiate\"")
    ,SOCIETY_INITIATED_PT1("§7You have been chosen to be part of the §csecret society§7.")
    ,SOCIETY_INITIATED_GROUP_PT1("§7There {} §c{}§7 other {}. Find them.", List.of("Is/are pluralization", "Member Amount", "Member/s pluralization"))//TODO formatLoosely
    ,SOCIETY_INITIATED_GROUP_PT2("§7Together, secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", List.of("Kills Needed", "Player/s pluralization"))//TODO formatLoosely
    ,SOCIETY_INITIATED_GROUP_PT3("§7Find the other members with the secret word:")
    ,SOCIETY_INITIATED_GROUP_PT4("§d\"{}\"", List.of("Secret Word"))//TODO formatLoosely
    ,SOCIETY_INITIATED_ALONE_PT1("§7You are alone.")
    ,SOCIETY_INITIATED_ALONE_PT2("§7Secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", List.of("Kills Needed", "Player/s pluralization"))//TODO formatLoosely
    ,SOCIETY_INITIATED_PT2("§7Type \"/society success\" when you complete your goal.")
    ,SOCIETY_INITIATED_PT3("§7Don't tell anyone else about the society.")
    ,SOCIETY_INITIATED_PT4("§7If you fail...")
    ,SOCIETY_INITIATED_PUNISHMENT("§7Type \"/society fail\", and you all lose §c{} {}§7.", List.of("Lives Lost", "Life/lives pluralization"))//TODO formatLoosely
    ,SOCIETY_NOTICE_ADDED("§c [NOTICE] You are now a Secret Society member!")
    ,SOCIETY_NOTICE_REMOVED("§c [NOTICE] You are no longer a Secret Society member!")
    ,SOCIETY_OTHER_MEMBER_ADDED("A player has been added to the Secret Society.")
    ,SOCIETY_OTHER_MEMBER_REMOVED("A player has been removed from the Secret Society.")

    ,SUBIN_END("§6You are no longer subbing in for {}", List.of("Player"))//TODO formatLoosely
    ,SUBIN_END_OTHER("§6{} is no longer subbing in for you", List.of("Player"))//TODO formatLoosely

    ModifiableText.NAME.get()
    ModifiableText.NAME.getString()
    player.sendSystemMessage(ModifiableText.NAME.get());

    ,NAME("")
    ,NAME("")
    ,NAME("")
    ,NAME("")
    ,NAME("")
    ,NAME("", List.of("Player"))
    ,NAME("", List.of("Player"))
    ,NAME("", List.of("Player"))
    ,NAME("", List.of("Player"))
    ,NAME("", List.of("Player"))
    ,NAME("", List.of("Player"))
    //,NAME("")
    //,NAME("", List.of("Player"))
    ;


    final String name;
    final String defaultValue;
    final List<String> args;

    ModifiableText(String defaultValue) {
        this( defaultValue, null);
    }

    ModifiableText(String defaultValue, List<String> args) {
        this.name = this.name().toLowerCase(Locale.ROOT).replace("_",".");
        this.defaultValue = ModifiableTextManager.fromMinecraftColorFormatting(defaultValue.replace("{}","%s"));
        this.args = args;
    }

    public Component get(Object... args) {
        return ModifiableTextManager.get(this.name, args);
    }

    public String getString(Object... args) {
        return get(args).getString();
    }

    public String getRegisterDefaultValue() {
        //Make sure to actually use the custom # ans % formatting here. TODO
        if (this == GIVELIFE_RECEIVE_OTHER && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return "{} received {} from {}";
        if (this == GIVELIFE_RECEIVE_SELF && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return "You received {} from {}";
        if (this == GIVELIFE_RECEIVE_SELF_TITLE && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return "You received {}";
        if (this == BOOGEYMAN_MESSAGE && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, your time will be dropped to the next color. All loyalties and friendships are removed while you are the Boogeyman.";
        return this.defaultValue;
    }
    public List<String> getRegisterArgs() {
        if (this == GIVELIFE_RECEIVE_OTHER && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return List.of("Receiver", "Time", "Giver");
        if (this == GIVELIFE_RECEIVE_SELF && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return List.of("Time", "Player");
        if (this == GIVELIFE_RECEIVE_SELF_TITLE && currentSeason.getSeason() == Seasons.LIMITED_LIFE) return List.of("Time");
        return this.args;
    }

    public static void registerAllTexts() {
        for (ModifiableText modifiableText : ModifiableText.values()) {
            ModifiableTextManager.register(modifiableText.name, modifiableText.getRegisterDefaultValue(), modifiableText.getRegisterArgs());
        }
    }
}
