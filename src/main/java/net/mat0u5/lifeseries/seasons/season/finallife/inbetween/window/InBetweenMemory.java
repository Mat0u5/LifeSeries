package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public record InBetweenMemory(int deathNumber, int number, MemoryFile image, @Nullable MemoryFile sound) {
	public record MemoryFile(Path path, String hash, int size) {}
}
