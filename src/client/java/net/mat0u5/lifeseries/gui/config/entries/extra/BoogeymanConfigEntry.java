package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.BooleanConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.DoubleConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.IntegerConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BoogeymanConfigEntry extends BooleanConfigEntry {
    private static final int DESCRIPTION_OFFSET_X = LABEL_OFFSET_X + 10;
    private static final int DESCRIPTION_OFFSET_Y = PREFFERED_HEIGHT + 6;
    private Double boogeymanMultiplier = null;
    private Integer boogeymanMin = null;
    private Integer boogeymanMax = null;
    private String[] boogeymanIgnore = null;
    private String[] boogeymanForce = null;

    public BoogeymanConfigEntry(String fieldName, String displayName, String description, boolean value, boolean defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
    }

    @Override
    protected void renderEntry(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        super.renderEntry(context, x, y, width, height, mouseX, mouseY, hovered, tickDelta);
        if (!value || parentGroup == null) {
            return;
        }

        updateBoogeyDescription();

        if (boogeymanMin == null || boogeymanMax == null || boogeymanIgnore == null || boogeymanForce == null || boogeymanMultiplier == null) {
            return;
        }

        List<String> boogeymanDescription = new ArrayList<>();
        boogeymanDescription.add("§nCurrent Quizmaster Settings:§r");
        if (boogeymanIgnore.length > 0) {
            String line = TextUtils.formatString(
                    "{} §f{}§r cannot become the quizmaster.",
                    TextUtils.pluralize("Player", boogeymanIgnore.length),
                    String.join("§r, §f", boogeymanIgnore)
            );

            boogeymanDescription.add(line);
        }
        if (boogeymanForce.length > 0) {
            String line = TextUtils.formatString(
                    "{} §f{}§r will be forced to become the Quizmaster.",
                    TextUtils.pluralize("Player", boogeymanForce.length),
                    String.join("§r, §f", boogeymanForce)
            );
            boogeymanDescription.add(line);
        }
        boogeymanDescription.add("");
        double currentChance = 1;
        int offset = Math.max(1, boogeymanMin-1);
        for (int i = 0; i < 5; i++) {
            int boogeyNum = i + offset;
            if (boogeyNum > boogeymanMin) {
                currentChance *= boogeymanMultiplier;
            }
            if (boogeyNum > boogeymanMax) {
                currentChance = 0;
            }
            boogeymanDescription.add(TextUtils.formatString(
                    "Chance for at least {} {}: {}",
                    boogeyNum,
                    TextUtils.pluralize("Quizmaster","Quizmasters",boogeyNum),
                    String.format("%.1f%%", currentChance * 100)
            ));
        }

        int currentY = y + DESCRIPTION_OFFSET_Y;
        for (String line : boogeymanDescription) {
            RenderUtils.drawTextLeft(context, textRenderer, TextColors.LIGHT_GRAY,  Text.literal(line), x+DESCRIPTION_OFFSET_X, currentY);
            currentY += textRenderer.fontHeight;
        }
    }

    private void updateBoogeyDescription() {
        String boogeymanIgnoreRaw = null;
        String boogeymanForceRaw = null;
        for (ConfigEntry entry : parentGroup.getChildEntries()) {
            if (entry instanceof IntegerConfigEntry integerConfigEntry) {
                if (integerConfigEntry.getFieldName().equalsIgnoreCase("boogeyman_min_amount")) {
                    boogeymanMin = integerConfigEntry.getValue();
                }
                if (integerConfigEntry.getFieldName().equalsIgnoreCase("boogeyman_max_amount")) {
                    boogeymanMax = integerConfigEntry.getValue();
                }
            }
            if (entry instanceof StringConfigEntry stringConfigEntry) {
                if (stringConfigEntry.getFieldName().equalsIgnoreCase("boogeyman_ignore")) {
                    boogeymanIgnoreRaw = stringConfigEntry.getValue();
                }
                if (stringConfigEntry.getFieldName().equalsIgnoreCase("boogeyman_force")) {
                    boogeymanForceRaw = stringConfigEntry.getValue();
                }
            }
            if (entry instanceof DoubleConfigEntry doubleConfigEntry) {
                if (doubleConfigEntry.getFieldName().equalsIgnoreCase("boogeyman_chance_multiplier")) {
                    boogeymanMultiplier = doubleConfigEntry.getValue();
                }
            }
        }
        if (boogeymanIgnoreRaw == null || boogeymanForceRaw == null) {
            return;
        }
        boogeymanIgnoreRaw = boogeymanIgnoreRaw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim();
        boogeymanForceRaw = boogeymanForceRaw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim();
        if (!boogeymanIgnoreRaw.isEmpty()) {
            boogeymanIgnore = boogeymanIgnoreRaw.split(",");
        }
        else {
            boogeymanIgnore = new String[]{};
        }
        if (!boogeymanForceRaw.isEmpty()) {
            boogeymanForce = boogeymanForceRaw.split(",");
        }
        else {
            boogeymanForce = new String[]{};
        }
    }

    @Override
    public int getPreferredHeight() {
        int initial = super.getPreferredHeight();
        if (value) {
            int lines = 9;
            return initial + textRenderer.fontHeight * lines + 6;
        }
        return initial;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.BOOGEYMAN;
    }
}
