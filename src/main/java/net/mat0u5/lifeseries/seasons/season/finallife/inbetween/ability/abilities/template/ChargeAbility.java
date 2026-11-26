package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities.template;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.EffectRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import java.util.List;

public abstract class ChargeAbility extends Ability {
	public static final int MAX_CHARGES = 3;
	public static final int RECHARGE_TICKS = 8 * 20; // 8s

	//Uses 6 saturation if available, else only 3 hunger
	private static final float SATURATION_COST = 6;
	private static final float HUNGER_COST = 3;

	public static final int MIN_FOOD_LEVEL = 7;

	private int currentCharges = MAX_CHARGES;
	private int rechargeTicks;

	public ChargeAbility(Abilities ability) {
		super(ability);
	}

	@Override
	public void onStart() {
		super.onStart();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		sendChargesPacket(player);
	}

	@Override
	public void tick() {
		super.tick();
		if (currentCharges >= MAX_CHARGES) return; //Charges full
		rechargeTicks++;
		if (rechargeTicks < RECHARGE_TICKS) return;
		// Charges finished
		currentCharges = MAX_CHARGES;
		rechargeTicks = 0;
		ServerPlayer player = getPlayer();
		if (player != null) {
			sendChargesPacket(player);
		}
	}

	protected boolean useCharge(ServerPlayer player) {
		if (currentCharges <= 0 || !player.isAlive() || player.isSpectator() || EffectRegistry.hasStickyFeet(player)) {
			sendChargesPacket(player);
			return false;
		}
		if (!hasEnoughFood(player)) {
			ModifiableSound.FINALLIFE_ABILITY_NO_FOOD.play(player);
			sendChargesPacket(player);
			return false;
		}
		currentCharges--;
		rechargeTicks = 0;
		useFood(player);
		sendChargesPacket(player);
		return true;
	}

	public static boolean hasEnoughFood(Player player) {
		return player.getFoodData().getFoodLevel() >= MIN_FOOD_LEVEL || player.getAbilities().mayfly; // mimics being able to sprint
	}

	private static void useFood(ServerPlayer player) {
		if (player.getAbilities().invulnerable) return;
		FoodData food = player.getFoodData();
		float saturation = food.getSaturationLevel();
		float usedSaturation = Math.min(saturation, SATURATION_COST);
		int usedHunger = Mth.ceil((SATURATION_COST - usedSaturation) * HUNGER_COST / SATURATION_COST);
		food.setFoodLevel(Math.max(food.getFoodLevel() - usedHunger, 0));
		food.setSaturation(Math.min(saturation - usedSaturation, food.getFoodLevel()));
	}

	protected static void playSound(ServerPlayer player, ModifiableSound sound) {
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound.get(), SoundSource.PLAYERS, 1, 1);
	}

	public void sendChargesPacket(ServerPlayer player) {
		SimplePackets.ABILITY_CHARGES.sendToClient(List.of(ability().name(), String.valueOf(currentCharges), String.valueOf(rechargeTicks), String.valueOf(RECHARGE_TICKS)), player);
	}
}
