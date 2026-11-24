package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class AbilityManager {
	private static final Map<UUID, List<Ability>> abilities = new HashMap<>();

	public static void saveStoredAbilities() {
		//TODO save to file
	}

	public static void loadStoredAbilities() {
		//TODO load from file &
		//abilities = ..
	}

	public static void tick() {
		for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
			var copyList = new ArrayList<>(getPlayerAbilities(player));
			copyList.forEach(Ability::tick);
		}
	}

	public static void onReload() {
		abilities.values().forEach(abilityList -> abilityList.forEach(Ability::reloadHealthChange));
		PlayerUtils.getAllPlayers().forEach(AbilityManager::resetPlayerMaxHealth);
	}

	public static void resetAllAbilities() {
		PlayerUtils.getAllPlayers().forEach(AbilityManager::clearAbilities);
		abilities.clear();
		saveStoredAbilities();
	}

	public static void onPlayerJoin(ServerPlayer player) {
		getPlayerAbilities(player).forEach(Ability::onStart);
	}

	public static void onChosen(ServerPlayer player, InBetweenChoice choice) {
		if (choice.isEmpty()) return;
		addAbility(player, choice.ability(), true);
	}

	public static void addAbility(ServerPlayer player, Abilities ability, boolean addHealth) {
		addAbility(player, ability.createAbility(), addHealth);
	}

	public static void addAbility(ServerPlayer player, Ability abilityInstance, boolean addHealth) {
		if (hasAbility(player, abilityInstance.ability())) return;
		abilityInstance.reloadHealthChange();

		UUID uuid = player.getUUID();
		if (!abilities.containsKey(uuid)) {
			abilities.put(uuid, new ArrayList<>());
		}
		abilities.get(uuid).add(abilityInstance);
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
		if (addHealth) player.heal(abilityInstance.getHealthChange());
		abilityInstance.bindPlayer(player);
		abilityInstance.onStart();
	}

	public static void clearAbilities(ServerPlayer player) {
		getPlayerAbilities(player).forEach(Ability::onEnd);
		abilities.put(player.getUUID(), new ArrayList<>());
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
	}

	public static void removeAbility(ServerPlayer player, Abilities ability) {
		Ability abilityInstance = getAbility(player, ability);
		if (abilityInstance == null) return; // Does not have this ability
		abilityInstance.onEnd();
		abilities.get(player.getUUID()).remove(abilityInstance);
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
	}

	public static List<Ability> getPlayerAbilities(ServerPlayer player) {
		return abilities.getOrDefault(player.getUUID(), List.of());
	}

	public static boolean hasAbility(ServerPlayer player, Abilities ability) {
		Ability abilityInstance = getAbility(player, ability);
		return abilityInstance != null;
	}

	public static Ability getAbility(ServerPlayer player, Abilities ability) {
		List<Ability> abilities = getPlayerAbilities(player);
		for (Ability abilityInstance : abilities) {
			if (abilityInstance.ability() == ability) {
				return abilityInstance;
			}
		}
		return null;
	}

	public static void resetPlayerMaxHealth(ServerPlayer player) {
		double health = LifeSeries.seasonConfig.MAX_PLAYER_HEALTH.get();
		for (Ability ability : getPlayerAbilities(player)) {
			health += ability.getHealthChange();
		}
		AttributeUtils.MAX_HEALTH.of(player).set(health);
	}
}

