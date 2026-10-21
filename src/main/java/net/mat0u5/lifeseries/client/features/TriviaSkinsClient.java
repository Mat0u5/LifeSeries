package net.mat0u5.lifeseries.client.features;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaSkins;
import net.mat0u5.matlib.client.utils.SyncedTexturesClient;
import net.minecraft.resources.Identifier;

public class TriviaSkinsClient {
    public static Identifier getTriviaTexture(String skinName) {
        return SyncedTexturesClient.getTexture(TriviaSkins.TEXTURES.getId(), skinName);
    }
}
