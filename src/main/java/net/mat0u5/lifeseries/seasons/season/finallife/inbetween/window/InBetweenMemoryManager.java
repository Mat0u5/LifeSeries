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
			"death1/1.jpg", "death1/1.ogg", "death1/2.jpg", "death1/2.ogg"
			,"death2/1.jpg", "death2/1.ogg", "death2/2.jpg", "death2/2.ogg"
	);
	private static final Pattern DEATH_FOLDER_PATTERN = Pattern.compile("death([1-9]\\d{0,3})");
	private static final Pattern MEMORY_FILE_PATTERN = Pattern.compile("([1-9]\\d{0,3})\\.(png|jpg|jpeg|ogg)");

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
			if (!InBetweenWindowManager.hasWindows(deathNumber)) continue;

			Map<Integer, Path> images = new TreeMap<>();
			Map<Integer, Path> sounds = new HashMap<>();
			File[] files = deathFolder.listFiles();
			if (files == null) continue;
			for (File file : files) {
				if (!file.isFile()) continue;
				Matcher fileMatcher = MEMORY_FILE_PATTERN.matcher(file.getName().toLowerCase(Locale.ROOT));
				if (!fileMatcher.matches()) continue;
				int number = Integer.parseInt(fileMatcher.group(1));
				if (fileMatcher.group(2).equals("ogg")) {
					sounds.put(number, file.toPath());
				}
				else if (images.putIfAbsent(number, file.toPath()) != null) {
					LifeSeries.LOGGER.warn("[In-Between] Multiple images numbered {} in {}, ignoring {}.", number, deathFolder, file.getName());
				}
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
			if (image ? !isPngFile(data) && !isJpegFile(data) : !isOggFile(data)) {
				LifeSeries.LOGGER.error("[In-Between] File {} is not a valid {} file.", path, image ? "PNG or JPEG" : "OGG (Vorbis)");
				return null;
			}
			if (image) {
				int[] size = isPngFile(data) ? pngSize(data) : jpegSize(data);
				int width = size == null ? 0 : size[0];
				int height = size == null ? 0 : size[1];
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

	public static boolean isPngFile(byte[] data) {
		return data.length > 24 && (data[0] & 0xFF) == 0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G';
	}

	public static boolean isJpegFile(byte[] data) {
		return data.length > 4 && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF;
	}

	private static int[] pngSize(byte[] data) {
		return new int[]{readInt(data, 16, 4), readInt(data, 20, 4)};
	}

	@Nullable
	private static int[] jpegSize(byte[] data) {
		int i = 2;
		while (i + 9 < data.length) {
			if ((data[i] & 0xFF) != 0xFF) return null;
			int marker = data[i + 1] & 0xFF;
			if (marker == 0xFF) {
				i++;
				continue;
			}
			if (marker == 0x01 || (marker >= 0xD0 && marker <= 0xD7)) {
				i += 2;
				continue;
			}
			if (marker == 0xD9 || marker == 0xDA) return null;
			if (marker >= 0xC0 && marker <= 0xCF && marker != 0xC4 && marker != 0xC8 && marker != 0xCC) {
				return new int[]{readInt(data, i + 7, 2), readInt(data, i + 5, 2)};
			}
			i += 2 + readInt(data, i + 2, 2);
		}
		return null;
	}

	private static int readInt(byte[] data, int offset, int bytes) {
		int value = 0;
		for (int i = 0; i < bytes; i++) value = (value << 8) | (data[offset + i] & 0xFF);
		return value;
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
