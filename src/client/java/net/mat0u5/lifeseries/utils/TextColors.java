package net.mat0u5.lifeseries.utils;

public class TextColors {
    public static int DEFAULT = rgb(60, 60, 60);
    public static int DEFAULT_LIGHTER = rgb(94, 94, 94);
    public static int DEBUG = rgb(255, 0, 242);
    public static int PURE_WHITE = rgb(255, 255, 255);
    public static int WHITE = rgb(224, 224, 224);
    public static int WHITE_BORDER = rgb(208, 208, 208);
    public static int BLACK = rgb(0, 0, 0);
    public static int PURPLE = rgb(120, 0, 165);
    public static int PURPLE_A128 = argb(128, 120, 0, 165);

    public static int ORANGE = rgb(214, 150, 26);
    public static int RED = rgb(191, 34, 34);
    public static int LIGHT_RED = rgb(255, 85, 85);

    public static int PASTEL_BLUE = rgb(92, 87, 243);
    public static int PASTEL_ORANGE = rgb(248, 170, 19);
    public static int PASTEL_LIME = rgb(94, 249, 97);
    public static int PASTEL_YELLOW = rgb( 245, 253, 110);
    public static int PASTEL_RED = rgb(237, 91, 100);
    public static int PASTEL_WHITE = rgb(204, 204, 204);

    public static int WHITE_A32 = argb(32, 224, 224, 224);
    public static int WHITE_A64 = argb(64, 224, 224, 224);
    public static int WHITE_A128 = argb(128, 224, 224, 224);
    public static int BLACK_A32 = argb(32, 0, 0, 0);
    public static int BLACK_A64 = argb(64, 0, 0, 0);
    public static int BLACK_A96 = argb(96, 0, 0, 0);
    public static int BLACK_A128 = argb(128, 0, 0, 0);
    public static int BLACK_A160 = argb(160, 0, 0, 0);
    public static int BLACK_A192 = argb(192, 0, 0, 0);
    public static int BLACK_A224 = argb(224, 0, 0, 0);
    public static int LIGHT_GRAY = rgb(170, 170, 170);
    public static int LIGHT_GRAY_A128 = argb(128, 170, 170, 170);
    public static int GRAY = rgb(128, 128, 128);
    public static int DARK_GRAY = rgb(64, 64, 64);
    public static int LIGHT_BLACK = rgb(32, 32, 32);

    public static int GUI_BACKGROUND = rgb(198, 198, 198);
    public static int GUI_GRAY = rgb(85, 85, 85);
    public static int GUI_GRAY_A32 = argb(32, 85, 85, 85);
    public static int GUI_GRAY_A96 = argb(96, 85, 85, 85);
    public static int GUI_GRAY_A128 = argb(128, 85, 85, 85);

    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int toArgb(int alpha, int rgb) {
        return (alpha << 24) | rgb;
    }
}
