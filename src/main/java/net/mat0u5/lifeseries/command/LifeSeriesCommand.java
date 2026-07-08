package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.seasons.util.SeasonChanger;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static net.mat0u5.lifeseries.LifeSeries.*;

public class LifeSeriesCommand extends Command {
    public static final List<String> ALLOWED_SEASON_NAMES = Seasons.getSeasonIds();

    @Override
    public boolean isAllowed() {
        return true;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("");
    }

    public List<String> getAdminCommands() {
        return List.of("lifeseries");
    }

    public List<String> getNonAdminCommands() {
        return List.of("lifeseries");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
          var lifeseriesTree = literal("lifeseries")
                .executes(context -> defaultCommand(context.getSource()))
                .then(literal("worlds")
                        .requires(PermissionManager::isAdmin)
                        .executes(context -> getWorlds(context.getSource()))
                )
                .then(literal("credits")
                        .executes(context -> getCredits(context.getSource()))
                )
                .then(literal("discord")
                    .executes(context -> getDiscord(context.getSource()))
                )
                .then(literal("getSeries")
                    .executes(context -> getSeason(context.getSource()))
                )
                .then(literal("version")
                    .executes(context -> getVersion(context.getSource()))
                )
                .then(literal("config")
                        .executes(context -> config(context.getSource()))
                        .then(literal("set")
                                .requires(PermissionManager::isAdmin)
                                .then(argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(seasonConfig.getAvailableConfigKeys(), builder))
                                        .then(argument("value", StringArgumentType.greedyString())
                                                .executes(context -> configSet(context.getSource(), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "value")))
                                        )
                                )
                        )
                        .then(literal("setEvent")
                                .requires(PermissionManager::isAdmin)
                                .then(argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(DatapackIntegration.getAvailableEvents(), builder))
                                        .then(argument("canceled", StringArgumentType.string())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("allow","override"), builder))
                                                .then(argument("command", StringArgumentType.greedyString())
                                                        .executes(context -> configSetEvent(context.getSource(), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "canceled"), StringArgumentType.getString(context, "command")))
                                                )
                                        )
                                )
                        )
                        .then(literal("viewChanges")
                                .requires(PermissionManager::isAdmin)
                                .executes(context -> configChanges(context.getSource()))
                        )
                        .then(literal("get")
                                .requires(PermissionManager::isAdmin)
                                .then(argument("key", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(seasonConfig.getAvailableConfigKeys(), builder))
                                        .executes(context -> configGet(context.getSource(), StringArgumentType.getString(context, "key")))
                                )
                        )
                )
                .then(literal("wiki")
                        .executes(context -> wiki(context.getSource()))
                )
                .then(literal("help")
                        .executes(context -> wiki(context.getSource()))
                )
                .then(literal("reload")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> reload(context.getSource()))
                )
                .then(literal("chooseSeries")
                        .requires(source -> PermissionManager.isAdmin(source) && (NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer()) || (source.getEntity() == null)))
                        .executes(context -> chooseSeason(context.getSource()))
                )
                .then(literal("setSeries")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("season", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ALLOWED_SEASON_NAMES, builder))
                        .executes(context -> setSeason(
                            context.getSource(), StringArgumentType.getString(context, "season"))
                        )
                        .then(literal("confirm")
                            .executes(context -> setSeason(
                                context.getSource(), StringArgumentType.getString(context, "season"), true)
                            )
                            .then(argument("args", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> suggestArgs(SeasonChanger.AVAILABLE_SEASON_ARGS, builder))
                                    .executes(context -> setSeason(
                                            context.getSource(), StringArgumentType.getString(context, "season"), true, StringArgumentType.getString(context, "args"))
                                    )
                            )
                        )
                    )
                )
                .then(literal("enable")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> enableOrDisable(context.getSource(), false))
                )
                .then(literal("disable")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> enableOrDisable(context.getSource(), true))
                );
        dispatcher.register(lifeseriesTree);
        dispatcher.register(literal("ls").redirect(lifeseriesTree.build()));
    }

    static CompletableFuture<Suggestions> suggestArgs(final Iterable<String> values, final SuggestionsBuilder builder) {
        String prefix = builder.getRemaining();
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        String suggestPrefix = "";
        if (lowerPrefix.contains(" ")) {
            suggestPrefix = prefix.substring(0,prefix.lastIndexOf(" ")+1);
            lowerPrefix = lowerPrefix.substring(lowerPrefix.lastIndexOf(" ")+1);
        }
        String fullLowerPrefix = prefix.toLowerCase(Locale.ROOT);

        for(String name : values) {
            String lowerName = name.toLowerCase(Locale.ROOT);
            if (!fullLowerPrefix.contains(lowerName+" ") && SharedSuggestionProvider.matchesSubStr(lowerPrefix, lowerName)) {
                builder.suggest(suggestPrefix+name);
            }
        }

        return builder.buildFuture();
    }

    private int enableOrDisable(CommandSourceStack source, boolean disabled) {
        sendCommandFeedback(source, ModifiableText.SERIES_DISABLE.get(disabled ? "disabled" : "enabled"));
        LifeSeries.setDisabled(disabled);
        return 1;
    }

    public int chooseSeason(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        if (source.getPlayer() == null) return -1;
        if (!NetworkHandlerServer.wasHandshakeSuccessful(source.getPlayer())) {
            sendCommandFailure(source, Component.nullToEmpty("You must have the Life Series mod installed §nclient-side§c to open the season selection GUI."));
            sendCommandFailure(source, Component.nullToEmpty("Use the '/lifeseries setSeries <season>' command instead."));
            return -1;
        }
        sendCommandFeedback(source, ModifiableText.SEASON_SELECTION_GUI.get());
        SimplePackets.SELECT_SEASON.sendToClient(currentSeason.getSeason().getId(), source.getPlayer());
        return 1;
    }

    public int setSeason(CommandSourceStack source, String setTo) {
        return setSeason(source, setTo, false);
    }

    public int setSeason(CommandSourceStack source, String setTo, boolean confirmed) {
        return setSeason(source, setTo, confirmed, "");
    }

    public int setSeason(CommandSourceStack source, String setTo, boolean confirmed, String args) {
        if (checkBanned(source)) return -1;
        if (!ALLOWED_SEASON_NAMES.contains(setTo)) {
            sendCommandFailure(source, ModifiableText.SEASON_INVALID.get());
            sendCommandFailure(source, ModifiableText.SEASON_INVALID_HELP.get(ALLOWED_SEASON_NAMES));
            return -1;
        }
        if (confirmed || LifeSeries.isSeason(Seasons.UNASSIGNED)) {
            setSeasonFinal(source, setTo, args);
        }
        else {
            sendCommandFeedbackQuiet(source, ModifiableText.SEASON_SELECT_WARNING.get());
        }
        return 1;
    }

    public void setSeasonFinal(CommandSourceStack source, String setTo, String argsStr) {
        boolean prevTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;

        Seasons currentSeason = LifeSeries.getSeason();
        Seasons season = Seasons.getSeasonFromStringName(setTo);
        SeasonChanger.ChangeSeasonArgs args = SeasonChanger.parseChangeSeasonArgs(argsStr);

        if (args.showChatMessage()) sendCommandFeedback(source, ModifiableText.SEASON_CHANGING.get(setTo));

        SeasonChanger.preChangeEvent(currentSeason, season);
        if (SeasonChanger.changeSeasonTo(season, args)) {
            SeasonChanger.postChangeEvent(currentSeason, season);
            if (args.showChatMessage()) PlayerUtils.broadcastMessage(ModifiableText.SEASON_CHANGED.get(setTo));
            boolean currentTickFreeze = Session.TICK_FREEZE_NOT_IN_SESSION;
            if (prevTickFreeze != currentTickFreeze) {
                OtherUtils.setFreezeGame(currentTickFreeze);
            }
        }
    }

    public int config(CommandSourceStack source) {
        //if (checkBanned(source)) return -1;
        ServerPlayer self = source.getPlayer();
        if (self == null) {
            return -1;
        }
        if (!NetworkHandlerServer.wasHandshakeSuccessful(self)) {
            sendCommandFailure(source, Component.nullToEmpty("You must have the Life Series mod installed §nclient-side§c to open the config GUI."));
            sendCommandFailure(source, Component.nullToEmpty("Either install the mod on the client on modify the config folder."));
            return -1;
        }

        SimplePackets.CLEAR_CONFIG.sendToClient(self);
        if (PermissionManager.isAdmin(self) && !LifeSeries.isSeason(Seasons.UNASSIGNED)) {
            LifeSeries.seasonConfig.sendConfigTo(self);
            sendCommandFeedback(source, ModifiableText.CONFIG_GUI_OPENING.get());
        }
        else {
            sendCommandFeedbackQuiet(source, ModifiableText.CONFIG_GUI_OPENING.get());
        }
        SimplePackets.OPEN_CONFIG.sendToClient(self);
        return 1;
    }

    public int configSetEvent(CommandSourceStack source, String key, String canceled, String command) {
        if (checkBanned(source)) return -1;
        seasonConfig.setProperty(key, command);
        seasonConfig.setProperty(key+"_canceled", String.valueOf(canceled.equalsIgnoreCase("override")));
        DatapackIntegration.reload();
        sendCommandFeedback(source, ModifiableText.CONFIG_SETEVENT.get(key));
        return 1;
    }

    public int configGet(CommandSourceStack source, String key) {
        if (checkBanned(source)) return 0;
        String value = seasonConfig.getProperty(key);
        boolean nullValue = value == null;
        if (nullValue) value = "null";
        sendCommandFeedbackQuiet(source, ModifiableText.CONFIG_GET.get(key, value));

        try {
            return Integer.parseInt(value);
        }catch(Exception ignored) {}

        return nullValue ? 0 : 1;
    }

    public int configChanges(CommandSourceStack source) {
        if (checkBanned(source)) return -1;

        if (NetworkHandlerServer.configChanges.isEmpty()) {
            sendCommandFailure(source, ModifiableText.CONFIG_MODIFY_NONE.get());
            return -1;
        }

        MutableComponent changes = ModifiableText.CONFIG_MODIFY_HEADER.get().copy();
        for (Component component : NetworkHandlerServer.configChanges) {
            changes.append(component);
        }

        sendCommandFeedbackQuiet(source, changes);

        return 1;
    }

    public int configSet(CommandSourceStack source, String key, String value) {
        if (checkBanned(source)) return -1;

        seasonConfig.setProperty(key, value);
        ConfigManager.onUpdatedUnknown(key, value);

        sendCommandFeedback(source, ModifiableText.CONFIG_SET.get(key));

        NetworkHandlerServer.updatedConfigThisTick = true;
        if (DefaultConfigValues.RELOAD_NEEDED.contains(key)) {
            NetworkHandlerServer.configNeedsReload = true;
        }
        return 1;
    }

    public int getWorlds(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component worldSavesText = TextUtils.format("§7If you want to play on the exact same world seeds as Grian did, click {}§7 to open a dropbox where you can download the pre-made worlds.", TextUtils.openURLText("https://www.dropbox.com/scl/fo/jk9fhqx0jjbgeo2qa6v5i/AOZZxMx6S7MlS9HrIRJkkX4?rlkey=2khwcnf2zhgi6s4ik01e3z9d0&st=ghw1d8k6&dl=0"));
        sendCommandFeedbackQuiet(source, worldSavesText);
        return 1;
    }

    public int defaultCommand(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        getDiscord(source);
        return 1;
    }

    public int wiki(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component text = TextUtils.format("§7Click {}§7 to open the Life Series Mod wiki", TextUtils.openURLText("https://mat0u5.github.io/LifeSeries-docs"));
        sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getDiscord(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component text = TextUtils.format("§7Click {}§7 to join the mod development discord if you have any questions, issues, requests, or if you just want to hang out :)", TextUtils.openURLText("https://discord.gg/QWJxfb4zQZ"));
        sendCommandFeedbackQuiet(source, text);
        return 1;
    }

    public int getSeason(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        sendCommandFeedbackQuiet(source, ModifiableText.SEASON_GET.get(currentSeason.getSeason().getId()));
        if (source.getPlayer() != null) {
            currentSeason.sendSetSeasonPacket(source.getPlayer());
        }
        return 1;
    }

    public int getVersion(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        sendCommandFeedbackQuiet(source, ModifiableText.MOD_VERSION.get(LifeSeries.MOD_VERSION));
        return 1;
    }

    public int reload(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        sendCommandFeedback(source, ModifiableText.MOD_RELOAD.get());
        OtherUtils.reloadServer();
        return 1;
    }

    public int getCredits(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        Component text = TextUtils.format("§7Click {}§7 to open the full Life Series Mod Credits", TextUtils.openURLText("https://mat0u5.github.io/LifeSeries-docs/other/credits"));
        sendCommandFeedbackQuiet(source, text);
        return 1;
    }
}
