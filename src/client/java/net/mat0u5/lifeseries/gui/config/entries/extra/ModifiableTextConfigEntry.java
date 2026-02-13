package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.config.ModifiableTextManager;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.interfaces.IPopup;
import net.mat0u5.lifeseries.gui.config.entries.interfaces.ITextFieldAddonPopup;
import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.enums.Formatted;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ModifiableTextConfigEntry extends StringConfigEntry implements ITextFieldAddonPopup {
    private int firstPartHeight = PREFFERED_HEIGHT;
    public ModifiableTextConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue, 300, 14);
    }

    public boolean isFirst() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == 0;
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        boolean isFirst = isFirst();
        int currentY = y;
        if (isFirst) {
            currentY += 3;
            currentY += RenderUtils.text(
                            Component.literal("§7Argument referencing (args are listed in hover descriptions):\n§f  %s - next argument\n  %1$s - first argument,  %2$s for the second, ..."), x+10, currentY)
                    .wrapLines(width-10, 6).render(context, textRenderer);
            currentY += 6 + RenderUtils.text(
                    Component.literal("§7Formatting codes:\n§0#0 §1#1 §2#2 §3#3 §4#4 §5#5 §6#6 §7#7 §8#8 §9#9 §a#a §b#b §c#c §d#d §e#e §f#f §k#k§r§f §l#l§r§f §m#m§r§f §n#n§r§f §o#o§r§f §p#p§r§f")
                    , x+10, currentY).wrapLines(width-10, 6).render(context, textRenderer);
        }
        firstPartHeight = currentY - y;

        super.renderEntry(context, x, currentY, width, height, mouseX, mouseY, hovered, tickDelta);
        renderPopup(context, mouseX, mouseY, tickDelta);
    }

    private int firstEntryAddHeight() {
        return firstPartHeight;
    }

    @Override
    protected int getTextFieldPosY(int y, int height) {
        return isFirst() ? y + 1 : super.getTextFieldPosY(y, height);
    }


    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.MODIFIABLE_TEXT;
    }

    @Override
    public int additionalLabelOffsetY() {
        return isFirst() ? firstEntryAddHeight() -2 : -2;
    }

    @Override
    public int getPreferredHeight() {
        int add = -4;
        if (isFirst()) {
            add += firstEntryAddHeight();
        }
        return super.getPreferredHeight()+add;
    }

    @Override
    public int getResetButtonHeight() {
        return super.getResetButtonHeight()-2;
    }

    @Override
    public int additionalResetButtonOffsetY() {
        return isFirst() ? firstEntryAddHeight() -1 : -1;
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
        Formatted formatted = Formatted.STYLED;
        ModifiableText modifiableText = ModifiableText.fromName(this.fieldName);
        if (modifiableText != null) {
            formatted = modifiableText.getFormatted();
        }

        String currentText = textField.getValue();
        List<Component> args = new ArrayList<>();
        if (description != null && description.contains("Arguments: §f")) {
            for (String arg : description.split("Arguments: §f")[1].split(", ")) {
                arg = "<"+arg+">";
                if (formatted == Formatted.LOOSELY_STYLED) {
                    args.add(Component.literal("§o"+arg));
                }
                else {
                    args.add(Component.literal("§8§o"+arg));
                }
            }
        }
        return ModifiableTextManager.getFromRawValue(formatted, ModifiableTextManager.toMinecraftColorFormatting(currentText).replace("\\n", "\n"), true, args.toArray());
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

    @Override
    public boolean allowMultiline() {
        return true;
    }

    @Override
    public int multilineWrap() {
        return (resetButton.getX()+resetButton.getWidth()-(textField.getX() + textField.getWidth()/2))*2;
    }
}
