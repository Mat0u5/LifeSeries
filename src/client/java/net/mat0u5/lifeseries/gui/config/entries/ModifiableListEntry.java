package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
//? if >= 1.21.9
import net.minecraft.client.input.*;

public abstract class ModifiableListEntry extends EmptyConfigEntry {
    public final Button deleteEntryButton;
    public final Button addEntryButton;

    public ModifiableListEntry(String fieldName) {
        this(fieldName, "", "");
    }

    public ModifiableListEntry(String fieldName, String displayName, String description) {
        super(fieldName, displayName, description);
        deleteEntryButton = Button.builder(Component.nullToEmpty("\uD83D\uDDD1"), this::deleteEntry)
                .bounds(0, 0, 16, 16)
                .build();
        addEntryButton = Button.builder(Component.nullToEmpty("+"), button -> addEntry())
                .bounds(0, 0, 16, 16)
                .build();
    }

    @Override
    protected void renderEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isFirst()) {
            renderFirstEntryExtras(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
            y += firstEntryHeightAdd();
        }
        renderMiddleEntryExtras(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        renderMainEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        y += getMainEntryHeight();

        if (isLast()) {
            renderLastEntryExtras(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        }
    }

    public void renderFirstEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    public void renderMiddleEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        deleteEntryButton.setX(x+5);
        deleteEntryButton.setY(y+2);
        //~ renames_26_1_volatile
        deleteEntryButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        //~ !renames_26_1_volatile
    }

    protected abstract void renderMainEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta);

    public void renderLastEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        addEntryButton.setX(x+10);
        addEntryButton.setY(y+2);
        if (isLast()) {
            //~ renames_26_1_volatile
            addEntryButton.extractRenderState(context, mouseX, mouseY, tickDelta);
            //~ !renames_26_1_volatile
        }
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (deleteEntryButton.mouseClicked(mouseX, mouseY, button) || addEntryButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (deleteEntryButton.mouseClicked(click, doubled) || addEntryButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }
    //?}

    @Override
    public ConfigTypes getValueType() {
        return null;
    }

    public void deleteEntry(Button button) {
        if (parentGroup == null) return;
        try {
            parentGroup.getMainEntry().markChangedForever();
            if (!parentGroup.getChildEntries().isEmpty()) {
                parentGroup.getChildEntries().get(0).markChangedForever();
            }
        }catch(Exception e) {}
        if (parentGroup.getChildEntries().size() == 1) {
            preventZeroEntries();
        }
        parentGroup.removeChildEntry(this);
    }

    public ConfigEntry preventZeroEntries() {
        return addEntry();
    }

    public ConfigEntry addEntry() {
        if (parentGroup == null) return null;
        ConfigEntry newEntry = getNewEntry();
        parentGroup.addChildEntry(newEntry);
        newEntry.markChangedForever();
        return newEntry;
    }
    public abstract ConfigEntry getNewEntry();

    public boolean isFirst() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == 0;
    }

    public boolean isLast() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == parentGroup.getChildEntries().size()-1;
    }

    @Override
    public int additionalResetButtonOffsetY() {
        return isFirst() ? firstEntryHeightAdd() : 0;
    }

    public int firstEntryHeightAdd() {
        return PREFFERED_HEIGHT;
    }

    public int lastEntryHeightAdd() {
        return PREFFERED_HEIGHT;
    }

    @Override
    public int getPreferredHeight() {
        int totalHeight = getMainEntryHeight();
        if (isFirst()) totalHeight += firstEntryHeightAdd();
        if (isLast()) totalHeight += lastEntryHeightAdd();
        return totalHeight;
    }

    public int getMainEntryHeight() {
        return PREFFERED_HEIGHT;
    }

    @Override
    public boolean hasResetButton() {
        return true;
    }

    @Override
    public boolean isSearchable() {
        return false;
    }

    public List<ModifiableListEntry> getListEntries() {
        List<ModifiableListEntry> result = new ArrayList<>();
        if (parentGroup != null) {
            for (ConfigEntry entry : parentGroup.getChildEntries()) {
                if (entry instanceof ModifiableListEntry modifiableListEntry) {
                    result.add(modifiableListEntry);
                }
            }
        }
        return result;
    }
    public List<ModifiableListEntry> getSisterEntries() {
        List<ModifiableListEntry> result = new ArrayList<>();
        if (parentGroup != null) {
            for (ConfigEntry entry : parentGroup.getChildEntries()) {
                if (entry instanceof ModifiableListEntry modifiableListEntry) {
                    if (modifiableListEntry != this) {
                        result.add(modifiableListEntry);
                    }
                }
            }
        }
        return result;
    }
}
