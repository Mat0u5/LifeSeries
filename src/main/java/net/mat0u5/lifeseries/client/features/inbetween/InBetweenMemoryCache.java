package net.mat0u5.lifeseries.client.features.inbetween;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenShaders;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window.InBetweenMemoryManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//? if >= 1.21.11 {
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
//?}
//? if >= 1.21.11 <= 26.2 {
//import com.mojang.blaze3d.textures.FilterMode;
//?} else if > 26.2 {
import com.mojang.renderpearl.api.textures.FilterMode;
//?}

public class InBetweenMemoryCache {
	private static final Path CACHE_FOLDER = Path.of("./config/lifeseries/client/finallife/memory_cache");
	private static final long MAX_CACHE_SIZE = 512L * 1024 * 1024; //512 MiB

	// Re-trying failed downloads:
	private static final int RETRY_CHECK_INTERVAL = 20;
	private static final int FAILED_RETRY_DELAY = 60;
	private static final int STALL_RETRY_DELAY = 600; // Doubles after every retry
	private static final int MAX_STALL_RETRY_DELAY = 6000;
	private static final int MAX_FAILED_RETRIES = 3;

	public record LoadedImage(Identifier texture, int width, int height) {}

	private static class Download {
		private final byte[] data;
		private int received = 0;
		private final long startMillis = System.currentTimeMillis();

		private Download(int size) {
			this.data = new byte[size];
		}
	}

	private static class FileRequest {
		private int lastActivity;
		private int retries = 0;
		private int failures = 0;
		private boolean failed = false;

		private FileRequest(int tick) {
			this.lastActivity = tick;
		}
	}

	// Separate thread for reading and caching
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "LifeSeries In-Between File Cache");
		thread.setDaemon(true);
		return thread;
	});

	private static final Map<String, Integer> expectedFiles = new ConcurrentHashMap<>(); // Hash -> size - gotten from the server
	private static final Set<String> imageHashes = ConcurrentHashMap.newKeySet();
	private static final Set<String> availableFiles = ConcurrentHashMap.newKeySet(); // Downloaded and checked to be correct
	private static final Map<String, Download> downloads = new HashMap<>(); // Current downloads
	private static final Map<String, FileRequest> requests = new HashMap<>(); // Requested from the server, not available yet
	private static final Map<String, LoadedImage> images = new HashMap<>();
	private static final Set<String> loadingImages = new HashSet<>();
	private static final Set<String> failedImages = new HashSet<>();
	private static int ticks = 0;

	public static void onFilesPacket(List<String> entries) {
		Map<String, Integer> files = new LinkedHashMap<>();
		Set<String> newImages = new HashSet<>();
		for (String entry : entries) {
			String[] split = entry.split(";");
			if (split.length != 3 || !InBetweenMemoryManager.isValidHash(split[1])) continue;
			try {
				int size = Integer.parseInt(split[2]);
				if (size <= 0 || size > InBetweenMemoryManager.MAX_FILE_SIZE_BYTES) continue;
				files.put(split[1], size);
				if (split[0].equals("image")) newImages.add(split[1]);
			} catch (NumberFormatException ignored) {}
		}
		expectedFiles.keySet().retainAll(files.keySet());
		expectedFiles.putAll(files);
		availableFiles.retainAll(files.keySet());
		imageHashes.clear();
		imageHashes.addAll(newImages);
		downloads.keySet().retainAll(files.keySet());
		requests.keySet().retainAll(files.keySet());
		failedImages.retainAll(newImages);

		// Removes un-needed images
		images.entrySet().removeIf(entry -> {
			if (!newImages.contains(entry.getKey())) {
				Minecraft.getInstance().getTextureManager().release(entry.getValue().texture());
				return true;
			}
			return false;
		});

		CompletableFuture.runAsync(() -> {
			List<String> missing = new ArrayList<>();
			for (Map.Entry<String, Integer> file : files.entrySet()) {
				if (availableFiles.contains(file.getKey()) || isCached(file.getKey(), file.getValue())) {
					// Files available from the cache folder
					availableFiles.add(file.getKey());
				}
				else {
					// Missing, will need to send a request to the server to download.
					missing.add(file.getKey());
				}
			}
			Minecraft.getInstance().execute(() -> {
				missing.removeIf(hash -> !expectedFiles.containsKey(hash));
				if (!missing.isEmpty()) {
					for (String hash : missing) {
						requests.put(hash, new FileRequest(ticks));
					}
					LifeSeries.LOGGER.info("[In-Between] Requesting {} memory file(s) from the server: {}", missing.size(), describeFiles(missing));
					SimplePackets.IN_BETWEEN_MEMORY_FILE_REQUEST.sendToServer(missing);
				}
				for (String hash : newImages) {
					if (availableFiles.contains(hash)) loadAndRegisterImage(hash);
				}
			});
		}, EXECUTOR);
	}

	public static void tick() {
		ticks++;
		if (ticks % RETRY_CHECK_INTERVAL != 0 || requests.isEmpty()) return;
		List<String> retry = new ArrayList<>();
		Iterator<Map.Entry<String, FileRequest>> iterator = requests.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, FileRequest> entry = iterator.next();
			String hash = entry.getKey();
			FileRequest request = entry.getValue();
			if (availableFiles.contains(hash) || !expectedFiles.containsKey(hash)) {
				iterator.remove();
				continue;
			}
			if (request.failures > MAX_FAILED_RETRIES) {
				LifeSeries.LOGGER.error("[In-Between] Failed to download memory file {} {} times, stopping.", hash, request.failures);
				iterator.remove();
				continue;
			}
			int delay = request.failed ? FAILED_RETRY_DELAY : Math.min(STALL_RETRY_DELAY << Math.min(request.retries, 8), MAX_STALL_RETRY_DELAY);
			if (ticks - request.lastActivity < delay) continue;
			if (!request.failed) request.retries++;
			request.failed = false;
			request.lastActivity = ticks;
			retry.add(hash);
		}
		if (!retry.isEmpty()) {
			LifeSeries.LOGGER.info("[In-Between] Re-requesting {} memory file(s) from the server: {}", retry.size(), describeFiles(retry));
			SimplePackets.IN_BETWEEN_MEMORY_FILE_REQUEST.sendToServer(retry);
		}
	}

	private static void onDownloadFailed(String hash) {
		if (!expectedFiles.containsKey(hash) || availableFiles.contains(hash)) return;
		FileRequest request = requests.computeIfAbsent(hash, h -> new FileRequest(ticks));
		request.failures++;
		request.failed = true;
		request.lastActivity = ticks;
	}

	private static void onFileMissing(String hash) {
		if (!availableFiles.remove(hash)) return;
		Minecraft.getInstance().execute(() -> {
			if (!expectedFiles.containsKey(hash) || availableFiles.contains(hash)) return;
			requests.putIfAbsent(hash, new FileRequest(ticks - STALL_RETRY_DELAY));
		});
	}

	// Part of the total file, sent over many ticks to reduce network stress.
	public static void onFilePartPayload(String hash, int totalSize, int offset, byte[] data) {
		Integer expectedSize = expectedFiles.get(hash);
		if (expectedSize == null || expectedSize != totalSize || availableFiles.contains(hash)) return;

		Download download = downloads.get(hash);
		if (offset == 0) {
			download = new Download(totalSize);
			downloads.put(hash, download);
			LifeSeries.LOGGER.info("[In-Between] Downloading memory {} {} ({})", fileType(hash), InBetweenMemoryManager.shortHash(hash), InBetweenMemoryManager.formatSize(totalSize));
		}
		if (download == null) return;
		if (offset != download.received || offset + data.length > totalSize) {
			downloads.remove(hash);
			LifeSeries.LOGGER.warn("[In-Between] Failed to download memory {} {}: received an invalid part", fileType(hash), InBetweenMemoryManager.shortHash(hash));
			onDownloadFailed(hash);
			return;
		}
		FileRequest request = requests.get(hash);
		if (request != null) request.lastActivity = ticks;
		System.arraycopy(data, 0, download.data, offset, data.length);
		download.received += data.length;
		if (download.received < totalSize) return;

		downloads.remove(hash);
		byte[] fileData = download.data;
		long startMillis = download.startMillis;
		CompletableFuture.runAsync(() -> {
			if (!InBetweenMemoryManager.sha256(fileData).equals(hash)) {
				LifeSeries.LOGGER.error("[In-Between] Failed to download memory {} {} - file corrupted", fileType(hash), InBetweenMemoryManager.shortHash(hash));
				Minecraft.getInstance().execute(() -> onDownloadFailed(hash));
				return;
			}
			try {
				Files.createDirectories(CACHE_FOLDER);
				Path temp = CACHE_FOLDER.resolve(hash + ".tmp");
				Files.write(temp, fileData);
				Files.move(temp, CACHE_FOLDER.resolve(hash), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LifeSeries.LOGGER.error("[In-Between] Failed to download memory {} {}: could not save it to the cache", fileType(hash), InBetweenMemoryManager.shortHash(hash), e);
				Minecraft.getInstance().execute(() -> onDownloadFailed(hash));
				return;
			}
			LifeSeries.LOGGER.info("[In-Between] Downloaded memory {} {} ({}) in {}", fileType(hash), InBetweenMemoryManager.shortHash(hash), InBetweenMemoryManager.formatSize(fileData.length), InBetweenMemoryManager.formatDuration(startMillis));
			if (!expectedFiles.containsKey(hash)) return;
			availableFiles.add(hash);
			deleteOldCacheFiles();
			Minecraft.getInstance().execute(() -> {
				requests.remove(hash);
				failedImages.remove(hash);
				if (imageHashes.contains(hash)) loadAndRegisterImage(hash);
			});
		}, EXECUTOR);
	}

	private static String fileType(String hash) {
		return imageHashes.contains(hash) ? "image" : "sound";
	}

	private static String describeFiles(List<String> hashes) {
		List<String> names = new ArrayList<>();
		for (String hash : hashes) {
			names.add(fileType(hash) + " " + InBetweenMemoryManager.shortHash(hash));
		}
		return String.join(", ", names);
	}

	@Nullable
	public static LoadedImage getImage(String hash) {
		LoadedImage image = images.get(hash);
		if (image == null && availableFiles.contains(hash)) loadAndRegisterImage(hash);
		return image;
	}

	public static boolean isAvailable(String hash) {
		return availableFiles.contains(hash);
	}

	@Nullable
	public static byte[] readFile(String hash) {
		if (!availableFiles.contains(hash)) return null;
		try {
			return Files.readAllBytes(CACHE_FOLDER.resolve(hash));
		} catch (IOException e) {
			LifeSeries.LOGGER.error("[In-Between] Failed to read memory file {}, re-trying download.", hash, e);
			onFileMissing(hash);
			return null;
		}
	}

	private static void loadAndRegisterImage(String hash) {
		if (images.containsKey(hash) || failedImages.contains(hash) || !loadingImages.add(hash)) return;
		CompletableFuture.supplyAsync(() -> {
			byte[] data = readFile(hash);
			if (data == null) return null;
			try {
				NativeImage image = InBetweenMemoryManager.isJpegFile(data) ? readJpeg(data) : NativeImage.read(new ByteArrayInputStream(data));
				if (image.getWidth() > InBetweenMemoryManager.MAX_IMAGE_DIMENSION || image.getHeight() > InBetweenMemoryManager.MAX_IMAGE_DIMENSION) {
					LifeSeries.LOGGER.error("[In-Between] Image {} is {}x{}, maximum supported is {}x{}.", hash, image.getWidth(), image.getHeight(), InBetweenMemoryManager.MAX_IMAGE_DIMENSION, InBetweenMemoryManager.MAX_IMAGE_DIMENSION);
					image.close();
					return null;
				}
				return image;
			} catch (IOException e) {
				LifeSeries.LOGGER.error("[In-Between] Image {} is not a valid PNG or JPEG.", hash, e);
				return null;
			}
		}, EXECUTOR).whenComplete((image, err) -> Minecraft.getInstance().execute(() -> {
			loadingImages.remove(hash);
			if (image == null) {
				failedImages.add(hash);
				return;
			}
			if (!imageHashes.contains(hash)) {
				image.close();
				return;
			}
			int width = image.getWidth();
			int height = image.getHeight();
			Identifier id = IdentifierHelper.lifeseries("dynamic/in_between_memory/" + hash);
			try {
				Minecraft.getInstance().getTextureManager().register(id, createMemoryTexture(hash, image));
				images.put(hash, new LoadedImage(id, width, height));
			} catch (Exception e) {
				LifeSeries.LOGGER.error("[In-Between] Failed to create texture of image {}", hash, e);
				failedImages.add(hash);
			}
		}));
	}

	// NativeImage only reads PNGs
	private static NativeImage readJpeg(byte[] data) throws IOException {
		try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
			if (!readers.hasNext()) throw new IOException("No JPEG reader available");
			ImageReader reader = readers.next();
			try {
				reader.setInput(input, true, true);
				int width = reader.getWidth(0);
				int height = reader.getHeight(0);
				if (width <= 0 || height <= 0 || width > InBetweenMemoryManager.MAX_IMAGE_DIMENSION || height > InBetweenMemoryManager.MAX_IMAGE_DIMENSION) {
					throw new IOException("Invalid image size " + width + "x" + height);
				}
				BufferedImage decoded = reader.read(0);
				NativeImage image = new NativeImage(width, height, false);
				int[] row = new int[width];
				for (int y = 0; y < height; y++) {
					decoded.getRGB(0, y, width, 1, row, 0, width);
					for (int x = 0; x < width; x++) InBetweenShaders.setPixel(image, x, y, row[x] | 0xFF000000);
				}
				return image;
			} finally {
				reader.dispose();
			}
		}
	}

	private static MemoryTexture createMemoryTexture(String hash, NativeImage image) {
		//? if <= 1.21.4 {
		/*MemoryTexture texture = new MemoryTexture(image);
		texture.setFilter(true, false);
		return texture;
		*///?} else if <= 1.21.9 {
		/*MemoryTexture texture = new MemoryTexture(() -> "In-Between memory " + hash, image);
		texture.setFilter(true, false);
		return texture;
		*///?} else {
		return new MemoryTexture(() -> "In-Between memory " + hash, image);
		//?}
	}

	// Smooth filtering
	private static class MemoryTexture extends DynamicTexture {
		//? if <= 1.21.4 {
		/*MemoryTexture(NativeImage image) {
			super(image);
		}
		*///?} else if <= 1.21.9 {
		/*MemoryTexture(java.util.function.Supplier<String> label, NativeImage image) {
			super(label, image);
		}
		*///?} else {
		MemoryTexture(Supplier<String> label, NativeImage image) {
			super(label, image);
			this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
		}
		//?}

		//? if <= 1.21.9 {
		/*@Override
		public void setFilter(boolean blur, boolean mipmap) {
			super.setFilter(true, false);
		}
		*///?}
	}

	private static boolean isCached(String hash, int size) {
		Path file = CACHE_FOLDER.resolve(hash);
		try {
			if (!Files.isRegularFile(file) || Files.size(file) != size) return false;
			if (!InBetweenMemoryManager.sha256(Files.readAllBytes(file)).equals(hash)) {
				Files.deleteIfExists(file);
				return false;
			}
			Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis())); // Recently used files are deleted last in deleteOldCacheFiles()
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static void deleteOldCacheFiles() {
		File[] files = CACHE_FOLDER.toFile().listFiles();
		if (files == null) return;
		long totalSize = 0;
		for (File file : files) totalSize += file.length();
		if (totalSize <= MAX_CACHE_SIZE) return;

		Arrays.sort(files, Comparator.comparingLong(File::lastModified));
		for (File file : files) {
			if (totalSize <= MAX_CACHE_SIZE) break;
			if (expectedFiles.containsKey(file.getName())) continue;
			long size = file.length();
			if (file.delete()) {
				totalSize -= size;
				availableFiles.remove(file.getName());
			}
		}
	}

	public static void clear() {
		expectedFiles.clear();
		imageHashes.clear();
		availableFiles.clear();
		downloads.clear();
		requests.clear();
		loadingImages.clear();
		failedImages.clear();
		for (LoadedImage image : images.values()) {
			Minecraft.getInstance().getTextureManager().release(image.texture());
		}
		images.clear();
	}
}
