package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = Player.class, priority = 1)
public abstract class PlayerMixin {

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    //? if <=1.21 {
    private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
     //?} else
    /*private void onApplyDamage(ServerLevel world, DamageSource source, float amount, CallbackInfo ci) {*/
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        Player player = (Player) (Object) this;
        if (WatcherManager.isWatcher(player)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            currentSeason.onPlayerDamage(serverPlayer, source, amount, ci);
        }
    }

    //? if <= 1.21 {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onPreDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void onPreDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        Player player = (Player) (Object) this;
        if (WatcherManager.isWatcher(player)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            currentSeason.onPrePlayerDamage(serverPlayer, source, amount, cir);
        }
    }

    @Inject(method = "isHurt", at = @At("HEAD"), cancellable = true)
    private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (currentSeason instanceof DoubleLife doubleLife)  {
            Player player = (Player) (Object) this;
            if (WatcherManager.isWatcher(player)) return;

            if (player instanceof ServerPlayer serverPlayer) {
                doubleLife.canFoodHeal(serverPlayer, cir);
            }
        }
    }

    //? if <= 1.21.6 {
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (Main.modFullyDisabled()) return;
        Player player = (Player) (Object) this;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (!morphComponent.isMorphed()) return;

        float scaleRatio = 1 / player.getScale();
        LivingEntity dummy = morphComponent.getDummy();
        if (morphComponent.isMorphed() && dummy != null) {
            cir.setReturnValue(dummy.getDimensions(pose).scale(scaleRatio, scaleRatio));
        }
    }
    //?}

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateHitbox(CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        ((Player) (Object) this).refreshDimensions();
    }

    @Unique
    private static final ReplaceDisk ls$frostWalker =  new ReplaceDisk(LevelBasedValue.constant(5.0F), LevelBasedValue.constant(1.0F), new Vec3i(0, -1, 0), Optional.of(BlockPredicate.allOf(BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR), BlockPredicate.matchesBlocks(Blocks.WATER), BlockPredicate.matchesFluids(Fluids.WATER), BlockPredicate.unobstructed())), BlockStateProvider.simple(Blocks.FROSTED_ICE), Optional.of(GameEvent.BLOCK_PLACE));

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3 movementInput, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof ServerPlayer player) || Main.modDisabled()) return;
        if (!player.onGround()) return;
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPERSPEED)) return;

        ls$frostWalker.apply(PlayerUtils.getServerWorld(player), 5, null, player, player.position());
    }
}
