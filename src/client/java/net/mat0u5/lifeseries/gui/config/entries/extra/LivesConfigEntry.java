package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.main.NullableIntegerConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public class LivesConfigEntry extends NullableIntegerConfigEntry {
    public Button addButton;
    public Button subtractButton;
    public LivesConfigEntry(String fieldName, String displayName, String description, Integer value, Integer defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);

        addButton = Button.builder(Component.nullToEmpty("+"), this::add)
                .bounds(0, 0, 16, 16)
                .build();
        subtractButton = Button.builder(Component.nullToEmpty("-"), this::subtract)
                .bounds(0, 0, 16, 16)
                .build();
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        addButton.setX(textField.getX() - 39);
        addButton.setY(y + 2);
        addButton.render(context, mouseX, mouseY, tickDelta);
        subtractButton.setX(textField.getX() - 20);
        subtractButton.setY(y + 2);
        subtractButton.render(context, mouseX, mouseY, tickDelta);
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (addButton.mouseClicked(mouseX, mouseY, button) || subtractButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (addButton.mouseClicked(click, doubled) || subtractButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }
    //?}

    public void add(Button button) {
        if (value == null) value = 0;
        value++;
        setText(getValueAsString());
    }

    public void subtract(Button button) {
        if (value == null) value = 0;
        value--;
        setText(getValueAsString());
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.LIVES_ENTRY;
    }

    @Override
    public void onSave() {
        SimplePackets.SET_LIVES.sendToServer(List.of(fieldName.replaceFirst("dynamic_lives_",""), getValueAsString()));
    }

    @Override
    public boolean isSearchable() {
        return false;
    }
}
