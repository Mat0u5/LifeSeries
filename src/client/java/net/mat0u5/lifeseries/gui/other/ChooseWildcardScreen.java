package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ChooseWildcardScreen extends DefaultScreen {

    public ChooseWildcardScreen() {
        super(Component.literal("Choose Wildcard Screen"), 230, 150);
    }

    @Override
    protected void init() {
        super.init();
        int oneThirdX = startX + BG_WIDTH / 3;
        int twoThirdX = startX + (BG_WIDTH / 3)*2;

        this.addRenderableWidget(
                Button.builder(Component.literal("Size Shifting"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("size_shifting");
                        })
                        .pos(oneThirdX - 50, startY  + 40)
                        .size(80, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Hunger"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("hunger");
                        })
                        .pos(oneThirdX - 50, startY  + 65)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Snails"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("snails");
                        })
                        .pos(oneThirdX - 50, startY  + 90)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Time Dilation"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("time_dilation");
                        })
                        .pos(oneThirdX - 50, startY  + 115)
                        .size(80, 20)
                        .build()
        );


        /*
            Second column
         */

        this.addRenderableWidget(
                Button.builder(Component.literal("Trivia"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("trivia");
                        })
                        .pos(twoThirdX - 30, startY  + 40)
                        .size(80, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Mob Swap"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("mob_swap");
                        })
                        .pos(twoThirdX - 30, startY  + 65)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Superpowers"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("superpowers");
                        })
                        .pos(twoThirdX - 30, startY  + 90)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Callback"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("callback");
                        })
                        .pos(twoThirdX - 30, startY  + 115)
                        .size(80, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        String prompt = "Select the Wildcard for this session.";
        RenderUtils.text(prompt, centerX, startY + 20).anchorCenter().render(context, this.font);
    }
}
