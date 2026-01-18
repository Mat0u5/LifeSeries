package net.mat0u5.lifeseries.compatibilities;

//TODO
//? if <= 1.21.11 {
import com.moulberry.flashback.playback.ReplayServer;
//?}
import net.minecraft.server.MinecraftServer;

public class FlashbackCompatibility {
    public static boolean isReplayServer(MinecraftServer server) {
        //? if <= 1.21.11 {
        return server instanceof ReplayServer;
        //?} else {
        /*return false;
        *///?}
    }
}
