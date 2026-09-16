package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.events.Events;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.util.other.Time;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.mat0u5.matlib.MatLib.server;

//? if >= 1.20.3
import net.minecraft.server.ServerTickRateManager;

public class OtherUtils extends net.mat0u5.matlib.util.other.OtherUtils {
    private static final Random rnd = new Random();

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
    public static Time parseTimeFromArgument(String time) {
        try {
            int totalSeconds = Integer.parseInt(time);
            return Time.seconds(totalSeconds);
        }catch(Exception e) {}

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
    public static String formatTimeArgumentFromSeconds(int totalSeconds) {
        if (totalSeconds == 0) {
            return "0";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString();
    }

    public static void throwError(String error) {
        PlayerUtils.broadcastMessageToAdmins(Component.nullToEmpty("§c"+error));
        LifeSeries.LOGGER.error(error);
    }

    public static SoundEvent getRandomSound(String name, int from, int to) {
        if (to > from) {
            int index = rnd.nextInt(from, to + 1);
            name += index;
        }
        return SoundEvent.createVariableRangeEvent(IdentifierHelper.lifeseries(name));
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
                LifeSeries.LOGGER.error("Detected and prevented possible reload loop!");
                return;
            }
            reloads.add(System.currentTimeMillis());
            executeCommand("reload");
        } catch (Exception e) {
            LifeSeries.LOGGER.error("Error reloading server", e);
        }
    }

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
            PlayerUtils.broadcastMessageToAdmins(ModifiableText.TICK_FREEZE.get());
        }
        else {
            PlayerUtils.broadcastMessageToAdmins(ModifiableText.TICK_UNFREEZE.get());
        }
        //?}
    }

}
