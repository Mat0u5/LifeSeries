package net.mat0u5.lifeseries.registries;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

//? if > 1.20.3
import net.minecraft.core.Holder;

public class EffectRegistry {
	public static final float STICKY_FEET_SPEED_FACTOR = 0.4f; // Same as honey blocks
	public static final float STICKY_FEET_JUMP_FACTOR = 0.5f;

	//? if <= 1.20.3 {
	//public static final MobEffect STICKY_FEET = Registry.register(BuiltInRegistries.MOB_EFFECT, IdentifierHelper.lifeseries("sticky_feet"), new MobEffect(MobEffectCategory.HARMFUL, 0xA9656A) {});
	//?} else {
	public static final Holder<MobEffect> STICKY_FEET = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, IdentifierHelper.lifeseries("sticky_feet"), new MobEffect(MobEffectCategory.HARMFUL, 0xA9656A) {});
	//?}

	public static void registerEffects() {
	}

	public static boolean hasStickyFeet(LivingEntity entity) {
		return entity.hasEffect(STICKY_FEET);
	}

	public static void playHoneyEffects(LivingEntity entity) {
		if (entity.level().isClientSide() || !hasStickyFeet(entity)) return;
		entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1, 1);
		entity.level().broadcastEntityEvent(entity, (byte) 54);
	}
}
