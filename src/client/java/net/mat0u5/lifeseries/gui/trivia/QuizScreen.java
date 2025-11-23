package net.mat0u5.lifeseries.gui.trivia;

import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

public class QuizScreen extends DefaultScreen {

    private static final int[] ANSWER_COLORS = {
            TextColors.PASTEL_BLUE, TextColors.PASTEL_ORANGE, TextColors.PASTEL_LIME, TextColors.PASTEL_YELLOW, TextColors.PASTEL_RED
    };

    private final List<List<FormattedCharSequence>> answers = new ArrayList<>();
    private String difficulty = "Difficulty: null";
    private int timerSeconds = 120;
    private final List<Rectangle> answerRects = new ArrayList<>();

    public QuizScreen() {
        super(Component.literal("Quiz Screen"));
    }

    @Override
    protected void init() {
        super.init();
        timerSeconds = Trivia.getRemainingSeconds();

        int fifth3 = startX + (BG_WIDTH / 5) * 3;
        int answersStartX = fifth3 + 15;
        int answersStopX = endX - 15;

        int maxWidth = answersStopX - answersStartX;

        int currentYPos = startY + 30;
        int gap = 8;
        answers.clear();
        answerRects.clear();
        for (int i = 0; i < Trivia.answers.size(); i++) {
            char answerIndex = (char) (i+65);
            MutableComponent label = TextUtils.format("{}: ", answerIndex).withStyle(ChatFormatting.BOLD);
            MutableComponent answerText = Component.literal(Trivia.answers.get(i));
            answerText.setStyle(answerText.getStyle().withBold(false));
            Component text = label.append(answerText);
            List<FormattedCharSequence> answer = this.font.split(text, maxWidth);
            answers.add(answer);
            int answerBoxHeight = this.font.lineHeight * answer.size()+2;
            int answerBoxWidth = 0;
            for (FormattedCharSequence line : answer) {
                int lineWidth = this.font.width(line);
                if (lineWidth > answerBoxWidth) answerBoxWidth = lineWidth;
            }
            answerBoxWidth += 2;

            Rectangle rect = new Rectangle(answersStartX, currentYPos, answerBoxWidth, answerBoxHeight);
            answerRects.add(rect);
            currentYPos += answerBoxHeight + gap;
        }
        switch (Trivia.difficulty) {
            case 1:
                difficulty = "Difficulty: Easy";
                break;
            case 2:
                difficulty = "Difficulty: Medium";
                break;
            case 3:
                difficulty = "Difficulty: Hard";
                break;
            default:
                difficulty = "Difficulty: null";
        }
    }

    @Override
    public void tick() {
        super.tick();
        timerSeconds = Trivia.getRemainingSeconds();
        if (timerSeconds <= 0) {
            this.onClose();
        }
    }

    @Override
    //? if <= 1.21.6 {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
    //?} else {
    /*public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) { // Left-click
    *///?}
            for (int i = 0; i < answerRects.size(); i++) {
                if (answerRects.get(i).contains(mouseX, mouseY)) {
                    if (this.minecraft != null) this.minecraft.setScreen(new ConfirmQuizAnswerScreen(this, i));
                    return true;
                }
            }
        }
        //? if <= 1.21.6 {
        return super.mouseClicked(mouseX, mouseY, button);
        //?} else {
        /*return super.mouseClicked(click, doubled);
        *///?}
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        // X
        int fifth1 = startX + (BG_WIDTH / 5);
        int fifth2 = startX + (BG_WIDTH / 5) * 2;
        int fifth4 = startX + (BG_WIDTH / 5) * 4;
        int questionX = startX + 10;
        int questionWidth = (fifth2-10) - questionX;

        // Y
        int minY = startY + 9;
        int maxY = endY - 23;
        int questionY = startY + 30;

        /*
        testDrawX(context, 0);
        testDrawX(context, this.width-1);
        testDrawY(context, 0);
        testDrawY(context, this.height-1);
        testDrawX(context, startX);
        testDrawY(context, startY);
        testDrawX(context, endX);
        testDrawY(context, endY);
        testDrawX(context, fifth1);
        testDrawX(context, fifth2);
        testDrawX(context, fifth3);
        testDrawX(context, fifth4);
        */

        // Timer
        long minutes = timerSeconds / 60;
        long seconds = timerSeconds - minutes * 60;
        String secondsStr = String.valueOf(seconds);
        String minutesStr = String.valueOf(minutes);
        while (secondsStr.length() < 2) secondsStr = "0" + secondsStr;
        while (minutesStr.length() < 2) minutesStr = "0" + minutesStr;

        Component timerText = TextUtils.format("{}:{}", minutesStr, secondsStr);
        if (timerSeconds <= 5) {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().colored(TextColors.RED).render(context, this.font);
        }
        else if (timerSeconds <= 30) {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().colored(TextColors.ORANGE).render(context, this.font);
        }
        else {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().render(context, this.font);
        }

        // Difficulty
        RenderUtils.text(difficulty, centerX, maxY).anchorCenter().render(context, this.font);

        // Questions
        RenderUtils.text(Component.literal("Question").withStyle(ChatFormatting.UNDERLINE), fifth1, minY).anchorCenter().render(context, this.font);
        List<FormattedCharSequence> wrappedQuestion = this.font.split(Component.literal(Trivia.question), questionWidth);
        for (int i = 0; i < wrappedQuestion.size(); i++) {
            RenderUtils.text(wrappedQuestion.get(i), questionX, questionY + i * this.font.lineHeight).render(context, this.font);
        }

        // Answers
        RenderUtils.text(Component.literal("Answers").withStyle(ChatFormatting.UNDERLINE), fifth4, minY).anchorCenter().render(context, this.font);
        for (int i = 0; i < Trivia.answers.size(); i++) {
            Rectangle rect = answerRects.get(i);
            int borderColor = ANSWER_COLORS[i % ANSWER_COLORS.length];
            context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, borderColor); // top border
            context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, borderColor); // bottom
            context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, borderColor); // left
            context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, borderColor); // right

            // Check if the mouse is hovering over this answer
            boolean hovered = rect.contains(mouseX, mouseY);
            int textColor = hovered ? TextColors.WHITE : DEFAULT_TEXT_COLOR;

            // Draw each line
            int lineY = rect.y + 2;
            for (FormattedCharSequence line : answers.get(i)) {
                RenderUtils.text(line, rect.x+1, lineY).colored(textColor).render(context, this.font);
                lineY += this.font.lineHeight;
            }
        }

        // Entity in the middle
        context.fill(centerX-33, centerY-55, centerX+33, centerY+55, TextColors.BLACK);
        drawBot(context, startX, startY, mouseX, mouseY, centerX, centerY, 40);
    }

    private void drawBot(GuiGraphics context, int i, int j, int mouseX, int mouseY, int x, int y, int size) {
        if (minecraft == null) return;
        if (minecraft.level == null) return;
        if (minecraft.player == null) return;
        TriviaBot bot = null;
        for (TriviaBot entity : minecraft.level.getEntitiesOfClass(TriviaBot.class, minecraft.player.getBoundingBox().inflate(10), entity->true)) {
            if (bot == null || minecraft.player.distanceTo(entity) < minecraft.player.distanceTo(bot)) {
                bot = entity;
            }
        }
        if (bot != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(context, x-30, y-70, x+30, y+70, size, 0.0625F, centerX, centerY+10, bot);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
