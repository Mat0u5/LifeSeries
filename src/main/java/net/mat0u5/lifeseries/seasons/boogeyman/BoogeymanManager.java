package net.mat0u5.lifeseries.seasons.boogeyman;

import net.mat0u5.lifeseries.seasons.boogeyman.advanceddeaths.AdvancedDeathsManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    public int BOOGEYMAN_INFINITE_LAST_PICK = 1800;
    public int BOOGEYMAN_INFINITE_AUTO_FAIL = 360000;

    public List<Boogeyman> boogeymen = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();
    public boolean boogeymanChosen = false;

    public void addSessionActions() {
        if (!BOOGEYMAN_ENABLED) return;
        currentSession.addSessionActionIfTime(
            new SessionAction(OtherUtils.minutesToTicks(BOOGEYMAN_CHOOSE_MINUTE-5)) {
                @Override
                public void trigger() {
                    if (!BOOGEYMAN_ENABLED) return;
                    if (boogeymanChosen) return;
                    PlayerUtils.broadcastMessage(Text.literal("The Boogeyman is being chosen in 5 minutes.").formatted(Formatting.RED));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
                }
            }
        );
        currentSession.addSessionActionIfTime(
            new SessionAction(OtherUtils.minutesToTicks(BOOGEYMAN_CHOOSE_MINUTE-1)) {
                @Override
                public void trigger() {
                    if (!BOOGEYMAN_ENABLED) return;
                    if (boogeymanChosen) return;
                    PlayerUtils.broadcastMessage(Text.literal("The Boogeyman is being chosen in 1 minute.").formatted(Formatting.RED));
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
                }
            }
        );
        currentSession.addSessionAction(
                new SessionAction(
                        OtherUtils.minutesToTicks(BOOGEYMAN_CHOOSE_MINUTE),TextUtils.formatString("§7Choose Boogeymen §f[{}]", OtherUtils.formatTime(OtherUtils.minutesToTicks(BOOGEYMAN_CHOOSE_MINUTE))), "Choose Boogeymen"
                ) {
                    @Override
                    public void trigger() {
                        if (!BOOGEYMAN_ENABLED) return;
                        if (boogeymanChosen) return;
                        prepareToChooseBoogeymen();
                    }
                }
        );
    }

    public boolean isBoogeyman(ServerPlayerEntity player) {
        if (player == null) return false;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUuid())) {
                return true;
            }
        }
        return false;
    }


    public boolean isBoogeymanThatCanBeCured(ServerPlayerEntity player, ServerPlayerEntity victim) {
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return false;
        if (boogeyman.cured) return false;
        if (boogeyman.failed) return false;
        if (livesManager.isOnLastLife(victim, true)) return false;
        return true;
    }

    public Boogeyman getBoogeyman(ServerPlayerEntity player) {
        if (player == null) return null;
        for (Boogeyman boogeyman : boogeymen) {
            if (boogeyman.uuid.equals(player.getUuid())) {
                return boogeyman;
            }
        }
        return null;
    }

    public void addBoogeyman(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        if (!rolledPlayers.contains(player.getUuid())) {
            rolledPlayers.add(player.getUuid());
        }
        Boogeyman newBoogeyman = new Boogeyman(player);
        boogeymen.add(newBoogeyman);
        boogeymanChosen = true;
    }

    public void addBoogeymanManually(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        addBoogeyman(player);
        player.sendMessage(Text.of("§c [NOTICE] You are now a Boogeyman!"));
    }

    public void removeBoogeymanManually(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeymen.remove(boogeyman);
        if (boogeymen.isEmpty()) boogeymanChosen = false;
        player.sendMessage(Text.of("§c [NOTICE] You are no longer a Boogeyman!"));
    }

    public void resetBoogeymen() {
        if (server == null) return;
        for (Boogeyman boogeyman : boogeymen) {
            ServerPlayerEntity player = PlayerUtils.getPlayer(boogeyman.uuid);
            if (player == null) continue;
            player.sendMessage(Text.of("§c [NOTICE] You are no longer a Boogeyman!"));
        }
        boogeymen = new ArrayList<>();
        boogeymanChosen = false;
        rolledPlayers = new ArrayList<>();
    }

    public void cure(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return;
        boogeyman.failed = false;
        if (boogeyman.cured) return;
        boogeyman.cured = true;
        PlayerUtils.sendTitle(player,Text.of("§aYou are cured!"), 20, 30, 20);
        PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_cure")));
        if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
            PlayerUtils.broadcastMessage(TextUtils.format("{}§7 is cured of the Boogeyman curse!", player));
        }
    }

    public void chooseNewBoogeyman() {
        if (!BOOGEYMAN_ENABLED) return;
        if (currentSession.statusFinished() || currentSession.statusNotStarted()) return;
        int remainingTicks = currentSession.getRemainingTime();
        if (remainingTicks <= OtherUtils.secondsToTicks(BOOGEYMAN_INFINITE_LAST_PICK)) return;

        PlayerUtils.broadcastMessage(Text.literal("A new boogeyman is about to be chosen.").formatted(Formatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(100, () -> {
            List<ServerPlayerEntity> allowedPlayers = getAllowedBoogeyPlayers();
            if (allowedPlayers.isEmpty()) return;
            Collections.shuffle(allowedPlayers);
            TaskScheduler.scheduleTask(180, () -> chooseBoogeymen(allowedPlayers, BoogeymanRollType.INFINITE));
        });
    }

    public void prepareToChooseBoogeymen() {
        if (!BOOGEYMAN_ENABLED) return;
        PlayerUtils.broadcastMessage(Text.literal("The Boogeyman is about to be chosen.").formatted(Formatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(100, () -> {
            resetBoogeymen();
            chooseBoogeymen(livesManager.getAlivePlayers(), BoogeymanRollType.NORMAL);
        });
    }

    public void showRolling(List<ServerPlayerEntity> allowedPlayers) {
        PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("3").formatted(Formatting.GREEN),0,35,0);

        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("2").formatted(Formatting.YELLOW),0,35,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("1").formatted(Formatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(90, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.of(Identifier.ofVanilla("lastlife_boogeyman_wait")));
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Text.literal("You are...").formatted(Formatting.YELLOW),10,50,20);
        });
    }
    public void chooseBoogeymen(List<ServerPlayerEntity> allowedPlayers, BoogeymanRollType rollType) {
        if (!BOOGEYMAN_ENABLED) return;
        allowedPlayers.removeIf(this::isBoogeyman);
        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(180, () -> boogeymenChooseRandom(allowedPlayers, rollType));
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
        List<ServerPlayerEntity> nonRedPlayers = livesManager.getNonRedPlayers();
        while(BOOGEYMAN_CHANCE_MULTIPLIER >= Math.random() && chooseBoogeymen < nonRedPlayers.size()) {
            chooseBoogeymen++;
        }
        if (chooseBoogeymen > BOOGEYMAN_AMOUNT_MAX) {
            chooseBoogeymen = BOOGEYMAN_AMOUNT_MAX;
        }
        return chooseBoogeymen;
    }

    public void boogeymenChooseRandom(List<ServerPlayerEntity> allowedPlayers, BoogeymanRollType rollType) {
        if (!BOOGEYMAN_ENABLED) return;
        if (BOOGEYMAN_AMOUNT_MAX <= 0) return;
        if (BOOGEYMAN_AMOUNT_MAX < BOOGEYMAN_AMOUNT_MIN) return;
        allowedPlayers.removeIf(this::isBoogeyman);
        if (allowedPlayers.isEmpty()) return;

        List<ServerPlayerEntity> normalPlayers = new ArrayList<>();
        List<ServerPlayerEntity> boogeyPlayers = new ArrayList<>();


        if (rollType != BoogeymanRollType.INFINITE) {
            boogeyPlayers.addAll(getRandomBoogeyPlayers(allowedPlayers, rollType));
        }
        else {
            // Infinite mode, just pick one from the allowed players
            Collections.shuffle(allowedPlayers);
            boogeyPlayers.add(allowedPlayers.getFirst());
        }

        for (ServerPlayerEntity player : allowedPlayers) {
            if (rollType != BoogeymanRollType.INFINITE) {
                if (rolledPlayers.contains(player.getUuid())) continue;
                rolledPlayers.add(player.getUuid());
            }
            if (boogeyPlayers.contains(player)) continue;
            normalPlayers.add(player);
        }

        handleBoogeymanLists(normalPlayers, boogeyPlayers);
    }

    public List<ServerPlayerEntity> getRandomBoogeyPlayers(List<ServerPlayerEntity> allowedPlayers, BoogeymanRollType rollType) {
        List<ServerPlayerEntity> boogeyPlayers = new ArrayList<>();
        List<ServerPlayerEntity> nonRedPlayers = livesManager.getNonRedPlayers();
        Collections.shuffle(nonRedPlayers);
        int chooseBoogeymen = getBoogeymanAmount(rollType);

        for (ServerPlayerEntity player : nonRedPlayers) {
            // First loop for the forced boogeymen
            if (isBoogeyman(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUuid())) continue;
            if (BOOGEYMAN_IGNORE.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (BOOGEYMAN_FORCE.contains(player.getNameForScoreboard().toLowerCase())) {
                boogeyPlayers.add(player);
                chooseBoogeymen--;
            }
        }
        for (ServerPlayerEntity player : nonRedPlayers) {
            // Second loop for the non-forced boogeymen
            if (chooseBoogeymen <= 0) break;
            if (isBoogeyman(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUuid())) continue;
            if (BOOGEYMAN_IGNORE.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (BOOGEYMAN_FORCE.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (boogeyPlayers.contains(player)) continue;

            boogeyPlayers.add(player);
            chooseBoogeymen--;
        }
        return boogeyPlayers;
    }

    public List<ServerPlayerEntity> getAllowedBoogeyPlayers() {
        List<ServerPlayerEntity> result = new ArrayList<>(livesManager.getNonRedPlayers());
        result.removeIf(this::isBoogeyman);
        return result;
    }

    public void handleBoogeymanLists(List<ServerPlayerEntity> normalPlayers, List<ServerPlayerEntity> boogeyPlayers) {
        PlayerUtils.playSoundToPlayers(normalPlayers, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_no")));
        PlayerUtils.playSoundToPlayers(boogeyPlayers, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_yes")));
        PlayerUtils.sendTitleToPlayers(normalPlayers, Text.literal("NOT the Boogeyman.").formatted(Formatting.GREEN),10,50,20);
        PlayerUtils.sendTitleToPlayers(boogeyPlayers, Text.literal("The Boogeyman.").formatted(Formatting.RED),10,50,20);
        for (ServerPlayerEntity boogey : boogeyPlayers) {
            addBoogeyman(boogey);
            messageBoogeymen(boogey);
        }
        SessionTranscript.boogeymenChosen(boogeyPlayers);
    }

    public void messageBoogeymen(ServerPlayerEntity boogey) {
        boogey.sendMessage(Text.of(BOOGEYMAN_MESSAGE));
    }

    public void sessionEnd() {
        if (!BOOGEYMAN_ENABLED) return;
        if (server == null) return;
        for (Boogeyman boogeyman : new ArrayList<>(boogeymen)) {
            if (boogeyman.died) continue;

            if (!boogeyman.cured && !boogeyman.failed) {
                ServerPlayerEntity player = PlayerUtils.getPlayer(boogeyman.uuid);
                if (player == null) {
                    if (BOOGEYMAN_ANNOUNCE_OUTCOME) {
                        PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.", boogeyman.name));
                    }
                    ScoreboardUtils.setScore(ScoreHolder.fromName(boogeyman.name), LivesManager.SCOREBOARD_NAME, 1);
                    continue;
                }
                playerFailBoogeyman(player, true);
            }
        }
    }

    public void playerFailBoogeymanManually(ServerPlayerEntity player, boolean sendMessage) {
        playerFailBoogeyman(player, sendMessage);
    }

    public boolean playerFailBoogeyman(ServerPlayerEntity player, boolean sendMessage) {
        if (!BOOGEYMAN_ENABLED) return false;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeymen == null) return false;

        boogeyman.cured = false;
        if (boogeyman.failed) return false;
        boogeyman.failed = true;

        boolean canChangeLives = livesManager.isAlive(player) && !livesManager.isOnLastLife(player, true);

        if (BOOGEYMAN_ADVANCED_DEATHS) {
            PlayerUtils.sendTitle(player,Text.of("§cThe curse consumes you.."), 20, 30, 20);
            if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been consumed by the curse.", player));
            }
            if (canChangeLives) {
                AdvancedDeathsManager.setPlayerLives(player, 1);
            }
        }
        else {
            PlayerUtils.sendTitle(player,Text.of("§cYou have failed."), 20, 30, 20);
            PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft","lastlife_boogeyman_fail")));
            if (BOOGEYMAN_ANNOUNCE_OUTCOME && sendMessage) {
                PlayerUtils.broadcastMessage(TextUtils.format("{}§7 failed to kill a player while being the §cBoogeyman§7. They have been dropped to their §cLast Life§7.", player));
            }
            if (canChangeLives) {
                livesManager.setPlayerLives(player, 1);
            }
        }
        return true;
    }

    public void playerLostAllLives(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        Boogeyman boogeyman = getBoogeyman(player);
        if (boogeyman == null) return;
        boogeyman.died = true;
    }

    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        if (!BOOGEYMAN_ENABLED) return;
        if (!boogeymanChosen) return;
        if (rolledPlayers.contains(player.getUuid())) return;
        if (livesManager.isDead(player)) return;
        if (boogeymen.size() >= BOOGEYMAN_AMOUNT_MAX) return;
        if (currentSession.statusNotStarted() || currentSession.statusFinished()) return;
        TaskScheduler.scheduleTask(40, () -> {
            player.sendMessage(Text.of("§cSince you were not present when the Boogeyman was being chosen, your chance to become the Boogeyman is now. Good luck!"));
            chooseBoogeymen(new ArrayList<>(List.of(player)), BoogeymanRollType.LATE_JOIN);
        });
    }

    public void onReload() {
        BOOGEYMAN_ENABLED = seasonConfig.BOOGEYMAN.get(seasonConfig);
        if (!BOOGEYMAN_ENABLED) {
            onDisabledBoogeyman();
        }
        BOOGEYMAN_CHANCE_MULTIPLIER = seasonConfig.BOOGEYMAN_CHANCE_MULTIPLIER.get(seasonConfig);
        BOOGEYMAN_AMOUNT_MIN = seasonConfig.BOOGEYMAN_MIN_AMOUNT.get(seasonConfig);
        BOOGEYMAN_AMOUNT_MAX = seasonConfig.BOOGEYMAN_MAX_AMOUNT.get(seasonConfig);
        BOOGEYMAN_ADVANCED_DEATHS = seasonConfig.BOOGEYMAN_ADVANCED_DEATHS.get(seasonConfig);
        BOOGEYMAN_MESSAGE = seasonConfig.BOOGEYMAN_MESSAGE.get(seasonConfig);
        BOOGEYMAN_IGNORE.clear();
        BOOGEYMAN_FORCE.clear();
        for (String name : seasonConfig.BOOGEYMAN_IGNORE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) BOOGEYMAN_IGNORE.add(name.toLowerCase());
        }
        for (String name : seasonConfig.BOOGEYMAN_FORCE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) BOOGEYMAN_FORCE.add(name.toLowerCase());
        }
        BOOGEYMAN_CHOOSE_MINUTE = seasonConfig.BOOGEYMAN_CHOOSE_MINUTE.get(seasonConfig);
        BOOGEYMAN_ANNOUNCE_OUTCOME = seasonConfig.BOOGEYMAN_ANNOUNCE_OUTCOME.get(seasonConfig);
        BOOGEYMAN_INFINITE = seasonConfig.BOOGEYMAN_INFINITE.get(seasonConfig);
        BOOGEYMAN_INFINITE_LAST_PICK = seasonConfig.BOOGEYMAN_INFINITE_LAST_PICK.get(seasonConfig);
        BOOGEYMAN_INFINITE_AUTO_FAIL = seasonConfig.BOOGEYMAN_INFINITE_AUTO_FAIL.get(seasonConfig);
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
    }

    public void infiniteBoogeymenTick(Boogeyman boogeyman) {
        if (!BOOGEYMAN_INFINITE) return;
        if (!currentSession.statusStarted()) return;
        if (!boogeyman.failed && !boogeyman.cured && !boogeyman.died) return;
        boogeymen.remove(boogeyman);
        TaskScheduler.scheduleTask(100, this::chooseNewBoogeyman);
    }

    public void autoFailTick(Boogeyman boogeyman) {
        if (!BOOGEYMAN_INFINITE) return;
        if (!currentSession.statusStarted()) return;
        if (boogeyman.failed) return;
        if (boogeyman.cured) return;
        if (boogeyman.died) return;

        int boogeymanTime = boogeyman.ticks / 20;
        int warningTime = (BOOGEYMAN_INFINITE_AUTO_FAIL - 5*60);
        if (boogeymanTime >= warningTime && warningTime >= 0) {
            if (!warningAutoFail.contains(boogeyman.uuid)) {
                ServerPlayerEntity player = boogeyman.getPlayer();
                if (player != null) {
                    warningAutoFail.add(boogeyman.uuid);
                    player.sendMessage(Text.of("§cYou only have 5 minutes left to kill someone as the Boogeyman before you fail!"));
                }
            }
        }
        else {
            warningAutoFail.remove(boogeyman.uuid);
        }

        if (boogeymanTime >= BOOGEYMAN_INFINITE_AUTO_FAIL) {
            ServerPlayerEntity player = boogeyman.getPlayer();
            if (player != null) {
                if (!playerFailBoogeyman(player, true)) {
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
        ServerPlayerEntity player = boogeyman.getPlayer();
        if (player == null) return;
        if (!player.isAlive()) return;
        if (AdvancedDeathsManager.hasQueuedDeath(player)) return;
        afterFailLogic(player);
    }

    public void afterFailLogic(ServerPlayerEntity player) {
        if (!afterFailedMessages()) return;
        afterFailedMessaged.add(player.getUuid());
        int delay = 20;
        if (!BOOGEYMAN_ADVANCED_DEATHS) {
            delay = 140;
        }
        if (currentSeason.getSeason() == Seasons.LIMITED_LIFE) {
            delay = 140;
        }

        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.sendTitle(player, Text.of("§cYour lives are taken..."), 20, 80, 20);
        });
        delay += 140;
        TaskScheduler.scheduleTask(delay, () -> {
            PlayerUtils.sendTitle(player, Text.of("§c...Now take theirs."), 20, 80, 20);
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
