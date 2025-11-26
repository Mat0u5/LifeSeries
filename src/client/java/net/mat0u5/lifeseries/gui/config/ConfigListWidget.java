package net.mat0u5.lifeseries.gui.config;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
*///?}

public class ConfigListWidget extends ObjectSelectionList<ConfigListWidget.ConfigEntryWidget> {
    public static final int ENTRY_GAP = 2;
    private static final int MAX_HIGHLIGHTED_ENTRIES = 3;
    private static final int SCROLLBAR_OFFSET_X = 6;
    protected ConfigScreen screen;

    //? if <= 1.20.2 {
    public ConfigListWidget(Minecraft client, int width, int height, int y, int itemHeight, int headerHeight) {
        super(client, width, height, y, itemHeight, headerHeight);
    }
    //?} else {
    /*public ConfigListWidget(Minecraft client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }
    *///?}

    public void setScreen(ConfigScreen screen) {
        this.screen = screen;
    }

    public void addEntry(ConfigEntry configEntry) {
        addEntry(new ConfigEntryWidget(configEntry));
    }

    public void clearAllEntries() {
        clearEntries();
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Override
    //? if < 1.20.5 {
    protected void renderList(GuiGraphics context, int mouseX, int mouseY, float delta) {
    //?} else {
    /*protected void renderListItems(GuiGraphics context, int mouseX, int mouseY, float delta) {
    *///?}
        //? if <= 1.21.2 {
        int maxScroll = getMaxScroll();
        //?} else {
        /*int maxScroll = maxScrollAmount();
         *///?}

        if (getScrolledAmount() > maxScroll) {
            setScrollAmount(maxScroll);
        }

        int listLeft = getX();
        int listTop = getY();
        int listRight = listLeft + width;
        int listBottom = listTop + height;

        context.fill(listLeft, listTop, listRight, listBottom, TextColors.BLACK_A32);

        int currentY = getCurrentY();

        Map<Float, ConfigEntry> highlightedEntries = new TreeMap<>();

        for (int i = 0; i < getItemCount(); i++) {
            ConfigEntryWidget entry = getEntry(i);
            ConfigEntry configEntry = entry.getConfigEntry();
            int entryHeight = configEntry.getPreferredHeight();

            if (currentY + entryHeight >= listTop && currentY < listBottom) {
                int entryWidth = getRowWidth();
                int entryLeft = listLeft + (width - entryWidth) / 2;

                boolean hovered = mouseX >= entryLeft && mouseX < entryLeft + entryWidth &&
                        mouseY >= currentY && mouseY < currentY + entryHeight;

                //? if <= 1.21.6 {
                entry.render(context, i, currentY, entryLeft, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
                //?} else {
                /*entry.setX(entryLeft);
                entry.setY(currentY);
                entry.setWidth(entryWidth);
                entry.setHeight(entryHeight);
                entry.renderContent(context, mouseX, mouseY, hovered, delta);
                *///?}


                List<ConfigEntry> withChildren = List.of(configEntry);

                if (screen != null) {
                    withChildren = screen.getAllEntries(withChildren);
                    if (!withChildren.contains(configEntry)) {
                        withChildren.add(configEntry);
                    }
                }

                for (ConfigEntry highlightEntry : withChildren) {
                    if (highlightEntry.highlightAlpha > 0.0f) {
                        highlightedEntries.put(highlightEntry.highlightAlpha, highlightEntry);
                    }
                }

            }

            currentY += entryHeight + ENTRY_GAP;
        }

        int highlightedCount = highlightedEntries.size();
        if (highlightedCount > MAX_HIGHLIGHTED_ENTRIES) {
            int pos = 0;
            for (ConfigEntry entry : highlightedEntries.values()) {
                if (pos >= MAX_HIGHLIGHTED_ENTRIES) {
                    break;
                }
                entry.highlightAlpha = 0.0f;
                pos++;
            }
        }

        if (maxScroll > 0) {
            int scrollbarX = listRight - SCROLLBAR_OFFSET_X;
            int scrollbarTop = listTop;
            int scrollbarBottom = listBottom;
            int scrollbarHeight = scrollbarBottom - scrollbarTop;

            context.fill(scrollbarX, scrollbarTop, scrollbarX + SCROLLBAR_OFFSET_X, scrollbarBottom, TextColors.BLACK_A64);

            int handleHeight = Math.max(10, scrollbarHeight * scrollbarHeight / (scrollbarHeight + maxScroll));
            int handleY = scrollbarTop + (int)((scrollbarHeight - handleHeight) * getScrolledAmount() / maxScroll);
            context.fill(scrollbarX + 1, handleY, scrollbarX + SCROLLBAR_OFFSET_X - 1, handleY + handleHeight, TextColors.WHITE_A128);
        }
    }

    @Override
    //? if <= 1.21.2 {
    public int getMaxScroll() {
    //?} else {
    /*public int maxScrollAmount() {
    *///?}
        int totalHeight = 0;
        for (int i = 0; i < getItemCount(); i++) {
            totalHeight += getEntry(i).getConfigEntry().getPreferredHeight();
        }
        return Math.max(0, totalHeight - height + 8);
    }
    //? if <= 1.20.2 {
    protected int getX() {
        return this.x0;
    }
    protected int getY() {
        return this.y0;
    }
    //?}

    //TODO in 1.20
    //? if > 1.20.3 && <= 1.21.2 {
    /*@Override
    protected boolean scrollbarVisible() {
        return false;
    }
    *///?}

    @Override
    //? if <= 1.21.6 {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //?} else {
    /*public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
    *///?}
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int listTop = getY();
        int currentY = getCurrentY();

        for (int i = 0; i < getItemCount(); i++) {
            ConfigEntryWidget entry = getEntry(i);
            int entryHeight = entry.getConfigEntry().getPreferredHeight() + ENTRY_GAP;

            if (mouseY >= currentY && mouseY < currentY + entryHeight) {
                setFocused(entry);
                entry.getConfigEntry().setFocused(true);
                //? if <= 1.21.6 {
                return entry.mouseClicked(mouseX, mouseY, button);
                //?} else {
                /*return entry.mouseClicked(click, doubled);
                *///?}
            }

            currentY += entryHeight;
        }

        return false;
    }

    public double getScrolledAmount() {
        //? if <= 1.21.2 {
        return getScrollAmount();
        //?} else {
        /*return scrollAmount();
        *///?}
    }

    //? if >= 1.21.9 {
    /*public ConfigEntryWidget getEntry(int entry) {
        return children().get(entry);
    }
    *///?}

    //? if <= 1.21.6 {
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ConfigEntryWidget entry = getFocused();
        if (entry == null) return false;
        return entry.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        ConfigEntryWidget entry = getFocused();
        if (entry == null) return false;
        return entry.charTyped(chr, modifiers);
    }
    //?} else {
    /*@Override
    public boolean keyPressed(KeyEvent input) {
        ConfigEntryWidget entry = getFocused();
        if (entry == null) return false;
        return entry.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        ConfigEntryWidget entry = getFocused();
        if (entry == null) return false;
        return entry.charTyped(input);
    }
    *///?}

    public int getCurrentY() {
        return getY() + 4 - (int)getScrolledAmount();
    }

    @Override
    public int getItemCount() {
        return this.children().size();
    }

    public static class ConfigEntryWidget extends ObjectSelectionList.Entry<ConfigEntryWidget> {
        private final ConfigEntry configEntry;

        public ConfigEntryWidget(ConfigEntry configEntry) {
            this.configEntry = configEntry;
        }

        //? if <= 1.21.6 {
        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            configEntry.render(context, x, y, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            configEntry.setFocused(true);
            return configEntry.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            configEntry.setFocused(true);
            return configEntry.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            configEntry.setFocused(true);
            return configEntry.charTyped(chr, modifiers);
        }
        //?} else {
        /*@Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            configEntry.render(context, this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY, hovered, tickDelta);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            configEntry.setFocused(true);
            return configEntry.mouseClicked(click, doubled);
        }

        @Override
        public boolean keyPressed(KeyEvent input) {
            configEntry.setFocused(true);
            return configEntry.keyPressed(input);
        }

        @Override
        public boolean charTyped(CharacterEvent input) {
            configEntry.setFocused(true);
            return configEntry.charTyped(input);
        }
        *///?}

        @Override
        public Component getNarration() {
            return configEntry.getDisplayName();
        }

        public ConfigEntry getConfigEntry() {
            return configEntry;
        }
    }
}