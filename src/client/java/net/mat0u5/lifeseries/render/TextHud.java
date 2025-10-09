package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.mixin.client.InGameHudAccessor;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextHud {
    public static void renderText(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;
        int yPos = client.getWindow().getScaledHeight() - (5 + (int) Math.ceil((client.textRenderer.fontHeight) * MainClient.TEXT_HUD_SCALE));

        yPos += renderGameNotBroken(client, context, yPos);
        yPos += renderSessionTimer(client, context, yPos);
        yPos += renderLimitedLifeTimer(client, context, yPos);
        yPos += renderMimicryTimer(client, context, yPos);
        yPos += renderSuperpowerCooldown(client, context, yPos);
        yPos += renderTriviaTimer(client, context, yPos);

        yPos += renderSidetitle(client, context, yPos);
    }

    public static void tick() {
        if (sideTitleRemainTicks > 0) {
            sideTitleRemainTicks--;
        }
    }

    public static int sideTitleRemainTicks = 0;
    public static int renderSidetitle(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.sideTitle == null) return 0;
        if (MainClient.sideTitle.getString().isEmpty()) return 0;
        if (sideTitleRemainTicks <= 0) return 0;

        return drawHudText(client, context, MainClient.sideTitle, y);
    }

    public static int renderGameNotBroken(MinecraftClient client, DrawContext context, int y) {
        if (client.player == null) return 0;
        if (!ClientRenderer.isGameFullyFrozen) return 0;
        long currentMillis = System.currentTimeMillis();
        int guiScale = client.options.getGuiScale().getValue();
        if (guiScale <= 3 && guiScale != 0) {

            String textString = "Don't worry, the game is not broken ";
            if (currentMillis % 1500 <= 750) textString = "§7§n"+textString;
            else textString = "§7"+textString;

            if (currentMillis % 500 <= 250) textString += "/o/";
            else textString += "\\o\\";

            Text text = Text.literal(textString);

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

            Text text0 = Text.literal(textString0);
            Text text1 = Text.literal(textString1);


            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;

            int draw1 = drawHudText(client, context, text1, y);
            int draw2 = drawHudText(client, context, text0, x - ((client.textRenderer.getWidth(text1)-client.textRenderer.getWidth(text0))/2), y - (client.textRenderer.fontHeight+1));

            return draw1 + draw2 - 5;
        }
    }

    public static long lastSessionSeconds = 0;
    public static long sessionSeconds = -1;
    public static boolean sessionSecondChanged = true;
    public static int renderSessionTimer(MinecraftClient client, DrawContext context, int y) {
        sessionSecondChanged = true;
        sessionSeconds = -1;
        if (!MainClient.SESSION_TIMER) return 0;
        if (System.currentTimeMillis()-MainClient.sessionTimeLastUpdated > 15000) return 0;
        if (MainClient.sessionTime == SessionTimerStates.OFF.getValue()) return 0;

        MutableText timerText = Text.empty();
        if (MainClient.sessionTime == SessionTimerStates.ENDED.getValue()) timerText = timerText.append(Text.of("§7Session has ended"));
        else if (MainClient.sessionTime == SessionTimerStates.PAUSED.getValue()) timerText = timerText.append(Text.of("§7Session has been paused"));
        else if (MainClient.sessionTime == SessionTimerStates.NOT_STARTED.getValue()) timerText = timerText.append(Text.of("§7Session has not started"));
        else {
            long remainingTime = roundTime(MainClient.sessionTime) - System.currentTimeMillis();
            sessionSeconds = (int) Math.ceil(remainingTime / 1000.0);
            if (lastSessionSeconds != sessionSeconds) {
                lastSessionSeconds = sessionSeconds;
            }
            else {
                sessionSecondChanged = false;
            }

            if (remainingTime < 0) timerText = timerText.append(Text.of("§7Session has ended"));
            else timerText = timerText.append(TextUtils.formatLoosely("§7Session {}", OtherUtils.formatTimeMillis(remainingTime)));
        }

        return drawHudText(client, context, timerText, y);
    }

    private static long limitedLifeTime = -1;
    public static int renderLimitedLifeTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) return 0;
        if (System.currentTimeMillis()-MainClient.limitedLifeTimeLastUpdated > 15000) return 0;

        MutableText timerText = Text.empty();
        if (sessionSecondChanged || MainClient.sessionTime <= 0 || Math.abs(limitedLifeTime - MainClient.limitedLifeLives) > 10) {
            limitedLifeTime = MainClient.limitedLifeLives;
        }
        if (limitedLifeTime == -1) timerText = timerText.append(TextUtils.formatLoosely("{}0:00:00", MainClient.limitedLifeTimerColor));
        else {
            long currentSeconds = limitedLifeTime;
            if (sessionSeconds != -1 && currentSeconds > 60) {
                long secondsDifference = (sessionSeconds % 60) - (currentSeconds % 60);
                if (Math.abs(secondsDifference) <= 5) {
                    currentSeconds += secondsDifference;
                }
            }
            long remainingTime = currentSeconds * 1000;

            if (remainingTime < 0) timerText = timerText.append(TextUtils.formatLoosely("{}0:00:00", MainClient.limitedLifeTimerColor));
            else timerText = timerText.append(Text.of(MainClient.limitedLifeTimerColor+ OtherUtils.formatTimeMillis(remainingTime)));
        }

        return drawHudText(client, context, timerText, y);
    }

    public static int renderTriviaTimer(MinecraftClient client, DrawContext context, int y) {
        if (!Trivia.isDoingTrivia()) return 0;
        if (client.currentScreen != null) return 0;

        long millisLeft = roundTime(Trivia.getEndTimestamp()) - System.currentTimeMillis();

        Text actualTimer = Text.of(OtherUtils.formatTimeMillis(millisLeft));
        Text timerText = Text.of("§7Trivia timer: ");

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        if (millisLeft <= 5_000) drawHudText(client, context, TextColors.RED, actualTimer, x, y);
        else if (millisLeft <= 30_000) drawHudText(client, context, TextColors.ORANGE, actualTimer, x, y);
        else drawHudText(client, context, TextColors.WHITE, actualTimer, x, y);

        return drawHudText(client, context, timerText, x - client.textRenderer.getWidth(actualTimer), y);
    }

    private static long lastPressed = 0;
    public static int renderSuperpowerCooldown(MinecraftClient client, DrawContext context, int y) {
        if (ClientKeybinds.superpower != null && ClientKeybinds.superpower.isPressed()) lastPressed = System.currentTimeMillis();

        if (MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = roundTime(MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10000000) return 0;

        long pressedAgo = System.currentTimeMillis() - lastPressed;
        boolean keyPressed = pressedAgo < 500;
        if (pressedAgo > 6000) return 0;

        Text timerText = TextUtils.formatLoosely("{}Superpower cooldown:§f {}", (keyPressed?"§c§n":"§7") , OtherUtils.formatTimeMillis(millisLeft));

        return drawHudText(client, context, timerText, y);
    }

    public static int renderMimicryTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.MIMICRY_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = roundTime(MainClient.MIMICRY_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10000000) return 0;

        Text timerText = TextUtils.formatLoosely("§7Mimic power cooldown: §f{}", OtherUtils.formatTimeMillis(millisLeft));

        return drawHudText(client, context, timerText, y);
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, Text text, int y) {
        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        return drawHudText(client, context, text, x, y);
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, Text text, int x, int y) {
        return drawHudText(client, context, TextColors.DEFAULT, text, x, y);
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, int color, Text text, int x, int y) {
        if (MainClient.TEXT_HUD_SCALE != 1) {
            RenderUtils.drawTextRightScaled(context, client.textRenderer, color, text, x, y, (float) MainClient.TEXT_HUD_SCALE, (float) MainClient.TEXT_HUD_SCALE, true);
            return -((int) Math.ceil((client.textRenderer.fontHeight) * MainClient.TEXT_HUD_SCALE) + 5);
        }
        RenderUtils.drawTextRight(context, client.textRenderer, color, text, x, y, true);
        return -client.textRenderer.fontHeight -5;
    }

    public static long roundTime(long time) {
        return time - (time % 1000);
    }
}
