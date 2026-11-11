package net.mat0u5.lifeseries.seasons.season.finallife.boogeyman;

import com.mojang.math.Transformation;
import net.mat0u5.lifeseries.config.modifiable.ModifiableSound;
import net.mat0u5.lifeseries.mixin.DisplayAccessor;
import net.mat0u5.lifeseries.mixin.ItemDisplayAccessor;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.matlib.utils.world.ItemStackUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//? if <= 26.1 {
//import net.minecraft.world.entity.EntityType;
//?} else {
import net.minecraft.world.entity.EntityTypes;
//?}
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
//? if <= 1.20.5 {
//import net.minecraft.world.item.enchantment.Enchantments;
//?}

public class ThrownBoogeymanWeapon {
	public static final String ITEM_DISPLAY_TAG = "lifeseries_boogeyman_weapon";
	public static final int MAX_AGE = 200;
	public static final double PICKUP_DISTANCE = 1.25;
	public static final double RETURN_DRAG = 0.8;
	public static final double RETURN_ACCELERATION = 0.2;
	public static final float HIT_DISTANCE = 0.3F;
	public static final float SPIN_DEGREES_PER_TICK = 50.0F;
	public static final int SOUND_INTERVAL = 4;

	private static final List<ThrownBoogeymanWeapon> currentThrownWeapons = new ArrayList<>();
	private static int timer = 0;

	private final ServerLevel level;
	private final UUID ownerUUID;
	private final InteractionHand hand;
	private final double maxRange;
	private final boolean isAxe;
	private final Display.ItemDisplay display;
	private ItemStack itemStack;
	private Vec3 position;
	private Vec3 velocity;
	private final Set<Integer> hitEntityIds = new HashSet<>();
	private Set<Integer> touchingEntityIds = new HashSet<>();
	private double distanceTraveled = 0;
	private boolean returning = false;
	private boolean discarded = false;
	private int age = 0;
	private float yRot;

	public ThrownBoogeymanWeapon(ServerPlayer owner, ItemStack itemStack, InteractionHand hand, double maxRange, Vec3 velocity) {
		this.level = ((IPlayer)owner).ls$getServerLevel();
		this.ownerUUID = owner.getUUID();
		this.hand = hand;
		this.maxRange = maxRange;
		this.itemStack = itemStack;
		this.isAxe = BoogeymanWeaponManager.isAxe(itemStack);
		this.position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
		this.velocity = velocity;
		this.yRot = getYRotFromVelocity(0);

		this.display = new Display.ItemDisplay(EntityTypes.ITEM_DISPLAY, level);
		((ItemDisplayAccessor) display).ls$setItemStack(itemStack.copy());
		DisplayAccessor displayAccessor = (DisplayAccessor) display;
		displayAccessor.ls$setTransformationInterpolationDuration(1);
		//? if > 1.20 {
		displayAccessor.ls$setPosRotInterpolationDuration(1);
		//?}
		displayAccessor.ls$setTransformation(createTransformation(0.0F));
		display.setPos(position.x, position.y, position.z);
		display.addTag(ITEM_DISPLAY_TAG);
	}

	public static void spawn(ThrownBoogeymanWeapon weapon) {
		weapon.level.addFreshEntity(weapon.display);
		currentThrownWeapons.add(weapon);
	}

	public static void onTick(MinecraftServer server) {
		for (ThrownBoogeymanWeapon weapon : new ArrayList<>(currentThrownWeapons)) {
			if (!weapon.discarded) {
				weapon.tick();
			}
			if (weapon.discarded) {
				currentThrownWeapons.remove(weapon);
			}
		}

		timer++;
		if (timer >= 100) {
			timer = 0;
			removeExtraDisplays(server);
		}
	}

	private void tick() {
		age++;
		ServerPlayer owner = getOwner();
		if (display.isRemoved()) {
			returnOrDrop(owner);
			return;
		}
		if (owner == null) {
			dropWeaponAsItem();
			return;
		}
		if (owner.level() != level || !owner.isAlive() || owner.isSpectator() || age > MAX_AGE ||
				owner.position().distanceToSqr(position) > (2*maxRange)*(2*maxRange)) {
			returnItemToOwner(owner);
			return;
		}

		if (returning) {
			Vec3 target = owner.position().add(0, owner.getBbHeight() * 0.6, 0);
			Vec3 offset = target.subtract(position);
			double distance = offset.length();
			if (distance <= Math.max(PICKUP_DISTANCE, velocity.length())) {
				returnItemToOwner(owner);
				return;
			}
			velocity = velocity.scale(RETURN_DRAG).add(offset.normalize().scale(RETURN_ACCELERATION));
		}

		checkHits(owner);
		if (discarded) return;

		position = position.add(velocity);
		yRot = getYRotFromVelocity(yRot);
		updateItemDisplay();

		if (age % SOUND_INTERVAL == 1) {
			float pitch = 0.9F + level.getRandom().nextFloat() * 0.3F;
			ModifiableSound.FINALLIFE_BOOGEYMAN_WEAPON_AIR.playWithSource(BoogeymanWeaponManager.getNearbyPlayers(level, position, BoogeymanWeaponManager.SOUND_RANGE), display, SoundSource.PLAYERS, 0.2F, pitch);
		}
	}

	private void checkHits(ServerPlayer owner) {
		Vec3 fromPos = position;
		Vec3 toPos = position.add(velocity);
		BlockHitResult blockHit = level.clip(new ClipContext(fromPos, toPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
		if (blockHit.getType() != HitResult.Type.MISS) {
			toPos = blockHit.getLocation();
		}

		Set<Integer> touching = new HashSet<>();
		for (Entity target : getEntitiesToHit(fromPos, toPos)) {
			int id = target.getId();
			touching.add(id);
			if (touchingEntityIds.contains(id)) continue;
			HitOutcome outcome = onHitEntity(owner, target);
			if (discarded) return;
			if (outcome == HitOutcome.HIT) {
				hitEntityIds.add(id);
			}
			if (outcome == HitOutcome.BLOCKED && !returning) {
				touchingEntityIds = touching;
				startReturning(-0.1);
				return;
			}
		}
		touchingEntityIds = touching;
		if (returning) return;

		if (blockHit.getType() != HitResult.Type.MISS) {
			//display.playSound(SoundEvents.TRIDENT_HIT_GROUND, 1.0F, 1.0F);
			startReturning(-0.2);
		}
		else {
			distanceTraveled += velocity.length();
			if (distanceTraveled >= maxRange) {
				startReturning(0.3);
			}
		}
	}

	private List<Entity> getEntitiesToHit(Vec3 from, Vec3 to) {
		AABB searchBox = new AABB(from, to).inflate(1.0);
		List<Entity> result = new ArrayList<>();
		Map<Entity, Double> hitDistances = new HashMap<>();
		for (Entity entity : level.getEntities(display, searchBox, this::canHitEntity)) {
			AABB hitBox = entity.getBoundingBox().inflate(HIT_DISTANCE);
			Vec3 hitPos = hitBox.contains(from) ? from : hitBox.clip(from, to).orElse(null);
			if (hitPos == null) continue;
			result.add(entity);
			hitDistances.put(entity, from.distanceToSqr(hitPos));
		}
		result.sort(Comparator.comparingDouble(hitDistances::get));
		return result;
	}

	private boolean canHitEntity(Entity entity) {
		if (entity instanceof Display) return false;
		if (ownerUUID.equals(entity.getUUID())) return false;
		if (hitEntityIds.contains(entity.getId())) return false;
		return !entity.isSpectator() && entity.canBeHitByProjectile();//entity.isAlive() && entity.isPickable()
	}

	private enum HitOutcome {
		HIT,
		BLOCKED,
		FAILED
	}

	private HitOutcome onHitEntity(ServerPlayer owner, Entity target) {
		boolean hurt = false;
		DamageSource source = createDamageSource(owner);
		float damage = BoogeymanWeaponManager.getAttackDamage(itemStack, level, target, source);
		boolean blockedByShield = target instanceof LivingEntity livingTarget && BoogeymanWeaponManager.isBlockedByShield(livingTarget, source, damage);
		boolean disableShield = isAxe && blockedByShield;

		// Temporarily put the weapon in the owner's hands, so that it is processed.
		Inventory inventory = owner.getInventory();

		//? if <= 1.21.4 {
		//int selectedSlot = inventory.selected;
		 //?} else {
		int selectedSlot = inventory.getSelectedSlot();
		//?}
		ItemStack heldItem = inventory.getItem(selectedSlot);
		ItemStack weapon = itemStack;
		inventory.setItem(selectedSlot, weapon);
		try {
			//? if <= 1.21 {
			//hurt = target.hurt(source, damage);
			//?} else {
			hurt = target.hurtServer(level, source, damage);
			//?}
			if (hurt && !blockedByShield) {
				applyEnchantmentEffects(owner, target, source, damage);
			}
		}
		finally {
			if (inventory.getItem(selectedSlot) == weapon) {
				inventory.setItem(selectedSlot, heldItem);
			}
			else {
				itemStack = ItemStack.EMPTY;
				if (!heldItem.isEmpty()) {
					ItemStackUtils.spawnItem(level, owner.position(), heldItem);
				}
				discardWeapon();
			}
		}

		if (disableShield) {
			BoogeymanWeaponManager.disableShield(level, (LivingEntity) target);
		}

		ModifiableSound.FINALLIFE_BOOGEYMAN_WEAPON_HIT.playWithSource(display, SoundSource.PLAYERS, 1.0F, 1.0F);
		if (blockedByShield) return HitOutcome.BLOCKED;
		return hurt ? HitOutcome.HIT : HitOutcome.FAILED;
	}

	private void applyEnchantmentEffects(ServerPlayer owner, Entity target, DamageSource source, float damage) {
		//? if <= 1.20.3 {
		/*int fireAspect = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, itemStack);
		if (fireAspect > 0) target.setSecondsOnFire(fireAspect * 4);
		*///?} else if <= 1.20.5 {
		/*int fireAspect = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, itemStack);
		if (fireAspect > 0) target.igniteForSeconds(fireAspect * 4);
		*///?}
		//? if <= 1.20.5 {
		/*if (target instanceof LivingEntity livingTarget) {
			EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
		}
		EnchantmentHelper.doPostDamageEffects(owner, target);
		float knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemStack);
		*///?} else {
		DamageSource directSource = level.damageSources().playerAttack(owner);
		EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, directSource, itemStack);
		float knockback = EnchantmentHelper.modifyKnockback(level, itemStack, target, directSource, 0.0F);
		//?}

		if (knockback > 0 && target instanceof LivingEntity livingTarget) {
			Vec3 direction = new Vec3(velocity.x, 0, velocity.z);
			if (direction.lengthSqr() < 1.0E-7) return;
			direction = direction.normalize();
			//? if <= 26.1 {
			//livingTarget.knockback(knockback * 0.5F, -direction.x, -direction.z);
			//?} else {
			livingTarget.knockback(knockback * 0.5F, -direction.x, -direction.z, source, damage);
			//?}
		}
	}

	private DamageSource createDamageSource(ServerPlayer owner) {
		//? if <= 1.21 {
		//Holder<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK);
		//?} else {
		Holder<DamageType> type = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.PLAYER_ATTACK);
		//?}
		return new DamageSource(type, display, owner);
	}

	private void startReturning(double velocityScale) {
		returning = true;
		velocity = velocity.scale(velocityScale);
	}

	private void updateItemDisplay() {
		display.setPos(position.x, position.y, position.z);
		DisplayAccessor displayAccessor = (DisplayAccessor) display;
		displayAccessor.ls$setTransformation(createTransformation(1.0F));
		displayAccessor.ls$setTransformationInterpolationDelay(0);
	}

	private Transformation createTransformation(float scale) {
		float xRot = (float) (Mth.atan2(velocity.y, velocity.horizontalDistance()) * (180.0F / Math.PI));
		float spin = age * SPIN_DEGREES_PER_TICK;
		Quaternionf rotation = new Quaternionf().rotateY(rad(yRot - 90.0F));
		if (isAxe) {
			// Vertical spin
			rotation.rotateZ(rad(-spin));
		}
		else {
			// Horizontal spin
			rotation.rotateZ(rad(xRot)).rotateY(rad(spin)).rotateX(rad(90.0F));
		}
		return new Transformation(new Vector3f(), rotation, new Vector3f(scale, scale, scale), new Quaternionf());
	}

	private float getYRotFromVelocity(float previous) {
		if (velocity.horizontalDistance() < 1.0E-4) return previous;
		return (float) (Mth.atan2(velocity.x, velocity.z) * (180.0F / Math.PI));
	}

	private static float rad(float degrees) {
		return degrees * ((float) Math.PI / 180.0F);
	}

	private ServerPlayer getOwner() {
		return PlayerUtils.getPlayer(ownerUUID);
	}

	private void returnItemToOwner(ServerPlayer owner) {
		if (!itemStack.isEmpty()) {
			ItemStack returned = itemStack.copy();
			itemStack = ItemStack.EMPTY;
			if (owner.getItemInHand(hand).isEmpty()) {
				owner.setItemInHand(hand, returned);
			}
			else {
				owner.getInventory().add(returned);
				if (!returned.isEmpty()) {
					ItemStackUtils.spawnItemForPlayer((ServerLevel) owner.level(), owner.position(), returned, owner);
				}
			}
		}
		discardWeapon();
	}

	private void dropWeaponAsItem() {
		if (!itemStack.isEmpty()) {
			ItemStackUtils.spawnItem(level, position, itemStack.copy());
			itemStack = ItemStack.EMPTY;
		}
		discardWeapon();
	}

	private void returnOrDrop(ServerPlayer owner) {
		if (owner != null) {
			returnItemToOwner(owner);
		}
		else {
			dropWeaponAsItem();
		}
	}

	private void discardWeapon() {
		discarded = true;
		if (!display.isRemoved()) {
			display.discard();
		}
	}

	public static void returnAllWeapons() {
		List<ThrownBoogeymanWeapon> weapons = new ArrayList<>(currentThrownWeapons);
		currentThrownWeapons.clear();
		for (ThrownBoogeymanWeapon weapon : weapons) {
			if (!weapon.discarded) {
				weapon.returnOrDrop(weapon.getOwner());
			}
		}
	}

	public static void returnWeapons(ServerPlayer player) {
		for (ThrownBoogeymanWeapon weapon : new ArrayList<>(currentThrownWeapons)) {
			if (!player.getUUID().equals(weapon.ownerUUID)) continue;
			currentThrownWeapons.remove(weapon);
			if (!weapon.discarded) {
				weapon.returnItemToOwner(player);
			}
		}
	}

	private static void removeExtraDisplays(MinecraftServer server) {
		List<Entity> strays = new ArrayList<>();
		for (ServerLevel serverLevel : server.getAllLevels()) {
			for (Entity entity : serverLevel.getAllEntities()) {
				if (!(entity instanceof Display.ItemDisplay)) continue;
				//? if <= 1.21.11 {
				//if (!entity.getTags().contains(ITEM_DISPLAY_TAG)) continue;
				//?} else {
				if (!entity.entityTags().contains(ITEM_DISPLAY_TAG)) continue;
				//?}
				if (isTracked(entity)) continue;
				strays.add(entity);
			}
		}
		strays.forEach(Entity::discard);
	}

	private static boolean isTracked(Entity display) {
		for (ThrownBoogeymanWeapon weapon : currentThrownWeapons) {
			if (weapon.display == display) return true;
		}
		return false;
	}
}
