package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.client.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.client.gui.config.entries.GroupConfigEntry;
import net.mat0u5.lifeseries.client.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.client.utils.TextColors;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
//? if >= 1.21.9
import net.minecraft.client.input.*;

public class SoulmateConfigEntry extends ModifiableListEntry {
    private static final int MAX_TEXT_FIELD_LENGTH = 8192;
    private static final int FIELD_START_X = 40;
    private static final int FIELD_GAP = 15;

    public String player1;
    public String player2;
    public String defaultPlayer1;
    public String defaultPlayer2;

    protected final EditBox textFieldPlayer1;
    protected final EditBox textFieldPlayer2;

    public boolean sentToServer;

    public SoulmateConfigEntry(String fieldName, List<String> args) {
        this(fieldName, args.size() > 3 ? args.get(3) : "", args.size() > 4 ? args.get(4) : "");
    }

    public SoulmateConfigEntry(String fieldName, String player1, String player2) {
        super(fieldName);
        this.defaultPlayer1 = player1;
        this.defaultPlayer2 = player2;
        this.player1 = player1;
        this.player2 = player2;

        textFieldPlayer1 = new EditBox(textRenderer, 0, 0, 100, 18, Component.empty());
        textFieldPlayer2 = new EditBox(textRenderer, 0, 0, 100, 18, Component.empty());

        textFieldPlayer1.setValue(this.player1);
        textFieldPlayer2.setValue(this.player2);

        textFieldPlayer1.setMaxLength(MAX_TEXT_FIELD_LENGTH);
        textFieldPlayer2.setMaxLength(MAX_TEXT_FIELD_LENGTH);

        textFieldPlayer1.setResponder(this::onChanged);
        textFieldPlayer2.setResponder(this::onChanged);
    }

    @Override
    protected void renderMainEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int fieldX = x + FIELD_START_X;
        int fieldWidth = getFieldWidth(x, width);

        textFieldPlayer1.setY(y + 1);
        textFieldPlayer2.setY(y + 1);
        textFieldPlayer1.setX(fieldX);
        textFieldPlayer2.setX(fieldX + fieldWidth + FIELD_GAP);
        textFieldPlayer1.setWidth(fieldWidth);
        textFieldPlayer2.setWidth(fieldWidth);

        textFieldPlayer1.setTextColor(isValidEntryName(getPlayer1()) ? TextColors.WHITE : TextColors.PASTEL_RED);
        textFieldPlayer2.setTextColor(isValidEntryName(getPlayer2()) ? TextColors.WHITE : TextColors.PASTEL_RED);

        //~ renames_26_1_volatile
        textFieldPlayer1.extractRenderState(context, mouseX, mouseY, tickDelta);
        textFieldPlayer2.extractRenderState(context, mouseX, mouseY, tickDelta);
        //~ !renames_26_1_volatile

        RenderUtils.text(Component.nullToEmpty("§f&"), fieldX + fieldWidth + (FIELD_GAP / 2), y + 6).anchorCenter().render(context, textRenderer);
    }

    @Override
    public void renderFirstEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int fieldX = x + FIELD_START_X;
        int fieldWidth = getFieldWidth(x, width);

        Component header1Text = Component.nullToEmpty("§f🛈 Soulmate");
        Component header2Text = Component.nullToEmpty("§fSoulmate");

        RenderUtils.text(header1Text, fieldX + (fieldWidth / 2), y + 5).anchorCenter().render(context, textRenderer);
        RenderUtils.text(header2Text, fieldX + fieldWidth + FIELD_GAP + (fieldWidth / 2), y + 5).anchorCenter().render(context, textRenderer);

        int header1X = fieldX + (fieldWidth / 2) - (textRenderer.width(header1Text) / 2);
        if (hovered && mouseY >= y + 5 && mouseY <= y + 5 + textRenderer.lineHeight
                && mouseX >= header1X && mouseX <= header1X + textRenderer.width(header1Text)) {
            Component hoverText = Component.nullToEmpty(
                    "The two players that are paired together.\nBoth players need to have joined this server at least once, they do not need to be online.\nLeave a row completely empty to have no pairing there."
            );
            //? if <= 1.21.5 {
            /*context.renderTooltip(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
             *///?} else {
            context.setTooltipForNextFrame(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
            //?}
        }
    }

    private int getFieldWidth(int x, int width) {
        int fieldX = x + FIELD_START_X;
        int rightEdge = hasResetButton() ? resetButton.getX() - 10 : x + getEntryContentWidth(width);
        return Math.max(30, (rightEdge - fieldX - FIELD_GAP) / 2);
    }

    public void onChanged(String text) {
        this.player1 = textFieldPlayer1.getValue();
        this.player2 = textFieldPlayer2.getValue();
        markChanged();
        checkAllErrors();
    }

    public String getPlayer1() {
        return this.player1 == null ? "" : this.player1.trim();
    }

    public String getPlayer2() {
        return this.player2 == null ? "" : this.player2.trim();
    }

    public boolean isEmptyEntry() {
        return getPlayer1().isEmpty() && getPlayer2().isEmpty();
    }

    public static boolean isValidEntryName(String name) {
        if (name.isEmpty()) return true;
        if (PlayerUtils.isValidUsername(name)) return true;
        try {
            UUID.fromString(name);
            return true;
        }catch(Exception ignored) {}
        return false;
    }

    public void checkAllErrors() {
        for (ModifiableListEntry entry : getListEntries()) {
            if (entry instanceof SoulmateConfigEntry soulmateEntry) {
                soulmateEntry.checkErrors();
            }
        }
    }

    public void checkErrors() {
        String name1 = getPlayer1();
        String name2 = getPlayer2();

        if (isEmptyEntry()) {
            clearError();
            return;
        }
        if (name1.isEmpty() || name2.isEmpty()) {
            setError("Both soulmates have to be filled in. Clear both fields to remove this pairing.");
            return;
        }
        if (!isValidEntryName(name1)) {
            setError(invalidNameError(name1));
            return;
        }
        if (!isValidEntryName(name2)) {
            setError(invalidNameError(name2));
            return;
        }
        if (name1.equalsIgnoreCase(name2)) {
            setError("A player cannot be their own soulmate.");
            return;
        }
        for (ModifiableListEntry entry : getSisterEntries()) {
            if (!(entry instanceof SoulmateConfigEntry soulmateEntry)) continue;
            if (soulmateEntry.isEmptyEntry()) continue;
            String duplicate = null;
            if (soulmateEntry.matchesName(name1)) duplicate = name1;
            else if (soulmateEntry.matchesName(name2)) duplicate = name2;
            if (duplicate != null) {
                setError(TextUtils.formatString("{} already has another soulmate. Every player can only be paired up once.", duplicate));
                return;
            }
        }
        clearError();
    }

    private static String invalidNameError(String name) {
        return TextUtils.formatString(
                "{} is not a valid player name. Names are at most {} characters long and can only contain letters, numbers and underscores.",
                name, PlayerUtils.MAX_USERNAME_LENGTH
        );
    }

    public boolean matchesName(String name) {
        if (name.isEmpty()) return false;
        return getPlayer1().equalsIgnoreCase(name) || getPlayer2().equalsIgnoreCase(name);
    }

    @Override
    public ConfigEntry getNewEntry() {
        return new SoulmateConfigEntry("dynamic_soulmate_" + UUID.randomUUID(), "", "");
    }

    @Override
    public void deleteEntry(Button button) {
        GroupConfigEntry<?> group = this.parentGroup;
        super.deleteEntry(button);
        clearError();
        if (group == null) return;
        for (ConfigEntry entry : group.getChildEntries()) {
            if (entry instanceof SoulmateConfigEntry soulmateEntry) {
                soulmateEntry.checkErrors();
            }
        }
    }

    @Override
    public void resetToDefault() {
        textFieldPlayer1.setValue(defaultPlayer1);
        textFieldPlayer2.setValue(defaultPlayer2);
        checkAllErrors();
    }

    @Override
    public boolean isModified() {
        if (this.changedForever) return true;
        return canReset();
    }

    @Override
    public boolean canReset() {
        return !Objects.equals(textFieldPlayer1.getValue(), defaultPlayer1)
                || !Objects.equals(textFieldPlayer2.getValue(), defaultPlayer2);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            textFieldPlayer1.setFocused(false);
            textFieldPlayer2.setFocused(false);
        }
    }

    //? if <= 1.21.6 {
    /*@Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        boolean clickedField1 = textFieldPlayer1.mouseClicked(mouseX, mouseY, button);
        boolean clickedField2 = textFieldPlayer2.mouseClicked(mouseX, mouseY, button);
        textFieldPlayer1.setFocused(clickedField1);
        textFieldPlayer2.setFocused(clickedField2);
        if (clickedField1 || clickedField2) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (textFieldPlayer1.keyPressed(keyCode, scanCode, modifiers) ||
                textFieldPlayer2.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressedEntry(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (textFieldPlayer1.charTyped(chr, modifiers) ||
                textFieldPlayer2.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTypedEntry(chr, modifiers);
    }
    *///?} else {
    @Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        boolean clickedField1 = textFieldPlayer1.mouseClicked(click, doubled);
        boolean clickedField2 = textFieldPlayer2.mouseClicked(click, doubled);
        textFieldPlayer1.setFocused(clickedField1);
        textFieldPlayer2.setFocused(clickedField2);
        if (clickedField1 || clickedField2) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }

    @Override
    protected boolean keyPressedEntry(KeyEvent input) {
        if (textFieldPlayer1.keyPressed(input) ||
                textFieldPlayer2.keyPressed(input)) {
            return true;
        }
        return super.keyPressedEntry(input);
    }

    @Override
    protected boolean charTypedEntry(CharacterEvent input) {
        if (textFieldPlayer1.charTyped(input) ||
                textFieldPlayer2.charTyped(input)) {
            return true;
        }
        return super.charTypedEntry(input);
    }
    //?}

    @Override
    public void onSave() {
        List<String> allPairs = new ArrayList<>();
        for (ModifiableListEntry entry : getListEntries()) {
            if (!(entry instanceof SoulmateConfigEntry soulmateEntry)) continue;
            if (soulmateEntry.sentToServer) return;
            if (soulmateEntry.isEmptyEntry()) continue;
            allPairs.add(soulmateEntry.getPlayer1());
            allPairs.add(soulmateEntry.getPlayer2());
        }
        this.sentToServer = true;
        SimplePackets.SET_SOULMATES.sendToServer(allPairs);
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.SOULMATE_ENTRY;
    }
}
