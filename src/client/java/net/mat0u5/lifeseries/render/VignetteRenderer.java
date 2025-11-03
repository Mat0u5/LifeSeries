package net.mat0u5.lifeseries.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//? if <= 1.21 {
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
*///?}

public class VignetteRenderer {
    private static final ResourceLocation VIGNETTE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");
    private static float vignetteDarkness = 0.0F;
    private static long vignetteEnd = 0;

    public static void renderVignette(GuiGraphics context) {
        if (System.currentTimeMillis() >= vignetteEnd && vignetteEnd != -1) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        float darkness = Mth.clamp(vignetteDarkness, 0.0F, 1.0F);
        if (darkness == 0) return;


        //? if <= 1.21 {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        context.setColor(darkness, darkness, darkness, 1.0F);
        context.blit(VIGNETTE_TEXTURE, 0, 0, -90, 0.0F, 0.0F,
                context.guiWidth(), context.guiHeight(),
                context.guiWidth(), context.guiHeight()
        );

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        //?} else if <= 1.21.5 {
        /*int color = ARGB.colorFromFloat(1.0F, darkness, darkness, darkness);
        context.blit(RenderType::vignette, VIGNETTE_TEXTURE, 0, 0, 0.0F, 0.0F,
                context.guiWidth(), context.guiHeight(), context.guiWidth(), context.guiHeight(), color);
        *///?} else {
        /*int color = ColorHelper.fromFloats(1.0F, darkness, darkness, darkness);
        context.drawTexture(RenderPipelines.VIGNETTE, VIGNETTE_TEXTURE, 0, 0, 0.0F, 0.0F,
                context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight(), color);
        *///?}
    }

    // Call this method to show the vignette for a certain duration
    public static void showVignetteFor(float darkness, long durationMillis) {
        vignetteDarkness = Mth.clamp(darkness, 0.0F, 1.0F);
        if (durationMillis == -1) {
            vignetteEnd = -1;
        }
        else {
            vignetteEnd = System.currentTimeMillis() + durationMillis;
        }
    }
}
