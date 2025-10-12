package net.mat0u5.lifeseries.gui.config.entries;

import net.minecraft.client.gui.DrawContext;
//? if >= 1.21.9 {
/*import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
*///?}

public abstract class EmptyConfigEntry extends ConfigEntry {
    public EmptyConfigEntry(String fieldName, String displayName, String description) {
        super(fieldName, displayName, description);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
    }

    //? if <= 1.21.6 {
    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        return false;
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(Click click, boolean doubled) {
        return false;
    }

    @Override
    protected boolean keyPressedEntry(KeyInput input) {
        return false;
    }

    @Override
    protected boolean charTypedEntry(CharInput input) {
        return false;
    }
    *///?}

    @Override
    public void resetToDefault() {

    }

    @Override
    public boolean canReset() {
        return false;
    }

    @Override
    public boolean hasResetButton() {
        return false;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getValueAsString() {
        return "";
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public String getDefaultValueAsString() {
        return "";
    }

    @Override
    public Object getStartingValue() {
        return null;
    }

    @Override
    public String getStartingValueAsString() {
        return "";
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public boolean modified() {
        return false;
    }
}
