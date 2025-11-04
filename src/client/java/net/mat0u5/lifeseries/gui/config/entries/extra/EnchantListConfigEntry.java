package net.mat0u5.lifeseries.gui.config.entries.extra;

import net.mat0u5.lifeseries.gui.config.entries.main.StringConfigEntry;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantListConfigEntry extends StringConfigEntry {
    public EnchantListConfigEntry(String fieldName, String displayName, String description, String value, String defaultValue) {
        super(fieldName, displayName, description, value, defaultValue);
    }

    @Override
    protected void onTextChanged(String text) {
        super.onTextChanged(text);
        reloadEntriesRaw(text);
    }

    protected void reloadEntriesRaw(String text) {
        String raw = text;
        raw = raw.replaceAll("\\[","").replaceAll("]","").replaceAll(" ", "");
        List<String> items = new ArrayList<>(Arrays.asList(raw.split(",")));
        reloadEntries(items);
    }
    protected void reloadEntries(List<String> items) {
        if (Minecraft.getInstance().level == null) return;

        List<ResourceKey<Enchantment>> newList = new ArrayList<>();
        boolean errors = false;

        Registry<Enchantment> enchantmentRegistry = Minecraft.getInstance().level.registryAccess()

        //? if <=1.21 {
        .registryOrThrow(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecraft", "enchantment")));
        //?} else
        /*.lookupOrThrow(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecraft", "enchantment")));*/


        for (String enchantmentId : items) {
            if (enchantmentId.isEmpty()) continue;
            if (!enchantmentId.contains(":")) enchantmentId = "minecraft:" + enchantmentId;

            try {
                ResourceLocation id = ResourceLocation.parse(enchantmentId);
                //? if <= 1.21 {
                Enchantment enchantment = enchantmentRegistry.get(id);
                //?} else {
                /*Enchantment enchantment = enchantmentRegistry.getValue(id);
                *///?}

                if (enchantment != null) {
                    newList.add(enchantmentRegistry.getResourceKey(enchantment).orElseThrow());
                } else {
                    setError(TextUtils.formatString("Invalid enchantment: '{}'", enchantmentId));
                    errors = true;
                }
            } catch (Exception e) {
                setError(TextUtils.formatString("Error parsing enchantment ID: '{}'", enchantmentId));
                errors = true;
            }
        }

        if (!errors) {
            clearError();
        }
    }

    @Override
    public boolean hasCustomErrors() {
        return true;
    }

    @Override
    public ConfigTypes getValueType() {
        return ConfigTypes.ENCHANT_LIST;
    }
}
