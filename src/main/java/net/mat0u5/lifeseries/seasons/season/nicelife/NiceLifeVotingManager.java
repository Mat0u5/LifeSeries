package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerListReference;
import net.mat0u5.lifeseries.utils.player.PlayerReference;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import static net.mat0u5.lifeseries.LifeSeries.livesManager;

//? if <= 26.1 {
/*import net.minecraft.ChatFormatting;
 *///?} else {
import net.minecraft.world.scores.TeamColor;
//?}

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
    public static boolean PUBLIC_VOTING = false;

    public enum VoteType {
        NICE_LIST,
        NAUGHTY_LIST,
        NICE_LIST_LIFE,
        NONE
    }

    public static void createTeams() {
        //~ if >= 26.2 'ChatFormatting' -> 'TeamColor' {
        TeamUtils.createTeam(NAUGHTY_LIST_TEAM, NAUGHTY_LIST_TEAM_NAME, TeamColor.DARK_PURPLE);
        TeamUtils.createTeam(NICE_LIST_TEAM, NICE_LIST_TEAM_NAME, TeamColor.LIGHT_PURPLE);
        //~}
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
        if (((IPlayer) player).ls$isDead()) return;
        if (!allowedToVote.contains(player.getUUID())) return;
        if (playersVoted.contains(player.getUUID())) return;
        if (vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (((IPlayer) votedFor).ls$isDead()) return;
        if (voteType == VoteType.NAUGHTY_LIST && ((IPlayer) votedFor).ls$isOnSpecificLives(1, true) && !REDS_ON_NAUGHTY_LIST) return;
        if (voteType == VoteType.NICE_LIST && player == votedFor) return;


        playersVoted.add(player.getUUID());
        if (!votesByCount.containsKey(votedFor.getUUID())) {
            votesByCount.put(votedFor.getUUID(), 0);
        }
        votesByCount.put(votedFor.getUUID(), votesByCount.get(votedFor.getUUID())+1);
        votesByPerson.put(player.getUUID(), votedFor.getUUID());

        List<String> liveVoting = new ArrayList<>();
        if (PUBLIC_VOTING) {
            for (Map.Entry<UUID, Integer> entry : votesByCount.entrySet()) {
                ServerPlayer playerVoted = PlayerUtils.getPlayer(entry.getKey());
                if (playerVoted == null) continue;
                Integer votes = entry.getValue();
                if (votes == null) continue;
                liveVoting.add(playerVoted.getScoreboardName()+":"+votes);
            }
        }
        SimplePackets.NICELIFE_LIVE_VOTING.sendToAllClients(liveVoting);
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
            if (((IPlayer) player).ls$isDead()) continue;
            if (((IPlayer) player).ls$isOnLastLife(false) && !REDS_ON_NAUGHTY_LIST) continue;
            players.add(uuid);
        }
        clearNaughtyListMembers();
        if (players.isEmpty()) return;
        int delay = 80;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_vote_result"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NAUGHTYLIST_START_TITLE_PT1.get(), 15, 80, 20);
        });
        delay += 90;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_naughtylist"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NAUGHTYLIST_START_TITLE_PT2.get(), 15, 80, 20);
        });
        delay += 80;
        for (UUID uuid : players) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            PlayerReference ref = PlayerReference.of(player);
            TaskScheduler.scheduleTask(delay, () -> {
                ServerPlayer playerNew = ref.get();
                if (playerNew != null && !((IPlayer) playerNew).ls$isDead() && !(((IPlayer) playerNew).ls$isOnLastLife(false) && !REDS_ON_NAUGHTY_LIST)) {
                    SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_naughtylist"));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                    DatapackIntegration.EVENT_NAUGHTY_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", playerNew.getScoreboardName()));
                    playerNew.addTag("naughty_list");
                    naughtyListMembers.add(uuid);
                    currentSeason.reloadPlayerTeam(playerNew);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(playerNew), 15, 80, 20);
                }
            });
            delay += 55;
        }
        delay += 55;

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NAUGHTYLIST_START_INFO_PT1.get(players.size(), TextUtils.pluralize("person", "people", players.size())));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NAUGHTYLIST_START_INFO_PT2.get());
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NAUGHTYLIST_START_INFO_PT3.get());
        });
    }
    public static void announceNiceList() {
        List<UUID> players = getMostVotedForPlayers(NICE_LIST_COUNT);
        clearNiceListMembers();
        if (players.isEmpty()) return;
        int delay = 80;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_vote_result"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_START_TITLE_PT1.get(), 15, 80, 20);
        });
        delay += 90;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_start"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_START_TITLE_PT2.get(), 15, 80, 20);
        });
        delay += 80;
        for (UUID uuid : players) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (((IPlayer) player).ls$isDead()) continue;
            PlayerReference ref = PlayerReference.of(player);
            TaskScheduler.scheduleTask(delay, () -> {
                ServerPlayer playerNew = ref.get();
                if (playerNew != null) {
                    SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_person"));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                    DatapackIntegration.EVENT_NICE_LIST_ADD.trigger(new DatapackIntegration.Events.MacroEntry("Player", playerNew.getScoreboardName()));
                    playerNew.addTag("nice_list");
                    niceListMembers.add(uuid);
                    currentSeason.reloadPlayerTeam(playerNew);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(playerNew), 15, 80, 20);
                }
            });
            delay += 55;
        }
        delay += 55;

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NICELIST_START_INFO_PT1.get());
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NICELIST_START_INFO_PT2.get());
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(ModifiableText.NICELIFE_NICELIST_START_INFO_PT3.get());
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
        PlayerListReference ref = PlayerListReference.of(niceListPlayers);
        TaskScheduler.scheduleTask(delay, () -> {
            voteType = VoteType.NICE_LIST_LIFE;
            var newList = ref.get();
            PlayerUtils.playSoundToPlayers(newList, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(newList, ModifiableText.NICELIFE_NICELIST_START_INFO_PT4.get(TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"))));
        });
        delay += 110;
        TaskScheduler.scheduleTask(delay, () -> {
            voteType = VoteType.NICE_LIST_LIFE;
            var newList = ref.get();
            PlayerUtils.playSoundToPlayers(newList, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(newList, ModifiableText.NICELIFE_NICELIST_START_INFO_PT5.get());
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
        ((IPlayer) player).ls$message(ModifiableText.NICELIFE_NICELIST_START_INFO_PT4.get(TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"))));
        PlayerReference ref = PlayerReference.of(player);
        TaskScheduler.scheduleTask(110, () -> {
            ServerPlayer playerNew = ref.get();
            if (playerNew != null) {
                PlayerUtils.playSoundToPlayer(playerNew, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
                ((IPlayer) playerNew).ls$message(ModifiableText.NICELIFE_NICELIST_START_INFO_PT5.get());
            }
        });
    }

    public static void manuallyRemoveNiceListMember(ServerPlayer player) {
        player.removeTag("nice_list");
        if (!niceListMembers.contains(player.getUUID())) return;
        niceListMembers.remove(player.getUUID());
        allowedToVote.remove(player.getUUID());
        currentSeason.reloadPlayerTeam(player);
        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        ((IPlayer) player).ls$message(ModifiableText.NICELIFE_NICELIST_REMOVED.get());
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
        SoundEvent voteSound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_vote_result"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), voteSound, 1f, 1);
        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NAUGHTYLIST_END_TITLE.get(), 15, 80, 20);

        int delay = 95;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_3"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_3.get(), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_2"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_2.get(), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_1"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_1.get(), 15, 25, 15);
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
        SoundEvent voteSound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_end"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), voteSound, 1f, 1);
        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_VOTE_END_TITLE.get(), 15, 80, 20);

        int delay = 95;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_3"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_3.get(), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_2"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_2.get(), 15, 25, 15);
        });
        delay += 40;
        TaskScheduler.scheduleTask(delay, () -> {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_nicelist_countdown_1"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_VOTE_COUNTDOWN_1.get(), 15, 25, 15);
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
            if (((IPlayer) player).ls$isDead()) continue;
            availableVotes++;
        }
        for (Map.Entry<UUID, UUID> entry : votesByPerson.entrySet()) {
            UUID votedForUUID = entry.getValue();
            ServerPlayer votingPlayer = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer votedFor = PlayerUtils.getPlayer(votedForUUID);
            if (votingPlayer == null || votedFor == null) continue;
            if (((IPlayer) votingPlayer).ls$isDead() || ((IPlayer) votedFor).ls$isDead()) continue;
            if (!niceListMembers.contains(votingPlayer.getUUID()) || niceListMembers.contains(votedFor.getUUID())) continue;
            validVotes++;
            if (!reloadedVotesByCount.containsKey(votedForUUID)) {
                reloadedVotesByCount.put(votedForUUID, 0);
            }
            reloadedVotesByCount.put(votedForUUID, reloadedVotesByCount.get(votedForUUID)+1);
        }




        if (reloadedVotesByCount.isEmpty() || availableVotes == 0 || validVotes == 0) {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_naughtylist"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_VOTE_ERROR_INSUFFICIENT.get(), 15, 80, 20);
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
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_vote_result"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_VOTE_RESULT.get(), 15, 80, 20);

                PlayerReference ref = PlayerReference.of(winner);
                TaskScheduler.scheduleTask(85, ()-> {
                    ServerPlayer winnerNew = ref.get();
                    if (winnerNew != null) {
                        if (((IPlayer) winnerNew).ls$isAlive()) {
                            ((IPlayer) winnerNew).ls$addLife();
                        }
                        currentSeason.reloadPlayerTeam(winnerNew);
                        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.FIREWORK_ROCKET_LAUNCH, 1f, 1);
                        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(winnerNew), 15, 80, 20);
                    }
                });
            }
            else {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.mod("nicelife_naughtylist"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                if (validVotes > requiredVotes) {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_VOTE_ERROR_AGREEMENT.get(), 15, 80, 20);
                }
                else {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.NICELIFE_NICELIST_VOTE_ERROR_INSUFFICIENT.get(), 15, 80, 20);
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

        NetworkHandlerServer.sendVoteScreenPacket(player, ModifiableText.NICELIFE_NICELIST_VOTE_TITLE.getString(), false, true, false, availableForVoting);
        return true;
    }

    public static void handleNiceListLifeVote(ServerPlayer player, String vote) {
        if (voteType != VoteType.NICE_LIST_LIFE) return;

        if (((IPlayer) player).ls$isDead()) return;
        if (!niceListMembers.contains(player.getUUID())) return;
        if (vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (((IPlayer) votedFor).ls$isDead()) return;
        if (niceListMembers.contains(votedFor.getUUID())) return;

        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        ((IPlayer) player).ls$message(ModifiableText.NICELIFE_NICELIST_VOTE.get(PlayerUtils.getPlayerNameWithIcon(votedFor)));
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
            Component message = ModifiableText.NICELIFE_NICELIST_VOTE_REMINDER.get(TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote")));
            PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(niceListPlayers, message);
            PlayerListReference ref = PlayerListReference.of(niceListPlayers);
            TaskScheduler.scheduleTask(Time.seconds(30), () -> {
                var niceListPlayersNew = ref.get();
                PlayerUtils.playSoundToPlayers(niceListPlayersNew, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
                PlayerUtils.broadcastMessage(niceListPlayersNew, message);
            });
        }
    }
}
