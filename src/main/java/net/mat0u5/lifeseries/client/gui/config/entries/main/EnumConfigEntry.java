package net.mat0u5.lifeseries.client.gui.config.entries.main;

import net.mat0u5.lifeseries.client.gui.config.entries.ButtonConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnumConfigEntry extends ButtonConfigEntry {

	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_MIN_WIDTH = 50;
	String startingValue;
	String value;
	String defaultValue;
	List<String> possibleValues;
	int currentIndex;
	int defaultIndex;

	public EnumConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue, List<String> possibleValues) {
		this(fieldName, displayName, description, value, defaultValue, possibleValues, getMaxLength(possibleValues), BUTTON_HEIGHT);
	}

	public EnumConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue, List<String> possibleValues, int buttonWidth, int buttonHeight) {
		super(fieldName, displayName, description, buttonWidth, buttonHeight);

		assert possibleValues.contains(value);
		assert possibleValues.contains(defaultValue);

		this.defaultValue = defaultValue;
		this.value = value;
		this.startingValue = value;
		this.possibleValues = possibleValues;
		this.currentIndex = Math.max(0, possibleValues.indexOf(value));
		this.defaultIndex = Math.max(0, possibleValues.indexOf(defaultValue));
		reloadValue();
	}

	public static int getMaxLength(List<String> values) {
		int maxWidth = BUTTON_MIN_WIDTH;
		for (String str : values) {
			int currentWidth = Minecraft.getInstance().font.width(str);
			if (currentWidth > maxWidth) {
				maxWidth = currentWidth;
			}
		}
		return maxWidth+10;
	}

	public void reloadValue() {
		if (possibleValues.isEmpty()) {
			this.value = "";
		}
		else {
			this.currentIndex %= possibleValues.size();
			this.value = possibleValues.get(currentIndex);
		}
		updateButtonText();
	}

	@Override
	protected Component getButtonText() {
		return Component.literal(value == null ? "" : value);
	}

	@Override
	protected void onButtonClick(Button button) {
		currentIndex++;
		reloadValue();
	}

	@Override
	public void resetToDefault() {
		currentIndex = defaultIndex;
		reloadValue();
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getValueAsString() {
		return value;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue;
	}

	@Override
	public Object getStartingValue() {
		return startingValue;
	}

	@Override
	public String getStartingValueAsString() {
		return startingValue;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String stringValue && possibleValues.contains(stringValue)) {
			this.currentIndex = possibleValues.indexOf(stringValue);
			reloadValue();
		}
	}

	@Override
	public ConfigTypes getValueType() {
		return ConfigTypes.ENUM;
	}
}
