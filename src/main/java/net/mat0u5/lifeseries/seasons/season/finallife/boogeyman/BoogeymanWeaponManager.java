package net.mat0u5.lifeseries.seasons.season.finallife.boogeyman;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLife;
import net.mat0u5.lifeseries.seasons.season.finallife.FinalLifeConfig;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.config.entry.ConfigRef;
import net.mat0u5.matlib.utils.world.ItemStackUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

//? if <= 1.20.3 {
//import net.minecraft.world.entity.MobType;
//?}
//? if >= 1.21.2 {
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.Consumable;
//?}
//? if >= 1.21.5 {
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Weapon;
//?}

public class BoogeymanWeaponManager {
	public static final String BOOGEYMAN_WEAPON_NBT = "BoogeymanWeapon";
	public static final int CONSUME_DURATION = 100000;
	public static final float THROW_SPEED = 1.0F;
	public static final double SOUND_RANGE = 32;

	public static final ConfigRef<Boolean> WEAPONS_ENABLED = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_THROWABLE_WEAPONS);
	public static final ConfigRef<Double> WEAPON_RANGE = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_THROW_RANGE);
	public static final ConfigRef<Double> WEAPON_CHARGE_TIME = ConfigRef.of(() -> FinalLifeConfig.get().FINAL_LIFE_BOOGEYMAN_THROW_CHARGE_TIME);

	public static boolean isWeapon(ItemStack stack) {
		return stack != null && !stack.isEmpty() && (stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES));
	}

	public static boolean isAxe(ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.is(ItemTags.AXES);
	}

	public static boolean isBoogeymanWeapon(ItemStack stack) {
		return isWeapon(stack) && ItemStackUtils.hasCustomComponentEntry(stack, BOOGEYMAN_WEAPON_NBT);
	}

	public static boolean areWeaponsActive() {
		if (LifeSeries.modDisabled()) return false;
		if (!(currentSeason instanceof FinalLife finalLife)) return false;
		return finalLife.finalLifeBoogeymanManager.isEnabled() && WEAPONS_ENABLED.get();
	}

	public static boolean canThrowWeapon(ServerPlayer player) {
		if (player == null || !areWeaponsActive()) return false;
		FinalLife finalLife = (FinalLife) currentSeason;
		FinalLifeBoogeymanManager bm = finalLife.finalLifeBoogeymanManager;
		return bm.isBoogeyman(player) && !player.isSpectator();
	}

	public static boolean isBlacklistedFor(ServerPlayer player, ItemStack stack) {
		if (player == null || !isBoogeymanWeapon(stack) || !areWeaponsActive()) return false;
		FinalLife finalLife = (FinalLife) currentSeason;
		return !finalLife.finalLifeBoogeymanManager.canHaveBoogeymanWeapons(player);
	}

	private static final Map<UUID, ItemStack> currentCharging = new HashMap<>();
	private static final Set<UUID> playedChargedSound = new HashSet<>();

	private static void onFullyCharged(ServerPlayer player) {
		if (playedChargedSound.contains(player.getUUID())) return;
		ItemStack chargingStack = currentCharging.get(player.getUUID());
		if (chargingStack == null || !player.isUsingItem() || player.getUseItem() != chargingStack) return;
		if (player.getTicksUsingItem() < getChargeTicks()) return;
		playedChargedSound.add(player.getUUID());
		ModifiableSound.FINALLIFE_BOOGEYMAN_WEAPON_CHARGED.play(player);
	}

	public enum UseResult {
		PASS,
		ACCEPT,
		DENY
	}

	public static UseResult onUseItem(ItemStack stack, Level level, Player player) {
		if (LifeSeries.modDisabled() || !isBoogeymanWeapon(stack)) return UseResult.PASS;
		if (level.isClientSide()) return UseResult.ACCEPT;
		if (player instanceof ServerPlayer serverPlayer && canThrowWeapon(serverPlayer)) {
			currentCharging.put(serverPlayer.getUUID(), stack);
			playedChargedSound.remove(serverPlayer.getUUID());
			return UseResult.ACCEPT;
		}
		if (!areWeaponsActive() || (player instanceof ServerPlayer serverPlayer && isBlacklistedFor(serverPlayer, stack))) {
			removeBoogeymanWeaponNBT(stack);
		}
		return UseResult.DENY;
	}

	public static void onStopUseItem(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (level.isClientSide()) return;
		if (!(entity instanceof ServerPlayer player)) return;
		ItemStack chargingStack = currentCharging.remove(player.getUUID());
		if (!canThrowWeapon(player)) return;
		if (chargingStack != stack || player.getItemInHand(player.getUsedItemHand()) != stack) return;
		if (player.getTicksUsingItem() >= getChargeTicks()) {
			throwWeapon(player, stack, player.getUsedItemHand());
		}
	}

	public static int getChargeTicks() {
		if (!(currentSeason instanceof FinalLife)) return 0;
		return Math.max(0, (int) Math.round(WEAPON_CHARGE_TIME.get() * 20));
	}

	public static void throwWeapon(ServerPlayer player, ItemStack stack, InteractionHand hand) {
		if (stack == null || stack.isEmpty()) return;
		ServerLevel level = ((IPlayer)player).ls$getServerLevel();
		Vec3 speed = player.getLookAngle().normalize().scale(THROW_SPEED);
		ThrownBoogeymanWeapon.spawn(new ThrownBoogeymanWeapon(player, stack.copy(), hand, WEAPON_RANGE.get(), speed));
		player.setItemInHand(hand, ItemStack.EMPTY);
		ModifiableSound.FINALLIFE_BOOGEYMAN_WEAPON_THROW.playWithSource(getNearbyPlayers(level, player.position(), SOUND_RANGE), player, SoundSource.PLAYERS, 0.5F, 1.0F);
	}

	//? if >= 1.21.2 {
	private static final Consumable THROW_CONSUMABLE = Consumable.builder()
			.consumeSeconds(CONSUME_DURATION)
			//~ if >= 1.21.11 'SPEAR' -> 'TRIDENT' {
			.animation(ItemUseAnimation.TRIDENT)
			//~}
			.sound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY))
			.hasConsumeParticles(false)
			.build();
	//?}

	public static void onTick() {
		currentCharging.keySet().removeIf(uuid -> {
			ServerPlayer player = PlayerUtils.getPlayer(uuid);
			return player == null || !player.isUsingItem();
		});
		playedChargedSound.removeIf(uuid -> !currentCharging.containsKey(uuid));
		boolean removeWeapons = !areWeaponsActive();
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			if (removeWeapons) {
				removeBoogeymanWeaponNBTs(player);
				continue;
			}
			if (canThrowWeapon(player)) {
				Inventory inventory = player.getInventory();
				for (int i = 0; i < inventory.getContainerSize(); i++) {
					addBoogeymanWeaponNBT(inventory.getItem(i));
				}
				preventUseEnd(player);
				onFullyCharged(player);
			}
			else {
				Inventory inventory = player.getInventory();
				for (int i = 0; i < inventory.getContainerSize(); i++) {
					ItemStack stack = inventory.getItem(i);
					if (isBlacklistedFor(player, stack)) {
						removeBoogeymanWeaponNBT(stack);
					}
				}
			}
		}
	}

	public static void removeAllBoogeymanWeaponNBTs() {
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			removeBoogeymanWeaponNBTs(player);
		}
	}

	public static void removeBoogeymanWeaponNBTs(ServerPlayer player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			removeBoogeymanWeaponNBT(inventory.getItem(i));
		}
	}

	public static void addBoogeymanWeaponNBT(ItemStack stack) {
		if (!isWeapon(stack)) return;
		if (!ItemStackUtils.hasCustomComponentEntry(stack, BOOGEYMAN_WEAPON_NBT)) {
			ItemStackUtils.setCustomComponentBoolean(stack, BOOGEYMAN_WEAPON_NBT, true);
		}
		//? if >= 1.21.2 {
		if (!THROW_CONSUMABLE.equals(stack.get(DataComponents.CONSUMABLE))) {
			stack.set(DataComponents.CONSUMABLE, THROW_CONSUMABLE);
		}
		//?}
	}

	public static void removeBoogeymanWeaponNBT(ItemStack stack) {
		if (!isWeapon(stack)) return;
		if (ItemStackUtils.hasCustomComponentEntry(stack, BOOGEYMAN_WEAPON_NBT)) {
			ItemStackUtils.removeCustomComponentEntry(stack, BOOGEYMAN_WEAPON_NBT);
		}
		//? if >= 1.21.2 {
		if (stack.has(DataComponents.CONSUMABLE) && !stack.getPrototype().has(DataComponents.CONSUMABLE)) {
			stack.remove(DataComponents.CONSUMABLE);
		}
		//?}
	}

	private static void preventUseEnd(ServerPlayer player) {
		if (player.isUsingItem() && isBoogeymanWeapon(player.getUseItem()) && player.getUseItemRemainingTicks() <= 20) {
			player.stopUsingItem();
		}
	}

	public static float getAttackDamage(ItemStack stack, ServerLevel level, Entity target, DamageSource source) {
		double[] damage = {1.0};
		//? if <= 1.20.3 {
		/*for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
			if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
				damage[0] += modifier.getAmount();
			}
		}
		float result = (float) damage[0];
		MobType mobType = target instanceof LivingEntity livingTarget ? livingTarget.getMobType() : MobType.UNDEFINED;
		result += EnchantmentHelper.getDamageBonus(stack, mobType);
		*///?} else {
		stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
			if (attribute.value() == Attributes.ATTACK_DAMAGE.value() && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
				damage[0] += modifier.amount();
			}
		});
		float result = (float) damage[0];
		//? if <= 1.20.5 {
		//result += EnchantmentHelper.getDamageBonus(stack, target.getType());
		//?} else {
		result = EnchantmentHelper.modifyDamage(level, stack, target, source, result);
		//?}

		//?}
		return result;
	}

	public static boolean isBlockedByShield(LivingEntity target, DamageSource source, float damage) {
		//? if <= 1.21.4 {
		//return target.isDamageSourceBlocked(source);
		//?} else {
		if (damage <= 0.0F) return false;
		ItemStack blockingWith = target.getItemBlockingWith();
		if (blockingWith == null) return false;
		BlocksAttacks blocksAttacks = blockingWith.get(DataComponents.BLOCKS_ATTACKS);
		if (blocksAttacks == null) return false;
		//? if <= 1.21.11 {
		//if (blocksAttacks.bypassedBy().map(source::is).orElse(false)) return false;
		//?} else {
		if (blocksAttacks.bypassedBy().map(set -> set.contains(source.typeHolder())).orElse(false)) return false;
		//?}
		Vec3 sourcePos = source.getSourcePosition();
		double angle = Math.PI;
		if (sourcePos != null) {
			Vec3 viewVector = Vec3.directionFromRotation(0.0F, target.getYHeadRot());
			Vec3 toSource = sourcePos.subtract(target.position());
			toSource = new Vec3(toSource.x, 0.0, toSource.z).normalize();
			angle = Math.acos(toSource.dot(viewVector));
		}
		return blocksAttacks.resolveBlockedDamage(source, damage, angle) > 0.0F;
		//?}
	}

	public static void disableShield(ServerLevel level, LivingEntity target) {
		//? if <= 1.20.3 {
		//if (target instanceof Player player) player.disableShield(true);
		//?} else if <= 1.21 {
		//if (target instanceof Player player) player.disableShield();
		//?} else if <= 1.21.4 {
		//if (target instanceof Player player) player.disableShield(player.getUseItem());
		//?} else {
		ItemStack blockingWith = target.getItemBlockingWith();
		if (blockingWith == null) return;
		BlocksAttacks blocksAttacks = blockingWith.get(DataComponents.BLOCKS_ATTACKS);
		if (blocksAttacks != null) {
			blocksAttacks.disable(level, target, Weapon.AXE_DISABLES_BLOCKING_FOR_SECONDS, blockingWith);
		}
		//?}
	}

	public static List<ServerPlayer> getNearbyPlayers(Level level, Vec3 pos, double range) {
		List<ServerPlayer> result = new ArrayList<>();
		double rangeSuared = range * range;
		for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
			if (player.level() != level) continue;
			if (player.position().distanceToSqr(pos) > rangeSuared) continue;
			result.add(player);
		}
		return result;
	}
}
