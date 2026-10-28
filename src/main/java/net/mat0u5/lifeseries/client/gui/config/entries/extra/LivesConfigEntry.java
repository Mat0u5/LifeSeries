package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.config.LifeSeriesConfigTypes;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.matlib.client.gui.config.entries.main.NullableIntegerConfigEntry;
import net.mat0u5.matlib.config.entry.ConfigGuiEntry;
import net.mat0u5.matlib.config.type.ConfigType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

//? if >= 1.21.9
import net.minecraft.client.input.*;

public class LivesConfigEntry extends NullableIntegerConfigEntry {
    public Button addButton;
    public Button subtractButton;
    public LivesConfigEntry(ConfigGuiEntry guiEntry, Integer value, Integer defaultValue) {
        super(guiEntry, value, defaultValue);

        addButton = Button.builder(Component.nullToEmpty("+"), this::add)
                .bounds(0, 0, 16, 16)
                .build();
        subtractButton = Button.builder(Component.nullToEmpty("-"), this::subtract)
                .bounds(0, 0, 16, 16)
                .build();
    }

    @Override
    protected void renderEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        addButton.setX(textField.getX() - 39);
        addButton.setY(y + 2);
        //~ renames_26_1_volatile, renames_26_4_volatile
        addButton.extractRenderState(context);
        subtractButton.setX(textField.getX() - 20);
        subtractButton.setY(y + 2);
        subtractButton.extractRenderState(context);
        //~ !renames_26_1_volatile, !renames_26_4_volatile
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
    public ConfigType<?> getValueType() {
        return LifeSeriesConfigTypes.LIVES_ENTRY;
    }

    @Override
    public Optional<ConfigGuiEntry> onSave() {
        SimplePackets.SET_LIVES.sendToServer(List.of(fieldName.replaceFirst("dynamic_lives_",""), getValueAsString()));
        return Optional.empty();
    }

    @Override
    public boolean isSearchable() {
        return false;
    }
}
