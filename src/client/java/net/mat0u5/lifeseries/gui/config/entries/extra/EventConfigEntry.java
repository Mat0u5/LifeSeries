package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

//? if <= 1.21.9 {
import net.minecraft.Util;
//?} else {
/*import net.minecraft.util.Util;
 *///?}

public class EventConfigEntry extends StringConfigEntry {
    Boolean canceled;
    Boolean defaultCanceled;
    Button canceledButton;
    public static final String tutorialLink = "https://mat0u5.github.io/LifeSeries-docs/integration/datapacks.html#events";
    final Button openTutorialButton;

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
        openTutorialButton = Button.builder(Component.nullToEmpty("HERE"), this::openTutorial)
                .bounds(0, 0, 35, 18)
                .build();
        updateButton();
    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        updateButton();
        canceledButton.render(context, mouseX, mouseY, tickDelta);
        boolean isFirst = isFirst();
        openTutorialButton.visible = isFirst;
        if (isFirst) {
            openTutorialButton.render(context, mouseX, mouseY, tickDelta);
            openTutorialButton.setY(y+1);
            Component part1 = Component.nullToEmpty("§cLearn how to use Events");
            Component part2 = Component.nullToEmpty("§cin the Life Series Wiki.");
            RenderUtils.drawTextLeft(context, textRenderer, part1, x+10, y+6);
            int widthText = textRenderer.width(part1);
            openTutorialButton.setX(x+widthText+15);
            RenderUtils.drawTextLeft(context, textRenderer, part2, x+widthText+openTutorialButton.getWidth()+20, y+6);
        }
        super.renderEntry(context, x, y + (isFirst?PREFFERED_HEIGHT:0), width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public int additionalLabelOffsetY() {
        return isFirst() ? PREFFERED_HEIGHT : 0;
    }

    @Override
    public int additionalResetButtonOffsetY() {
        return isFirst() ? PREFFERED_HEIGHT : 0;
    }

    public boolean isFirst() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == 0;
    }
    @Override
    public int getPreferredHeight() {
        int heightMultiplier = 1;
        if (isFirst()) heightMultiplier++;
        return PREFFERED_HEIGHT * heightMultiplier;
    }

    public void openTutorial(Button button) {
        Util.getPlatform().openUri(tutorialLink);
    }

    public void buttonClick(Button button) {
        if (canceled == null) return;
        canceled = !canceled;
        updateButton();
    }

    public void updateButton() {
        canceledButton.active = canceled != null;
        String text = "OVERRIDE";
        if (canceled == null || !canceled) text = "ALLOW";
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
    public boolean canReset() {
        return isModified();
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
        if (canceledButton.mouseClicked(mouseX, mouseY, button) || openTutorialButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (canceledButton.mouseClicked(click, doubled) || openTutorialButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }
    *///?}

}
