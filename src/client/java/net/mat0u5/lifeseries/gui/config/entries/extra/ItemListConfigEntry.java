package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            if (!itemId.contains(":")) itemId = "minecraft:" + itemId;

            try {
                ResourceLocation id = ResourceLocation.parse(itemId);
                ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

                Item item = BuiltInRegistries.ITEM.get(key);
                if (item != null) {
                    newList.add(item);
                } else {
                    setError(TextUtils.formatString("Invalid item: '{}'", itemId));
                    errors = true;
                }
            } catch (Exception e) {
                setError(TextUtils.formatString("Error parsing item ID: '{}'", itemId));
                errors = true;
            }
        }
        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphics context, Item item, int x, int y, int mouseX, int mouseY, float tickDelta) {
        context.renderItem(item.getDefaultInstance(), x, y);
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
