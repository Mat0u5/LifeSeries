package net.mat0u5.lifeseries.gui.trivia;

import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
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
                            Trivia.sendAnswer(answerIndex);
                        })
                        .pos(startX + 8, endY - 28)
                        .size(60, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), btn -> {
                            if (this.minecraft != null) this.minecraft.setScreen(parent);
                        })
                        .pos(endX  - 68, endY - 28)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        RenderUtils.text("Submit answer?", centerX, startY + 10).anchorCenter().render(context, this.font);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}