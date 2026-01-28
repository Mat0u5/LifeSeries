package net.mat0u5.lifeseries.features;

import com.mojang.blaze3d.platform.NativeImage;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
 *///?} else {
import net.minecraft.resources.Identifier;
//?}

public class SnailSkinsClient {
    //? if <= 1.21.9 {
    /*private static final Map<String, ResourceLocation> snailTextures = new HashMap<>();
    *///?} else {
    private static final Map<String, Identifier> snailTextures = new HashMap<>();
    //?}

    public static void handleSnailTexture(String skinName, byte[] textureData) {
        try {
            Main.LOGGER.info(TextUtils.formatString("Received snail texture '{}'", skinName));
            Minecraft client = Minecraft.getInstance();

            var textureId = IdentifierHelper.mod("dynamic/snailskin/" + skinName);

            NativeImage image = NativeImage.read(new ByteArrayInputStream(textureData));
            if (image.getWidth() == 32 && image.getHeight() == 32) {
                Main.LOGGER.info("Converting old 32x32 snail texture to the new format.");
                image = convertOldSnailTexture(image);
                if (Main.DEBUG) saveImageDebug(image,"config/lifeseries/wildlife/snailskins/convert", skinName);
            }

            if (!(image.getWidth() == 128 && image.getHeight() == 128)) {
                Main.LOGGER.info("Snail texture has wrong dimensions, ignoring.");
                return;
            }

            //? if <= 1.21.4 {
            /*DynamicTexture texture = new DynamicTexture(image);
            *///?} else {
            DynamicTexture texture = new DynamicTexture(() -> skinName, image);
            //?}
            removeSnailTexture(skinName);
            client.getTextureManager().register(textureId, texture);
            Main.LOGGER.info(TextUtils.formatString("Added snail texture '{}'", textureId));

            snailTextures.put(skinName, textureId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //? if <= 1.21.9 {
    /*public static ResourceLocation getSnailTexture(String skinName) {
    *///?} else {
    public static Identifier getSnailTexture(String skinName) {
    //?}
        if (skinName == null) return null;
        return snailTextures.get(skinName);
    }

    public static void removeSnailTexture(String skinName) {
        var textureId = snailTextures.remove(skinName);
        if (textureId != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
            Main.LOGGER.info(TextUtils.formatString("Removed old snail texture '{}'", textureId));
        }
    }

    public static NativeImage convertOldSnailTexture(NativeImage original) {
        NativeImage remapped = new NativeImage(128, 128, true);


        // Parachute
        scaleRegion(original, remapped, 24, 18, 4, 4, 16, 0, 4);
        scaleRegion(original, remapped, 24, 18, 4, 4, 32, 0, 4);

        scaleRegionXY(original, remapped, 28, 18, 4, 1, 16, 16, 4, 1);
        scaleRegionXY(original, remapped, 28, 19, 4, 1, 0, 16, 4, 1);
        scaleRegionXY(original, remapped, 28, 20, 4, 1, 48, 16, 4, 1);
        scaleRegionXY(original, remapped, 28, 21, 4, 1, 32, 16, 4, 1);

        copyRegion(original, remapped, 26, 1, 1, 4, 60, 37);
        copyRegion(original, remapped, 26, 1, 1, 4, 58, 38);
        copyRegion(original, remapped, 26, 1, 1, 4, 58, 42);
        copyRegion(original, remapped, 26, 1, 1, 4, 58, 47);
        copyRegion(original, remapped, 26, 1, 1, 4, 46, 58);
        copyRegion(original, remapped, 26, 1, 1, 4, 44, 58);
        copyRegion(original, remapped, 26, 1, 1, 4, 40, 59);
        copyRegion(original, remapped, 26, 1, 1, 4, 42, 59);

        scaleRegionXY(original, remapped, 29, 0, 1, 1, 34, 33, 34, 4);
        scaleRegionXY(original, remapped, 29, 0, 1, 1, 0, 50, 34, 1);
        scaleRegionXY(original, remapped, 29, 0, 1, 1, 0, 33, 34, 1);
        scaleRegionXY(original, remapped, 29, 0, 1, 1, 16, 17, 2, 34);

        // Propeller
        copyRegion(original, remapped, 24, 18, 4, 4, 36, 53);
        copyRegion(original, remapped, 24, 18, 4, 4, 40, 53);
        copyRegion(original, remapped, 28, 18, 4, 1, 36, 57);
        copyRegion(original, remapped, 28, 19, 4, 1, 32, 57);
        copyRegion(original, remapped, 28, 20, 4, 1, 44, 57);
        copyRegion(original, remapped, 28, 21, 4, 1, 40, 57);

        scaleRegionXY(original, remapped, 25, 5, 1, 1, 37, 58, 2, 1);
        scaleRegionXY(original, remapped, 25, 5, 1, 1, 36, 59, 4, 2);

        copyRegion(original, remapped, 26, 6, 6, 6, 40, 47);

        // Main Body
        copyRegion(original, remapped, 0, 0, 8, 8, 42, 25);
        copyRegion(original, remapped, 8, 0, 8, 8, 58, 25);
        copyRegion(original, remapped, 16, 0, 8, 8, 50, 17);
        copyRegion(original, remapped, 0, 8, 8, 8, 34, 25);
        flipRegionHorizontal(remapped, 34, 25, 8, 8);
        copyRegion(original, remapped, 8, 8, 8, 8, 50, 25);
        copyRegion(original, remapped, 0, 16, 8, 8, 42, 17);

        copyRegion(original, remapped, 24, 24, -4, -2, 2, 58);
        copyRegion(original, remapped, 16, 23, -4, 2, 6, 58);
        copyRegion(original, remapped, 22, 14, 4, 2, 2, 60);
        copyRegion(original, remapped, 24, 3, 2, 2, 6, 60);
        copyRegion(original, remapped, 22, 16, 4, 2, 8, 60);
        copyRegion(original, remapped, 24, 1, 2, 2, 0, 60);

        copyRegion(original, remapped, 12, 20, -4, -4, 20, 51);
        copyRegion(original, remapped, 20, 8, -4, 4, 24, 51);
        copyRegion(original, remapped, 26, 12, 4, 2, 16, 55);
        scaleRegionXY(original, remapped, 0, 24, 4, 1, 20, 55, 1, 2);
        copyRegion(original, remapped, 26, 12, 4, 2, 24, 55);
        scaleRegionXY(original, remapped, 24, 0, 4, 1, 28, 55, 1, 2);

        copyRegion(original, remapped, 12, 24, -4, -8, 42, 37);
        copyRegion(original, remapped, 20, 8, -4, 8, 46, 37);
        copyRegion(original, remapped, 22, 12, 8, 2, 34, 45);
        scaleRegionXY(original, remapped, 0, 24, 4, 1, 42, 45, 1, 2);
        copyRegion(original, remapped, 22, 12, 8, 2, 46, 45);
        scaleRegionXY(original, remapped, 24, 0, 4, 1, 54, 45, 1, 2);

        copyRegion(original, remapped, 26, 10, -4, -2, 18, 57);
        copyRegion(original, remapped, 26, 10, -4, 2, 22, 57);
        copyRegion(original, remapped, 16, 23, 2, 2, 16, 59);
        copyRegion(original, remapped, 12, 21, 4, 2, 18, 59);
        copyRegion(original, remapped, 18, 23, 2, 2, 22, 59);
        copyRegion(original, remapped, 16, 21, 4, 2, 24, 59);

        copyRegion(original, remapped, 24, 20, -4, -2, 50, 53);
        copyRegion(original, remapped, 24, 20, -4, 2, 54, 53);
        copyRegion(original, remapped, 20, 8, 2, 5, 48, 55);
        copyRegion(original, remapped, 12, 16, 4, 5, 50, 55);
        copyRegion(original, remapped, 20, 13, 2, 5, 54, 55);
        copyRegion(original, remapped, 16, 16, 4, 5, 56, 55);


        copyRegion(original, remapped, 8, 25, -1, -1, 13, 58);
        copyRegion(original, remapped, 9, 24, -1, 1, 14, 58);
        copyRegion(original, remapped, 5, 24, 1, 3, 12, 59);
        copyRegion(original, remapped, 4, 24, 1, 3, 13, 59);
        copyRegion(original, remapped, 6, 24, 1, 3, 14, 59);
        copyRegion(original, remapped, 24, 5, 1, 3, 15, 59);

        copyRegion(original, remapped, 8, 25, -1, -1, 29, 57);
        copyRegion(original, remapped, 9, 24, -1, 1, 30, 57);
        copyRegion(original, remapped, 5, 24, 1, 3, 28, 58);
        copyRegion(original, remapped, 4, 24, 1, 3, 29, 58);
        copyRegion(original, remapped, 6, 24, 1, 3, 30, 58);
        copyRegion(original, remapped, 24, 5, 1, 3, 31, 58);

        return remapped;
    }

    private static void copyRegion(NativeImage source, NativeImage dest,
                                   int srcX, int srcY, int width, int height,
                                   int destX, int destY) {
        if (width < 0) {
            srcX += width;
            width *= -1;
        }
        if (height < 0) {
            srcY += height;
            height *= -1;
        }

        for (int y = 0; y < Math.abs(height); y++) {
            for (int x = 0; x < Math.abs(width); x++) {
                int color = getColor(source, srcX + x, srcY + y);
                setColor(dest, destX + x, destY + y, color);
            }
        }
    }
    private static void scaleRegion(NativeImage source, NativeImage dest,
                                    int srcX, int srcY, int srcWidth, int srcHeight,
                                    int destX, int destY, int scale) {
        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {
                int color = getColor(source, srcX + x, srcY + y);
                for (int sy = 0; sy < scale; sy++) {
                    for (int sx = 0; sx < scale; sx++) {
                        setColor(dest, destX + x * scale + sx, destY + y * scale + sy, color);
                    }
                }
            }
        }
    }
    private static void scaleRegionXY(NativeImage source, NativeImage dest,
                                      int srcX, int srcY, int srcWidth, int srcHeight,
                                      int destX, int destY, int scaleX, int scaleY) {
        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {
                int color = getColor(source, srcX + x, srcY + y);
                for (int sy = 0; sy < scaleY; sy++) {
                    for (int sx = 0; sx < scaleX; sx++) {
                        setColor(dest, destX + x * scaleX + sx, destY + y * scaleY + sy, color);
                    }
                }
            }
        }
    }

    private static void flipRegionHorizontal(NativeImage image, int x, int y, int width, int height) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width / 2; col++) {
                int leftColor = getColor(image, x + col, y + row);
                int rightColor = getColor(image, x + width - 1 - col, y + row);
                setColor(image, x + col, y + row, rightColor);
                setColor(image, x + width - 1 - col, y + row, leftColor);
            }
        }
    }

    private static int getColor(NativeImage image, int x, int y) {
        //? if <= 1.21 {
        /*return image.getPixelRGBA(x, y);
         *///?} else {
        return image.getPixel(x, y);
        //?}
    }

    private static void setColor(NativeImage image, int x, int y, int color) {
        //? if <= 1.21 {
        /*image.setPixelRGBA(x, y, color);
         *///?} else {
        image.setPixel(x, y, color);
        //?}
    }

    private static void saveImageDebug(NativeImage image, String path, String filename) {
        try {
            Path debugPath = Paths.get(path);
            Files.createDirectories(debugPath);
            Path outputPath = debugPath.resolve(filename + ".png");
            image.writeToFile(outputPath);
            System.out.println("Saved debug texture to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
