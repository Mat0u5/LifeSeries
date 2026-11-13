package net.mat0u5.lifeseries.seasons.season.finallife.inbetween;

import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.choice.InBetweenChoiceManager;
import net.mat0u5.lifeseries.seasons.season.finallife.inbetween.particle.InBetweenParticles;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.seasons.season.finallife.inbetween.InBetweenBuilder.Slice;


public class InBetweenInstance {

	public final int inBetweenSlot;
	public final UUID playerUUID;
	public final int deathNumber;
	public boolean finished = false;
	public int age = 0;
	@Nullable
	private Vec3 lastSafePos;
	public ServerLevel level;
	public final InBetweenParticles particleManager;
	public final InBetweenChoiceManager choiceManager;
	public final InBetweenBuilder inBetweenBuilder;


	private InBetweenInstance(int inBetweenSlot, ServerPlayer player, ServerLevel level, int deathNumber, List<Slice> slices) {
		this.particleManager = new InBetweenParticles(this);
		this.choiceManager = new InBetweenChoiceManager(player, this);
		this.inBetweenBuilder = new InBetweenBuilder(this, slices, inBetweenSlot, level);
		this.inBetweenSlot = inBetweenSlot;
		this.playerUUID = player.getUUID();
		this.deathNumber = deathNumber;
		this.level = level;
	}

	@Nullable
	public static InBetweenInstance create(int slot, ServerPlayer player, ServerLevel level, int deathNumber) {
		List<Slice> slices = InBetweenBuilder.loadStructureSlices(level, InBetweenBuilder.STRUCTURE_PREFIX);
		if (slices.isEmpty()) slices = InBetweenBuilder.loadStructureSlices(level, InBetweenBuilder.DEFAULT_STRUCTURE_PREFIX);
		if (slices.isEmpty()) return null;
		return new InBetweenInstance(slot, player, level, deathNumber, slices);
	}

	public void destroy() {
		choiceManager.destroy();
		inBetweenBuilder.removeEntities();
		inBetweenBuilder.clearRegion();
	}

	public Vec3 getSpawnPos() {
		return inBetweenBuilder.spawnPos;
	}

	public float getSpawnYaw() {
		return inBetweenBuilder.spawnYaw;
	}

	public List<ItemStack> collectDroppedItems() {
		List<ItemStack> result = new ArrayList<>();
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, inBetweenBuilder.getRegion())) {
			if (!item.getItem().isEmpty()) result.add(item.getItem().copy());
			item.discard();
		}
		return result;
	}

	public void tick(ServerPlayer player) {
		if (finished) return;
		age++;
		forceInBounds(player);
		particleManager.tick(player);
		choiceManager.tick(player, age);
	}

	public void forceInBounds(ServerPlayer player) {
		if (player.isCreative() || player.isSpectator()) return;
		Vec3 pos = player.position();
		if (!inBetweenBuilder.isInsideBounds(pos)) {
			if (player.isFallFlying()) player.stopFallFlying();
			player.setDeltaMovement(Vec3.ZERO);
			player.resetFallDistance();
			Vec3 target = lastSafePos != null && inBetweenBuilder.isInsideBounds(lastSafePos) ? lastSafePos : inBetweenBuilder.spawnPos;
			LevelUtils.teleport(player, level, target, player.getYRot(), player.getXRot());
			return;
		}
		if (player.onGround()) {
			lastSafePos = pos;
		}
	}
}
