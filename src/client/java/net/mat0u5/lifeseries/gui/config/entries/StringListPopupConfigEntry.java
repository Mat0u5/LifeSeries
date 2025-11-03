package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.entries.interfaces.IPopup;
import net.mat0u5.lifeseries.gui.config.entries.interfaces.ITextFieldAddonPopup;
import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class StringListPopupConfigEntry<T> extends StringConfigEntry implements ITextFieldAddonPopup {
    private final int MAX_LINE_ENTRIES;
    private final int ENTRY_SIZE;
    private final int ENTRY_PADDING;
    protected List<T> entries = null;
    private String lastEntryStr = "";

    public StringListPopupConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        this(fieldName,displayName, description, value, defaultValue, 10, 16, 2);
    }

    public StringListPopupConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue,
                                      int maxLineEntries, int entrySize, int entryPadding) {
        super(fieldName, displayName, description, value, defaultValue);
        this.MAX_LINE_ENTRIES = maxLineEntries;
        this.ENTRY_SIZE = entrySize;
        this.ENTRY_PADDING = entryPadding;
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        renderPopup(context, mouseX, mouseY, tickDelta);
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        reloadEntriesRaw(text);
    }

    protected void reloadEntriesRaw(String text) {
        if (lastEntryStr != null && lastEntryStr.equalsIgnoreCase(text)) {
            return;
        }
        String raw = text;
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        List<String> items = new ArrayList<>(Arrays.asList(raw.split(",")));
        reloadEntries(items);
        lastEntryStr = text;
    }

    @Override
    public EditBox getTextField() {
        return textField;
    }

    @Override
    public Font getTextRenderer() {
        return textRenderer;
    }

    @Override
    public Component getPopupText() {
        if (entries == null || entries.isEmpty()) {
            return Component.literal("Empty").withStyle(ChatFormatting.GRAY);
        }
        return Component.empty();
    }

    @Override
    public boolean shouldShowPopup() {
        if (textField == null) return false;
        //if (hasError()) return false;

        if (isFocused()) return true;

        if (isHovered) {
            ConfigEntry entry = screen.getFocusedEntry();
            if (!(entry instanceof IPopup popup)) return true;
            if (popup == this) return true;
            return !popup.shouldShowPopup();

        }
        return false;
    }

    @Override
    public int getPopupWidth() {
        if (entries == null || entries.isEmpty()) {
            return ITextFieldAddonPopup.super.getPopupWidth();
        }
        return Math.min(entries.size(), MAX_LINE_ENTRIES) * (ENTRY_SIZE + ENTRY_PADDING) + ENTRY_PADDING;
    }

    @Override
    public int getPopupHeight() {
        if (entries == null || entries.isEmpty()) {
            return ITextFieldAddonPopup.super.getPopupHeight();
        }
        return (Math.max(entries.size()-1, 0) / MAX_LINE_ENTRIES + 1) * (ENTRY_SIZE + ENTRY_PADDING) + ENTRY_PADDING;
    }

    @Override
    public void renderContent(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta) {
        if (entries == null || entries.isEmpty()) {
            ITextFieldAddonPopup.super.renderContent(context, x, y, width, height, mouseX, mouseY, tickDelta);
            return;
        }
        int startingItemX = x + ENTRY_PADDING;
        int startingItemY = y + ENTRY_PADDING;
        int itemX = startingItemX;
        int itemY = startingItemY;
        int currentLine = 0;
        for (T entry : entries) {
            if (currentLine >= MAX_LINE_ENTRIES) {
                itemX = startingItemX;
                itemY += ENTRY_SIZE + ENTRY_PADDING;
                currentLine = 0;
            }
            renderListEntry(context, entry, itemX, itemY, mouseX, mouseY, tickDelta);
            itemX += ENTRY_SIZE + ENTRY_PADDING;
            currentLine++;
        }
    }

    protected abstract void reloadEntries(List<String> items);
    protected abstract void renderListEntry(GuiGraphics context, T entry, int x, int y, int mouseX, int mouseY, float tickDelta);
}
