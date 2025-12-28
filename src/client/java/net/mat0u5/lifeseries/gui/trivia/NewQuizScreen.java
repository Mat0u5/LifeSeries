package net.mat0u5.lifeseries.gui.trivia;

import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
 *///?}

public class NewQuizScreen extends Screen {
    private final List<List<FormattedCharSequence>> answers = new ArrayList<>();
    private final List<Rectangle> answerRects = new ArrayList<>();
    private int timerSeconds = 67;

    public NewQuizScreen() {
        super(Component.literal("Quiz Screen"));
    }

    @Override
    protected void init() {
        super.init();
        int quarter1 = this.width / 4;
        int quarter2 = quarter1*2;
        int quarter3 = quarter1*3;

        int answersStartX = quarter3 + 7;
        int answersStopX = this.width - 7;

        int maxWidth = answersStopX - answersStartX;

        int currentYPos = 25;
        int gap = 7;
        answers.clear();
        answerRects.clear();
        for (int i = 0; i < Trivia.answers.size(); i++) {
            MutableComponent answerText = Component.literal(Trivia.answers.get(i));
            answerText.setStyle(answerText.getStyle().withBold(false));
            List<FormattedCharSequence> answer = this.font.split(answerText, maxWidth);
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
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    //@Override
    //public void onClose() {
    //}


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
                    this.onClose();
                    Minecraft.getInstance().setScreen(new EmptySleepScreen(false));
                    Trivia.sendAnswer(i);
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
    public void tick() {
        super.tick();
        timerSeconds = Trivia.getRemainingSeconds();
        if (timerSeconds <= 0) {
            this.onClose();
            Minecraft.getInstance().setScreen(new EmptySleepScreen(false));
        }
    }

    @Override
    //? if <= 1.20 {
    /*public void renderBackground(GuiGraphics context) {}
     *///?} else {
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {}
    //?}

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int quarter1 = this.width / 4;
        int quarter2 = quarter1*2;
        int quarter3 = quarter1*3;
        context.fill(0, 0, quarter1, this.height, TextColors.BLACK_A96);
        context.fill(quarter3, 0, this.width, this.height, TextColors.BLACK_A96);


        // Timer
        long minutes = timerSeconds / 60;
        long seconds = timerSeconds - minutes * 60;
        String secondsStr = String.valueOf(seconds);
        String minutesStr = String.valueOf(minutes);
        while (secondsStr.length() < 2) secondsStr = "0" + secondsStr;
        while (minutesStr.length() < 2) minutesStr = "0" + minutesStr;

        Component timerText = TextUtils.format("{}:{}", minutesStr, secondsStr);
        RenderUtils.text(timerText, quarter2, 9).colored(TextColors.WHITE).anchorCenter().render(context, this.font);

        // Question
        int questionWidth = quarter1-14;
        RenderUtils.text(Component.literal("Question").withStyle(ChatFormatting.UNDERLINE), 7, 9).colored(TextColors.WHITE).render(context, this.font);
        List<FormattedCharSequence> wrappedQuestion = this.font.split(Component.literal(Trivia.question), questionWidth);
        for (int i = 0; i < wrappedQuestion.size(); i++) {
            RenderUtils.text(wrappedQuestion.get(i), 7, 25 + i * this.font.lineHeight).colored(TextColors.WHITE).render(context, this.font);
        }

        // Answers
        RenderUtils.text(Component.literal("Answers").withStyle(ChatFormatting.UNDERLINE), quarter3+7, 9).colored(TextColors.WHITE).render(context, this.font);
        for (int i = 0; i < Trivia.answers.size(); i++) {
            Rectangle rect = answerRects.get(i);

            // Check if the mouse is hovering over this answer
            if (rect.contains(mouseX, mouseY)) {
                context.fill(rect.x-2, rect.y-2, rect.x+rect.width+4, rect.y+rect.height+4, TextColors.BLACK_A64);
            }

            // Draw each line
            int lineY = rect.y + 2;
            for (FormattedCharSequence line : answers.get(i)) {
                RenderUtils.text(line, rect.x+1, lineY).withShadow().colored(TextColors.WHITE).render(context, this.font);
                lineY += this.font.lineHeight;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
