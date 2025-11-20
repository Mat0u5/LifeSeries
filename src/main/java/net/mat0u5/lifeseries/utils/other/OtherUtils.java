package net.mat0u5.lifeseries.utils.other;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.mat0u5.lifeseries.Main.server;

//? if <= 1.21.9
import net.minecraft.world.level.GameRules;
//? if > 1.21.9
/*import net.minecraft.world.level.gamerules.GameRule;*/

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
        if (Main.isClient()) {
            Main.LOGGER.info(string);
        }
    }

    public static void debugString(String str) {
        Main.LOGGER.info(TextUtils.formatString("String length: {}", str.length()));

        // Print each character as its code point
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Main.LOGGER.info("Character at %d: '%c' (Unicode: U+%04X)%n", i, c, (int)c);
        }
    }

    public static String formatTime(int totalTicks) {
        int hours = totalTicks / 72000;
        int minutes = (totalTicks % 72000) / 1200;
        int seconds = (totalTicks % 1200) / 20;
        return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    public static String formatTimeMillis(long millis) {
        long totalSeconds = (long) Math.ceil(millis / 1000.0);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = (totalSeconds % 60);
        if (hours == 0) {
            return TextUtils.formatString("{}:{}", formatTimeNumber(minutes), formatTimeNumber(seconds));
        }

        return TextUtils.formatString("{}:{}:{}", hours, formatTimeNumber(minutes), formatTimeNumber(seconds));
    }

    public static String formatTimeNumber(int time) {
        String value = String.valueOf(time);
        while (value.length() < 2) value = "0" + value;
        return value;
    }

    public static String formatTimeNumber(long time) {
        String value = String.valueOf(time);
        while (value.length() < 2) value = "0" + value;
        return value;
    }

    public static String formatSecondsToReadable(int seconds) {
        boolean isNegative = seconds < 0;
        seconds = Math.abs(seconds);

        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        int secs = remainingSeconds % 60;

        if (hours > 0 && minutes == 0 && secs == 0) {
            return (isNegative ? "-" : "+") + hours + (hours == 1 ? " hour" : " hours");
        } else if (hours == 0 && minutes > 0 && secs == 0) {
            return (isNegative ? "-" : "+") + minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours == 0 && minutes == 0 && secs > 0) {
            return (isNegative ? "-" : "+") + secs + (secs == 1 ? " second" : " seconds");
        } else {
            return String.format("%s%d:%02d:%02d", isNegative ? "-" : "+", hours, minutes, secs);
        }
    }

    public static int minutesToTicks(int mins) {
        return mins*60*20;
    }
    public static int minutesToTicks(double mins) {
        double ticks = mins*60*20;
        return (int) ticks;
    }

    public static int secondsToTicks(int secs) {
        return secs*20;
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
    public static Integer parseTimeFromArgument(String time) {
        time = time.replaceAll(" ", "").replaceAll("\"", "");
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return null; // Invalid time format
        }

        int hours = parseInt(matcher.group(1));
        int minutes = parseInt(matcher.group(2));
        int seconds = parseInt(matcher.group(3));

        return (hours * 3600 + minutes * 60 + seconds) * 20;
    }

    public static Integer parseTimeSecondsFromArgument(String time) {
        time = time.replaceAll(" ", "").replaceAll("\"", "");
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return null; // Invalid time format
        }

        int hours = parseInt(matcher.group(1));
        int minutes = parseInt(matcher.group(2));
        int seconds = parseInt(matcher.group(3));

        return (hours * 3600 + minutes * 60 + seconds);
    }

    private static int parseInt(String value) {
        return value == null ? 0 : Integer.parseInt(value);
    }

    public static void executeCommand(String command) {
        OtherUtils.log("Executing: " + command);
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

    private static long[] reloads = {System.currentTimeMillis(),System.currentTimeMillis(),System.currentTimeMillis()};
    public static void reloadServer() {
        try {
            Arrays.sort(reloads);
            int inInterval = 0;
            for (int i = 0; i < 3; i++) {
                if (System.currentTimeMillis() - OtherUtils.reloads[i] < 5000) {
                    inInterval++;
                }
            }

            if (inInterval >= 3) {
                Main.LOGGER.error("Detected and prevented possible reload loop!");
                return;
            }
            reloads[0] = System.currentTimeMillis();
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
        return profile.getId();
         //?} else {
        /*return profile.id();
        *///?}
    }

    public static String profileName(GameProfile profile) {
        //? if <= 1.21.6 {
        return profile.getName();
         //?} else {
        /*return profile.name();
        *///?}
    }

    //? if <= 1.21.9 {
    public static boolean getBooleanGameRule(ServerLevel level, GameRules.Key<GameRules.BooleanValue> gamerule) {
        return level.getGameRules().getBoolean(gamerule);
    }
    public static <T extends GameRules.Value<T>> void setBooleanGameRule(ServerLevel level, GameRules.Key<GameRules.BooleanValue> gamerule, boolean value) {
        level.getGameRules().getRule(gamerule).set(value, server);
    }
    //?} else {
    /*public static boolean getBooleanGameRule(ServerLevel level, GameRule<?> gamerule) {
        if (level.getGameRules().get(gamerule) instanceof Boolean bool) {
            return bool;
        }
        return false;
    }
    public static void setBooleanGameRule(ServerLevel level, GameRule<Boolean> gamerule, Boolean value) {
        level.getGameRules().set(gamerule, value, server);
    }
    *///?}

    public static void setFreezeGame(boolean frozen) {
        if (server == null) return;
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
    }

    public static boolean isNumber(String text) {
        try {
            int num = Integer.parseInt(text);
            return true;
        } catch (Exception e) {}
        return false;
    }
}
