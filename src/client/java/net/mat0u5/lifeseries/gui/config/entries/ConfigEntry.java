package net.mat0u5.lifeseries.gui.config.entries;

import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public abstract class ConfigEntry {
    public static final int PREFFERED_HEIGHT = 20;
    protected static final int LABEL_OFFSET_X = 25;
    protected static final int LABEL_OFFSET_Y = 6;
    private static final float HIGHTLIGHT_FADE = 0.1f;

    private static final int RESET_BUTTON_OFFSET_X = -5;
    private static final int RESET_BUTTON_OFFSET_Y = 2;
    protected static final int RESET_BUTTON_WIDTH = 50;
    private static final int RESET_BUTTON_HEIGHT = 16;

    protected static final int ERROR_LABEL_OFFSET_X = - RESET_BUTTON_WIDTH + RESET_BUTTON_OFFSET_X - 2;
    protected static final int ERROR_LABEL_OFFSET_Y = 8;

    public static final int MAX_DESCRIPTION_WIDTH = 250;

    protected Font textRenderer;
    public ConfigScreen screen;
    protected final String fieldName;
    protected final String displayName;
    protected final String description;
    protected boolean hasError = false;
    protected String errorMessage = "";

    protected Button resetButton;
    public float highlightAlpha = 0.0f;
    protected boolean isHovered = false;
    private boolean isFocused = false;
    public GroupConfigEntry<?> parentGroup;
    protected List<GroupConfigEntry<?>> groupTopology = new ArrayList<>();
    private boolean isNew = false;
    public boolean changedForever = false;

    public ConfigEntry(String fieldName, String displayName, String description) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.description = description;
        this.textRenderer = Minecraft.getInstance().font;
        initializeResetButton();
    }

    public void setNew() {
        isNew = true;
    }

    private void initializeResetButton() {
        if (!hasResetButton()) {
            return;
        }
        resetButton = Button.builder(Component.nullToEmpty("Reset"), this::onResetClicked)
                .bounds(0, 0, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)
                .build();
    }

    private void onResetClicked(Button button) {
        resetToDefault();
        markChanged();
    }

    public void setScreen(ConfigScreen screen) {
        this.screen = screen;
    }

    public int additionalLabelOffsetY() {
        return 0;
    }
    public int additionalResetButtonOffsetY() {
        return 0;
    }

    public void render(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        isHovered = hovered;
        updateHighlightAnimation(tickDelta);

        if (highlightAlpha > 0.0f) {
            int highlightColor = TextColors.argb((int)(highlightAlpha * 128), 128, 128, 128);
            context.fill(x, y, x + width, y + height, highlightColor);
        }

        int textColor = hasError() ? TextColors.PASTEL_RED : TextColors.WHITE;
        int labelX = x + LABEL_OFFSET_X;
        int labelY = y + LABEL_OFFSET_Y + additionalLabelOffsetY();
        context.drawString(textRenderer, getDisplayName(), labelX, labelY, textColor);

        int resetButtonX = x + width - RESET_BUTTON_WIDTH + RESET_BUTTON_OFFSET_X;
        if (hasResetButton()) {
            resetButton.setX(resetButtonX);
            resetButton.setY(y + RESET_BUTTON_OFFSET_Y + additionalResetButtonOffsetY());
            resetButton.active = canReset();
            resetButton.render(context, mouseX, mouseY, tickDelta);
        }

        if (hasError()) {
            RenderUtils.text("⚠", x + width + ERROR_LABEL_OFFSET_X, y + ERROR_LABEL_OFFSET_Y).anchorRight().colored(TextColors.PASTEL_RED).render(context, textRenderer);
            if (isHovered) {
                Component errorText = TextUtils.format("§cERROR:\n{}",getErrorMessage());
                //? if <= 1.21.5 {
                /*context.renderTooltip(textRenderer, textRenderer.split(errorText, MAX_DESCRIPTION_WIDTH), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                 *///?} else {
                context.setTooltipForNextFrame(textRenderer, textRenderer.split(errorText, MAX_DESCRIPTION_WIDTH), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
                //?}
            }
        }
        else if (description != null && !description.isEmpty()) {
            if (mouseX >= labelX&& mouseX <= labelX + textRenderer.width(getDisplayName()) &&
                mouseY >= labelY && mouseY <= labelY + textRenderer.lineHeight) {
                Component descriptionText = getDisplayName().withStyle(ChatFormatting.UNDERLINE).append("§r\n"+description);
                //? if <= 1.21.5 {
                /*context.renderTooltip(textRenderer, textRenderer.split(descriptionText, MAX_DESCRIPTION_WIDTH), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                 *///?} else {
                context.setTooltipForNextFrame(textRenderer, textRenderer.split(descriptionText, MAX_DESCRIPTION_WIDTH), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
                //?}
            }
        }

        if (isNew) {
            context.drawString(textRenderer, "New", 2, labelY, TextColors.LIGHT_GRAY_A128);
        }


        renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    protected void updateHighlightAnimation(float tickDelta) {
        if (isHovered) {
            highlightAlpha = 1.0f;
        } else {
            highlightAlpha = Math.max(0.0f, highlightAlpha - tickDelta * HIGHTLIGHT_FADE);
        }
    }

    protected int getEntryContentWidth(int totalWidth) {
        return totalWidth - RESET_BUTTON_WIDTH - 15;
    }

    //? if <= 1.21.6 {
    /*protected abstract boolean mouseClickedEntry(double mouseX, double mouseY, int button);
    protected abstract boolean keyPressedEntry(int keyCode, int scanCode, int modifiers);
    protected abstract boolean charTypedEntry(char chr, int modifiers);
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasResetButton() && resetButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return mouseClickedEntry(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return keyPressedEntry(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        return charTypedEntry(chr, modifiers);
    }
    *///?} else {
    protected abstract boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled);
    protected abstract boolean keyPressedEntry(KeyEvent keyInput);
    protected abstract boolean charTypedEntry(CharacterEvent charInput);
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (hasResetButton() && resetButton.mouseClicked(click, doubled)) {
            return true;
        }
        return mouseClickedEntry(click, doubled);
    }

    public boolean keyPressed(KeyEvent keyInput) {
        return keyPressedEntry(keyInput);
    }

    public boolean charTyped(CharacterEvent charInput) {
        return charTypedEntry(charInput);
    }
    //?}

    public void setFocused(boolean focused) {
        setActualFocused(focused);
        if (focused && screen != null) {
            screen.setFocusedEntry(this);
        }
    }

    protected void setActualFocused(boolean focused) {
        this.isFocused = focused;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public boolean canLoseFocusEasily() {
        return true;
    }

    public boolean isTopologyFocused() {
        if (groupTopology.size() >= 2) {
            return groupTopology.get(1).isFocused();
        }
        return isFocused();
    }

    public int getPreferredHeight() {
        return PREFFERED_HEIGHT;
    }

    protected abstract void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta);
    public abstract void resetToDefault();

    public abstract Object getValue();
    public abstract String getValueAsString();
    public abstract Object getDefaultValue();
    public abstract String getDefaultValueAsString();
    public abstract Object getStartingValue();
    public abstract String getStartingValueAsString();
    public abstract ConfigTypes getValueType();
    public abstract void setValue(Object value);

    public boolean isModified() {
        return !Objects.equals(getValue(), getStartingValue());
    }

    public boolean canReset() {
        return !Objects.equals(getValue(), getDefaultValue());
    }

    public String getFieldName() {
        return fieldName;
    }

    public MutableComponent getDisplayName() {
        return Component.literal(displayName);
    }

    public String getDescription() {
        return description;
    }

    public boolean hasError() {
        if (hasError) return true;
        if (parentGroup != null && parentGroup.getMainEntry() == this && parentGroup.hasError()) return true;
        return false;
    }

    public String getErrorMessage() {
        if (!hasError && parentGroup != null && parentGroup.getMainEntry() == this && parentGroup.hasError()) {
            return "A child entry has an error.";
        }
        return errorMessage;
    }

    protected void setError(String errorMessage) {
        this.hasError = true;
        this.errorMessage = errorMessage;
    }

    protected void clearError() {
        this.hasError = false;
        this.errorMessage = "";
    }

    public void markChanged() {
        if (screen != null) {
            screen.onEntryValueChanged();
        }
    }

    public void markChangedForever() {
        changedForever = true;
    }

    public boolean hasResetButton() {
        return true;
    }

    public boolean sendToServer() {
        return true;
    }

    public boolean isSearchable() {
        return true;
    }

    public void onSave() {
        NetworkHandlerClient.sendConfigUpdate(
                getValueType().toString(),
                getFieldName(),
                List.of(getValueAsString())
        );
    }
}