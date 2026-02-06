package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigListWidget;
import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public class GroupConfigEntry<T extends ConfigEntry & IEntryGroupHeader> extends EmptyConfigEntry {
    private static final int CHILD_INDENT = 20;
    protected static final int EXPAND_TEXT_OFFSET_X = LABEL_OFFSET_X - 10;
    protected static final int EXPAND_TEXT_OFFSET_Y = LABEL_OFFSET_Y;
    private static final int EXPAND_SIDEBAR_THICKNESS = 1;
    private static final int EXPAND_SIDEBAR_OFFSET_X = -9;
    private static final float ANIMATION_OPEN_SPEED = 0.15f;
    private static final float ANIMATION_CLOSE_SPEED = 0.4f;

    private final T mainEntry;
    private final List<ConfigEntry> childEntries;
    private boolean isExpanded = false;
    private boolean showSidebar;
    private boolean renderBottomBar = false;
    private int currentHeight;

    private int y;

    public GroupConfigEntry(T mainEntry, List<ConfigEntry> childEntries, boolean showSidebar, boolean openByDefault) {
        super("", "","");
        this.mainEntry = mainEntry;
        this.currentHeight = mainEntry.getPreferredHeight();
        this.childEntries = new ArrayList<>(childEntries);
        this.showSidebar = showSidebar;
        if (openByDefault) {
            mainEntry.expand();
        }

        this.mainEntry.parentGroup = this;
        this.mainEntry.groupTopology.add(this);
        this.childEntries.forEach(entry -> {
            entry.parentGroup = this;
            entry.groupTopology.add(this);
        });
    }

    @Override
    public void setScreen(ConfigScreen screen) {
        super.setScreen(screen);
        if (mainEntry != null) {
            mainEntry.setScreen(screen);
        }
        for (ConfigEntry child : childEntries) {
            child.setScreen(screen);
        }
    }

    @Override
    public void render(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.y = y;
        renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int maxHeight = getMaxHeight();
        if (hasExpandingChild()) {
            currentHeight = maxHeight;
        }
        else if (Math.abs(currentHeight - maxHeight) > 1.5f) {
            float animationSpeed = (currentHeight < maxHeight) ? ANIMATION_OPEN_SPEED : ANIMATION_CLOSE_SPEED;
            float change = ((maxHeight - currentHeight) * animationSpeed * tickDelta);
            if (Math.abs(change) < (animationSpeed*10)) change *= (animationSpeed*10)/Math.abs(change);
            currentHeight += (int) change;
        }
        else {
            currentHeight = maxHeight;
        }

        int currentY = y;

        if (mainEntry != null && screen != null) {
            int entryHeight = mainEntry.getPreferredHeight();
            boolean entryHovered = mouseX >= x && mouseX < x + width &&
                    mouseY >= currentY && mouseY < currentY + entryHeight;

            mainEntry.render(context, x, y, width, entryHeight, mouseX, mouseY, entryHovered, tickDelta);
            currentY += entryHeight + ConfigListWidget.ENTRY_GAP;

            if (showExpandText()) {
                String expandText = !mainEntry.isExpanded() ? "Click to expand" : "Click to collapse";
                RenderUtils.text(expandText, mainEntry.expandTextX(x, width), y + LABEL_OFFSET_Y).anchorRight().colored(TextColors.LIGHT_GRAY_A128).render(context, textRenderer);
            }
        }


        boolean shouldBeExpanded = shouldExpand();
        if (shouldBeExpanded != isExpanded) {
            isExpanded = shouldBeExpanded;
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                int entryHeight = child.getPreferredHeight();
                if ((currentY+entryHeight + ConfigListWidget.ENTRY_GAP) - 10 <= y+currentHeight) {
                    boolean entryHovered = mouseX >= x && mouseX < x + width &&
                            mouseY >= currentY && mouseY < currentY + entryHeight;
                    child.render(context, x + CHILD_INDENT, currentY, width - CHILD_INDENT, entryHeight, mouseX, mouseY, entryHovered, tickDelta);
                }
                currentY += entryHeight + ConfigListWidget.ENTRY_GAP;
            }
        }

        renderExpandIcon(context, x, y, isExpanded, y+currentHeight+1, width);
    }

    private void renderExpandIcon(GuiGraphics context, int x, int y, boolean expanded, int endY, int width) {
        String text = expanded ? "- " : "+ ";
        if (showExpandIcon()) {
            RenderUtils.text(text, x + EXPAND_TEXT_OFFSET_X, y + EXPAND_TEXT_OFFSET_Y).anchorRight().colored(TextColors.WHITE).render(context, textRenderer);
        }
        if (showSidebar) {
            context.fill(x+EXPAND_SIDEBAR_OFFSET_X, y, x+EXPAND_SIDEBAR_OFFSET_X+EXPAND_SIDEBAR_THICKNESS, endY - ConfigListWidget.ENTRY_GAP, TextColors.WHITE_A128);
        }
        if (expanded && renderBottomBar) {
            context.fill(x+EXPAND_SIDEBAR_OFFSET_X, endY - ConfigListWidget.ENTRY_GAP, Math.max(x + LABEL_OFFSET_X + CHILD_INDENT + 20, (x+width)/2), endY - ConfigListWidget.ENTRY_GAP+EXPAND_SIDEBAR_THICKNESS, TextColors.WHITE_A32);
        }
    }

    private boolean shouldExpand() {
        return mainEntry.isExpanded();
    }

    private boolean showExpandIcon() {
        return mainEntry.showExpandIcon();
    }

    private boolean showExpandText() {
        return mainEntry.showExpandText();
    }

    private boolean hasExpandingChild() {
        for (ConfigEntry child : childEntries) {
            if (child instanceof GroupConfigEntry<?> groupChild) {
                if (groupChild.isAnimating() || groupChild.hasExpandingChild()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAnimating() {
        return Math.abs(currentHeight - getMaxHeight()) > 1.5f;
    }

    @Override
    //? if <= 1.21.6 {
    /*protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
    *///?} else {
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
    //?}
        int currentY = (int) mouseY - this.y;

        if (currentY < 0 || mainEntry == null) return false;

        if (currentY <= mainEntry.getPreferredHeight()) {
            mainEntry.setFocused(true);
            //? if <= 1.21.6 {
            /*if (mainEntry.mouseClicked(mouseX, mouseY, button)) return true;
            *///?} else {
            if (mainEntry.mouseClicked(click, doubled)) return true;
            //?}
        }

        if (isExpanded) {
            double childY = mainEntry.getPreferredHeight();
            for (ConfigEntry child : childEntries) {
                int childHeight = child.getPreferredHeight() + ConfigListWidget.ENTRY_GAP;
                if ((childY+childHeight + ConfigListWidget.ENTRY_GAP) - 10 <= currentY+currentHeight) {
                    if (currentY >= childY && currentY < childY + childHeight) {
                        child.setFocused(true);
                        //? if <= 1.21.6 {
                        /*if (child.mouseClicked(mouseX, mouseY, button)) return true;
                        *///?} else {
                        if (child.mouseClicked(click, doubled)) return true;
                        //?}
                    }
                }
                childY += childHeight;
            }
        }

        return false;
    }

    @Override
    //? if <= 1.21.6 {
    /*protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
    *///?} else {
    protected boolean keyPressedEntry(KeyEvent input) {
    //?}
        if (mainEntry != null && mainEntry.isFocused()) {
            //? if <= 1.21.6 {
            /*if (mainEntry.keyPressed(keyCode, scanCode, modifiers)) {
            *///?} else {
            if (mainEntry.keyPressed(input)) {
            //?}
                return true;
            }
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused()) continue;
                //? if <= 1.21.6 {
                /*if (child.keyPressed(keyCode, scanCode, modifiers)) {
                *///?} else {
                if (child.keyPressed(input)) {
                //?}
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    //? if <= 1.21.6 {
    /*protected boolean charTypedEntry(char chr, int modifiers) {
    *///?} else {
    protected boolean charTypedEntry(CharacterEvent input) {
    //?}
        if (mainEntry != null && mainEntry.isFocused()) {
            //? if <= 1.21.6 {
            /*if (mainEntry.charTyped(chr, modifiers)) {
            *///?} else {
            if (mainEntry.charTyped(input)) {
            //?}
                return true;
            }
        }

        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                if (!child.isFocused()) continue;
                //? if <= 1.21.6 {
                /*if (child.charTyped(chr, modifiers)) {
                *///?} else {
                if (child.charTyped(input)) {
                //?}
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getPreferredHeight() {
        return currentHeight;
    }

    public int getMaxHeight() {
        int height = PREFFERED_HEIGHT;
        if (mainEntry != null) {
            height = mainEntry.getPreferredHeight();
        }
        if (isExpanded) {
            for (ConfigEntry child : childEntries) {
                height += child.getPreferredHeight() + ConfigListWidget.ENTRY_GAP;
            }
        }

        return height;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.GROUP;
    }

    @Override
    public boolean hasError() {
        if (mainEntry != null && mainEntry.hasError) {
            return true;
        }

        for (ConfigEntry child : childEntries) {
            if (child.hasError()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void markChanged() {
        super.markChanged();
        if (mainEntry != null) {
            mainEntry.markChanged();
        }
    }

    public void addChildEntry(ConfigEntry entry) {
        entry.parentGroup = this;
        entry.groupTopology.add(this);
        entry.setScreen(this.screen);
        childEntries.add(entry);
    }

    public void removeChildEntry(ConfigEntry entry) {
        childEntries.remove(entry);
        entry.parentGroup = null;
        entry.groupTopology.remove(this);
    }

    public List<ConfigEntry> getChildEntries() {
        return new ArrayList<>(childEntries);
    }

    public ConfigEntry getMainEntry() {
        return mainEntry;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    @Override
    public boolean isFocused() {
        boolean isFocused = super.isFocused();
        if (isFocused) {
            return true;
        }
        for (ConfigEntry child : childEntries) {
            if (child.isFocused()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sendToServer() {
        return false;
    }

    @Override
    public boolean canLoseFocusEasily() {
        return false;
    }
}