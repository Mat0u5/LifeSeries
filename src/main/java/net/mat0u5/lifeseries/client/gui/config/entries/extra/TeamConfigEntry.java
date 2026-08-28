package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.client.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.client.render.RenderUtils;
import net.mat0u5.lifeseries.client.utils.TextColors;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.Time;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.*;

//? if >= 1.21.9
import net.minecraft.client.input.*;
//? if <= 26.1 {
/*import net.minecraft.ChatFormatting;
*///?} else {
import net.minecraft.world.scores.TeamColor;
//?}

public class TeamConfigEntry extends ModifiableListEntry {
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

    private int RED_TIME = 0;
    private int YELLOW_TIME = 0;
    private int DEFAULT_TIME = 0;

    public TeamConfigEntry(String fieldName, List<String> args) {
        super(fieldName);
        try {
            RED_TIME = Integer.parseInt(args.get(8));
            YELLOW_TIME = Integer.parseInt(args.get(9));
            DEFAULT_TIME = Integer.parseInt(args.get(10));
        }catch(Exception e) {}
        this.defaultTeamNum = livesOrTime(args.get(3));
        this.defaultTeamName = args.get(4);
        this.defaultTeamColor = args.get(5);
        this.defaultAllowedKill = livesOrTime(args.get(6));
        this.defaultGainLifeKill = livesOrTime(args.get(7));
        this.teamNum = defaultTeamNum;
        this.teamName = defaultTeamName;
        this.teamColor = defaultTeamColor;
        this.allowedKill = defaultAllowedKill;
        this.gainLifeKill = defaultGainLifeKill;

        textFieldLives = new EditBox(textRenderer, 0, 0, isLimitedLife()?45:30, 18, Component.empty());
        textFieldName = new EditBox(textRenderer, 0, 0, 80, 18, Component.empty());
        textFieldColor = new EditBox(textRenderer, 0, 0, 80, 18, Component.empty());
        textFieldAllowedKill = new EditBox(textRenderer, 0, 0, 45, 18, Component.empty());
        textFieldGainLife = new EditBox(textRenderer, 0, 0, 45, 18, Component.empty());

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
    }

    @Override
    protected void renderMainEntry(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int field1X = x+40;
        int field2X = x+80;
        int field3X = x+170;
        int field4X = x+260;
        int field5X = x+320;
        textFieldLives.setY(y+1);
        textFieldName.setY(y+1);
        textFieldColor.setY(y+1);
        textFieldAllowedKill.setY(y+1);
        textFieldGainLife.setY(y+1);

        textFieldLives.setX(isLimitedLife() ? field1X-9 : field1X);
        textFieldName.setX(field2X);
        textFieldColor.setX(field3X);
        textFieldAllowedKill.setX(field4X);
        textFieldGainLife.setX(field5X);

        //~ renames_26_1_volatile
        textFieldLives.extractRenderState(context, mouseX, mouseY, tickDelta);
        textFieldName.extractRenderState(context, mouseX, mouseY, tickDelta);
        textFieldColor.extractRenderState(context, mouseX, mouseY, tickDelta);
        textFieldAllowedKill.extractRenderState(context, mouseX, mouseY, tickDelta);
        textFieldGainLife.extractRenderState(context, mouseX, mouseY, tickDelta);
        //~ !renames_26_1_volatile
        textFieldLives.setEditable(!isNonEditableLivesTeam());

        if (getTeamNum() == null) textFieldLives.setTextColor(TextColors.PASTEL_RED);
        else textFieldLives.setTextColor(TextColors.WHITE);
        var teamColor = getTeamColor();
        //? if <= 26.1 {
        /*if (teamColor == null || teamColor.getColor() == null) {
        *///?} else {
        if (teamColor == null) {
        //?}
            textFieldColor.setTextColor(TextColors.PASTEL_RED);
            textFieldName.setTextColor(TextColors.WHITE);
        }
        else {
            textFieldColor.setTextColor(TextColors.WHITE);
            //? if <= 26.1 {
            /*textFieldName.setTextColor(TextColors.toArgb(255, teamColor.getColor()));
            *///?} else {
            textFieldName.setTextColor(TextColors.toArgb(255, teamColor.rgb()));
            //?}
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

    @Override
    public void renderFirstEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
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
        Component header1Text = Component.nullToEmpty("§f\uD83D\uDEC8 "+(isLimitedLife()?"Time":"Lives"));
        Component header2Text = Component.nullToEmpty("§fName");
        Component header3Text = Component.nullToEmpty("§fColor");
        Component header4Text = Component.nullToEmpty("§f\uD83D\uDEC8 Can Kill");
        Component header5Text = Component.nullToEmpty("§f\uD83D\uDEC8 Gain "+(isLimitedLife()?"Time":"Life"));

        RenderUtils.text(header1Text, field1CenterX, y+5).anchorCenter().render(context, textRenderer);
        RenderUtils.text(header2Text, field2CenterX, y+5).anchorCenter().render(context, textRenderer);
        RenderUtils.text(header3Text, field3CenterX, y+5).anchorCenter().render(context, textRenderer);
        RenderUtils.text(header4Text, field4CenterX, y+5).anchorCenter().render(context, textRenderer);
        RenderUtils.text(header5Text, field5CenterX, y+5).anchorCenter().render(context, textRenderer);

        if (hovered && mouseY >= y + 5 && mouseY <= y + 5 + textRenderer.lineHeight) {
            Component hoverText = null;
            if (mouseX >= field1X && mouseX <= field1X + textRenderer.width(header1Text)) {
                if (isLimitedLife()) {
                    hoverText = Component.nullToEmpty("Time boundary where players are put into this team (Rounded to nearest team with boundary <= time).");
                }
                else {
                    hoverText = Component.nullToEmpty("Lives boundary where players are put into this team (Rounded to nearest team with boundary <= lives).");
                }
            }
            if (mouseX >= field4X && mouseX <= field4X + textRenderer.width(header4Text)) {
                if (isLimitedLife()) {
                    hoverText = Component.nullToEmpty("Time boundary where this team can kill.\nFor example if set to 8h, this team can kill any players with at least 8h of time.");
                }
                else {
                    hoverText = Component.nullToEmpty("Lives boundary where this team can kill.\nFor example if set to 2, this team can kill any players with at least 2 lives.");
                }
            }
            if (mouseX >= field5X && mouseX <= field5X + textRenderer.width(header5Text)) {
                if (isLimitedLife()) {
                    hoverText = Component.nullToEmpty("Time boundary where this team can gain time for killing.\nFor example if set to 16h, this team will gain a life for killing players with at least 16h of time.");
                }
                else {
                    hoverText = Component.nullToEmpty("Lives boundary where this team can gain lives for killing.\nFor example if set to 4, this team will gain a life for killing players with at least 4 lives.");
                }
            }
            if (hoverText != null) {
                //? if <= 1.21.5 {
                /*context.renderTooltip(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY);
                 *///?} else {
                context.setTooltipForNextFrame(textRenderer, textRenderer.split(hoverText, 210), DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, false);
                //?}
            }
        }
    }
    public void renderLastEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderLastEntryExtras(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        addEntryButton.active = true;
    }
    public void renderMiddleEntryExtras(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderMiddleEntryExtras(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        deleteEntryButton.active = !isDefaultTeam();
    }

    public boolean isDefaultTeam() {
        String parsed = parseDefaultTeamNum();
        return Objects.equals(parsed, "0") || Objects.equals(parsed, "1") || Objects.equals(parsed, "2") || Objects.equals(parsed, "3") || Objects.equals(parsed, "4");
    }

    public boolean isNonEditableLivesTeam() {
        if (isLimitedLife()) {
            String parsed = parseDefaultTeamNum();
            return Objects.equals(parsed, "0") || Objects.equals(parsed, "1");
        }
        return isDefaultTeam();
    }

    public void deleteEntry(Button button) {
        if (isDefaultTeam()) return;
        super.deleteEntry(button);
    }

    public void onChanged(String text) {
        this.teamNum = textFieldLives.getValue();
        this.teamName = textFieldName.getValue();
        this.teamColor = textFieldColor.getValue();
        this.allowedKill = textFieldAllowedKill.getValue();
        this.gainLifeKill = textFieldGainLife.getValue();
        markChanged();
        for (var entry : getListEntries()) {
            if (entry instanceof TeamConfigEntry teamConfigEntry) teamConfigEntry.checkErrors();
        }
    }

    public String livesOrTime(String str) {
        if (isLimitedLife()) {
            try {
                if (str.isEmpty()) return str;
                Integer num = Integer.parseInt(str);
                Integer parsed = undoEquivalentLivesBoundary(num);
                if (parsed != null) {
                    return OtherUtils.formatTimeArgumentFromSeconds(parsed);
                }
            }catch(Exception e) {}
        }
        return str;
    }

    public String parseDefaultTeamNum() {
        String str = defaultTeamNum;
        if (isLimitedLife()) {
            if (str.isEmpty()) return str;
            Time time = OtherUtils.parseTimeFromArgument(str);
            if (time == null) return str;
            return String.valueOf(getEquivalentLivesBoundary(time.getSeconds()));
        }
        return str;
    }

    public String parseLivesOrTimeNoBoundary(String str) {
        if (isLimitedLife()) {
            if (str.isEmpty()) return str;
            Time time = OtherUtils.parseTimeFromArgument(str);
            if (time == null) return str;
            return String.valueOf(time.getSeconds());
        }
        return str;
    }

    public Integer getEquivalentLivesBoundary(Integer limitedLifeLives) {
        if (limitedLifeLives == null) return null;
        if (limitedLifeLives == 0) return 0;
        if (limitedLifeLives == 1) return 1;
        if (limitedLifeLives == RED_TIME) return 2;
        if (limitedLifeLives == YELLOW_TIME) return 3;
        if (limitedLifeLives == DEFAULT_TIME) return 4;
        return limitedLifeLives;
    }

    public Integer undoEquivalentLivesBoundary(Integer normalLives) {
        if (normalLives == null) return null;
        if (normalLives == 0) return 0;
        if (normalLives == 1) return 1;
        if (normalLives == 2) return RED_TIME;
        if (normalLives == 3) return YELLOW_TIME;
        if (normalLives == 4) return DEFAULT_TIME;
        return normalLives;
    }

    public Integer getTeamNum() {
        try {
            if (isLimitedLife() && isDefaultTeam()) {
                return Integer.parseInt(parseDefaultTeamNum());
            }
            return Integer.parseInt(parseLivesOrTimeNoBoundary(this.teamNum));
        } catch (Exception e) {}
        return null;
    }
    public boolean isLimitedLife() {
        return LifeSeries.isSeason(Seasons.LIMITED_LIFE);
    }

    public String getTeamNumAsString() {
        return String.valueOf(getTeamNum());
    }

    public String getTeamName() {
        return this.teamName;
    }

    //? if <= 26.1 {
    /*public ChatFormatting getTeamColor() {
        try {
            return ChatFormatting.getByName(this.teamColor);
        } catch (Exception e) {}
        return null;
    }
    *///?} else {
    public TeamColor getTeamColor() {
        try {
            return TeamColor.byName(this.teamColor);
        } catch (Exception e) {}
        return null;
    }
    //?}

    public Integer getTeamAllowedKill() {
        try {
            return Integer.parseInt(parseLivesOrTimeNoBoundary(this.allowedKill));
        } catch (Exception e) {}
        return null;
    }

    public Integer getTeamGainLifeKill() {
        try {
            return Integer.parseInt(parseLivesOrTimeNoBoundary(this.gainLifeKill));
        } catch (Exception e) {}
        return null;
    }

    public List<Integer> getSisterTeamNums() {
        List<Integer> result = new ArrayList<>();
        for (ModifiableListEntry entry : getSisterEntries()) {
            if (entry instanceof TeamConfigEntry teamEntry) {
                Integer num = teamEntry.maxTeamNumGet();
                if (num != null) result.add(num);
            }
        }
        return result;
    }

    public Map<Integer, Integer> getDefaultTeamNums() {
        Map<Integer, Integer> result = new HashMap<>();
        for (ModifiableListEntry entry : getListEntries()) {
            if (entry instanceof TeamConfigEntry teamEntry) {
                if (!teamEntry.isDefaultTeam()) continue;
                Integer num = teamEntry.getTeamNum();
                Integer numReal = teamEntry.maxTeamNumGet();
                if (num != null && numReal != null) result.put(num, numReal);
            }
        }
        return result;
    }

    public Integer maxTeamNumGet() {
        try {
            return Integer.parseInt(parseLivesOrTimeNoBoundary(this.teamNum));
        } catch (Exception e) {}
        return null;
    }

    @Override
    public ConfigEntry getNewEntry() {
        int max = maxTeamNumGet() != null ? maxTeamNumGet() : 0;
        for (int i : getSisterTeamNums()) {
            if (i > max) max = i;
        }
        TeamConfigEntry newEntry = new TeamConfigEntry("dynamic_teams_"+ UUID.randomUUID(), List.of("", "" ,"", String.valueOf(max+(isLimitedLife()?8*3600:1)), "New Team", "white", "", "", String.valueOf(RED_TIME), String.valueOf(YELLOW_TIME), String.valueOf(DEFAULT_TIME)));
        newEntry.defaultTeamNum = "";
        return newEntry;
    }

    public void checkErrors() {
        if (getTeamColor() == null) {
            setError("Invalid color");
            return;
        }
        Integer currentTeamNum = getTeamNum();
        Integer currentAllowedKill = getTeamAllowedKill();
        Integer currentGainLife = getTeamGainLifeKill();
        if (currentTeamNum == null || maxTeamNumGet() == null || (currentAllowedKill == null && !this.allowedKill.isEmpty()) || (currentGainLife == null && !this.gainLifeKill.isEmpty())) {
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
        if (isLimitedLife()) {
            Map<Integer, Integer> defaultTeamNums = getDefaultTeamNums();

            if (isDefaultTeam()) {
                if (currentTeamNum >= 2) {
                    Integer prevRealTeamNum = defaultTeamNums.get(currentTeamNum - 1);
                    Integer currentRealTeamNum = defaultTeamNums.get(currentTeamNum);
                    Integer nextRealTeamNum = defaultTeamNums.get(currentTeamNum+1);
                    if (prevRealTeamNum != null && currentRealTeamNum != null && currentRealTeamNum <= prevRealTeamNum) {
                        setError("Default team boundaries must be in ascending order.");
                        return;
                    }
                    if (currentRealTeamNum != null && nextRealTeamNum != null && nextRealTeamNum <= currentRealTeamNum) {
                        setError("Default team boundaries must be in ascending order.");
                        return;
                    }
                }
            }
            else {
                Integer defaultTeamNum =  defaultTeamNums.get(4);
                if (defaultTeamNum != null && currentTeamNum <= defaultTeamNum) {
                    setError("Team boundary must be greater than the default teams.");
                    return;
                }
            }
        }
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
    public boolean isModified() {
        if (!Objects.equals(textFieldLives.getValue(), defaultTeamNum)) return true;
        if (!Objects.equals(textFieldName.getValue(), defaultTeamName)) return true;
        if (!Objects.equals(textFieldColor.getValue(), defaultTeamColor)) return true;
        if (!Objects.equals(textFieldAllowedKill.getValue(), defaultAllowedKill)) return true;
        if (!Objects.equals(textFieldGainLife.getValue(), defaultGainLifeKill)) return true;
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
            textFieldLives.setFocused(false);
            textFieldName.setFocused(false);
            textFieldColor.setFocused(false);
            textFieldAllowedKill.setFocused(false);
            textFieldGainLife.setFocused(false);
        }
    }

    //? if <= 1.21.6 {
    /*@Override
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
                textFieldGainLifeClick) {
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
    *///?} else {
    @Override
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
                textFieldGainLifeClick) {
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
    //?}

    @Override
    public void onSave() {
        List<String> allTeams = new ArrayList<>();
        allTeams.add("lives_"+this.getTeamNumAsString());
        for (ModifiableListEntry entry : getSisterEntries()) {
            if (entry instanceof TeamConfigEntry teamEntry) {
                allTeams.add("lives_"+teamEntry.getTeamNumAsString());
            }
        }
        String kill = parseLivesOrTimeNoBoundary(allowedKill);
        String gainlife = parseLivesOrTimeNoBoundary(gainLifeKill);
        SimplePackets.SET_TEAM.sendToServer(List.of(
                String.join(";",allTeams), this.getTeamNumAsString(), teamName, teamColor, kill, gainlife, isLimitedLife() ? parseLivesOrTimeNoBoundary(this.teamNum) : ""
        ));
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TEAM_ENTRY;
    }
}
