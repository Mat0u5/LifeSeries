package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.lastlife.LastLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SeasonInfoScreen extends DefaultScreen {

    public static Seasons season;

    public SeasonInfoScreen(Seasons season) {
        super(Component.literal("Season Info Screen"), 410, 230);
        this.season = season;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        // Background + images
        ResourceLocation logo = season.getLogo();
        if (logo != null) {
            RenderUtils.drawTextureScaled(context, logo, startX + 5, endY - 64, 0, 0, 256, 256, 0.25f, 0.25f);
            RenderUtils.drawTextureScaled(context, logo, endX - 64 - 5, endY - 64, 0, 0, 256, 256, 0.25f, 0.25f);
        }

        String seasonName = season.getName();
        RenderUtils.drawTextCenterScaled(context, this.font, Component.nullToEmpty("§0"+ seasonName), centerX, startY + 10, 2.25f, 2.25f);

        int currentY = startY + 40;
        MutableComponent adminCommandsText = Component.literal("§8Available §nadmin§8 commands: ");
        MutableComponent adminCommandsTextActual = getSeasonAdminCommands();
        if (adminCommandsTextActual != null) {
            MutableComponent combined = adminCommandsText.copy().append(adminCommandsTextActual);
            if (font.width(combined) < (endX - startX)) {
                RenderUtils.drawTextLeft(context, this.font, combined, startX + 15, currentY);
                currentY += font.lineHeight + 5;
            }
            else {
                RenderUtils.drawTextLeft(context, this.font, adminCommandsText, startX + 15, currentY);
                currentY += font.lineHeight + 5;
                RenderUtils.drawTextLeft(context, this.font, TextUtils.format("  {}", adminCommandsTextActual), startX + 15, currentY);
                currentY += font.lineHeight + 8;
            }
        }

        MutableComponent commandsText = Component.literal("§8Available §nnon-admin§8 commands: ");
        MutableComponent commandsTextActual = getSeasonCommands();
        if (commandsTextActual != null) {
            MutableComponent combined = commandsText.copy().append(commandsTextActual);
            if (font.width(combined) < (endX - startX)) {
                RenderUtils.drawTextLeft(context, this.font, combined, startX + 15, currentY);
                currentY += font.lineHeight + 10;
            }
            else {
                RenderUtils.drawTextLeft(context, this.font, commandsText, startX + 15, currentY);
                currentY += font.lineHeight + 5;
                RenderUtils.drawTextLeft(context, this.font, TextUtils.format("  {}", commandsTextActual), startX + 15, currentY);
                currentY += font.lineHeight + 10;
            }
        }

        Component howToStart = Component.nullToEmpty("§0§nHow to start a session");
        RenderUtils.drawTextLeftScaled(context, this.font, howToStart, startX + 15, currentY+3, 1.3f, 1.3f);
        currentY += font.lineHeight + 13;

        Component sessionTimer = Component.nullToEmpty("§8Run §3'/session timer set <time>'§8 to set the desired session time.");
        RenderUtils.drawTextLeft(context, this.font, sessionTimer, startX + 15, currentY);
        currentY += font.lineHeight + 5;

        Component sessionStart = Component.nullToEmpty("§8After that, run §3'/session start'§8 to start the session.");
        RenderUtils.drawTextLeft(context, this.font, sessionStart, startX + 15, currentY);
        currentY += font.lineHeight + 15;

        Component configText = Component.nullToEmpty("§0§nRun §8§n'/lifeseries config'§0§n to open the Life Series configuration!");
        RenderUtils.drawTextLeft(context, this.font, configText, startX + 15, currentY);
        currentY += font.lineHeight + 5;
    }

    private static @Nullable MutableComponent getSeasonCommands() {
        MutableComponent commandsTextActual = null;
        if (season == Seasons.THIRD_LIFE || season == Seasons.SIMPLE_LIFE || season == Seasons.REAL_LIFE) commandsTextActual = Component.literal(ThirdLife.COMMANDS_TEXT);
        if (season == Seasons.LAST_LIFE) commandsTextActual = Component.literal(LastLife.COMMANDS_TEXT);
        if (season == Seasons.DOUBLE_LIFE) commandsTextActual = Component.literal(DoubleLife.COMMANDS_TEXT);
        if (season == Seasons.LIMITED_LIFE) commandsTextActual = Component.literal(LimitedLife.COMMANDS_TEXT);
        if (season == Seasons.SECRET_LIFE) commandsTextActual = Component.literal(SecretLife.COMMANDS_TEXT);
        if (season == Seasons.WILD_LIFE) commandsTextActual = Component.literal(WildLife.COMMANDS_TEXT);
        if (season == Seasons.PAST_LIFE) commandsTextActual = Component.literal(PastLife.COMMANDS_TEXT);
        return commandsTextActual;
    }

    @Nullable
    private static MutableComponent getSeasonAdminCommands() {
        MutableComponent adminCommandsTextActual = null;
        if (season == Seasons.THIRD_LIFE || season == Seasons.SIMPLE_LIFE || season == Seasons.REAL_LIFE) adminCommandsTextActual = Component.literal(ThirdLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.LAST_LIFE) adminCommandsTextActual = Component.literal(LastLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.DOUBLE_LIFE) adminCommandsTextActual = Component.literal(DoubleLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.LIMITED_LIFE) adminCommandsTextActual = Component.literal(LimitedLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.SECRET_LIFE) adminCommandsTextActual = Component.literal(SecretLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.WILD_LIFE) adminCommandsTextActual = Component.literal(WildLife.COMMANDS_ADMIN_TEXT);
        if (season == Seasons.PAST_LIFE) adminCommandsTextActual = Component.literal(PastLife.COMMANDS_ADMIN_TEXT);
        return adminCommandsTextActual;
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