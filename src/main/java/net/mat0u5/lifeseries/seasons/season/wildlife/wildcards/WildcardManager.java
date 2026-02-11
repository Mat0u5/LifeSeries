package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards;

import net.mat0u5.lifeseries.entity.triviabot.server.trivia.WildLifeTriviaHandler;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.AstralProjection;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Invisibility;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.PlayerDisguise;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.TimeControl;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import java.util.*;
import static net.mat0u5.lifeseries.Main.*;
//? if >= 1.21.2
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.CreakingPower;

public class WildcardManager {
    public static final Map<Wildcards, Wildcard> activeWildcards = new HashMap<>();
    public static final Random rnd = new Random();
    public static double ACTIVATE_WILDCARD_MINUTE = 2.5;
    public static boolean FINALE = false;

    public static void addSessionActions() {
        currentSession.addSessionActionIfTime(
                new SessionAction(Time.minutes(ACTIVATE_WILDCARD_MINUTE-2)) {
                    @Override
                    public void trigger() {
                        if (activeWildcards.isEmpty()) {
                            PlayerUtils.broadcastMessage(Component.literal("A Wildcard will be activated in 2 minutes!").withStyle(ChatFormatting.GRAY));
                        }
                    }
                }
        );
        currentSession.addSessionAction(
            new SessionAction(Time.minutes(ACTIVATE_WILDCARD_MINUTE), "Activate Wildcard") {
                @Override
                public void trigger() {
                    if (activeWildcards.isEmpty()) {
                        activateWildcards();
                    }
                }
            }
        );
    }

    public static Wildcards chosenWildcard = null;

    public static WildLife getSeason() {
        if (currentSeason instanceof WildLife wildLife) return wildLife;
        return null;
    }

    public static void chosenWildcard(Wildcards wildcard) {
        PlayerUtils.broadcastMessageToAdmins(TextUtils.format("The {} wildcard has been selected for this session.", wildcard));
        PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7Use the §f'/wildcard choose' §7 command if you want to change it."));
        WildcardManager.chosenWildcard = wildcard;
    }

    public static void chooseRandomWildcard() {
        if (chosenWildcard != null) {
            activeWildcards.put(chosenWildcard, chosenWildcard.getInstance());
            return;
        }
        Wildcards wildcard = Wildcards.getWildcards().get(rnd.nextInt(Wildcards.getWildcards().size()));
        activeWildcards.put(wildcard, wildcard.getInstance());
    }

    public static void onPlayerJoin(ServerPlayer player) {
        if (!isActiveWildcard(Wildcards.SIZE_SHIFTING)) {
            //? if > 1.20.3 {
            if (SizeShifting.getPlayerSize(player) != 1 && !WildLifeTriviaHandler.cursedGigantificationPlayers.contains(player.getUUID())) {
                SizeShifting.setPlayerSize(player, 1);
            }
            //?}
        }
        if (!isActiveWildcard(Wildcards.HUNGER)) {
            player.removeEffect(MobEffects.HUNGER);
        }
        if (!isActiveWildcard(Wildcards.TRIVIA)) {
            TriviaWildcard.resetPlayerOnBotSpawn(player);
        }
        TaskScheduler.scheduleTask(1, () -> {
            for (ServerPlayer onlinePlayer : PlayerUtils.getAllPlayers()) {
                Superpower power = SuperpowersWildcard.getSuperpowerInstance(onlinePlayer);
                if (power != null) {
                    if (power instanceof PlayerDisguise playerDisguise) playerDisguise.sendDisguisePacket();
                    if (power instanceof AstralProjection astralProjection) astralProjection.sendDisguisePacket();
                    if (power instanceof Invisibility invisibility) invisibility.sendInvisibilityPacket();
                }
            }
        });

        MorphManager.resetMorph(player);
    }

    public static void onPlayerFinishJoining(ServerPlayer player) {
        if (isActiveWildcard(Wildcards.SUPERPOWERS) && !SuperpowersWildcard.hasPower(player) && player.ls$isAlive()) {
            SuperpowersWildcard.rollRandomSuperpowers(new ArrayList<>(List.of(player)));
        }
    }

    public static void activateWildcards() {
        showDots();
        TaskScheduler.scheduleTask(90, () -> {
            if (activeWildcards.isEmpty()) {
                chooseRandomWildcard();
            }
            for (Wildcard wildcard : activeWildcards.values()) {
                if (wildcard.active) continue;
                wildcard.activate();
            }
            showCryptTitle("A wildcard is active!");
        });
        TaskScheduler.scheduleTask(92, NetworkHandlerServer::sendUpdatePackets);
    }

    public static void fadedWildcard() {
        PlayerUtils.broadcastMessage(Component.nullToEmpty("§7A Wildcard has faded..."));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.BEACON_DEACTIVATE);
    }

    public static void showDots() {
        List<ServerPlayer> players = PlayerUtils.getAllPlayers();
        PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
        PlayerUtils.sendTitleToPlayers(players, Component.literal("§a§l,"),0,40,0);
        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, Component.literal("§a§l, §e§l,"),0,40,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, Component.literal("§a§l, §e§l, §c§l,"),0,40,0);
        });
    }

    public static void showCryptTitle(String text) {
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ZOMBIE_VILLAGER_CURE, 0.2f, 1);
        String colorCrypt = "§r§6§l§k";
        String colorNormal = "§r§6§l";

        List<Integer> encryptedIndexes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            encryptedIndexes.add(i);
        }

        for (int i = 0; i < text.length(); i++) {
            if (!encryptedIndexes.isEmpty()) {
                encryptedIndexes.remove(rnd.nextInt(encryptedIndexes.size()));
            }

            StringBuilder result = new StringBuilder();
            for (int j = 0; j < text.length(); j++) {
                result.append(encryptedIndexes.contains(j) ? colorCrypt : colorNormal);
                result.append(text.charAt(j));
            }

            TaskScheduler.scheduleTask((i + 1) * 4, () -> PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal(String.valueOf(result)), 0, 30, 20));
        }
    }

    private static final List<String> allColorCodes = List.of("6","9","a","b","c","d","e");
    public static void showRainbowCryptTitle(String text) {
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.ZOMBIE_VILLAGER_CURE, 0.2f, 1);
        String colorCrypt = "§r§_§l§k";
        String colorNormal = "§r§_§l";

        List<Integer> encryptedIndexes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            encryptedIndexes.add(i);
        }

        for (int i = 0; i < text.length()+24; i++) {
            if (!encryptedIndexes.isEmpty()) {
                encryptedIndexes.remove(rnd.nextInt(encryptedIndexes.size()));
            }

            StringBuilder result = new StringBuilder();
            for (int j = 0; j < text.length(); j++) {
                String randomColor = allColorCodes.get(rnd.nextInt(allColorCodes.size()));
                result.append(encryptedIndexes.contains(j) ? colorCrypt.replace("_", randomColor) : colorNormal.replace("_", randomColor));
                result.append(text.charAt(j));
            }

            TaskScheduler.scheduleTask((i + 1) * 2, () -> PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), Component.literal(String.valueOf(result)), 0, 4, 4));
        }
    }

    public static void tick() {
        SuperpowersWildcard.onTick();
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.softTick();
            if (!wildcard.active) continue;
            wildcard.tick();
        }
        //? if > 1.20.3 {
        SizeShifting.resetSizesTick(isActiveWildcard(Wildcards.SIZE_SHIFTING));
        //?}
        if (server != null && server.getTickCount() % 200 == 0) {
            if (!isActiveWildcard(Wildcards.MOB_SWAP)) {
                MobSwap.killMobSwapMobs();
            }
            //? if >= 1.21.2 {
            CreakingPower.killUnassignedMobs();
            //?}
        }

        if (TimeControl.changedSpeedFor > 0) TimeControl.changedSpeedFor--;
        if (!isActiveWildcard(Wildcards.TIME_DILATION) && TimeControl.changedSpeedFor <= 0) {
            if (TimeDilation.getWorldSpeed() != 20) {
                TimeDilation.setWorldSpeed(20);
            }
        }

        for (UUID uuid : WildLifeTriviaHandler.cursedSliding) {
            ServerPlayer player = PlayerUtils.getPlayer(uuid);
            SimplePackets.CURSE_SLIDING.target(player).sendToClient(System.currentTimeMillis());
        }
    }

    public static void tickSessionOn() {
        for (Wildcard wildcard : activeWildcards.values()) {
            if (!wildcard.active) continue;
            wildcard.tickSessionOn();
        }
    }

    public static void onSessionStart() {
        if (chosenWildcard == null && activeWildcards.isEmpty()) {
            for (ServerPlayer player : PlayerUtils.getAdminPlayers()) {
                SimplePackets.SELECT_WILDCARDS.target(player).sendToClient();
            }
        }
    }

    public static void onSessionEnd() {
        FINALE = false;
        if (!activeWildcards.isEmpty()) {
            fadedWildcard();
        }
        if (isActiveWildcard(Wildcards.CALLBACK)) {
            if (activeWildcards.get(Wildcards.CALLBACK) instanceof Callback callback) {
                callback.deactivate();
                activeWildcards.remove(Wildcards.CALLBACK);
            }
        }
        for (Wildcard wildcard : activeWildcards.values()) {
            wildcard.deactivate();
        }
        activeWildcards.clear();
        DatapackIntegration.initWildcards();
        SuperpowersWildcard.resetAllSuperpowers();
        NetworkHandlerServer.sendUpdatePackets();
        chosenWildcard = null;
    }

    public static boolean isActiveWildcard(Wildcards wildcard) {
        return activeWildcards.containsKey(wildcard);
    }

    public static void onUseItem(ServerPlayer player) {
        Hunger.onUseItem(player);
    }
}
