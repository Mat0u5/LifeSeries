package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class PastLifeChooseTwistScreen extends DefaultScreen {

    public PastLifeChooseTwistScreen() {
        super(Component.nullToEmpty("Choose Twist"), 240, 80);
    }

    @Override
    public void init() {
        super.init();

        int firstX = startX + BG_WIDTH / 3-17;
        int secondsX = startX + (BG_WIDTH*2) / 3+17;

        this.addRenderableWidget(
                Button.builder(Component.literal("The Boogeyman"), btn -> {
                            this.onClose();
                            ClientUtils.runCommand("/pastlife boogeyman");
                        })
                        .pos(firstX - 50, startY + 27)
                        .size(100, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Secret Society"), btn -> {
                            this.onClose();
                            ClientUtils.runCommand("/pastlife society");
                        })
                        .pos(secondsX - 50, startY + 27)
                        .size(100, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Pick Randomly"), btn -> {
                            this.onClose();
                            ClientUtils.runCommand("/pastlife pickRandom");
                        })
                        .pos(firstX - 50, startY + 52)
                        .size(100, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("No Twist"), btn -> {
                            this.onClose();
                        })
                        .pos(secondsX - 50, startY + 52)
                        .size(100, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        RenderUtils.drawTextCenter(context, font, Component.nullToEmpty("Choose Past Life session twist:"), centerX, startY + 10);
    }
}
