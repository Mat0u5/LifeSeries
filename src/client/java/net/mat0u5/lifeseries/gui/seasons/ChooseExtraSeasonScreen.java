package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.9
/*import net.minecraft.client.gui.Click;*/

public class ChooseExtraSeasonScreen extends DefaultScreen {

    public static boolean hasSelectedBefore = false;
    private List<ChooseSeasonScreen.SeasonRegion> seasonRegions = new ArrayList<>();
    private static final int ROWS = 1;
    private static final int LOGO_TEXTURE_SIZE = 256;
    private static final float LOGO_SCALE = 0.2f;
    private static final int LOGO_SIZE = (int) (LOGO_TEXTURE_SIZE * LOGO_SCALE);

    public ChooseExtraSeasonScreen(boolean hasSelectedBefore) {
        super(Text.literal("Choose April Season Screen"), 190, 100);
        this.hasSelectedBefore = hasSelectedBefore;
    }

    @Override
    public void init() {
        super.init();
        addSeasonRegions();
    }

    public void addSeasonRegions() {
        seasonRegions.clear();
        List<Seasons> seasons = Seasons.getAprilFoolsSeasons();

        int PADDING = ChooseSeasonScreen.PADDING;

        List<List<Seasons>> rows = ChooseSeasonScreen.splitIntoRows(seasons, ROWS);
        int currentRegionIndex = 1;
        int currentY = startY + 30;
        for (List<Seasons> row : rows) {
            int columns = row.size();
            int currentX = startX + (BG_WIDTH - (LOGO_SIZE * columns + PADDING * (columns-1))) / 2;
            for (Seasons season : row) {
                seasonRegions.add(ChooseSeasonScreen.getSeasonRegion(currentRegionIndex, season, currentX, currentY, LOGO_SIZE, LOGO_SIZE));
                currentRegionIndex++;
                currentX += LOGO_SIZE + PADDING;
            }
            currentY += LOGO_SIZE;// Don't add padding (the logos are usually wider than taller anyways)
        }
    }


    public int getRegion(int x, int y) {
        for (ChooseSeasonScreen.SeasonRegion region : seasonRegions) {
            if (x >= region.bounds().x && x <= region.bounds().x + region.bounds().width &&
                    y >= region.bounds().y && y <= region.bounds().y + region.bounds().height) {
                return region.id();
            }
        }

        Text goBack = Text.of("Go Back");
        int textWidth = textRenderer.getWidth(goBack);
        int textHeight = textRenderer.fontHeight;

        Rectangle rect = new Rectangle(startX+6, endY-8-textHeight, textWidth+1, textHeight+1);
        if (x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height) {
            return -1;
        }

        return 0;
    }

    @Override
    //? if <= 1.21.6 {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left-click
    //?} else {
    /*public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) { // Left-click
    *///?}
            int region = getRegion((int) mouseX, (int) mouseY);
            if (region == -1 && this.client != null) {
                this.client.setScreen(new ChooseSeasonScreen(hasSelectedBefore));
                return true;
            }
            else if (region != 0) {
                handleSeasonRegionClick(region);
                return true;
            }
        }
        //? if <= 1.21.6 {
        return super.mouseClicked(mouseX, mouseY, button);
        //?} else {
        /*return super.mouseClicked(click, doubled);
        *///?}
    }

    public void handleSeasonRegionClick(int region) {
        for (ChooseSeasonScreen.SeasonRegion seasonRegion : seasonRegions) {
            if (seasonRegion.id() == region) {
                if (hasSelectedBefore && this.client != null) {
                    this.client.setScreen(new ConfirmSeasonAnswerScreen(this, seasonRegion.season()));
                }
                else {
                    NetworkHandlerClient.sendStringPacket(PacketNames.SET_SEASON, seasonRegion.season().getName());
                    this.close();
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        int currentRegion = getRegion(mouseX, mouseY);

        // Background + images
        for (ChooseSeasonScreen.SeasonRegion seasonRegion : seasonRegions) {
            ChooseSeasonScreen.renderSeasonRegion(context, seasonRegion, currentRegion, LOGO_TEXTURE_SIZE, LOGO_SCALE);
        }

        String prompt = "Select the season you want to play.";
        RenderUtils.drawTextCenter(context, this.textRenderer, Text.of(prompt), centerX, startY + 15);

        Text goBack = Text.of("Go Back");
        int textWidth = textRenderer.getWidth(goBack);
        int textHeight = textRenderer.fontHeight;

        Rectangle rect = new Rectangle(startX+6, endY-8-textHeight, textWidth+1, textHeight+1);

        context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, DEFAULT_TEXT_COLOR); // top border
        context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, DEFAULT_TEXT_COLOR); // bottom
        context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, DEFAULT_TEXT_COLOR); // left
        context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, DEFAULT_TEXT_COLOR); // right

        if (currentRegion == -1) {
            RenderUtils.drawTextLeft(context, this.textRenderer, TextColors.PURE_WHITE, goBack, rect.x+1, rect.y+1);
        }
        else {
            RenderUtils.drawTextLeft(context, this.textRenderer, DEFAULT_TEXT_COLOR, goBack, rect.x+1, rect.y+1);
        }

    }
}
