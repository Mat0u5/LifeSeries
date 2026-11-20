package net.mat0u5.lifeseries.client.render.inbetween;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

import java.util.Arrays;

public class InBetweenMesh {
	public static final int ORDER_CLOUDS = -1;
	public static final int ORDER_IMAGES = 0;
	public static final int ORDER_LIGHT = 2;
	private static final int FULL_BRIGHT = 0xF000F0;
	private static final int VARS = 5; // x, y, z, u, v

	public final RenderType renderType;
	public final int order;
	private final boolean text;
	private float[] vertices = new float[VARS * 64];
	private int[] colors = new int[64];
	private int count = 0;

	public InBetweenMesh(RenderType renderType, int order, boolean text) {
		this.renderType = renderType;
		this.order = order;
		this.text = text;
	}

	// Color - 0xAARRGGBB format
	public void add(double x, double y, double z, double u, double v, int color) {
		if (count >= colors.length) {
			vertices = Arrays.copyOf(vertices, vertices.length * 2);
			colors = Arrays.copyOf(colors, colors.length * 2);
		}
		int i = count * VARS;
		vertices[i] = (float) x;
		vertices[i + 1] = (float) y;
		vertices[i + 2] = (float) z;
		vertices[i + 3] = (float) u;
		vertices[i + 4] = (float) v;
		colors[count] = color;
		count++;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public void render(Matrix4f pose, VertexConsumer consumer) {
		for (int vertex = 0; vertex < count; vertex++) {
			int i = vertex * VARS;
			int color = colors[vertex];
			int red = (color >> 16) & 0xFF;
			int green = (color >> 8) & 0xFF;
			int blue = color & 0xFF;
			int alpha = (color >>> 24) & 0xFF;
			//? if <= 1.20.5 {
			/*if (text) consumer.vertex(pose, vertices[i], vertices[i + 1], vertices[i + 2]).color(red, green, blue, alpha).uv(vertices[i + 3], vertices[i + 4]).uv2(FULL_BRIGHT).endVertex();
			else consumer.vertex(pose, vertices[i], vertices[i + 1], vertices[i + 2]).uv(vertices[i + 3], vertices[i + 4]).color(red, green, blue, alpha).endVertex();
			*///?} else {
			VertexConsumer added = consumer.addVertex(pose, vertices[i], vertices[i + 1], vertices[i + 2]).setUv(vertices[i + 3], vertices[i + 4]).setColor(red, green, blue, alpha);
			if (text) added.setLight(FULL_BRIGHT);
			//?}
		}
	}
}
