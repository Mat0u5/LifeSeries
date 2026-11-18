package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.config.LifeSeriesConfigGuiClient;
import net.mat0u5.lifeseries.config.LifeSeriesConfigTypes;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.matlib.client.gui.config.ConfigScreen;
import net.mat0u5.matlib.client.gui.config.entries.ConfigEntry;
import net.mat0u5.matlib.client.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.matlib.client.gui.config.entries.GroupConfigEntry;
import net.mat0u5.matlib.client.gui.config.entries.ModifiableListEntry;
import net.mat0u5.matlib.client.gui.config.entries.interfaces.IEntryGroupHeader;
import net.mat0u5.matlib.client.render.RenderUtils;
import net.mat0u5.matlib.client.utils.TextColors;
import net.mat0u5.matlib.config.entry.ConfigGuiEntry;
import net.mat0u5.matlib.config.type.ConfigType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.*;

//? if >= 1.21.9
import net.minecraft.client.input.*;

public class TriviaQuestionConfigEntry extends ModifiableListEntry {
    private static final int TOGGLE_BUTTON_SIZE = 16;
    boolean sentToServer = false;
    public InnerTriviaQuestionConfigEntry child;
    public GroupConfigEntry<InnerTriviaQuestionConfigEntry> renderAsGroup;

    public class InnerTriviaQuestionConfigEntry extends EmptyConfigEntry implements IEntryGroupHeader {
        String defaultText;
        String text;
        String triviaType;
        EditBox textField;
        String defaultPlayer;
        EditBox playerField;
        Button playerToggleButton;
        boolean showPlayer;
        boolean defaultShowPlayer;
        private int maxTextFieldLength = 8192;
        private TriviaQuestionConfigEntry parent;

        public InnerTriviaQuestionConfigEntry(TriviaQuestionConfigEntry parent, ConfigGuiEntry guiEntry, String question, String type, String player, List<ConfigEntry> answers) {
            super(guiEntry);
            this.text = question;
            this.defaultText = question;
            this.triviaType = type;
            textField = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
            textField.setMaxLength(maxTextFieldLength);
            textField.setValue(question);
            textField.setResponder(this::onChanged);

            this.defaultPlayer = player;
            this.showPlayer = !player.isEmpty() && canHavePlayer();
            this.defaultShowPlayer = showPlayer;
            playerField = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
            playerField.setMaxLength(64);
            playerField.setValue(player);
            playerField.setResponder(value -> markChanged());

            //? if <= 1.20 {
            /*textField.moveCursorToStart();
            playerField.moveCursorToStart();
            *///?} else {
            textField.moveCursorToStart(false);
            playerField.moveCursorToStart(false);
            //?}

            playerToggleButton = Button.builder(Component.empty(), button -> togglePlayer())
                    .bounds(0, 0, TOGGLE_BUTTON_SIZE, TOGGLE_BUTTON_SIZE)
                    .build();
            playerToggleButton.setTooltip(Tooltip.create(Component.literal("Player specific question.\nOnly that specific player gets them.")));
            this.parent = parent;
        }

        private void togglePlayer() {
            showPlayer = !showPlayer;
            if (!showPlayer) playerField.setFocused(false);
        }

        public String getPlayer() {
            return playerField.getValue().trim();
        }

        @Override
        public int getPreferredHeight() {
            int height = super.getPreferredHeight();
            return showPlayer ? height * 2 : height;
        }

        @Override
        public void renderEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Component questionText = Component.literal("Question:");
            RenderUtils.text(questionText,x + 25, y+6).colored(TextColors.LIGHT_GRAY).render(context, textRenderer);
            int fieldX = x + 25 + textRenderer.width(questionText) + 7;
            int buttonX = resetButton != null ? resetButton.getX() : width;
            int fieldEndX = buttonX - 10;
            int rowY = y + super.getPreferredHeight();
            if (showPlayer) {
                Component playerText = Component.literal("Player:");
                RenderUtils.text(playerText, fieldX - 7 - textRenderer.width(playerText), rowY+6).colored(TextColors.LIGHT_GRAY).render(context, textRenderer);
            }

            //~ renames_26_1_volatile
            if (canHavePlayer()) {
                ChatFormatting color = !showPlayer && !getPlayer().isEmpty() ? ChatFormatting.YELLOW : ChatFormatting.WHITE;
                playerToggleButton.setMessage(Component.literal(showPlayer ? "▲" : "▼").withStyle(color));
                playerToggleButton.setX(fieldEndX - TOGGLE_BUTTON_SIZE);
                playerToggleButton.setY(y+2);
                playerToggleButton.extractRenderState(context, mouseX, mouseY, tickDelta);
                fieldEndX -= TOGGLE_BUTTON_SIZE + 5;
            }

            textField.setY(y+1);
            textField.setX(fieldX);
            textField.setWidth(fieldEndX-fieldX);
            textField.extractRenderState(context, mouseX, mouseY, tickDelta);

            if (showPlayer) {
                playerField.setY(rowY+1);
                playerField.setX(fieldX);
                playerField.setWidth(fieldEndX-fieldX);
                playerField.extractRenderState(context, mouseX, mouseY, tickDelta);
            }
            //~ !renames_26_1_volatile
        }

        @Override
        public ConfigType<?> getValueType() {
            return LifeSeriesConfigTypes.TRIVIA_QUESTION;
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
            playerField.setValue(defaultPlayer);
            showPlayer = defaultShowPlayer;
            for (TriviaAnswerConfigEntry answerConfigEntry : getAnswerEntries()) {
                answerConfigEntry.resetToDefault();
            }
        }

        @Override
        public boolean isModified() {
            if (!Objects.equals(textField.getValue(), defaultText)) return true;
            if (!Objects.equals(getPlayer(), defaultPlayer)) return true;
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
                playerField.setFocused(false);
                playerToggleButton.setFocused(false);
            }
        }

        //? if <= 1.21.6 {
        /*@Override
        protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
            if (canHavePlayer() && playerToggleButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            boolean textFieldClick = textField.mouseClicked(mouseX, mouseY, button);
            textField.setFocused(textFieldClick);
            boolean playerFieldClick = showPlayer && playerField.mouseClicked(mouseX, mouseY, button);
            playerField.setFocused(playerFieldClick);
            if (textFieldClick || playerFieldClick) {
                return true;
            }
            return super.mouseClickedEntry(mouseX, mouseY, button);
        }

        @Override
        protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
            if (textField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (showPlayer && playerField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressedEntry(keyCode, scanCode, modifiers);
        }

        @Override
        protected boolean charTypedEntry(char chr, int modifiers) {
            if (textField.charTyped(chr, modifiers)) {
                return true;
            }
            if (showPlayer && playerField.charTyped(chr, modifiers)) {
                return true;
            }
            return super.charTypedEntry(chr, modifiers);
        }
        *///?} else {
        @Override
        protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
            if (canHavePlayer() && playerToggleButton.mouseClicked(click, doubled)) {
                return true;
            }
            boolean textFieldClick = textField.mouseClicked(click, doubled);
            textField.setFocused(textFieldClick);
            boolean playerFieldClick = showPlayer && playerField.mouseClicked(click, doubled);
            playerField.setFocused(playerFieldClick);
            if (textFieldClick || playerFieldClick) {
                return true;
            }
            return super.mouseClickedEntry(click, doubled);
        }

        @Override
        protected boolean keyPressedEntry(KeyEvent input) {
            if (textField.keyPressed(input)) {
                return true;
            }
            if (showPlayer && playerField.keyPressed(input)) {
                return true;
            }
            return super.keyPressedEntry(input);
        }

        @Override
        protected boolean charTypedEntry(CharacterEvent input) {
            if (textField.charTyped(input)) {
                return true;
            }
            if (showPlayer && playerField.charTyped(input)) {
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

    private static boolean canHavePlayer() {
        return !LifeSeries.isSeason(Seasons.NICE_LIFE); // Nice life has global trivia questions
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

    public TriviaQuestionConfigEntry(ConfigGuiEntry guiEntry, String question, String type, String player, List<ConfigEntry> answers) {
        super(guiEntry);
        this.child = new InnerTriviaQuestionConfigEntry(this, LifeSeriesConfigGuiClient.copyWithKey(guiEntry, fieldName+"_inner"), question, type, player, answers);
        renderAsGroup = new GroupConfigEntry<>(this.child, answers, false, true);
        renderAsGroup.setScreen(this.screen);
    }

    @Override
    protected void renderMainEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        renderAsGroup.render(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public ConfigEntry getNewEntry() {
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= getAnswerEntries().size(); i++) {
            answers.add("Answer #"+i);
        }
        TriviaQuestionConfigEntry newEntry = getEntry(inboundGuiEntry, child.triviaType, "Placeholder Question", 0, "", answers);
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

    public static TriviaQuestionConfigEntry fromGuiEntry(ConfigGuiEntry guiEntry) {
        List<String> args = new ArrayList<>(guiEntry.args);
        String triviaType = args.remove(0);
        String question = args.remove(0);
        int correctAnswerIndex = Integer.parseInt(args.remove(0));
        String player = args.remove(0);
        return getEntry(guiEntry, triviaType, question, correctAnswerIndex, player, args);
    }

    public static TriviaQuestionConfigEntry getEntry(ConfigGuiEntry base, String triviaType, String question, int correctAnswerIndex, String player, List<String> answers) {
        String id = "dynamic_trivia_entry_"+ UUID.randomUUID();
        List<ConfigEntry> childEntries = new ArrayList<>();
        int answerIndex = 0;
        for (String answerText : answers) {
            answerIndex++;
            TriviaAnswerConfigEntry answerEntry = new TriviaAnswerConfigEntry(LifeSeriesConfigGuiClient.copyWithKey(base, id+"_ans"+answerIndex), answerText, answerIndex, (answerIndex-1) == correctAnswerIndex);
            childEntries.add(answerEntry);
        }
        return new TriviaQuestionConfigEntry(LifeSeriesConfigGuiClient.copyWithKey(base, id), question, triviaType, player, childEntries);
    }

    @Override
    public int firstEntryHeightAdd() {
        return 0;
    }

    @Override
    public ConfigType<?> getValueType() {
        return LifeSeriesConfigTypes.TRIVIA_QUESTION;
    }

    private static String removeIllegal(String text) {
        while (text.contains("~~~")) text = text.replace("~~~", "~~");
        return text;
    }

    @Override
    public Optional<ConfigGuiEntry> onSave() {
        if (sentToServer) return Optional.empty();
        List<ModifiableListEntry> allTriviaQuestions = getListEntries();

        List<String> list = new ArrayList<>();
        list.add(child.triviaType);

        for (ModifiableListEntry entry : allTriviaQuestions) {
            if (entry instanceof TriviaQuestionConfigEntry questionConfigEntry) {
                if (questionConfigEntry.sentToServer) return Optional.empty();
                int correctAnswerIndex = 1;
                List<String> answers = new ArrayList<>();
                for (TriviaAnswerConfigEntry answerConfigEntry : questionConfigEntry.getAnswerEntries()) {
                    answers.add(removeIllegal(answerConfigEntry.text));
                    if (answerConfigEntry.isCorrect) {
                        correctAnswerIndex = answerConfigEntry.answerIndex;
                    }
                }
                String questionText = removeIllegal(questionConfigEntry.child.text);
                String player = removeIllegal(questionConfigEntry.child.getPlayer());
                list.add(questionText + "~~~" + correctAnswerIndex + "~~~" + player + "~~~" + String.join("~~~", answers));
            }
        }
        sentToServer = true;
        SimplePackets.CONFIG_TRIVIA.sendToServer(list);
        return Optional.empty();
    }

    @Override
    public boolean canLoseFocusEasily() {
        return false;
    }

    @Override
    public boolean hasResetButton() {
        return false;
    }

    @Override
    public List<ConfigEntry> getInnerEntries() {
        List<ConfigEntry> innerEntries = new ArrayList<>(this.renderAsGroup.getChildEntries());
        innerEntries.add(this.renderAsGroup.getMainEntry());
        return innerEntries;
    }
}
