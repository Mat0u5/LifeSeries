package net.mat0u5.lifeseries.client.platform.forge;

//? if forge {

/*//? if <= 1.20 {
/^import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.client.render.ClientRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = LifeSeries.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
^///?}
public class ForgeClientEventSubscriber {
    //? if <= 1.20 {
    /^@SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        ClientRenderer.render(event.getGuiGraphicsExtractor());
    }
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        ClientRenderer.postRender(event.getGuiGraphicsExtractor());
    }
    ^///?}
}
*///?}
