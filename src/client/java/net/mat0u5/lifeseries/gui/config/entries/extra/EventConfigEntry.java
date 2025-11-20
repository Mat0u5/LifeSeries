package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

public class EventConfigEntry extends StringConfigEntry {
    Boolean canceled;
    Boolean defaultCanceled;
    Button canceledButton;
    public EventConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue, String canceledStr) {
        super(fieldName, displayName, description, value, defaultValue);
        Boolean canceledBool = null;
        if (canceledStr.equalsIgnoreCase("true")) canceledBool = true;
        if (canceledStr.equalsIgnoreCase("false")) canceledBool = false;
        this.defaultCanceled = canceledBool;
        this.canceled = canceledBool;
        canceledButton = Button.builder(Component.empty(), this::buttonClick)
                .bounds(0, 0, 60, 18)
                .build();
        updateButton();
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        updateButton();
        canceledButton.render(context, mouseX, mouseY, tickDelta);
    }

    public void buttonClick(Button button) {
        if (canceled == null) return;
        canceled = !canceled;
        updateButton();
    }

    public void updateButton() {
        canceledButton.active = canceled != null;
        String text = "CANCEL";
        if (canceled == null || !canceled) text = "PASS";
        canceledButton.setMessage(Component.nullToEmpty(text));
        canceledButton.setX(textField.getX() - 10 - canceledButton.getWidth());
        canceledButton.setY(textField.getY());
    }
    @Override
    public void resetToDefault() {
        super.resetToDefault();
        canceled = defaultCanceled;
        updateButton();
    }

    @Override
    public int labelEndX() {
        return super.labelEndX() + canceledButton.getWidth() + 10;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(canceled, defaultCanceled) || super.isModified();
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.EVENT_ENTRY;
    }

    @Override
    public void onSave() {
        String canceledStr = canceled == null ? "" : String.valueOf(canceled);
        NetworkHandlerClient.sendConfigUpdate(
                getValueType().toString(),
                getFieldName(),
                List.of(getValueAsString(), canceledStr)
        );
    }


    //? if <= 1.21.6 {
    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (canceledButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (canceledButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }
    *///?}

}
