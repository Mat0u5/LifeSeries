package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public class SecretLifeTaskConfigEntry extends ModifiableListEntry {
    protected final EditBox textField;
    String taskType;
    String text;
    String defaultText;
    private int maxTextFieldLength = 8192;
    private boolean sentToServer = false;

    public SecretLifeTaskConfigEntry(String fieldName, List<String> info) {
        super(fieldName);
        textField = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
        this.taskType = info.get(3);
        this.defaultText = info.get(4);
        this.text = info.get(4);
        textField.setMaxLength(maxTextFieldLength);
        textField.setValue(defaultText);
        textField.setResponder(this::onChanged);
        //? if <= 1.20 {
        /*textField.moveCursorToStart();
         *///?} else {
        textField.moveCursorToStart(false);
        //?}
    }

    @Override
    protected void renderMainEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        textField.setY(y+1);
        textField.setX(x + 25);
        textField.setWidth(resetButton.getX()-textField.getX()-10);
        textField.render(context, mouseX, mouseY, tickDelta);
    }
    @Override
    public void renderFirstEntryExtras(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int textX = x - 5;
        int textY = y;
        RenderUtils.text(Component.literal("Available text replacements:"), textX, textY).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"\\n\" - creates a new line in the task book"), textX, textY+9).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"\\p\" - creates a new page in the task book"), textX, textY+18).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"${random_player}\" - replaced with a random player name"), textX, textY+27).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"${green}\" - replaced with the word 'green', if there are any greens left"), textX, textY+36).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"${yellow}\" - replaced with the word 'yellow', if there are any yellows left"), textX, textY+45).colored(TextColors.GRAY).render(context, textRenderer);
        RenderUtils.text(Component.literal(" \"${red}\" - replaced with the word 'red', if there are any reds left"), textX, textY+54).colored(TextColors.GRAY).render(context, textRenderer);
    }

    @Override
    public int firstEntryHeightAdd() {
        return 64;
    }

    public void onChanged(String text) {
        this.text = textField.getValue();
        markChanged();
    }

    @Override
    public ConfigEntry getNewEntry() {
        return new SecretLifeTaskConfigEntry("dynamic_task_entry_"+ UUID.randomUUID(), List.of("", "", "", taskType, "New Task"));
    }

    @Override
    public void resetToDefault() {
        textField.setValue(defaultText);
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(textField.getValue(), defaultText)) return true;
        return false;
    }

    @Override
    public boolean canReset() {
        return isModified();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            textField.setFocused(false);
        }
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.SECRET_TASK;
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        boolean textFieldClick = textField.mouseClicked(mouseX, mouseY, button);
        textField.setFocused(textFieldClick);
        if (textFieldClick) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (textField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressedEntry(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (textField.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTypedEntry(chr, modifiers);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        boolean textFieldClick = textField.mouseClicked(click, doubled);
        textField.setFocused(textFieldClick);
        if (textFieldClick) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }

    @Override
    protected boolean keyPressedEntry(KeyEvent input) {
        if (textField.keyPressed(input)) {
            return true;
        }
        return super.keyPressedEntry(input);
    }

    @Override
    protected boolean charTypedEntry(CharacterEvent input) {
        if (textField.charTyped(input)) {
            return true;
        }
        return super.charTypedEntry(input);
    }
    //?}

    @Override
    public void onSave() {
        List<String> list = new ArrayList<>();
        list.add(taskType);
        for (ModifiableListEntry entry : getListEntries()) {
            if (entry instanceof SecretLifeTaskConfigEntry taskEntry) {
                if (taskEntry.sentToServer) return;
                String value = taskEntry.text;
                if (value == null || value.isEmpty()) continue;
                list.add(value);
            }
        }
        this.sentToServer = true;
        SimplePackets.CONFIG_SECRET_TASK.sendToServer(list);
    }
}
