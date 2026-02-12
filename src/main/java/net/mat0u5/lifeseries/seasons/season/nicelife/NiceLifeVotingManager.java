package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

import static net.mat0u5.lifeseries.Main.currentSeason;
import static net.mat0u5.lifeseries.Main.livesManager;

public class NiceLifeVotingManager {
    public static Random rnd = new Random();
    public static VoteType voteType = VoteType.NAUGHTY_LIST;
    public static double NICE_LIST_CHANCE = 0.5;
    public static Time VOTING_TIME = Time.seconds(60);
    public static boolean REDS_ON_NAUGHTY_LIST = false;
    public static List<UUID> allowedToVote = new ArrayList<>();
    public static List<UUID> playersVoted = new ArrayList<>();
    public static Map<UUID, Integer> votesByCount = new HashMap<>();
    public static Map<UUID, UUID> votesByPerson = new HashMap<>();
    public static int NICE_LIST_COUNT = 3;
    public static int NAUGHTY_LIST_COUNT = 3;
    public static List<UUID> niceListMembers = new ArrayList<>();
    public static List<UUID> naughtyListMembers = new ArrayList<>();
    public static String NAUGHTY_LIST_TEAM = "naughty_list";
    public static String NAUGHTY_LIST_TEAM_NAME = "Naughty List";
    public static String NICE_LIST_TEAM = "nice_list";
    public static String NICE_LIST_TEAM_NAME = "Nice List";
    public static Optional<VoteType> forcedTriviaVote = Optional.empty();

    public enum VoteType {
        NICE_LIST,
        NAUGHTY_LIST,
        NICE_LIST_LIFE,
        NONE
    }

    public static void createTeams() {
        TeamUtils.createTeam(NAUGHTY_LIST_TEAM, NAUGHTY_LIST_TEAM_NAME, ChatFormatting.DARK_PURPLE);
        TeamUtils.createTeam(NICE_LIST_TEAM, NICE_LIST_TEAM_NAME, ChatFormatting.LIGHT_PURPLE);
    }

    public static void reset() {
        allowedToVote.clear();
        playersVoted.clear();
        votesByCount.clear();
        votesByPerson.clear();
        voteType = VoteType.NONE;
    }

    public static void chooseVote() {
        if (forcedTriviaVote.isPresent()) {
            voteType = forcedTriviaVote.get();
            forcedTriviaVote = Optional.empty();
            return;
        }

        voteType = VoteType.NICE_LIST;
        if (rnd.nextDouble() > NICE_LIST_CHANCE) {
            voteType = VoteType.NONE;
            if (livesManager.anyPlayersAtLeastLives(2) || REDS_ON_NAUGHTY_LIST) {
                voteType = VoteType.NAUGHTY_LIST;
            }
        }
    }

    public static void handleVote(ServerPlayer player, String vote) {
        if (NiceLifeTriviaManager.triviaInProgress) {
            if (voteType == VoteType.NICE_LIST || voteType == VoteType.NAUGHTY_LIST) {
                handleTriviaVote(player, vote);
            }
        }
        else {
            if (voteType == VoteType.NICE_LIST_LIFE) {
                handleNiceListLifeVote(player, vote);
            }
        }
    }

    public static void handleTriviaVote(ServerPlayer player, String vote) {
        if (player == null) return;
        if (voteType == VoteType.NONE) return;

        TriviaBot bot = NiceLifeTriviaManager.bots.get(player.getUUID());
        if (bot == null) return;
        if (!bot.isAlive()) return;
        if (!(bot.triviaHandler instanceof NiceLifeTriviaHandler triviaHandler)) return;
        if (triviaHandler.currentState != NiceLifeTriviaHandler.BotState.VOTING) return;
        triviaHandler.changeStateTo(NiceLifeTriviaHandler.BotState.LEAVING);


        if (!player.isSleeping()) return;
        if (player.ls$isDead()) return;
        if (!allowedToVote.contains(player.getUUID())) return;
        if (playersVoted.contains(player.getUUID())) return;
        if (vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (votedFor.ls$isDead()) return;
        if (voteType == VoteType.NAUGHTY_LIST && votedFor.ls$isOnSpecificLives(1, true) && !REDS_ON_NAUGHTY_LIST) return;
        if (voteType == VoteType.NICE_LIST && player == votedFor) return;


        playersVoted.add(player.getUUID());
        if (!votesByCount.containsKey(votedFor.getUUID())) {
            votesByCount.put(votedFor.getUUID(), 0);
        }
        votesByCount.put(votedFor.getUUID(), votesByCount.get(votedFor.getUUID())+1);
        votesByPerson.put(player.getUUID(), votedFor.getUUID());
    }

    public static List<UUID> getMostVotedForPlayers(int count) {
        Map<Integer, List<UUID>> voteGroups = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : votesByCount.entrySet()) {
            voteGroups.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        List<Integer> sortedVoteCounts = new ArrayList<>(voteGroups.keySet());
        sortedVoteCounts.sort(Collections.reverseOrder());

        List<UUID> result = new ArrayList<>();
        Random random = new Random();

        for (int voteCount : sortedVoteCounts) {
            List<UUID> group = new ArrayList<>(voteGroups.get(voteCount));

            if (result.size() + group.size() <= count) {
                result.addAll(group);
            } else {
                Collections.shuffle(group, random);
                int remaining = count - result.size();
                result.addAll(group.subList(0, remaining));
                break;
            }

            if (result.size() >= count) break;
        }

        return result;
    }

    public static void endTriviaVoting() {
        if (voteType == VoteType.NAUGHTY_LIST) {
            announceNaughtyList();
        }
        if (voteType == VoteType.NICE_LIST) {
            announceNiceList();
        }
        currentSeason.reloadAllPlayerTeams();
        reset();
    }

    public static void endListsIfNecessary() {
        if (!naughtyListMembers.isEmpty()) {
            endNaughtyList();
        }
        if (!niceListMembers.isEmpty()) {
            endNiceList();
        }
    }

    public static void announceNaughtyList() {
        List<UUID> playersRaw = getMostVotedForPlayers(NAUGHTY_LIST_COUNT);
        List<UUID> players = new ArrayList<>();
        for (UUID uuid : playersRaw) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.ls$isDead()) continue;
            if (player.ls$isOnLastLife(false) && !REDS_ON_NAUGHTY_LIST) continue;
            players.add(uuid);
        }
        clearNaughtyListMembers();
        if (players.isEmpty()) return;
        int delay = 80;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_vote_result"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cThese players are on..."), 15, 80, 20);
        });
        delay += 90;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cTHE NAUGHTY LIST"), 15, 80, 20);
        });
        delay += 80;
        for (UUID uuid : players) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            TaskScheduler.scheduleTask(delay, () -> {
                if (player != null && !player.ls$isDead() && !(player.ls$isOnLastLife(false) && !REDS_ON_NAUGHTY_LIST)) {
                    SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                    DatapackIntegration.EVENT_NAUGHTY_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
                    player.addTag("naughty_list");
                    naughtyListMembers.add(uuid);
                    currentSeason.reloadPlayerTeam(player);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(player), 15, 80, 20);
                }
            });
            delay += 55;
        }
        delay += 55;

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(TextUtils.formatLoosely("\n §6[§e!§6]§7 You have voted for {} {} to be on the §cNAUGHTY LIST§7.\n", players.size(), TextUtils.pluralize("person", "people", players.size())));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(Component.literal(" §6[§e!§6]§7 People on the §cnaughty list§7 have a purple name and can be killed.\n"));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(Component.literal(" §6[§e!§6]§7 They return to their previous colour at sunset. They can defend themselves.\n"));
        });
    }
    public static void announceNiceList() {
        List<UUID> players = getMostVotedForPlayers(NICE_LIST_COUNT);
        clearNiceListMembers();
        if (players.isEmpty()) return;
        int delay = 80;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_vote_result"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§aThese players are on..."), 15, 80, 20);
        });
        delay += 90;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_start"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§aTHE NICE LIST"), 15, 80, 20);
        });
        delay += 80;
        for (UUID uuid : players) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.ls$isDead()) continue;
            TaskScheduler.scheduleTask(delay, () -> {
                if (player != null) {
                    SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_person"));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                    DatapackIntegration.EVENT_NICE_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
                    player.addTag("nice_list");
                    niceListMembers.add(uuid);
                    currentSeason.reloadPlayerTeam(player);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(player), 15, 80, 20);
                }
            });
            delay += 55;
        }
        delay += 55;

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(Component.literal("\n §6[§e!§6]§7 At sunset, players on the nice list will vote to give a §2non-pink§7 name a life.\n"));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(Component.literal(" §6[§e!§6]§7 The majority of the §dpinks§7 must vote for the same player for the life to be given.\n"));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(Component.literal(" §6[§e!§6]§7 Pink names are not allowed to be targeted by any other players, including §creds§7.\n"));
        });
        List<ServerPlayer> niceListPlayers = new ArrayList<>();
        for (UUID uuid : players) {
            allowedToVote.add(uuid);
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player != null) {
                niceListPlayers.add(player);
            }
        }
        delay += 150;
        TaskScheduler.scheduleTask(delay, () -> {
            voteType = VoteType.NICE_LIST_LIFE;
            PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(niceListPlayers, TextUtils.format(" §6[§e!§6]§7 You are on the nice list. Type {}§7 to choose who you would like to give a life to.\n", TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"))));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            voteType = VoteType.NICE_LIST_LIFE;
            PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(niceListPlayers, Component.literal(" §6[§e!§6]§7 You can change your vote at anytime, but the results will be locked in at sunset.\n"));
        });
    }

    public static void manuallyAddNiceListMember(ServerPlayer player) {
        player.addTag("nice_list");
        if (niceListMembers.contains(player.getUUID())) return;
        DatapackIntegration.EVENT_NICE_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        niceListMembers.add(player.getUUID());
        allowedToVote.add(player.getUUID());
        currentSeason.reloadPlayerTeam(player);
        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        player.sendSystemMessage(TextUtils.format(" §6[§e!§6]§7 You are on the nice list. Type {}§7 to choose who you would like to give a life to.\n", TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"))));
        TaskScheduler.scheduleTask(110, () -> {
            PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            player.sendSystemMessage(Component.literal(" §6[§e!§6]§7 You can change your vote at anytime, but the results will be locked in at sunset.\n"));
        });
    }

    public static void manuallyRemoveNiceListMember(ServerPlayer player) {
        player.removeTag("nice_list");
        if (!niceListMembers.contains(player.getUUID())) return;
        niceListMembers.remove(player.getUUID());
        allowedToVote.remove(player.getUUID());
        currentSeason.reloadPlayerTeam(player);
        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        player.sendSystemMessage(Component.literal(" §6[§e!§6]§7 You are no longer on the Nice List"));
    }

    public static void manuallyAddNaughtyListMember(ServerPlayer player) {
        player.addTag("naughty_list");
        if (naughtyListMembers.contains(player.getUUID())) return;
        DatapackIntegration.EVENT_NAUGHTY_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        naughtyListMembers.add(player.getUUID());
        currentSeason.reloadPlayerTeam(player);
    }

    public static void manuallyRemoveNaughtyListMember(ServerPlayer player) {
        player.removeTag("naughty_list");
        if (!naughtyListMembers.contains(player.getUUID())) return;
        naughtyListMembers.remove(player.getUUID());
        currentSeason.reloadPlayerTeam(player);
    }

    public static void endNaughtyList() {
        DatapackIntegration.EVENT_NAUGHTY_LIST_END.trigger();
        SoundEvent voteSound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_vote_result"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), voteSound, 1f, 1);
        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cPlayers return to normal in..."), 15, 80, 20);

        int delay = 95;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_3"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§23.."), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_2"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§e2.."), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_1"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§c1.."), 15, 25, 15);
        });
        delay += 55;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.CHICKEN_EGG, 1f, 1);
            clearNaughtyListMembers();
            currentSeason.reloadAllPlayerTeams();
        });
        NiceLife.postponeTriviaStart(Time.ticks(delay+20));
    }

    public static void endNiceList() {
        DatapackIntegration.EVENT_NICE_LIST_END.trigger();
        SoundEvent voteSound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_end"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), voteSound, 1f, 1);
        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cThe nice vote will end in..."), 15, 80, 20);

        int delay = 95;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_3"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§23.."), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_2"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§e2.."), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_1"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§c1.."), 15, 25, 15);
        });
        delay += 55;
        TaskScheduler.scheduleTask(delay, () -> {
            actuallyEndNiceList();
        });
        NiceLife.postponeTriviaStart(Time.ticks(delay+85));
    }

    public static void actuallyEndNiceList() {
        Map<UUID, Integer> reloadedVotesByCount = new HashMap<>();
        int availableVotes = 0;
        int validVotes = 0;
        for (UUID uuid : niceListMembers) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.ls$isDead()) continue;
            availableVotes++;
        }
        for (Map.Entry<UUID, UUID> entry : votesByPerson.entrySet()) {
            UUID votedForUUID = entry.getValue();
            ServerPlayer votingPlayer = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer votedFor = PlayerUtils.getPlayer(votedForUUID);
            if (votingPlayer == null || votedFor == null) continue;
            if (votingPlayer.ls$isDead() || votedFor.ls$isDead()) continue;
            if (!niceListMembers.contains(votingPlayer.getUUID()) || niceListMembers.contains(votedFor.getUUID())) continue;
            validVotes++;
            if (!reloadedVotesByCount.containsKey(votedForUUID)) {
                reloadedVotesByCount.put(votedForUUID, 0);
            }
            reloadedVotesByCount.put(votedForUUID, reloadedVotesByCount.get(votedForUUID)+1);
        }




        if (reloadedVotesByCount.isEmpty() || availableVotes == 0 || validVotes == 0) {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cInsufficient votes"), 15, 80, 20);
        }
        else {
            UUID mostVotedFor = reloadedVotesByCount.keySet().stream().iterator().next();
            int mostVotes = 0;
            for (Map.Entry<UUID, Integer> entry : reloadedVotesByCount.entrySet()) {
                UUID vote = entry.getKey();
                int votes = entry.getValue();
                if (votes > mostVotes) {
                    mostVotedFor = vote;
                    mostVotes = votes;
                }
            }
            double requiredVotes = (double)availableVotes/2.0;
            ServerPlayer winner = PlayerUtils.getPlayer(mostVotedFor);

            if (mostVotes > requiredVotes && winner != null) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_vote_result"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§2The winner is..."), 15, 80, 20);

                TaskScheduler.scheduleTask(85, ()-> {
                    if (winner.ls$isAlive()) {
                        winner.ls$addLife();
                    }
                    currentSeason.reloadPlayerTeam(winner);
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.FIREWORK_ROCKET_LAUNCH, 1f, 1);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(winner), 15, 80, 20);
                });
            }
            else {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                if (validVotes > requiredVotes) {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cNo agreement reached"), 15, 80, 20);
                }
                else {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal("§cInsufficient votes"), 15, 80, 20);
                }
            }
        }
        clearNiceListMembers();
        currentSeason.reloadAllPlayerTeams();
        reset();
    }

    public static void clearNaughtyListMembers() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.removeTag("naughty_list");
        }
        naughtyListMembers.clear();
    }

    public static void clearNiceListMembers() {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.removeTag("nice_list");
        }
        niceListMembers.clear();
    }

    public static boolean openNiceListLifeVote(ServerPlayer player) {
        List<String> availableForVoting = new ArrayList<>();
        for (ServerPlayer availableVotePlayer : livesManager.getAlivePlayers()) {
            if (niceListMembers.contains(availableVotePlayer.getUUID())) continue;
            availableForVoting.add(availableVotePlayer.getScoreboardName());
        }
        if (availableForVoting.isEmpty()) {
            return false;
        }

        NetworkHandlerServer.sendVoteScreenPacket(player, "Vote for who should get a life", false, true, false, availableForVoting);
        return true;
    }

    public static void handleNiceListLifeVote(ServerPlayer player, String vote) {
        if (voteType != VoteType.NICE_LIST_LIFE) return;

        if (player.ls$isDead()) return;
        if (!niceListMembers.contains(player.getUUID())) return;
        if (vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (votedFor.ls$isDead()) return;
        if (niceListMembers.contains(votedFor.getUUID())) return;

        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        player.sendSystemMessage(TextUtils.format("\n §6[§e!§6]§7 You voted for {}§7.\n", PlayerUtils.getPlayerNameWithIcon(votedFor)), false);
        votesByPerson.put(player.getUUID(), votedFor.getUUID());
    }

    public static void warnNiceListMembers() {
        List<ServerPlayer> niceListPlayers = new ArrayList<>();
        for (UUID uuid : NiceLifeVotingManager.niceListMembers) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player != null) {
                niceListPlayers.add(player);
            }
        }
        if (!niceListPlayers.isEmpty()) {
            Component message = TextUtils.format("\n§7Don't forget to {}§7!\n", TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote")));
            PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(niceListPlayers, message);
            TaskScheduler.scheduleTask(Time.seconds(30), () -> {
                PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
                PlayerUtils.broadcastMessage(niceListPlayers, message);
            });
        }
    }
}
