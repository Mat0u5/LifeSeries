package net.mat0u5.lifeseries.utils.other;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
//? if <= 1.20.3
//import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryUtils {

	public static List<String> parseStringList(String raw) {
		if (raw == null || raw.isEmpty()) return new ArrayList<>();
		raw = raw.replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "");
		if (raw.isEmpty()) return new ArrayList<>();
		return new ArrayList<>(Arrays.asList(raw.split(",")));
	}

	public static List<Item> resolveItems(String entryId) throws IllegalArgumentException {
		List<Item> results = new ArrayList<>();
		if (entryId == null || entryId.isEmpty()) return results;

		boolean isTag = entryId.startsWith("#");
		String cleanId = isTag ? entryId.substring(1) : entryId;

		if (!cleanId.contains(":")) cleanId = "minecraft:" + cleanId;

		//? if <= 1.20.3
		//if (cleanId.equalsIgnoreCase("minecraft:head_armor")) return List.of(Items.LEATHER_HELMET, Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET);

		try {
			var id = IdentifierHelper.parse(cleanId);

			if (isTag) {
				TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), id);
				var tagElements = BuiltInRegistries.ITEM.getTagOrEmpty(tagKey);
				if (tagElements.iterator().hasNext()) {
					for (var holder : tagElements) {
						results.add(holder.value());
					}
				}
				else {
					throw new IllegalArgumentException("Empty or invalid tag: " + entryId);
				}
			}
			else {
				ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), id);

				//? if <= 1.21 {
				/*Item item = BuiltInRegistries.ITEM.get(key);
				 *///?} else {
				Item item = BuiltInRegistries.ITEM.getValue(key);
				//?}

				if (item != null) {
					results.add(item);
				}
				else {
					throw new IllegalArgumentException("Invalid item: " + entryId);
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error parsing ID: " + entryId, e);
		}
		return results;
	}

	public static <T> T resolveRegistryEntry(Registry<T> registry, String entryId) throws IllegalArgumentException {
		if (entryId == null || entryId.isEmpty()) return null;
		if (!entryId.contains(":")) entryId = "minecraft:" + entryId;

		try {
			var id = IdentifierHelper.parse(entryId);
			ResourceKey<T> key = ResourceKey.create(registry.key(), id);

			//? if <= 1.21 {
			/*T entry = registry.get(key);
			*///?} else {
			T entry = registry.getValue(key);
			//?}

			if (entry != null) {
				return entry;
			}
			else {
				throw new IllegalArgumentException("Invalid registry entry: " + entryId);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error parsing ID: " + entryId, e);
		}
	}
}