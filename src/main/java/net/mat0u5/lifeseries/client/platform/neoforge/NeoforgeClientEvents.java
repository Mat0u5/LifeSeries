package net.mat0u5.lifeseries.client.platform.neoforge;

//? if neoforge {

/*//? if <= 1.20.3 {
/^import net.mat0u5.lifeseries.LifeSeries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
^///?}

//? if <= 1.20.3 {
/^@net.neoforged.fml.common.Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
^///?}
public class NeoforgeClientEvents {
    //? if <= 1.20.3 {
    /^@SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            ClientRenderer.render(event.getGuiGraphicsExtractor());
        }
    }
    ^///?}
}

*///?}