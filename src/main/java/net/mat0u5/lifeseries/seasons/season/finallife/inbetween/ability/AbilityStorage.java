package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import com.google.gson.*;
import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static net.mat0u5.matlib.MatLib.server;

public class AbilityStorage {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = "finallife_abilities.json";
	private static final int VERSION = 1;

	@Nullable
	private static Path getFile() {
		if (server == null) return null;
		return server.getWorldPath(LevelResource.ROOT).resolve("lifeseries").resolve(FILE_NAME);
	}

	public static void save(Map<UUID, List<Ability>> abilities) {
		Path file = getFile();
		if (file == null) return;

		JsonObject players = new JsonObject();
		for (Map.Entry<UUID, List<Ability>> entry : abilities.entrySet()) {
			if (entry.getValue().isEmpty()) continue;
			JsonArray list = new JsonArray();
			for (Ability ability : entry.getValue()) {
				if (ability.isForced()) continue;
				JsonObject json = new JsonObject();
				json.addProperty("id", ability.ability().name().toLowerCase(Locale.ROOT));
				//ability.saveExtraData(json);
				list.add(json);
			}
			players.add(entry.getKey().toString(), list);
		}
		JsonObject root = new JsonObject();
		root.addProperty("version", VERSION);
		root.add("players", players);

		try {
			Files.createDirectories(file.getParent());
			// Temp file so that no mid-write issues can happen
			Path temp = file.resolveSibling(FILE_NAME + ".tmp");
			Files.writeString(temp, GSON.toJson(root), StandardCharsets.UTF_8);
			Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LifeSeries.LOGGER.error("[Abilities] Failed to save {}", file, e);
		}
	}

	public static Map<UUID, List<Ability>> load() {
		Map<UUID, List<Ability>> result = new HashMap<>();
		Path file = getFile();
		if (file == null || !Files.exists(file)) return result;

		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			JsonObject players = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("players");
			if (players == null) return result;
			for (Map.Entry<String, JsonElement> entry : players.entrySet()) {
				UUID uuid = UUID.fromString(entry.getKey());
				List<Ability> list = new ArrayList<>();
				for (JsonElement element : entry.getValue().getAsJsonArray()) {
					Ability ability = loadAbility(element.getAsJsonObject());
					if (ability != null) list.add(ability);
				}
				result.put(uuid, list);
			}
		} catch (Exception e) {
			LifeSeries.LOGGER.error("[Abilities] Failed to load {}, moving to {}.broken", file, FILE_NAME, e);
			try {
				Files.move(file, file.resolveSibling(FILE_NAME + ".broken"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ignored) {}
			return new HashMap<>();
		}
		return result;
	}

	@Nullable
	private static Ability loadAbility(JsonObject json) {
		String id = json.get("id").getAsString();
		Abilities type;
		try {
			type = Abilities.valueOf(id.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			LifeSeries.LOGGER.warn("[Abilities] Skipping unknown ability '{}'", id);
			return null;
		}
		Ability ability = type.createAbility();
		//ability.loadExtraData(json);
		return ability;
	}
}
