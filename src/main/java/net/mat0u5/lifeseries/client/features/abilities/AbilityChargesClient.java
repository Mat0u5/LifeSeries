package net.mat0u5.lifeseries.client.features.abilities;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template.ChargeAbility;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//? if >= 1.21.2 && <= 1.21.5
//import net.minecraft.client.renderer.rendertype.RenderType;
//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines;

public class AbilityChargesClient {
	private static final List<Abilities> CHARGE_ABILITIES = List.of(Abilities.AIR_JUMP, Abilities.DASH);
	private static final int CHARGE_LEVELS = 6;
	private static final int ICON_SIZE = 6;
	private static final int ICON_SPACING = 7;
	private static final int COLUMN_SPACING = 8;
	private static final int HOTBAR_GAP = 6; // Same as the vanilla attack indicator
	private static final int OFFHAND_SLOT_WIDTH = 29;
	private static final Map<Abilities, Charges> charges = new EnumMap<>(Abilities.class);

	private static class Charges {
		private final Identifier[] textures = new Identifier[CHARGE_LEVELS + 1];
		private int currentCharges = ChargeAbility.MAX_CHARGES;
		//All times are in nanos
		private long rechargeStart = -1;
		private long rechargeEnd = -1;

		private Charges(Abilities ability) {
			for (int i = 0; i <= CHARGE_LEVELS; i++) {
				textures[i] = IdentifierHelper.lifeseries("textures/gui/ability_charges/" + ability.name().toLowerCase(Locale.ROOT) + "/charge_" + i + ".png");
			}
		}

		private void set(int currentCharges, int rechargedTicks, int totalTicks) {
			this.currentCharges = currentCharges;
			if (currentCharges >= ChargeAbility.MAX_CHARGES || totalTicks <= 0) {
				rechargeStart = -1;
				rechargeEnd = -1;
				return;
			}
			long now = System.nanoTime();
			rechargeStart = now - rechargedTicks * 50_000_000L;
			rechargeEnd = now + (totalTicks - rechargedTicks) * 50_000_000L;
		}

		private void update() {
			if (currentCharges < ChargeAbility.MAX_CHARGES && rechargeEnd >= 0 && System.nanoTime() >= rechargeEnd) {
				set(ChargeAbility.MAX_CHARGES, 0, 0);
			}
		}

		private int rechargeLevel() {
			if (rechargeEnd < 0) {
				return CHARGE_LEVELS;
			}
			float progress = (float) (System.nanoTime() - rechargeStart) / (rechargeEnd - rechargeStart);
			return Mth.clamp((int) (progress * CHARGE_LEVELS), 0, CHARGE_LEVELS - 1);
		}
	}

	private static Charges get(Abilities ability) {
		return charges.computeIfAbsent(ability, Charges::new);
	}

	public static void onChargesPayload(List<String> payload) {
		try {
			get(Abilities.valueOf(payload.get(0))).set(Integer.parseInt(payload.get(1)), Integer.parseInt(payload.get(2)), Integer.parseInt(payload.get(3)));
		} catch (Exception ignored) {}
	}

	public static boolean hasAbility(Abilities ability) {
		return !LifeSeries.modDisabled() && LifeSeries.isSeason(Seasons.FINAL_LIFE) && AbilitiesClient.isActive(ability);
	}

	public static boolean hasCharge(Abilities ability) {
		if (!hasAbility(ability)) return false;
		Charges abilityCharges = get(ability);
		abilityCharges.update();
		return abilityCharges.currentCharges > 0;
	}

	public static boolean tryUse(Abilities ability, Player player) {
		if (!hasCharge(ability) || !ChargeAbility.hasEnoughFood(player)) return false;
		Charges abilityCharges = get(ability);
		abilityCharges.set(abilityCharges.currentCharges - 1, 0, ChargeAbility.RECHARGE_TICKS);
		return true;
	}

	public static void renderCharges(GuiGraphicsExtractor graphics) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		int x = graphics.guiWidth() / 2 + 91 + HOTBAR_GAP;
		if (player.getMainArm() == HumanoidArm.LEFT && !player.getOffhandItem().isEmpty()) {
			x += OFFHAND_SLOT_WIDTH;
		}
		int y = graphics.guiHeight() - 22;
		for (Abilities ability : CHARGE_ABILITIES) {
			if (!hasAbility(ability)) continue;
			Charges abilityCharges = get(ability);
			abilityCharges.update();
			int rechargeLevel = abilityCharges.rechargeLevel();
			for (int i = 0; i < ChargeAbility.MAX_CHARGES; i++) {
				int level = i >= ChargeAbility.MAX_CHARGES - abilityCharges.currentCharges ? CHARGE_LEVELS : rechargeLevel;
				drawIcon(graphics, abilityCharges.textures[level], x, y + i * ICON_SPACING);
			}
			x += COLUMN_SPACING;
		}
	}

	private static void drawIcon(GuiGraphicsExtractor graphics, Identifier texture, int x, int y) {
		//? if <= 1.21 {
		//graphics.blit(texture, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
		//?} else if <= 1.21.5 {
		//graphics.blit(RenderType::guiTextured, texture, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
		//?} else {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
		//?}
	}

	public static void reset() {
		charges.clear();
	}
}
