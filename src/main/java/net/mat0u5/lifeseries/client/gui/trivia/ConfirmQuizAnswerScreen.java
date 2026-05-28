package net.mat0u5.lifeseries.client.gui.trivia;

import net.mat0u5.lifeseries.client.LifeSeriesClient;
import net.mat0u5.lifeseries.client.features.Trivia;
import net.mat0u5.lifeseries.client.gui.DefaultScreen;
import net.mat0u5.lifeseries.client.gui.EmptySleepScreen;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfirmQuizAnswerScreen extends DefaultScreen {
    private final QuizScreen parent;
    private final int answerIndex;

    public ConfirmQuizAnswerScreen(QuizScreen parent, int answerIndex) {
        super(Component.literal("Confirm Answer"), 150, 60);
        this.parent = parent;
        this.answerIndex = answerIndex;
    }

    @Override
    public boolean allowCloseButton() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                Button.builder(Component.literal("Confirm"), btn -> {
                            this.onClose();
                             if (LifeSeriesClient.clientCurrentSeason == Seasons.NICE_LIFE) {
                                 RenderUtils.setScreen(new EmptySleepScreen(false));
                             }
                            Trivia.sendAnswer(answerIndex);
                        })
                        .pos(startX + 8, endY - 28)
                        .size(60, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), btn -> {
                            if (this.minecraft != null) RenderUtils.setScreen(parent);
                        })
                        .pos(endX  - 68, endY - 28)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        RenderUtils.text("Submit answer?", centerX, startY + 10).anchorCenter().render(context, this.font);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}