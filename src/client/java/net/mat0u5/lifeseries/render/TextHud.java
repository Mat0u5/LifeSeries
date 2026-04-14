package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.LifeSeriesClient;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextHud {
    public static void renderText(GuiGraphicsExtractor context) {
        Minecraft client = Minecraft.getInstance();
        //? if <= 26.1 {
        if (client.options.hideGui) return;
        //?} else {
        /*if (client.gui.hud.isHidden()) return;
        *///?}
        int yPos = client.getWindow().getGuiScaledHeight() - (5 + (int) Math.ceil((client.font.lineHeight) * LifeSeriesClient.TEXT_HUD_SCALE));

        if (!LifeSeries.modDisabled()) {
            yPos += renderGameNotBroken(client, context, yPos);
            yPos += renderSessionTimer(client, context, yPos);
            yPos += renderLimitedLifeTimer(client, context, yPos);
            yPos += renderMimicryTimer(client, context, yPos);
            yPos += renderSuperpowerCooldown(client, context, yPos);
            yPos += renderTriviaTimer(client, context, yPos);
        }

        yPos += renderSidetitle(client, context, yPos);
    }

    public static void tick() {
        if (sideTitleRemainTicks > 0) {
            sideTitleRemainTicks--;
        }
    }

    public static int sideTitleRemainTicks = 0;
    public static int renderSidetitle(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (LifeSeriesClient.sideTitle == null) return 0;
        if (LifeSeriesClient.sideTitle.getString().isEmpty()) return 0;
        if (sideTitleRemainTicks <= 0) return 0;

        return drawHudText(client, context, LifeSeriesClient.sideTitle, y);
    }

    public static int renderGameNotBroken(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (client.player == null) return 0;
        if (!ClientRenderer.isGameFullyFrozen) return 0;
        long currentMillis = System.currentTimeMillis();
        int guiScale = client.options.guiScale().get();
        if (guiScale <= 3 && guiScale != 0) {

            String textString = "Don't worry, the game is not broken ";
            if (currentMillis % 1500 <= 750) textString = "§7§n"+textString;
            else textString = "§7"+textString;

            if (currentMillis % 500 <= 250) textString += "/o/";
            else textString += "\\o\\";

            Component text = Component.literal(textString);

            return drawHudText(client, context, text, y) - 5;
        }
        else {
            String textString0 = "Don't worry,";
            String textString1 = "the game isn't broken ";

            if (currentMillis % 1500 <= 750) {
                textString0 = "§7§n"+textString0;
                textString1 = "§7§n"+textString1;
            }
            else {
                textString0 = "§7"+textString0;
                textString1 = "§7"+textString1;
            }

            if (currentMillis % 500 <= 250) textString1 += "/o/";
            else textString1 += "\\o\\";

            Component text0 = Component.literal(textString0);
            Component text1 = Component.literal(textString1);


            int screenWidth = client.getWindow().getGuiScaledWidth();
            int x = screenWidth - 5;

            int draw1 = drawHudText(client, context, text1, y);
            int draw2 = drawHudText(client, context, text0, x - ((client.font.width(text1)-client.font.width(text0))/2), y - (client.font.lineHeight+1));

            return draw1 + draw2 - 5;
        }
    }

    public static long lastSessionSeconds = 0;
    public static long sessionSeconds = -1;
    public static boolean sessionSecondChanged = true;
    public static int renderSessionTimer(Minecraft client, GuiGraphicsExtractor context, int y) {
        sessionSecondChanged = true;
        sessionSeconds = -1;
        if (!LifeSeriesClient.SESSION_TIMER) return 0;
        if (System.currentTimeMillis()- LifeSeriesClient.sessionTimeLastUpdated > 15000) return 0;
        if (LifeSeriesClient.sessionTime == SessionTimerStates.OFF.getValue()) return 0;

        MutableComponent timerText = Component.empty();
        if (LifeSeriesClient.sessionTime == SessionTimerStates.ENDED.getValue()) timerText = timerText.append(Component.nullToEmpty("§7Session has ended"));
        else if (LifeSeriesClient.sessionTime == SessionTimerStates.PAUSED.getValue()) timerText = timerText.append(Component.nullToEmpty("§7Session has been paused"));
        else if (LifeSeriesClient.sessionTime == SessionTimerStates.NOT_STARTED.getValue()) timerText = timerText.append(Component.nullToEmpty("§7Session has not started"));
        else {
            long remainingTime = roundTime(LifeSeriesClient.sessionTime) - System.currentTimeMillis();
            sessionSeconds = (int) Math.ceil(remainingTime / 1000.0);
            if (lastSessionSeconds != sessionSeconds) {
                lastSessionSeconds = sessionSeconds;
            }
            else {
                sessionSecondChanged = false;
            }

            if (remainingTime < 0) timerText = timerText.append(Component.nullToEmpty("§7Session has ended"));
            else timerText = timerText.append(TextUtils.formatLoosely("§7Session {}", Time.millis(remainingTime).formatLong()));
        }

        return drawHudText(client, context, timerText, y);
    }

    private static long limitedLifeTime = -1;
    public static int renderLimitedLifeTimer(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (LifeSeriesClient.clientCurrentSeason != Seasons.LIMITED_LIFE) return 0;
        if (System.currentTimeMillis()- LifeSeriesClient.limitedLifeTimeLastUpdated > 15000) return 0;

        MutableComponent timerText = Component.empty();
        if (sessionSecondChanged || LifeSeriesClient.sessionTime <= 0 || Math.abs(limitedLifeTime - LifeSeriesClient.limitedLifeLives) > 10) {
            limitedLifeTime = LifeSeriesClient.limitedLifeLives;
        }
        if (limitedLifeTime == -1) timerText = timerText.append(TextUtils.formatLoosely("{}0:00:00", LifeSeriesClient.limitedLifeTimerColor));
        else {
            long currentSeconds = limitedLifeTime;
            if (sessionSeconds != -1 && currentSeconds > 60) {
                long secondsDifference = (sessionSeconds % 60) - (currentSeconds % 60);
                if (Math.abs(secondsDifference) <= 5) {
                    currentSeconds += secondsDifference;
                }
            }
            long remainingTime = currentSeconds * 1000;

            if (remainingTime < 0) timerText = timerText.append(TextUtils.formatLoosely("{}0:00:00", LifeSeriesClient.limitedLifeTimerColor));
            else timerText = timerText.append(Component.nullToEmpty(LifeSeriesClient.limitedLifeTimerColor+ Time.millis(remainingTime).formatLong()));
        }

        return drawHudText(client, context, timerText, y);
    }

    private static int triviaTimer = -1;
    public static int renderTriviaTimer(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (!Trivia.isDoingTrivia()) return 0;
        if (LifeSeriesClient.clientCurrentSeason == Seasons.NICE_LIFE) return 0;

        if (sessionSecondChanged || LifeSeriesClient.sessionTime <= 0 || Math.abs(triviaTimer - Trivia.getRemainingSeconds()) >= 2) {
            triviaTimer = Trivia.getRemainingSeconds();
        }

        int secondsLeft = triviaTimer;

        Component actualTimer = Component.nullToEmpty(Time.seconds(secondsLeft).format());
        Component timerText = Component.nullToEmpty("§7Trivia timer: ");

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int x = screenWidth - 5;

        if (secondsLeft <= 5) drawHudText(client, context, TextColors.RED, actualTimer, x, y);
        else if (secondsLeft <= 30) drawHudText(client, context, TextColors.ORANGE, actualTimer, x, y);
        else drawHudText(client, context, TextColors.WHITE, actualTimer, x, y);

        return drawHudText(client, context, timerText, x - client.font.width(actualTimer), y);
    }

    public static long lastPressedSuperpowerKey = 0;
    public static int renderSuperpowerCooldown(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (ClientKeybinds.superpower != null && ClientKeybinds.superpower.isDown()) lastPressedSuperpowerKey = System.currentTimeMillis();

        if (LifeSeriesClient.SUPERPOWER_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= LifeSeriesClient.SUPERPOWER_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = roundTime(LifeSeriesClient.SUPERPOWER_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10000000) return 0;

        long pressedAgo = System.currentTimeMillis() - lastPressedSuperpowerKey;
        boolean keyPressed = pressedAgo < 500;
        if (pressedAgo > 6000) return 0;

        Component timerText = TextUtils.formatLoosely("{}Superpower cooldown:§f {}", (keyPressed?"§c§n":"§7") , Time.millis(millisLeft).format());

        return drawHudText(client, context, timerText, y);
    }

    public static int renderMimicryTimer(Minecraft client, GuiGraphicsExtractor context, int y) {
        if (LifeSeriesClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= LifeSeriesClient.MIMICRY_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = roundTime(LifeSeriesClient.MIMICRY_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10000000) return 0;

        Component timerText = TextUtils.formatLoosely("§7Mimic power cooldown: §f{}", Time.millis(millisLeft).format());

        return drawHudText(client, context, timerText, y);
    }

    public static int drawHudText(Minecraft client, GuiGraphicsExtractor context, Component text, int y) {
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int x = screenWidth - 5;
        return drawHudText(client, context, text, x, y);
    }

    public static int drawHudText(Minecraft client, GuiGraphicsExtractor context, Component text, int x, int y) {
        return drawHudText(client, context, TextColors.DEFAULT, text, x, y);
    }

    public static int drawHudText(Minecraft client, GuiGraphicsExtractor context, int color, Component text, int x, int y) {
        if (LifeSeriesClient.TEXT_HUD_SCALE != 1) {
            float scaleX = (float) LifeSeriesClient.TEXT_HUD_SCALE;
            float scaleY = (float) LifeSeriesClient.TEXT_HUD_SCALE;
            RenderUtils.text(text, x, y).anchorRight().colored(color).scaled(scaleX, scaleY).withShadow().render(context, client.font);
            return -((int) Math.ceil((client.font.lineHeight) * LifeSeriesClient.TEXT_HUD_SCALE) + 5);
        }
        RenderUtils.text(text, x, y).anchorRight().colored(color).withShadow().render(context, client.font);
        return -client.font.lineHeight -5;
    }

    public static long roundTime(long time) {
        return time - (time % 1000);
    }
}
