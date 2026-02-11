package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmSeasonAnswerScreen extends DefaultScreen {
    private final Screen parent;
    private final Seasons season;

    public ConfirmSeasonAnswerScreen(Screen parent, Seasons season) {
        super(Component.literal("Confirm Answer"), 325, 110);
        this.parent = parent;
        this.season = season;
    }

    @Override
    public boolean allowCloseButton() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        int startX = (this.width - BG_WIDTH) / 2;
        int startY = (this.height - BG_HEIGHT) / 2;
        int fifth2 = startX + (BG_WIDTH / 5)*2;
        int fifth3 = startX + (BG_WIDTH / 5)*3;

        this.addRenderableWidget(
                Button.builder(Component.literal("Confirm"), btn -> {
                            this.onClose();
                            SimplePackets.SET_SEASON.sendToServer(season.getName());
                        })
                        .pos(fifth2 - 40, startY + BG_HEIGHT - 35)
                        .size(60, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), btn -> {
                            if (this.minecraft != null) this.minecraft.setScreen(parent);
                        })
                        .pos(fifth3 - 20, startY + BG_HEIGHT - 35)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        Component prompt1 = Component.nullToEmpty("WARNING: you have already selected a season.");
        Component prompt2 = Component.nullToEmpty("Changing it might cause some saved data to be lost (lives, ...).");
        Component prompt3 = TextUtils.formatPlain("Change the season to {}?", season.getName());
        RenderUtils.text(prompt1, centerX, startY + 15).anchorCenter().render(context, this.font);
        RenderUtils.text(prompt2, centerX, startY + 20 + font.lineHeight).anchorCenter().render(context, this.font);
        RenderUtils.text(prompt3, centerX, startY + 35 + font.lineHeight*2).anchorCenter().render(context, this.font);
    }
}