package net.mat0u5.lifeseries.utils.other;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.mat0u5.lifeseries.Main.server;

//? if >= 1.20.3
import net.minecraft.server.ServerTickRateManager;

//? if <= 1.21.9
//import net.minecraft.world.level.GameRules;
//? if > 1.21.9
import net.minecraft.world.level.gamerules.GameRule;

public class OtherUtils {
    private static final Random rnd = new Random();

    public static void log(Component message) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.displayClientMessage(message, false);
        }
        Main.LOGGER.info(message.getString());
    }

    public static void log(String string) {
        Component message = Component.nullToEmpty(string);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.displayClientMessage(message, false);
        }
        Main.LOGGER.info(string);
    }

    public static void logConsole(String string) {
        Main.LOGGER.info(string);
    }

    public static void logIfClient(String string) {
        if (Main.hasClient()) {
            Main.LOGGER.info(string);
        }
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
    public static Time parseTimeFromArgument(String time) {
        time = time.replaceAll(" ", "").replaceAll("\"", "");
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return null; // Invalid time format
        }

        try {
            int hours = parseInt(matcher.group(1));
            int minutes = parseInt(matcher.group(2));
            int seconds = parseInt(matcher.group(3));
            int totalSeconds = Math.addExact(Math.addExact(Math.multiplyExact(hours, 3600), Math.multiplyExact(minutes, 60)), seconds);
            return Time.seconds(totalSeconds);
        }catch(Exception e) {}
        return null;
    }

    private static int parseInt(String value) {
        return value == null ? 0 : Integer.parseInt(value);
    }

    public static void executeCommand(String command) {
        try {
            if (server == null) return;
            Commands manager = server.getCommands();
            CommandSourceStack commandSource = server.createCommandSourceStack().withSuppressedOutput();
            manager.performPrefixedCommand(commandSource, command);
        } catch (Exception e) {
            Main.LOGGER.error("Error executing command: " + command, e);
        }
    }

    public static void throwError(String error) {
        PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§c"+error));
        Main.LOGGER.error(error);
    }

    public static SoundEvent getRandomSound(String name, int from, int to) {
        if (to > from) {
            int index = rnd.nextInt(from, to + 1);
            name += index;
        }
        return SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla(name));
    }

    public static String getTimeAndDate() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    public static void reloadServerNoUpdate() {
        Events.skipNextTickReload = true;
        reloadServer();
    }

    private static List<Long> reloads = new ArrayList<>();
    public static void reloadServer() {
        try {
            int inInterval = 0;
            if (reloads.size() >= 3) {
                int size = reloads.size();
                if (System.currentTimeMillis() - reloads.get(size-1) < 5000) inInterval++;
                if (System.currentTimeMillis() - reloads.get(size-2) < 5000) inInterval++;
                if (System.currentTimeMillis() - reloads.get(size-3) < 5000) inInterval++;
            }

            if (inInterval >= 3) {
                Main.LOGGER.error("Detected and prevented possible reload loop!");
                return;
            }
            reloads.add(System.currentTimeMillis());
            OtherUtils.executeCommand("reload");
        } catch (Exception e) {
            Main.LOGGER.error("Error reloading server", e);
        }
    }

    public static void sendCommandFeedback(CommandSourceStack source, Component text) {
        if (source == null || text == null) return;
        source.sendSuccess(() -> text, true);
    }

    public static void sendCommandFeedbackQuiet(CommandSourceStack source, Component text) {
        if (source == null || text == null) return;
        source.sendSuccess(() -> text, false);
    }

    public static UUID profileId(GameProfile profile) {
        //? if <= 1.21.6 {
        /*return profile.getId();
         *///?} else {
        return profile.id();
        //?}
    }

    public static String profileName(GameProfile profile) {
        //? if <= 1.21.6 {
        /*return profile.getName();
         *///?} else {
        return profile.name();
        //?}
    }

    public static PropertyMap profileProperties(GameProfile profile) {
        //? if <= 1.21.6 {
        /*return profile.getProperties();
         *///?} else {
        return profile.properties();
        //?}
    }

    //? if <= 1.21.9 {
    /*public static boolean getBooleanGameRule(ServerLevel level, GameRules.Key<GameRules.BooleanValue> gamerule) {
        return level.getGameRules().getBoolean(gamerule);
    }
    public static <T extends GameRules.Value<T>> void setBooleanGameRule(ServerLevel level, GameRules.Key<GameRules.BooleanValue> gamerule, boolean value) {
        level.getGameRules().getRule(gamerule).set(value, server);
    }
    *///?} else {
    public static boolean getBooleanGameRule(ServerLevel level, GameRule<?> gamerule) {
        if (level.getGameRules().get(gamerule) instanceof Boolean bool) {
            return bool;
        }
        return false;
    }
    public static void setBooleanGameRule(ServerLevel level, GameRule<Boolean> gamerule, Boolean value) {
        level.getGameRules().set(gamerule, value, server);
    }
    //?}

    public static void setFreezeGame(boolean frozen) {
        if (server == null) return;
        //? if >= 1.20.3 {
        ServerTickRateManager serverTickRateManager = server.tickRateManager();

        if (serverTickRateManager.isFrozen() == frozen)  return;

        if (frozen) {
            if (serverTickRateManager.isSprinting()) {
                serverTickRateManager.stopSprinting();
            }

            if (serverTickRateManager.isSteppingForward()) {
                serverTickRateManager.stopStepping();
            }
        }

        serverTickRateManager.setFrozen(frozen);

        if (frozen) {
            PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7The game is frozen"));
        }
        else {
            PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§7The game is no longer frozen."));
        }
        //?}
    }

    public static boolean isNumber(String text) {
        try {
            int num = Integer.parseInt(text);
            return true;
        } catch (Exception e) {}
        try {
            double num = Double.parseDouble(text);
            return true;
        } catch (Exception e) {}
        return false;
    }

    public static double clamp(double value, double min, double max) {
        if (!(min < max)) {
            if (Double.isNaN(min)) {
                throw new IllegalArgumentException("min is NaN");
            }
            if (Double.isNaN(max)) {
                throw new IllegalArgumentException("max is NaN");
            }
            if (Double.compare(min, max) > 0) {
                throw new IllegalArgumentException(min + " > " + max);
            }
        }
        return Math.min(max, Math.max(value, min));
    }

    public static float clamp(float value, float min, float max) {
        if (!(min < max)) {
            if (Double.isNaN(min)) {
                throw new IllegalArgumentException("min is NaN");
            }
            if (Double.isNaN(max)) {
                throw new IllegalArgumentException("max is NaN");
            }
            if (Double.compare(min, max) > 0) {
                throw new IllegalArgumentException(min + " > " + max);
            }
        }
        return Math.min(max, Math.max(value, min));
    }

    public static int clamp(int value, int min, int max) {
        if (!(min < max)) {
            if (Double.isNaN(min)) {
                throw new IllegalArgumentException("min is NaN");
            }
            if (Double.isNaN(max)) {
                throw new IllegalArgumentException("max is NaN");
            }
            if (Double.compare(min, max) > 0) {
                throw new IllegalArgumentException(min + " > " + max);
            }
        }
        return Math.min(max, Math.max(value, min));
    }
}
