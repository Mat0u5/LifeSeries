package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.utils.resource.ResourceHandler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InBetweenMemoryManager {
	public static final Path MEMORY_FOLDER = Path.of("./config/lifeseries/finallife/memories");
	private static final String BUNDLED_FOLDER = "/files/finallife/memories/";
	public static final int MAX_FILE_SIZE_MiB = 16;
	public static final int MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MiB * 1024 * 1024;
	public static final int MAX_IMAGE_DIMENSION = 4096; // Width / height in pixels
	private static final List<String> BUNDLED_FILES = List.of(
			"death1/1.png", "death1/1.ogg", "death1/2.png", "death1/2.ogg"
			//,"death2/1.png", "death2/1.ogg", "death2/2.png", "death2/2.ogg"
	);
	private static final Pattern DEATH_FOLDER_PATTERN = Pattern.compile("death([1-9]\\d{0,3})");
	private static final Pattern MEMORY_FILE_PATTERN = Pattern.compile("([1-9]\\d{0,3})\\.(png|ogg)");

	// Separate thread for reading and caching
	public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "LifeSeries In-Between Files");
		thread.setDaemon(true);
		return thread;
	});

	private record FileInfo(long size, long lastModified, String hash) {}
	private static final Map<Path, FileInfo> hashCache = new ConcurrentHashMap<>();

	public static List<InBetweenMemory> loadMemories(String playerName) {
		List<InBetweenMemory> result = new ArrayList<>();
		Path playerFolder = getPlayerFolder(playerName);
		File[] deathFolders = playerFolder.toFile().listFiles();
		if (deathFolders == null) return result;

		for (File deathFolder : deathFolders) {
			if (!deathFolder.isDirectory()) continue;
			Matcher deathMatcher = DEATH_FOLDER_PATTERN.matcher(deathFolder.getName().toLowerCase(Locale.ROOT));
			if (!deathMatcher.matches()) continue;
			int deathNumber = Integer.parseInt(deathMatcher.group(1));

			Map<Integer, Path> images = new TreeMap<>();
			Map<Integer, Path> sounds = new HashMap<>();
			File[] files = deathFolder.listFiles();
			if (files == null) continue;
			for (File file : files) {
				if (!file.isFile()) continue;
				Matcher fileMatcher = MEMORY_FILE_PATTERN.matcher(file.getName().toLowerCase(Locale.ROOT));
				if (!fileMatcher.matches()) continue;
				int number = Integer.parseInt(fileMatcher.group(1));
				if (fileMatcher.group(2).equals("png")) images.put(number, file.toPath());
				else sounds.put(number, file.toPath());
			}

			for (Map.Entry<Integer, Path> entry : images.entrySet()) {
				InBetweenMemory.MemoryFile image = readFile(entry.getValue(), true);
				if (image == null) continue;
				Path soundPath = sounds.get(entry.getKey());
				InBetweenMemory.MemoryFile sound = soundPath == null ? null : readFile(soundPath, false);
				result.add(new InBetweenMemory(deathNumber, entry.getKey(), image, sound));
			}
		}
		result.sort(Comparator.comparingInt(InBetweenMemory::deathNumber).thenComparingInt(InBetweenMemory::number));
		return result;
	}

	public static Path getPlayerFolder(String playerName) {
		String folderName = playerName.replaceAll("[^A-Za-z0-9_.-]", "_");
		File[] currentFiles = MEMORY_FOLDER.toFile().listFiles();
		if (currentFiles != null) {
			for (File file : currentFiles) {
				if (file.isDirectory() && file.getName().equalsIgnoreCase(folderName)) {
					return file.toPath();
				}
			}
		}
		Path folder = MEMORY_FOLDER.resolve(folderName);
		ResourceHandler handler = new ResourceHandler();
		// Copy default memories
		for (String file : BUNDLED_FILES) {
			handler.copyBundledSingleFile(BUNDLED_FOLDER + file, folder.resolve(file), true);
		}
		return folder;
	}

	@Nullable
	private static InBetweenMemory.MemoryFile readFile(Path path, boolean image) {
		try {
			Path filePath = path.toAbsolutePath().normalize();
			long fileSize = Files.size(path);
			if (fileSize <=0 || fileSize > MAX_FILE_SIZE_BYTES) {
				LifeSeries.LOGGER.error("[In-Between] File {} has invalid size ({} bytes). Must be > 1 byte and <={}MB.", path, fileSize, MAX_FILE_SIZE_MiB);
				return null;
			}

			// Skip validation checks if it's cached.
			FileInfo cachedFileInfo = hashCache.get(filePath);
			long lastModified = Files.getLastModifiedTime(path).toMillis();
			if (cachedFileInfo != null && cachedFileInfo.size() == fileSize && cachedFileInfo.lastModified() == lastModified) {
				return new InBetweenMemory.MemoryFile(path, cachedFileInfo.hash(), (int) fileSize);
			}

			byte[] data = Files.readAllBytes(path);
			if (image ? !isPngFile(data) : !isOggFile(data)) {
				LifeSeries.LOGGER.error("[In-Between] File {} is not a valid {} file.", path, image ? "PNG" : "OGG (Vorbis)");
				return null;
			}
			if (image) {
				// Width and height are at the start of the PNG header
				int width = ((data[16] & 0xFF) << 24) | ((data[17] & 0xFF) << 16) | ((data[18] & 0xFF) << 8) | (data[19] & 0xFF);
				int height = ((data[20] & 0xFF) << 24) | ((data[21] & 0xFF) << 16) | ((data[22] & 0xFF) << 8) | (data[23] & 0xFF);
				if (width <= 0 || height <= 0 || width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION) {
					LifeSeries.LOGGER.error("[In-Between] Image {} is {}x{}, maximum supported is {}x{}.", path, width, height, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION);
					return null;
				}
			}
			String hash = sha256(data);
			hashCache.put(filePath, new FileInfo(data.length, lastModified, hash));
			return new InBetweenMemory.MemoryFile(path, hash, data.length);
		} catch (Exception e) {
			LifeSeries.LOGGER.error("[In-Between] Failed to read file {}", path, e);
			return null;
		}
	}

	@Nullable
	public static byte[] readBytesIfUnchanged(InBetweenMemory.MemoryFile file) {
		try {
			byte[] data = Files.readAllBytes(file.path());
			if (!sha256(data).equals(file.hash())) {
				LifeSeries.LOGGER.warn("[In-Between] File '{}' has changed since last load. Run '/lifeseries reload'.", file.path());
				return null;
			}
			return data;
		} catch (Exception e) {
			LifeSeries.LOGGER.error("[In-Between] Failed to read file {}", file.path(), e);
			return null;
		}
	}

	private static boolean isPngFile(byte[] data) {
		return data.length > 24 && (data[0] & 0xFF) == 0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G';
	}

	private static final byte[] VORBIS_ID_HEADER = {1, 'v', 'o', 'r', 'b', 'i', 's'};
	private static boolean isOggFile(byte[] data) {
		if (data.length < 27 || data[0] != 'O' || data[1] != 'g' || data[2] != 'g' || data[3] != 'S') return false;
		int packetStart = 27 + (data[26] & 0xFF);
		if (data.length < packetStart + VORBIS_ID_HEADER.length) return false;
		for (int i = 0; i < VORBIS_ID_HEADER.length; i++) {
			if (data[packetStart + i] != VORBIS_ID_HEADER[i]) return false;
		}
		return true;
	}

	private static final Pattern HASH_PATTERN = Pattern.compile("[0-9a-f]{64}");
	public static boolean isValidHash(@Nullable String hash) {
		return hash != null && HASH_PATTERN.matcher(hash).matches();
	}

	public static String sha256(byte[] data) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static String shortHash(String hash) {
		return hash.length() > 12 ? hash.substring(0, 12) : hash;
	}

	public static String formatSize(long bytes) {
		if (bytes < 1024 * 1024) return String.format(Locale.ROOT, "%.1f KiB", bytes / 1024.0);
		return String.format(Locale.ROOT, "%.1f MiB", bytes / (1024.0 * 1024.0));
	}

	public static String formatDuration(long startMillis) {
		return String.format(Locale.ROOT, "%.1fs", (System.currentTimeMillis() - startMillis) / 1000.0);
	}
}
