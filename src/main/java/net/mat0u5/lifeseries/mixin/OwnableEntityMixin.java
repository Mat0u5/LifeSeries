package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.seasons.subin.SubInManager;
import net.minecraft.world.entity.OwnableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

//? if > 1.21.4 {
import net.minecraft.world.entity.EntityReference;
import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
//?}

@Mixin(value = OwnableEntity.class, priority = 1)
public interface OwnableEntityMixin {

//? if !forge || >= 1.21 {
//? if <= 1.21.4 {
	/*@ModifyArg(method = "getOwner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/EntityGetter;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/world/entity/player/Player;"), index = 0)
	private @Nullable UUID subinOwner(UUID uuid) {
		if (uuid == null) return uuid;
		return SubInManager.subOrSubout(uuid);
	}
*///?} else {
	//? if <= 1.21.6 {
	/*@ModifyArg(method = "getOwner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityReference;get(Lnet/minecraft/world/entity/EntityReference;Lnet/minecraft/world/level/entity/UUIDLookup;Ljava/lang/Class;)Lnet/minecraft/world/level/entity/UniquelyIdentifyable;"), index = 0)
	*///?} else {
	@ModifyArg(method = "getOwner", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityReference;getLivingEntity(Lnet/minecraft/world/entity/EntityReference;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/LivingEntity;"), index = 0)
	//?}
	private @Nullable EntityReference<LivingEntity> subinOwner(@Nullable EntityReference<LivingEntity> original) {
		if (original == null) return original;
		UUID uuid = original.getUUID();
		UUID afterSub = SubInManager.subOrSubout(uuid);
		if (!Objects.equals(uuid, afterSub)) {
			//? if <= 1.21.6 {
			/*return new EntityReference<>(afterSub);
			*///?} else {
			return EntityReference.of(afterSub);
			//?}
		}
		return original;
	}
//?}
//?}
}
