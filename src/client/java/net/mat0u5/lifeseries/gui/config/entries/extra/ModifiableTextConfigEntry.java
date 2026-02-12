package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.interfaces.IPopup;
import net.mat0u5.lifeseries.gui.config.entries.interfaces.ITextFieldAddonPopup;
import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ModifiableTextConfigEntry extends StringConfigEntry implements ITextFieldAddonPopup {
    public ModifiableTextConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue, 350, 14);
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        renderPopup(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.MODIFIABLE_TEXT;
    }

    @Override
    public int additionalLabelOffsetY() {
        return -2;
    }

    @Override
    public int getPreferredHeight() {
        return super.getPreferredHeight()-4;
    }

    @Override
    public int getResetButtonHeight() {
        return super.getResetButtonHeight()-2;
    }

    @Override
    public int additionalResetButtonOffsetY() {
        return -1;
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
        if (textField == null) return null;
        String currentText = textField.getValue();
        List<Component> args = new ArrayList<>();
        if (description != null && description.contains("Arguments: §f")) {
            for (String arg : description.split("Arguments: §f")[1].split(", ")) {
                args.add(Component.literal("§8§o"+arg));
            }
        }
        return ModifiableText.getFromRawValue(ModifiableText.toMinecraftColorFormatting(currentText), true, args.toArray());
    }

    @Override
    public boolean shouldShowPopup() {
        if (textField == null) return false;
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
    public int getTextColor() {
        return TextColors.WHITE;
    }
}
