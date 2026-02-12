package net.mat0u5.lifeseries.seasons.secretsociety;

import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public static final Time INITIATE_MESSAGE_DELAYS = Time.seconds(15);
    public List<SocietyMember> members = new ArrayList<>();
    public boolean societyStarted = false;
    public boolean societyEnded = false;
    public Time timer = Time.zero();
    public String secretWord = "";
    public Random rnd = new Random();

    public void onReload() {
        SOCIETY_ENABLED = seasonConfig.SECRET_SOCIETY.get();
        if (!SOCIETY_ENABLED) {
            onDisabledSociety();
        }

        MEMBER_COUNT = seasonConfig.SECRET_SOCIETY_MEMBER_AMOUNT.get();
        START_TIME = seasonConfig.SECRET_SOCIETY_START_TIME.get();
        KILL_COUNT = seasonConfig.SECRET_SOCIETY_KILLS_REQUIRED.get();
        PUNISHMENT_LIVES = seasonConfig.SECRET_SOCIETY_PUNISHMENT_LIVES.get();
        SOUND_ONLY_MEMBERS = seasonConfig.SECRET_SOCIETY_SOUND_ONLY_MEMBERS.get();

        FORCE_MEMBERS.clear();
        IGNORE_MEMBERS.clear();
        POSSIBLE_WORDS.clear();
        for (String name : seasonConfig.SECRET_SOCIETY_FORCE.get().replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) FORCE_MEMBERS.add(name.toLowerCase(Locale.ROOT));
        }
        for (String name : seasonConfig.SECRET_SOCIETY_IGNORE.get().replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) IGNORE_MEMBERS.add(name.toLowerCase(Locale.ROOT));
        }
        for (String name : seasonConfig.SECRET_SOCIETY_WORDS.get().replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) POSSIBLE_WORDS.add(name);
        }
    }

    public void addSessionActions() {
        if (!SOCIETY_ENABLED) return;
        currentSession.addSessionAction(new SessionAction(Time.minutes(START_TIME), "Begin Secret Society") {
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
        timer = Time.zero();
        resetMembers();
        chooseMembers(PlayerUtils.getAllFunctioningPlayers());
    }

    public void forceEndSociety() {
        resetMembers();
        societyStarted = false;
        societyEnded = true;
    }

    @Nullable
    public SocietyMember getMember(ServerPlayer player) {
        for (SocietyMember member : members) {
            if (member.uuid == player.getUUID()) {
                return member;
            }
        }
        return null;
    }
    
    public boolean isMember(ServerPlayer player) {
        SocietyMember member = getMember(player);
        return member != null;
    }

    public void chooseMembers(List<ServerPlayer> allowedPlayers) {
        if (!SOCIETY_ENABLED) return;
        Collections.shuffle(allowedPlayers);
        List<ServerPlayer> memberPlayers = getRandomMembers(allowedPlayers);
        List<ServerPlayer> nonMemberPlayers = new ArrayList<>();

        for (ServerPlayer player : allowedPlayers) {
            if (memberPlayers.contains(player)) continue;
            nonMemberPlayers.add(player);
        }

        memberPlayers.forEach(this::addMember);
        SessionTranscript.societyMembersChosen(memberPlayers);

        if (!SOUND_ONLY_MEMBERS) {
            PlayerUtils.playSoundToPlayers(nonMemberPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")));
        }
        PlayerUtils.playSoundToPlayers(memberPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")));
        PlayerUtils.sendTitleToPlayers(memberPlayers, Component.nullToEmpty("§cThe Society calls"), 0, 30, 0);

        TaskScheduler.scheduleTask(15, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Component.nullToEmpty("§cThe Society calls."), 0, 30, 0);
        });
        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Component.nullToEmpty("§cThe Society calls.."), 0, 30, 0);
        });
        TaskScheduler.scheduleTask(45, () -> {
            PlayerUtils.sendTitleToPlayers(memberPlayers, Component.nullToEmpty("§cThe Society calls..."), 0, 45, 30);
        });
        TaskScheduler.scheduleTask(115, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§cTake yourself somewhere quiet"), 20, 60, 20);
        });
    }

    public List<ServerPlayer> getRandomMembers(List<ServerPlayer> allowedPlayers) {
        List<ServerPlayer> memberPlayers = new ArrayList<>();
        int remainingMembers = MEMBER_COUNT;
        for (ServerPlayer player : allowedPlayers) {
            if (IGNORE_MEMBERS.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (FORCE_MEMBERS.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) {
                memberPlayers.add(player);
                remainingMembers--;
            }
        }

        for (ServerPlayer player : allowedPlayers) {
            if (remainingMembers <= 0) break;
            if (IGNORE_MEMBERS.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (FORCE_MEMBERS.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
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
        timer.tick();
        if (timer.isSmaller(Time.ticks(250))) return;
        if (timer.isMultipleOf(INITIATE_MESSAGE_DELAYS)) {
            for (SocietyMember member : members) {
                if (member.initiated) continue;
                ServerPlayer player = member.getPlayer();
                if (player == null) continue;
                player.sendSystemMessage(Component.nullToEmpty("§7When you are alone, type \"/initiate\""));
            }
        }
    }

    public void initiateMember(ServerPlayer player) {
        if (!SOCIETY_ENABLED) return;
        SocietyMember member = getMember(player);
        if (member == null) return;
        if (member.initiated) return;
        member.initiated = true;
        afterInitiate(player);
        SessionTranscript.societyMemberInitiated(player);
    }

    public void afterInitiate(ServerPlayer player) {
        PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.parse("secretlife_task")), 1, 1);

        int currentTime = 20;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.displayClientMessage(Component.nullToEmpty("§7You have been chosen to be part of the §csecret society§7."), false);
        });
        currentTime += 50;

        int otherMembers = members.size()-1;
        if (otherMembers >= 1) {
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(TextUtils.formatLoosely("§7There {} §c{}§7 other {}. Find them.", TextUtils.pluralize("is", "are", otherMembers), otherMembers, TextUtils.pluralize("member", otherMembers)), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(TextUtils.formatLoosely("§7Together, secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", KILL_COUNT, TextUtils.pluralize("player", KILL_COUNT)), false);
            });
            currentTime += 100;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(Component.nullToEmpty("§7Find the other members with the secret word:"), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(Component.nullToEmpty("§d\""+secretWord+"\""), false);
            });
        }
        else {
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(Component.nullToEmpty("§7You are alone."), false);
            });
            currentTime += 80;
            TaskScheduler.scheduleTask(currentTime, () -> {
                player.displayClientMessage(TextUtils.formatLoosely("§7Secretly kill §c{}§7 other {} by §cnon-pvp§7 means.", KILL_COUNT, TextUtils.pluralize("player", KILL_COUNT)), false);
            });
        }

        currentTime += 80;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.displayClientMessage(Component.nullToEmpty("§7Type \"/society success\" when you complete your goal."), false);
        });
        currentTime += 80;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.displayClientMessage(Component.nullToEmpty("§7Don't tell anyone else about the society."), false);
        });
        currentTime += 70;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.displayClientMessage(Component.nullToEmpty("§7If you fail..."), false);
        });
        currentTime += 70;
        TaskScheduler.scheduleTask(currentTime, () -> {
            player.displayClientMessage(getPunishmentText(), false);
        });
    }

    public Component getPunishmentText() {
        return TextUtils.formatLoosely("§7Type \"/society fail\", and you all lose §c{} {}§7.", Math.abs(PUNISHMENT_LIVES), TextUtils.pluralize("life", "lives", PUNISHMENT_LIVES));
    }

    public void removeMember(ServerPlayer player) {
        members.removeIf(member -> member.uuid == player.getUUID());
        player.removeTag("society_member");
    }

    public void addMember(ServerPlayer player) {
        if (!SOCIETY_ENABLED) return;
        members.add(new SocietyMember(player));
        player.addTag("society_member");
        DatapackIntegration.EVENT_SOCIETY_MEMBER_ADDED.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
    }

    public void addMemberManually(ServerPlayer player) {
        if (!SOCIETY_ENABLED) return;
        player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are now a Secret Society member!"));
        sendMessageToMembers(Component.nullToEmpty("A player has been added to the Secret Society."));
        addMember(player);
    }

    public void removeMemberManually(ServerPlayer player) {
        if (!SOCIETY_ENABLED) return;
        player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are no longer a Secret Society member!"));
        removeMember(player);
        sendMessageToMembers(Component.nullToEmpty("A player has been removed from the Secret Society."));
    }

    public void sendMessageToMembers(Component message) {
        for (ServerPlayer player : getMembers()) {
            player.sendSystemMessage(message);
        }
    }

    public void resetMembers() {
        for (ServerPlayer player : getMembers()) {
            player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are no longer a Secret Society member!"));
            player.removeTag("society_member");
        }
        members.clear();
    }

    public List<ServerPlayer> getMembers() {
        List<ServerPlayer> memberPlayers = new ArrayList<>();
        for (SocietyMember member : members) {
            ServerPlayer player = member.getPlayer();
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
            TaskScheduler.scheduleTask(Time.seconds(2), () -> {
                PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§c The Secret Society has not been ended by any Member!"));
                PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§c Run \"/society members list\" to see the Members."));
            });
        }
    }

    public void endSociety() {
        societyStarted = false;
        societyEnded = true;
        SessionTranscript.societyEnded();
        if (SOUND_ONLY_MEMBERS) {
            PlayerUtils.playSoundToPlayers(getMembers(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")));
        }
        else {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("secretlife_task")));
        }
    }

    public void endSuccess() {
        endSociety();
        List<ServerPlayer> memberPlayers = getMembers();
        PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§aThe Society is pleased"), 20, 30, 20);
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§aYou will not be punished"), 20, 30, 20);
            for (ServerPlayer member : memberPlayers) {
                DatapackIntegration.EVENT_SOCIETY_SUCCESS_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", member.getScoreboardName()));
            }
        });
        TaskScheduler.scheduleTask(150, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§cYou are still sworn to secrecy"), 20, 30, 20);
        });
    }

    public void endFail() {
        endSociety();
        List<ServerPlayer> memberPlayers = getMembers();
        PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§cThe Society is displeased"), 20, 30, 20);
        TaskScheduler.scheduleTask(75, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§cYou will be punished"), 20, 30, 20);
        });
        TaskScheduler.scheduleTask(110, () -> {
            for (ServerPlayer member : memberPlayers) {
                punishPlayer(member);
            }
        });
        TaskScheduler.scheduleTask(150, () -> {
            PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Component.empty(), Component.nullToEmpty("§cYou are still sworn to secrecy"), 20, 30, 20);
        });
    }

    public void punishPlayer(ServerPlayer member) {
        member.ls$hurt(member.damageSources().playerAttack(member), 0.001f);
        DatapackIntegration.EVENT_SOCIETY_FAIL_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", member.getScoreboardName()));
        if (DatapackIntegration.EVENT_SOCIETY_FAIL_REWARD.isCanceled()) return;
        int punishmentLives = Math.abs(PUNISHMENT_LIVES);
        Integer currentLives = member.ls$getLives();
        if (currentLives != null) {
            punishmentLives = Math.min(Math.abs(currentLives-1), punishmentLives);
        }
        member.ls$addLives(-punishmentLives);
    }
}
