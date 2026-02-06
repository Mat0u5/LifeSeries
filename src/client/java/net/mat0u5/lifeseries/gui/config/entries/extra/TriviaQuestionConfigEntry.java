package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.ConfigScreen;
import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.GroupConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.interfaces.IEntryGroupHeader;
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

public class TriviaQuestionConfigEntry extends ModifiableListEntry {
    boolean sentToServer = false;
    public InnerTriviaQuestionConfigEntry child;
    public GroupConfigEntry<InnerTriviaQuestionConfigEntry> renderAsGroup;

    public class InnerTriviaQuestionConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {
        String defaultText;
        String text;
        String triviaType;
        EditBox textField;
        private int maxTextFieldLength = 8192;
        private TriviaQuestionConfigEntry parent;

        public InnerTriviaQuestionConfigEntry(TriviaQuestionConfigEntry parent, String fieldName, String question, String type, List<ConfigEntry> answers) {
            super(fieldName, "", "");
            this.text = question;
            this.defaultText = question;
            this.triviaType = type;
            textField = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
            textField.setMaxLength(maxTextFieldLength);
            textField.setValue(question);
            textField.setResponder(this::onChanged);
            //? if <= 1.20 {
            /*textField.moveCursorToStart();
             *///?} else {
            textField.moveCursorToStart(false);
            //?}
            this.parent = parent;
        }

        @Override
        public void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Component questionText = Component.literal("Question:");
            RenderUtils.text(questionText,x + 25, y+6).colored(TextColors.LIGHT_GRAY).render(context, textRenderer);
            textField.setY(y+1);
            textField.setX(x + 25 + textRenderer.width(questionText) + 7);
            int buttonX = resetButton != null ? resetButton.getX() : width;
            textField.setWidth(buttonX-textField.getX()-10);
            textField.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public ConfigTypes getValueType() {
            return ConfigTypes.TRIVIA_QUESTION;
        }

        @Override
        public void expand() {
        }

        @Override
        public boolean isExpanded() {
            return true;
        }

        @Override
        public int expandTextX(int x, int width) {
            return x + width;
        }

        @Override
        public boolean showExpandIcon() {
            return false;
        }

        @Override
        public boolean showExpandText() {
            return false;
        }

        public void onChanged(String text) {
            this.text = textField.getValue();
            markChanged();
        }

        @Override
        public void resetToDefault() {
            textField.setValue(defaultText);
            for (TriviaAnswerConfigEntry answerConfigEntry : getAnswerEntries()) {
                answerConfigEntry.resetToDefault();
            }
        }

        @Override
        public boolean isModified() {
            if (!Objects.equals(textField.getValue(), defaultText)) return true;
            for (TriviaAnswerConfigEntry answerConfigEntry : getAnswerEntries()) {
                if (answerConfigEntry.isModified()) return true;
            }
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
        protected void updateHighlightAnimation(float tickDelta) {
            super.updateHighlightAnimation(tickDelta);
        }
        @Override
        public boolean hasResetButton() {
            return true;
        }
    }


    @Override
    public boolean isModified() {
        if (this.child.parentGroup == null) return false;
        if (this.child.parentGroup.getMainEntry().isModified()|| this.child.parentGroup.getMainEntry().changedForever) {
            return true;
        }
        for (ConfigEntry entry : this.child.parentGroup.getChildEntries()) {
            if (entry.isModified()|| entry.changedForever) return true;
        }
        return false;
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        if (super.mouseClickedEntry(mouseX, mouseY, button)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressedEntry(keyCode, scanCode, modifiers)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (super.charTypedEntry(chr, modifiers)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.charTyped(chr, modifiers);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        if (super.mouseClickedEntry(click, doubled)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.mouseClicked(click, doubled);
    }

    @Override
    protected boolean keyPressedEntry(KeyEvent input) {
        if (super.keyPressedEntry(input)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.keyPressed(input);
    }

    @Override
    protected boolean charTypedEntry(CharacterEvent input) {
        if (super.charTypedEntry(input)) return true;
        if (this.child.parentGroup == null) return false;
        return this.child.parentGroup.charTyped(input);
    }
    //?}

    @Override
    protected void updateHighlightAnimation(float tickDelta) {
        highlightAlpha = 0.0f;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.child.parentGroup != null) {
            this.child.parentGroup.setFocused(focused);
        }
    }

    @Override
    public int getMainEntryHeight() {
        if (this.child.parentGroup == null) {
            return super.getPreferredHeight();
        }
        return this.child.parentGroup.getPreferredHeight();
    }

    public TriviaQuestionConfigEntry(String fieldName, String question, String type, List<ConfigEntry> answers) {
        super(fieldName);
        this.child = new InnerTriviaQuestionConfigEntry(this, fieldName+"_inner", question, type, answers);
        renderAsGroup = new GroupConfigEntry<>(this.child, answers, false, true);
        renderAsGroup.setScreen(this.screen);
    }

    @Override
    protected void renderMainEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        renderAsGroup.render(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public ConfigEntry getNewEntry() {
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= getAnswerEntries().size(); i++) {
            answers.add("Answer #"+i);
        }
        TriviaQuestionConfigEntry newEntry = getEntry(child.triviaType, "Placeholder Question", 0, answers);
        newEntry.setScreen(this.screen);
        return newEntry;
    }

    @Override
    public void setScreen(ConfigScreen screen) {
        super.setScreen(screen);
        this.renderAsGroup.setScreen(screen);
    }

    public List<TriviaAnswerConfigEntry> getAnswerEntries() {
        List<TriviaAnswerConfigEntry> result = new ArrayList<>();
        if (child.parentGroup == null) return result;
        for (ConfigEntry childEntry : child.parentGroup.getChildEntries()) {
            if (childEntry instanceof TriviaAnswerConfigEntry answerEntry) {
                result.add(answerEntry);
            }
        }
        return result;
    }

    public static TriviaQuestionConfigEntry getEntry(String triviaType, String question, int correctAnswerIndex, List<String> answers) {
        String id = "dynamic_trivia_entry_"+ UUID.randomUUID();
        List<ConfigEntry> childEntries = new ArrayList<>();
        int answerIndex = 0;
        for (String answerText : answers) {
            answerIndex++;
            TriviaAnswerConfigEntry answerEntry = new TriviaAnswerConfigEntry(id+"_ans"+answerIndex, answerText, answerIndex, (answerIndex-1) == correctAnswerIndex);
            childEntries.add(answerEntry);
        }
        return new TriviaQuestionConfigEntry(id, question, triviaType, childEntries);
    }

    @Override
    public int firstEntryHeightAdd() {
        return 0;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TRIVIA_QUESTION;
    }

    @Override
    public void onSave() {
        if (sentToServer) return;
        List<ModifiableListEntry> allTriviaQuestions = getListEntries();

        List<String> list = new ArrayList<>();
        list.add(child.triviaType);

        for (ModifiableListEntry entry : allTriviaQuestions) {
            if (entry instanceof TriviaQuestionConfigEntry questionConfigEntry) {
                if (questionConfigEntry.sentToServer) return;
                int correctAnswerIndex = 1;
                List<String> answers = new ArrayList<>();
                for (TriviaAnswerConfigEntry answerConfigEntry : questionConfigEntry.getAnswerEntries()) {
                    String answerText = answerConfigEntry.text;
                    while (answerText.contains("~~~")) answerText = answerText.replace("~~~", "~~");
                    answers.add(answerConfigEntry.text);
                    if (answerConfigEntry.isCorrect) {
                        correctAnswerIndex = answerConfigEntry.answerIndex;
                    }
                }
                String questionText = questionConfigEntry.child.text;
                while (questionText.contains("~~~")) questionText = questionText.replace("~~~", "~~");
                list.add(questionText + "~~~" + correctAnswerIndex + "~~~" + String.join("~~~", answers));
            }
        }
        sentToServer = true;
        NetworkHandlerClient.sendStringListPacket(PacketNames.CONFIG_TRIVIA, list);
    }

    @Override
    public boolean canLoseFocusEasily() {
        return false;
    }

    @Override
    public boolean hasResetButton() {
        return false;
    }
}
