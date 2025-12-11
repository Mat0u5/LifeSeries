package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SeasonInfoScreen extends DefaultScreen {

    public static Seasons season;
    public final String adminCommands;
    public final String nonAdminCommands;
    public SeasonInfoScreen(Seasons season, String adminCommands, String nonAdminCommands) {
        super(Component.literal("Season Info Screen"), 410, 230);
        this.season = season;
        this.adminCommands = adminCommands;
        this.nonAdminCommands = nonAdminCommands;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        // Background + images
        var logo = season.getLogo();
        if (logo != null) {
            RenderUtils.texture(logo, startX + 5, endY - 64, 256, 256).scaled(0.25f, 0.25f).render(context);
            RenderUtils.texture(logo, endX - 64 - 5, endY - 64, 256, 256).scaled(0.25f, 0.25f).render(context);
        }

        String seasonName = season.getName();
        RenderUtils.text("§0" + seasonName, centerX, startY + 10).anchorCenter().scaled(2.25f, 2.25f).render(context, this.font);

        int currentY = startY + 40;
        MutableComponent adminCommandsText = Component.literal("§8Available §nadmin§8 commands: ");
        MutableComponent adminCommandsTextActual = Component.literal(adminCommands);
        MutableComponent combinedAdminCommands = adminCommandsText.copy().append(adminCommandsTextActual);
        currentY += 3 + RenderUtils.text(combinedAdminCommands, startX+15, currentY).wrapLines(BG_WIDTH-20, 6).render(context, this.font);

        MutableComponent commandsText = Component.literal("§8Available §nnon-admin§8 commands: ");
        MutableComponent commandsTextActual = Component.literal(nonAdminCommands);
        MutableComponent combinedNonAdminCommands = commandsText.copy().append(commandsTextActual);
        currentY += 3 + RenderUtils.text(combinedNonAdminCommands, startX+15, currentY).wrapLines(BG_WIDTH-20, 6).render(context, this.font);

        String howToStart = "§0§nHow to start a session";
        RenderUtils.text(howToStart, startX + 15, currentY+3).scaled(1.3f, 1.3f).render(context, this.font);
        currentY += font.lineHeight + 13;

        Component sessionTimer = Component.nullToEmpty("§8Run §3'/session timer set <time>'§8 to set the desired session time.");
        RenderUtils.text(sessionTimer, startX + 15, currentY).render(context, this.font);
        currentY += font.lineHeight + 5;

        Component sessionStart = Component.nullToEmpty("§8After that, run §3'/session start'§8 to start the session.");
        RenderUtils.text(sessionStart, startX + 15, currentY).render(context, this.font);
        currentY += font.lineHeight + 15;

        Component configText = Component.nullToEmpty("§0§nRun §8§n'/lifeseries config'§0§n to open the Life Series configuration!");
        RenderUtils.text(configText, startX + 15, currentY).render(context, this.font);
        currentY += font.lineHeight + 5;
    }

    @Override
    public void onClose() {
        if (season == Seasons.PAST_LIFE && this.minecraft != null) {
            this.minecraft.setScreen(new PastLifeInfoScreen());
            return;
        }
        super.onClose();
    }
}