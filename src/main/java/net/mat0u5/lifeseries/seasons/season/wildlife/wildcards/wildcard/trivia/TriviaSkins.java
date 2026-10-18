package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.resource.ResourceHandler;
import net.mat0u5.matlib.utils.resource.SyncedTextureFolder;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TriviaSkins {
	public static final Path FOLDER = Path.of("./config/lifeseries/wildlife/triviaskins");
	public static final SyncedTextureFolder TEXTURES = new SyncedTextureFolder(IdentifierHelper.lifeseries("triviaskin"), FOLDER, 128, 128)
			.excludeFiles("example.png");

	public static void sendTexturesTo(ServerPlayer player) {
		TEXTURES.sendTo(player);
	}

	public static void sendTextures() {
		TEXTURES.sendTo(PlayerUtils.getAllPlayers());
	}

	public static List<File> getAllSkinFiles() {
		return TEXTURES.getFiles();
	}

	public static List<String> getAllSkins() {
		return TEXTURES.getAllTextureNames();
	}

	public static void createConfig() {
		if (!TEXTURES.createFolder()) return;
		copyConfigFiles();
	}

	private static void copyConfigFiles() {
		ResourceHandler handler = new ResourceHandler();

		handler.copyBundledSingleFile("/files/trivia/triviabot-skins.bbmodel", FOLDER.resolve("triviabot.bbmodel"));
		handler.copyBundledSingleFile("/files/trivia/example.png", FOLDER.resolve("example.png"));
	}
}
