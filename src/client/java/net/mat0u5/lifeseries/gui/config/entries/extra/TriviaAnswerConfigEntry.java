package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import java.util.Objects;

//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

public class TriviaAnswerConfigEntry extends ModifiableListEntry {

    String defaultText;
    String text;
    EditBox textField;
    Button setCorrectButton;
    private int maxTextFieldLength = 8192;
    public int answerIndex;
    boolean isCorrect;
    boolean defaultIsCorrect;

    public TriviaAnswerConfigEntry(String fieldName, String answer, int index, boolean isCorrect) {
        super(fieldName);
        this.defaultText = answer;
        this.text = answer;
        this.answerIndex = index;
        this.isCorrect = isCorrect;
        this.defaultIsCorrect = isCorrect;
        textField = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
        textField.setMaxLength(maxTextFieldLength);
        textField.setValue(answer);
        textField.setResponder(this::onChanged);
        //? if <= 1.20 {
        /*textField.moveCursorToStart();
         *///?} else {
        textField.moveCursorToStart(false);
        //?}

        setCorrectButton = Button.builder(Component.nullToEmpty("✔"), this::setCorrect)
                .bounds(0, 0, 16, 16)
                .build();
    }

    @Override
    protected void renderMainEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int currentX = x + 25;

        Component questionText = Component.literal("Answer #"+answerIndex+":");
        RenderUtils.text(questionText,currentX, y+6).colored(isCorrect ? TextColors.PASTEL_LIME : TextColors.PASTEL_RED).render(context, textRenderer);
        currentX += textRenderer.width(questionText) + 5;

        setCorrectButton.setX(currentX);
        setCorrectButton.setY(y+2);
        setCorrectButton.render(context, mouseX, mouseY, tickDelta);
        setCorrectButton.active = !isCorrect;
        currentX += setCorrectButton.getWidth() + 5;

        textField.setY(y+1);
        textField.setX(currentX);
        textField.setWidth(resetButton.getX()-textField.getX()-10);
        textField.render(context, mouseX, mouseY, tickDelta);
    }

    public void onChanged(String text) {
        this.text = textField.getValue();
        markChanged();
    }

    public void setCorrect(Button button) {
        if (isCorrect) return;
        for (ConfigEntry entry : getSisterEntries()) {
            if (entry instanceof TriviaAnswerConfigEntry answerConfigEntry) {
                if (answerConfigEntry.isCorrect) {
                    answerConfigEntry.markChanged();
                    answerConfigEntry.isCorrect = false;
                }
            }
        }
        isCorrect = true;
        markChanged();
    }

    @Override
    public ConfigEntry getNewEntry() {
        return new TriviaAnswerConfigEntry(this.fieldName.substring(0, this.fieldName.length()-1)+(this.answerIndex+1), "Answer Text", this.answerIndex+1, false);
    }

    @Override
    public void deleteEntry(Button button) {
        boolean needsToMakeOtherCorrect = isCorrect;
        int answerIndex = 0;
        for (ConfigEntry entry : getSisterEntries()) {
            answerIndex++;
            if (entry instanceof TriviaAnswerConfigEntry answerConfigEntry) {
                answerConfigEntry.answerIndex = answerIndex;
                if (needsToMakeOtherCorrect) {
                    needsToMakeOtherCorrect = false;
                    answerConfigEntry.isCorrect = true;
                }
            }
        }
        super.deleteEntry(button);
    }

    @Override
    public ConfigEntry preventZeroEntries() {
        ConfigEntry newEntry = super.preventZeroEntries();
        if (newEntry != null) {
            if (newEntry instanceof TriviaAnswerConfigEntry answerConfigEntry) {
                answerConfigEntry.isCorrect = true;
                answerConfigEntry.answerIndex = 1;
            }
        }
        return newEntry;
    }

    @Override
    public void resetToDefault() {
        textField.setValue(defaultText);
        if (isCorrect != defaultIsCorrect) {
            this.isCorrect = this.defaultIsCorrect;
            for (ConfigEntry entry : getSisterEntries()) {
                if (entry instanceof TriviaAnswerConfigEntry answerConfigEntry) {
                    answerConfigEntry.isCorrect = answerConfigEntry.defaultIsCorrect;
                }
            }
        }
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(textField.getValue(), defaultText)|| this.changedForever) return true;
        if (isCorrect != defaultIsCorrect) return true;
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
            setCorrectButton.setFocused(false);
        }
    }

    @Override
    public int firstEntryHeightAdd() {
        return 0;
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        boolean buttonClick = setCorrectButton.mouseClicked(mouseX, mouseY, button);
        boolean textFieldClick = textField.mouseClicked(mouseX, mouseY, button);
        setCorrectButton.setFocused(buttonClick);
        textField.setFocused(textFieldClick);
        if (textFieldClick || buttonClick) {
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
        boolean buttonClick = setCorrectButton.mouseClicked(click, doubled);
        boolean textFieldClick = textField.mouseClicked(click, doubled);
        setCorrectButton.setFocused(buttonClick);
        textField.setFocused(textFieldClick);
        if (textFieldClick || buttonClick) {
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
    public ConfigTypes getValueType() {
        return ConfigTypes.TRIVIA_ANSWER;
    }

    @Override
    public void onSave() {
    }
}
