package net.mat0u5.lifeseries.gui.config.entries.main;

import net.mat0u5.lifeseries.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}

public class TextConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {
    private final boolean clickable;
    public boolean clicked;

    public TextConfigEntry(String fieldName, String displayName, String description) {
        this(fieldName, displayName, description, true);
    }

    public TextConfigEntry(String fieldName, String displayName, String description, boolean clickable) {
        super(fieldName, displayName, description);
        this.clickable = clickable;
    }

    @Override
    //? if <= 1.21.6 {
    /*protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (clickable && button == 0) {
    *///?} else {
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
    if (clickable && click.button() == 0) {
    //?}
            clicked = !clicked;
        }
        return clickable;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TEXT;
    }

    @Override
    public void expand() {
        clicked = true;
    }

    @Override
    public boolean isExpanded() {
        return clicked;
    }

    @Override
    public int expandTextX(int x, int width) {
        return x + width - 5;
    }
}