package net.mat0u5.lifeseries.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageUtils {

	public static void copyRegion(BufferedImage src, Graphics2D g, int srcX, int srcY, int width, int height, int destX, int destY) {
		if (width < 0) { srcX += width; width *= -1; }
		if (height < 0) { srcY += height; height *= -1; }
		g.drawImage(src, destX, destY, destX + width, destY + height, srcX, srcY, srcX + width, srcY + height, null);
	}

	public static void copyRegionFlipped(BufferedImage src, Graphics2D g, int srcX, int srcY, int width, int height, int destX, int destY) {
		if (width < 0) { srcX += width; width *= -1; }
		if (height < 0) { srcY += height; height *= -1; }
		g.drawImage(src, destX + width, destY, destX, destY + height, srcX, srcY, srcX + width, srcY + height, null);
	}

	public static void scaleRegionXY(BufferedImage src, Graphics2D g, int srcX, int srcY, int srcW, int srcH, int destX, int destY, int scaleX, int scaleY) {
		int destW = srcW * scaleX;
		int destH = srcH * scaleY;
		g.drawImage(src, destX, destY, destX + destW, destY + destH, srcX, srcY, srcX + srcW, srcY + srcH, null);
	}

	public static void scaleRegion(BufferedImage src, Graphics2D g, int srcX, int srcY, int srcW, int srcH, int destX, int destY, int scale) {
		scaleRegionXY(src, g, srcX, srcY, srcW, srcH, destX, destY, scale, scale);
	}
}
