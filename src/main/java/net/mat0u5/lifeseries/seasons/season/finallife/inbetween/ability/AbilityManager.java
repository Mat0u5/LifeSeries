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
	private static final Set<Abilities> disabledAbilities = EnumSet.noneOf(Abilities.class);

	public static void saveStoredAbilities() {
		AbilityStorage.save(abilities);
	}

	public static void loadStoredAbilities() {
		abilities.clear();
		abilities.putAll(AbilityStorage.load());
		disabledAbilities.clear();
		for (Abilities ability : Abilities.values()) {
			if (!ability.isEnabled()) disabledAbilities.add(ability);
		}
		abilities.forEach((uuid, list) -> list.forEach(ability -> {
			ability.bindPlayer(uuid);
			if (PlayerUtils.getPlayer(uuid) != null && ability.ability().isEnabled()) ability.onStart();
		}));
		sendUpdatePacket(PlayerUtils.getAllPlayers());
	}

	public static void tick() {
		checkDisabledAbilities();
		for (ServerPlayer player : PlayerUtils.getAllFunctioningPlayers()) {
			getActiveAbilities(player).forEach(Ability::tick);
		}
	}

	private static void checkDisabledAbilities() {
		for (Abilities ability : Abilities.values()) {
			boolean disabled = !ability.isEnabled();
			boolean storedDisabled = disabledAbilities.contains(ability);
			if (disabled == storedDisabled) continue;

			if (disabled) {
				disabledAbilities.add(ability);
			}
			else {
				disabledAbilities.remove(ability);
			}

			for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
				Ability abilityInstance = getStoredAbility(player, ability);
				if (abilityInstance == null) continue;
				if (disabled) {
					abilityInstance.onEnd();
				}
				else {
					abilityInstance.onStart();
				}
				resetPlayerMaxHealth(player);
				sendUpdatePacket(player);
			}
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
			if (joined != null) getActiveAbilities(joined).forEach(Ability::onStart);
		});
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		getActiveAbilities(player).forEach(Ability::onEnd);
	}

	public static float modifyDamage(ServerPlayer player, DamageSource source, float amount) {
		for (Ability ability : getActiveAbilities(player)) {
			amount = ability.modifyDamage(source, amount);
		}
		return amount;
	}

	public static boolean preventDamage(ServerPlayer player, DamageSource source) {
		for (Ability ability : getActiveAbilities(player)) {
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
		if (!abilityInstance.ability().isEnabled() || hasStoredAbility(player, abilityInstance.ability())) return;
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
		Ability current = getStoredAbility(player, ability);
		if (shouldHave && current == null) {
			Ability abilityInstance = ability.createAbility().setForced();
			abilities.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).add(abilityInstance);
			abilityInstance.bindPlayer(player);
			if (ability.isEnabled()) abilityInstance.onStart();
			sendUpdatePacket(player);
		}
		else if (!shouldHave && current != null && current.isForced()) {
			if (ability.isEnabled()) current.onEnd();
			abilities.get(player.getUUID()).remove(current);
			sendUpdatePacket(player);
		}
	}

	public static void clearAbilities(ServerPlayer player) {
		boolean hadAbilities = !getPlayerAbilities(player).isEmpty();
		getActiveAbilities(player).forEach(Ability::onEnd);
		abilities.put(player.getUUID(), new ArrayList<>());
		saveStoredAbilities();
		resetPlayerMaxHealth(player);
		sendUpdatePacket(player);
		if (hadAbilities) removeAbilityMessage(player);
	}

	public static void removeAbility(ServerPlayer player, Abilities ability) {
		Ability abilityInstance = getStoredAbility(player, ability);
		if (abilityInstance == null) return; // Does not have this ability
		if (ability.isEnabled()) abilityInstance.onEnd();
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

	// Includes disabled abilities
	public static List<Ability> getPlayerAbilities(ServerPlayer player) {
		return abilities.getOrDefault(player.getUUID(), List.of());
	}

	public static List<Ability> getActiveAbilities(ServerPlayer player) {
		List<Ability> active = new ArrayList<>();
		for (Ability ability : getPlayerAbilities(player)) {
			if (ability.ability().isEnabled()) {
				active.add(ability);
			}
		}
		return active;
	}

	public static boolean hasAbility(ServerPlayer player, Abilities ability) {
		Ability abilityInstance = getAbility(player, ability);
		return abilityInstance != null;
	}

	// Only returns if the ability is enabled
	public static Ability getAbility(ServerPlayer player, Abilities ability) {
		if (!ability.isEnabled()) return null;
		return getStoredAbility(player, ability);
	}

	public static boolean hasStoredAbility(ServerPlayer player, Abilities ability) {
		return getStoredAbility(player, ability) != null;
	}

	private static Ability getStoredAbility(ServerPlayer player, Abilities ability) {
		for (Ability abilityInstance : getPlayerAbilities(player)) {
			if (abilityInstance.ability() == ability) {
				return abilityInstance;
			}
		}
		return null;
	}

	public static void resetPlayerMaxHealth(ServerPlayer player) {
		double health = LifeSeries.seasonConfig.MAX_PLAYER_HEALTH.get();
		for (Ability ability : getActiveAbilities(player)) {
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
			for (Ability ability : getActiveAbilities(player)) {
				abilities.add(ability.ability().name() + ";" + ability.getHeartsChange());
			}
			SimplePackets.ACTIVE_ABILITIES.sendToClient(abilities, player);
		}
	}

	public static void onTriggerAbilityPacket(ServerPlayer player) {
		//TODO soulmate ability trigger
	}
}

