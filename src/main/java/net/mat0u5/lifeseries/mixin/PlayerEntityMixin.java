package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.client.ClientUtils;
import net.mat0u5.lifeseries.dependencies.CardinalComponentsDependency;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.series.doublelife.DoubleLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = PlayerEntity.class, priority = 1)
public abstract class PlayerEntityMixin {

    @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
    //? if <=1.21 {
    private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
     //?} else
    /*private void onApplyDamage(ServerWorld world, DamageSource source, float amount, CallbackInfo ci) {*/
        if (!Main.isLogicalSide()) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            currentSeries.onPlayerDamage(serverPlayer, source, amount, ci);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    private void onPreDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*private void onPreDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (!Main.isLogicalSide()) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            currentSeries.onPrePlayerDamage(serverPlayer, source, amount, cir);
        }
    }

    @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide()) return;
        if (currentSeries instanceof DoubleLife doubleLife)  {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                doubleLife.canFoodHeal(serverPlayer, cir);
            }
        }
    }


    @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (DependencyManager.cardinalComponentsLoaded()) {
            CardinalComponentsDependency.getBaseDimensions(player, pose, cir);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateHitbox(CallbackInfo ci) {
        ((PlayerEntity) (Object) this).calculateDimensions();
    }

    @Unique
    private static final ReplaceDiskEnchantmentEffect frostWalker =  new ReplaceDiskEnchantmentEffect(EnchantmentLevelBasedValue.constant(5.0F), EnchantmentLevelBasedValue.constant(1.0F), new Vec3i(0, -1, 0), Optional.of(BlockPredicate.allOf(BlockPredicate.matchingBlockTag(new Vec3i(0, 1, 0), BlockTags.AIR), BlockPredicate.matchingBlocks(Blocks.WATER), BlockPredicate.matchingFluids(Fluids.WATER), BlockPredicate.unobstructed())), BlockStateProvider.of(Blocks.FROSTED_ICE), Optional.of(GameEvent.BLOCK_PLACE));
    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof ServerPlayerEntity player)) return;
        if (!player.isOnGround()) return;
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPERSPEED)) {
            frostWalker.apply(PlayerUtils.getServerWorld(player), 5, null, player, player.getPos());
        }
    }

    //? if >= 1.21.2 {
    /*@Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isClient()) return;
        if (ClientUtils.shouldPreventGliding()) {
            cir.setReturnValue(false);
        }
    }
    *///?}
}
