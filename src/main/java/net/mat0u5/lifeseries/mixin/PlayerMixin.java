package net.mat0u5.lifeseries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.interfaces.IPlayer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.NicknameManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.mat0u5.lifeseries.Main.currentSeason;

//? if >= 1.21.2
import net.minecraft.server.level.ServerLevel;

//? if <= 1.20.5 {
/*import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
*///?} else {
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import java.util.Optional;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
//?}

//?if <= 1.21.9 {
/*import net.minecraft.world.level.GameRules;
*///?} else {
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
//?}
//?if <= 1.21.6 {
/*import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphComponent;
import net.mat0u5.lifeseries.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
*///?}
//?if >= 26.1 {
/*import net.minecraft.world.entity.Entity;
*///?}

@Mixin(value = Player.class, priority = 1)
public abstract class PlayerMixin implements IPlayer {

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    //? if <=1.21 {
    /*private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
     *///?} else
    private void onApplyDamage(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        Player player = (Player) (Object) this;
        if (WatcherManager.isWatcher(player)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            currentSeason.onPlayerDamage(serverPlayer, source, amount, ci);
        }
    }

    //? if <= 1.21 {
    /*@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onPreDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?} else {
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void onPreDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //?}
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
    //? if <= 1.20.3 {
    /*@Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(Pose pose, EntityDimensions entityDimensions, CallbackInfoReturnable<Float> cir) {
        if (Main.modFullyDisabled()) return;
        Player player = (Player) (Object) this;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (!morphComponent.isMorphed()) return;

        float scaleRatio = 1 / player.getScale();
        LivingEntity dummy = morphComponent.getDummy();
        if (morphComponent.isMorphed() && dummy != null) {
            cir.setReturnValue(dummy.getEyeHeight(pose) * scaleRatio);
        }
    }
    *///?}

    //? if <= 1.21.6 {
    /*//? if <= 1.20.3 {
    /^@Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    ^///?} else {
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    //?}
    public void getBaseDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (Main.modFullyDisabled()) return;
        Player player = (Player) (Object) this;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (!morphComponent.isMorphed()) return;

        EntityType<?> morphType = morphComponent.getType();
        if (morphType != null) {
            float scaleRatio = 1 / player.getScale();
            EntityDimensions morphDimensions = morphType.getDimensions();
            cir.setReturnValue(morphDimensions.scale(scaleRatio, scaleRatio));
        }
    }
    *///?}

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateHitbox(CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        ((Player) (Object) this).refreshDimensions();
    }

    //? if > 1.20.5 {
    @Unique
    private static final ReplaceDisk ls$frostWalker =  new ReplaceDisk(LevelBasedValue.constant(5.0F), LevelBasedValue.constant(1.0F), new Vec3i(0, -1, 0), Optional.of(BlockPredicate.allOf(BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR), BlockPredicate.matchesBlocks(Blocks.WATER), BlockPredicate.matchesFluids(Fluids.WATER), BlockPredicate.unobstructed())), BlockStateProvider.simple(Blocks.FROSTED_ICE), Optional.of(GameEvent.BLOCK_PLACE));
    //?}

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3 movementInput, CallbackInfo ci) {
        if (Main.modFullyDisabled()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof ServerPlayer player) || Main.modDisabled()) return;
        if (!player.onGround()) return;
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPERSPEED)) return;

        //? if <= 1.20.5 {
        /*FrostWalkerEnchantment.onEntityMoved(entity, entity.level(), entity.blockPosition(), 5);
        *///?} else {
        ls$frostWalker.apply(player.ls$getServerLevel(), 5, null, player, player.position());
        //?}
    }


    //Located in the ServerPlayer class in < 26.1
    //? if >= 26.1 {
    /*@Inject(method = "attack", at = @At("HEAD"))
    private void onAttackEntity(Entity target, CallbackInfo ci) {
        if (Main.modDisabled()) return;
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer) {
            currentSeason.onUpdatedInventory(serverPlayer);
        }
    }
    *///?}



    @Unique
    private Component ls$cachedDisplayName = null;
    @Unique
    private int ls$cacheAge = -1;
    @Unique
    private boolean ls$preventRecursion = false;
    @ModifyArg(method = "getDisplayName", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;" ))
    private Component ls$injectNickname(Component originalName) {
        try {
            Player player = (Player) (Object) this;
            if (ls$preventRecursion) {
                return originalName;
            }

            if (ls$cacheAge == player.tickCount && ls$cachedDisplayName != null) {
                return ls$cachedDisplayName;
            }

            if (player.level().getServer() != null && !player.level().getServer().isSameThread()) {
                return originalName;
            }

            ls$preventRecursion = true;

            Component nickname = NicknameManager.getNicknameText(OtherUtils.profileId(player.getGameProfile()));

            if (nickname != null) {
                ls$cachedDisplayName = nickname;
                ls$cacheAge = player.tickCount;
                ls$preventRecursion = false;
                return nickname;
            }

            ls$preventRecursion = false;
        } catch (Exception e) {
            ls$preventRecursion = false;
            e.printStackTrace();
        }

        return originalName;
    }

    @Override
    public void ls$resetUsernameCache() {
        this.ls$cachedDisplayName = null;
        this.ls$cacheAge = -1;
    }

    //? if <= 1.21.9 {
    /*@WrapOperation(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean changeKeepInventory(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        Player player = (Player) (Object) this;
        boolean result = original.call(instance, key);
        if (player instanceof ServerPlayer serverPlayer) {
            return currentSeason.modifyKeepInventory(serverPlayer, result);
        }
        return result;
    }
    *///?} else {
    @WrapOperation(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private <T> T changeKeepInventory(GameRules instance, GameRule<T> gameRule, Operation<T> original) {
        Player player = (Player) (Object) this;
        T result = original.call(instance, gameRule);
        if (result instanceof Boolean originalBoolean && originalBoolean != null && player instanceof ServerPlayer serverPlayer) {
            Boolean modified = currentSeason.modifyKeepInventory(serverPlayer, originalBoolean);
            return (T) modified;
        }
        return result;
    }
    //?}
}
