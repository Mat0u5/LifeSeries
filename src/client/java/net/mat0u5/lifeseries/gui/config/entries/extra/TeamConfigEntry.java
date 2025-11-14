package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.EmptyConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
//? if >= 1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
*///?}

public class TeamConfigEntry extends EmptyConfigEntry {
    public String teamNum;
    public String teamName;
    public String teamColor;
    public String allowedKill;
    public String gainLifeKill;
    public String defaultTeamNum;
    public String defaultTeamName;
    public String defaultTeamColor;
    public String defaultAllowedKill;
    public String defaultGainLifeKill;
    private int maxTextFieldLength = 8192;

    protected final EditBox textFieldLives;
    protected final EditBox textFieldName;
    protected final EditBox textFieldColor;
    protected final EditBox textFieldAllowedKill;
    protected final EditBox textFieldGainLife;
    protected final Button deleteEntryButton;
    protected final Button addEntryButton;


    public TeamConfigEntry(String fieldName, List<String> args) {
        super(fieldName, "", "");
        this.defaultTeamNum = args.get(3);
        this.defaultTeamName = args.get(4);
        this.defaultTeamColor = args.get(5);
        this.defaultAllowedKill = args.get(6);
        this.defaultGainLifeKill = args.get(7);
        this.teamNum = defaultTeamNum;
        this.teamName = defaultTeamName;
        this.teamColor = defaultTeamColor;
        this.allowedKill = defaultAllowedKill;
        this.gainLifeKill = defaultGainLifeKill;

        textFieldLives = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
        textFieldName = new EditBox(textRenderer, 0, 0, 80, 18, Component.empty());
        textFieldColor = new EditBox(textRenderer, 0, 0, 80, 18, Component.empty());
        textFieldAllowedKill = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());
        textFieldGainLife = new EditBox(textRenderer, 0, 0, 30, 18, Component.empty());

        textFieldLives.setValue(teamNum);
        textFieldName.setValue(teamName);
        textFieldColor.setValue(teamColor);
        textFieldAllowedKill.setValue(allowedKill);
        textFieldGainLife.setValue(gainLifeKill);

        textFieldLives.setResponder(this::onChanged);
        textFieldName.setResponder(this::onChanged);
        textFieldColor.setResponder(this::onChanged);
        textFieldAllowedKill.setResponder(this::onChanged);
        textFieldGainLife.setResponder(this::onChanged);

        textFieldLives.setMaxLength(maxTextFieldLength);
        textFieldName.setMaxLength(maxTextFieldLength);
        textFieldColor.setMaxLength(maxTextFieldLength);
        textFieldAllowedKill.setMaxLength(maxTextFieldLength);
        textFieldGainLife.setMaxLength(maxTextFieldLength);

        deleteEntryButton = Button.builder(Component.nullToEmpty("\uD83D\uDDD1"), this::deleteEntry)
                .bounds(0, 0, 16, 16)
                .build();
        addEntryButton = Button.builder(Component.nullToEmpty("+"), this::addEntry)
                .bounds(0, 0, 16, 16)
                .build();

    }

    @Override
    protected void renderEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int field1X = x+40;
        int field2X = x+80;
        int field3X = x+170;
        int field4X = x+260;
        int field5X = x+320;
        int field1CenterX = field1X + 15;
        int field2CenterX = field2X + 30;
        int field3CenterX = field3X + 30;
        int field4CenterX = field4X + 15;
        int field5CenterX = field5X + 15;
        if (isFirst()) {
            Component header1Text = Component.nullToEmpty("§f\uD83D\uDEC8 Lives");
            Component header2Text = Component.nullToEmpty("§fName");
            Component header3Text = Component.nullToEmpty("§fColor");
            Component header4Text = Component.nullToEmpty("§f\uD83D\uDEC8 Can Kill");
            Component header5Text = Component.nullToEmpty("§f\uD83D\uDEC8 Gain Life");
            RenderUtils.drawTextCenter(context, textRenderer, header1Text, field1CenterX, y+5);
            RenderUtils.drawTextCenter(context, textRenderer, header2Text, field2CenterX, y+5);
            RenderUtils.drawTextCenter(context, textRenderer, header3Text, field3CenterX, y+5);
            RenderUtils.drawTextCenter(context, textRenderer, header4Text, field4CenterX, y+5);
            RenderUtils.drawTextCenter(context, textRenderer, header5Text, field5CenterX, y+5);

            if (hovered && mouseY >= y + 5 && mouseY <= y + 5 + textRenderer.lineHeight) {
                Component hoverText = null;
                if (mouseX >= field1X && mouseX <= field1X + textRenderer.width(header1Text)) {
                    hoverText = Component.nullToEmpty("Lives boundary where players are put into this team (Rounded to nearest team with boundary <= lives).");
                }
                if (mouseX >= field4X && mouseX <= field4X + textRenderer.width(header4Text)) {
                    hoverText = Component.nullToEmpty("Lives boundary where this team can kill.\nFor example if set to 2, this team can kill any players with at least 2 lives.");
                }
                if (mouseX >= field5X && mouseX <= field5X + textRenderer.width(header5Text)) {
                    hoverText = Component.nullToEmpty("Lives boundary where this team can gain lives for killing.\nFor example if set to 4, this team will gain a life for killing players with at least 4 lives.");
                }
                if (hoverText != null) {
                    //? if <= 1.21.5 {
                    context.renderTooltip(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                    //?} else {
                    /*context.setTooltipForNextFrame(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
                     *///?}
                }
            }

            y += PREFFERED_HEIGHT;
        }
        textFieldLives.setY(y+1);
        textFieldName.setY(y+1);
        textFieldColor.setY(y+1);
        textFieldAllowedKill.setY(y+1);
        textFieldGainLife.setY(y+1);

        textFieldLives.setX(field1X);
        textFieldName.setX(field2X);
        textFieldColor.setX(field3X);
        textFieldAllowedKill.setX(field4X);
        textFieldGainLife.setX(field5X);


        textFieldLives.render(context, mouseX, mouseY, tickDelta);
        textFieldName.render(context, mouseX, mouseY, tickDelta);
        textFieldColor.render(context, mouseX, mouseY, tickDelta);
        textFieldAllowedKill.render(context, mouseX, mouseY, tickDelta);
        textFieldGainLife.render(context, mouseX, mouseY, tickDelta);

        deleteEntryButton.setX(x+5);
        deleteEntryButton.setY(y+2);
        deleteEntryButton.render(context, mouseX, mouseY, tickDelta);

        addEntryButton.setX(x+10);
        addEntryButton.setY(y+PREFFERED_HEIGHT+2);
        if (isLast()) {
            addEntryButton.render(context, mouseX, mouseY, tickDelta);
        }

        deleteEntryButton.active = !isDefaultTeam();
        textFieldLives.setEditable(!isDefaultTeam());

        if (getTeamNum() == null) textFieldLives.setTextColor(TextColors.PASTEL_RED);
        else textFieldLives.setTextColor(TextColors.WHITE);
        if (getTeamColor() == null) {
            textFieldColor.setTextColor(TextColors.PASTEL_RED);
            textFieldName.setTextColor(TextColors.WHITE);
        }
        else {
            textFieldColor.setTextColor(TextColors.WHITE);
            textFieldName.setTextColor(getTeamColor().getColor());
        }
        Integer currentTeamAllowed = getTeamAllowedKill();
        Integer currentTeamGain = getTeamGainLifeKill();
        if ((currentTeamAllowed == null && !this.allowedKill.isEmpty()) || (currentTeamAllowed != null && currentTeamAllowed < 0)) {
            textFieldAllowedKill.setTextColor(TextColors.PASTEL_RED);
        }
        else {
            textFieldAllowedKill.setTextColor(TextColors.WHITE);
        }
        if ((currentTeamGain == null && !this.gainLifeKill.isEmpty()) || (currentTeamGain != null && currentTeamGain < 0)) {
            textFieldGainLife.setTextColor(TextColors.PASTEL_RED);
        }
        else {
            textFieldGainLife.setTextColor(TextColors.WHITE);
        }
    }

    public boolean isDefaultTeam() {
        return Objects.equals(defaultTeamNum, "0") || Objects.equals(defaultTeamNum, "1") || Objects.equals(defaultTeamNum, "2") || Objects.equals(defaultTeamNum, "3") || Objects.equals(defaultTeamNum, "4");
    }

    public void deleteEntry(Button button) {
        if (isDefaultTeam()) return;
        if (parentGroup == null) return;
        parentGroup.removeChildEntry(this);
    }

    public void addEntry(Button button) {
        if (parentGroup == null) return;
        int max = getTeamNum() != null ? getTeamNum() : 0;
        for (int i : getSisterTeamNums()) {
            if (i > max) max = i;
        }
        parentGroup.addChildEntry(new TeamConfigEntry("dynamic_teams_"+ UUID.randomUUID(), List.of("", "" ,"", String.valueOf(max+1), "New Team", "white", "", "")));
    }

    public boolean isFirst() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == 0;
    }

    public boolean isLast() {
        if (parentGroup == null) return false;
        return parentGroup.getChildEntries().indexOf(this) == parentGroup.getChildEntries().size()-1;
    }

    @Override
    public int getPreferredHeight() {
        int heightMultiplier = 1;
        if (isFirst()) heightMultiplier++;
        if (isLast()) heightMultiplier++;
        return PREFFERED_HEIGHT * heightMultiplier;
    }

    public void onChanged(String text) {
        this.teamNum = textFieldLives.getValue();
        this.teamName = textFieldName.getValue();
        this.teamColor = textFieldColor.getValue();
        this.allowedKill = textFieldAllowedKill.getValue();
        this.gainLifeKill = textFieldGainLife.getValue();
        System.out.println("New Value: " + teamNum);
        markChanged();
        checkErrors();
    }

    public Integer getTeamNum() {
        try {
            return Integer.parseInt(this.teamNum);
        } catch (Exception e) {}
        return null;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public ChatFormatting getTeamColor() {
        try {
            return ChatFormatting.getByName(textFieldColor.getValue());
        } catch (Exception e) {}
        return null;
    }

    public Integer getTeamAllowedKill() {
        try {
            return Integer.parseInt(this.allowedKill);
        } catch (Exception e) {}
        return null;
    }

    public Integer getTeamGainLifeKill() {
        try {
            return Integer.parseInt(this.gainLifeKill);
        } catch (Exception e) {}
        return null;
    }

    public List<TeamConfigEntry> sisterEntries() {
        List<TeamConfigEntry> result = new ArrayList<>();
        if (parentGroup != null) {
            for (ConfigEntry entry : parentGroup.getChildEntries()) {
                if (entry instanceof TeamConfigEntry teamConfigEntry) {
                    if (teamConfigEntry != this) {
                        result.add(teamConfigEntry);
                    }
                }
            }
        }
        return result;
    }

    public List<Integer> getSisterTeamNums() {
        List<Integer> result = new ArrayList<>();
        for (TeamConfigEntry entry : sisterEntries()) {
            Integer num = entry.getTeamNum();
            if (num != null) result.add(num);
        }
        return result;
    }

    public void checkErrors() {
        if (getTeamColor() == null) {
            setError("Invalid color");
            return;
        }
        Integer currentTeamNum = getTeamNum();
        Integer currentAllowedKill = getTeamAllowedKill();
        Integer currentGainLife = getTeamGainLifeKill();
        if (currentTeamNum == null || (currentAllowedKill == null && !this.allowedKill.isEmpty()) || (currentGainLife == null && !this.gainLifeKill.isEmpty())) {
            setError("Invalid number format");
            return;
        }
        if (currentTeamNum < 0 || (currentAllowedKill != null && currentAllowedKill < 0) || (currentGainLife != null && currentGainLife < 0)) {
            setError("Boundaries cannot be less than 0");
            return;
        }
        if (getSisterTeamNums().contains(currentTeamNum)) {
            setError("Team boundary cannot be the same as another team.");
            return;
        }
        System.out.println("ClearError");
        clearError();
        markChanged();
    }

    @Override
    public void resetToDefault() {
        textFieldLives.setValue(defaultTeamNum);
        textFieldName.setValue(defaultTeamName);
        textFieldColor.setValue(defaultTeamColor);
        textFieldAllowedKill.setValue(defaultAllowedKill);
        textFieldGainLife.setValue(defaultGainLifeKill);
    }

    @Override
    public boolean canReset() {
        return isModified();
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(textFieldLives.getValue(), defaultTeamNum)) return true;
        if (!Objects.equals(textFieldName.getValue(), defaultTeamName)) return true;
        if (!Objects.equals(textFieldColor.getValue(), defaultTeamColor)) return true;
        if (!Objects.equals(textFieldAllowedKill.getValue(), defaultAllowedKill)) return true;
        if (!Objects.equals(textFieldGainLife.getValue(), defaultGainLifeKill)) return true;
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            textFieldLives.setFocused(false);
            textFieldName.setFocused(false);
            textFieldColor.setFocused(false);
            textFieldAllowedKill.setFocused(false);
            textFieldGainLife.setFocused(false);
        }
    }

    //? if <= 1.21.6 {
    @Override
    protected boolean mouseClickedEntry(double mouseX, double mouseY, int button) {
        boolean textFieldLivesClick = textFieldLives.mouseClicked(mouseX, mouseY, button);
        boolean textFieldNameClick = textFieldName.mouseClicked(mouseX, mouseY, button);
        boolean textFieldColorClick = textFieldColor.mouseClicked(mouseX, mouseY, button);
        boolean textFieldAllowedKillClick = textFieldAllowedKill.mouseClicked(mouseX, mouseY, button);
        boolean textFieldGainLifeClick = textFieldGainLife.mouseClicked(mouseX, mouseY, button);
        textFieldLives.setFocused(textFieldLivesClick);
        textFieldName.setFocused(textFieldNameClick);
        textFieldColor.setFocused(textFieldColorClick);
        textFieldAllowedKill.setFocused(textFieldAllowedKillClick);
        textFieldGainLife.setFocused(textFieldGainLifeClick);
        if (textFieldLivesClick ||
                textFieldNameClick ||
                textFieldColorClick ||
                textFieldAllowedKillClick ||
                textFieldGainLifeClick ||
                deleteEntryButton.mouseClicked(mouseX, mouseY, button) ||
                addEntryButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClickedEntry(mouseX, mouseY, button);
    }

    @Override
    protected boolean keyPressedEntry(int keyCode, int scanCode, int modifiers) {
        if (textFieldLives.keyPressed(keyCode, scanCode, modifiers) ||
                textFieldName.keyPressed(keyCode, scanCode, modifiers) ||
                textFieldColor.keyPressed(keyCode, scanCode, modifiers) ||
                textFieldAllowedKill.keyPressed(keyCode, scanCode, modifiers) ||
                textFieldGainLife.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressedEntry(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTypedEntry(char chr, int modifiers) {
        if (textFieldLives.charTyped(chr, modifiers) ||
                textFieldName.charTyped(chr, modifiers) ||
                textFieldColor.charTyped(chr, modifiers) ||
                textFieldAllowedKill.charTyped(chr, modifiers) ||
                textFieldGainLife.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTypedEntry(chr, modifiers);
    }
    //?} else {
    /*@Override
    protected boolean mouseClickedEntry(MouseButtonEvent click, boolean doubled) {
        boolean textFieldLivesClick = textFieldLives.mouseClicked(click, doubled);
        boolean textFieldNameClick = textFieldName.mouseClicked(click, doubled);
        boolean textFieldColorClick = textFieldColor.mouseClicked(click, doubled);
        boolean textFieldAllowedKillClick = textFieldAllowedKill.mouseClicked(click, doubled);
        boolean textFieldGainLifeClick = textFieldGainLife.mouseClicked(click, doubled);
        textFieldLives.setFocused(textFieldLivesClick);
        textFieldName.setFocused(textFieldNameClick);
        textFieldColor.setFocused(textFieldColorClick);
        textFieldAllowedKill.setFocused(textFieldAllowedKillClick);
        textFieldGainLife.setFocused(textFieldGainLifeClick);
        if (textFieldLivesClick ||
                textFieldNameClick ||
                textFieldColorClick ||
                textFieldAllowedKillClick ||
                textFieldGainLifeClick ||
                deleteEntryButton.mouseClicked(click, doubled) ||
                addEntryButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClickedEntry(click, doubled);
    }

    @Override
    protected boolean keyPressedEntry(KeyEvent input) {
        if (textFieldLives.keyPressed(input) ||
                textFieldName.keyPressed(input) ||
                textFieldColor.keyPressed(input) ||
                textFieldAllowedKill.keyPressed(input) ||
                textFieldGainLife.keyPressed(input)) {
            return true;
        }
        return super.keyPressedEntry(input);
    }

    @Override
    protected boolean charTypedEntry(CharacterEvent input) {
        if (textFieldLives.charTyped(input) ||
                textFieldName.charTyped(input) ||
                textFieldColor.charTyped(input) ||
                textFieldAllowedKill.charTyped(input) ||
                textFieldGainLife.charTyped(input)) {
            return true;
        }
        return super.charTypedEntry(input);
    }
    *///?}

    @Override
    public void onSave() {
        //TODO
    }

    @Override
    public boolean isSearchable() {
        return false;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TEAM_ENTRY;
    }

    @Override
    public boolean hasResetButton() {
        return true;
    }
}
