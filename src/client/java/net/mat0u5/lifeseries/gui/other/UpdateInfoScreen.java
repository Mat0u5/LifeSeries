package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.versions.UpdateChecker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
//? if <= 1.21.9 {
import net.minecraft.Util;
//?} else {
/*import net.minecraft.util.Util;
*///?}

public class UpdateInfoScreen extends DefaultScreen {
    private String versionName;
    private String description;
    private Component dismissText = Component.nullToEmpty("Dismiss for this update");
    private int textWidth = 0;
    private Button dismissButton;

    public UpdateInfoScreen(String versionName, String description) {
        super(Component.nullToEmpty("New Life Series Update"), 400, 225, 0, +10);
        this.versionName = versionName;
        this.description = description.replace("\r","");
    }

    public boolean isInCheckboxRegion(int x, int y) {
        return (x >= endX - textWidth/2-2-40) && (x <= endX + textWidth/2+1-40)
                && y >= startY - 23 && y <= startY + 3;
    }

    @Override
    protected void init() {
        super.init();
        textWidth = font.width(dismissText) + 5;

        this.addRenderableWidget(
                Button.builder(Component.literal("Join Discord").withStyle(style -> style.withColor(TextColors.PASTEL_WHITE)),btn -> {
                            Util.getPlatform().openUri("https://discord.gg/QWJxfb4zQZ");
                        })
                        .pos(startX + 5, endY - 25)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Full Changelog").withStyle(style -> style.withColor(TextColors.PASTEL_WHITE)), btn -> {
                            Util.getPlatform().openUri(UpdateChecker.getChangelogLink());
                        })
                        .pos(endX - 80 - 5, endY - 25)
                        .size(80, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Download on Modrinth"), btn -> {
                            this.onClose();
                            Util.getPlatform().openUri("https://modrinth.com/mod/life-series"); //Same as having a text with a click event, but that doesnt work in GUIs
                        })
                        .pos(centerX - 85, endY - 25)
                        .size(170, 20)
                        .build()
        );
        dismissButton = this.addRenderableWidget(
                Button.builder(dismissText, btn -> {
                            MainClient.clientConfig.setProperty("ignore_update", String.valueOf(UpdateChecker.version));
                            this.onClose();
                        })
                        .pos(endX - textWidth/2-40, startY - 20)
                        .size(textWidth, 16)
                        .build()
        );
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY) {
        if (isInCheckboxRegion(mouseX, mouseY)) {
            context.fill(endX - textWidth/2-3-40, startY - 23, endX + textWidth/2+3-40, startY, TextColors.BLACK);
            context.fill(endX - textWidth/2-2-40, startY - 22, endX + textWidth/2+2-40, startY - 1, TextColors.GUI_BACKGROUND);
            dismissButton.visible = true;
        }
        else {
            dismissButton.visible = false;
        }
        super.renderBackground(context, mouseX, mouseY);
    }
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        RenderUtils.text("§0§nA new Life Series mod update is available!", centerX, startY + 7).anchorCenter().render(context, this.font);
        RenderUtils.text(TextUtils.formatLoosely("§0§nChangelog in version §l{}§0:",versionName), startX + 7, startY + 25 + font.lineHeight).render(context, this.font);
        RenderUtils.text(description, startX + 7, startY + 30 + font.lineHeight*2).wrapLines(backgroundWidth-14, 5).render(context, this.font);
    }
}