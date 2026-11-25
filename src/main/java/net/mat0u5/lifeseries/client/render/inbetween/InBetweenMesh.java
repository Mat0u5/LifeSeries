package net.mat0u5.lifeseries.client.render.inbetween;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mat0u5.lifeseries.client.features.inbetween.InBetweenCameraView;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;

//? if <= 1.21.6 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
*///?}

public class InBetweenMesh {
	public static final int ORDER_CLOUDS = -1;
	public static final int ORDER_IMAGES = 0;
	public static final int ORDER_TEXT = 1;
	public static final int ORDER_LIGHT = 2;
	public static final int FULL_BRIGHT = 0xF000F0;
	private static final int VARS = 5; // x, y, z, u, v
	private static final float SCREEN_QUAD_DISTANCE = 0.3f;
	private static final float SCREEN_QUAD_SIZE = 2f;

	public final RenderType renderType;
	public final int order;
	private final boolean text;
	@Nullable
	public final FontText fontText;
	private float[] vertices = new float[VARS * 64];
	private int[] colors = new int[64];
	private int count = 0;

	public record FontText(Component component, Matrix4f pose, int color) {}

	public InBetweenMesh(RenderType renderType, int order, boolean text) {
		this(renderType, order, text, null);
	}

	private InBetweenMesh(RenderType renderType, int order, boolean text, @Nullable FontText fontText) {
		this.renderType = renderType;
		this.order = order;
		this.text = text;
		this.fontText = fontText;
	}

	public static InBetweenMesh fontText(Component component, Matrix4f pose, int color) {
		return new InBetweenMesh(null, ORDER_TEXT, true, new FontText(component, pose, color));
	}

	//? if <= 1.21.6 {
	/*public void drawFontText(Matrix4f pose, MultiBufferSource bufferSource) {
		if (fontText == null) return;
		Minecraft.getInstance().font.drawInBatch(fontText.component(), 0, 0, fontText.color(), true, new Matrix4f(pose).mul(fontText.pose()), bufferSource, Font.DisplayMode.NORMAL, 0, FULL_BRIGHT);
	}
	*///?}

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

	public void addScreenQuad(InBetweenCameraView view, int color) {
		Vector3f forward = new Vector3f(view.forward()).mul(SCREEN_QUAD_DISTANCE);
		Vector3f left = new Vector3f(view.left()).mul(SCREEN_QUAD_SIZE);
		Vector3f up = new Vector3f(view.up()).mul(SCREEN_QUAD_SIZE);
		for (float[] sign : new float[][]{{1, -1}, {-1, -1}, {-1, 1}, {1, 1}}) {
			add(forward.x + left.x * sign[0] + up.x * sign[1], forward.y + left.y * sign[0] + up.y * sign[1], forward.z + left.z * sign[0] + up.z * sign[1], 0, 0, color);
		}
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
