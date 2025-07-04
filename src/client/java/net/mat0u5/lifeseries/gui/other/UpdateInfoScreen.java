package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class UpdateInfoScreen extends DefaultScreen {
    private String versionName;
    private String description;

    public UpdateInfoScreen(String versionName, String description) {
        super(Text.of("New Life Series Update"));
        this.versionName = versionName;
        this.description = description.replace("\r","");
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Click here to download on Modrinth"), btn -> {
                            if (this.client != null) this.client.setScreen(null);
                            Util.getOperatingSystem().open("https://modrinth.com/mod/life-series"); //Same as having a text with a click event, but that doesnt work in GUIs
                        })
                        .position(centerX - 100, endY - 28)
                        .size(200, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of("§0§nA new Life Series mod update is available!"), centerX, startY + 10);
        RenderUtils.drawTextLeft(context, this.textRenderer, Text.of("§0§nChangelog in version §l" +versionName+ "§0:"), startX + 10, startY + 25 + textRenderer.fontHeight);
        RenderUtils.drawTextLeftWrapLines(context, this.textRenderer, DEFAULT_TEXT_COLOR, Text.of(description), startX + 10, startY + 30 + textRenderer.fontHeight*2, backgroundWidth-20, 5);
    }
}