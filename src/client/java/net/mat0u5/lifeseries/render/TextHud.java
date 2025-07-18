package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.config.ClientConfig;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.mat0u5.lifeseries.MainClient.clientConfig;

public class TextHud {
    private static long renderTicks = 0;
    public static void renderText(DrawContext context) {
        renderTicks++;
        MinecraftClient client = MinecraftClient.getInstance();
        int yPos = client.getWindow().getScaledHeight() - 5 - client.textRenderer.fontHeight;

        if (Main.DEBUG) {
            yPos += _renderSnailDistance(client, context, yPos);
        }

        yPos += renderGameNotBroken(client, context, yPos);
        yPos += renderSessionTimer(client, context, yPos);
        yPos += renderLimitedLifeTimer(client, context, yPos);
        yPos += renderMimicryTimer(client, context, yPos);
        yPos += renderSuperpowerCooldown(client, context, yPos);
        yPos += renderTriviaTimer(client, context, yPos);
    }

    public static int _renderSnailDistance(MinecraftClient client, DrawContext context, int y) {
        try {
            if (MainClient.snailPos == null) return 0;
            if (System.currentTimeMillis() - MainClient.snailPosTime > 2000) return 0;
            if (client.player == null) return 0;

            float distance = (float) client.player.getPos().distanceTo(MainClient.snailPos.toCenterPos());

            Text timerText = Text.literal(String.valueOf(Math.round(distance)));
            if (distance < 20) timerText = Text.literal("§c"+String.valueOf(Math.round(distance)));

            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;

            RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

            return -client.textRenderer.fontHeight-5;
        }catch(Exception e) {}
        return 0;
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


            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;
            RenderUtils.drawTextRight(context, client.textRenderer, text, x, y);

            return -client.textRenderer.fontHeight-10;
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
            RenderUtils.drawTextRight(context, client.textRenderer, text1, x, y);
            RenderUtils.drawTextRight(context, client.textRenderer, text0, x - ((client.textRenderer.getWidth(text1)-client.textRenderer.getWidth(text0))/2),  y - (client.textRenderer.fontHeight+1));

            return -client.textRenderer.fontHeight*2-15;
        }
    }

    public static int renderSessionTimer(MinecraftClient client, DrawContext context, int y) {
        if (System.currentTimeMillis()-MainClient.sessionTimeLastUpdated > 15000) return 0;
        if (MainClient.sessionTime == 0) return 0;

        MutableText timerText = Text.literal("");
        if (MainClient.sessionTime == -3) timerText = timerText.append(Text.of("§7Session has ended"));
        else if (MainClient.sessionTime == -2) timerText = timerText.append(Text.of("§7Session has been paused"));
        else if (MainClient.sessionTime == -1) timerText = timerText.append(Text.of("§7Session has not started"));
        else {
            long remainingTime = roundTime(MainClient.sessionTime) - System.currentTimeMillis();
            if (remainingTime < 0) timerText = timerText.append(Text.of("§7Session has ended"));
            else timerText = timerText.append(Text.of("§7Session " + OtherUtils.formatTimeMillis(remainingTime)));
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

        return -client.textRenderer.fontHeight-5;
    }

    public static int renderLimitedLifeTimer(MinecraftClient client, DrawContext context, int y) {
        if (System.currentTimeMillis()-MainClient.limitedLifeTimeLastUpdated > 15000) return 0;

        MutableText timerText = Text.literal("");
        if (MainClient.limitedLifeLives == -1) timerText = timerText.append(Text.of(MainClient.limitedLifeTimerColor+"0:00:00"));
        else {
            long remainingTime = roundTime(MainClient.limitedLifeLives*1000);

            if (remainingTime < 0) timerText = timerText.append(Text.of(MainClient.limitedLifeTimerColor+"0:00:00"));
            else timerText = timerText.append(Text.of(MainClient.limitedLifeTimerColor+ OtherUtils.formatTimeMillis(remainingTime)));
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

        return -client.textRenderer.fontHeight-5;
    }

    public static int renderTriviaTimer(MinecraftClient client, DrawContext context, int y) {
        if (!Trivia.isDoingTrivia()) return 0;
        if (client.currentScreen != null) return 0;

        long millisLeft = roundTime(Trivia.getEndTimestamp()) - System.currentTimeMillis();

        Text actualTimer = Text.of(OtherUtils.formatTimeMillis(millisLeft));
        Text timerText = Text.of("§7Trivia timer: ");

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        if (millisLeft <= 5_000) RenderUtils.drawTextRight(context, client.textRenderer, TextColors.RED, actualTimer, x, y);
        else if (millisLeft <= 30_000) RenderUtils.drawTextRight(context, client.textRenderer, TextColors.ORANGE, actualTimer, x, y);
        else RenderUtils.drawTextRight(context, client.textRenderer, TextColors.WHITE, actualTimer, x, y);

        RenderUtils.drawTextRight(context, client.textRenderer, timerText, x - client.textRenderer.getWidth(actualTimer), y);

        return -client.textRenderer.fontHeight-5;
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

        Text timerText = Text.of((keyPressed?"§c§n":"§7")+"Superpower cooldown:§f "+OtherUtils.formatTimeMillis(millisLeft));

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

        return -client.textRenderer.fontHeight-5;
    }

    public static int renderMimicryTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) return 0;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.MIMICRY_COOLDOWN_TIMESTAMP) return 0;
        long millisLeft = roundTime(MainClient.MIMICRY_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10000000) return 0;

        Text timerText = Text.of("§7Mimic power cooldown: §f"+OtherUtils.formatTimeMillis(millisLeft));

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        RenderUtils.drawTextRight(context, client.textRenderer, timerText, x, y);

        return -client.textRenderer.fontHeight-5;
    }

    public static long roundTime(long time) {
        return time - (time % 1000);
    }
}
