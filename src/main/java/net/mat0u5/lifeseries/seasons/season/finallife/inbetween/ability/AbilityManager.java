package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.player.AttributeUtils;
import net.mat0u5.matlib.utils.player.PlayerReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.*;

public class AbilityManager {
	private static final Map<UUID, List<Ability>> abilities = new HashMap<>();

	public static void saveStoredAbilities() {
		AbilityStorage.save(abilities);
	}

	public static void loadStoredAbilities() {
		abilities.clear();
		abilities.putAll(AbilityStorage.load());
		abilities.forEach((uuid, list) -> list.forEach(ability -> {
			ability.bindPlayer(uuid);
			if (PlayerUtils.getPlayer(uuid) != null) ability.onStart();
		}));
		sendUpdatePacket(PlayerUtils.getAllPlayers());
	}

	public static void tick() {
		for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
			var copyList = new ArrayList<>(getPlayerAbilities(player));
			copyList.forEach(Ability::tick);
		}
	}

	public static void onReload() {
		abilities.values().forEach(abilityList -> abilityList.forEach(Ability::reloadHeartsChange));
		PlayerUtils.getAllPlayers().forEach(AbilityManager::resetPlayerMaxHealth);
		sendUpdatePacket(PlayerUtils.getAllPlayers());
	}

	public static void resetAllAbilities() {
		PlayerUtils.getAllPlayers().forEach(AbilityManager::clearAbilities);
		abilities.clear();
		saveStoredAbilities();
		sendUpdatePacket(PlayerUtils.getAllPlayers());
	}

	public static void onPlayerJoin(ServerPlayer player) {
		sendUpdatePacket(player);
		PlayerReference ref = PlayerReference.of(player);
		TaskScheduler.scheduleTask(1, () -> {
			ServerPlayer joined = ref.get();
			if (joined != null) getPlayerAbilities(joined).forEach(Ability::onStart);
		});
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		getPlayerAbilities(player).forEach(Ability::onEnd);
	}

	public static float modifyDamage(ServerPlayer player, DamageSource source, float amount) {
		for (Ability ability : getPlayerAbilities(player)) {
			amount = ability.modifyDamage(source, amount);
		}
		return amount;
	}

	public static boolean preventDamage(ServerPlayer player, DamageSource source) {
		for (Ability ability : getPlayerAbilities(player)) {
			if (ability.preventDamage(source)) return true;
		}
		return false;
	}

	public static void onChosen(ServerPlayer player, InBetweenChoice choice) {
		if (choice.isEmpty()) return;
		Ability abilityInstance = choice.ability();
		addAbility(player, abilityInstance, true);
	}

	public static void addAbility(ServerPlayer player, Abilities ability, boolean delayedMessage) {
		addAbility(player, ability.createAbility(), delayedMessage);
	}

	public static void addAbility(ServerPlayer player, Ability abilityInstance, boolean delayedMessage) {
		if (hasAbility(player, abilityInstance.ability())) return;
		abilityInstance.reloadHeartsChange();

		UUID uuid = player.getUUID();
		if (!abilities.containsKey(uuid)) {
			abilities.put(uuid, new ArrayList<>());
		}
		abilities.get(uuid).add(abilityInstance);
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
		player.heal(abilityInstance.getHeartsChange()*2);
		abilityInstance.bindPlayer(player);
		abilityInstance.onStart();
		sendUpdatePacket(player);

		Abilities ability = abilityInstance.ability();
		if (delayedMessage) {
			TaskScheduler.schedulePriorityTask(100, () -> {
				addAbilityMessage(player, ability);
			});
		}
		else {
			addAbilityMessage(player, ability);
		}
	}

	public static void setForcedAbility(ServerPlayer player, Abilities ability, boolean shouldHave) {
		Ability current = getAbility(player, ability);
		if (shouldHave && current == null) {
			Ability abilityInstance = ability.createAbility().setForced();
			abilities.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).add(abilityInstance);
			abilityInstance.bindPlayer(player);
			abilityInstance.onStart();
			sendUpdatePacket(player);
		}
		else if (!shouldHave && current != null && current.isForced()) {
			current.onEnd();
			abilities.get(player.getUUID()).remove(current);
			sendUpdatePacket(player);
		}
	}

	public static void clearAbilities(ServerPlayer player) {
		var playerAbilities = getPlayerAbilities(player);
		boolean hadAbilities = !playerAbilities.isEmpty();
		playerAbilities.forEach(Ability::onEnd);
		abilities.put(player.getUUID(), new ArrayList<>());
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
		sendUpdatePacket(player);
		if (hadAbilities) removeAbilityMessage(player);
	}

	public static void removeAbility(ServerPlayer player, Abilities ability) {
		Ability abilityInstance = getAbility(player, ability);
		if (abilityInstance == null) return; // Does not have this ability
		abilityInstance.onEnd();
		abilities.get(player.getUUID()).remove(abilityInstance);
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
		sendUpdatePacket(player);
		removeAbilityMessage(player);
	}

	private static void addAbilityMessage(ServerPlayer player, Abilities ability) {
		SimplePackets.ABILITY_GAIN_MESSAGE.sendToClient(ability.name(), player);
		ModifiableSound.FINALLIFE_ABILITY_GAIN.play(player);
	}

	private static void removeAbilityMessage(ServerPlayer player) {
		ModifiableSound.FINALLIFE_ABILITY_LOSE.play(player);
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
			health += ability.getHeartsChange()*2;
		}
		AttributeUtils.MAX_HEALTH.of(player).set(health);
	}

	public static void sendUpdatePacket(ServerPlayer player) {
		sendUpdatePacket(List.of(player));
	}

	public static void sendUpdatePacket(List<ServerPlayer> players) {
		if (!LifeSeries.isSeason(Seasons.FINAL_LIFE)) {
			SimplePackets.ACTIVE_ABILITIES.sendToClient(List.of(), players);
			return;
		}
		for (ServerPlayer player : players) {
			List<String> abilities = new ArrayList<>();
			for (Ability ability : getPlayerAbilities(player)) {
				abilities.add(ability.ability().name() + ";" + ability.getHeartsChange());
			}
			SimplePackets.ACTIVE_ABILITIES.sendToClient(abilities, player);
		}
	}

	public static void onTriggerAbilityPacket(ServerPlayer player) {
		//TODO soulmate ability trigger
	}
}

