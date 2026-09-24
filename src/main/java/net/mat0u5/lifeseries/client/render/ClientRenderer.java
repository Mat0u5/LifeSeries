package net.mat0u5.lifeseries.client.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import java.util.List;

//? if >= 1.21.2 {
import net.minecraft.util.ARGB;
import org.joml.Vector3f;
//?}

public class ClientRenderer {
    public static final List<String> lifeSkinsAllowedColors = List.of(
            "aqua","black","blue","dark_aqua","dark_blue","dark_gray","dark_green",
            "dark_purple","dark_red","gold","gray","green","light_purple","white","yellow", "red"
    );
    public static final List<String> lifeSkinsAllowedHearts = List.of(
            "hud/heart/full", "hud/heart/full_blinking", "hud/heart/half", "hud/heart/half_blinking",
            "hud/heart/hardcore_full", "hud/heart/hardcore_full_blinking", "hud/heart/hardcore_half", "hud/heart/hardcore_half_blinking"
    );
    public static boolean isGameFullyFrozen = false;

    public static void render(GuiGraphicsExtractor graphics) {
        TextHud.renderText(graphics);
    }

    public static void postRender(GuiGraphicsExtractor graphics) {
        TextHud.renderTextPost(graphics);
    }

    public static Vec3 modifyColor(Vec3 original, Vec3 targetColor, boolean setMode, Vec3 cache) {
        if (targetColor != null) {
            if (setMode) {
                if (cache != null && targetColor.x == -1 && targetColor.y == -1 && targetColor.z == -1) {
                    return cache;
                }
                return targetColor;
            }
            else {
                return new Vec3(
                        Math.min(1, Math.max(0, original.x+targetColor.x)),
                        Math.min(1, Math.max(0, original.y+targetColor.y)),
                        Math.min(1, Math.max(0, original.z+targetColor.z))
                );
            }
        }
        return original;
    }

    //? if >= 1.21.2 {
    public static int modifyColor(int originalInt, Vec3 targetColor, boolean setMode, Vec3 cache) {
        Vector3f original = ARGB.vector3fFromRGB24(originalInt);
        if (targetColor != null) {
            if (setMode) {
                if (cache != null && targetColor.x == -1 && targetColor.y == -1 && targetColor.z == -1) {
                    return ARGB.color(cache);
                }
                return ARGB.color(targetColor);
            }
            else {
                return ARGB.color(new Vec3(
                        Math.min(1, Math.max(0, original.x+targetColor.x)),
                        Math.min(1, Math.max(0, original.y+targetColor.y)),
                        Math.min(1, Math.max(0, original.z+targetColor.z))
                ));
            }
        }
        return originalInt;
    }

    public static Vector3f modifyColor(Vector3f original, Vec3 targetColor, boolean setMode, Vec3 cache) {
        Vec3 newColor =  modifyColor(new Vec3(original.x, original.y, original.z), targetColor, setMode, cache);
        return new Vector3f((float) newColor.x, (float) newColor.y, (float) newColor.z);
    }
    //?}

    public static Vec3 modifyColor(float r, float g, float b, Vec3 targetColor, boolean setMode, Vec3 cache) {
        return modifyColor(new Vec3(r, g, b), targetColor, setMode, cache);
    }

    public static Vector4f modifyColor(Vector4f original, Vec3 targetColor, boolean setMode, Vec3 cache) {
        Vec3 newColor =  modifyColor(new Vec3(original.x, original.y, original.z), targetColor, setMode, cache);
        return new Vector4f((float) newColor.x, (float) newColor.y, (float) newColor.z, original.w);
    }
}
