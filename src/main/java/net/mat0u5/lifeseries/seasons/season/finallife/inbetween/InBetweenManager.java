package net.mat0u5.lifeseries.seasons.season.finallife.inbetween;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.registries.ParticleRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLife;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.choice.InBetweenChoice;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.events.EventResult;
import net.mat0u5.matlib.utils.player.PlayerReference;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//? if <= 1.20.5 {
//import net.minecraft.core.BlockPos;
//?} else if <= 1.21 {
//import net.minecraft.world.level.portal.DimensionTransition;
//?} else {
import net.minecraft.world.level.portal.TeleportTransition;
//?}

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;
import static net.mat0u5.matlib.MatLib.server;

public class InBetweenManager {
	public static final String DIMENSION_ID = "in_between";
	public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, IdentifierHelper.lifeseries(DIMENSION_ID));

	public static final ConfigRef<Boolean> ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_IN_BETWEEN);
	public static final ConfigRef<Boolean> ISOLATE_CHAT = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_IN_BETWEEN_ISOLATE_CHAT);

	private static final Map<UUID, InBetweenInstance> instances = new HashMap<>();
	private static final Set<UUID> preparedRespawns = new HashSet<>();

	@Nullable
	public static ServerLevel getLevel() {
		if (server == null) return null;
		return server.getLevel(DIMENSION);
	}

	public static boolean isInInBetween(Entity entity) {
		return entity != null && entity.level().dimension() == DIMENSION;
	}

	public static boolean shouldBlockInteractions(Player player) {
		return isInInBetween(player) && !player.isCreative() && !player.isSpectator();
	}

	private static boolean isFinalLife() {
		return currentSeason instanceof FinalLife;
	}

	public static boolean shouldEnterAfterDeath(ServerPlayer player) {
		if (!isFinalLife() || !ENABLED.get()) return false;
		if (getLevel() == null) return false;
		IPlayer pl = (IPlayer) player;
		if (pl.ls$isWatcher()) return false;
		if (!pl.ls$hasAssignedLives() || !pl.ls$isAlive()) return false;
		if (currentSeason instanceof FinalLife finalLife && finalLife.finalLifeBoogeymanManager.isBoogeyman(player)) return false; //TODO this might not be correct
		return true;
	}

	public static boolean hasInBetweenInstance(ServerPlayer player) {
		return player != null && instances.containsKey(player.getUUID());
	}

	public static void onPlayerRespawn(ServerPlayer player) {
		boolean prepared = preparedRespawns.remove(player.getUUID());
		if (hasInBetweenInstance(player) && isInInBetween(player)) {
			if (prepared) onEnteredInBetween(player);
			return;
		}
		// Fallback, in case the instant respawn failed
		if (shouldEnterAfterDeath(player)) {
			enterInBetween(player);
		}
	}

	public static void onPlayerJoin(ServerPlayer originalPlayer) {
		PlayerReference ref = PlayerReference.of(originalPlayer);
		TaskScheduler.scheduleTask(1, () -> {
			ServerPlayer player = ref.get();
			if (player == null || !isInInBetween(player) || hasInBetweenInstance(player)) return;
			if (!isFinalLife() || !ENABLED.get() || player.isSpectator() || !enterInBetween(player)) {
				exitInBetween(player, null);
			}
		});
	}

	public static void onPlayerDeath(ServerPlayer player) {
		discardInstance(player);
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		discardInstance(player);
	}

	public static EventResult onPrePlayerDamage(ServerPlayer player, DamageSource source) {
		if (!isInInBetween(player)) return EventResult.PASS;
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return EventResult.PASS;
		return EventResult.DENY;
	}

	public static void tick() {
		for (Map.Entry<UUID, InBetweenInstance> entry : new ArrayList<>(instances.entrySet())) {
			UUID uuid = entry.getKey();
			InBetweenInstance instance = entry.getValue();
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			if (player == null || !isInInBetween(player)) {
				discardInstance(uuid);
				continue;
			}
			instance.tick(player);
		}
	}

	public static void onServerStopping() {
		instances.values().forEach(InBetweenInstance::destroy);
		instances.clear();
		preparedRespawns.clear();
	}

	public static void exitInBetweenAllPlayers() {
		for (UUID uuid : new ArrayList<>(instances.keySet())) {
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			if (player != null) {
				exitInBetween(player, null);
			}
			else {
				InBetweenInstance instance = instances.remove(uuid);
				if (instance != null) instance.destroy();
			}
		}
	}

	@Nullable
	public static InBetweenInstance prepareInBetweenInstance(ServerPlayer deadPlayer) {
		if (!shouldEnterAfterDeath(deadPlayer)) return null;
		discardInstance(deadPlayer); // In case there already was one
		InBetweenInstance instance = createInstance(deadPlayer);
		if (instance == null) return null;
		instances.put(deadPlayer.getUUID(), instance);
		preparedRespawns.add(deadPlayer.getUUID());
		return instance;
	}

	@Nullable
	private static InBetweenInstance createInstance(ServerPlayer player) {
		ServerLevel level = getLevel();
		if (level == null) {
			LifeSeries.LOGGER.error("[In-Between] Dimension not found.");
			return null;
		}

		discardInstance(player); // Remove any old instances

		int deathNumber = getDeaths(player);
		InBetweenInstance instance = InBetweenInstance.create(getFirstFreeSlot(), player, level, deathNumber);
		if (instance == null) {
			LifeSeries.LOGGER.error("[In-Between] Structure files not found.");
			return null;
		}
		instance.inBetweenBuilder.build();
		return instance;
	}

	public static boolean enterInBetween(ServerPlayer player) {
		InBetweenInstance instance = createInstance(player);
		if (instance == null) return false;
		UUID uuid = player.getUUID();
		instances.put(uuid, instance);

		player.resetFallDistance();
		player.setDeltaMovement(Vec3.ZERO);
		LevelUtils.teleport(player, instance.level, instance.getSpawnPos(), instance.getSpawnYaw(), 0);
		onEnteredInBetween(player);
		return true;
	}

	private static void onEnteredInBetween(ServerPlayer player) {
		if (player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
			player.setGameMode(GameType.ADVENTURE);
		}
		playDimensionTransition(player);
	}

	public static void exitInBetween(ServerPlayer player, @Nullable InBetweenChoice choice) {
		UUID uuid = player.getUUID();
		InBetweenInstance instance = instances.remove(uuid);
		if (choice != null) {
			choice.onChosen(player);
		}
		List<ItemStack> droppedItems = new ArrayList<>();
		if (instance != null) {
			droppedItems.addAll(instance.collectDroppedItems());
			instance.destroy();
		}

		restoreGameMode(player);
		teleportToRespawnPoint(player);

		for (ItemStack stack : droppedItems) {
			if (!player.getInventory().add(stack) && !stack.isEmpty()) {
				ItemEntity item = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
				item.setNoPickUpDelay();
				player.level().addFreshEntity(item);
			}
		}
		playDimensionTransition(player); //TODO once the animation is added, wait for the finish
	}

	public static void discardInstance(ServerPlayer player) {
		discardInstance(player.getUUID());
	}

	public static void discardInstance(UUID uuid) {
		preparedRespawns.remove(uuid);
		InBetweenInstance instance = instances.remove(uuid);
		if (instance != null) {
			instance.destroy();
		}
	}

	private static void restoreGameMode(ServerPlayer player) {
		if (player.gameMode.getGameModeForPlayer() != GameType.ADVENTURE) return;
		player.setGameMode(GameType.SURVIVAL);
	}

	private static void teleportToRespawnPoint(ServerPlayer player) {
		if (server == null) return;
		player.resetFallDistance();
		player.setDeltaMovement(Vec3.ZERO);

		//? if <= 1.20.5 {
		/*ServerLevel respawnLevel = server.getLevel(player.getRespawnDimension());
		BlockPos respawnPos = player.getRespawnPosition();
		if (respawnLevel != null && respawnPos != null && respawnLevel.dimension() != DIMENSION) {
			Optional<Vec3> pos = Player.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPos, player.getRespawnAngle(), player.isRespawnForced(), true);
			if (pos.isPresent()) {
				LevelUtils.teleport(player, respawnLevel, pos.get(), player.getRespawnAngle(), 0);
				return;
			}
		}
		ServerLevel overworld = server.overworld();
		LevelUtils.teleport(player, overworld, Vec3.atBottomCenterOf(overworld.getSharedSpawnPos()), overworld.getSharedSpawnAngle(), 0);
		*///?} else if <= 1.21 {
		/*DimensionTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
		player.changeDimension(transition);
		*///?} else {
		TeleportTransition transition = player.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
		player.teleport(transition);
		//?}
	}

	private static void playDimensionTransition(ServerPlayer player) {
		PlayerReference ref = PlayerReference.of(player);
		TaskScheduler.scheduleTask(1, () -> {
			ServerPlayer target = ref.get();
			if (target != null) ModifiableSound.FINALLIFE_DIMENSION_SWAP.play(target);
		});
		//TODO animation
	}

	private static int getFirstFreeSlot() {
		Set<Integer> used = new HashSet<>();
		for (InBetweenInstance instance : instances.values()) {
			used.add(instance.inBetweenSlot);
		}
		int slot = 0;
		while (used.contains(slot)) {
			slot++;
		}
		return slot;
	}

	public static int getDeaths(ServerPlayer player) {
		if (currentSeason instanceof FinalLife finalLife) {
			return finalLife.getDeaths(player);
		}
		return 0;
	}

	public static void setDeaths(ServerPlayer player, int deaths) {
		if (currentSeason instanceof FinalLife finalLife) {
			finalLife.setDeaths(player, deaths);
		}
	}

	public static ParticleOptions getAmbientParticles(int deathNumber) {
		if (deathNumber == 3) {
			return ParticleRegistry.IN_BETWEEN_BACKGROUND_TRIVIA;
		}
		return ParticleRegistry.IN_BETWEEN_BACKGROUND;
	}

	public static boolean shouldHideChatMessage(ServerPlayer receiver, OutgoingChatMessage message) {
		if (!isFinalLife() || !ISOLATE_CHAT.get()) return false;
		UUID sender = message instanceof OutgoingChatMessage.Player playerMessage ? playerMessage.message().sender() : null;
		if (receiver.getUUID().equals(sender)) return false;
		if (isInInBetween(receiver)) return true;
		if (sender != null) {
			ServerPlayer senderPlayer = PlayerUtils.getPlayer(sender);
			return senderPlayer != null && isInInBetween(senderPlayer);
		}
		return false;
	}

	public static void onMadeChoice(ServerPlayer player, InBetweenChoice choice) {
		exitInBetween(player, choice);
	}
}
