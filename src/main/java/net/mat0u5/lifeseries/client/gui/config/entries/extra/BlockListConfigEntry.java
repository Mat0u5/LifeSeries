package net.mat0u5.lifeseries.client.gui.config.entries.extra;

import net.mat0u5.lifeseries.client.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.RegistryUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockListConfigEntry extends StringListPopupConfigEntry<Block> {
    public BlockListConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
        reloadEntriesRaw(value);
    }

    @Override
    protected void reloadEntries(List<String> items) {
        if (entries != null) {
            entries.clear();
        }

        List<Block> newList = new ArrayList<>();
        boolean errors = false;

        for (String blockId : items) {
            if (blockId.isEmpty()) continue;
            try {
                newList.add(RegistryUtils.resolveRegistryEntry(BuiltInRegistries.BLOCK, blockId));
            } catch (Exception e) {
                setError(TextUtils.formatString("Invalid item or tag: '{}'", blockId));
                errors = true;
            }
        }

        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphicsExtractor context, Block block, int x, int y, int mouseX, int mouseY, float tickDelta) {
        context.item(block.asItem().getDefaultInstance(), x, y);
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.BLOCK_LIST;
    }
}
