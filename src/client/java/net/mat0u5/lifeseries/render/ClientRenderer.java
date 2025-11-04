package net.mat0u5.lifeseries.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class ClientRenderer {
    public static boolean isGameFullyFrozen = false;
    public static void onInitialize() {
        HudRenderCallback.EVENT.register(ClientRenderer::renderText);
    }

    private static void renderText(GuiGraphics context, DeltaTracker renderTickCounter) {
        TextHud.renderText(context);
        VignetteRenderer.renderVignette(context);
    }
}
