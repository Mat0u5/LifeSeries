package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.client.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.RegistryUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListConfigEntry extends StringListPopupConfigEntry<Item> {

    public ItemListConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
        reloadEntriesRaw(value);
    }

    @Override
    protected void reloadEntries(List<String> items) {
        if (entries != null) {
            entries.clear();
        }

        List<Item> newList = new ArrayList<>();
        boolean errors = false;

        for (String itemId : items) {
            if (itemId.isEmpty()) continue;
            try {
                newList.addAll(RegistryUtils.resolveItems(itemId));
            } catch (Exception e) {
                setError(TextUtils.formatString("Invalid item or tag: '{}'", itemId));
                errors = true;
            }
        }
        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphicsExtractor context, Item item, int x, int y, int mouseX, int mouseY, float tickDelta) {
        context.item(item.getDefaultInstance(), x, y);
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.ITEM_LIST;
    }
}
