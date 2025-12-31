package net.mat0u5.lifeseries.compatibilities;

import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientPlayerStateManager;

public class VoicechatClient {
    public static boolean shouldBeMuted = false;

    public static void checkMute() {
        if (shouldBeMuted && !isMuted()) {
            setMuted(true);
        }
    }

    public static boolean isMuted() {
        ClientPlayerStateManager playerStates = ClientManager.getPlayerStateManager();
        return playerStates.isMuted();
    }

    public static void setMuted(boolean muted) {
        ClientPlayerStateManager playerStates = ClientManager.getPlayerStateManager();
        playerStates.setMuted(muted);
    }
}
