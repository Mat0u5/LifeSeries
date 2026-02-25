package net.mat0u5.lifeseries.render;

import net.minecraft.client.gui.GuiGraphics;

//? if >= 1.21.2 && <= 1.21.5
//import net.minecraft.client.renderer.RenderType;
//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines;

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
 //?}

public class CustomTextureRenderer {
    //? if <= 1.21.9 {
    /*private final ResourceLocation texture;
    *///?} else {
    private final Identifier texture;
    //?}
    private final float x;
    private final float y;
    private final int width;
    private final int height;
    private int outWidth;
    private int outHeight;
    private int textureWidth;
    private int textureHeight;
    private int u = 0;
    private int v = 0;
    private float scaleX = 1;
    private float scaleY = 1;

    //? if <= 1.21.9 {
    /*public CustomTextureRenderer(ResourceLocation texture, float x, float y, int width, int height) {
    *///?} else {
    public CustomTextureRenderer(Identifier texture, float x, float y, int width, int height) {
     //?}
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.outWidth = width;
        this.outHeight = height;
        this.textureWidth = width;
        this.textureHeight = height;
        this.texture = texture;
    }

    public CustomTextureRenderer scaled(float newScaleX, float newScaleY) {
        scaleX = newScaleX;
        scaleY = newScaleY;
        return this;
    }

    public CustomTextureRenderer uv(int newU, int newV) {
        u = newU;
        v = newV;
        return this;
    }

    public CustomTextureRenderer outSize(int newOutWidth, int newOutHeight) {
        outWidth = newOutWidth;
        outHeight = newOutHeight;
        return this;
    }

    public CustomTextureRenderer textureSize(int newTextureWidth, int newTextureHeight) {
        textureWidth = newTextureWidth;
        textureHeight = newTextureHeight;
        return this;
    }

    private boolean isScaled() {
        return scaleX != 1 || scaleY != 1;
    }

    public void render(GuiGraphics context) {
        if (isScaled()) {
            //? if <= 1.21.5 {
            /*context.pose().pushPose();
            context.pose().scale(scaleX, scaleY, 1.0f);
            *///?} else {
            context.pose().pushMatrix();
            context.pose().scale(scaleX, scaleY);
            //?}
        }

        //? if <= 1.21 {
        /*context.blit(texture, (int) (x / scaleX), (int) (y / scaleY), outWidth, outHeight, u, v, width, height, textureWidth, textureHeight);
        *///?} else if <= 1.21.5 {
        /*context.blit(RenderType::guiTextured, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, outWidth, outHeight, width, height, textureWidth, textureHeight);
        *///?} else {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, outWidth, outHeight, width, height, textureWidth, textureHeight);
        //?}

        if (isScaled()) {
            //? if <= 1.21.5 {
            /*context.pose().popPose();
            *///?} else {
            context.pose().popMatrix();
             //?}
        }
    }
}
