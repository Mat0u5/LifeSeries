package net.mat0u5.lifeseries.client.compatibilities;

import com.replaymod.replay.ReplayModReplay;

public class ReplayModCompatibility {
    public static boolean isReplayServer() {
         return ReplayModReplay.instance.getReplayHandler() != null;
    }
}
