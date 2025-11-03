package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.StringListPopupConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            if (!blockId.contains(":")) blockId = "minecraft:" + blockId;

            try {
                ResourceLocation id = ResourceLocation.parse(blockId);
                ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(), id);

                Block block = BuiltInRegistries.BLOCK.get(key);
                if (block != null) {
                    newList.add(block);
                } else {
                    setError(TextUtils.formatString("Invalid block: '{}'", blockId));
                    errors = true;
                }
            } catch (Exception e) {
                setError(TextUtils.formatString("Error parsing block ID: '{}'", blockId));
                errors = true;
            }
        }

        entries = newList;
        if (!errors) {
            clearError();
        }
    }

    @Override
    protected void renderListEntry(GuiGraphics context, Block block, int x, int y, int mouseX, int mouseY, float tickDelta) {
        context.renderItem(block.asItem().getDefaultInstance(), x, y);
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
