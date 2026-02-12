package net.mat0u5.lifeseries.seasons.boogeyman;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class BoogeymanManager {
    public boolean BOOGEYMAN_ENABLED = false;
    public double BOOGEYMAN_CHANCE_MULTIPLIER = 0.5;
    public int BOOGEYMAN_AMOUNT_MIN = 1;
    public int BOOGEYMAN_AMOUNT_MAX = 99;
    public boolean BOOGEYMAN_ADVANCED_DEATHS = false;
    public double BOOGEYMAN_CHOOSE_MINUTE = 10;
    public boolean BOOGEYMAN_ANNOUNCE_OUTCOME = false;
    public List<String> BOOGEYMAN_IGNORE = new ArrayList<>();
    public List<String> BOOGEYMAN_FORCE = new ArrayList<>();
    public String BOOGEYMAN_MESSAGE = "§7You are the Boogeyman. You must by any means necessary kill a §2dark green§7, §agreen§7 or §eyellow§7 name by direct action to be cured of the curse. If you fail, you will become a §cred name§7. All loyalties and friendships are removed while you are the Boogeyman.";
    public boolean BOOGEYMAN_INFINITE = false;
    public Time BOOGEYMAN_INFINITE_LAST_PICK = Time.minutes(30);
    public Time BOOGEYMAN_INFINITE_AUTO_FAIL = Time.hours(100);
    public boolean BOOGEYMAN_TEAM_NOTICE = false;
    public int BOOGEYMAN_KILLS_NEEDED = 1;
    public boolean BOOGEYMAN_STEAL_LIFE = false;
    public boolean BOOGEYMAN_LOCATOR_BAR = false;

    public List<Boogeyman> boogeymen = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();
    public boolean boogeymanChosen = false;
    public boolean boogeymanListChanged = false;

    public void addSessionActions() {
        if (!BOOGEYMAN_ENABLED) return;
        currentSession.addSessionActionIfTime(
            new SessionAction(Time.minutes(BOOGEYMAN_CHOOSE_MINUTE-5)) {
                @Override
                public void trigger() {
                    if (!BOOGEYMAN_ENABLED) return;
                    if (boogeymanChosen) return;
                    PlayerUtils.broadcastMessage(Component.literal("The Boogeyman is being chosen in 5 minutes.").withStyle(ChatFormatting.RED));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);
                }
            }
        );
        currentSession.addSessionActionIfTime(
            new SessionAction(Time.minutes(BOOGEYMAN_CHOOSE_MINUTE-1)) {
                @Override
                public void trigger() {
                    if (!BOOGEYMAN_ENABLED) return;
                    if (boogeymanChosen) return;
                    PlayerUtils.broadcastMessage(Component.literal("The Boogeyman is being chosen in 1 minute.").withStyle(ChatFormatting.RED));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);
                }
            }
        );
        currentSession.addSessionAction(
                new SessionAction(Time.minutes(BOOGEYMAN_CHOOSE_MINUTE), "Choose Boogeymen") {
                    @Override
                    public void trigger() {
                        if (!BOOGEYMAN_ENABLED) return;
                        if (boogeymanChosen) return;
                        prepareToChooseBoogeymen();
                    }
                }
        );
    }

    public boolean isBoogeyman(ServerPlayer player) {
        if (player == null) return false;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUUID())) {
                return true;
            }
        }
        return false;
    }


    public boolean isBoogeymanThatCanBeCured(ServerPlayer player, ServerPlayer victim) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return false;
        if (boogeyman.cured) return false;
        if (boogeyman.failed) return false;
        List<ServerPlayer> nonReds = livesManager.getNonRedPlayers();
        nonReds.remove(player);
        if (victim.ls$isOnLastLife(true) && !nonReds.isEmpty()) return false;
        return true;
    }

    public Boogeyman getBoogeyman(ServerPlayer player) {
        if (player == null) return null;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUUID())) {
                return boogeyman;
            }
        }
        return null;
    }

    public Boogeyman addBoogeyman(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return null;
        if (!rolledPlayers.contains(player.getUUID())) {
            rolledPlayers.add(player.getUUID());
        }
        Boogeyman newBoogeyman = new Boogeyman(player);
        boogeymen.add(newBoogeyman);
        player.addTag("boogeyman");
        boogeymanChosen = true;
        boogeymanListChanged = true;
        DatapackIntegration.EVENT_BOOGEYMAN_ADDED.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        return newBoogeyman;
    }

    public void addBoogeymanManually(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman newBoogeyman = addBoogeyman(player);
        player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are now a Boogeyman!"));
        messageBoogeyman(newBoogeyman, player);
    }

    public void removeBoogeymanManually(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeymen.remove(boogeyman);
        player.removeTag("boogeyman");
        player.removeTag("boogeyman_cured");
        player.removeTag("boogeyman_failed");
        if (boogeymen.isEmpty()) boogeymanChosen = false;
        player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are no longer a Boogeyman!"));
    }

    public void resetBoogeymen() {
        if (server == null) return;
        for (Boogeyman boogeyman : boogeymen) {
            ServerPlayer player = PlayerUtils.getPlayer(boogeyman.uuid);
            if (player == null) continue;
            player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are no longer a Boogeyman!"));
            player.removeTag("boogeyman");
            player.removeTag("boogeyman_cured");
            player.removeTag("boogeyman_failed");
        }
        boogeymen = new ArrayList<>();
        boogeymanChosen = false;
        rolledPlayers = new ArrayList<>();
    }

    public void reset(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return;
        if (boogeyman.failed || boogeyman.cured) {
            player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] Your Boogeyman  fail/cure status has been reset"));
        }
        boogeyman.failed = false;
        boogeyman.cured = false;
        player.removeTag("boogeyman_cured");
        player.removeTag("boogeyman_failed");
        boogeyman.died = false;
        boogeyman.resetKills();
    }

    public void cure(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return;
        boogeyman.failed = false;
        player.addTag("boogeyman_cured");
        player.removeTag("boogeyman_failed");
        if (boogeyman.cured) return;
        boogeyman.cured = true;
        PlayerUtils.sendTitle(player,Component.nullToEmpty("§aYou are cured!"), 20, 30, 20);
        PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_cure")));

        boolean stealLife = BOOGEYMAN_STEAL_LIFE && livesManager.canChangeLivesNaturally();

        if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
            if (stealLife) {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 is cured of the Boogeyman curse and gained a life for succeeding!", player));
            }
            else {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 is cured of the Boogeyman curse!", player));
            }
        }
        DatapackIntegration.EVENT_BOOGEYMAN_CURE_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        if (!DatapackIntegration.EVENT_BOOGEYMAN_CURE_REWARD.isCanceled() && stealLife) {
            player.ls$addLife();
        }
    }

    public void onBoogeymanKill(ServerPlayer boogeyPlayer, ServerPlayer victim) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(boogeyPlayer);
        if (boogeymen == null) return;
        if (boogeyman.cured || boogeyman.failed) return;
        DatapackIntegration.EVENT_BOOGEYMAN_KILL.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Boogeyman", boogeyPlayer.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("Victim", victim.getScoreboardName())
        ));
        boogeyman.onKill();
        if (boogeyman.shouldCure()) {
            cure(boogeyPlayer);
        }
        else {
            boogeyPlayer.sendSystemMessage(TextUtils.formatLoosely("§7You still need {} {} to be cured of the curse.", boogeyman.killsNeeded, TextUtils.pluralize("kill", boogeyman.killsNeeded)));
        }
    }

    public void chooseNewBoogeyman() {
        if (!BOOGEYMAN_ENABLED) return;
        if (currentSession.statusFinished() || currentSession.statusNotStarted()) return;
        if (currentSession.getRemainingTime().isSmaller(BOOGEYMAN_INFINITE_LAST_PICK)) return;

        PlayerUtils.broadcastMessage(Component.literal("A new boogeyman is about to be chosen.").withStyle(ChatFormatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            List<ServerPlayer> allowedPlayers = getAllowedBoogeyPlayers();
            if (allowedPlayers.isEmpty()) return;
            Collections.shuffle(allowedPlayers);
            TaskScheduler.scheduleTask(Time.seconds(9), () -> chooseBoogeymen(allowedPlayers, BoogeymanRollType.INFINITE));
        });
    }

    public void prepareToChooseBoogeymen() {
        if (!BOOGEYMAN_ENABLED) return;
        PlayerUtils.broadcastMessage(Component.literal("The Boogeyman is about to be chosen.").withStyle(ChatFormatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            resetBoogeymen();
            chooseBoogeymen(livesManager.getAlivePlayers(), BoogeymanRollType.NORMAL);
        });
    }

    public void showRolling(List<ServerPlayer> allowedPlayers) {
        PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("3").withStyle(ChatFormatting.GREEN),0,35,0);

        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("2").withStyle(ChatFormatting.YELLOW),0,35,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("1").withStyle(ChatFormatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(90, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_wait")));
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("You are...").withStyle(ChatFormatting.YELLOW),10,50,20);
        });
    }
    public void chooseBoogeymen(List<ServerPlayer> allowedPlayers, BoogeymanRollType rollType) {
        if (!BOOGEYMAN_ENABLED) return;
        allowedPlayers.removeIf(this::isBoogeyman);
        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(Time.seconds(9), () -> boogeymenChooseRandom(allowedPlayers, rollType));
    }

    public int getBoogeymanAmount(BoogeymanRollType rollType) {
        if (rollType == BoogeymanRollType.INFINITE) {
            return 1;
        }
        if (rollType == BoogeymanRollType.LATE_JOIN) {
            if ((1.0 / PlayerUtils.getAllFunctioningPlayers().size()) >= Math.random()) {
                return 1;
            }
            else {
                return 0;
            }
        }

        int chooseBoogeymen = BOOGEYMAN_AMOUNT_MIN;
        List<ServerPlayer> nonRedPlayers = livesManager.getNonRedPlayers();
        while(BOOGEYMAN_CHANCE_MULTIPLIER >= Math.random() && chooseBoogeymen < nonRedPlayers.size()) {
            chooseBoogeymen++;
        }
        if (chooseBoogeymen > BOOGEYMAN_AMOUNT_MAX) {
            chooseBoogeymen = BOOGEYMAN_AMOUNT_MAX;
        }
        return chooseBoogeymen;
    }

    public void boogeymenChooseRandom(List<ServerPlayer> allowedPlayers, BoogeymanRollType rollType) {
        if (!BOOGEYMAN_ENABLED) return;
        if (BOOGEYMAN_AMOUNT_MAX <= 0) return;
        if (BOOGEYMAN_AMOUNT_MAX < BOOGEYMAN_AMOUNT_MIN) return;
        allowedPlayers.removeIf(this::isBoogeyman);
        if (allowedPlayers.isEmpty()) return;

        List<ServerPlayer> normalPlayers = new ArrayList<>();
        List<ServerPlayer> boogeyPlayers = new ArrayList<>();


        if (rollType != BoogeymanRollType.INFINITE) {
            boogeyPlayers.addAll(getRandomBoogeyPlayers(allowedPlayers, rollType));
        }
        else {
            // Infinite mode, just pick one from the allowed players
            Collections.shuffle(allowedPlayers);
            boogeyPlayers.add(allowedPlayers.get(0));
        }

        for (ServerPlayer player : allowedPlayers) {
            if (rollType != BoogeymanRollType.INFINITE) {
                if (rolledPlayers.contains(player.getUUID())) continue;
                rolledPlayers.add(player.getUUID());
            }
            if (boogeyPlayers.contains(player)) continue;
            normalPlayers.add(player);
        }

        handleBoogeymanLists(normalPlayers, boogeyPlayers);
    }

    public List<ServerPlayer> getRandomBoogeyPlayers(List<ServerPlayer> allowedPlayers, BoogeymanRollType rollType) {
        List<ServerPlayer> boogeyPlayers = new ArrayList<>();
        List<ServerPlayer> nonRedPlayers = livesManager.getNonRedPlayers();
        Collections.shuffle(nonRedPlayers);
        int chooseBoogeymen = getBoogeymanAmount(rollType);

        for (ServerPlayer player : nonRedPlayers) {
            // First loop for the forced boogeymen
            if (isBoogeyman(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUUID())) continue;
            if (BOOGEYMAN_IGNORE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (BOOGEYMAN_FORCE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) {
                boogeyPlayers.add(player);
                chooseBoogeymen--;
            }
        }
        for (ServerPlayer player : nonRedPlayers) {
            // Second loop for the non-forced boogeymen
            if (chooseBoogeymen <= 0) break;
            if (isBoogeyman(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUUID())) continue;
            if (BOOGEYMAN_IGNORE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (BOOGEYMAN_FORCE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (boogeyPlayers.contains(player)) continue;

            boogeyPlayers.add(player);
            chooseBoogeymen--;
        }
        return boogeyPlayers;
    }

    public List<ServerPlayer> getAllowedBoogeyPlayers() {
        List<ServerPlayer> result = new ArrayList<>(livesManager.getNonRedPlayers());
        result.removeIf(this::isBoogeyman);
        return result;
    }

    public void handleBoogeymanLists(List<ServerPlayer> normalPlayers, List<ServerPlayer> boogeyPlayers) {
        PlayerUtils.playSoundToPlayers(normalPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_no")));
        PlayerUtils.playSoundToPlayers(boogeyPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_yes")));
        PlayerUtils.sendTitleToPlayers(normalPlayers, Component.literal("NOT the Boogeyman.").withStyle(ChatFormatting.GREEN),10,50,20);
        PlayerUtils.sendTitleToPlayers(boogeyPlayers, Component.literal("The Boogeyman.").withStyle(ChatFormatting.RED),10,50,20);
        for (ServerPlayer boogey : boogeyPlayers) {
            Boogeyman boogeyman = addBoogeyman(boogey);
            messageBoogeyman(boogeyman, boogey);
        }
        SessionTranscript.boogeymenChosen(boogeyPlayers);
    }

    public void messageBoogeyman(Boogeyman boogeyman, ServerPlayer boogey) {
        boogey.sendSystemMessage(Component.nullToEmpty(BOOGEYMAN_MESSAGE));
        if (boogeyman != null && boogeyman.killsNeeded != 1) {
            boogey.sendSystemMessage(TextUtils.formatLoosely("§7You need {} {} to be cured of the curse.", boogeyman.killsNeeded, TextUtils.pluralize("kill", boogeyman.killsNeeded)));
        }
    }

    public void sessionEnd() {
        if (!BOOGEYMAN_ENABLED) return;
        if (server == null) return;
        for (Boogeyman boogeyman : new ArrayList<>(boogeymen)) {
            if (boogeyman.died) continue;

            if (!boogeyman.cured && !boogeyman.failed) {
                ServerPlayer player = PlayerUtils.getPlayer(boogeyman.uuid);
                if (player == null) {
                    if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
                        PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.", boogeyman.name));
                    }
                    ScoreboardUtils.setScore(boogeyman.name, LivesManager.SCOREBOARD_NAME, 1);
                    continue;
                }
                playerFailBoogeyman(player, true);
            }
        }
    }

    public void playerFailBoogeymanManually(ServerPlayer player, boolean sendMessage) {
        playerFailBoogeyman(player, sendMessage);
    }

    public boolean playerFailBoogeyman(ServerPlayer player, boolean sendMessage) {
        if (!BOOGEYMAN_ENABLED) return false;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return false;

        player.removeTag("boogeyman_cured");
        player.addTag("boogeyman_failed");
        boogeyman.cured = false;
        if (boogeyman.failed) return false;
        boogeyman.failed = true;

        boolean canChangeLives = player.ls$isAlive() && !player.ls$isOnLastLife(true);


        DatapackIntegration.EVENT_BOOGEYMAN_FAIL_REWARD.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        if (!DatapackIntegration.EVENT_BOOGEYMAN_FAIL_REWARD.isCanceled()) {
            if (BOOGEYMAN_ADVANCED_DEATHS) {
                PlayerUtils.sendTitle(player,Component.nullToEmpty("§cThe curse consumes you.."), 20, 30, 20);
                if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                    PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been consumed by the curse.", player));
                }
                if (canChangeLives) {
                    AdvancedDeathsManager.setPlayerLives(player, 1);
                }
            }
            else {
                PlayerUtils.sendTitle(player,Component.nullToEmpty("§cYou have failed."), 20, 30, 20);
                PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_fail")));
                if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                    PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.", player));
                }
                if (canChangeLives) {
                    player.ls$setLives(1);
                }
            }
        }
        return true;
    }

    public void playerLostAllLives(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeyman.died = true;
    }

    public void onPlayerFinishJoining(ServerPlayer player) {
        if (!BOOGEYMAN_ENABLED) return;
        if (!boogeymanChosen) return;
        if (rolledPlayers.contains(player.getUUID())) return;
        if (player.ls$isDead()) return;
        if (boogeymen.size() >= BOOGEYMAN_AMOUNT_MAX) return;
        if (currentSession.statusNotStarted() || currentSession.statusFinished()) return;
        TaskScheduler.scheduleTask(Time.seconds(2), () -> {
            player.sendSystemMessage(Component.nullToEmpty("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!"));
            chooseBoogeymen(new ArrayList<>(List.of(player)), BoogeymanRollType.LATE_JOIN);
        });
    }

    public void onReload() {
        BOOGEYMAN_ENABLED = seasonConfig.BOOGEYMAN.get();
        if (!BOOGEYMAN_ENABLED) {
            onDisabledBoogeyman();
        }
        BOOGEYMAN_CHANCE_MULTIPLIER = seasonConfig.BOOGEYMAN_CHANCE_MULTIPLIER.get();
        BOOGEYMAN_AMOUNT_MIN = seasonConfig.BOOGEYMAN_MIN_AMOUNT.get();
        BOOGEYMAN_AMOUNT_MAX = seasonConfig.BOOGEYMAN_MAX_AMOUNT.get();
        BOOGEYMAN_ADVANCED_DEATHS = seasonConfig.BOOGEYMAN_ADVANCED_DEATHS.get();
        BOOGEYMAN_MESSAGE = seasonConfig.BOOGEYMAN_MESSAGE.get();
        BOOGEYMAN_IGNORE.clear();
        BOOGEYMAN_FORCE.clear();
        for (String name : seasonConfig.BOOGEYMAN_IGNORE.get().replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) BOOGEYMAN_IGNORE.add(name.toLowerCase(Locale.ROOT));
        }
        for (String name : seasonConfig.BOOGEYMAN_FORCE.get().replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) BOOGEYMAN_FORCE.add(name.toLowerCase(Locale.ROOT));
        }
        BOOGEYMAN_CHOOSE_MINUTE = seasonConfig.BOOGEYMAN_CHOOSE_MINUTE.get();
        BOOGEYMAN_ANNOUNCE_OUTCOME = seasonConfig.BOOGEYMAN_ANNOUNCE_OUTCOME.get();
        BOOGEYMAN_INFINITE = seasonConfig.BOOGEYMAN_INFINITE.get();
        BOOGEYMAN_INFINITE_LAST_PICK = Time.seconds(seasonConfig.BOOGEYMAN_INFINITE_LAST_PICK.get());
        BOOGEYMAN_INFINITE_AUTO_FAIL = Time.seconds(seasonConfig.BOOGEYMAN_INFINITE_AUTO_FAIL.get());
        BOOGEYMAN_TEAM_NOTICE = seasonConfig.BOOGEYMAN_TEAM_NOTICE.get();
        BOOGEYMAN_KILLS_NEEDED = seasonConfig.BOOGEYMAN_KILLS_NEEDED.get();
        BOOGEYMAN_STEAL_LIFE = seasonConfig.BOOGEYMAN_STEAL_LIFE.get();
        BOOGEYMAN_LOCATOR_BAR = seasonConfig.BOOGEYMAN_LOCATOR_BAR.get();
    }

    public void onDisabledBoogeyman() {
        resetBoogeymen();
    }

    List<UUID> afterFailedMessaged = new ArrayList<>();
    List<UUID> warningAutoFail = new ArrayList<>();
    public void tick() {
        if (!BOOGEYMAN_ENABLED) return;
        for (Boogeyman boogeyman : boogeymen) {
            boogeyman.tick();
            infiniteBoogeymenTick(boogeyman);
            autoFailTick(boogeyman);
            failedMessagesTick(boogeyman);
        }
        if (boogeymanListChanged) {
            boogeymanListChanged = false;
            if (BOOGEYMAN_TEAM_NOTICE) {
                sendBoogeymanTeamNotice();
            }
        }
    }

    public void sendBoogeymanTeamNotice() {
        for (Boogeyman boogeyman : boogeymen) {
            ServerPlayer player = boogeyman.getPlayer();
            if (player == null) continue;

            List<Component> boogeymenList = new ArrayList<>();
            for (Boogeyman otherBoogeyman : boogeymen) {
                ServerPlayer otherPlayer = otherBoogeyman.getPlayer();
                if (otherPlayer == player) continue;
                if (otherPlayer != null) {
                    boogeymenList.add(otherPlayer.getDisplayName());
                }
                else {
                    boogeymenList.add(Component.nullToEmpty(otherBoogeyman.name));
                }
            }

            if (!boogeymenList.isEmpty()) {
                player.sendSystemMessage(TextUtils.format("Current Boogeymen: {}", boogeymenList));

            }
        }
    }

    public void infiniteBoogeymenTick(Boogeyman boogeyman) {
        if (!BOOGEYMAN_INFINITE) return;
        if (!currentSession.statusStarted()) return;
        if (!boogeyman.failed && !boogeyman.cured && !boogeyman.died) return;
        boogeymen.remove(boogeyman);
        ServerPlayer player = boogeyman.getPlayer();
        if (player != null) {
            player.removeTag("boogeyman");
            player.removeTag("boogeyman_cured");
            player.removeTag("boogeyman_failed");
        }
        TaskScheduler.scheduleTask(Time.seconds(5), this::chooseNewBoogeyman);
    }

    public void autoFailTick(Boogeyman boogeyman) {
        if (!BOOGEYMAN_INFINITE) return;
        if (!currentSession.statusStarted()) return;
        if (boogeyman.failed) return;
        if (boogeyman.cured) return;
        if (boogeyman.died) return;

        Time boogeymanTime = boogeyman.timeBoogeyman;
        Time warningTime = BOOGEYMAN_INFINITE_AUTO_FAIL.diff(Time.minutes(5));
        if (boogeymanTime.isLarger(warningTime) && warningTime.isLarger(Time.zero())) {
            if (!warningAutoFail.contains(boogeyman.uuid)) {
                ServerPlayer player = boogeyman.getPlayer();
                if (player != null) {
                    warningAutoFail.add(boogeyman.uuid);
                    player.sendSystemMessage(Component.nullToEmpty("§cYou only have 5 minutes left to kill someone as the Boogeyman before you fail!"));
                }
            }
        }
        else {
            warningAutoFail.remove(boogeyman.uuid);
        }

        if (boogeymanTime.isLarger(BOOGEYMAN_INFINITE_AUTO_FAIL)) {
            ServerPlayer player = boogeyman.getPlayer();
            if (player != null) {
                if (!playerFailBoogeyman(player, true)) {
                    player.addTag("boogeyman_failed");
                    boogeyman.failed = true;
                }
            }
        }
    }

    public void failedMessagesTick(Boogeyman boogeyman) {
        if (!afterFailedMessages()) return;
        if (!boogeyman.failed) {
            afterFailedMessaged.remove(boogeyman.uuid);
            return;
        }
        if (afterFailedMessaged.contains(boogeyman.uuid)) return;
        ServerPlayer player = boogeyman.getPlayer();
        if (player == null) return;
        if (!player.isAlive()) return;
        if (AdvancedDeathsManager.hasQueuedDeath(player)) return;
        afterFailLogic(player);
    }

    public void afterFailLogic(ServerPlayer player) {
        if (!afterFailedMessages()) return;
        afterFailedMessaged.add(player.getUUID());
        int delay = 20;
        if (!BOOGEYMAN_ADVANCED_DEATHS) {
            delay = 140;
        }
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            delay = 140;
        }

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.sendTitle(player, Component.nullToEmpty("§cYour lives are taken..."), 20, 80, 20);
        });
        delay += 140;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.sendTitle(player, Component.nullToEmpty("§c...Now take theirs."), 20, 80, 20);
        });
    }

    public boolean afterFailedMessages() {
        return BOOGEYMAN_ADVANCED_DEATHS;
    }

    public enum BoogeymanRollType {
        NORMAL,
        LATE_JOIN,
        INFINITE;
    }
}
