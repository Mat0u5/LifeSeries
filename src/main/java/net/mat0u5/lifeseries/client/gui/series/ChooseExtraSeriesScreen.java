package net.mat0u5.lifeseries.client.gui.series;

import net.mat0u5.lifeseries.client.gui.DefaultSmallScreen;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ChooseExtraSeriesScreen extends DefaultSmallScreen {

    private static final Identifier TEXTURE_SELECTED = Identifier.of("lifeseries","textures/gui/selected.png");
    private static final Identifier TEXTURE_SIMPLELIFE = Identifier.of("lifeseries","textures/gui/simplelife.png");

    public static boolean hasSelectedBefore = false;

    public ChooseExtraSeriesScreen(boolean hasSelectedBefore) {
        super(Text.literal("Choose April Series Screen"), 1.3f, 1.5f);
        this.hasSelectedBefore = hasSelectedBefore;
    }

    public int getRegion(int x, int y) {
        // X
        int startX = (this.width - BG_WIDTH) / 2;
        int endX = startX + BG_WIDTH;
        int centerX = (startX + endX) / 2;


        // Y
        int startY = (this.height - BG_HEIGHT) / 2;

        if (Math.abs(y - (startY + 60)) < 25) {
            if (Math.abs(x - centerX) < 25) return 1;
        }


        Text goBack = Text.of("Go Back");
        int textWidth = textRenderer.getWidth(goBack);
        int textHeight = textRenderer.fontHeight;

        Rectangle rect = new Rectangle(startX+9, endY-11-textHeight, textWidth+1, textHeight+1);
        if (x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height) {
            return 2;
        }

        return 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
            int region = getRegion((int) mouseX, (int) mouseY);
            if (region == 2 && this.client != null) {
                this.client.setScreen(new ChooseSeriesScreen(hasSelectedBefore));
            }
            else if (region != 0) {
                if (hasSelectedBefore && this.client != null) {
                    if (region == 1) this.client.setScreen(new ConfirmSeriesAnswerScreen(this, "Simple Life"));
                }
                else {
                    if (region == 1) NetworkHandlerClient.sendStringPacket("set_series", "Simple Life");
                    if (this.client != null) this.client.setScreen(null);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        int region = getRegion(mouseX, mouseY);

        // Background + images
        //? if <= 1.21 {

        if (region != 0) {
            if (region == 1) context.drawTexture(TEXTURE_SELECTED,centerX-25, startY + 35, 0, 0, 50, 50);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.2f, 0.2f, 1.0f);

        context.drawTexture(TEXTURE_SIMPLELIFE, (centerX-25) * 5, (startY + 35) * 5, 0, 0, 256, 256);

        context.getMatrices().pop();
        //?} else if <= 1.21.5 {
        /*if (region != 0) {
            if (region == 1) context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SELECTED, centerX-25, startY + 35, 0, 0, 50, 50, 64, 64);
        }

        context.getMatrices().push();
        context.getMatrices().scale(0.2f, 0.2f, 1.0f);

        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SIMPLELIFE, (centerX-25) * 5, (startY + 35) * 5, 0, 0, 256, 256, 256, 256);

        context.getMatrices().pop();
        *///?} else {
        /*if (region != 0) {
            if (region == 1) context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SELECTED, centerX-25, startY + 35, 0, 0, 50, 50, 64, 64);
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(0.2f, 0.2f);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_SIMPLELIFE, (centerX-25) * 5, (startY + 35) * 5, 0, 0, 256, 256, 256, 256);

        context.getMatrices().popMatrix();
        *///?}

        String prompt = "Select the series you want to play.";
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 20);

        Text goBack = Text.of("Go Back");
        int textWidth = textRenderer.getWidth(goBack);
        int textHeight = textRenderer.fontHeight;

        Rectangle rect = new Rectangle(startX+9, endY-11-textHeight, textWidth+1, textHeight+1);

        context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, 0xFF3c3c3c); // top border
        context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, 0xFF3c3c3c); // bottom
        context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, 0xFF3c3c3c); // left
        context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, 0xFF3c3c3c); // right

        if (region == 2) {
            RenderUtils.drawTextLeft(context, this.textRenderer, 0xffffff, goBack, rect.x+1, rect.y+1);
        }
        else {
            RenderUtils.drawTextLeft(context, this.textRenderer, DEFAULT_TEXT_COLOR, goBack, rect.x+1, rect.y+1);
        }

    }
}
