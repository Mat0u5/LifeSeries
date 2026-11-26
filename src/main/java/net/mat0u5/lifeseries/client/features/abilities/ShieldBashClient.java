package net.mat0u5.lifeseries.client.features.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.ShieldBashAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ShieldBashClient {
	private static final int FRAME_COLOR = 0xFFB600; // Same as Second Wind
	private static final int CELLS = 4;
	private static final double OUTLINE_WIDTH = 3.25;
	private static final int GRID_ORIGIN = -2;
	private static final int GRID_SIZE = 20 * CELLS;
	private static final double[][] GUI_SHIELD_BORDER = {{9.5, 15.6}, {2.46, 14.09}, {3.59, 0.47}, {3.86, 0.32}, {10.9, 1.82}, {9.77, 15.45}};
	private static final double[][] LEFT_PATH = {GUI_SHIELD_BORDER[0], GUI_SHIELD_BORDER[1], GUI_SHIELD_BORDER[2], GUI_SHIELD_BORDER[3]};
	private static final double[][] RIGHT_PATH = {GUI_SHIELD_BORDER[0], GUI_SHIELD_BORDER[5], GUI_SHIELD_BORDER[4], GUI_SHIELD_BORDER[3]};
	private static final List<int[]> LEFT_FILL_ORDER = new ArrayList<>();
	private static final List<int[]> RIGHT_FILL_ORDER = new ArrayList<>();
	private static final float[][] cellAlpha = new float[GRID_SIZE][GRID_SIZE];
	static {
		calculateOutlines();
	}

	private static final float THRUST_ANIM_OUT = 0.05f; // Seconds
	private static final float THRUST_ANIM_HOLD = 0.4f;
	private static final float THRUST_ANIM_BACK = 0.15f;
	private static final float THRUST_ANIM_FORWARD = 0.35f;
	private static final float THRUST_ANIM_SIDEWAYS = 0.12f;
	private static final float THRUST_ANIM_UP = 0.04f;
	private static final long START = System.nanoTime();

	//All times are in nanos
	private static long cooldownStart = -1;
	private static long cooldownEnd = -1;
	private static long lastBash = -1;

	public static void onCooldownPayload(List<String> payload) {
		try {
			setCooldown(Integer.parseInt(payload.get(0)), Integer.parseInt(payload.get(1)));
		} catch (Exception ignored) {}
	}

	private static void setCooldown(int remainingTicks, int totalTicks) {
		if (remainingTicks <= 0 || totalTicks <= 0) {
			cooldownStart = -1;
			cooldownEnd = -1;
			return;
		}
		long now = timeSinceStart();
		cooldownStart = now - (totalTicks - remainingTicks) * 50_000_000L;
		cooldownEnd = now + remainingTicks * 50_000_000L;
	}

	private static boolean isReady() {
		return cooldownEnd < 0 || timeSinceStart() >= cooldownEnd;
	}

	private static boolean hasAbility() {
		return !LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.FINAL_LIFE) && AbilitiesClient.isActive(Abilities.SHIELD_BASH);
	}

	public static void onKeyPress() {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null || !player.isUsingItem() || !player.isBlocking() || !hasAbility() || !isReady()) return;
		if (!client.options.keyAttack.consumeClick()) return;
		SimplePackets.SHIELD_BASH.sendToServer();
		lastBash = timeSinceStart();
		setCooldown(ShieldBashAbility.COOLDOWN, ShieldBashAbility.COOLDOWN);
	}

	public static void renderShieldBorder(GuiGraphicsExtractor graphics, int x, int y, Player player, ItemStack stack) {
		if (player != Minecraft.getInstance().player || !stack.is(Items.SHIELD) || !hasAbility()) return;
		if (stack != player.getMainHandItem() && stack != player.getOffhandItem()) return;
		float progress = cooldownProgress();
		//? if <= 1.21.5 {
		/*graphics.pose().pushPose();
		graphics.pose().translate(x, y, 300); // Above the item and its durability bar
		graphics.pose().scale(1f / CELLS, 1f / CELLS, 1);
		*///?} else {
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(1f / CELLS, 1f / CELLS);
		//?}
		drawOutline(graphics, progress);
		//? if <= 1.21.5 {
		//graphics.pose().popPose();
		//?} else {
		graphics.pose().popMatrix();
		//?}
	}

	private static void drawOutline(GuiGraphicsExtractor graphics, float progress) {
		for (float[] row : cellAlpha) Arrays.fill(row, 0);
		for (List<int[]> cells : List.of(LEFT_FILL_ORDER, RIGHT_FILL_ORDER)) {
			for (int i = 0; i < cells.size(); i++) {
				float alpha = Mth.clamp(progress * cells.size() - i, 0, 1);
				if (alpha <= 0) break;
				cellAlpha[cells.get(i)[1]][cells.get(i)[0]] = alpha;
			}
		}
		int offset = GRID_ORIGIN * CELLS;
		for (int y = 0; y < GRID_SIZE; y++) {
			int x = 0;
			while (x < GRID_SIZE) {
				float alpha = cellAlpha[y][x];
				int end = x + 1;
				while (alpha > 0 && end < GRID_SIZE && cellAlpha[y][end] == alpha) end++;
				if (alpha > 0) {
					graphics.fill(x + offset, y + offset, end + offset, y + 1 + offset, (Math.round(alpha * 255) << 24) | FRAME_COLOR);
				}
				x = end;
			}
		}
	}

	private static void calculateOutlines() {
		boolean[][] inside = new boolean[GRID_SIZE][GRID_SIZE];
		for (int y = 0; y < GRID_SIZE; y++) {
			for (int x = 0; x < GRID_SIZE; x++) {
				int covered = 0;
				for (int dy = 0; dy < 4; dy++) {
					for (int dx = 0; dx < 4; dx++) {
						if (insideBorder(guiCoords(x + (dx + 0.5) / 4), guiCoords(y + (dy + 0.5) / 4))) {
							covered++;
						}
					}
				}
				inside[y][x] = covered >= 8;
			}
		}
		List<double[]> left = new ArrayList<>();
		List<double[]> right = new ArrayList<>();
		int reach = (int) Math.ceil(OUTLINE_WIDTH);
		for (int y = 0; y < GRID_SIZE; y++) {
			for (int x = 0; x < GRID_SIZE; x++) {
				if (inside[y][x] || !nearBorder(inside, x, y, reach)) continue;
				double guiX = guiCoords(x + 0.5);
				double guiY = guiCoords(y + 0.5);
				double[] onLeft = pathPosition(LEFT_PATH, guiX, guiY);
				double[] onRight = pathPosition(RIGHT_PATH, guiX, guiY);
				if (onLeft[0] <= onRight[0]) {
					left.add(new double[]{onLeft[1], x, y});
				}
				else {
					right.add(new double[]{onRight[1], x, y});
				}
			}
		}
		for (List<double[]> cells : List.of(left, right)) {
			cells.sort(Comparator.comparingDouble(cell -> cell[0]));
			List<int[]> order = cells == left ? LEFT_FILL_ORDER : RIGHT_FILL_ORDER;
			for (double[] cell : cells) {
				order.add(new int[]{(int) cell[1], (int) cell[2]});
			}
		}
	}

	private static double guiCoords(double cell) {
		return GRID_ORIGIN + cell / CELLS;
	}

	private static boolean nearBorder(boolean[][] inside, int x, int y, int reach) {
		for (int dy = -reach; dy <= reach; dy++) {
			for (int dx = -reach; dx <= reach; dx++) {
				int newX = x + dx;
				int newY = y + dy;
				if (newX < 0 || newY < 0 || newX >= GRID_SIZE || newY >= GRID_SIZE || !inside[newY][newX]) {
					continue;
				}
				if (dx * dx + dy * dy <= OUTLINE_WIDTH * OUTLINE_WIDTH) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean insideBorder(double x, double y) {
		boolean positive = false;
		boolean negative = false;
		for (int i = 0; i < GUI_SHIELD_BORDER.length; i++) {
			double[] start = GUI_SHIELD_BORDER[i];
			double[] end = GUI_SHIELD_BORDER[(i + 1) % GUI_SHIELD_BORDER.length];
			double cross = (end[0] - start[0]) * (y - start[1]) - (end[1] - start[1]) * (x - start[0]);
			if (cross > 0) positive = true;
			if (cross < 0) negative = true;
		}
		return !(positive && negative);
	}

	private static double[] pathPosition(double[][] path, double x, double y) {
		double totalLength = 0;
		for (int i = 0; i + 1 < path.length; i++) {
			totalLength += Math.hypot(path[i + 1][0] - path[i][0], path[i + 1][1] - path[i][1]);
		}
		double bestDistance = Double.MAX_VALUE;
		double bestPosition = 0;
		double travelledLength = 0;
		for (int i = 0; i + 1 < path.length; i++) {
			double[] start = path[i];
			double[] end = path[i + 1];
			double dx = end[0] - start[0];
			double dy = end[1] - start[1];
			double segmentLegnth = Math.hypot(dx, dy);
			double segments = Mth.clamp(((x - start[0]) * dx + (y - start[1]) * dy) / (segmentLegnth * segmentLegnth), 0, 1);
			double distance = Math.hypot(start[0] + segments * dx - x, start[1] + segments * dy - y);
			if (distance < bestDistance) {
				bestDistance = distance;
				bestPosition = (travelledLength + segments * segmentLegnth) / totalLength;
			}
			travelledLength += segmentLegnth;
		}
		return new double[]{bestDistance, bestPosition};
	}

	private static float cooldownProgress() {
		long now = timeSinceStart();
		if (cooldownEnd < 0 || now >= cooldownEnd) return 1;
		return Mth.clamp((float) (now - cooldownStart) / (cooldownEnd - cooldownStart), 0, 1);
	}

	public static void applyShieldThrust(PoseStack poseStack, InteractionHand hand) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (lastBash < 0 || player == null || !player.isUsingItem() || player.getUsedItemHand() != hand) return;
		float seconds = (timeSinceStart() - lastBash) / 1.0E9f;
		float thrust;
		if (seconds < THRUST_ANIM_OUT) {
			thrust = smoothstepInterpolation(seconds / THRUST_ANIM_OUT);
		}
		else if (seconds < THRUST_ANIM_OUT + THRUST_ANIM_HOLD) {
			thrust = 1;
		}
		else {
			thrust = 1 - smoothstepInterpolation((seconds - THRUST_ANIM_OUT - THRUST_ANIM_HOLD) / THRUST_ANIM_BACK);
		}
		if (thrust <= 0) {
			return;
		}
		HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
		float towardsMiddle = arm == HumanoidArm.RIGHT ? -1 : 1;
		poseStack.translate(towardsMiddle * THRUST_ANIM_SIDEWAYS * thrust, THRUST_ANIM_UP * thrust, -THRUST_ANIM_FORWARD * thrust);
	}

	private static float smoothstepInterpolation(float x) {
		x = Mth.clamp(x, 0, 1);
		return x * x * (3 - 2*x);
	}

	public static void reset() {
		cooldownStart = -1;
		cooldownEnd = -1;
		lastBash = -1;
	}

	private static long timeSinceStart() {
		return System.nanoTime() - START;
	}
}
