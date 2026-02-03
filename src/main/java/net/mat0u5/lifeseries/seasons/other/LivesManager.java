package net.mat0u5.lifeseries.seasons.other;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.mat0u5.lifeseries.utils.world.AnimationUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

//? if > 1.20.2
import net.minecraft.world.scores.PlayerScoreEntry;

public class LivesManager {
    public static final String SCOREBOARD_NAME = "Lives";
    public boolean FINAL_DEATH_LIGHTNING = true;
    public SoundEvent FINAL_DEATH_SOUND = SoundEvents.LIGHTNING_BOLT_THUNDER;
    public boolean SHOW_DEATH_TITLE = false;
    public boolean ONLY_TAKE_LIVES_IN_SESSION = false;
    public boolean SEE_FRIENDLY_INVISIBLE_PLAYERS = false;
    public static int MAX_TAB_NUMBER = 4;
    public boolean LIVES_SYSTEM_DISABLED = false;
    public boolean ROLL_LIVES = false;
    public int ROLL_MIN_LIVES = 2;
    public int ROLL_MAX_LIVES = 6;

    public boolean assignedLives = false;
    public SessionAction actionChooseLives = new SessionAction(Time.minutes(1),"Assign lives if necessary") {
        @Override
        public void trigger() {
            assignRandomLivesToUnassignedPlayers();
        }
    };
    Random rnd = new Random();

    public void reload() {
        SHOW_DEATH_TITLE = seasonConfig.FINAL_DEATH_TITLE_SHOW.get(seasonConfig);
        FINAL_DEATH_LIGHTNING = seasonConfig.FINAL_DEATH_LIGHTNING.get(seasonConfig);
        FINAL_DEATH_SOUND = SoundEvent.createVariableRangeEvent(IdentifierHelper.parse(seasonConfig.FINAL_DEATH_SOUND.get(seasonConfig)));
        ONLY_TAKE_LIVES_IN_SESSION = seasonConfig.ONLY_TAKE_LIVES_IN_SESSION.get(seasonConfig);
        SEE_FRIENDLY_INVISIBLE_PLAYERS = seasonConfig.SEE_FRIENDLY_INVISIBLE_PLAYERS.get(seasonConfig);
        LIVES_SYSTEM_DISABLED = seasonConfig.LIVES_SYSTEM_DISABLED.get(seasonConfig);
        updateTeams();

        ROLL_LIVES = seasonConfig.LIVES_RANDOMIZE.get(seasonConfig);
        int minLivesConfig = seasonConfig.LIVES_RANDOMIZE_MIN.get(seasonConfig);
        int maxLivesConfig = seasonConfig.LIVES_RANDOMIZE_MAX.get(seasonConfig);
        ROLL_MIN_LIVES = Math.min(minLivesConfig, maxLivesConfig);
        ROLL_MAX_LIVES = Math.max(minLivesConfig, maxLivesConfig);
    }

    public Map<Integer, PlayerTeam> getLivesTeams() {
        Map<Integer, PlayerTeam> result = new TreeMap<>();
        Collection<PlayerTeam> allTeams = TeamUtils.getAllTeams();
        if (allTeams != null) {
            for (PlayerTeam team : allTeams) {
                String name = team.getName();
                if (name.startsWith("lives_")) {
                    try {
                        int number = Integer.parseInt(name.replace("lives_",""));
                        result.put(number, team);
                    }catch(Exception e) {}
                }
            }
        }
        return result;
    }

    public void updateTeams() {
        MAX_TAB_NUMBER = 4;
        for (Map.Entry<Integer, PlayerTeam> entry : getLivesTeams().entrySet()) {
            MAX_TAB_NUMBER = Math.max(MAX_TAB_NUMBER, entry.getKey());
            entry.getValue().setSeeFriendlyInvisibles(SEE_FRIENDLY_INVISIBLE_PLAYERS);
        }
        NetworkHandlerServer.sendNumberPackets(PacketNames.TAB_LIVES_CUTOFF, MAX_TAB_NUMBER);
    }

    public Integer getTeamCanKill(String teamName) {
        Integer teamConfig = seasonConfig.getOrCreateInt("team_cankill-"+teamName, defaultTeamCanKill(teamName));
        if (teamConfig <= -1) {
            teamConfig = defaultTeamCanKill(teamName);
            seasonConfig.setProperty("team_cankill-"+teamName, String.valueOf(teamConfig));
        }
        if (teamConfig <= -1) teamConfig = null;
        return teamConfig;
    }

    public Integer getTeamGainLives(String teamName) {
        Integer teamConfig = seasonConfig.getOrCreateInt("team_gainlvies-"+teamName, defaultTeamGainLife(teamName));
        if (teamConfig <= -1) {
            teamConfig = defaultTeamGainLife(teamName);
            seasonConfig.setProperty("team_gainlvies-"+teamName, String.valueOf(teamConfig));
        }
        if (teamConfig <= -1) teamConfig = null;
        return teamConfig;
    }

    public int defaultTeamCanKill(String teamName) {
        if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
            if (teamName.equals("lives_2")) {
                return 3;
            }
        }
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            if (teamName.equals("lives_2")) {
                return LimitedLifeLivesManager.YELLOW_TIME;
            }
        }
        if (teamName.equals("lives_1")) {
            return 1;
        }
        return -1;
    }

    public int defaultTeamGainLife(String teamName) {
        if (currentSeason.getSeason() == Seasons.WILD_LIFE) {
            if (teamName.equals("lives_1") || teamName.equals("lives_2")) {
                return 4;
            }
        }
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            if (teamName.equals("lives_1")) {
                return 1;
            }
            if (teamName.equals("lives_2")) {
                return LimitedLifeLivesManager.YELLOW_TIME;
            }
        }
        return -1;
    }

    public void updateTeamConfig(String teamName, Integer canKill, Integer gainLife) {
        if (canKill == null) canKill = -1;
        if (gainLife == null) gainLife = -1;
        seasonConfig.setProperty("team_cankill-"+teamName, String.valueOf(canKill));
        seasonConfig.setProperty("team_gainlvies-"+teamName, String.valueOf(gainLife));
    }

    public void createTeams() {
        TeamUtils.createTeam("lives_null", "Unassigned", ChatFormatting.GRAY);
        TeamUtils.createTeam("lives_0", "Dead", ChatFormatting.DARK_GRAY);
        TeamUtils.createTeam("lives_1", "Red", ChatFormatting.RED);
        TeamUtils.createTeam("lives_2", "Yellow", ChatFormatting.YELLOW);
        TeamUtils.createTeam("lives_3", "Green", ChatFormatting.GREEN);
        TeamUtils.createTeam("lives_4", "Dark Green", ChatFormatting.DARK_GREEN);
    }

    public void createScoreboards() {
        ScoreboardUtils.createObjective(SCOREBOARD_NAME);
    }

    public ChatFormatting getColorForLives(ServerPlayer player) {
        return getColorForLives(getPlayerLives(player));
    }

    public ChatFormatting getColorForLives(Integer lives) {
        PlayerTeam team = TeamUtils.getTeam(getTeamForLives(lives));
        if (team != null) {
            ChatFormatting color = team.getColor();
            if (color != null) {
                return color;
            }
        }
        return ChatFormatting.DARK_GRAY;
    }

    public Component getFormattedLives(ServerPlayer player) {
        return getFormattedLives(getPlayerLives(player));
    }

    public Component getFormattedLives(@Nullable Integer lives) {
        if (lives == null) {
            lives = 0;
        }
        ChatFormatting color = getColorForLives(lives);
        return Component.literal(String.valueOf(lives)).withStyle(color);
    }
    public String getTeamForPlayer(ServerPlayer player) {
        if (LIVES_SYSTEM_DISABLED) {
            return null;
        }
        Integer lives = getPlayerLives(player);
        return getTeamForLives(lives);
    }
    public String getTeamForLives(Integer lives) {
        String prefix = "lives_";
        String nullTeam = prefix+"null";
        if (lives == null) {
            return nullTeam;
        }
        List<Integer> livesTeams = new ArrayList<>();
        Collection<PlayerTeam> allTeams = TeamUtils.getAllTeams();
        if (allTeams != null) {
            for (PlayerTeam team : allTeams) {
                String name = team.getName();
                if (name.startsWith(prefix)) {
                    try {
                        int index = Integer.parseInt(name.replaceAll(prefix,""));
                        if (index == lives) {
                            return name;
                        }
                        livesTeams.add(index);
                    }catch(Exception ignored) {}
                }
            }
        }
        if (!livesTeams.isEmpty()) {
            Collections.sort(livesTeams);

            if (lives <= livesTeams.get(0)) {
                return prefix + livesTeams.get(0);
            }
            Collections.reverse(livesTeams);
            for (int i : livesTeams) {
                if (lives >= i) {
                    return prefix + i;
                }
            }
        }
        return nullTeam;
    }

    @Nullable
    public Integer getPlayerLives(ServerPlayer player) {
        if (player == null) return null;
        if (isWatcher(player)) return null;
        return ScoreboardUtils.getScore(player, SCOREBOARD_NAME);
    }

    public boolean hasAssignedLives(ServerPlayer player) {
        Integer lives = getPlayerLives(player);
        return lives != null;
    }

    public boolean isAlive(ServerPlayer player) {
        Integer lives = getPlayerLives(player);
        if (lives == null) return false;
        if (!hasAssignedLives(player)) return false;
        return lives > 0;
    }

    public boolean isDead(ServerPlayer player) {
        return !isAlive(player);
    }

    public void removePlayerLife(ServerPlayer player) {
        addToPlayerLives(player,-1);
    }

    public void resetPlayerLife(ServerPlayer player) {
        ScoreboardUtils.resetScore(player, SCOREBOARD_NAME);
        currentSeason.reloadPlayerTeam(player);
        currentSeason.assignDefaultLives(player);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(player);
        }
    }

    public void resetAllPlayerLivesInner() {
        createScoreboards();
        //? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
            ScoreboardUtils.resetScore(entry.getOwner(), SCOREBOARD_NAME);
        }
        *///?} else {
        for (PlayerScoreEntry entry : ScoreboardUtils.getScores(SCOREBOARD_NAME)) {
            ScoreboardUtils.resetScore(entry.owner(), SCOREBOARD_NAME);
        }
        //?}

        currentSeason.reloadAllPlayerTeams();
    }

    public void resetAllPlayerLives() {
        resetAllPlayerLivesInner();
        PlayerUtils.getAllPlayers().forEach(currentSeason::assignDefaultLives);
    }

    public void addPlayerLife(ServerPlayer player) {
        addToPlayerLives(player,1);
    }

    public void addToPlayerLives(ServerPlayer player, int amount) {
        if (amount == 0) return;
        Integer currentLives = getPlayerLives(player);
        if (currentLives == null) currentLives = 0;
        int lives = currentLives + amount;
        if (lives < 0 && !Necromancy.isRessurectedPlayer(player)) lives = 0;
        setPlayerLives(player, lives);
    }

    public void addToLivesNoUpdate(ServerPlayer player, int amount) {
        if (isWatcher(player)) return;
        Integer currentLives = getPlayerLives(player);
        if (currentLives == null) currentLives = 0;
        int lives = currentLives + amount;
        if (lives < 0) lives = 0;
        ScoreboardUtils.setScore(player, SCOREBOARD_NAME, lives);
    }

    public void receiveLifeFromOtherPlayer(Component playerName, ServerPlayer target, boolean isRevive) {
        target.ls$playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.MASTER, 10, 1);
        if (seasonConfig.GIVELIFE_BROADCAST.get(seasonConfig)) {
            PlayerUtils.broadcastMessageExcept(TextUtils.format("{} received a life from {}", target, playerName), target);
        }
        target.sendSystemMessage(TextUtils.format("You received a life from {}", playerName));
        PlayerUtils.sendTitleWithSubtitle(target, Component.nullToEmpty("You received a life"), TextUtils.format("from {}", playerName), 10, 60, 10);
        AnimationUtils.createSpiral(target, 175);
        currentSeason.reloadPlayerTeam(target);
        SessionTranscript.givelife(playerName, target);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(target);
        }
        if (isRevive && isAlive(target)) {
            PlayerUtils.safelyPutIntoSurvival(target);
        }
    }

    public void setPlayerLives(ServerPlayer player, int lives) {
        if (player == null || isWatcher(player)) return;
        Integer livesBefore = getPlayerLives(player);
        ScoreboardUtils.setScore(player, SCOREBOARD_NAME, lives);
        if (lives <= 0) {
            playerLostAllLives(player, livesBefore);
        }
        else if (player.isSpectator()) {
            PlayerUtils.safelyPutIntoSurvival(player);
        }
        currentSeason.reloadPlayerTeam(player);

        if (SubInManager.isSubbingIn(player.getUUID())) {
            String substitutedPlayerName =OtherUtils.profileName(SubInManager.getSubstitutedPlayer(player.getUUID()));
            setScore(substitutedPlayerName, lives);
        }
    }

    public void setScore(String playerName, int lives) {
        ScoreboardUtils.setScore(playerName, SCOREBOARD_NAME, lives);
        currentSeason.reloadAllPlayerTeams();
    }

    @Nullable
    public Integer getScoreLives(String playerName) {
        return ScoreboardUtils.getScore(playerName, SCOREBOARD_NAME);
    }

    @Nullable
    public Boolean isOnLastLife(ServerPlayer player) {
        return isOnSpecificLives(player, 1);
    }

    public boolean isOnLastLife(ServerPlayer player, boolean fallback) {
        Boolean isOnLastLife = isOnLastLife(player);
        if (isOnLastLife == null) return fallback;
        return isOnLastLife;
    }

    @Nullable
    public Boolean isOnSpecificLives(ServerPlayer player, int check) {
        if (isDead(player)) return null;
        Integer lives = getPlayerLives(player);
        if (lives == null) return null;
        return lives == check;
    }

    public boolean isOnSpecificLives(ServerPlayer player, int check, boolean fallback) {
        Boolean isOnLife = isOnSpecificLives(player, check);
        if (isOnLife == null) return fallback;
        return isOnLife;
    }

    @Nullable
    public Boolean isOnAtLeastLives(ServerPlayer player, int check) {
        if (isDead(player)) return null;
        Integer lives = getPlayerLives(player);
        if (lives == null) return null;
        return lives >= check;
    }

    public boolean isOnAtLeastLives(ServerPlayer player, int check, boolean fallback) {
        Boolean isOnAtLeast = isOnAtLeastLives(player, check);
        if (isOnAtLeast == null) return fallback;
        return isOnAtLeast;
    }


    public void playerLostAllLives(ServerPlayer player, Integer livesBefore) {
        if (livesBefore != null) {
            player.setGameMode(GameType.SPECTATOR);
        }
        Vec3 pos = player.position();
        HashMap<Vec3, List<Float>> info = new HashMap<>();
        info.put(pos, List.of(player.getYRot(),player.getXRot()));
        currentSeason.respawnPositions.put(player.getUUID(), info);
        currentSeason.dropItemsOnLastDeath(player);
        if (livesBefore != null) {
            if (FINAL_DEATH_LIGHTNING) {
                LevelUtils.summonHarmlessLightning(player);
            }
            if (livesBefore > 0) {
                Necromancy.clearedPlayers.remove(player.getUUID());
                if (FINAL_DEATH_SOUND != null) {
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), FINAL_DEATH_SOUND);
                }
                showDeathTitle(player);
                DatapackIntegration.EVENT_PLAYER_FINAL_DEATH.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
            }
        }
        SessionTranscript.onPlayerLostAllLives(player);
        currentSeason.boogeymanManager.playerLostAllLives(player);
    }

    public void showDeathTitle(ServerPlayer player) {
        if (SHOW_DEATH_TITLE) {
            String subtitle = seasonConfig.FINAL_DEATH_TITLE_SUBTITLE.get(seasonConfig);
            PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(), player.getDisplayName(), Component.literal(subtitle), 20, 80, 20);
        }
        Component deathMessage = getDeathMessage(player);
        if (!deathMessage.getString().isEmpty()) {
            PlayerUtils.broadcastMessage(deathMessage);
        }
    }

    public Component getDeathMessage(ServerPlayer player) {
        String message = seasonConfig.FINAL_DEATH_MESSAGE.get(seasonConfig);
        if (message.contains("${player}")) {
            return TextUtils.format(message.replace("${player}", "{}"), player);
        }
        return Component.literal(message);
    }

    public List<ServerPlayer> getNonAssignedPlayers() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        players.removeIf(player -> getPlayerLives(player) != null);
        return players;
    }

    public List<ServerPlayer> getNonRedPlayers() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        players.removeIf(player -> isOnLastLife(player, true));
        return players;
    }

    public List<ServerPlayer> getRedPlayers() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        players.removeIf(player -> !isOnLastLife(player, false));
        return players;
    }

    public List<ServerPlayer> getAlivePlayers() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        players.removeIf(this::isDead);
        return players;
    }

    public List<ServerPlayer> getDeadPlayers() {
        List<ServerPlayer> players = PlayerUtils.getAllFunctioningPlayers();
        players.removeIf(this::isAlive);
        return players;
    }

    public boolean canChangeLivesNaturally(ServerPlayer player) {
        if (ONLY_TAKE_LIVES_IN_SESSION && currentSession != null && !AdvancedDeathsManager.hasQueuedDeath(player)) {
            return currentSession.statusStarted();
        }
        return true;
    }

    public boolean canChangeLivesNaturally() {
        if (ONLY_TAKE_LIVES_IN_SESSION && currentSession != null) {
            return currentSession.statusStarted();
        }
        return true;
    }

    public boolean anyPlayersOnLives(int lives) {
        for (ServerPlayer player : getAlivePlayers()) {
            if (isOnSpecificLives(player, lives, false)) return true;
        }
        return false;
    }

    public boolean anyPlayersAtLeastLives(int lives) {
        for (ServerPlayer player : getAlivePlayers()) {
            if (isOnAtLeastLives(player, lives, false)) return true;
        }
        return false;
    }

    public void addSessionActions() {
        if (ROLL_LIVES) {
            currentSession.addSessionAction(actionChooseLives);
        }
    }

    public void assignRandomLivesToUnassignedPlayers() {
        if (!ROLL_LIVES) return;
        assignedLives = true;
        List<ServerPlayer> assignTo = new ArrayList<>();
        for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
            if (player.ls$hasAssignedLives()) continue;
            assignTo.add(player);
        }
        if (assignTo.isEmpty()) return;
        assignRandomLives(assignTo);
    }

    public void assignRandomLives(List<ServerPlayer> players) {
        players.forEach(this::resetPlayerLife);
        PlayerUtils.sendTitleToPlayers(players, Component.literal("You will have...").withStyle(ChatFormatting.GRAY), 10, 40, 10);
        TaskScheduler.scheduleTask(Time.seconds(3), ()-> rollLives(players));
    }

    public void rollLives(List<ServerPlayer> players) {
        int delay = showRandomNumbers(players) + 20;

        HashMap<ServerPlayer, Integer> lives = new HashMap<>();

        int totalSize = players.size();
        int chosenNotRandomly = ROLL_MIN_LIVES;
        for (ServerPlayer player : players) {
            int diff = ROLL_MAX_LIVES-ROLL_MIN_LIVES+2;
            if (chosenNotRandomly <= ROLL_MAX_LIVES && totalSize > diff) {
                lives.put(player, chosenNotRandomly);
                chosenNotRandomly++;
                continue;
            }

            int randomLives = getRandomLife();
            lives.put(player, randomLives);
        }

        TaskScheduler.scheduleTask(delay, () -> {
            //Show the actual amount of lives for one cycle
            for (Map.Entry<ServerPlayer, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayer player = playerEntry.getKey();
                Component textLives = getFormattedLives(livesNum);
                PlayerUtils.sendTitle(player, textLives, 0, 25, 0);
            }
            PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
        });

        delay += 20;

        TaskScheduler.scheduleTask(delay, () -> {
            //Show "x lives." screen
            for (Map.Entry<ServerPlayer, Integer> playerEntry : lives.entrySet()) {
                Integer livesNum = playerEntry.getValue();
                ServerPlayer player = playerEntry.getKey();
                String livesOrTime = currentSeason.getSeason() == Seasons.LIMITED_LIFE ? "to live" : TextUtils.pluralize("life","lives", livesNum);
                Component textLives = TextUtils.format("{}§a {}.", getFormattedLives(livesNum), livesOrTime);
                PlayerUtils.sendTitle(player, textLives, 0, 60, 20);
                SessionTranscript.assignRandomLives(player, livesNum);
                setPlayerLives(player, livesNum);
            }
            PlayerUtils.playSoundToPlayers(lives.keySet(), SoundEvents.END_PORTAL_SPAWN);
            currentSeason. reloadAllPlayerTeams();
        });
    }

    public int showRandomNumbers(List<ServerPlayer> players) {
        int currentDelay = 0;
        int lastLives = -1;
        for (int i = 0; i < 80; i++) {
            if (i >= 75) currentDelay += 20;
            else if (i >= 65) currentDelay += 8;
            else if (i >= 50) currentDelay += 4;
            else if (i >= 30) currentDelay += 2;
            else currentDelay += 1;

            int lives = getRandomLife(lastLives);
            lastLives = lives;

            TaskScheduler.scheduleTask(currentDelay, () -> {
                PlayerUtils.sendTitleToPlayers(players, getFormattedLives(lives), 0, 25, 0);
                PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
            });
        }

        return currentDelay;
    }

    public int getRandomLife() {
        int minLives = ROLL_MIN_LIVES;
        int maxLives = ROLL_MAX_LIVES;
        return rnd.nextInt(minLives, maxLives+1);
    }

    public boolean onlyOnePossibleLife() {
        return ROLL_MIN_LIVES == ROLL_MAX_LIVES;
    }

    public int getRandomLife(int except) {
        if (!onlyOnePossibleLife()){
            int tries = 0;
            while (tries < 100) {
                tries++;
                int lives = getRandomLife();
                if (lives != except) {
                    return lives;
                }
            }
        }
        return getRandomLife();
    }

    public void onPlayerFinishJoining(ServerPlayer player) {
        if (!ROLL_LIVES) return;
        if (!assignedLives) return;
        if (hasAssignedLives(player)) return;
        if (player.ls$isWatcher()) return;
        String livesOrTime = currentSeason.getSeason() == Seasons.LIMITED_LIFE ? "times" : "lives";
        PlayerUtils.broadcastMessageToAdmins(TextUtils.format("§7Assigning random {} to {}§7...", livesOrTime, player));
        assignRandomLives(new ArrayList<>(List.of(player)));
    }
}
