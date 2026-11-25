package net.mat0u5.lifeseries.client.features.inbetween;

import net.mat0u5.lifeseries.client.particle.InBetweenParticle;
import net.mat0u5.lifeseries.client.render.inbetween.InBetweenMesh;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.InBetweenChoiceManager;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

//? if >= 1.21.9
import net.minecraft.network.chat.FontDescription;

public class InBetweenChoicesClient {
	//? if <= 1.21.6 {
	//private static final Identifier SMALL_CAPS = IdentifierHelper.lifeseries("small_caps");
	//?} else {
	private static final FontDescription SMALL_CAPS = new FontDescription.Resource(IdentifierHelper.lifeseries("small_caps"));
	//?}
	private static final float HEART_TEXTURE_PIXEL = 0.024F;
	private static final float TITLE_FONT = 0.035F; // Font pixel
	private static final float SUBTITLE_FONT = 0.022F; // Without a heart texture
	private static final float TITLE_Y = 0.5F; // Above the heart texture center
	private static final double FADE_START = 20;
	private static final double FADE_END = 65;
	private static final int MIN_ALPHA = 26;

	private static final List<ChoiceLabel> labels = new ArrayList<>();

	public static void onLabelsPacket(List<String> payload) {
		clear();
		for (String entry : payload) {
			String[] split = entry.split(";", 7);
			if (split.length != 7) {
				continue;
			}
			try {
				Vec3 anchor = new Vec3(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
				Identifier icon = split[3].equals("-") ? null : IdentifierHelper.parse(split[3]);
				labels.add(new ChoiceLabel(anchor, icon, findHeartTexture(Double.parseDouble(split[4])), split[5], split[6]));
			} catch (Exception ignored) {}
		}
	}

	public static void tick(LocalPlayer player) {
		if (labels.isEmpty()) return;
		Minecraft client = Minecraft.getInstance();
		ClientLevel level = client.level;
		if (level == null || !InBetweenManager.isInInBetween(player)) {
			labels.forEach(ChoiceLabel::hide);
			return;
		}
		for (ChoiceLabel label : labels) {
			label.tick(client, level);
		}
	}

	public static void onParticlesCleared() {
		for (ChoiceLabel label : labels) {
			label.particle = null;
		}
	}

	public static void clear() {
		labels.forEach(ChoiceLabel::hide);
		labels.clear();
	}

	private record HeartTexture(Identifier texture, int width, int height) {}

	// textures/gui/menu/heart_texture/hearts_<hearts>.png, null when theres none
	@Nullable
	private static HeartTexture findHeartTexture(double hearts) {
		if (hearts < 0 || hearts % 1 != 0) return null;
		Identifier texture = IdentifierHelper.lifeseries("textures/gui/menu/heart_texture/hearts_" + (int) hearts + ".png");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);
		if (resource.isEmpty()) return null;
		try (InputStream stream = resource.get().open()) {
			ByteBuffer header = ByteBuffer.wrap(stream.readNBytes(24));
			return new HeartTexture(texture, header.getInt(16), header.getInt(20));
		} catch (Exception e) {
			return null;
		}
	}

	private static MutableComponent smallCaps(String text) {
		return Component.literal(text.toLowerCase(Locale.ROOT)).withStyle(style -> style.withFont(SMALL_CAPS));
	}

	private static Matrix4f labelPose(InBetweenCameraView view, Vec3 anchor) {
		Vector3f left = view.left();
		Vector3f up = view.up();
		Vector3f forward = view.forward();
		Vec3 offset = anchor.subtract(view.position());
		return new Matrix4f(
				-left.x, -left.y, -left.z, 0,
				-up.x, -up.y, -up.z, 0,
				-forward.x, -forward.y, -forward.z, 0,
				(float) offset.x, (float) offset.y, (float) offset.z, 1);
	}

	private static InBetweenMesh quad(Identifier texture, Matrix4f pose, float x0, float y0, float x1, float y1, int color) {
		InBetweenMesh mesh = new InBetweenMesh(InBetweenWindowsClient.imageRenderType(texture), InBetweenMesh.ORDER_IMAGES, true);
		addVertex(mesh, pose, x0, y1, 0, 1, color);
		addVertex(mesh, pose, x1, y1, 1, 1, color);
		addVertex(mesh, pose, x1, y0, 1, 0, color);
		addVertex(mesh, pose, x0, y0, 0, 0, color);
		return mesh;
	}

	private static void addVertex(InBetweenMesh mesh, Matrix4f pose, float x, float y, float u, float v, int color) {
		Vector3f pos = pose.transformPosition(x, y, 0, new Vector3f());
		mesh.add(pos.x, pos.y, pos.z, u, v, color);
	}

	private static InBetweenMesh centeredText(Font font, Component text, Matrix4f pose, float y, float fontPixel, int color) {
		float width = font.width(text) * fontPixel;
		Matrix4f textPose = new Matrix4f(pose).translate(-width / 2, y - 5 * fontPixel, 0).scale(fontPixel);
		return InBetweenMesh.fontText(text, textPose, color);
	}

	private static class ChoiceLabel {
		private final Vec3 anchor;
		@Nullable
		private final Identifier icon;
		@Nullable
		private final HeartTexture heartTexture;
		private final Component title;
		private final Component fallbackSubtitle;
		private final AABB boundingBox;
		@Nullable
		private InBetweenParticle particle;
		@Nullable
		private ClientLevel particleLevel;
		private boolean active = true;

		private ChoiceLabel(Vec3 anchor, @Nullable Identifier icon, @Nullable HeartTexture heartTexture, String title, String subtitle) {
			this.anchor = anchor;
			this.icon = icon;
			this.heartTexture = heartTexture;
			this.title = smallCaps(title).withStyle(ChatFormatting.BOLD);
			this.fallbackSubtitle = smallCaps(subtitle);
			double top = InBetweenChoiceManager.ICON_OFFSET + InBetweenChoiceManager.ICON_SIZE;
			this.boundingBox = new AABB(anchor.x - 3, anchor.y - 1, anchor.z - 3, anchor.x + 3, anchor.y + top, anchor.z + 3);
		}

		private void tick(Minecraft client, ClientLevel level) {
			active = true;
			if (particle == null || !particle.isAlive() || particleLevel != level) {
				if (particle != null) particle.remove();
				particle = new InBetweenParticle(level, boundingBox, () -> active, this::buildMeshes);
				particleLevel = level;
				client.particleEngine.add(particle);
			}
		}

		private void hide() {
			active = false;
			if (particle != null) particle.remove();
			particle = null;
			particleLevel = null;
		}

		private List<InBetweenMesh> buildMeshes(InBetweenCameraView view) {
			List<InBetweenMesh> meshes = new ArrayList<>();
			double fade = Math.max(0, Math.min(1, (view.position().distanceTo(anchor) - FADE_START) / (FADE_END - FADE_START)));
			int alpha = (int) Math.round(255 * (1 - fade * fade * (3 - 2 * fade)));
			if (alpha < MIN_ALPHA) return meshes;
			int color = alpha << 24 | 0xFFFFFF;
			Font font = Minecraft.getInstance().font;
			Matrix4f pose = labelPose(view, anchor);

			if (icon != null) {
				float half = InBetweenChoiceManager.ICON_SIZE / 2;
				float y = (float) -InBetweenChoiceManager.ICON_OFFSET;
				meshes.add(quad(icon, pose, -half, y - half, half, y + half, color));
			}
			meshes.add(centeredText(font, title, pose, -TITLE_Y, TITLE_FONT, color));
			if (heartTexture != null) {
				float halfWidth = heartTexture.width() * HEART_TEXTURE_PIXEL / 2;
				float halfHeight = heartTexture.height() * HEART_TEXTURE_PIXEL / 2;
				meshes.add(quad(heartTexture.texture(), pose, -halfWidth, -halfHeight, halfWidth, halfHeight, color));
			}
			else {
				meshes.add(centeredText(font, fallbackSubtitle, pose, 0, SUBTITLE_FONT, color));
			}
			return meshes;
		}
	}
}
