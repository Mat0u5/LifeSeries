package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.events.Events;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.mat0u5.lifeseries.Main.server;

public class OtherUtils {
    private static final Random rnd = new Random();
    private static HashMap<Text, Integer> cooldown = new HashMap<>();

    public static void broadcastMessage(Text message) {
        broadcastMessage(message, 1);
    }

    public static void broadcastMessageToAdmins(Text message) {
        broadcastMessageToAdmins(message, 1);
    }

    public static void log(String string) {
        Text message = Text.of(string);
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.sendMessage(message, false);
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
        System.out.println("String length: " + str.length());

        // Print each character as its code point
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            System.out.printf("Character at %d: '%c' (Unicode: U+%04X)%n", i, c, (int)c);
        }
    }

    public static void broadcastMessage(Text message, int cooldownTicks) {
        if (cooldown.containsKey(message)) return;
        cooldown.put(message, cooldownTicks);

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            player.sendMessage(message, false);
        }
    }

    public static void broadcastMessageToAdmins(Text message, int cooldownTicks) {
        if (cooldown.containsKey(message)) return;
        cooldown.put(message, cooldownTicks);

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            if (!PermissionManager.isAdmin(player)) continue;
            player.sendMessage(message, false);
        }
        Main.LOGGER.info(message.getString());
    }

    public static void onTick() {
        if (cooldown.isEmpty()) return;
        HashMap<Text, Integer> newCooldowns = new HashMap<>();
        for (Map.Entry<Text, Integer> entry : cooldown.entrySet()) {
            Text key = entry.getKey();
            Integer value = entry.getValue();
            value--;
            if (value > 0) {
                newCooldowns.put(key, value);
            }
        }
        cooldown = newCooldowns;
    }

    public static String formatTime(int totalTicks) {
        int hours = totalTicks / 72000;
        int minutes = (totalTicks % 72000) / 1200;
        int seconds = (totalTicks % 1200) / 20;

        return hours+":"+ formatTimeNumber(minutes)+":"+ formatTimeNumber(seconds);
    }

    public static String formatTimeMillis(long millis) {
        long totalSeconds = (long) Math.ceil(millis / 1000.0);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = (totalSeconds % 60);
        if (hours == 0) {
            return formatTimeNumber(minutes)+":"+ formatTimeNumber(seconds);
        }

        return hours+":"+ formatTimeNumber(minutes)+":"+ formatTimeNumber(seconds);
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

    public static int secondsToTicks(int secs) {
        return secs*20;
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
    public static int parseTimeFromArgument(String time) {
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return -1; // Invalid time format
        }

        int hours = parseInt(matcher.group(1));
        int minutes = parseInt(matcher.group(2));
        int seconds = parseInt(matcher.group(3));

        return (hours * 3600 + minutes * 60 + seconds) * 20;
    }

    public static int parseTimeSecondsFromArgument(String time) {
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.matches()) {
            return -1; // Invalid time format
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
        if (server == null) return;
        CommandManager manager = server.getCommandManager();
        ServerCommandSource commandSource = server.getCommandSource().withSilent();
        manager.executeWithPrefix(commandSource, command);
    }

    public static void throwError(String error) {
        broadcastMessageToAdmins(Text.of("§c"+error));
        Main.LOGGER.error(error);
    }

    public static SoundEvent getRandomSound(String name, int from, int to) {
        if (to > from) {
            int index = rnd.nextInt(from, to + 1);
            name += index;
        }
        return SoundEvent.of(Identifier.of("minecraft", name));
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
    public static void reloadServer() {
        OtherUtils.executeCommand("reload");
    }
}
