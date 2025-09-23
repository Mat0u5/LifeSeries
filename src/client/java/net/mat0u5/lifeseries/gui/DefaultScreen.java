package net.mat0u5.lifeseries.gui;

import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

//? if >= 1.21.9
/*import net.minecraft.client.gui.Click;*/

public abstract class DefaultScreen extends Screen {

    protected int BG_WIDTH;
    protected int BG_HEIGHT;
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected static final int DEFAULT_TEXT_COLOR = TextColors.DEFAULT;

    protected DefaultScreen(Text name, int widthX, int widthY, int offsetX, int offsetY) {
        super(name);
        this.BG_WIDTH = widthX;
        this.BG_HEIGHT = widthY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        calculateCoordinates();
    }
    protected DefaultScreen(Text name, int widthX, int widthY) {
        super(name);
        this.BG_WIDTH = widthX;
        this.BG_HEIGHT = widthY;
        calculateCoordinates();
    }

    public DefaultScreen(Text name) {
        super(name);
        this.BG_WIDTH = 320;
        this.BG_HEIGHT = 180;
        calculateCoordinates();
    }


    protected int startX;
    protected int centerX;
    protected int endX;
    protected int backgroundWidth;

    protected int startY;
    protected int centerY;
    protected int endY;
    protected int backgroundHeight;

    public void calculateCoordinates() {
        startX = (this.width - BG_WIDTH) / 2 + offsetX;
        endX = startX + BG_WIDTH;
        centerX = (startX + endX) / 2;
        backgroundWidth = endX - startX;

        startY = (this.height - BG_HEIGHT) / 2 + offsetY;
        endY = startY + BG_HEIGHT;
        centerY = (startY + endY) / 2;
        backgroundHeight = endY - startY;
    }

    public boolean allowCloseButton() {
        return true;
    }

    //? if <= 1.21.6 {
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && allowCloseButton()) { // Left-click
            if (isInCloseRegion((int)mouseX, (int)mouseY)) {
                closeButtonClicked();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.isLeft() && allowCloseButton()) { // Left-click
            if (isInCloseRegion((int)click.x(), (int)click.y())) {
                closeButtonClicked();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }
    *///?}

    public void closeButtonClicked() {
        this.close();
    }

    public boolean isInCloseRegion(int x, int y) {
        double width = textRenderer.getWidth(Text.of("✖"));
        double middleX = endX - 4 - (width/2);
        double height = textRenderer.fontHeight;
        double middleY = startY + 4 + (height/2);
        return Math.abs(x-middleX) <= (width/2) && Math.abs(y-middleY) <= (height/2);
    }

    @Override
    protected void init() {
        calculateCoordinates();
        super.init();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        // Thick borders
        context.fill(startX-2, startY-2, endX, endY, TextColors.PURE_WHITE);
        context.fill(startX, startY, endX+2, endY+2, TextColors.GUI_GRAY);

        // Background
        context.fill(startX, startY, endX, endY, TextColors.GUI_BACKGROUND);

        // Borders
        context.fill(startX-1, startY-3, endX, startY-2, TextColors.BLACK);
        context.fill(startX-3, startY-1, startX-2, endY, TextColors.BLACK);
        context.fill(startX, endY+3, endX+1, endY+2, TextColors.BLACK);
        context.fill(endX+3, startY, endX+2, endY+1, TextColors.BLACK);


        // Single Pixels
        // Top Left
        context.fill(startX-2, startY-2, startX-1, startY-1, TextColors.BLACK);
        context.fill(startX, startY, startX+1, startY+1, TextColors.PURE_WHITE);
        // Top Right
        context.fill(endX, startY, endX+1, startY-1, TextColors.GUI_BACKGROUND);
        context.fill(endX, startY-1, endX+1, startY-2, TextColors.BLACK);
        context.fill(endX+1, startY, endX+2, startY-1, TextColors.BLACK);
        // Bottom Left
        context.fill(startX, endY, startX-1, endY+1, TextColors.GUI_BACKGROUND);
        context.fill(startX-1, endY, startX-2, endY+1, TextColors.BLACK);
        context.fill(startX, endY+1, startX-1, endY+2, TextColors.BLACK);
        // Bottom Right
        context.fill(endX+1, endY+1, endX+2, endY+2, TextColors.BLACK);
        context.fill(endX-1, endY-1, endX, endY, TextColors.GUI_GRAY);
    }

    public void renderClose(DrawContext context, int mouseX, int mouseY) {
        if (isInCloseRegion(mouseX, mouseY)) RenderUtils.drawTextRight(context, textRenderer, Text.of("§l✖"), endX - 1, startY + 1);
        else RenderUtils.drawTextRight(context, textRenderer, Text.of("✖"), endX - 1, startY + 1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY);
        this.render(context, mouseX, mouseY);
        if (allowCloseButton()) renderClose(context, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
    }

    public abstract void render(DrawContext context, int mouseX, int mouseY);
}