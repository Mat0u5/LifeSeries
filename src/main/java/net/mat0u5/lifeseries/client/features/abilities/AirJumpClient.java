package net.mat0u5.lifeseries.client.features.abilities;

import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.EffectRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.minecraft.client.player.LocalPlayer;

public class AirJumpClient {
	private static boolean wasJumping = true;
	private static boolean wasOnGround = true;

	public static void tick(LocalPlayer player) {
		//? if <= 1.21 {
		//boolean jumping = player.input.jumping;
		//?} else {
		boolean jumping = player.input.keyPresses.jump();
		//?}
		boolean pressed = jumping && !wasJumping;
		wasJumping = jumping;
		boolean leftGround = wasOnGround;
		wasOnGround = player.onGround();
		if (!pressed || leftGround || !canAirJump(player) || !AbilityChargesClient.hasCharge(Abilities.AIR_JUMP)) return;
		SimplePackets.AIR_JUMP.sendToServer();
		if (!AbilityChargesClient.tryUse(Abilities.AIR_JUMP, player)) return;
		player.jumpFromGround();
	}

	private static boolean canAirJump(LocalPlayer player) {
		if (player.onGround() || player.isInWater() || player.isInLava() || player.onClimbable()) return false;
		if (player.isPassenger() || player.isFallFlying() || player.isSpectator() || player.getAbilities().mayfly) return false;
		if (EffectRegistry.hasStickyFeet(player)) return false;
		return true;
	}

	public static void reset() {
		wasJumping = true;
		wasOnGround = true;
	}
}
