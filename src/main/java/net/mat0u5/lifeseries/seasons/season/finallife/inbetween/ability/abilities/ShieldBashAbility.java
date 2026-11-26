package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.abilities;

import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.registries.EffectRegistry;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Abilities;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.ability.Ability;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;

import java.util.List;

public class ShieldBashAbility extends Ability {
	public static final ConfigRef<Integer> HEALTH = ConfigRef.of(() -> FinalLifeConfig.get().ABILITY_SHIELD_BASH_HEALTH);
	public static final int COOLDOWN = 10 * 20; // 10s
	private static final int STICKY_FEET_DURATION = 6 * 20; // 6s
	private static final double BASH_FORWARD_OFFSET = 1;
	private static final double PUSH_RADIUS = 2.5;
	private static final double PUSH_POWER = 1.2;
	private static final double EXPLOSION_SIZE = 1.5;
	private static final float DAMAGE = 2;

	private int currentCooldown;

	public ShieldBashAbility() {
		super(Abilities.SHIELD_BASH);
	}

	@Override
	protected int loadHealthChange() {
		return HEALTH.get();
	}

	@Override
	public void onStart() {
		super.onStart();
		ServerPlayer player = getPlayer();
		if (player == null) return;
		sendCooldownPacket(player);
	}

	@Override
	public void tick() {
		super.tick();
		if (currentCooldown > 0 && --currentCooldown == 0) {
			ServerPlayer player = getPlayer();
			if (player != null) {
				sendCooldownPacket(player);
			}
		}
	}

	// Triggered via packet sent from client
	public void bash(ServerPlayer player) {
		if (currentCooldown > 0 || !player.isAlive() || player.isSpectator() || !player.isBlocking()) {
			sendCooldownPacket(player);
			return;
		}
		currentCooldown = COOLDOWN;
		sendCooldownPacket(player);

		ServerLevel level = (ServerLevel) player.level();
		Vec3 look = player.getLookAngle().multiply(1, 0, 1).normalize(); // Remove upwards/downwards look
		Vec3 center = player.position().add(look.x * BASH_FORWARD_OFFSET, 1, look.z * BASH_FORWARD_OFFSET);

		level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, EXPLOSION_SIZE, 0, 0, 1);
		level.playSound(null, center.x, center.y, center.z, ModifiableSound.FINALLIFE_SHIELD_BASH.get(), SoundSource.PLAYERS, 1, 1.2f);

		AABB area = new AABB(center.x - PUSH_RADIUS, center.y - PUSH_RADIUS, center.z - PUSH_RADIUS, center.x + PUSH_RADIUS, center.y + PUSH_RADIUS, center.z + PUSH_RADIUS);
		List<LivingEntity> pushedEntities = level.getEntitiesOfClass(LivingEntity.class, area, other -> other != player && other.isAlive() && !other.isSpectator() && other.distanceToSqr(center) <= PUSH_RADIUS * PUSH_RADIUS);
		DamageSource damageSource = level.damageSources().explosion(player, player);
		for (LivingEntity other : pushedEntities) {
			//? if <= 1.21 {
			//other.hurt(damageSource, DAMAGE);
			//?} else {
			other.hurtServer(level, damageSource, DAMAGE);
			//?}
			push(player, other);
			other.addEffect(new MobEffectInstance(EffectRegistry.STICKY_FEET, STICKY_FEET_DURATION, 0, true, true, true), player);
		}
	}

	public void push(ServerPlayer player, LivingEntity target) {
		double pushPower = PUSH_POWER * (1-target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
		if (pushPower <= 0) return;
		Vec3 away = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ());
		if (away.lengthSqr() < 1.0E-4) away = player.getLookAngle().multiply(1, 0, 1);
		away = away.normalize().scale(pushPower);
		Vec3 movement = target.getDeltaMovement();
		target.setDeltaMovement(movement.x / 2 + away.x, target.onGround() ? Math.min(0.4, movement.y / 2 + pushPower) : movement.y, movement.z / 2 + away.z);
		//? if <= 26.2 {
		//target.hurtMarked = true;
		//?} else {
		target.syncVelocity = true;
		//?}
	}

	public void sendCooldownPacket(ServerPlayer player) {
		SimplePackets.SHIELD_BASH_COOLDOWN.sendToClient(List.of(String.valueOf(currentCooldown), String.valueOf(COOLDOWN)), player);
	}
}
