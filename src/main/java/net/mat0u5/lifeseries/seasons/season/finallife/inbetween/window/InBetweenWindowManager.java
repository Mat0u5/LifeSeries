package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window;

import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.trivia.InBetweenTriviaManager;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InBetweenWindowManager {
	public static final ConfigRef<Boolean> WINDOWS_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().IN_BETWEEN_WINDOWS);
	public record WindowMarker(Vec3 pos, float yaw) {} //pos - bottom center of the window
	public record PlacedWindow(InBetweenMemory memory, Vec3 pos, float yaw) {} //pos - real center of the window

	public final Map<Integer, WindowMarker> windowMarkers = new TreeMap<>(); // Filled in InBetweenBuilder
	private List<InBetweenMemory> memories;
	private final List<PlacedWindow> windows = new ArrayList<>();

	public InBetweenWindowManager(ServerPlayer player, int deathNumber) {
		this.memories = InBetweenFileSync.getLoadedMemories(player, deathNumber);
	}

	public void onMemoriesLoaded(ServerPlayer player, List<InBetweenMemory> loaded) {
		if (loaded.equals(memories)) return;
		memories = loaded;
		createWindows();
		if (InBetweenManager.isInInBetween(player)) {
			sendWindowsToClient(player);
		}
	}

	public void createWindows() {
		windows.clear();
		for (InBetweenMemory memory : memories) {
			WindowMarker marker = windowMarkers.get(memory.number());
			if (marker == null) {
				continue;
			}
			double height = InBetweenWindowShapes.get(memory.deathNumber(), memory.number()).length * InBetweenWindowShapes.CELL_SIZE;
			windows.add(new PlacedWindow(memory, marker.pos().add(0, height / 2, 0), marker.yaw()));
		}
	}

	public List<PlacedWindow> getWindows() {
		return windows;
	}

	public void sendWindowsToClient(ServerPlayer player) {
		List<String> entries = new ArrayList<>();
		for (PlacedWindow window : windows) {
			InBetweenMemory memory = window.memory();
			String[] shape = InBetweenWindowShapes.get(memory.deathNumber(), memory.number());
			entries.add(String.format(Locale.ROOT, 
					"%.4f;%.4f;%.4f;%.3f;%s;%s;%.4f;%s",
					window.pos().x, window.pos().y, window.pos().z,
					window.yaw(),
					memory.image().hash(),
					memory.sound() == null ? "-" : memory.sound().hash(),
					InBetweenWindowShapes.CELL_SIZE,
					String.join("/", shape)
			));
		}
		SimplePackets.IN_BETWEEN_WINDOWS.sendToClient(entries, player);
	}

	public static void sendClientClearWindows(ServerPlayer player) {
		SimplePackets.IN_BETWEEN_WINDOWS.sendToClient(List.of(), player);
	}

	public static boolean isEnabled() {
		return InBetweenManager.ENABLED.get() && WINDOWS_ENABLED.get();
	}

	public static boolean hasWindows(int deathNumber) {
		return isEnabled() && !InBetweenTriviaManager.hasTrivia(deathNumber); //TODO perhaps make this more configurable to allow for having both.
	}

	@Nullable
	public static Float directionToYaw(String text) {
		return switch (text.toLowerCase(Locale.ROOT)) {
			case "south", "s" -> 0f;
			case "west", "w" -> 90f;
			case "north", "n" -> 180f;
			case "east", "e" -> -90f;
			default -> null;
		};
	}
}
