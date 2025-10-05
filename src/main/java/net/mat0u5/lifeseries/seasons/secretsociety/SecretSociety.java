package net.mat0u5.lifeseries.seasons.secretsociety;

import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.mat0u5.lifeseries.Main.*;

public class SecretSociety {
    public boolean SOCIETY_ENABLED = false;
    public double START_TIME = 5.0;
    public int MEMBER_COUNT = 3;
    public List<String> FORCE_MEMBERS = new ArrayList<>();
    public List<String> IGNORE_MEMBERS = new ArrayList<>();
    public List<String> POSSIBLE_WORDS = new ArrayList<>(List.of("Hammer","Magnet","Throne","Gravity","Puzzle","Spiral","Pivot","Flare"));
    public int KILL_COUNT = 2;
    public int PUNISHMENT_LIVES = -2;
    public boolean SOUND_ONLY_MEMBERS = false;

    public static final int INITIATE_MESSAGE_DELAYS = 15*20;
    public List<SocietyMember> members = new ArrayList<>();
    public boolean societyStarted = false;
    public boolean societyEnded = false;
    public long ticks = 0;
    public String secretWord = "";
    public Random rnd = new Random();

    public void onReload() {
        SOCIETY_ENABLED = seasonConfig.SECRET_SOCIETY.get(seasonConfig);
        if (!SOCIETY_ENABLED) {
            onDisabledSociety();
        }

        MEMBER_COUNT = seasonConfig.SECRET_SOCIETY_MEMBER_AMOUNT.get(seasonConfig);
        START_TIME = seasonConfig.SECRET_SOCIETY_START_TIME.get(seasonConfig);
        KILL_COUNT = seasonConfig.SECRET_SOCIETY_KILLS_REQUIRED.get(seasonConfig);
        PUNISHMENT_LIVES = seasonConfig.SECRET_SOCIETY_PUNISHMENT_LIVES.get(seasonConfig);
        SOUND_ONLY_MEMBERS = seasonConfig.SECRET_SOCIETY_SOUND_ONLY_MEMBERS.get(seasonConfig);

        FORCE_MEMBERS.clear();
        IGNORE_MEMBERS.clear();
        POSSIBLE_WORDS.clear();
        for (String name : seasonConfig.SECRET_SOCIETY_FORCE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) FORCE_MEMBERS.add(name.toLowerCase());
        }
        for (String name : seasonConfig.SECRET_SOCIETY_IGNORE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) IGNORE_MEMBERS.add(name.toLowerCase());
        }
        for (String name : seasonConfig.SECRET_SOCIETY_WORDS.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) POSSIBLE_WORDS.add(name);
        }
    }

    public void addSessionActions() {
        if (!SOCIETY_ENABLED) return;
        currentSession.addSessionAction(new SessionAction(OtherUtils.minutesToTicks(START_TIME), TextUtils.formatString("§7Begin Secret Society §f[{}]", OtherUtils.formatTime(OtherUtils.minutesToTicks(START_TIME))), "Begin Secret Society") {
            @Override
            public void trigger() {
                if (!SOCIETY_ENABLED) return;
                startSociety(null);
            }
        });
    }

    public void startSociety(String word) {
        if (!SOCIETY_ENABLED) return;
        if (server == null) return;
        if (word == null && !POSSIBLE_WORDS.isEmpty()) {
            word = POSSIBLE_WORDS.get(rnd.nextInt(POSSIBLE_WORDS.size()));
        }
        if (word != null) {
            this.secretWord = word;
        }

        societyStarted = true;
        societyEnded = false;
        SessionTranscript.societyStarted();
        ticks = 0;
        resetMembers();
        chooseMembers(PlayerUtils.getAllFunctioningPlayers());
    }

    public void forceEndSociety() {
        resetMembers();
        societyStarted = false;
        societyEnded = true;
    }

    @Nullable
    public SocietyMember getMember(ServerPlayerEntity player) {
        for (SocietyMember member : members) {
            if (member.uuid == player.getUuid()) {
                return member;
            }
        }
        return null;
    }
    
    public boolean isMember(ServerPlayerEntity player) {
        SocietyMember member = getMember(player);
        return member != null;
    }

    public void chooseMembers(List<ServerPlayerEntity> allowedPlayers) {
        if (!SOCIETY_ENABLED) return;
        Collections.shuffle(allowedPlayers);
        List<ServerPlayerEntity> memberPlayers = getRandomMembers(allowedPlayers);
        List<ServerPlayerEntity> nonMemberPlayers = new ArrayList<>();

        for (ServerPlayerEntity player : allowedPlayers) {
            if (memberPlayers.contains(player)) continue;
            nonMemberPlayers.add(player);
        }

        memberPlayers.forEach(this::addMember);
        SessionTranscript.societyMembersChosen(memberPlayers);

        if (!SOUND_ONLY_MEMBERS) {
            PlayerUtils.playSoundToPlayers(nonMemberPlayers, SoundEvent.of(Identifier.of("minecraft","secretlife_task")));
        }
        PlayerUtils.playSoundToPlayers(memberPlayers, SoundEvent.of(Identifier.of("minecraft","secretlife_task")));
        PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls"), 0, 30, 0);

        TaskScheduler.scheduleTask(15, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls."), 0, 30, 0);
        });
        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls.."), 0, 30, 0);
        });
        TaskScheduler.scheduleTask(45, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls..."), 0, 45, 30);
        });
        TaskScheduler.scheduleTask(115, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§cTake yourself somewhere quiet"), 20, 60, 20);
        });
    }

    public List<ServerPlayerEntity> getRandomMembers(List<ServerPlayerEntity> allowedPlayers) {
        List<ServerPlayerEntity> memberPlayers = new ArrayList<>();
        int remainingMembers = MEMBER_COUNT;
        for (ServerPlayerEntity player : allowedPlayers) {
            if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
                memberPlayers.add(player);
                remainingMembers--;
            }
        }

        for (ServerPlayerEntity player : allowedPlayers) {
            if (remainingMembers <= 0) break;
            if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (memberPlayers.contains(player)) continue;
            memberPlayers.add(player);
            remainingMembers--;
        }
        return memberPlayers;
    }

    public void tick() {
        if (!SOCIETY_ENABLED) return;
        if (!societyStarted) return;
        if (societyEnded) return;
        ticks++;
        if (ticks < 250) return;
        if (ticks % INITIATE_MESSAGE_DELAYS == 0) {
            for (SocietyMember member : members) {
                if (member.initiated) continue;
                ServerPlayerEntity player = member.getPlayer();
                if (player == null) continue;
                player.sendMessage(Text.of("§7When you are alone, type \"/initiate\""));
            }
        }
    }

    public void initiateMember(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        SocietyMember member = getMember(player);
        if (member == null) return;
        if (member.initiated) return;
        member.initiated = true;
        afterInitiate(player);
        SessionTranscript.societyMemberInitiated(player);
    }

    public void afterInitiate(ServerPlayerEntity player) {
        PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("secretlife_task")), 1, 1);

        int currentTime = 20;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.sendMessage(Text.of("§7You have been chosen to be part of the §csecret society§7."), false);
        });
        currentTime += 50;

        int otherMembers = members.size()-1;
        if (otherMembers >= 1) {
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(TextUtils.formatLoosely("§7There {} §c{}§7 other {}. Find them.", TextUtils.pluralize("is", "are", otherMembers), otherMembers, TextUtils.pluralize("member", otherMembers)), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(TextUtils.formatLoosely("§7Together, secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", KILL_COUNT, TextUtils.pluralize("player", KILL_COUNT)), false);
            });
            currentTime += 100;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(Text.of("§7Find the other members with the secret word:"), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(Text.of("§d\""+secretWord+"\""), false);
            });
        }
        else {
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(Text.of("§7You are alone."), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.sendMessage(TextUtils.formatLoosely("§7Secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", KILL_COUNT, TextUtils.pluralize("player", KILL_COUNT)), false);
            });
        }

        currentTime += 80;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.sendMessage(Text.of("§7Type \"/society success\" when you complete your goal."), false);
        });
        currentTime += 80;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.sendMessage(Text.of("§7Don't tell anyone else about the society."), false);
        });
        currentTime += 70;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.sendMessage(Text.of("§7If you fail..."), false);
        });
        currentTime += 70;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.sendMessage(getPunishmentText(), false);
        });
    }

    public Text getPunishmentText() {
        return TextUtils.formatLoosely("§7Type \"/society fail\", and you all lose §c{} {}§7.", Math.abs(PUNISHMENT_LIVES), TextUtils.pluralize("life", "lives", PUNISHMENT_LIVES));
    }

    public void removeMember(ServerPlayerEntity player) {
        members.removeIf(member -> member.uuid == player.getUuid());
    }

    public void addMember(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        members.add(new SocietyMember(player));
    }

    public void addMemberManually(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        player.sendMessage(Text.of("§c [NOTICE] You are now a Secret Society member!"));
        sendMessageToMembers(Text.of("A player has been added to the Secret Society."));
        addMember(player);
    }

    public void removeMemberManually(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
        removeMember(player);
        sendMessageToMembers(Text.of("A player has been removed from the Secret Society."));
    }

    public void sendMessageToMembers(Text message) {
        for (ServerPlayerEntity player : getMembers()) {
            player.sendMessage(message);
        }
    }

    public void resetMembers() {
        for (ServerPlayerEntity player : getMembers()) {
            player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
        }
        members.clear();
    }

    public List<ServerPlayerEntity> getMembers() {
        List<ServerPlayerEntity> memberPlayers = new ArrayList<>();
        for (SocietyMember member : members) {
            ServerPlayerEntity player = member.getPlayer();
            if (player == null) continue;
            memberPlayers.add(player);
        }
        return memberPlayers;
    }

    public void onDisabledSociety() {
        forceEndSociety();
    }
    
    public void sessionEnd() {
        if (!SOCIETY_ENABLED) return;
        if (societyStarted && !societyEnded) {
            TaskScheduler.scheduleTask(40, () -> {
                PlayerUtils.broadcastMessageToAdmins(Text.of("§c The Secret Society has not been ended by any Member!"));
                PlayerUtils.broadcastMessageToAdmins(Text.of("§c Run \"/society members list\" to see the Members."));
            });
        }
    }

    public void endSociety() {
        societyStarted = false;
        societyEnded = true;
        SessionTranscript.societyEnded();
        if (SOUND_ONLY_MEMBERS) {
            PlayerUtils.playSoundToPlayers(getMembers(), SoundEvent.of(Identifier.of("minecraft","secretlife_task")));
        }
        else {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvent.of(Identifier.of("minecraft","secretlife_task")));
        }
    }

    public void endSuccess() {
        endSociety();
        List<ServerPlayerEntity> memberPlayers = getMembers();
        PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§aThe Society is pleased"), 20, 30, 20);
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§aYou will not be punished"), 20, 30, 20);
        });
        TaskScheduler.scheduleTask(150, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§cYou are still sworn to secrecy"), 20, 30, 20);
        });
    }

    public void endFail() {
        endSociety();
        List<ServerPlayerEntity> memberPlayers = getMembers();
        PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§cThe Society is displeased"), 20, 30, 20);
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§cYou will be punished"), 20, 30, 20);
        });
        TaskScheduler.scheduleTask(110, () -> {
            for (ServerPlayerEntity member : memberPlayers) {
                punishPlayer(member);
            }
        });
        TaskScheduler.scheduleTask(150, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(), Text.of("§cYou are still sworn to secrecy"), 20, 30, 20);
        });
    }

    public void punishPlayer(ServerPlayerEntity member) {
        PlayerUtils.damage(member, member.getDamageSources().playerAttack(member), 0.001f);
        int punishmentLives = Math.abs(PUNISHMENT_LIVES);
        Integer currentLives = livesManager.getPlayerLives(member);
        if (currentLives != null) {
            punishmentLives = Math.min(Math.abs(currentLives-1), punishmentLives);
        }
        livesManager.addToPlayerLives(member, -punishmentLives);
    }
}
