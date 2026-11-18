package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.packets.InBetweenMemoryPayload;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenInstance;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.network.NetworkHandlerServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.mat0u5.matlib.MatLib.server;

public class InBetweenFileSync {
	public static final int CHUNK_SIZE = 64 * 1024; // 64 KiB
	private static final int CHUNKS_PER_TICK = 1; // 1.25 MiB/s per active transfer
	private static final int MAX_ACTIVE_TRANSFERS = 2;
	private static final int CHECK_INTERVAL = 20;

	private static final Map<UUID, PlayerBoundFiles> players = new HashMap<>();
	private static final Map<UUID, Transfer> transfers = new LinkedHashMap<>(); // In the order they were requested
	private static final Set<UUID> loading = new HashSet<>();
	private static int checkCooldown = 0;

	private static class PlayerBoundFiles {
		private List<InBetweenMemory> memories;
		private List<String> sentEntries = List.of();
		private Map<String, InBetweenMemory.MemoryFile> requestableFiles = Map.of(); // Hash -> file

		private PlayerBoundFiles(List<InBetweenMemory> memories) {
			this.memories = memories;
		}
	}

	private static class Transfer {
		private final Deque<InBetweenMemory.MemoryFile> queue = new ArrayDeque<>();
		@Nullable
		private InBetweenMemory.MemoryFile current;
		@Nullable
		private CompletableFuture<byte[]> data;
		private int offset = 0;
		private long startMillis = 0;

		private boolean has(String hash) {
			if (current != null && current.hash().equals(hash)) return true;
			for (InBetweenMemory.MemoryFile file : queue) {
				if (file.hash().equals(hash)) {
					return true;
				}
			}
			return false;
		}

		private void updateTargetFiles(Set<String> hashes) {
			queue.removeIf(file -> !hashes.contains(file.hash()));
			if (current != null && !hashes.contains(current.hash())) {
				current = null;
				data = null;
			}
		}

		private void pause() {
			data = null;
		}

		private boolean isEmpty() {
			return current == null && queue.isEmpty();
		}
	}

	// Loads the memory files of the player in the background, then tells the client which files it needs
	public static void checkPlayerMemories(ServerPlayer player) {
		if (server == null) return;
		if (!InBetweenWindowManager.isEnabled()) {
			applyMemories(player, List.of());
			return;
		}
		UUID uuid = player.getUUID();
		String name = player.getScoreboardName();
		loading.add(uuid);
		CompletableFuture.supplyAsync(() -> InBetweenMemoryManager.loadMemories(name), InBetweenMemoryManager.EXECUTOR)
				.whenCompleteAsync((loadedMemories, error) -> {
					loading.remove(uuid);
					if (error != null) return; // Logged below
					ServerPlayer updatedPlayer = PlayerUtils.getPlayer(uuid);
					if (updatedPlayer == null) return;
					applyMemories(updatedPlayer, loadedMemories);
				}, server)
				.exceptionally(e -> {
					LifeSeries.LOGGER.error("[In-Between] Failed to load the memories of {}", name, e);
					return null;
				});
	}

	private static void applyMemories(ServerPlayer player, List<InBetweenMemory> loadedMemories) {
		setMemories(player, loadedMemories);
		updatePlayer(player);
		InBetweenInstance instance = InBetweenManager.getInstance(player);
		if (instance != null) {
			instance.windowManager.onMemoriesLoaded(player, filterCurrentMemories(loadedMemories, instance.deathNumber));
		}
	}

	public static void onReload() {
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			checkPlayerMemories(player);
		}
	}

	private static void setMemories(ServerPlayer player, List<InBetweenMemory> loaded) {
		PlayerBoundFiles playerFiles = players.get(player.getUUID());
		if (playerFiles == null) {
			players.put(player.getUUID(), new PlayerBoundFiles(loaded));
		}
		else {
			playerFiles.memories = loaded;
		}
	}

	public static void updatePlayer(ServerPlayer player) {
		// Sends the client a list of which files it needs
		PlayerBoundFiles playerFiles = players.get(player.getUUID());
		if (playerFiles == null || player.isDeadOrDying()) return;

		List<InBetweenMemory> neededMemories = filterCurrentMemories(playerFiles.memories, getDesiredMemoryDeathNumber(player));
		List<String> entries = createPacketListFromMemores(neededMemories);
		if (entries.equals(playerFiles.sentEntries)) return;

		Map<String, InBetweenMemory.MemoryFile> files = new HashMap<>();
		for (InBetweenMemory memory : neededMemories) {
			files.put(memory.image().hash(), memory.image());
			if (memory.sound() != null) files.put(memory.sound().hash(), memory.sound());
		}
		playerFiles.sentEntries = entries;
		playerFiles.requestableFiles = files;

		Transfer transfer = transfers.get(player.getUUID());
		if (transfer != null) {
			transfer.updateTargetFiles(files.keySet());
			if (transfer.isEmpty()) transfers.remove(player.getUUID());
		}
		SimplePackets.IN_BETWEEN_MEMORY_FILES.sendToClient(entries, player);
	}

	private static int getDesiredMemoryDeathNumber(ServerPlayer player) {
		InBetweenInstance instance = InBetweenManager.getInstance(player);
		if (instance != null) return instance.deathNumber;
		int deaths = InBetweenManager.getDeaths(player);
		return InBetweenManager.isInInBetween(player) ? deaths : deaths + 1;
	}

	private static List<InBetweenMemory> filterCurrentMemories(List<InBetweenMemory> memories, int deathNumber) {
		if (!InBetweenWindowManager.hasWindows(deathNumber)) return List.of();
		List<InBetweenMemory> result = new ArrayList<>();
		for (InBetweenMemory memory : memories) {
			if (memory.deathNumber() == deathNumber) result.add(memory);
		}
		return result;
	}

	private static List<String> createPacketListFromMemores(List<InBetweenMemory> memories) {
		Set<String> entries = new LinkedHashSet<>();
		for (InBetweenMemory memory : memories) {
			entries.add("image;" + memory.image().hash() + ";" + memory.image().size());
		}
		for (InBetweenMemory memory : memories) {
			if (memory.sound() != null) {
				entries.add("sound;" + memory.sound().hash() + ";" + memory.sound().size());
			}
		}
		return new ArrayList<>(entries);
	}

	public static List<InBetweenMemory> getLoadedMemories(ServerPlayer player, int deathNumber) {
		if (!InBetweenWindowManager.hasWindows(deathNumber)) return List.of();
		PlayerBoundFiles playerFiles = players.get(player.getUUID());
		if (playerFiles == null) {
			if (!loading.contains(player.getUUID())) checkPlayerMemories(player);
			return List.of();
		}
		return filterCurrentMemories(playerFiles.memories, deathNumber);
	}

	public static void onMemoryRequestPacket(ServerPlayer player, List<String> requestedHashes) {
		PlayerBoundFiles playerFiles = players.get(player.getUUID());
		if (playerFiles == null || requestedHashes == null)return;

		Transfer transfer = transfers.computeIfAbsent(player.getUUID(), uuid -> new Transfer());
		for (String hash : requestedHashes) {
			InBetweenMemory.MemoryFile file = playerFiles.requestableFiles.get(hash);
			if (file == null || transfer.has(hash)) continue;
			transfer.queue.add(file);
		}
		if (transfer.isEmpty()) {
			transfers.remove(player.getUUID());
		}
	}

	public static void tick() {
		checkCooldown++;
		if (checkCooldown >= CHECK_INTERVAL) {
			checkCooldown = 0;
			for (UUID uuid : new ArrayList<>(players.keySet())) {
				ServerPlayer player = PlayerUtils.getPlayer(uuid);
				if (player != null) {
					updatePlayer(player);
				}
			}
		}

		if (transfers.isEmpty()) return;

		List<Map.Entry<UUID, Transfer>> order = new ArrayList<>();
		for (Map.Entry<UUID, Transfer> entry : new ArrayList<>(transfers.entrySet())) {
			if (PlayerUtils.getPlayer(entry.getKey()) == null) { // Disconneected
				transfers.remove(entry.getKey());
				continue;
			}
			order.add(entry);
		}
		// Prioritize players already in the in-between
		order.sort(Comparator.comparingInt((Map.Entry<UUID, Transfer> entry) -> needsFilesUrgently(entry.getKey()) ? 0 : 1)
				.thenComparingInt(entry -> entry.getValue().current != null ? 0 : 1));

		int activeTransfers = 0;
		for (Map.Entry<UUID, Transfer> entry : order) {
			Transfer transfer = entry.getValue();
			if (activeTransfers >= MAX_ACTIVE_TRANSFERS) {
				transfer.pause();
				continue;
			}
			activeTransfers++;
			ServerPlayer player = PlayerUtils.getPlayer(entry.getKey());
			boolean transferFinished = player == null || !tickTransfer(player, transfer);
			if (transferFinished) {
				transfers.remove(entry.getKey());
			}
		}
	}

	private static boolean needsFilesUrgently(UUID uuid) {
		ServerPlayer player = PlayerUtils.getPlayer(uuid);
		return player != null && (InBetweenManager.hasInBetweenInstance(player) || InBetweenManager.isInInBetween(player));
	}

	private static boolean tickTransfer(ServerPlayer player, Transfer transfer) {
		int chunks = 0;
		while (chunks < CHUNKS_PER_TICK) {
			if (transfer.current == null) {
				transfer.current = transfer.queue.poll();
				transfer.offset = 0;
				transfer.data = null;
				if (transfer.current == null) return false;
			}
			if (transfer.data == null) {
				InBetweenMemory.MemoryFile file = transfer.current;
				transfer.data = CompletableFuture.supplyAsync(() -> InBetweenMemoryManager.readBytesIfUnchanged(file), InBetweenMemoryManager.EXECUTOR)
						.exceptionally(e -> null);
			}
			if (!transfer.data.isDone()) return true;

			byte[] data = transfer.data.join();
			if (data == null || transfer.offset >= data.length) {
				if (data == null) {
					LifeSeries.LOGGER.warn("[In-Between] Failed to send memory file {} to {}", transfer.current.path(), player.getScoreboardName());
				}
				transfer.current = null;
				continue;
			}

			InBetweenMemory.MemoryFile file = transfer.current;
			if (transfer.offset == 0) {
				transfer.startMillis = System.currentTimeMillis();
				LifeSeries.LOGGER.info("[In-Between] Sending memory file {} ({}) to {}", file.path(), InBetweenMemoryManager.formatSize(data.length), player.getScoreboardName());
			}
			int length = Math.min(CHUNK_SIZE, data.length - transfer.offset);
			byte[] chunk = Arrays.copyOfRange(data, transfer.offset, transfer.offset + length);
			NetworkHandlerServer.sendPacket(player, new InBetweenMemoryPayload(file.hash(), data.length, transfer.offset, chunk));
			transfer.offset += length;
			chunks++;
			if (transfer.offset >= data.length) {
				LifeSeries.LOGGER.info("[In-Between] Sent memory file {} ({}) to {} in {}", file.path(), InBetweenMemoryManager.formatSize(data.length), player.getScoreboardName(), InBetweenMemoryManager.formatDuration(transfer.startMillis));
				transfer.current = null;
				transfer.data = null;
			}
		}
		return true;
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		players.remove(player.getUUID());
		transfers.remove(player.getUUID());
	}

	public static void switchOutOfSeason() {
		players.clear();
		transfers.clear();
		loading.clear();
	}
}
