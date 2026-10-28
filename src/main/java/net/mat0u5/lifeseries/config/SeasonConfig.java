package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSoundManager;
import net.mat0u5.lifeseries.config.modifiable.ModifiableTextManager;
import net.mat0u5.lifeseries.network.LifeSeriesNetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.seasons.season.nicelife.NiceLifeTriviaManager;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLifeUsedTasks;
import net.mat0u5.lifeseries.seasons.season.secretlife.TaskManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestion;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.util.LivesManager;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.mat0u5.matlib.config.entry.ConfigEntry;
import net.mat0u5.matlib.config.entry.ConfigGuiEntry;
import net.mat0u5.matlib.config.entry.ConfigurableValue;
import net.mat0u5.matlib.config.entry.DynamicEntry;
import net.mat0u5.matlib.config.type.ConfigType;
import net.mat0u5.matlib.utils.player.PermissionManager;
import net.mat0u5.matlib.utils.player.ScoreboardUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.mat0u5.lifeseries.LifeSeries.*;
import static net.mat0u5.matlib.MatLib.server;

//? if >= 26.2 {
import net.minecraft.world.scores.TeamColor;
//?}

public class SeasonConfig extends DefaultConfigValues {
    public static final String CONFIG_ID = LifeSeries.MOD_ID + ":season";
    public static final String CLIENT_CONFIG_ID = LifeSeries.MOD_ID + ":client";
    public static final String GUI_TITLE = "Life Series Config";

    private static final Map<Class<?>, SeasonConfig> fallbackConfigs = new ConcurrentHashMap<>();
    private boolean silentChange = false;

    protected SeasonConfig(Seasons season) {
        this(LifeSeries.MOD_ID, season.getId() + ".properties");
    }

    protected SeasonConfig(String folderPath, String filePath) {
        super(CONFIG_ID, GUI_TITLE, folderPath, filePath);

        TICK_FREEZE_NOT_IN_SESSION.onChange((oldValue, newValue) -> {
            if (!isActive() || silentChange) return;
            TaskScheduler.schedulePriorityTask(1, () -> {
                if (currentSession != null) currentSession.checkTickFreeze();
                if (!newValue) OtherUtils.setFreezeGame(false);
            });
        });
        WORLDBORDER_SIZE.onChange((oldValue, newValue) -> setWorldborderSize(Level.OVERWORLD, newValue));
        //? if >= 1.21.9 {
        WORLDBORDER_NETHER_SIZE.onChange((oldValue, newValue) -> setWorldborderSize(Level.NETHER, newValue));
        WORLDBORDER_END_SIZE.onChange((oldValue, newValue) -> setWorldborderSize(Level.END, newValue));
        //?}
    }

    protected static <T extends SeasonConfig> T getActive(Class<T> configClass, Supplier<T> fallback) {
        if (configClass.isInstance(seasonConfig)) {
            fallbackConfigs.remove(configClass);
            return configClass.cast(seasonConfig);
        }
        return configClass.cast(fallbackConfigs.computeIfAbsent(configClass, clazz -> fallback.get()));
    }

    public boolean isActive() {
        return seasonConfig == this;
    }

    public <T> void setSilently(ConfigurableValue<T> configurableValue, T value) {
        silentChange = true;
        try {
            configurableValue.set(value);
        } finally {
            silentChange = false;
        }
    }

    private void setWorldborderSize(net.minecraft.resources.ResourceKey<Level> dimension, int size) {
        if (!isActive() || silentChange) return;
        TaskScheduler.schedulePriorityTask(1, () -> {
            if (server == null) return;
            ServerLevel level = server.getLevel(dimension);
            if (level != null) level.getWorldBorder().setSize(size);
        });
    }

    @Override
    public Map<String, String> getRenamedProperties() {
        Map<String, String> renamed = new LinkedHashMap<>();
        renamed.put("show_death_title_on_last_death", "final_death_title_show");
        renamed.put("players_drop_items_on_last_death", "players_drop_items_on_final_death");
        renamed.put("blacklist_banned_potions", "blacklist_banned_potion_effects");
        renamed.put("auto_keep_inventory", "keep_inventory");
        renamed.put("beoadcast_secret_keeper", "broadcast_secret_keeper");
        renamed.put("blacklist_clamped_enchants", "blacklist_clamped_enchants_level_1");
        renamed.put("wildcard_superpowers_zombies_lose_items", "wildcard_superpowers_zombies_first_spawn_clear_items");
        renamed.put("boogeyman_message", "text.boogeyman.message");
        renamed.put("final_death_title_subtitle", "text.final.death.title.subtitle");
        renamed.put("text.wildlife.superpowes.dead", "text.wildlife.superpowers.dead");
        renamed.put("only_take_lives_in_session", "only_change_lives_in_session");
        return renamed;
    }

    public List<String> getAvailableConfigKeys() {
        List<String> result = new ArrayList<>();
        for (ConfigEntry entry : getEntries()) {
            if (entry instanceof ConfigurableValue<?> value) {
                result.add(value.getKey());
            }
        }
        return result;
    }

    @Override
    public boolean hasPermissionsToOpen(ServerPlayer player) {
        return PermissionManager.isAdmin(player) && !LifeSeries.isSeason(Seasons.UNASSIGNED);
    }

    @Override
    public String getClientCounterpartId() {
        return CLIENT_CONFIG_ID;
    }

    @Override
    public <T> void onValueChanged(ConfigurableValue<T> configurableValue, T oldValue, T newValue) {
        super.onValueChanged(configurableValue, oldValue, newValue);
        if (!isActive() || silentChange) return;
        ConfigType<T> type = configurableValue.getType();
        String oldString = oldValue == null ? null : type.serialize(oldValue);
        String newString = newValue == null ? null : type.serialize(newValue);
        LifeSeriesNetworkHandlerServer.onConfigChanged(configurableValue.getKey(), oldString, newString);
    }

    @Override
    public void onDynamicEntryUpdate(ServerPlayer player, ConfigGuiEntry entry) {
        String typeId = entry.type.id();
        String key = entry.key;
        if (typeId.equals(LifeSeriesConfigTypes.EVENT_ENTRY.id()) && entry.args.size() >= 2) {
            String oldValue = getProperty(key);
            setOrRemoveProperty(key, entry.args.get(0).strip());
            setOrRemoveProperty(key + "_canceled", entry.args.get(1));
            LifeSeriesNetworkHandlerServer.onConfigChanged(key, oldValue, getProperty(key));
            DatapackIntegration.reload();
        }
        else if (typeId.equals(LifeSeriesConfigTypes.MODIFIABLE_TEXT.id()) && !entry.args.isEmpty()) {
            String oldValue = getProperty(key);
            setProperty(key, entry.value());
            LifeSeriesNetworkHandlerServer.onConfigChanged(key, oldValue, getProperty(key));
        }
        else if (typeId.equals(LifeSeriesConfigTypes.MODIFIABLE_SOUND.id()) && !entry.args.isEmpty()) {
            String oldValue = getProperty(key);
            setProperty(key, entry.value());
            LifeSeriesNetworkHandlerServer.onConfigChanged(key, oldValue, getProperty(key));
        }
    }

    /*
        Dynamic Entries
     */

    @Override
    public List<DynamicEntry> getDynamicEntries() {
        List<DynamicEntry> result = new ArrayList<>();
        addLivesEntries(result);
        addTeamEntries(result);
        addSoulmateEntries(result);
        addEventEntries(result);
        addSecretTaskEntries(result);
        addTriviaEntries(result);
        addModifiableTextEntries(result);
        addModifiableSoundEntries(result);
        return result;
    }

    private void addLivesEntries(List<DynamicEntry> result) {
        //? if <= 1.20.2 {
        /*for (Score entry : ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME)) {
            result.add(new DynamicEntry(LifeSeriesConfigTypes.LIVES_ENTRY, "dynamic_lives_"+entry.getOwner(), entry.getOwner(), "", "lives", List.of(String.valueOf(entry.getScore()))));
        }
        *///?} else {
        for (PlayerScoreEntry entry : ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME)) {
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.LIVES_ENTRY,
                    "dynamic_lives_"+entry.owner(),
                    entry.owner(),
                    "",
                    "lives",
                    List.of(String.valueOf(entry.value()))
            ));
        }
        //?}
        for (ServerPlayer nonAssignedPlayer : livesManager.getNonAssignedPlayers()) {
            String name = nonAssignedPlayer.getScoreboardName();
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.LIVES_ENTRY,
                    "dynamic_lives_"+name,
                    name,
                    "",
                    "lives",
                    List.of("")
            ));
        }
    }

    private void addTeamEntries(List<DynamicEntry> result) {
        for (Map.Entry<Integer, PlayerTeam> entry : livesManager.getLivesTeams().entrySet()) {
            PlayerTeam team = entry.getValue();
            String teamName = team.getName();
            int teamNum = entry.getKey();
            Integer validKill = livesManager.getTeamCanKill(teamName);
            Integer gainLife = livesManager.getTeamGainLives(teamName);
            String validKillStr = validKill != null ? String.valueOf(validKill) : "";
            String gainLifeStr = gainLife != null ? String.valueOf(gainLife) : "";
            //? if <= 26.1 {
            //String teamColorName = team.getColor().getName();
            //?} else {
            String teamColorName = team.getColor().orElse(TeamColor.WHITE).getSerializedName();
            //?}
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.TEAM_ENTRY,
                    "dynamic_teams_"+UUID.randomUUID(),
                    "",
                    "",
                    "teams",
                    List.of(
                            String.valueOf(teamNum),
                            team.getDisplayName().getString(),
                            teamColorName,
                            validKillStr,
                            gainLifeStr,
                            String.valueOf(LimitedLifeLivesManager.RED_TIME.get()),
                            String.valueOf(LimitedLifeLivesManager.YELLOW_TIME.get()),
                            String.valueOf(LimitedLifeLivesManager.DEFAULT_TIME.get())
                    )
            ));
        }
    }

    private void addSoulmateEntries(List<DynamicEntry> result) {
        if (!LifeSeries.isSeason(Seasons.DOUBLE_LIFE) || !(currentSeason instanceof DoubleLife doubleLife)) return;
        List<Map.Entry<String, String>> soulmatePairs = doubleLife.getSoulmatePairNames();
        if (soulmatePairs.isEmpty()) {
            soulmatePairs = List.of(Map.entry("", ""));
        }
        for (Map.Entry<String, String> pair : soulmatePairs) {
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.SOULMATE_ENTRY,
                    "dynamic_soulmate_"+UUID.randomUUID(),
                    "",
                    "",
                    "season.soulmates",
                    List.of(pair.getKey(), pair.getValue())
            ));
        }
    }

    private void addEventEntries(List<DynamicEntry> result) {
        for (DatapackIntegration.Events event : DatapackIntegration.getAllEvents()) {
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.EVENT_ENTRY,
                    event.getEventName(),
                    event.getDisplayName(),
                    event.getDescription(),
                    "events",
                    List.of(event.getCommand(), event.getCanceled())
            ));
        }
    }

    private void addSecretTaskEntries(List<DynamicEntry> result) {
        if (!LifeSeries.isSeason(Seasons.SECRET_LIFE) || TaskManager.easyTasks_all == null || TaskManager.hardTasks_all == null || TaskManager.redTasks_all == null) return;
        List<String> alreadySelected = SecretLifeUsedTasks.getUsedTasks(TaskManager.usedTasksConfig);
        for (String easyTask : TaskManager.easyTasks_all) {
            result.add(secretTaskEntry("easy", easyTask, alreadySelected.contains(easyTask)));
        }
        for (String hardTask : TaskManager.hardTasks_all) {
            result.add(secretTaskEntry("hard", hardTask, alreadySelected.contains(hardTask)));
        }
        for (String redTask : TaskManager.redTasks_all) {
            result.add(secretTaskEntry("red", redTask, false));
        }
    }

    private DynamicEntry secretTaskEntry(String type, String task, boolean alreadySelected) {
        return new DynamicEntry(
                LifeSeriesConfigTypes.SECRET_TASK,
                "dynamic_task_entry_"+UUID.randomUUID(),
                "",
                "",
                "season.tasks."+type,
                List.of(type, task, String.valueOf(alreadySelected))
        );
    }

    private void addTriviaEntries(List<DynamicEntry> result) {
        if (LifeSeries.isSeason(Seasons.WILD_LIFE) && TriviaWildcard.easyTrivia != null && TriviaWildcard.normalTrivia != null && TriviaWildcard.hardTrivia != null) {
            addTriviaEntries(result, TriviaWildcard.easyTrivia, "easy", "season.trivia.questions.easy");
            addTriviaEntries(result, TriviaWildcard.normalTrivia, "normal", "season.trivia.questions.normal");
            addTriviaEntries(result, TriviaWildcard.hardTrivia, "hard", "season.trivia.questions.hard");
        }
        if (LifeSeries.isSeason(Seasons.NICE_LIFE) && NiceLifeTriviaManager.triviaQuestions != null) {
            addTriviaEntries(result, NiceLifeTriviaManager.triviaQuestions, "normal", "season.trivia.questions");
        }
    }

    private void addTriviaEntries(List<DynamicEntry> result, TriviaQuestionManager manager, String type, String group) {
        for (TriviaQuestion question : manager.tryGetTriviaQuestions()) {
            List<String> info = new ArrayList<>();
            info.add(type);
            info.add(question.getQuestion());
            info.add(String.valueOf(question.getCorrectAnswerIndex()));
            info.addAll(question.getAnswers());
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.TRIVIA_QUESTION,
                    "dynamic_trivia_entry_"+UUID.randomUUID(),
                    "",
                    "",
                    group,
                    info)
            );
        }
    }

    private void addModifiableTextEntries(List<DynamicEntry> result) {
        for (ModifiableTextManager.TextEntry entry : ModifiableTextManager.getRegisteredEntries().values()) {
            String key = entry.configKey();
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.MODIFIABLE_TEXT,
                    key, entry.key(),
                    entry.description(),
                    "text",
                    List.of(getOrCreateProperty(key, entry.defaultValue()), entry.defaultValue())
            ));
        }
    }

    private void addModifiableSoundEntries(List<DynamicEntry> result) {
        for (ModifiableSoundManager.SoundEntry entry : ModifiableSoundManager.getRegisteredEntries().values()) {
            String key = entry.configKey();
            result.add(new DynamicEntry(
                    LifeSeriesConfigTypes.MODIFIABLE_SOUND,
                    key, entry.key(),
                    "",
                    "sound",
                    List.of(getOrCreateProperty(key, entry.defaultValue()), entry.defaultValue())
            ));
        }
    }
}
