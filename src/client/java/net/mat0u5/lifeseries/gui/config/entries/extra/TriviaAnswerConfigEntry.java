package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.ConfigEntry;
import net.mat0u5.lifeseries.gui.config.entries.ModifiableListEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.gui.GuiGraphics;

public class TriviaAnswerConfigEntry extends ModifiableListEntry {

    public TriviaAnswerConfigEntry(String fieldName, String answer, int index, boolean isCorrect) {
        super(fieldName);
    }

    @Override
    protected void renderMainEntry(GuiGraphics context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {

    }

    @Override
    public ConfigEntry getNewEntry() {
        return null;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.TRIVIA_ANSWER;
    }
}
